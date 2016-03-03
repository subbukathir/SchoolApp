package com.daemon.oxfordschool.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.Utils.Font;
import com.daemon.oxfordschool.adapter.AttendanceAdapter;
import com.daemon.oxfordschool.adapter.StudentPagerAdapter;
import com.daemon.oxfordschool.asyncprocess.GetAttendance;
import com.daemon.oxfordschool.asyncprocess.GetStudentList;
import com.daemon.oxfordschool.classes.User;
import com.daemon.oxfordschool.classes.CResult;
import com.daemon.oxfordschool.components.RecycleEmptyErrorView;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.listeners.Attendance_List_Item_Click_Listener;
import com.daemon.oxfordschool.response.Attendance_Response;
import com.daemon.oxfordschool.response.StudentsList_Response;
import com.daemon.oxfordschool.listeners.AttendanceListener;
import com.daemon.oxfordschool.listeners.StudentsListListener;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by daemonsoft on 3/2/16.
 */
public class Fragment_Attendance extends Fragment implements StudentsListListener,AttendanceListener,AdapterView.OnItemSelectedListener
        ,Attendance_List_Item_Click_Listener
{
    public static String MODULE = "Fragment_Attendance ";
    public static String TAG = "";

    int mSelectedPosition,mSelectedMonthPosition;

    RecycleEmptyErrorView recycler_view;
    RecyclerView.LayoutManager mLayoutManager;
    SharedPreferences mPreferences;
    User mUser,mSelectedUser;
    ViewPager vp_student;
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

    private Font font= MyApplication.getInstance().getFontInstance();
    String Str_StudentList_Url = ApiConstants.STUDENT_LIST;
    String Str_Attendance_Url = ApiConstants.ATTENDANCE_BY_STUDENT_URL;


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
            vp_student = (ViewPager) view.findViewById(R.id.vp_student);
            recycler_view = (RecycleEmptyErrorView) view.findViewById(R.id.recycler_view_attendance);
            tv_lbl_select_month = (TextView)view.findViewById(R.id.tv_lbl_select_month);
            tv_lbl_date = (TextView)view.findViewById(R.id.tv_lbl_date);
            tv_lbl_status = (TextView)view.findViewById(R.id.tv_lbl_status);
            tv_working_days = (TextView)view.findViewById(R.id.tv_working_days);
            tv_present_days = (TextView)view.findViewById(R.id.tv_present_days);
            tv_percentage = (TextView)view.findViewById(R.id.tv_percentage);
            spinner_months=(Spinner)view.findViewById(R.id.spinner_month);
            layout_empty = (RelativeLayout) view.findViewById(R.id.layout_empty);
            text_view_empty = (TextView) view.findViewById(R.id.text_view_empty);
            ll_attendance = (LinearLayout) view.findViewById(R.id.ll_attendance_details);
            String[] items = getResources().getStringArray(R.array.array_months);
            adapter = ArrayAdapter.createFromResource(mActivity,
                    R.array.array_months, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_months.setAdapter(adapter);
            spinner_months.setOnItemSelectedListener(this);
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
            layout_empty.setVisibility(View.GONE);
            ll_attendance.setVisibility(View.GONE);
            text_view_empty.setTypeface(font.getHelveticaRegular());
            tv_lbl_select_month.setTypeface(font.getHelveticaRegular());
            tv_working_days.setTypeface(font.getHelveticaRegular());
            tv_present_days.setTypeface(font.getHelveticaRegular());
            tv_percentage.setTypeface(font.getHelveticaRegular());
            tv_lbl_date.setTypeface(font.getHelveticaBold());
            tv_lbl_status.setTypeface(font.getHelveticaBold());
            mLayoutManager = new LinearLayoutManager(getActivity());
            recycler_view.setLayoutManager(mLayoutManager);
            vp_student.addOnPageChangeListener(_OnPageChangeListener);
            text_view_empty.setText(getString(R.string.lbl_no_attendance) + " " + mMonth_value);
        }
        catch (Exception ex)
        {

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
            if(mListStudents.size()>0) showStudentsList();
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l)
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
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    ViewPager.OnPageChangeListener _OnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mSelectedPosition=position;
            getAttendanceFromService();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void onStudentsReceived() {
        TAG = "onStudentsReceived";
        Log.d(MODULE, TAG);
        try
        {
            getStudentsList();
            if(mListStudents.size()>0)
            {
                showStudentsList();
                mSelectedPosition=0;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onStudentsReceivedError(String Str_Msg) {
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
        Log.d(MODULE,TAG + "getAttendanceFromService");
        mSelectedUser=mListStudents.get(mSelectedPosition);
        if(mSelectedMonthPosition==0)
        {
            text_view_empty.setText(getString(R.string.lbl_select_month));
            showEmptyView();
        }
        else
        {
            AppUtils.showProgressDialog(mActivity);
            new GetAttendance(Str_Attendance_Url,Payload_Attendance(),Fragment_Attendance.this).getAttendance();
            ll_attendance.setVisibility(View.VISIBLE);
            text_view_empty.setText(getString(R.string.lbl_no_attendance) + " "+mMonth_value);
        }
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

    @Override
    public void onAttendanceListItemClicked(int position) {

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
                tv_present_days.setText(getString(R.string.lbl_present_days) + " : " +mPresentDays);
                tv_percentage.setText(mPercentage);
                ll_attendance.setVisibility(View.VISIBLE);
                layout_empty.setVisibility(View.GONE);
                if(mAttendanceResult.size()>0)
                {
                    AttendanceAdapter adapter = new AttendanceAdapter(mAttendanceResult,this);
                    if(recycler_view!=null)recycler_view.setAdapter(adapter);
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
            layout_empty.setVisibility(View.VISIBLE);
            ll_attendance.setVisibility(View.GONE);
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

}
