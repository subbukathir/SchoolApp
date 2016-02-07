package com.daemon.oxfordschool.fragment;

/**
 * Created by Ravi on 29/07/15.
 */
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
import com.daemon.oxfordschool.adapter.StudentsListAdapter;
import com.daemon.oxfordschool.asyncprocess.ClassList_Process;
import com.daemon.oxfordschool.asyncprocess.GetAttendance;
import com.daemon.oxfordschool.asyncprocess.GetStudentList;
import com.daemon.oxfordschool.asyncprocess.SectionList_Process;
import com.daemon.oxfordschool.asyncprocess.SubjectList_Process;
import com.daemon.oxfordschool.classes.CResult;
import com.daemon.oxfordschool.classes.Common_Class;
import com.daemon.oxfordschool.classes.User;
import com.daemon.oxfordschool.components.RecycleEmptyErrorView;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.listeners.AttendanceListener;
import com.daemon.oxfordschool.listeners.Attendance_List_Item_Click_Listener;
import com.daemon.oxfordschool.listeners.ClassListListener;
import com.daemon.oxfordschool.listeners.SectionListListener;
import com.daemon.oxfordschool.listeners.Student_List_Item_Click_Listener;
import com.daemon.oxfordschool.listeners.StudentsListListener;
import com.daemon.oxfordschool.response.Attendance_Response;
import com.daemon.oxfordschool.response.CommonList_Response;
import com.daemon.oxfordschool.response.StudentsList_Response;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Fragment_Attendance_Staff extends Fragment implements StudentsListListener,ClassListListener,
        SectionListListener,Student_List_Item_Click_Listener,AttendanceListener,Attendance_List_Item_Click_Listener
{

    public static String MODULE = "Fragment_Attendance_Staff ";
    public static String TAG = "";

    RecycleEmptyErrorView recycler_view;
    RecyclerView.LayoutManager mLayoutManager;
    Spinner spinner_class,spinner_section,spinner_student,spinner_month;
    SharedPreferences mPreferences;
    User mUser,mSelectedStudent;
    int mSelectedPosition,mSelectedMonthPosition=0;
    ArrayList<Common_Class> mListClass =new ArrayList<Common_Class>();
    ArrayList<Common_Class> mListSection =new ArrayList<Common_Class>();
    ArrayList<User> mListStudents =new ArrayList<User>();
    ArrayList<CResult> mAttendanceResult =new ArrayList<CResult>();
    Integer mSuccess;
    CommonList_Response responseCommon;
    StudentsList_Response response;
    Attendance_Response attendance_response;

    AppCompatActivity mActivity;
    String Str_Id="";
    private Font font= MyApplication.getInstance().getFontInstance();
    String Str_ClassId,Str_SectionId,Str_StudentId,mMonth_value,mWorkingDays,mPresentDays,mPercentage,mMonth="";
    TextView tv_lbl_class,tv_lbl_section,tv_lbl_student,tv_lbl_month,tv_working_days,tv_present_days,
            tv_percentage,text_view_empty,tv_lbl_date,tv_lbl_status;
    RelativeLayout layout_empty;
    LinearLayout ll_attendance;

    String Str_StudentUrl = ApiConstants.STUDENT_LIST;
    String Str_Attendance_Url = ApiConstants.ATTENDANCE_URL;


    public Fragment_Attendance_Staff()
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
            getProfile();
            getClassList();
            getSectionList();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_attendance_staff, container, false);
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
            tv_lbl_class = (TextView) view.findViewById(R.id.tv_lbl_class);
            tv_lbl_section = (TextView) view.findViewById(R.id.tv_lbl_section);
            tv_lbl_student = (TextView) view.findViewById(R.id.tv_lbl_student);
            tv_lbl_month = (TextView) view.findViewById(R.id.tv_lbl_month);
            tv_lbl_date = (TextView)view.findViewById(R.id.tv_lbl_date);
            tv_lbl_status = (TextView)view.findViewById(R.id.tv_lbl_status);
            tv_working_days = (TextView)view.findViewById(R.id.tv_working_days);
            tv_present_days = (TextView)view.findViewById(R.id.tv_present_days);
            tv_percentage = (TextView)view.findViewById(R.id.tv_percentage);
            layout_empty = (RelativeLayout) view.findViewById(R.id.layout_empty);
            text_view_empty = (TextView) view.findViewById(R.id.text_view_empty);
            ll_attendance = (LinearLayout) view.findViewById(R.id.ll_attendance_details);
            spinner_class = (Spinner) view.findViewById(R.id.spinner_class);
            spinner_section = (Spinner) view.findViewById(R.id.spinner_section);
            spinner_student = (Spinner) view.findViewById(R.id.spinner_student);
            spinner_month = (Spinner) view.findViewById(R.id.spinner_month);
            recycler_view = (RecycleEmptyErrorView) view.findViewById(R.id.recycler_view_attendance);
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
            if(mListClass.size()>0) showClassList();
            if(mListSection.size()>0) showSectionList();
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
            tv_lbl_class.setTypeface(font.getHelveticaRegular());
            tv_lbl_section.setTypeface(font.getHelveticaRegular());
            tv_lbl_student.setTypeface(font.getHelveticaRegular());
            tv_lbl_month.setTypeface(font.getHelveticaRegular());
            text_view_empty.setTypeface(font.getHelveticaRegular());
            tv_working_days.setTypeface(font.getHelveticaRegular());
            tv_present_days.setTypeface(font.getHelveticaRegular());
            tv_percentage.setTypeface(font.getHelveticaRegular());
            tv_lbl_date.setTypeface(font.getHelveticaBold());
            tv_lbl_status.setTypeface(font.getHelveticaBold());
            mLayoutManager = new LinearLayoutManager(getActivity());
            recycler_view.setLayoutManager(mLayoutManager);
            String[] array_months = getResources().getStringArray(R.array.array_months);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity,android.R.layout.simple_spinner_item,array_months);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_class.setOnItemSelectedListener(_OnClassItemSelectedListener);
            spinner_section.setOnItemSelectedListener(_OnSectionItemSelectedListener);
            spinner_student.setOnItemSelectedListener(_OnStudentItemSelectedListener);
            spinner_month.setOnItemSelectedListener(_OnMonthItemSelectedListener);
            spinner_month.setAdapter(adapter);
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

    public void getAttendanceFromService()
    {
        TAG = "getAttendanceFromService";
        Log.d(MODULE,TAG);
        try
        {
            mSelectedStudent=mListStudents.get(mSelectedPosition-1);
            if(mSelectedMonthPosition==0)
            {
                text_view_empty.setText(getString(R.string.lbl_select_month));
                showEmptyView();
            }
            else
            {
                AppUtils.showProgressDialog(mActivity);
                new GetAttendance(Str_Attendance_Url,Payload_Attendance(),Fragment_Attendance_Staff.this).getAttendance();
                text_view_empty.setText(getString(R.string.lbl_no_attendance) + " " + mMonth_value);
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

    }

    AdapterView.OnItemSelectedListener _OnClassItemSelectedListener =  new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id)
        {
            TAG = "onItemSelected";
            Log.d(MODULE, TAG);
            try
            {
                if (position > 0) Str_ClassId = mListClass.get(position - 1).getID();
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

    AdapterView.OnItemSelectedListener _OnSectionItemSelectedListener =  new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id)
        {
            TAG = "_OnSectionItem";
            Log.d(MODULE, TAG);
            try
            {
                if(position>0)
                {
                    Log.d(MODULE, TAG + " Spinner Section : " + position);
                    Str_SectionId=mListSection.get(position-1).getID();
                    new GetStudentList(Str_StudentUrl,PayloadStudent(),Fragment_Attendance_Staff.this).getStudents();
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

    AdapterView.OnItemSelectedListener _OnStudentItemSelectedListener =  new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id)
        {
            TAG = "_OnStudentItem";
            Log.d(MODULE, TAG);

            try
            {
                if(position>0)
                {
                    Log.d(MODULE, TAG + " Spinner Student : " + position);
                    Str_StudentId=mListStudents.get(position-1).getStudentId();
                    mSelectedPosition=position;
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

    AdapterView.OnItemSelectedListener _OnMonthItemSelectedListener =  new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id)
        {
            TAG = "_OnMonthItem";
            Log.d(MODULE, TAG);

            if(position>0)
            {
                Log.d(MODULE, TAG + " Spinner Section : " + position);
                mMonth=Integer.toString(position);
                mSelectedMonthPosition=position;
                mMonth_value= spinner_month.getSelectedItem().toString();
                getAttendanceFromService();
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> adapterView)
        {

        }
    };

    @Override
    public void onClassListReceived() {
        TAG = "onClassListReceived";
        Log.d(MODULE, TAG);
        try
        {
            getClassList();
            showClassList();
        }
        catch (Exception ex)
        {

        }
    }

    @Override
    public void onClassListReceivedError(String Str_Msg) {

    }

    @Override
    public void onSectionListReceived() {
        TAG = "onSectionListReceived";
        Log.d(MODULE, TAG);
        try
        {
            getSectionList();
            showSectionList();
        }
        catch (Exception ex)
        {

        }
    }

    @Override
    public void onSectionListReceivedError(String Str_Msg) {

    }

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

    @Override
    public void onStudentListItemClicked(int position) {

    }

    @Override
    public void onAttendanceReceived()
    {
        TAG = "onAttendanceReceived";
        Log.d(MODULE, TAG);

        try
        {
            getAttendanceDetails();
            showAttendanceDetails();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

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

    public void getClassList()
    {
        TAG = "getClassList";
        Log.d(MODULE, TAG);
        try
        {
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            String Str_Json = mPreferences.getString(AppUtils.SHARED_CLASS_LIST, "");
            Log.d(MODULE, TAG + " Str_Json : " + Str_Json);
            if (Str_Json.length() > 0)
            {
                responseCommon = (CommonList_Response) AppUtils.fromJson(Str_Json, new TypeToken<CommonList_Response>() {}.getType());
                mListClass = responseCommon.getCclass();
                Log.d(MODULE, TAG + " mListClass : " + mListClass.size());
            }
            else
            {
                new ClassList_Process(mActivity, this).GetClassList();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void getSectionList()
    {
        TAG = "getSectionList";
        Log.d(MODULE, TAG);
        try
        {
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            String Str_Json = mPreferences.getString(AppUtils.SHARED_SECTION_LIST, "");
            Log.d(MODULE, TAG + " Str_Json : " + Str_Json);
            if (Str_Json.length() > 0)
            {
                responseCommon = (CommonList_Response) AppUtils.fromJson(Str_Json, new TypeToken<CommonList_Response>() {}.getType());
                mListSection = responseCommon.getCclass();
                Log.d(MODULE, TAG + " mListSection : " + mListSection.size());
            }
            else
            {
                new SectionList_Process(mActivity, this).GetSectionList();
            }
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
                response = (StudentsList_Response) AppUtils.fromJson(Str_Json, new TypeToken<StudentsList_Response>(){}.getType());
                mListStudents = response.getCstudents();
                Log.d(MODULE, TAG + " mListStudents : " + mListStudents.size());
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
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

    public void showClassList()
    {
        TAG = "showClassList";
        Log.d(MODULE, TAG);
        try
        {
            String[] items = AppUtils.getArray(mListClass,getString(R.string.lbl_select_class));
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity,android.R.layout.simple_spinner_item,items);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_class.setAdapter(adapter);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void showSectionList()
    {
        TAG = "showSectionList";
        Log.d(MODULE, TAG);
        try
        {
            String[] items = AppUtils.getArray(mListSection,getString(R.string.lbl_select_section));
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity,android.R.layout.simple_spinner_item,items);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_section.setAdapter(adapter);
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
            String[] items = AppUtils.getStudentArray(mListStudents,getString(R.string.lbl_select_student));
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity,android.R.layout.simple_spinner_item,items);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_student.setAdapter(adapter);
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
            if(mAttendanceResult.size()>0)
            {
                tv_working_days.setText( getString(R.string.lbl_working_days) + " : " + mWorkingDays);
                tv_present_days.setText(getString(R.string.lbl_present_days) + " : " +mPresentDays);
                tv_percentage.setText(mPercentage);
                ll_attendance.setVisibility(View.VISIBLE);
                layout_empty.setVisibility(View.GONE);
                AttendanceAdapter adapter = new AttendanceAdapter(mAttendanceResult,this);
                if(recycler_view!=null)recycler_view.setAdapter(adapter);
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

    public JSONObject PayloadStudent()
    {
        TAG = "Payload";
        Log.d(MODULE, TAG);

        JSONObject obj = new JSONObject();
        try {
            obj.put("ParentId", "");
            obj.put("ClassId", Str_ClassId);
            obj.put("SectionId", Str_SectionId);
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
            obj.put("ClassId",Str_ClassId);
            obj.put("SectionId", Str_SectionId);
            obj.put("StudentId", Str_StudentId);
        }
        catch (JSONException ex)
        {
            ex.printStackTrace();
        }
        Log.d(MODULE, TAG + " obj : " + obj.toString());
        return obj;
    }

}
