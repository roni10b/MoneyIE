package io.money.moneyie.fragments.graphicsTabs;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import io.money.moneyie.model.utilities.MonthYearPicker;

public class FragmentTab_YearGraphic extends Fragment {

    private View view;
    private EditText editDate;
    private DatabaseHelperFirebase fdb;
    private List<MoneyFlow> filteredArr;
    private PieChart pieChart;
    private HorizontalBarChart chart;
    private HorizontalBarChart horizontalBarChart;
    private Calendar calendar;
    private MonthYearPicker monthYearPicker;
    private Spinner spinner;
    private long start, end;
    private int spinnerPosition;
    private int year;
    private TextView income, expense, overall, activityText;
    private ImageView questionPie, questionCombined, questionHorizontal, plusMinus;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_fragment_graphics, container, false);
        initialiseElements();
        setVisibility();
        setFilterClickListener();
        filterDataOnStart();
        setSpinnerSettings();
        setQuestionListeners();
        return view;
    }

    //method used for initialisations
    private void initialiseElements() {
        fdb = DatabaseHelperFirebase.getInstance(view.getContext());
        pieChart = view.findViewById(R.id.statistics_income_expense_year_pie);
        chart = view.findViewById(R.id.statistics_income_expense_year_combined);
        horizontalBarChart = view.findViewById(R.id.statistics_income_expense_year_horizontal_bar_chart);
        monthYearPicker = new MonthYearPicker(view.getContext());
        calendar = Calendar.getInstance();
        filteredArr = new ArrayList<>();
        editDate = view.findViewById(R.id.graphics_date_edit);
        editDate.setText("" + calendar.get(Calendar.YEAR));
        spinner = view.findViewById(R.id.statistics_spinner_menu);
        spinnerPosition = 0;
        year = calendar.get(Calendar.YEAR);
        income = view.findViewById(R.id.statistics_income_bar);
        expense = view.findViewById(R.id.statistics_expense_bar);
        overall = view.findViewById(R.id.statistics_overall_bar);
        questionPie = view.findViewById(R.id.statistics_question_pie);
        questionCombined = view.findViewById(R.id.statistics_question_combined);
        questionHorizontal = view.findViewById(R.id.statistics_question_horizontal_bar);
        plusMinus = view.findViewById(R.id.ststistics_plusminus);
        activityText = view.findViewById(R.id.graphics_activity_text);
    }

    private void setVisibility(){
        chart.setVisibility(View.VISIBLE);
        questionCombined.setVisibility(View.VISIBLE);
        pieChart.setVisibility(View.VISIBLE);
        questionPie.setVisibility(View.VISIBLE);
        horizontalBarChart.setVisibility(View.VISIBLE);
        questionHorizontal.setVisibility(View.VISIBLE);
    }

    public void setQuestionListeners(){
        questionPie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.displayPopupWindow(v, getString(R.string.pie_chart) +
                        getString(R.string.pie_year_text));
            }
        });

        questionCombined.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.displayPopupWindow(v, getString(R.string.combined_chart) +
                        getString(R.string.combined_year_text));
            }
        });

        questionHorizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.displayPopupWindow(v, getString(R.string.horizontal_bar_chart) +
                        getString(R.string.horizontal_year_text));
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
                //date picker settings
                calendar.set(year, 0, 1, 0, 0, 0);

                long start = calendar.getTimeInMillis();

                calendar.set(Calendar.YEAR, year + 1);

                long end = calendar.getTimeInMillis();
                filteredArr = fdb.filterData(start, end, position);
                calendar.set(year, 0, 1, 0, 0, 0);
                editDate.setText("" + year);
                incomeExpenseYear();
            }

            public void onNothingSelected(AdapterView<?> parent){
            }
        });
    }

    private void setFilterClickListener(){
    //edit text with calendar settings click listener for changing the date
        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                DialogInterface.OnClickListener positiveClick = new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //date picker settings
                        calendar.set(monthYearPicker.getSelectedYear(), 1, 1, 0,0,0);

                        start = calendar.getTimeInMillis();

                        calendar.set(Calendar.YEAR, monthYearPicker.getSelectedYear() + 1);

                        end = calendar.getTimeInMillis();
                        filteredArr = fdb.filterData(start, end, spinnerPosition);
                        calendar = Calendar.getInstance();
                        editDate.setText("" + monthYearPicker.getSelectedYear());
                        monthYearPicker = new MonthYearPicker(view.getContext());
                        incomeExpenseYear();
                    }
                };

                DialogInterface.OnClickListener negativeClick = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        monthYearPicker = new MonthYearPicker(view.getContext());
                    }
                };

                monthYearPicker.build(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR), positiveClick, negativeClick, false, false, true);
                monthYearPicker.show();
            }
        });
    }

    //this method sets hour range which is passed to the filter array
    private void filterDataOnStart(){
        end = calendar.getTimeInMillis();
        calendar.set(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        start = calendar.getTimeInMillis();
    }

    //start the charts and set them visible/gone
    private void incomeExpenseYear() {
        GraphicUtilities.dataFilerForCurrentTab(income, expense, overall, filteredArr, plusMinus);
        GraphicUtilities.pieChart(pieChart, filteredArr, questionPie);
        GraphicUtilities.combinedBarChart(chart, filteredArr, questionHorizontal);
        GraphicUtilities.horizontalBarChart(horizontalBarChart, filteredArr, questionCombined, view.getContext());

        if (filteredArr.isEmpty()) {
            activityText.setVisibility(View.VISIBLE);
        } else {
            activityText.setVisibility(View.GONE);
        }
    }
}
