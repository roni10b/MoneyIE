package io.money.moneyie.fragments.statisticsTabs;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.money.moneyie.R;
import io.money.moneyie.model.MoneyFlow;
import io.money.moneyie.model.database.DatabaseHelperFirebase;
import io.money.moneyie.model.recyclers.HistoryRecyclerViewAdapter;
import io.money.moneyie.model.utilities.GraphicUtilities;

public class FragmentTab_Period extends Fragment {

    private View view;
    private EditText calendarFrom, calendarTo;
    private TextView from, to, activityText, plusMin;
    private ImageView plusMinImg;
    private DatabaseHelperFirebase fdb;
    private RecyclerView recyclerView;
    private Calendar calendar;
    private List<MoneyFlow> filteredArr;
    private Spinner spinner;
    private long start, end;
    private int spinnerPosition;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_fragment_datahistory, container, false);
        initialise();
        setStartEnd();
        setFilter();
        setSpinnerSettings();
        return view;
    }

    private void initialise() {
        calendar = Calendar.getInstance();
        fdb = DatabaseHelperFirebase.getInstance(view.getContext());
        recyclerView = view.findViewById(R.id.history_recycler_view);
        filteredArr = new ArrayList<>();
        calendarFrom = view.findViewById(R.id.history_date_edit);
        calendarFrom.setText(calendar.get(Calendar.YEAR) + " / " +
                (calendar.get(Calendar.MONTH)+1) + " / " +  calendar.get(Calendar.DAY_OF_MONTH));
        spinner = view.findViewById(R.id.history_spinner);
        spinnerPosition = 0;
        calendarTo = view.findViewById(R.id.history_date_edit1);
        calendarTo.setText(calendar.get(Calendar.YEAR) + " / " +
                (calendar.get(Calendar.MONTH)+1) + " / " +  calendar.get(Calendar.DAY_OF_MONTH));
        from = view.findViewById(R.id.history_text_from);
        to = view.findViewById(R.id.history_text_to);
        activityText = view.findViewById(R.id.history_activity_text);
        plusMin = view.findViewById(R.id.history_overall_bar);
        plusMinImg = view.findViewById(R.id.history_plusminus);
        setVisibility();
    }

    private void setStartEnd(){
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0,0,0);
        start = calendar.getTimeInMillis();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        end = calendar.getTimeInMillis();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0,0,0);
    }

    private void setVisibility() {
        calendarTo.setVisibility(View.VISIBLE);
        to.setVisibility(View.VISIBLE);
        from.setVisibility(View.VISIBLE);
    }
    private void setSpinnerSettings() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.history_spinner, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerPosition = position;
                filteredArr = fdb.filterData(start, end, spinnerPosition);
                startRecycler(filteredArr);
            }

            public void onNothingSelected(AdapterView<?> parent){

            }
        });
    }

    private void setFilter(){

        final DatePickerDialog.OnDateSetListener fromDate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(year, monthOfYear, dayOfMonth, 0,0,0);
                start = calendar.getTimeInMillis();
                filteredArr = fdb.filterData(start, end, spinnerPosition);
                startRecycler(filteredArr);
                calendarFrom.setText(dayOfMonth + " / " + (monthOfYear + 1) + " / " + year);
                calendar = Calendar.getInstance();
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
                startRecycler(filteredArr);
                calendarTo.setText(dayOfMonth + " / " + (monthOfYear + 1) + " / " + year);
                calendar = Calendar.getInstance();
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

    private void startRecycler(List<MoneyFlow> data) {
        if (data.isEmpty()) {
            recyclerView.setVisibility(View.INVISIBLE);
            activityText.setVisibility(View.VISIBLE);
            return;
        }
        GraphicUtilities.plusMinusHistory(plusMin, data, plusMinImg);
        recyclerView.setVisibility(View.VISIBLE);
        activityText.setVisibility(View.GONE);
        HistoryRecyclerViewAdapter adapter = new HistoryRecyclerViewAdapter(view.getContext(), data);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);
    }
}
