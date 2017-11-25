package io.money.moneyie.model.utilities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import io.money.moneyie.R;
import io.money.moneyie.activities.HomeActivity;


public final class Utilities {
    private static boolean isFirebasePersistence = false;
    private static boolean hasFriend = false;
    private static int idGeneratorNotifications = 0;

    //id generator for notifications
    public static int gnerateID(){
        idGeneratorNotifications++;
        return idGeneratorNotifications;
    }

    public static boolean isHasFriend() {
        return hasFriend;
    }

    public static void setHasFriend(boolean hasFriend) {
        Utilities.hasFriend = hasFriend;
    }

    public static boolean isFirebasePersistence() {
        return isFirebasePersistence;
    }

    public static void setIsFirebasePersistence(boolean isFirebasePersist) {
        isFirebasePersistence = isFirebasePersist;
    }

    //checks for forbidden symbols
    public static boolean checkString(String str){
        if (TextUtils.isEmpty(str) ||  str.matches("[&.\\;'\"]")){
            return false;
        }
        return true;
    }


    public static boolean isMail(String str){
        return !TextUtils.isEmpty(str) && android.util.Patterns.EMAIL_ADDRESS.matcher(str).matches();
    }

    //check is string is number
    public static boolean isNumber(String str){
        if (!TextUtils.isEmpty(str) ||  str.matches("[0-9.]+")){
            return true;
        }
        return false;
    }

    //fires notification when friend has income or expense
    public static void notifyFriend(Context context, String type, String sum){
        String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());

        if(!preferences.getString(uID  + "notifications", "EMPTY").equals("OFF")){
            String CHANNEL_ID = "my_channel_01";
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context, CHANNEL_ID)
                            .setSmallIcon(R.mipmap.moneyioicon)
                            .setContentTitle("Your friend spend")
                            .setContentText(sum + " for " + type);
            // Creates an explicit intent for an Activity in your app
            Intent resultIntent = new Intent(context, HomeActivity.class);

            // The stack builder object will contain an artificial back stack for the
            // started Activity.
            // This ensures that navigating backward from the Activity leads out of
            // your app to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(HomeActivity.class);

            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            // mNotificationId is a unique integer your app uses to identify the
            // notification. For example, to cancel the notification, you can pass its ID
            // number to NotificationManager.cancel().
            mNotificationManager.notify(gnerateID(), mBuilder.build());
        }
    }

    //filter "." from mail address, "." is forbidden in firebase.
    public static String filterMail(String inputStr){
        if(inputStr == null){
            return "";
        }

        StringBuilder filtered = new StringBuilder();
        for (int i = 0; i < inputStr.length(); i++) {
            if (inputStr.charAt(i) == '.') {
                filtered.append("__");
            } else {
                filtered.append(inputStr.charAt(i));
            }
        }
        return filtered.toString();
    }

    // Display popup attached to the button as a position anchor
   public static void displayPopupWindow(View anchorView, String text) {
        PopupWindow popup = new PopupWindow(anchorView.getContext());
        View layout = LayoutInflater.from(anchorView.getContext()).inflate(R.layout.popup_content, null);
        ((TextView)(layout.findViewById(R.id.popup_text))).setText(text);
        popup.setContentView(layout);
        // Set content width and height
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        // Closes the popup window when touch outside of it - when looses focus
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        // Show anchored to button
        popup.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        popup.showAsDropDown(anchorView);
    }
}
