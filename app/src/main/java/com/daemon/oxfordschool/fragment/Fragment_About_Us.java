package com.daemon.oxfordschool.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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

public class Fragment_About_Us extends Fragment
{
    public static String MODULE = "Fragment_About_Us";
    public static String TAG = "";

    AppCompatActivity mActivity;
    TextView tv_about_us_one,tv_about_us_two,tv_about_us_three;
    private Font font= MyApplication.getInstance().getFontInstance();

    public Fragment_About_Us()
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
        View rootView = inflater.inflate(R.layout.fragment_about_us, container, false);
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
            tv_about_us_one = (TextView) view.findViewById(R.id.tv_about_us_one);
            tv_about_us_two = (TextView) view.findViewById(R.id.tv_about_us_two);
            tv_about_us_three = (TextView) view.findViewById(R.id.tv_about_us_three);
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
            tv_about_us_one.setTypeface(font.getHelveticaRegular());
            tv_about_us_two.setTypeface(font.getHelveticaRegular());
            tv_about_us_three.setTypeface(font.getHelveticaRegular());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

}
