package com.daemon.oxfordschool.gcm;

/**
 * Created by daemonsoft on 23/2/16.
 */
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.iid.InstanceIDListenerService;

public class MyInstanceIDListenerService extends InstanceIDListenerService
{

    private String MODULE = GcmIntentService.class.getName();
    String TAG = "";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    @Override
    public void onTokenRefresh()
    {
        TAG = "onMessageReceived";
        Log.d(MODULE,TAG);
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        Intent intent = new Intent(this, GcmIntentService.class);
        startService(intent);
    }
}
