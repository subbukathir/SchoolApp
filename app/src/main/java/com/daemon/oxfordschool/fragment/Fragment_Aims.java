package com.daemon.oxfordschool.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.Font;

public class Fragment_Aims extends Fragment
{
    public static String MODULE = "Fragment_Aims";
    public static String TAG = "";

    AppCompatActivity mActivity;
    TextView tv_our_aims_text_one,tv_our_aims_text_two,tv_our_aims_text_three,tv_our_aims_text_four,tv_our_aims_text_five,
            tv_our_aims_text_six,tv_our_aims_text_seven;
    private Font font= MyApplication.getInstance().getFontInstance();

    public Fragment_Aims()
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
            setHasOptionsMenu(true);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_aims, container, false);
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
            tv_our_aims_text_one = (TextView) view.findViewById(R.id.tv_our_aims_text_one);
            tv_our_aims_text_two = (TextView) view.findViewById(R.id.tv_our_aims_text_two);
            tv_our_aims_text_three = (TextView) view.findViewById(R.id.tv_our_aims_text_three);
            tv_our_aims_text_four = (TextView) view.findViewById(R.id.tv_our_aims_text_four);
            tv_our_aims_text_five = (TextView) view.findViewById(R.id.tv_our_aims_text_five);
            tv_our_aims_text_six = (TextView) view.findViewById(R.id.tv_our_aims_text_six);
            tv_our_aims_text_seven = (TextView) view.findViewById(R.id.tv_our_aims_text_seven);
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
            tv_our_aims_text_one.setTypeface(font.getHelveticaRegular());
            tv_our_aims_text_two.setTypeface(font.getHelveticaRegular());
            tv_our_aims_text_three.setTypeface(font.getHelveticaRegular());
            tv_our_aims_text_four.setTypeface(font.getHelveticaRegular());
            tv_our_aims_text_five.setTypeface(font.getHelveticaRegular());
            tv_our_aims_text_six.setTypeface(font.getHelveticaRegular());
            tv_our_aims_text_seven.setTypeface(font.getHelveticaRegular());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_settings).setVisible(false);
        menu.findItem(R.id.action_list_view).setVisible(false);
        menu.findItem(R.id.action_chart_view).setVisible(false);
        super.onPrepareOptionsMenu(menu);
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

}
