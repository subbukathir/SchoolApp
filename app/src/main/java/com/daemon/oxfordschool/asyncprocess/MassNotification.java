package com.daemon.oxfordschool.asyncprocess;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.listeners.SendNotificationListener;

import org.json.JSONObject;

/**
 * Created by daemonsoft on 28/3/16.
 */
public class MassNotification
{
    public static String MODULE = "MassNotification";
    public static String TAG ="";
    String Str_Msg = "",Str_Code="";
    SendNotificationListener mCallBack;
    String Str_Url="";
    Fragment mFragment;
    AppCompatActivity mActivity;
    JSONObject object;

    public MassNotification(String Str_Url, Fragment mFragment, JSONObject object)
    {
        this.Str_Url = Str_Url;
        this.mFragment=mFragment;
        this.object=object;
        this.mActivity = (AppCompatActivity)mFragment.getActivity();
        mCallBack = (SendNotificationListener) mFragment;
    }

    /**
     * Getting students list json by making http call
     */
    public void sendNotification()
    {
        TAG = "sendNotification";
        Log.d(MODULE,TAG);
        // appending offset to url
        String url = Str_Url;
        Log.d(MODULE,TAG+ " payload"+ object.toString());
        // Volley's json array request object
        JsonObjectRequest req = new JsonObjectRequest(url,object,responseListener,responseErrorListener);
        MyApplication.getInstance().addToRequestQueue(req);
    }

    Response.Listener responseListener = new Response.Listener()
    {
        @Override
        public void onResponse(Object o)
        {
            try
            {

                JSONObject response = (JSONObject) o;
                Log.d(TAG, response.toString());

                String Str_Code = response.getString("success");
                Log.d(MODULE, TAG + " Str_Code : " + Str_Code);
                if (Str_Code.equals(ApiConstants.SUCCESS_CODE))
                {
                    Str_Msg = response.getString("message");
                    mCallBack.onSendNotificationReceived(Str_Msg);
                }
            }
            catch (Exception ex)
            {
                Str_Msg = mActivity.getResources().getString(R.string.msg_notification_not_sent);
                mCallBack.onSendNotificationReceivedError(Str_Msg);
            }

        }
    };

    Response.ErrorListener responseErrorListener = new Response.ErrorListener()
    {
        @Override
        public void onErrorResponse(VolleyError error)
        {
            VolleyLog.e("Error: ", error.getMessage());
            Log.e(MODULE, TAG + " UnknownResponse");
            if (error instanceof NetworkError)
            {
                Str_Msg = mActivity.getResources().getString(R.string.msg_no_internet);
            }
            else if (error instanceof ServerError)
            {
                Str_Msg = mActivity.getResources().getString(R.string.msg_unexpected_error);
            }
            else if (error instanceof AuthFailureError)
            {
                Str_Msg = mActivity.getResources().getString(R.string.msg_unexpected_error);
            }
            else if (error instanceof ParseError)
            {
                Str_Msg = mActivity.getResources().getString(R.string.msg_unexpected_error);
            }
            else if (error instanceof NoConnectionError)
            {
                Str_Msg = mActivity.getResources().getString(R.string.msg_no_internet);
            }
            else if (error instanceof TimeoutError)
            {
                Str_Msg = mActivity.getResources().getString(R.string.msg_unexpected_error);
            }
            mCallBack.onSendNotificationReceivedError(Str_Msg);
        }
    };

}
