package io.money.moneyie.model.receivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import io.money.moneyie.model.utilities.AlarmUtilities;

public class AlarmReceiver extends BroadcastReceiver {

    //receiver for notifications
    @Override
    public void onReceive(Context context, Intent intent) {
        //get notification text from intent
        String message = intent.getExtras().getString("message");
        Integer id = intent.getIntExtra("id", 9999999);
        //fires notification
        AlarmUtilities.notifyMe(context, message, id);
    }
}
