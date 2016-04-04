package com.daemon.oxfordschool.gcm;

/**
 * Created by daemonsoft on 23/2/16.
 */
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.daemon.oxfordschool.Config;
import com.daemon.oxfordschool.activity.MainActivity;
import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONObject;


public class MyGcmPushReceiver extends GcmListenerService
{

    private String MODULE = GcmIntentService.class.getName();
    String TAG = "";

    private NotificationUtils notificationUtils;

    /**
     * Called when message is received.
     *
     * @param from   SenderID of the sender.
     * @param bundle Data bundle containing message data as key/value pairs.
     *               For Set of keys use data.keySet().
     */

    @Override
    public void onMessageReceived(String from, Bundle bundle)
    {
        TAG = "onMessageReceived";
        Log.d(MODULE,TAG);
        Log.d(MODULE,TAG + " bundle:"+bundle);
        try
        {
            //String responseData = bundle.getString("data");
            //JSONObject myJson = new JSONObject(responseData);

            String title = bundle.getString("title");
            String message = bundle.getString("message");
            //String title = bundle.getString("gcm.notification.title");
            //String message = bundle.getString("gcm.notification.message");

            Log.e(TAG, "From: " + from);
            Log.e(TAG, "Title: " + title);
            Log.e(TAG, "message: " + message);

            if (!NotificationUtils.isAppIsInBackground(getApplicationContext()))
            {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils();
                notificationUtils.playNotificationSound();
            }
            else
            {
                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                resultIntent.putExtra("message", message);
                showNotificationMessage(getApplicationContext(), title, message, resultIntent);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void showNotificationMessage(Context context, String title, String message,Intent intent)
    {

        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.clearNotifications();
        notificationUtils.showNotificationMessage(title, message,"",intent);
    }

}
