package io.money.moneyie.fragments.statisticsTabs;

import android.content.DialogInterface;
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
import io.money.moneyie.model.utilities.MonthYearPicker;

public class FragmentTab_Month extends Fragment {

    private View view;
    private DatabaseHelperFirebase fdb;
    private RecyclerView recyclerView;
    private Calendar calendar;
    private EditText editDate;
    private MonthYearPicker monthYearPicker;
    private List<MoneyFlow> filteredArr;
    private Spinner spinner;
    private long start, end;
    private int spinnerPosition;
    private int year, month;
    private TextView activityText, plusMin;
    private ImageView plusMinImg;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.tab_fragment_datahistory, container, false);
        initialiseElements();
        setFilter();
        filterDataOnStart();
        setSpinnerSettings();
        return view;
    }

    private void initialiseElements() {
        fdb = DatabaseHelperFirebase.getInstance(view.getContext());
        recyclerView = view.findViewById(R.id.history_recycler_view);
        calendar = Calendar.getInstance();
        monthYearPicker = new MonthYearPicker(view.getContext());
        filteredArr = new ArrayList<>();
        editDate = view.findViewById(R.id.history_date_edit);
        editDate.setText(calendar.get(Calendar.YEAR) + " / " + (calendar.get(Calendar.MONTH)+1));
        spinner = view.findViewById(R.id.history_spinner);
        spinnerPosition = 0;
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        activityText = view.findViewById(R.id.history_activity_text);
        plusMin = view.findViewById(R.id.history_overall_bar);
        plusMinImg = view.findViewById(R.id.history_plusminus);
    }

    private void setSpinnerSettings() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.history_spinner, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                calendar.set(year, month, 1, 0,0,0);
                editDate.setText(year + " / " + (month+1));
                start = calendar.getTimeInMillis();

                if(month == 12){
                    calendar.set(Calendar.YEAR, year + 1);
                    calendar.set(Calendar.MONTH, 1);
                } else {
                    calendar.set(Calendar.MONTH, month + 1);
                }
                end = calendar.getTimeInMillis();

                filteredArr = fdb.filterData(start, end, position);
                spinnerPosition = position;

                startRecycler(filteredArr);
            }

            public void onNothingSelected(AdapterView<?> parent){
            }
        });
    }

    private void setFilter(){
        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                DialogInterface.OnClickListener positiveClick = new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        year = monthYearPicker.getSelectedYear();
                        month = monthYearPicker.getSelectedMonth();
                        editDate.setText(year + " / " + (month+1));
                        calendar.set(year, month, 1, 0, 0, 0);

                        start = calendar.getTimeInMillis();

                        if(month == 12){
                            calendar.set(Calendar.YEAR, year + 1);
                            calendar.set(Calendar.MONTH, 1);
                        } else {
                            calendar.set(Calendar.MONTH, month + 1);
                        }

                        end = calendar.getTimeInMillis();
                        calendar.set(year, month, 1, 0, 0, 0);

                        filteredArr = fdb.filterData(start, end, spinnerPosition);
                        startRecycler(filteredArr);
                        calendar = Calendar.getInstance();
                        monthYearPicker = new MonthYearPicker(view.getContext());
                    }
                };

                DialogInterface.OnClickListener negativeClick = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        monthYearPicker = new MonthYearPicker(view.getContext());
                    }
                };

                monthYearPicker.build(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH)-1, calendar.get(Calendar.YEAR), positiveClick, negativeClick,false, true, true);
                monthYearPicker.show();
            }
        });
    }

    private void filterDataOnStart(){
        end = calendar.getTimeInMillis();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        start = calendar.getTimeInMillis();
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
