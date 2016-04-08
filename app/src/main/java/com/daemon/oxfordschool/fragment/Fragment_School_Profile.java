package com.daemon.oxfordschool.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.adapter.SchoolProfileAdapter;


public class Fragment_School_Profile extends Fragment
{
    public static String MODULE = "Fragment_School_Profile";
    public static String TAG = "";

    int mSelectedPosition;

    AppCompatActivity mActivity;
    SharedPreferences mPreferences;
    ViewPager vp_school_profile;
    FragmentManager mManager;
    TabLayout tab_layout;

    public Fragment_School_Profile()
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
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS,Context.MODE_PRIVATE);
            setHasOptionsMenu(true);
            if (mActivity.getCurrentFocus() != null)
            {
                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
            }

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_school_profile, container, false);
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
            vp_school_profile = (ViewPager) view.findViewById(R.id.vp_school_profile);
            tab_layout = (TabLayout) view.findViewById(R.id.tab_layout);
            setProperties();
            setAdapter();
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
            tab_layout.addTab(tab_layout.newTab().setText(getString(R.string.lbl_about_us)));
            tab_layout.addTab(tab_layout.newTab().setText(getString(R.string.lbl_our_aims)));
            tab_layout.addTab(tab_layout.newTab().setText(getString(R.string.lbl_facilities)));
            tab_layout.setOnTabSelectedListener(_OnTabSelectedListener);
            vp_school_profile.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab_layout));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    TabLayout.OnTabSelectedListener _OnTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            mSelectedPosition=tab.getPosition();
            vp_school_profile.setCurrentItem(mSelectedPosition);
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    public void setAdapter()
    {
        TAG = "setAdapter";
        Log.d(MODULE, TAG);
        try
        {
            mManager =mActivity.getSupportFragmentManager();
            SchoolProfileAdapter adapter = new SchoolProfileAdapter(mManager,3);
            vp_school_profile.setAdapter(adapter);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                if(FragmentDrawer.mDrawerLayout.isDrawerOpen(GravityCompat.START))
                    FragmentDrawer.mDrawerLayout.closeDrawer(GravityCompat.START);
                else
                    FragmentDrawer.mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_settings).setVisible(false);
        menu.findItem(R.id.action_list_view).setVisible(false);
        menu.findItem(R.id.action_chart_view).setVisible(false);
        menu.findItem(R.id.action_help).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

}
