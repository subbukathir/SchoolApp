package com.daemon.oxfordschool.asyncprocess;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.listeners.ViewProfileListener;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by daemonsoft on 28/1/16.
 */
public class AfterImageUpdate_Process
{
    public static String MODULE = "AfterImageUpdate_Process";
    public static String TAG ="";

    String Str_Msg = "",Str_Code="";
    ViewProfileListener mCallBack;
    String Str_Url="";
    Fragment mFragment;
    AppCompatActivity mActivity;
    SharedPreferences mPreferences;
    SharedPreferences.Editor editor;
    JSONObject mObject;

    public AfterImageUpdate_Process(String Str_Url, Fragment mFragment, JSONObject object)
    {
        this.Str_Url = Str_Url;
        this.mFragment = mFragment;
        this.mActivity = (AppCompatActivity)mFragment.getActivity();
        this.mObject= object;
        mCallBack = (ViewProfileListener) mFragment;
        mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
        editor = mPreferences.edit();
    }

    public void UpdateProfile()
    {

        TAG = " UpdateProfile";
        Log.d(MODULE,TAG + mObject.toString());

        try
        {
            RequestQueue rq = Volley.newRequestQueue(mActivity);

            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,Str_Url,mObject,new Response.Listener<JSONObject>()
            {
                @Override
                public void onResponse(JSONObject response)
                {
                    TAG="onResponse";
                    try
                    {
                        Log.d(MODULE, TAG + response.toString());

                        if (response.length() == 0)
                        {
                            editor = mPreferences.edit();
                            editor.putString(AppUtils.SHARED_LOGIN_PROFILE, "");
                            editor.commit();
                            mCallBack.onViewProfileReceived();
                        }
                        else
                        {
                            String Str_Code = response.getString("success");
                            Log.d(MODULE, TAG + " Str_Code : " + Str_Code);

                            if (Str_Code.equals(ApiConstants.SUCCESS_CODE))
                            {
                                editor = mPreferences.edit();
                                editor.putString(AppUtils.SHARED_LOGIN_PROFILE, response.toString());
                                editor.commit();
                                mCallBack.onViewProfileReceived();
                            }
                            else
                            {
                                Str_Msg = response.getString("message");
                                mCallBack.onViewProfileReceivedError(Str_Msg);
                            }
                        }

                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                        Log.e(MODULE, TAG + " UnknownResponse");
                        Str_Msg = mActivity.getResources().getString(R.string.msg_unexpected_error);
                        mCallBack.onViewProfileReceivedError(Str_Msg);
                    }

                }

            },
                    new Response.ErrorListener()
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
                            mCallBack.onViewProfileReceivedError(Str_Msg);
                        }
                    });

            int socketTimeout = 60000;// 30 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            req.setRetryPolicy(policy);
            rq.add(req);
        }
        catch (Exception e)
        {
            Log.e(MODULE, TAG + " Exception Occurs - " + e);
            Str_Msg = mActivity.getResources().getString(R.string.msg_unexpected_error);
            mCallBack.onViewProfileReceivedError(Str_Msg);
        }
    }

}
