package com.daemon.oxfordschool.gcm;

/**
 * Created by daemonsoft on 23/2/16.
 */
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.daemon.oxfordschool.Config;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.classes.User;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class GcmIntentService extends IntentService
{

    private String MODULE = GcmIntentService.class.getName();
    String TAG = "";
    static String TTAG = GcmIntentService.class.getName();

    public GcmIntentService()
    {
        super(TTAG);
    }

    public static final String KEY = "key";
    public static final String TOPIC = "topic";
    public static final String SUBSCRIBE = "subscribe";
    public static final String UNSUBSCRIBE = "unsubscribe";


    @Override
    protected void onHandleIntent(Intent intent)
    {
        TAG = "onHandleIntent";
        Log.d(MODULE,TAG);
        String key = intent.getStringExtra(KEY);
        Log.d(MODULE,TTAG + "key : " + KEY);
        switch (key)
        {
            case SUBSCRIBE:
                 // subscribe to a topic
                 String topic = intent.getStringExtra(TOPIC);
                 subscribeToTopic(topic);
                 break;
            case UNSUBSCRIBE:
                 String topic1 = intent.getStringExtra(TOPIC);
                 unsubscribeFromTopic(topic1);
                 break;
            default:
                 // if key is not specified, register with GCM
                 registerGCM();
        }

    }

    /**
     * Registering with GCM and obtaining the gcm registration id
     */
    private void registerGCM()
    {
        TAG = "registerGCM";
        Log.d(MODULE,TAG);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String token = null;

        try
        {
            InstanceID instanceID = InstanceID.getInstance(this);
            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.d(MODULE,TAG + " GCM Registration Token: " + token);
            // sending the registration id to our server
            sharedPreferences.edit().putBoolean(Config.SENT_TOKEN_TO_SERVER, true).apply();
            subscribeToTopic("Mass");
        }
        catch (Exception e)
        {
            Log.e(MODULE,TAG + "Failed to complete token refresh", e);
            sharedPreferences.edit().putBoolean(Config.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", token);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Subscribe to a topic
     */
    public void subscribeToTopic(String topic)
    {
        TAG = "subscribeToTopic";
        Log.d(MODULE,TAG);

        GcmPubSub pubSub = GcmPubSub.getInstance(getApplicationContext());
        InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
        String token = null;
        try
        {
            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            if (token != null)
            {
                pubSub.subscribe(token, "/topics/" + topic, null);
                Log.e(MODULE,TAG + " Subscribed to topic: " + topic);
            }
            else
            {
                Log.e(MODULE,TAG + " error: gcm registration id is null");
            }
        }
        catch (IOException e)
        {
            Log.e(TAG, "Topic subscribe error. Topic: " + topic + ", error: " + e.getMessage());
            Toast.makeText(getApplicationContext(), "Topic subscribe error. Topic: " + topic + ", error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void unsubscribeFromTopic(String topic)
    {
        TAG = "unsubscribeFromTopic";
        Log.d(MODULE,TAG);

        GcmPubSub pubSub = GcmPubSub.getInstance(getApplicationContext());
        InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
        String token = null;
        try
        {
            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            if (token != null)
            {
                pubSub.unsubscribe(token, "");
                Log.e(MODULE,TAG +  " Unsubscribed from topic: " + topic);
            }
            else
            {
                Log.e(MODULE,TAG +  " error: gcm registration id is null");
            }
        }
        catch (IOException e)
        {
            Log.e(MODULE,TAG + "Topic unsubscribe error. Topic: " + topic + ", error: " + e.getMessage());
            Toast.makeText(getApplicationContext(), "Topic subscribe error. Topic: " + topic + ", error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

 }
