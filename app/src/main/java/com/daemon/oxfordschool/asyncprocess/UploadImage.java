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
import com.daemon.oxfordschool.listeners.ImageUploadListener;

import org.json.JSONObject;

/**
 * Created by daemonsoft on 8/03/16.
 */
public class UploadImage
{
    public static String MODULE = "UploadImage";
    public static String TAG ="";
    String Str_Msg = "",Str_Code="";
    ImageUploadListener mCallBack;
    String Str_Url="";
    JSONObject Payload;
    Fragment mFragment;
    AppCompatActivity mActivity;
    SharedPreferences mPreferences;
    SharedPreferences.Editor editor;

    public UploadImage(String Str_Url, JSONObject Payload, Fragment mFragment)
    {
        this.Str_Url = Str_Url;
        this.Payload = Payload;
        this.mFragment=mFragment;
        this.mActivity = (AppCompatActivity)mFragment.getActivity();
        mCallBack = (ImageUploadListener) mFragment;
        mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
        editor = mPreferences.edit();
    }

    /**
     * Putting profile image using json by making http call
     */
    public void putImageDetail()
    {
        TAG = "putImageDetail";
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
                    editor.putString(AppUtils.SHARED_UPLOAD_IMAGE, "");
                    editor.commit();
                    Str_Msg = mActivity.getResources().getString(R.string.msg_unexpected_error);
                    mCallBack.onImageUploadError(Str_Msg);
                }
                else
                {
                    String Str_Code = response.getString("success");
                    Log.d(MODULE, TAG + " Str_Code : " + Str_Code);

                    if (Str_Code.equals(ApiConstants.SUCCESS_CODE))
                    {
                        editor = mPreferences.edit();
                        editor.putString(AppUtils.SHARED_UPLOAD_IMAGE, response.toString());
                        editor.commit();
                        mCallBack.onImageUpload();
                    }
                    else
                    {
                        Str_Msg = response.getString("message");
                        mCallBack.onImageUploadError(Str_Msg);
                    }
                }
            }
            catch (Exception ex)
            {
                Str_Msg = mActivity.getResources().getString(R.string.msg_unexpected_error);
                mCallBack.onImageUploadError(Str_Msg);
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
            mCallBack.onImageUploadError(Str_Msg);
        }
    };

}
