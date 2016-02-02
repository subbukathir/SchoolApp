package com.daemon.oxfordschool.fragment;

/**
 * Created by Ravi on 29/07/15.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.Utils.Font;
import com.daemon.oxfordschool.classes.User;
import com.google.gson.reflect.TypeToken;

public class Fragment_ProfileView extends Fragment
{

    public static String MODULE = "Fragment_ProfileView ";
    public static String TAG = "";

    TextView tv_profile_mobile_number,tv_profile_email,tv_lbl_profile_address,tv_profile_address;
    SharedPreferences mPreferences;
    User mUser;
    AppCompatActivity mActivity;
    String Str_Id="";
    private Font font= MyApplication.getInstance().getFontInstance();

    public Fragment_ProfileView()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        TAG = "onCreate";
        Log.d(MODULE, TAG);
        try
        {
            mActivity = (AppCompatActivity) getActivity();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_profileview, container, false);
        TAG = "onCreateView";
        Log.d(MODULE, TAG);
        initView(rootView);
        return rootView;
    }

    public void initView(View view)
    {
        TAG = "initView";
        Log.d(MODULE, TAG);
        try
        {
            tv_profile_mobile_number=(TextView) view.findViewById(R.id.tv_profile_mobile_number);
            tv_profile_email=(TextView) view.findViewById(R.id.tv_profile_email);
            tv_lbl_profile_address=(TextView) view.findViewById(R.id.tv_lbl_profile_address);
            tv_profile_address=(TextView) view.findViewById(R.id.tv_profile_address);
            setProperties();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();

        TAG = "onStart";
        Log.d(MODULE, TAG);

        try
        {
            getProfile();
            setProfile();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void setProperties()
    {
        TAG = "setProperties";
        Log.d(MODULE, TAG);
        try
        {
            tv_profile_mobile_number.setTypeface(font.getHelveticaRegular());
            tv_profile_email.setTypeface(font.getHelveticaRegular());
            tv_lbl_profile_address.setTypeface(font.getHelveticaRegular());
            tv_profile_address.setTypeface(font.getHelveticaRegular());
        }
        catch (Exception ex)
        {

        }
    }

    public void getProfile()
    {
        TAG = "getProfile";
        Log.d(MODULE, TAG);
        try
        {
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            String Str_Json = mPreferences.getString(AppUtils.SHARED_LOGIN_PROFILE,"");
            Log.d(MODULE, TAG + " Str_Json : " + Str_Json);
            if(Str_Json.length()>0)
            {
                mUser = (User) AppUtils.fromJson(Str_Json, new TypeToken<User>(){}.getType());
                Str_Id = mUser.getID();
                Log.d(MODULE, TAG + " Str_Id : " + Str_Id);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void setProfile()
    {
        TAG = "setProfile";
        Log.d(MODULE, TAG);

        tv_profile_mobile_number.setText(mUser.getMobile_Number());
        tv_profile_email.setText(mUser.getEmail());
        StringBuilder Str_Address = new StringBuilder();
        Str_Address.append(mUser.getAddress1()).append(" ");
        Str_Address.append(mUser.getAddress2()).append(" ");
        Str_Address.append(mUser.getAddress3()).append(" ");
        tv_profile_address.setText(Str_Address);
    }



}
