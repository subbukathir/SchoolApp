package com.daemon.oxfordschool.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.daemon.oxfordschool.Config;
import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.Utils.Font;
import com.daemon.oxfordschool.asyncprocess.RegisterDevice;
import com.daemon.oxfordschool.classes.User;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.fragment.FragmentDrawer;
import com.daemon.oxfordschool.fragment.Fragment_Attendance;
import com.daemon.oxfordschool.fragment.Fragment_Attendance_Staff;
import com.daemon.oxfordschool.fragment.Fragment_CCE_ExamReport;
import com.daemon.oxfordschool.fragment.Fragment_CCE_ExamReport_Student;
import com.daemon.oxfordschool.fragment.Fragment_Calendar;
import com.daemon.oxfordschool.fragment.Fragment_Class_List;
import com.daemon.oxfordschool.fragment.Fragment_Diary_Notes;
import com.daemon.oxfordschool.fragment.Fragment_Diary_Notes_Staff;
import com.daemon.oxfordschool.fragment.Fragment_Diary_Notes_Student;
import com.daemon.oxfordschool.fragment.Fragment_Discipline_Report;
import com.daemon.oxfordschool.fragment.Fragment_Discipline_Report_Staff;
import com.daemon.oxfordschool.fragment.Fragment_Discipline_Report_Student;
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
import com.daemon.oxfordschool.fragment.Fragment_School_Profile;
import com.daemon.oxfordschool.fragment.Fragment_Section_List;
import com.daemon.oxfordschool.fragment.Fragment_StudentList;
import com.daemon.oxfordschool.fragment.Fragment_StudentProfile;
import com.daemon.oxfordschool.fragment.Fragment_Student_View_Profile;
import com.daemon.oxfordschool.fragment.Fragment_TimeTable;
import com.daemon.oxfordschool.fragment.Fragment_ExamResult_Student;
import com.daemon.oxfordschool.fragment.Fragment_HomeWork_Student;
import com.daemon.oxfordschool.fragment.Fragment_Attendance_Student;
import com.daemon.oxfordschool.fragment.Fragment_ExamSchedule_Student;
import com.daemon.oxfordschool.fragment.Fragment_TimeTable_Staff;
import com.daemon.oxfordschool.fragment.Fragment_TimeTable_Student;
import com.daemon.oxfordschool.fragment.Fragment_PaymentDetail_Student;
import com.daemon.oxfordschool.fragment.Fragment_Add_Event;
import com.daemon.oxfordschool.fragment.Fragment_Add_Marks;
import com.daemon.oxfordschool.fragment.Fragment_Subjects;
import com.daemon.oxfordschool.fragment.Fragment_Mass_Notification;

