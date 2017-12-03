package io.money.moneyie.model.utilities;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;
import java.util.List;

import io.money.moneyie.R;

import io.money.moneyie.activities.HomeActivity;
import io.money.moneyie.model.Alarm;
import io.money.moneyie.model.receivers.AlarmReceiver;

public class AlarmUtilities {

    //fires notification
    public static void notifyMe (Context context, String message, Integer id){

        String CHANNEL_ID = "my_channel_01";
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.moneyioicon)
                        .setContentTitle(context.getString(R.string.remember))
                        .setContentText(message);

        Intent resultIntent = new Intent(context, HomeActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        stackBuilder.addParentStack(HomeActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(id, mBuilder.build());
    }

    //sets list of alarms
    public static void setAlarms(Context context, List<Alarm> alarms){
        for (Alarm alarm : alarms) {
            setAlarm(context, alarm);
        }
    }

    //sets single alarm
    public static void setAlarm(Context context, Alarm alarm){

        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        int curentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int curentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int curentMin = calendar.get(Calendar.MINUTE);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), alarm.getDate(), alarm.getHour(), alarm.getMinutes());

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        if(curentDay <= day && curentHour <= hour && curentMin < min) {
            switch (currentMonth){
                case 1:
                    if(day == 29 || day == 30 || day == 31) {
                        if(calendar.get(Calendar.YEAR)%4 == 0)
                        {
                            calendar.set(Calendar.DAY_OF_MONTH, 29);
                        } else {
                            calendar.set(Calendar.DAY_OF_MONTH, 28);
                        }
                    }
                    break;
                case 3:
                case 5:
                case 8:
                case 10:
                    if(day == 31) {
                        calendar.set(Calendar.DAY_OF_MONTH, 30);
                    }
                    break;
                default:
                    break;
            }
            AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent myIntent = new Intent(context, AlarmReceiver.class);
            myIntent.putExtra("message", alarm.getMassage());
            myIntent.putExtra("id", alarm.getId());
            PendingIntent pi = PendingIntent.getBroadcast(context, alarm.getId(), myIntent, 0);
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
        }
    }
}
