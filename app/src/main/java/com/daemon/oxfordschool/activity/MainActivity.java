package com.daemon.oxfordschool.activity;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.classes.User;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.fragment.FragmentDrawer;
import com.daemon.oxfordschool.fragment.Fragment_Attendance_Staff;
import com.daemon.oxfordschool.fragment.Fragment_Events;
import com.daemon.oxfordschool.fragment.Fragment_ExamResult;
import com.daemon.oxfordschool.fragment.Fragment_ExamSchedule;
import com.daemon.oxfordschool.fragment.Fragment_Exam_Result_Staff;
import com.daemon.oxfordschool.fragment.Fragment_Exam_Schedule_Staff;
import com.daemon.oxfordschool.fragment.Fragment_HomeWork;
import com.daemon.oxfordschool.fragment.Fragment_HomeWork_Staff;
import com.daemon.oxfordschool.fragment.Fragment_PaymentDetail;
import com.daemon.oxfordschool.fragment.Fragment_PaymentDetail_Staff;
import com.daemon.oxfordschool.fragment.Fragment_ProfileView;
import com.daemon.oxfordschool.fragment.Fragment_StudentList;
import com.daemon.oxfordschool.fragment.Fragment_StudentProfile;
import com.daemon.oxfordschool.fragment.Fragment_Attendance;
import com.daemon.oxfordschool.fragment.Fragment_Student_View_Profile;
import com.daemon.oxfordschool.fragment.Fragment_TimeTable;
import com.daemon.oxfordschool.fragment.Fragment_TimeTable_Staff;
import com.daemon.oxfordschool.fragment.Fragment_ExamResult_Student;
import com.daemon.oxfordschool.fragment.Fragment_HomeWork_Student;
import com.daemon.oxfordschool.fragment.Fragment_Attendance_Student;
import com.daemon.oxfordschool.fragment.Fragment_ExamSchedule_Student;
import com.daemon.oxfordschool.fragment.Fragment_TimeTable_Student;
import com.daemon.oxfordschool.fragment.Fragment_PaymentDetail_Student;

import com.google.gson.reflect.TypeToken;

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private static final String MODULE ="MainActivity" ;
    private static String TAG = MainActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    User mUser;
    SharedPreferences mPreferences;
    String Str_Id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getProfile();

        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
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
                if(mUser.getUserType().equals(ApiConstants.STAFF))
                {
                    fragment = new Fragment_StudentList();
                    title = getString(R.string.lbl_students);
                }
                else if(mUser.getUserType().equals(ApiConstants.STUDENT))
                {
                    fragment = new Fragment_Student_View_Profile();
                    title = getString(R.string.lbl_students);
                }
                else
                {
                    fragment = new Fragment_StudentProfile();
                    title = getString(R.string.lbl_students);
                }
                break;
            case 2:
                fragment = new Fragment_Events();
                title = getString(R.string.lbl_events);
                break;
            case 3:
                if(mUser.getUserType().equals(ApiConstants.STAFF))
                {
                    fragment = new Fragment_HomeWork_Staff();
                    title = getString(R.string.lbl_homework);
                }
                else if(mUser.getUserType().equals(ApiConstants.STUDENT))
                {
                    fragment = new Fragment_HomeWork_Student();
                    title = getString(R.string.lbl_homework);
                }
                else
                {
                    fragment = new Fragment_HomeWork();
                    title = getString(R.string.lbl_homework);
                }
                break;
            case 4:
                if(mUser.getUserType().equals(ApiConstants.STAFF))
                {
                    fragment = new Fragment_Attendance_Staff();
                    title = getString(R.string.lbl_attendance);
                }
                else if(mUser.getUserType().equals(ApiConstants.STUDENT))
                {
                    fragment = new Fragment_Attendance_Student();
                    title = getString(R.string.lbl_attendance);
                }
                else
                {
                    fragment = new Fragment_Attendance();
                    title = getString(R.string.lbl_attendance);
                }
                break;
            case 5:
                if(mUser.getUserType().equals(ApiConstants.STAFF))
                {
                    fragment = new Fragment_Exam_Schedule_Staff();
                    title = getString(R.string.lbl_exam_schedule);
                }
                else if(mUser.getUserType().equals(ApiConstants.STUDENT))
                {
                    fragment = new Fragment_ExamSchedule_Student();
                    title = getString(R.string.lbl_exam_schedule);
                }
                else
                {
                    fragment = new Fragment_ExamSchedule();
                    title = getString(R.string.lbl_exam_schedule);
                }
                break;
            case 6:
                if(mUser.getUserType().equals(ApiConstants.STAFF))
                {
                    fragment = new Fragment_Exam_Result_Staff();
                    title = getString(R.string.lbl_exam_result);
                }
                else if(mUser.getUserType().equals(ApiConstants.STUDENT))
                {
                    fragment = new Fragment_ExamResult_Student();
                    title = getString(R.string.lbl_exam_result);
                }
                else
                {
                    fragment = new Fragment_ExamResult();
                    title = getString(R.string.lbl_exam_result);
                }
                break;
            case 7:
                if(mUser.getUserType().equals(ApiConstants.STAFF))
                {
                    fragment = new Fragment_PaymentDetail_Staff();
                    title = getString(R.string.lbl_fees_detail);
                }
                else if(mUser.getUserType().equals(ApiConstants.STUDENT))
                {
                    fragment = new Fragment_PaymentDetail_Student();
                    title = getString(R.string.lbl_fees_detail);
                }
                else
                {
                    fragment = new Fragment_PaymentDetail();
                    title = getString(R.string.lbl_fees_detail);
                }
                break;
            case 8:
                if(mUser.getUserType().equals(ApiConstants.STAFF))
                {
                    fragment = new Fragment_TimeTable_Staff();
                    title = getString(R.string.lbl_time_table);
                }
                else if(mUser.getUserType().equals(ApiConstants.STUDENT))
                {
                    fragment = new Fragment_TimeTable_Student();
                    title = getString(R.string.lbl_time_table);
                }
                else
                {
                    fragment = new Fragment_TimeTable();
                    title = getString(R.string.lbl_time_table);
                }
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

    public void getProfile()
    {
        TAG = "getProfile";
        Log.d(MODULE, TAG);
        try
        {
            mPreferences = this.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
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

}