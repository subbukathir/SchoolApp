package com.daemon.oxfordschool.asyncprocess;

/**
 * Created by daemonsoft on 9/3/16.
 */
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.listeners.ImageUploadListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UploadImageData
{

    public static String MODULE = "UploadImageData ";
    public static String TAG = "";

    ProgressDialog loading;
    RequestHandler rh = new RequestHandler();
    AppCompatActivity mActivity;
    Fragment mFragment;
    ImageUploadListener mCallBack;
    HashMap<String,String> postParams;
    String Str_Msg = "";
    String Str_Url =ApiConstants.UPLOADIMAGE_URL;
    SharedPreferences mPreferences;

    public UploadImageData(Fragment mFragment,HashMap<String,String> postParams)
    {
        TAG = "UploadImageData";
        Log.d(MODULE, TAG);
        this.mFragment=mFragment;
        this.mActivity = (AppCompatActivity)mFragment.getActivity();
        mCallBack = (ImageUploadListener) mFragment;
        this.postParams = postParams;
        mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
        String Str_Title = mFragment.getString(R.string.msg_upload_image);
        String Str_Name = mFragment.getString(R.string.app_name);
        loading = ProgressDialog.show(mActivity,Str_Name,Str_Title,true,true);
    }

    /**
     * upload image to server
     */

    public void uploadImageToServer()
    {
        TAG = "uploadImageToServer";
        Log.d(MODULE,TAG);
        // Volley's json array request object
        StringRequest req = new StringRequest(Request.Method.POST,Str_Url,responseListener,responseErrorListener)
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                return postParams;
            }
        };
        Log.d(MODULE,TAG + "Body Params:::" + postParams);
        MyApplication.getInstance().addToRequestQueue(req);
    }


    Response.Listener<String> responseListener = new Response.Listener<String>()
    {
        @Override
        public void onResponse(String Str_Response)
        {
            try
            {
                Log.d(MODULE, TAG + " Str_Response:::" + Str_Response);
                loading.dismiss();
                JSONObject response = new JSONObject(Str_Response);
                if (response.toString().length()==0)
                {
                    Log.d(TAG, "empty");
                    Str_Msg = mActivity.getResources().getString(R.string.msg_unexpected_error);
                    mCallBack.onImageUploadError(Str_Msg);
                }
                else
                {
                    String Str_Code = response.getString("success");
                    Log.d(MODULE, TAG + " Str_Code : " + Str_Code);

                    if (Str_Code.equals(ApiConstants.SUCCESS_CODE))
                    {
                        Str_Msg = response.getString("message");
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
                ex.printStackTrace();
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
            error.printStackTrace();
            loading.dismiss();
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

