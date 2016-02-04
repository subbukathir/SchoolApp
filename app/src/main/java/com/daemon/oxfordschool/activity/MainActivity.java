package com.daemon.oxfordschool.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.fragment.FragmentDrawer;
import com.daemon.oxfordschool.fragment.Fragment_Events;
import com.daemon.oxfordschool.fragment.Fragment_ExamResult;
import com.daemon.oxfordschool.fragment.Fragment_ExamSchedule;
import com.daemon.oxfordschool.fragment.Fragment_HomeWork;
import com.daemon.oxfordschool.fragment.Fragment_ProfileView;
import com.daemon.oxfordschool.fragment.Fragment_StudentProfile;
import com.daemon.oxfordschool.fragment.Fragment_Attendance;

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private static final String MODULE ="MainActivity" ;
    private static String TAG = MainActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        // display the first navigation drawer view on app launch
        displayView(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_settings:
                 return true;
            case android.R.id.home:
                 FragmentDrawer.mDrawerLayout.openDrawer(Gravity.LEFT);
                 return false;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fragment = new Fragment_ProfileView();
                title = getString(R.string.lbl_profile);
                break;
            case 1:
                fragment = new Fragment_StudentProfile();
                title = getString(R.string.lbl_students);
                break;
            case 2:
                fragment = new Fragment_Events();
                title = getString(R.string.lbl_events);
                break;
            case 3:
                fragment = new Fragment_HomeWork();
                title = getString(R.string.lbl_homework);
                break;
            case 4:
                fragment = new Fragment_Attendance();
                title = getString(R.string.lbl_attendance);
                break;
            case 5:
                fragment = new Fragment_ExamSchedule();
                title = getString(R.string.lbl_exam_schedule);
                break;
            case 6:
                fragment = new Fragment_ExamResult();
                title = getString(R.string.lbl_exam_result);
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        TAG="onBackPressed";
        Log.d(MODULE, TAG);
    }
}