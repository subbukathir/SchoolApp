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
import com.daemon.oxfordschool.listeners.EventsListListener;
import com.daemon.oxfordschool.listeners.HomeWorkListListener;

import org.json.JSONObject;

/**
 * Created by daemonsoft on 4/12/15.
 */
public class GetHomeWorkList
{
    public static String MODULE = "GetHomeWorkList";
    public static String TAG ="";
    String Str_Msg = "",Str_Code="";
    HomeWorkListListener mCallBack;
    String Str_Url="";
    JSONObject Payload;
    Fragment mFragment;
    AppCompatActivity mActivity;
    SharedPreferences mPreferences;
    SharedPreferences.Editor editor;

    public GetHomeWorkList(String Str_Url,JSONObject Payload, Fragment mFragment)
    {
        this.Str_Url = Str_Url;
        this.Payload=Payload;
        this.mFragment=mFragment;
        this.mActivity = (AppCompatActivity)mFragment.getActivity();
        mCallBack = (HomeWorkListListener) mFragment;
        mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
        editor = mPreferences.edit();
    }

    /**
     * Getting students list json by making http call
     */
    public void getHomeWorks()
    {
        TAG = "getHomeWorks";
        Log.d(MODULE,TAG);
        // appending offset to url
        String url = Str_Url;
        // Volley's json array request object
        JsonObjectRequest req = new JsonObjectRequest(url,Payload,responseListener,responseErrorListener);
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
                if (response.toString().length()==0)
                {
                    Log.d(TAG, "empty");
                    editor = mPreferences.edit();
                    editor.putString(AppUtils.SHARED_HOMEWORK_LIST, "");
                    editor.commit();
                    Str_Msg = mActivity.getResources().getString(R.string.msg_unexpected_error);
                    mCallBack.onHomeWorkListReceivedError(Str_Msg);
                }
                else
                {
                    String Str_Code = response.getString("success");
                    Log.d(MODULE, TAG + " Str_Code : " + Str_Code);

                    if (Str_Code.equals(ApiConstants.SUCCESS_CODE))
                    {
                        editor = mPreferences.edit();
                        editor.putString(AppUtils.SHARED_HOMEWORK_LIST, response.toString());
                        editor.commit();
                        mCallBack.onHomeWorkListReceived();
                    }
                    else
                    {
                        Str_Msg = response.getString("message");
                        mCallBack.onHomeWorkListReceivedError(Str_Msg);
                    }
                }
            }
            catch (Exception ex)
            {
                Str_Msg = mActivity.getResources().getString(R.string.msg_unexpected_error);
                mCallBack.onHomeWorkListReceivedError(Str_Msg);
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
            mCallBack.onHomeWorkListReceivedError(Str_Msg);
        }
    };

}
