package io.money.moneyie.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.money.moneyie.R;
import io.money.moneyie.model.recyclers.AlarmsRecyclerViewAdapter;
import io.money.moneyie.model.Alarm;
import io.money.moneyie.model.database.DatabaseHelperSQLite;
import io.money.moneyie.model.utilities.AlarmUtilities;
import io.money.moneyie.model.utilities.MonthYearPicker;
import io.money.moneyie.model.utilities.Utilities;


public class Fragment_Reminders extends Fragment implements AlarmsRecyclerViewAdapter.ItemClickListener {

    private View view;
    private RecyclerView recyclerView;
    private DatabaseHelperSQLite db;
    private String user;
    private long mLastClickTime;
    private AlarmsRecyclerViewAdapter adapter;
    private List<Alarm> alarms;
    private EditText dateEdit, timeEdit, messageEdit;
    private Button addAlarmBtn;
    private Calendar calendar;
    private int hour, minute, date;
    private MonthYearPicker monthYearPicker;
    private LinearLayout layout, layoutAdd;
    private ImageView imgEye, imgQuestion;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reminders, container, false);
        initialiseElements();
        startRecycler();
        onTimeEditClickListener();
        onAddAlarmBtnListener();
        setInitialStateDateTimeFields();
        onDateEditClickListener();
        keyboardHideListener();
        return view;
    }

    private void deleteAlarm(int position) {
        List<Alarm> temp = new ArrayList<>(alarms);
        if (position >= 0) {
            temp.remove(position);
        }
        alarms = temp;
    }

    private void initialiseElements() {
        recyclerView = view.findViewById(R.id.recycler_alarms);
        db = DatabaseHelperSQLite.getInstance(view.getContext());
        user = "moneyiefree";
        mLastClickTime = SystemClock.elapsedRealtime();
        dateEdit = view.findViewById(R.id.alarm_date_set_edit);
        timeEdit = view.findViewById(R.id.alarm_time_set_edit);
        messageEdit = view.findViewById(R.id.alarm_message_set_edit);
        addAlarmBtn = view.findViewById(R.id.alarm_add_btn);
        monthYearPicker = new MonthYearPicker(view.getContext());
        layout = view.findViewById(R.id.alarms_layout);
        layoutAdd = view.findViewById(R.id.alarm_add_layout);
        imgEye = view.findViewById(R.id.alarm_eye);
        imgQuestion = view.findViewById(R.id.alarm_question);
    }

    private boolean isRemindersArrayEmpty() {
        if (alarms.size() == 0) {
            return true;
        } else {
            return false;
        }
    }

    private void startRecycler() {
        alarms = db.getUserAlarms(user);
        if (isRemindersArrayEmpty()) {
            imgEye.setVisibility(View.INVISIBLE);
        } else {
            imgEye.setVisibility(View.VISIBLE);
        }
        adapter = new AlarmsRecyclerViewAdapter(view.getContext(), alarms);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        onEyeClickListener();
        onQuestionClickListener();
    }

    public void onQuestionClickListener(){
        imgQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.displayPopupWindow(v, getString(R.string.set_your_personal_reminders_here) +
                        getString(R.string.reminders_text));
            }
        });
    }

    public void onEyeClickListener(){
        imgEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layoutAdd.getVisibility() == View.VISIBLE){
                    layoutAdd.setVisibility(View.GONE);
                    imgEye.setImageResource(R.drawable.eye_invisible);
                } else {
                    layoutAdd.setVisibility(View.VISIBLE);
                    imgEye.setImageResource(R.drawable.eye_visible);
                }
            }
        });
    }

    @Override
    public void onItemClick(View view, int pos) {
        final  int position = pos;
        if (SystemClock.elapsedRealtime() - mLastClickTime < 700){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        AlertDialog.Builder a_builder = new AlertDialog.Builder(view.getContext());
        a_builder.setMessage(getString(R.string.are_you_sure))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.deleteAlarm(user, alarms.get(position).getDate(), alarms.get(position).getHour(),
                                alarms.get(position).getMinutes(), alarms.get(position).getMassage());

                        deleteAlarm(position);
                        recyclerView.removeViewAt(position);
                        adapter.notifyItemRemoved(position);
                        adapter.notifyItemRangeChanged(position, alarms.size());
                        Toast.makeText(getContext(), getString(R.string.DELETED), Toast.LENGTH_SHORT).show();
                        startRecycler();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = a_builder.create();
        alert.setTitle(getString(R.string.DELETE));
        alert.show();
        }


    public void onTimeEditClickListener(){
        timeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hour = calendar.get(Calendar.HOUR_OF_DAY);
                minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(view.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        calendar.set(Calendar.MINUTE, selectedMinute);
                        calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                        hour = calendar.get(Calendar.HOUR_OF_DAY);
                        minute = calendar.get(Calendar.MINUTE);
                        timeEdit.setText(getString(R.string.time) + " " + calendar.get(Calendar.HOUR_OF_DAY) + " : " +
                                ((calendar.get(Calendar.MINUTE) < 10)? "0" : "") + calendar.get(Calendar.MINUTE));
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle(getString(R.string.select_time));
                mTimePicker.show();
            }
        });
    }

    public void onDateEditClickListener(){
        dateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener positiveClick = new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        date = monthYearPicker.getSelectedDay();

                        dateEdit.setText(getString(R.string.Day) + date);
                        monthYearPicker = new MonthYearPicker(view.getContext());
                    }
                };

                DialogInterface.OnClickListener negativeClick = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        monthYearPicker = new MonthYearPicker(view.getContext());
                    }
                };

                monthYearPicker.build(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR), positiveClick, negativeClick, true, false, false);
                monthYearPicker.show();
            }
        });
    }


    public void onAddAlarmBtnListener(){
        addAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEdit.getText().toString().trim();
                boolean isAdded = false;
                if (Utilities.checkString(message)) {
                    isAdded = db.addAlarm(user, date, hour, minute, message);
                    if (isAdded) {
                        Toast.makeText(view.getContext(), getString(R.string.added), Toast.LENGTH_SHORT).show();
                        startRecycler();
                        AlarmUtilities.setAlarm(view.getContext(), new Alarm(date, hour, minute, message));
                        setInitialStateDateTimeFields();
                    } else {
                        Toast.makeText(view.getContext(), getString(R.string.already_exists), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    messageEdit.setError(getString(R.string.message_is_required));
                }
            }
        });
    }


    private void setInitialStateDateTimeFields(){
        calendar = Calendar.getInstance();
        dateEdit.setText(getString(R.string.Day) + calendar.get(Calendar.DAY_OF_MONTH));
        timeEdit.setText(getString(R.string.time) + calendar.get(Calendar.HOUR_OF_DAY) + " : " + calendar.get(Calendar.MINUTE));
        messageEdit.setText("");
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        date = calendar.get(Calendar.DAY_OF_MONTH);
    }

    private void keyboardHideListener(){
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
            }
        });
        messageEdit.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER || event.getKeyCode() == KeyEvent.KEYCODE_BACK)) {
                    hideKeyboard();
                    return true;
                }
                return false;
            }
        });
    }

    private void hideKeyboard(){
        layout.requestFocus();
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