import com.daemon.oxfordschool.gcm.GcmIntentService;
import com.daemon.oxfordschool.listeners.RegistrationListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener,
        RegistrationListener
{

    private static final String MODULE ="MainActivity" ;
    private static String TAG = "";

    Font font= MyApplication.getInstance().getFontInstance();
    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    User mUser;
    SharedPreferences mPreferences;
    SharedPreferences.Editor editor;
    String Str_Id="",Str_DeviceId="",Str_Token="",Str_Url="";

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private static String[] titles = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TAG = "onCreate";
        Log.d(MODULE,TAG);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getProfile();
        editor = mPreferences.edit();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);
        // display the first navigation drawer view on app launch
        setActionBarFont();
        mRegistrationBroadcastReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                TAG = "onReceive";
                Log.d(MODULE,TAG);
                try
                {
                    // checking for type intent filter
                    if (intent.getAction().equals(Config.REGISTRATION_COMPLETE))
                    {
                        TAG = "REGISTRATION_COMPLETE";
                        Log.d(MODULE,TAG);
                        String token = intent.getStringExtra("token");
                        Str_Token = token;
                        sendRegistrationToServer();
                    }
                    else if (intent.getAction().equals(Config.SENT_TOKEN_TO_SERVER))
                    {
                        // gcm registration id is stored in our server's MySQL
                        TAG = "SENT_TOKEN_TO_SERVER";
                        Log.d(MODULE,TAG);
                    }
                    else if (intent.getAction().equals(Config.PUSH_NOTIFICATION))
                    {
                        // new push notification is received
                        TAG = "PUSH_NOTIFICATION";
                        Log.d(MODULE,TAG);
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        };

        if (checkPlayServices())
        {
            registerGCM();
        }

    }

    private void setActionBarFont()
    {
        TextView titleTextView = null;
        try
        {
            Field f = mToolbar.getClass().getDeclaredField("mTitleTextView");
            f.setAccessible(true);
            titleTextView = (TextView) f.get(mToolbar);
            titleTextView.setTypeface(font.getHelveticaRegular());
        }
        catch (NoSuchFieldException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        TAG="onResume";
        Log.d(MODULE, TAG);

        if (getCurrentFocus() != null)
        {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.REGISTRATION_COMPLETE));
        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.PUSH_NOTIFICATION));
    }

    @Override
    protected void onPause()
    {
        TAG="onPause";
        Log.d(MODULE, TAG);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onDrawerItemSelected(View view,String Str_Item)
    {
        TAG = "onDrawerItemSelected";
        Log.d(MODULE, TAG);
        displayView(Str_Item);
    }

    private void displayView(String navItem) {

        TAG = "displayView";
        Log.d(MODULE,TAG);

        Fragment fragment = null;
        String title = getString(R.string.app_name);

        if(getString(R.string.lbl_about_us).equals(navItem))
        {
            fragment = new Fragment_School_Profile();
            title = getString(R.string.lbl_about_us);
        }
        else if(getString(R.string.lbl_profile).equals(navItem))
        {
            fragment = new Fragment_ProfileView();
            title = getString(R.string.lbl_profile);
        }

        else if(getString(R.string.lbl_student_profile).equals(navItem))
        {
            if(mUser.getUserType().equals(ApiConstants.STAFF) || mUser.getUserType().equals(ApiConstants.ADMIN))
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
        }
        else if(getString(R.string.lbl_events).equals(navItem))
        {
            fragment = new Fragment_Events();
            title = getString(R.string.lbl_events);
        }
        else if(getString(R.string.lbl_add_event).equals(navItem))
        {
            if(mUser.getUserType().equals(ApiConstants.STAFF) || mUser.getUserType().equals(ApiConstants.ADMIN))
            {
                fragment = new Fragment_Add_Event();
                title = getString(R.string.lbl_add_event);
            }
        }
        else if(getString(R.string.lbl_add_marks).equals(navItem))
        {
            if(mUser.getUserType().equals(ApiConstants.STAFF) || mUser.getUserType().equals(ApiConstants.ADMIN))
            {
                fragment = new Fragment_Add_Marks();
                title = getString(R.string.lbl_add_marks);
            }
        }
        else if(getString(R.string.lbl_homework).equals(navItem))
        {
            if(mUser.getUserType().equals(ApiConstants.STAFF) || mUser.getUserType().equals(ApiConstants.ADMIN))
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
        }
        else if(getString(R.string.lbl_diary).equals(navItem))
        {
            if(mUser.getUserType().equals(ApiConstants.STAFF) || mUser.getUserType().equals(ApiConstants.ADMIN))
            {
                fragment = new Fragment_Diary_Notes_Staff();
                title = getString(R.string.lbl_diary);
            }
            else if(mUser.getUserType().equals(ApiConstants.STUDENT))
            {
                fragment = new Fragment_Diary_Notes_Student();
                title = getString(R.string.lbl_diary);
            }
            else
            {
                fragment = new Fragment_Diary_Notes();
                title = getString(R.string.lbl_diary);
            }
        }
        else if(getString(R.string.lbl_class_management).equals(navItem))
        {
            if(mUser.getUserType().equals(ApiConstants.ADMIN))
            {
                fragment = new Fragment_Class_List();
                title = getString(R.string.lbl_class_management);
            }
        }
        else if(getString(R.string.lbl_section_management).equals(navItem))
        {
            if(mUser.getUserType().equals(ApiConstants.ADMIN))
            {
                fragment = new Fragment_Section_List();
                title = getString(R.string.lbl_section_management);
            }
        }
        else if(getString(R.string.lbl_discipline_report).equals(navItem))
        {
            if(mUser.getUserType().equals(ApiConstants.STAFF) || mUser.getUserType().equals(ApiConstants.ADMIN))
            {
                fragment = new Fragment_Discipline_Report_Staff();
                title = getString(R.string.lbl_discipline_report);
            }
            else if(mUser.getUserType().equals(ApiConstants.STUDENT))
            {
                fragment = new Fragment_Discipline_Report_Student();
                title = getString(R.string.lbl_discipline_report);
            }
            else
            {
                fragment = new Fragment_Discipline_Report();
                title = getString(R.string.lbl_discipline_report);
            }
        }
        else if(getString(R.string.lbl_attendance).equals(navItem))
        {
            if(mUser.getUserType().equals(ApiConstants.STAFF) || mUser.getUserType().equals(ApiConstants.ADMIN))
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
        }
        else if(getString(R.string.lbl_calendar).equals(navItem))
        {
            fragment = new Fragment_Calendar();
            title = getString(R.string.lbl_calendar);
        }
        else if(getString(R.string.lbl_exam_schedule).equals(navItem))
        {
            if(mUser.getUserType().equals(ApiConstants.STAFF) || mUser.getUserType().equals(ApiConstants.ADMIN))
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
        }
        else if(getString(R.string.lbl_exam_result).equals(navItem))
        {
            if(mUser.getUserType().equals(ApiConstants.STAFF) || mUser.getUserType().equals(ApiConstants.ADMIN))
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
        }
        else if(getString(R.string.lbl_fees_detail).equals(navItem))
        {
            if(mUser.getUserType().equals(ApiConstants.STAFF) || mUser.getUserType().equals(ApiConstants.ADMIN))
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
        }
        else if(getString(R.string.lbl_time_table).equals(navItem))
        {
            if(mUser.getUserType().equals(ApiConstants.STAFF) || mUser.getUserType().equals(ApiConstants.ADMIN))
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
        }
        else if(getString(R.string.lbl_reports).equals(navItem))
        {
            if(mUser.getUserType().equals(ApiConstants.PARENT))
            {
                fragment = new Fragment_CCE_ExamReport();
                title = getString(R.string.lbl_reports);
            }
            else if(mUser.getUserType().equals(ApiConstants.STUDENT))
            {
                fragment = new Fragment_CCE_ExamReport_Student();
                title = getString(R.string.lbl_reports);
            }
        }
        else if(getString(R.string.lbl_subjects).equals(navItem))
        {
            if(mUser.getUserType().equals(ApiConstants.STAFF) || mUser.getUserType().equals(ApiConstants.ADMIN))
            {
                fragment = new Fragment_Subjects();
                title = getString(R.string.lbl_subjects);
            }
        }
        else if(getString(R.string.lbl_mass_notification).equals(navItem))
        {
            if(mUser.getUserType().equals(ApiConstants.ADMIN))
            {
                fragment = new Fragment_Mass_Notification();
                title = getString(R.string.lbl_mass_notification);
            }
        }
        else if(getString(R.string.lbl_logout).equals(navItem))
        {
            editor = mPreferences.edit();
            editor.clear();
            editor.commit();

            Intent intent=new Intent(this,Activity_Login.class);
            startActivity(intent);
            this.finish();
        }

        if (fragment != null)
        {
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

    private boolean checkPlayServices()
    {
        TAG = "checkPlayServices";
        Log.d(MODULE,TAG);
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (apiAvailability.isUserResolvableError(resultCode))
            {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            else
            {
                Log.i(TAG, "This device is not supported. Google Play Services not installed!");
                Toast.makeText(getApplicationContext(), "This device is not supported. Google Play Services not installed!", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    // starting the service to register with GCM
    private void registerGCM()
    {
        TAG = "registerGCM";
        Log.d(MODULE,TAG);
        try
        {
            Intent intent = new Intent(this, GcmIntentService.class);
            intent.putExtra("key", "register");
            startService(intent);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void sendRegistrationToServer()
    {
        // Send the registration token to our server
        // to keep it in MySQL
        TAG = "sendRegistrationToServer";
        Log.d(MODULE,TAG);
        Str_Url = ApiConstants.DEVICE_REG_URL;
        try
        {
            GetUniqueDeviceId();
            if(Str_DeviceId.length()!=0 && Str_Token.length()!=0)
            {
                new RegisterDevice(Str_Url,this,Payload()).registerDevice();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void GetUniqueDeviceId()
    {
        try
        {
            TelephonyManager TelephonyMgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
            Str_DeviceId = TelephonyMgr.getDeviceId();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    public JSONObject Payload()
    {
        TAG = "Payload";
        Log.d(MODULE, TAG);

        JSONObject obj = new JSONObject();
        try {
            obj.put("RegId", Str_Token);
            obj.put("DeviceId",Str_DeviceId);
            obj.put("UserId", Str_Id);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(MODULE, TAG + " obj : " + obj.toString());

        return obj;
    }

    @Override
    public void onRegistrationReceived(String Str_Msg) {
        TAG = "onRegistrationReceived";
        Log.d(MODULE, TAG);
        Log.d(MODULE, TAG + " Str_Msg : " + Str_Msg);
        displayView(getString(R.string.lbl_about_us));
    }

    @Override
    public void onRegistrationReceivedError(String Str_Msg) {
        TAG = "onRegistrationReceivedError";
        Log.d(MODULE, TAG);
        Log.d(MODULE, TAG + " Str_Msg : " + Str_Msg);
        displayView(getString(R.string.lbl_about_us));
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


}