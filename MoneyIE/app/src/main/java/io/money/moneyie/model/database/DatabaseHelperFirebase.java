package io.money.moneyie.model.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.money.moneyie.model.AddFriend;
import io.money.moneyie.model.MoneyFlow;
import io.money.moneyie.model.utilities.Utilities;

public class DatabaseHelperFirebase extends SQLiteOpenHelper {


    private static List<MoneyFlow> data = new ArrayList<>();
    private static final String usersIE = "usersie";
    private Context context;
    private static DatabaseHelperFirebase instance;

//
//    private String expense;
//    private String type;
//    private long calendar;
//    private String comment;
//    private double sum;
//    private String uid;

    private static final String DATABASE_NAME = "MoneyIЕ_free.db";
    private static int DATABASE_VERSION = 1;

    private static final String TABLE_IE = "user_setings";

    private static final String T_IE_COL_1 = "expense";
    private static final String T_IE_COL_2 = "type";
    private static final String T_IE_COL_3 = "calendar";
    private static final String T_IE_COL_4 = "comment";
    private static final String T_IE_COL_5 = "sum";

    private static final String IE_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_IE +
            " (" + T_IE_COL_1 + " TEXT, " +
            T_IE_COL_2 + " TEXT, " +
            T_IE_COL_3 + " INTEGER, " +
            T_IE_COL_4 + " TEXT," +
            T_IE_COL_5 + " INTEGER);";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(IE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    private DatabaseHelperFirebase(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public static synchronized DatabaseHelperFirebase getInstance(Context con) {
        if (instance == null) {
            instance = new DatabaseHelperFirebase(con.getApplicationContext());
        }
        return instance;
    }



    //
    //
    //
    //spinnerPos -> 2 = my stats, 1 = friends stats, 0 = combined stats
    public static List<MoneyFlow> filterData(long start, long end, int spinnerPos){

        String uid = "";
        List<MoneyFlow> filteredArr = new ArrayList<>();

        for (MoneyFlow f: data) {
            switch (spinnerPos) {
                case 2:
                        if(start <= f.getCalendar() && f.getCalendar() <= end && f.getUid().equals(uid)){
                        filteredArr.add(f);
                    }
                    break;
                case 1:
                    if(start <= f.getCalendar() && f.getCalendar() <= end && !f.getUid().equals(uid)){
                    filteredArr.add(f);
                    }
                    break;
                case 0:
                    if(start <= f.getCalendar() && f.getCalendar() <= end){
                    filteredArr.add(f);
                    }
                    break;
            }
        }

        Collections.sort(filteredArr, new Comparator<MoneyFlow>() {
            @Override
            public int compare(MoneyFlow o1, MoneyFlow o2) {
                return (o1.getCalendar() > o2.getCalendar())? -1 : 1;
            }
        });
        return Collections.unmodifiableList(filteredArr);
    }


    public static List<MoneyFlow> filterData(long start, long end) {

        return filterData(start, end, 0);
    }


    public void addData(String userId, MoneyFlow moneyFlow){
       // this.base.child(usersIE).child(userId).push().setValue(moneyFlow);
    }

    private ChildEventListener userEvent = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            MoneyFlow t = dataSnapshot.getValue(MoneyFlow.class);
            data.add(t);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };


    private void readDatabase(String friendId) {
        data = new ArrayList<>();

        //base.child(usersIE).child(firebaseAuth.getCurrentUser().getUid()).removeEventListener(userEvent);

        //base.child(usersIE).child(firebaseAuth.getCurrentUser().getUid()).addChildEventListener(userEvent);
    }

    public List<MoneyFlow> getData() {
        return Collections.unmodifiableList(data);
    }

}
