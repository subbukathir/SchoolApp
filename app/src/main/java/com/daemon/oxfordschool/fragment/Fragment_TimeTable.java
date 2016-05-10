package com.daemon.oxfordschool.fragment;

/**
 * Created by Ravi on 29/07/15.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.Utils.Font;
import com.daemon.oxfordschool.adapter.StudentPagerAdapter;
import com.daemon.oxfordschool.adapter.TimeTableAdapter;
import com.daemon.oxfordschool.asyncprocess.AllSubjectList_Process;
import com.daemon.oxfordschool.asyncprocess.GetFeesTermList;
import com.daemon.oxfordschool.asyncprocess.GetStudentList;
import com.daemon.oxfordschool.asyncprocess.GetTimeTable;
import com.daemon.oxfordschool.classes.Common_Class;
import com.daemon.oxfordschool.classes.TimeTable;
import com.daemon.oxfordschool.classes.TimeTableItem;
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
    CoordinatorLayout cl_main;

    public static String MODULE = "Fragment_TimeTable";
    public static String TAG = "";

    public static int HEADER=0;
    public static int NON_HEADER=1;

    int mSelectedPosition;

    SharedPreferences mPreferences;
    User mUser,mSelectedUser;
    ViewPager vp_student;
    TextView text_view_empty;

    ArrayList<Common_Class> mSubjectList;
    ArrayList<User> mListStudents =new ArrayList<User>();
    ArrayList<TimeTableItem> mListSubjectId = new ArrayList<TimeTableItem>();
    TimeTable mTimeTable;
    GridView grid_view_table;
    RelativeLayout layout_empty;

    StudentsList_Response studentListResponse;
    CommonList_Response response;

    AppCompatActivity mActivity;
    String Str_Id="",Str_ClassId="",Str_SectionId="";
    private Font font= MyApplication.getInstance().getFontInstance();
    String Str_StudentList_Url = ApiConstants.STUDENT_LIST;
    String Str_TimeTable_Url = ApiConstants.TIME_TABLE_URL;

    TimeTableAdapter adapter;

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
            getSubjectsListFromService();
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
            cl_main = (CoordinatorLayout) mActivity.findViewById(R.id.cl_main);
            vp_student = (ViewPager) view.findViewById(R.id.vp_student);
            grid_view_table=(GridView) view.findViewById(R.id.grid_view_table);
            text_view_empty = (TextView) view.findViewById(R.id.text_view_empty);
            layout_empty = (RelativeLayout) view.findViewById(R.id.layout_empty);
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
        try {
            text_view_empty.setTypeface(font.getHelveticaRegular());
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

    public void getSubjectsListFromService()
    {
        TAG = "getSubjectsListFromService";
        Log.d(MODULE, TAG);
        try
        {
            mSelectedUser = mListStudents.get(mSelectedPosition);
            Str_ClassId = mSelectedUser.getClassId();
            Str_SectionId = mSelectedUser.getSectionId();
            new AllSubjectList_Process(this).GetSubjectsList();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

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
            showSnackBar(Str_Msg,1);
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
            vp_student.addOnPageChangeListener(_OnPageChangeListener);
            vp_student.setAdapter(adapter);
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
            if(mSubjectList.size()>0) getTimeTableFromService();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public void getTimeTable()
    {
        TAG = "getTimeTable";
        Log.d(MODULE, TAG);
        try
        {
            mListSubjectId.clear();
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            String Str_Json = mPreferences.getString(AppUtils.SHARED_TIMETABLE,"");
            Log.d(MODULE, TAG + " Str_Json : " + Str_Json);
            if(Str_Json.length()>0)
            {
                mTimeTable = (TimeTable) AppUtils.fromJson(Str_Json, new TypeToken<TimeTable>(){}.getType());
                Log.d(MODULE, TAG + " mTimeTable : " + mTimeTable.getDay1Hour1());

                TimeTableItem item = new TimeTableItem();item.setName(" ");item.setType(NON_HEADER);
                TimeTableItem itemDay1 = new TimeTableItem();itemDay1.setName(getString(R.string.lbl_monday));itemDay1.setType(HEADER);
                TimeTableItem itemDay2 = new TimeTableItem();itemDay2.setName(getString(R.string.lbl_tuesday));itemDay2.setType(HEADER);
                TimeTableItem itemDay3 = new TimeTableItem();itemDay3.setName(getString(R.string.lbl_wednesday));itemDay3.setType(HEADER);
                TimeTableItem itemDay4 = new TimeTableItem();itemDay4.setName(getString(R.string.lbl_thursday));itemDay4.setType(HEADER);
                TimeTableItem itemDay5 = new TimeTableItem();itemDay5.setName(getString(R.string.lbl_friday));itemDay5.setType(HEADER);

                TimeTableItem itemDay0Hour0 = new TimeTableItem();itemDay0Hour0.setName(getString(R.string.lbl_1hour));itemDay0Hour0.setType(HEADER);
                TimeTableItem itemDay1Hour1 = new TimeTableItem();itemDay1Hour1.setName(mTimeTable.getDay1Hour1());itemDay1Hour1.setType(NON_HEADER);
                TimeTableItem itemDay2Hour1 = new TimeTableItem();itemDay2Hour1.setName(mTimeTable.getDay2Hour1());itemDay2Hour1.setType(NON_HEADER);
                TimeTableItem itemDay3Hour1 = new TimeTableItem();itemDay3Hour1.setName(mTimeTable.getDay3Hour1());itemDay3Hour1.setType(NON_HEADER);
                TimeTableItem itemDay4Hour1 = new TimeTableItem();itemDay4Hour1.setName(mTimeTable.getDay4Hour1());itemDay4Hour1.setType(NON_HEADER);
                TimeTableItem itemDay5Hour1 = new TimeTableItem();itemDay5Hour1.setName(mTimeTable.getDay5Hour1());itemDay5Hour1.setType(NON_HEADER);

                TimeTableItem itemDay0Hour1 = new TimeTableItem();itemDay0Hour1.setName(getString(R.string.lbl_2hour));itemDay0Hour1.setType(HEADER);
                TimeTableItem itemDay1Hour2 = new TimeTableItem();itemDay1Hour2.setName(mTimeTable.getDay1Hour2());itemDay1Hour2.setType(NON_HEADER);
                TimeTableItem itemDay2Hour2 = new TimeTableItem();itemDay2Hour2.setName(mTimeTable.getDay2Hour2());itemDay2Hour2.setType(NON_HEADER);
                TimeTableItem itemDay3Hour2 = new TimeTableItem();itemDay3Hour2.setName(mTimeTable.getDay3Hour2());itemDay3Hour2.setType(NON_HEADER);
                TimeTableItem itemDay4Hour2 = new TimeTableItem();itemDay4Hour2.setName(mTimeTable.getDay4Hour2());itemDay4Hour2.setType(NON_HEADER);
                TimeTableItem itemDay5Hour2 = new TimeTableItem();itemDay5Hour2.setName(mTimeTable.getDay5Hour2());itemDay5Hour2.setType(NON_HEADER);

                TimeTableItem itemDay0Hour2 = new TimeTableItem();itemDay0Hour2.setName(getString(R.string.lbl_3hour));itemDay0Hour2.setType(HEADER);
                TimeTableItem itemDay1Hour3 = new TimeTableItem();itemDay1Hour3.setName(mTimeTable.getDay1Hour3());itemDay1Hour3.setType(NON_HEADER);
                TimeTableItem itemDay2Hour3 = new TimeTableItem();itemDay2Hour3.setName(mTimeTable.getDay2Hour3());itemDay2Hour3.setType(NON_HEADER);
                TimeTableItem itemDay3Hour3 = new TimeTableItem();itemDay3Hour3.setName(mTimeTable.getDay3Hour3());itemDay3Hour3.setType(NON_HEADER);
                TimeTableItem itemDay4Hour3 = new TimeTableItem();itemDay4Hour3.setName(mTimeTable.getDay4Hour3());itemDay4Hour3.setType(NON_HEADER);
                TimeTableItem itemDay5Hour3 = new TimeTableItem();itemDay5Hour3.setName(mTimeTable.getDay5Hour3());itemDay5Hour3.setType(NON_HEADER);

                TimeTableItem itemDay0Hour3 = new TimeTableItem();itemDay0Hour3.setName(getString(R.string.lbl_4hour));itemDay0Hour3.setType(HEADER);
                TimeTableItem itemDay1Hour4 = new TimeTableItem();itemDay1Hour4.setName(mTimeTable.getDay1Hour4());itemDay1Hour4.setType(NON_HEADER);
                TimeTableItem itemDay2Hour4 = new TimeTableItem();itemDay2Hour4.setName(mTimeTable.getDay2Hour4());itemDay2Hour4.setType(NON_HEADER);
                TimeTableItem itemDay3Hour4 = new TimeTableItem();itemDay3Hour4.setName(mTimeTable.getDay3Hour4());itemDay3Hour4.setType(NON_HEADER);
                TimeTableItem itemDay4Hour4 = new TimeTableItem();itemDay4Hour4.setName(mTimeTable.getDay4Hour4());itemDay4Hour4.setType(NON_HEADER);
                TimeTableItem itemDay5Hour4 = new TimeTableItem();itemDay5Hour4.setName(mTimeTable.getDay5Hour4());itemDay5Hour4.setType(NON_HEADER);

                TimeTableItem itemDay0Hour4 = new TimeTableItem();itemDay0Hour4.setName(getString(R.string.lbl_5hour));itemDay0Hour4.setType(HEADER);
                TimeTableItem itemDay1Hour5 = new TimeTableItem();itemDay1Hour5.setName(mTimeTable.getDay1Hour5());itemDay1Hour5.setType(NON_HEADER);
                TimeTableItem itemDay2Hour5 = new TimeTableItem();itemDay2Hour5.setName(mTimeTable.getDay2Hour5());itemDay2Hour5.setType(NON_HEADER);
                TimeTableItem itemDay3Hour5 = new TimeTableItem();itemDay3Hour5.setName(mTimeTable.getDay3Hour5());itemDay3Hour5.setType(NON_HEADER);
                TimeTableItem itemDay4Hour5 = new TimeTableItem();itemDay4Hour5.setName(mTimeTable.getDay4Hour5());itemDay4Hour5.setType(NON_HEADER);
                TimeTableItem itemDay5Hour5 = new TimeTableItem();itemDay5Hour5.setName(mTimeTable.getDay5Hour5());itemDay5Hour5.setType(NON_HEADER);

                TimeTableItem itemDay0Hour5 = new TimeTableItem();itemDay0Hour5.setName(getString(R.string.lbl_6hour));itemDay0Hour5.setType(HEADER);
                TimeTableItem itemDay1Hour6 = new TimeTableItem();itemDay1Hour6.setName(mTimeTable.getDay1Hour6());itemDay1Hour6.setType(NON_HEADER);
                TimeTableItem itemDay2Hour6 = new TimeTableItem();itemDay2Hour6.setName(mTimeTable.getDay2Hour6());itemDay2Hour6.setType(NON_HEADER);
                TimeTableItem itemDay3Hour6 = new TimeTableItem();itemDay3Hour6.setName(mTimeTable.getDay3Hour6());itemDay3Hour6.setType(NON_HEADER);
                TimeTableItem itemDay4Hour6 = new TimeTableItem();itemDay4Hour6.setName(mTimeTable.getDay4Hour6());itemDay4Hour6.setType(NON_HEADER);
                TimeTableItem itemDay5Hour6 = new TimeTableItem();itemDay5Hour6.setName(mTimeTable.getDay5Hour6());itemDay5Hour6.setType(NON_HEADER);

                TimeTableItem itemDay0Hour6 = new TimeTableItem();itemDay0Hour6.setName(getString(R.string.lbl_7hour));itemDay0Hour6.setType(HEADER);
                TimeTableItem itemDay1Hour7 = new TimeTableItem();itemDay1Hour7.setName(mTimeTable.getDay1Hour7());itemDay1Hour7.setType(NON_HEADER);
                TimeTableItem itemDay2Hour7 = new TimeTableItem();itemDay2Hour7.setName(mTimeTable.getDay2Hour7());itemDay2Hour7.setType(NON_HEADER);
                TimeTableItem itemDay3Hour7 = new TimeTableItem();itemDay3Hour7.setName(mTimeTable.getDay3Hour7());itemDay3Hour7.setType(NON_HEADER);
                TimeTableItem itemDay4Hour7 = new TimeTableItem();itemDay4Hour7.setName(mTimeTable.getDay4Hour7());itemDay4Hour7.setType(NON_HEADER);
                TimeTableItem itemDay5Hour7 = new TimeTableItem();itemDay5Hour7.setName(mTimeTable.getDay5Hour7());itemDay5Hour7.setType(NON_HEADER);

                TimeTableItem itemDay0Hour7 = new TimeTableItem();itemDay0Hour7.setName(getString(R.string.lbl_8hour));itemDay0Hour7.setType(HEADER);
                TimeTableItem itemDay1Hour8 = new TimeTableItem();itemDay1Hour8.setName(mTimeTable.getDay1Hour8());itemDay1Hour8.setType(NON_HEADER);
                TimeTableItem itemDay2Hour8 = new TimeTableItem();itemDay2Hour8.setName(mTimeTable.getDay2Hour8());itemDay2Hour8.setType(NON_HEADER);
                TimeTableItem itemDay3Hour8 = new TimeTableItem();itemDay3Hour8.setName(mTimeTable.getDay3Hour8());itemDay3Hour8.setType(NON_HEADER);
                TimeTableItem itemDay4Hour8 = new TimeTableItem();itemDay4Hour8.setName(mTimeTable.getDay4Hour8());itemDay4Hour8.setType(NON_HEADER);
                TimeTableItem itemDay5Hour8 = new TimeTableItem();itemDay5Hour8.setName(mTimeTable.getDay5Hour8());itemDay5Hour8.setType(NON_HEADER);

                mListSubjectId.add(item);mListSubjectId.add(itemDay1);mListSubjectId.add(itemDay2);mListSubjectId.add(itemDay3);
                mListSubjectId.add(itemDay4);mListSubjectId.add(itemDay5);

                mListSubjectId.add(itemDay0Hour0);mListSubjectId.add(itemDay1Hour1);mListSubjectId.add(itemDay2Hour1);
                mListSubjectId.add(itemDay3Hour1);mListSubjectId.add(itemDay4Hour1);mListSubjectId.add(itemDay5Hour1);

                mListSubjectId.add(itemDay0Hour1);mListSubjectId.add(itemDay1Hour2);mListSubjectId.add(itemDay2Hour2);
                mListSubjectId.add(itemDay3Hour2);mListSubjectId.add(itemDay4Hour2);mListSubjectId.add(itemDay5Hour2);

                mListSubjectId.add(itemDay0Hour2);mListSubjectId.add(itemDay1Hour3);mListSubjectId.add(itemDay2Hour3);
                mListSubjectId.add(itemDay3Hour3);mListSubjectId.add(itemDay4Hour3);mListSubjectId.add(itemDay5Hour3);

                mListSubjectId.add(itemDay0Hour3);mListSubjectId.add(itemDay1Hour4);mListSubjectId.add(itemDay2Hour4);
                mListSubjectId.add(itemDay3Hour4);mListSubjectId.add(itemDay4Hour4);mListSubjectId.add(itemDay5Hour4);

                mListSubjectId.add(itemDay0Hour4);mListSubjectId.add(itemDay1Hour5);mListSubjectId.add(itemDay2Hour5);
                mListSubjectId.add(itemDay3Hour5);mListSubjectId.add(itemDay4Hour5);mListSubjectId.add(itemDay5Hour5);

                mListSubjectId.add(itemDay0Hour5);mListSubjectId.add(itemDay1Hour6);mListSubjectId.add(itemDay2Hour6);
                mListSubjectId.add(itemDay3Hour6);mListSubjectId.add(itemDay4Hour6);mListSubjectId.add(itemDay5Hour6);

                mListSubjectId.add(itemDay0Hour6);mListSubjectId.add(itemDay1Hour7);mListSubjectId.add(itemDay2Hour7);
                mListSubjectId.add(itemDay3Hour7);mListSubjectId.add(itemDay4Hour7);mListSubjectId.add(itemDay5Hour7);

                mListSubjectId.add(itemDay0Hour7);mListSubjectId.add(itemDay1Hour8);mListSubjectId.add(itemDay2Hour8);
                mListSubjectId.add(itemDay3Hour8);mListSubjectId.add(itemDay4Hour8);mListSubjectId.add(itemDay5Hour8);
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
            if(mListSubjectId.size()>0)
            {
                adapter = new TimeTableAdapter(mActivity,mListSubjectId,mSubjectList);
                grid_view_table.setAdapter(adapter);
                grid_view_table.setVisibility(View.VISIBLE);
                layout_empty.setVisibility(View.GONE);
            }
            else
            {
                text_view_empty.setText(R.string.lbl_timetable_not_found);
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
            grid_view_table.setVisibility(View.GONE);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public JSONObject PayloadSection()
    {
        TAG = "Payload";
        Log.d(MODULE, TAG);

        JSONObject obj = new JSONObject();
        try
        {
            obj.put("ClassId", Str_ClassId);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        Log.d(MODULE, TAG + " obj : " + obj.toString());

        return obj;
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
            getTimeTableFromService();
        }
        catch (Exception ex)
        {

        }
    }

    @Override
    public void onSubjectListReceivedError(String Str_Msg)
    {
        showSnackBar(Str_Msg,0);
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

    public void showSnackBar(String Str_Msg,final int mService)
    {
        Snackbar snackbar = Snackbar.make(cl_main, Str_Msg, Snackbar.LENGTH_LONG);
        snackbar.setAction(getString(R.string.lbl_retry), new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(mService==0) getSubjectsListFromService();
                else if(mService==1) getTimeTableFromService();
            }
        });
        // Changing message text color
        snackbar.setActionTextColor(Color.RED);
        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        textView.setTypeface(font.getHelveticaRegular());
        snackbar.show();
    }

}
