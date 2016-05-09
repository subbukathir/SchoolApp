package com.daemon.oxfordschool.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.Utils.Font;
import com.daemon.oxfordschool.adapter.StudentPagerAdapter;
import com.daemon.oxfordschool.asyncprocess.GetAttendance;
import com.daemon.oxfordschool.asyncprocess.GetStudentList;
import com.daemon.oxfordschool.classes.CResult;
import com.daemon.oxfordschool.classes.User;
import com.daemon.oxfordschool.components.InteractiveScrollView;
import com.daemon.oxfordschool.components.SimpleGestureFilter;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.decorators.AttendanceDecorator;
import com.daemon.oxfordschool.decorators.HighlightWeekendsDecorator;
import com.daemon.oxfordschool.decorators.MySelectorDecorator;
import com.daemon.oxfordschool.decorators.OneDayDecorator;
import com.daemon.oxfordschool.listeners.AttendanceListener;
import com.daemon.oxfordschool.listeners.StudentsListListener;
import com.daemon.oxfordschool.response.Attendance_Response;
import com.daemon.oxfordschool.response.StudentsList_Response;
import com.google.gson.reflect.TypeToken;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by daemonsoft on 3/2/16.
 */
public class Fragment_Attendance extends Fragment implements StudentsListListener,AttendanceListener,
        OnDateSelectedListener, OnMonthChangedListener
{
    public static String MODULE = "Fragment_Attendance ";
    public static String TAG = "";

    int mSelectedPosition,mSelectedMonthPosition;

    SharedPreferences mPreferences;
    User mUser,mSelectedUser;
    ViewPager vp_student;
    RelativeLayout layout_profile_header;
    MaterialCalendarView widget;
    SwipeRefreshLayout swipe_refresh_layout;
    InteractiveScrollView scrollView_Calendar;
    ArrayList<User> mListStudents =new ArrayList<User>();
    ArrayList<CResult> mAttendanceResult =new ArrayList<CResult>();
    Integer mSuccess;
    StudentsList_Response studentListresponse;
    Attendance_Response attendance_response;

    AppCompatActivity mActivity;
    String Str_Id="",mMonth="",mWorkingDays="",mPresentDays="",mPercentage="",mMonth_value;
    Spinner spinner_months;
    TextView tv_lbl_select_month, tv_working_days,tv_present_days,tv_percentage,text_view_empty,tv_lbl_date,tv_lbl_status;
    ArrayAdapter<CharSequence> adapter;
    RelativeLayout layout_empty;
    LinearLayout ll_attendance;
    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();

    private Font font= MyApplication.getInstance().getFontInstance();
    String Str_StudentList_Url = ApiConstants.STUDENT_LIST;
    String Str_Attendance_Url = ApiConstants.ATTENDANCE_BY_STUDENT_URL;
    CalendarDay day;
    private SimpleGestureFilter detector;

    public Fragment_Attendance()
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
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            getProfile();
            getStudentsList();
            setHasOptionsMenu(true);
            if (mActivity.getCurrentFocus() != null)
            {
                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
            }
            //Str_Date=GetTodayDate();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_attendance, container, false);
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
            layout_profile_header=(RelativeLayout) view.findViewById(R.id.layout_profile_header);
            vp_student = (ViewPager) view.findViewById(R.id.vp_student);
            swipe_refresh_layout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
            scrollView_Calendar = (InteractiveScrollView) view.findViewById(R.id.scrollView_Calendar);
            widget = (MaterialCalendarView) view.findViewById(R.id.calendarView);
            tv_lbl_select_month = (TextView)view.findViewById(R.id.tv_lbl_select_month);
            tv_working_days = (TextView)view.findViewById(R.id.tv_working_days);
            tv_present_days = (TextView)view.findViewById(R.id.tv_present_days);
            tv_percentage = (TextView)view.findViewById(R.id.tv_percentage);
            spinner_months=(Spinner)view.findViewById(R.id.spinner_month);
            layout_empty = (RelativeLayout) view.findViewById(R.id.layout_empty);
            text_view_empty = (TextView) view.findViewById(R.id.text_view_empty);
            ll_attendance = (LinearLayout) view.findViewById(R.id.ll_attendance_details);
            String[] items = getResources().getStringArray(R.array.array_months);
            adapter = ArrayAdapter.createFromResource(mActivity,R.array.array_months, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_months.setAdapter(adapter);
            spinner_months.setOnItemSelectedListener(_OnMonthSelectedListener);
            setProperties();
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
            text_view_empty.setTypeface(font.getHelveticaRegular());
            tv_lbl_select_month.setTypeface(font.getHelveticaRegular());
            tv_working_days.setTypeface(font.getHelveticaRegular());
            tv_present_days.setTypeface(font.getHelveticaRegular());
            tv_percentage.setTypeface(font.getHelveticaRegular());

            vp_student.addOnPageChangeListener(_OnPageChangeListener);
            scrollView_Calendar.setOnBottomReachedListener(_OnBottomChangedListener);
            swipe_refresh_layout.setRefreshing(false);
            swipe_refresh_layout.setOnRefreshListener(_OnRefreshListener);
            text_view_empty.setText(getString(R.string.lbl_no_attendance) + " " + mMonth_value);

            widget.setFirstDayOfWeek(Calendar.MONDAY);
            widget.setOnDateChangedListener(this);
            widget.setOnMonthChangedListener(this);
            widget.setShowOtherDates(MaterialCalendarView.SHOW_ALL);
            widget.setArrowColor(getResources().getColor(R.color.color_app));
            widget.setSelectionColor(getResources().getColor(R.color.color_app));
            widget.addDecorators(
                    new MySelectorDecorator(mActivity),
                    new HighlightWeekendsDecorator(),
                    oneDayDecorator
            );
            day= CalendarDay.today();
            mMonth = Integer.toString(day.getMonth() + 1);
            try
            {
                Field f = swipe_refresh_layout.getClass().getDeclaredField("mCircleView");
                f.setAccessible(true);
                ImageView img = (ImageView)f.get(swipe_refresh_layout);
                img.setAlpha(0f);
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
            if (mListStudents.size()>0)
            {
                showStudentsList();
                getAttendanceFromService();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
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

    AdapterView.OnItemSelectedListener _OnMonthSelectedListener = new AdapterView.OnItemSelectedListener()
    {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id)
        {
            TAG="onItemSelected";
            Log.d(MODULE,TAG);
            try
            {
                if(pos>0)
                {
                    mSelectedMonthPosition=pos;
                    mMonth=Integer.toString(pos);
                    mMonth_value= spinner_months.getSelectedItem().toString();
                    Log.d(MODULE, TAG + " position " + pos + " value " + mMonth_value);
                    getAttendanceFromService();
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView)
        {

        }
    };

    ViewPager.OnPageChangeListener _OnPageChangeListener = new ViewPager.OnPageChangeListener()
    {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {

        }

        @Override
        public void onPageSelected(int position)
        {
            mSelectedPosition=position;
            getAttendanceFromService();
        }

        @Override
        public void onPageScrollStateChanged(int state) {}
    };

    InteractiveScrollView.OnBottomReachedListener _OnBottomChangedListener = new InteractiveScrollView.OnBottomReachedListener()
    {
        @Override
        public void onBottomReached()
        {
            //vp_student.setVisibility(View.GONE);
            slideToTop((View) layout_profile_header);
        }
    };

   SwipeRefreshLayout.OnRefreshListener _OnRefreshListener = new SwipeRefreshLayout.OnRefreshListener()
   {
       @Override
       public void onRefresh()
       {
           if (swipe_refresh_layout.isRefreshing()) swipe_refresh_layout.setRefreshing(false);
           if(!layout_profile_header.isShown())
           {
              //vp_student.setVisibility(View.VISIBLE);
               slideToBottom((View) layout_profile_header);
           }
       }
   };

    @Override
    public void onStudentsReceived()
    {
        TAG = "onStudentsReceived";
        Log.d(MODULE, TAG);
        try
        {
            getStudentsList();
            if(mListStudents.size()>0)
            {
                showStudentsList();
                getAttendanceFromService();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onStudentsReceivedError(String Str_Msg)
    {
        TAG = "onStudentsReceivedError";
        Log.d(MODULE, TAG);
        try
        {

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @Nullable CalendarDay date, boolean selected)
    {
        TAG = "onDateSelected";
        Log.d(MODULE, TAG);
        try
        {

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date)
    {
        TAG = "onMonthChanged";
        Log.d(MODULE, TAG);
        try
        {
            mSelectedMonthPosition=date.getMonth()+1;
            mMonth=Integer.toString(date.getMonth() + 1);
            getAttendanceFromService();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void getStudentsList()
    {
        TAG = "getStudentsList";
        Log.d(MODULE, TAG);
        try
        {
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            String Str_Json = mPreferences.getString(AppUtils.SHARED_STUDENT_LIST,"");
            Log.d(MODULE, TAG + " Str_Json : " + Str_Json);
            if(Str_Json.length()>0)
            {
                studentListresponse = (StudentsList_Response) AppUtils.fromJson(Str_Json, new TypeToken<StudentsList_Response>(){}.getType());
                mListStudents = studentListresponse.getCstudents();
                Log.d(MODULE, TAG + " mListStudents : " + mListStudents.size());
                if(mListStudents==null || mListStudents.size()==0)
                {
                    new GetStudentList(Str_StudentList_Url,Payload_StudentList(),this).getStudents();
                }
            }
            else
            {
                new GetStudentList(Str_StudentList_Url,Payload_StudentList(),this).getStudents();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void showStudentsList()
    {
        TAG = "showStudentsList";
        Log.d(MODULE, TAG);
        try
        {
            StudentPagerAdapter adapter = new StudentPagerAdapter(mActivity,mListStudents);
            vp_student.setAdapter(adapter);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void getAttendanceFromService()
    {
        TAG = "getAttendanceFromService";
        Log.d(MODULE, TAG);
        mSelectedUser=mListStudents.get(mSelectedPosition);
        AppUtils.showProgressDialog(mActivity);
        new GetAttendance(Str_Attendance_Url,Payload_Attendance(),Fragment_Attendance.this).getAttendance();
        ll_attendance.setVisibility(View.VISIBLE);
        text_view_empty.setText(getString(R.string.lbl_no_attendance) + " "+mMonth_value);
    }

    @Override
    public void onAttendanceReceived()
    {
        TAG = "onAttendanceReceived";
        Log.d(MODULE, TAG);

        getAttendanceDetails();
        showAttendanceDetails();
    }

    @Override
    public void onAttendanceReceivedError(String Str_Msg)
    {
        AppUtils.hideProgressDialog();
        TAG = "onAttendanceReceivedError";
        Log.d(MODULE, TAG + "error " + Str_Msg);

        showEmptyView();
    }

    public void getAttendanceDetails()
    {
        TAG = "getAttendanceDetails";
        Log.d(MODULE, TAG);
        try
        {
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            String Str_Json = mPreferences.getString(AppUtils.SHARED_ATTENTANCE,"");
            Log.d(MODULE, TAG + " Str_Json : " + Str_Json);
            if(Str_Json.length()>0)
            {
                attendance_response = (Attendance_Response) AppUtils.fromJson(Str_Json, new TypeToken<Attendance_Response>(){}.getType());
                mSuccess = Integer.parseInt(attendance_response.getSuccess());
                mAttendanceResult=attendance_response.getCresult();
                mWorkingDays = attendance_response.getWorkingDays();
                mPresentDays = attendance_response.getPresentDays();
                mPercentage = attendance_response.getPercentage();

                Log.d(MODULE, TAG + " ListSize of attendance : " + mAttendanceResult.size());

                for (int i = 0; i < mAttendanceResult.size(); i++) {
                    final CResult mAttendance = mAttendanceResult.get(i);

                    Log.d(MODULE, TAG + " AttendanceId : " + mAttendance.getAttendanceId());
                    Log.d(MODULE, TAG + " AttendanceDate : " + mAttendance.getAttendanceDate());
                    Log.d(MODULE, TAG + " IsPresent : " + mAttendance.getIsPresent());
                    Log.d(MODULE, TAG + " IsAfterNoon : " + mAttendance.getIsAfterNoon());
                    Log.d(MODULE, TAG + " IsHalfDay : " + mAttendance.getIsHalfDay());
                }


                Log.d(MODULE, TAG + " Success_Code : " + mSuccess.toString());

            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    public void showAttendanceDetails()
    {
        TAG = "showAttendanceDetails";
        Log.d(MODULE, TAG);

        try
        {
            AppUtils.hideProgressDialog();
            if(mSuccess==0)
            {
                tv_working_days.setText( getString(R.string.lbl_working_days) + " : " + mWorkingDays);
                tv_present_days.setText(getString(R.string.lbl_present_days) + " : " + mPresentDays);
                tv_percentage.setText(mPercentage);

                if(mAttendanceResult.size()>0)
                {
                    ArrayList<CalendarDay> presentDates = new ArrayList<>();
                    ArrayList<CalendarDay> ObsentDates = new ArrayList<>();
                    for(int i=0;i<mAttendanceResult.size(); i++)
                    {
                        String startDateTime = mAttendanceResult.get(i).getAttendanceDate();
                        CalendarDay day = GetCalendarDay(startDateTime);
                        if(mAttendanceResult.get(i).getIsPresent().equals("0")) presentDates.add(day);
                        else ObsentDates.add(day);
                    }
                    widget.addDecorator(new AttendanceDecorator(Color.GREEN,presentDates));
                    widget.addDecorator(new AttendanceDecorator(Color.RED,ObsentDates));
                }
                else
                {
                    showEmptyView();
                }
            }
            else
            {
                showEmptyView();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void showEmptyView()
    {
        TAG = "showEmptyView";
        Log.d(MODULE, TAG);
        AppUtils.hideProgressDialog();
        try
        {
            tv_working_days.setText( getString(R.string.lbl_working_days) + " : 0 ");
            tv_present_days.setText(getString(R.string.lbl_present_days) + " : 0 ");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public JSONObject Payload_StudentList()
    {
        TAG = "Payload";
        Log.d(MODULE, TAG);

        JSONObject obj = new JSONObject();
        try {
            obj.put("ParentId", Str_Id);
            obj.put("ClassId", "");
            obj.put("SectionId", "");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(MODULE, TAG + " obj : " + obj.toString());

        return obj;
    }

    public JSONObject Payload_Attendance()
    {
        TAG = "Payload_Attendance";
        Log.d(MODULE,TAG);
        JSONObject obj = new JSONObject();
        try
        {
            obj.put("Month",mMonth);
            obj.put("StudentId", mSelectedUser.getStudentId());
        }
        catch (JSONException ex)
        {
            ex.printStackTrace();
        }
        Log.d(MODULE, TAG + " obj : " + obj.toString());
        return obj;
    }

    public CalendarDay GetCalendarDay(String Str_DateTime)
    {
        try
        {
            String[] startDate = Str_DateTime.split(" ");
            String[] array_datetime = startDate[0].split("-");
            int mYear = Integer.parseInt(array_datetime[0]);
            int mMonth = Integer.parseInt(array_datetime[1]);
            int mDate = Integer.parseInt(array_datetime[2]);
            CalendarDay day = CalendarDay.from(mYear,(mMonth-1),mDate);
            return day;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_settings).setVisible(false);
        menu.findItem(R.id.action_list_view).setVisible(false);
        menu.findItem(R.id.action_chart_view).setVisible(false);
        menu.findItem(R.id.action_help).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
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

    // To animate view slide out from top to bottom
    public void slideToBottom(View view){
        TranslateAnimation animate = new TranslateAnimation(0,0,-view.getHeight(),0);
        animate.setDuration(500);
        animate.setAnimationListener(animationDownListener);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    // To animate view slide out from bottom to top
    public void slideToTop(View view){
        TranslateAnimation animate = new TranslateAnimation(0,0,0,-view.getHeight());
        animate.setDuration(500);
        animate.setFillAfter(true);
        animate.setAnimationListener(animationUpListener);
        view.startAnimation(animate);
    }

    Animation.AnimationListener animationDownListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            layout_profile_header.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    Animation.AnimationListener animationUpListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

            layout_profile_header.setVisibility(View.GONE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

}
