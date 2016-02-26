package com.daemon.oxfordschool.asyncprocess;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.listeners.RegistrationListener;

import org.json.JSONObject;


/**
 * Created by daemonsoft on 4/12/15.
 */
public class RegisterDevice
{
    public static String MODULE = "RegisterDevice";
    public static String TAG ="";
    String Str_Msg = "",Str_Code="";
    RegistrationListener mCallBack;
    String Str_Url="";
    Fragment mFragment;
    AppCompatActivity mActivity;
    SharedPreferences mPreferences;
    SharedPreferences.Editor editor;
    JSONObject object;

    public RegisterDevice(String Str_Url, AppCompatActivity mActivity, JSONObject object)
    {
        this.Str_Url = Str_Url;
        this.mActivity=mActivity;
        this.object=object;
        mCallBack = (RegistrationListener) mActivity;
        mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
        editor = mPreferences.edit();
    }

    /**
     * Getting students list json by making http call
     */
    public void registerDevice()
    {
        TAG = "registerDevice";
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

                    String Str_Code = response.getString("Success");
                    Log.d(MODULE, TAG + " Str_Code : " + Str_Code);

                    if (Str_Code.equals(ApiConstants.SUCCESS_CODE))
                    {
                        Str_Msg = response.getString("Message");
                        mCallBack.onRegistrationReceived(Str_Msg);
                    }
                    else
                    {
                        Str_Msg = response.getString("Message");
                        mCallBack.onRegistrationReceivedError(Str_Msg);
                    }

            }
            catch (Exception ex)
            {
                Str_Msg = mActivity.getResources().getString(R.string.msg_unexpected_error);
                mCallBack.onRegistrationReceivedError(Str_Msg);
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
            mCallBack.onRegistrationReceivedError(Str_Msg);
        }
    };

}
