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
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.yalantis.phoenix.PullToRefreshView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.money.moneyie.R;
import io.money.moneyie.model.MoneyFlow;
import io.money.moneyie.model.database.DatabaseHelperFirebase;
import io.money.moneyie.model.recyclers.HistoryRecyclerViewAdapter;

public class FragmentTab_Day extends Fragment {

    private View view;
    private DatabaseHelperFirebase fdb;
    private RecyclerView recyclerView;
    private Calendar calendar;
    private EditText editDate;
    private List<MoneyFlow> filteredArr;
    private Spinner spinner;
    private long start, end;
    private int spinnerPosition;
    private TextView activityText;
    private PullToRefreshView refresh;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_fragment_datahistory, container, false);
        initialiseElements();
        setFilter();
        filterDataOnStart();
        setSpinnerSettings();
        refresh();
        return view;
    }

    private void initialiseElements() {
        calendar = Calendar.getInstance();
        fdb = DatabaseHelperFirebase.getInstance(view.getContext());
        recyclerView = view.findViewById(R.id.history_recycler_view);
        filteredArr = new ArrayList<>();
        editDate = view.findViewById(R.id.history_date_edit);
        editDate.setText(calendar.get(Calendar.YEAR) + " / " +
                        (calendar.get(Calendar.MONTH)+1) + " / " +  calendar.get(Calendar.DAY_OF_MONTH));
        spinner = view.findViewById(R.id.history_spinner);
        spinnerPosition = 0;
        activityText = view.findViewById(R.id.history_activity_text);
        refresh = view.findViewById(R.id.pull_to_refresh);
    }

    private void refresh() {
        refresh.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //code to check during refresh
                        refresh.setRefreshing(false);
                    }
                }, 250);
            }
        });
    }

    private void setSpinnerSettings() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.history_spinner, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calendar = Calendar.getInstance();
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0,0,0);
                start = calendar.getTimeInMillis();
                end = start + 1000*60*60*24;
                calendar = Calendar.getInstance();
                filteredArr = fdb.filterData(start, end, position);
                spinnerPosition = position;
                startRecycler(filteredArr);
            }

            public void onNothingSelected(AdapterView<?> parent){

            }
        });
    }

        private void setFilter(){

            final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    calendar.set(year, monthOfYear, dayOfMonth, 0,0,0);
                    start = calendar.getTimeInMillis();
                    end = start + 1000*60*60*24;
                    filteredArr = fdb.filterData(start, end, spinnerPosition);
                    startRecycler(filteredArr);
                    editDate.setText(dayOfMonth + " / " + (monthOfYear + 1) + " / " + year);
                    calendar = Calendar.getInstance();
                }
            };

            editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                        new DatePickerDialog(view.getContext(), date, calendar
                                .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
        });
    }

    private void filterDataOnStart(){
        end = calendar.getTimeInMillis();
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
        recyclerView.setVisibility(View.VISIBLE);
        activityText.setVisibility(View.GONE);
        HistoryRecyclerViewAdapter adapter = new HistoryRecyclerViewAdapter(view.getContext(), data);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);
    }
}
