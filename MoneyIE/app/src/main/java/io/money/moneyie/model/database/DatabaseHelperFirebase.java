package io.money.moneyie.model.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import io.money.moneyie.model.Alarm;
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

    //spinnerPos -> 2 = my stats, 1 = friends stats, 0 = combined stats
    public List<MoneyFlow> filterData(long start, long end, int spinnerPos){

        SQLiteDatabase db = this.getWritableDatabase();
        String myRawQuery = "SELECT * FROM " + TABLE_IE +
                " WHERE " + T_IE_COL_3 + " BETWEEN " + start + " AND " + end +
                " ORDER BY " + T_IE_COL_3 + " DESC;";
        Cursor c = db.rawQuery(myRawQuery, null);

        String uid = "";
        List<MoneyFlow> filteredArr = new ArrayList<>();

        for (int i = 0; i < c.getCount(); i++){
            c.moveToPosition(i);
            filteredArr.add(new MoneyFlow(uid, c.getString(0), c.getString(1), c.getString(3), c.getDouble(4), c.getLong(2)));
        }

        c.close();

        return Collections.unmodifiableList(filteredArr);
    }


    public List<MoneyFlow> filterData(long start, long end) {

        return filterData(start, end, 0);
    }

    public void addData(String userId, MoneyFlow moneyFlow){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(T_IE_COL_1, moneyFlow.getExpense());
        contentValues.put(T_IE_COL_2, moneyFlow.getType());
        contentValues.put(T_IE_COL_3, moneyFlow.getCalendar());
        contentValues.put(T_IE_COL_4, moneyFlow.getComment());
        contentValues.put(T_IE_COL_5, moneyFlow.getSum());

        //long b =
        db.insert(TABLE_IE, null, contentValues);
        //return (b != -1);

       // this.base.child(usersIE).child(userId).push().setValue(moneyFlow);
    }

//    private void readDatabase(String friendId) {
//        data = new ArrayList<>();
//
//        //base.child(usersIE).child(firebaseAuth.getCurrentUser().getUid()).removeEventListener(userEvent);
//
//        //base.child(usersIE).child(firebaseAuth.getCurrentUser().getUid()).addChildEventListener(userEvent);
//    }

    public List<MoneyFlow> getData() {
        return Collections.unmodifiableList(data);
    }

}
