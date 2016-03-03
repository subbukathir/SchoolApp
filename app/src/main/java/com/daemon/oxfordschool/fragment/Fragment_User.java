package com.daemon.oxfordschool.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daemon.oxfordschool.R;

import java.util.zip.Inflater;

/**
 * Created by daemonsoft on 28/1/16.
 */
public class Fragment_User extends Fragment
{
    public static String MODULE = "Fragment_User";
    public static String TAG="";

    AppCompatActivity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TAG="onCreate";
        Log.d(MODULE,TAG);

        mActivity = (AppCompatActivity)getActivity();

        //InitUI()

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        TAG="onCreateView";
        Log.d(MODULE,TAG);
        View rootView= inflater.inflate(R.layout.fragment_profile,container,false);
        InitUI(rootView);
        return rootView;
    }

    public void InitUI(View view)
    {
        TAG="InitUI";
        Log.d(MODULE,TAG);


    }
}
