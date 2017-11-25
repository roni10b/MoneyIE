package io.money.moneyie.fragments.graphicsTabs;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.money.moneyie.R;
import io.money.moneyie.model.MoneyFlow;
import io.money.moneyie.model.database.DatabaseHelperFirebase;
import io.money.moneyie.model.utilities.Utilities;
import io.money.moneyie.model.utilities.GraphicUtilities;

public class FragmentTab_PeriodGraphic extends Fragment {

    private View view;
    private EditText calendarTo, calendarFrom;
    private TextView from, to;
    private DatabaseHelperFirebase fdb;
    private List<MoneyFlow> filteredArr;
    private PieChart pieChart;
    private BarChart chart;
    private HorizontalBarChart horizontalBarChart;
    private Calendar calendar;
    private Spinner spinner;
    private long start, end;
    private int spinnerPosition;
    private TextView income, expense, overall, activityText;
    private ImageView questionPie, questionCombined, questionHorizontal, plusMinus;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_fragment_graphics, container, false);
        initialise();
        setStartEnd();
        setFilterClickListener();
        setSpinnerSettings();
        setQuestionListeners();
        return view;
    }

    private void initialise() {
        calendar = Calendar.getInstance();
        calendarTo = view.findViewById(R.id.graphics_date_edit1);
        calendarTo.setText(calendar.get(Calendar.YEAR) + " / " +
                (calendar.get(Calendar.MONTH)+1) + " / " +  calendar.get(Calendar.DAY_OF_MONTH));
        from = view.findViewById(R.id.graphics_text_from);
        to = view.findViewById(R.id.graphics_text_to);
        fdb = DatabaseHelperFirebase.getInstance(view.getContext());
        pieChart = view.findViewById(R.id.statistics_income_expense_year_pie);
        chart = view.findViewById(R.id.statistics_income_expense_year_combined);
        horizontalBarChart = view.findViewById(R.id.statistics_income_expense_year_horizontal_bar_chart);
        filteredArr = new ArrayList<>();
        calendarFrom = view.findViewById(R.id.graphics_date_edit);
        calendarFrom.setText(calendar.get(Calendar.YEAR) + " / " +
                (calendar.get(Calendar.MONTH)+1) + " / " +  calendar.get(Calendar.DAY_OF_MONTH));
        spinner = view.findViewById(R.id.statistics_spinner_menu);
        spinnerPosition = 0;
        income = view.findViewById(R.id.statistics_income_bar);
        expense = view.findViewById(R.id.statistics_expense_bar);
        overall = view.findViewById(R.id.statistics_overall_bar);
        questionPie = view.findViewById(R.id.statistics_question_pie);
        questionCombined = view.findViewById(R.id.statistics_question_combined);
        questionHorizontal = view.findViewById(R.id.statistics_question_horizontal_bar);
        plusMinus = view.findViewById(R.id.ststistics_plusminus);
        activityText = view.findViewById(R.id.graphics_activity_text);
        setVisibility();
    }

    private void setVisibility() {
        calendarTo.setVisibility(View.VISIBLE);
        to.setVisibility(View.VISIBLE);
        from.setVisibility(View.VISIBLE);
        chart.setVisibility(View.GONE);
        questionCombined.setVisibility(View.GONE);
        pieChart.setVisibility(View.GONE);
        questionPie.setVisibility(View.GONE);
        horizontalBarChart.setVisibility(View.VISIBLE);
        questionHorizontal.setVisibility(View.VISIBLE);
    }

    private void setStartEnd(){
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0,0,0);
        start = calendar.getTimeInMillis();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        end = calendar.getTimeInMillis();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0,0,0);
    }

    public void setQuestionListeners(){
        questionHorizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.displayPopupWindow(v, getString(R.string.horizontal_bar_chart) + getString(R.string.hbc_day_text));
            }
        });
    }

    private void setSpinnerSettings() {
        //creating spinner adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.history_spinner, R.layout.support_simple_spinner_dropdown_item);
        //set the theme of the spinner
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        //setting the spinner's adapter
        spinner.setAdapter(adapter);

        //spinner item selected listener functionality
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerPosition = position;
                filteredArr = fdb.filterData(start, end, spinnerPosition);
                incomeExpensePeriod();
            }

            public void onNothingSelected(AdapterView<?> parent){
            }
        });
    }

    private void setFilterClickListener(){
        //date picker settings
        final DatePickerDialog.OnDateSetListener fromDate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(year, monthOfYear, dayOfMonth, 0,0,0);
                start = calendar.getTimeInMillis();
                filteredArr = fdb.filterData(start, end, spinnerPosition);
                calendarFrom.setText(year + " / " + (monthOfYear + 1) + " / " + dayOfMonth);
                calendar = Calendar.getInstance();
                incomeExpensePeriod();
            }
        };

        calendarFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                calendar.setTimeInMillis(start);
                new DatePickerDialog(view.getContext(), fromDate, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final DatePickerDialog.OnDateSetListener toDate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(year, monthOfYear, dayOfMonth, 23, 59, 59);
                end = calendar.getTimeInMillis();
                filteredArr = fdb.filterData(start, end, spinnerPosition);
                calendarFrom.setText(year + " / " + (monthOfYear + 1) + " / " + dayOfMonth);
                calendar = Calendar.getInstance();
                incomeExpensePeriod();
            }
        };

        calendarTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                calendar.setTimeInMillis(end);
                new DatePickerDialog(view.getContext(), toDate, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    //start the charts and set them visible/gone
    private void incomeExpensePeriod() {
        GraphicUtilities.dataFilerForCurrentTab(income, expense, overall, filteredArr, plusMinus);
        GraphicUtilities.horizontalBarChart(horizontalBarChart, filteredArr, questionHorizontal, view.getContext());
        if (filteredArr.isEmpty()) {
            activityText.setVisibility(View.VISIBLE);
        } else {
            activityText.setVisibility(View.GONE);
        }
    }
}
