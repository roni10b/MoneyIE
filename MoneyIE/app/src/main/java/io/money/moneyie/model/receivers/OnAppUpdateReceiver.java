package io.money.moneyie.model.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import io.money.moneyie.model.Alarm;
import io.money.moneyie.model.database.DatabaseHelperSQLite;
import io.money.moneyie.model.utilities.AlarmUtilities;

public class OnAppUpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseHelperSQLite db = DatabaseHelperSQLite.getInstance(context);
        List<Alarm> alarms = db.getUserAlarms();
        AlarmUtilities.setAlarms(context, alarms);
        Log.e("ivan", "test");
    }
}
