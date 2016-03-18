package com.daemon.oxfordschool.fragment;

/**
 * Created by Ravi on 29/07/15.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.Utils.Font;
import com.daemon.oxfordschool.adapter.StudentPagerAdapter;
import com.daemon.oxfordschool.adapter.TimeTablePagerAdapter;
import com.daemon.oxfordschool.asyncprocess.GetStudentList;
import com.daemon.oxfordschool.asyncprocess.GetTimeTable;
import com.daemon.oxfordschool.asyncprocess.SubjectList_Process;
import com.daemon.oxfordschool.classes.Common_Class;
import com.daemon.oxfordschool.classes.TimeTable;
import com.daemon.oxfordschool.classes.User;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.listeners.StudentsListListener;
import com.daemon.oxfordschool.listeners.SubjectListListener;
import com.daemon.oxfordschool.listeners.TimeTableListener;
import com.daemon.oxfordschool.response.CommonList_Response;
import com.daemon.oxfordschool.response.StudentsList_Response;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Fragment_TimeTable extends Fragment implements StudentsListListener,TimeTableListener,SubjectListListener
{

    public static String MODULE = "Fragment_TimeTable";
    public static String TAG = "";

    int mSelectedPosition;

    SharedPreferences mPreferences;
    User mUser,mSelectedUser;
    ViewPager vp_student,vp_time_table;

    ArrayList<Common_Class> mSubjectList;
    ArrayList<User> mListStudents =new ArrayList<User>();
    TimeTable mTimeTable;

    StudentsList_Response studentListResponse;
    CommonList_Response response;

    AppCompatActivity mActivity;
    String Str_Id="",Str_ClassId="",Str_SectionId="";
    private Font font= MyApplication.getInstance().getFontInstance();
    String Str_StudentList_Url = ApiConstants.STUDENT_LIST;
    String Str_TimeTable_Url = ApiConstants.TIME_TABLE_URL;

    String[] Day1 = new String[8];
    String[] Day2 = new String[8];
    String[] Day3 = new String[8];
    String[] Day4 = new String[8];
    String[] Day5 = new String[8];

    Object[] ObjTbl = new Object[5];

    TimeTablePagerAdapter adapter;

    public Fragment_TimeTable()
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
            getProfile();
            getStudentsList();
            getSubjects();
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
        View rootView = inflater.inflate(R.layout.fragment_timetable, container, false);
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
            vp_time_table=(ViewPager) view.findViewById(R.id.vp_time_table);
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
            if(mListStudents.size()>0) showStudentsList();
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
            vp_student.addOnPageChangeListener(_OnPageChangeListener);
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
                studentListResponse = (StudentsList_Response) AppUtils.fromJson(Str_Json, new TypeToken<StudentsList_Response>(){}.getType());
                mListStudents = studentListResponse.getCstudents();
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

    ViewPager.OnPageChangeListener _OnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mSelectedPosition=position;
            getTimeTableFromService();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public void getTimeTableFromService()
    {
        TAG = "getTimeTableFromService";
        Log.d(MODULE, TAG);
        try
        {
            mSelectedUser = mListStudents.get(mSelectedPosition);
            Str_ClassId = mSelectedUser.getClassId();
            Str_SectionId = mSelectedUser.getSectionId();
            new GetTimeTable(Str_TimeTable_Url,Payload_TimeTable(),this).getTimeTable();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
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
                mSelectedPosition=0;
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
    public void onTimeTableReceived() {
        TAG = "onTimeTableReceived";
        Log.d(MODULE, TAG);
        try
        {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getTimeTable();
                    showTimeTable();
                }
            });

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onTimeTableReceivedError(String Str_Msg) {
        TAG = "onTimeTableReceivedError";
        Log.d(MODULE, TAG + Str_Msg);
        try
        {

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
            getTimeTableFromService();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void getTimeTable()
    {
        TAG = "getTimeTable";
        Log.d(MODULE, TAG);
        try
        {
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            String Str_Json = mPreferences.getString(AppUtils.SHARED_TIMETABLE,"");
            Log.d(MODULE, TAG + " Str_Json : " + Str_Json);
            if(Str_Json.length()>0)
            {
                mTimeTable = (TimeTable) AppUtils.fromJson(Str_Json, new TypeToken<TimeTable>(){}.getType());
                Log.d(MODULE, TAG + " mTimeTable : " + mTimeTable.getDay1Hour1());
                Day1[0] = mTimeTable.getDay1Hour1();Day1[1] = mTimeTable.getDay1Hour2();Day1[2] = mTimeTable.getDay1Hour3();
                Day1[3] = mTimeTable.getDay1Hour4();Day1[4] = mTimeTable.getDay1Hour5();Day1[5] = mTimeTable.getDay1Hour6();
                Day1[6] = mTimeTable.getDay1Hour7();Day1[7] = mTimeTable.getDay1Hour8();

                Day2[0] = mTimeTable.getDay2Hour1();Day2[1] = mTimeTable.getDay2Hour2();Day2[2] = mTimeTable.getDay2Hour3();
                Day2[3] = mTimeTable.getDay2Hour4();Day2[4] = mTimeTable.getDay2Hour5();Day2[5] = mTimeTable.getDay2Hour6();
                Day2[6] = mTimeTable.getDay2Hour7();Day2[7] = mTimeTable.getDay2Hour8();

                Day3[0] = mTimeTable.getDay3Hour1();Day3[1] = mTimeTable.getDay3Hour2();Day3[2] = mTimeTable.getDay3Hour3();
                Day3[3] = mTimeTable.getDay3Hour4();Day3[4] = mTimeTable.getDay3Hour5();Day3[5] = mTimeTable.getDay3Hour6();
                Day3[6] = mTimeTable.getDay3Hour7();Day3[7] = mTimeTable.getDay3Hour8();

                Day4[0] = mTimeTable.getDay4Hour1();Day4[1] = mTimeTable.getDay4Hour2();Day4[2] = mTimeTable.getDay4Hour3();
                Day4[3] = mTimeTable.getDay4Hour4();Day4[4] = mTimeTable.getDay4Hour5();Day4[5] = mTimeTable.getDay4Hour6();
                Day4[6] = mTimeTable.getDay4Hour7();Day4[7] = mTimeTable.getDay4Hour8();

                Day5[0] = mTimeTable.getDay5Hour1();Day5[1] = mTimeTable.getDay5Hour2();Day5[2] = mTimeTable.getDay5Hour3();
                Day5[3] = mTimeTable.getDay5Hour4();Day5[4] = mTimeTable.getDay5Hour5();Day5[5] = mTimeTable.getDay5Hour6();
                Day5[6] = mTimeTable.getDay5Hour7();Day5[7] = mTimeTable.getDay5Hour8();

                ObjTbl[0]=Day1; ObjTbl[1]=Day2; ObjTbl[2]=Day3; ObjTbl[3]=Day4; ObjTbl[4]=Day5;

            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void showTimeTable()
    {
        TAG = "showTimeTable";
        Log.d(MODULE, TAG);
        try
        {
            adapter = new TimeTablePagerAdapter(mActivity,ObjTbl,mSubjectList);
            vp_time_table.setAdapter(adapter);

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public JSONObject Payload_StudentList()
    {
        TAG = "Payload_StudentList";
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

    public JSONObject Payload_TimeTable()
    {
        TAG = "Payload_TimeTable";
        Log.d(MODULE, TAG);

        JSONObject obj = new JSONObject();
        try {
            obj.put("ClassId", Str_ClassId);
            obj.put("SectionId", Str_SectionId);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(MODULE, TAG + " obj : " + obj.toString());

        return obj;
    }

    @Override
    public void onSubjectListReceived()
    {
        TAG = "onSubjectListReceived";
        Log.d(MODULE, TAG);
        try
        {
            getSubjects();
        }
        catch (Exception ex)
        {

        }
    }

    @Override
    public void onSubjectListReceivedError(String Str_Msg) {

    }

    public void getSubjects()
    {
        TAG = "getSubjects";
        Log.d(MODULE, TAG);
        try
        {
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            String Str_Json = mPreferences.getString(AppUtils.SHARED_SUBJECT_LIST, "");
            Log.d(MODULE, TAG + " Str_Json : " + Str_Json);
            if (Str_Json.length() > 0)
            {
                response = (CommonList_Response) AppUtils.fromJson(Str_Json, new TypeToken<CommonList_Response>() { }.getType());
                mSubjectList = response.getCclass();
                Log.d(MODULE, TAG + " mSubjectList : " + mSubjectList.size());
            }
            else
            {
                new SubjectList_Process(mActivity, this).GetSubjectList();
            }
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
}
