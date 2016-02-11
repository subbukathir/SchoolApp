package com.daemon.oxfordschool.asyncprocess;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.listeners.AttendanceAddListener;
import com.daemon.oxfordschool.listeners.ClassListListener;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by daemonsoft on 1/2/16.
 */
public class AttendanceAdd_Process
{
    public static String MODULE = "AttendanceAdd_Process";
    public static String TAG ="";

    String Str_Msg = "",Str_Code="";
    AttendanceAddListener mCallBack;
    String Str_Url=ApiConstants.ADD_ATTENDANCE;
    AppCompatActivity mActivity;
    Object Obj;
    JSONObject Payload;

    public AttendanceAdd_Process(AppCompatActivity mActivity,JSONObject Payload ,Object Obj)
    {
        TAG = "AttendanceAdd_Process";
        Log.d(MODULE,TAG);
        this.mActivity = mActivity;
        this.Obj = Obj;
        this.Payload = Payload;
        mCallBack = (AttendanceAddListener) Obj;
    }

    public void AddAttendance()
    {
        TAG = "AddAttendance";
        Log.d(MODULE,TAG);
        // Volley's json array request object
        JsonObjectRequest req = new JsonObjectRequest(Str_Url,Payload,responseListener,responseErrorListener);
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
                    Str_Msg = mActivity.getResources().getString(R.string.msg_unexpected_error);
                    mCallBack.onAttendanceAddReceivedError(Str_Msg);
                }
                else
                {
                    String Str_Code = response.getString("success");
                    Log.d(MODULE, TAG + " Str_Code : " + Str_Code);

                    if (Str_Code.equals(ApiConstants.SUCCESS_CODE))
                    {
                        Str_Msg = response.getString("message");
                        mCallBack.onAttendanceAddReceived(Str_Msg);
                    }
                    else
                    {
                        Str_Msg = response.getString("message");
                        mCallBack.onAttendanceAddReceivedError(Str_Msg);
                    }
                }
            }
            catch (Exception ex)
            {
                Str_Msg = mActivity.getResources().getString(R.string.msg_unexpected_error);
                mCallBack.onAttendanceAddReceivedError(Str_Msg);
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
            mCallBack.onAttendanceAddReceivedError(Str_Msg);
        }
    };
}
