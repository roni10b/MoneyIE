package io.money.moneyie.model.receivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

import io.money.moneyie.model.Alarm;
import io.money.moneyie.model.database.DatabaseHelperSQLite;
import io.money.moneyie.model.utilities.AlarmUtilities;

public class BootCompletedReceiver extends BroadcastReceiver {

    //sets all alarms when phone is turned on
    @Override
    public void onReceive(Context context, Intent intent) {

        DatabaseHelperSQLite db = DatabaseHelperSQLite.getInstance(context);
        List<Alarm> alarms = db.getUserAlarms();

            //set all alarms
        AlarmUtilities.setAlarms(context, alarms);

    }
}
