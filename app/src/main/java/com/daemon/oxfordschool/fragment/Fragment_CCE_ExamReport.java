package com.daemon.oxfordschool.fragment;

/**
 * Created by Ravi on 29/07/15.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.Utils.Font;
import com.daemon.oxfordschool.activity.MainActivity;
import com.daemon.oxfordschool.adapter.StudentPagerAdapter;
import com.daemon.oxfordschool.asyncprocess.GetStudentList;
import com.daemon.oxfordschool.classes.User;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.listeners.StudentsListListener;
import com.daemon.oxfordschool.response.CCE_ExamReport_Response;
import com.daemon.oxfordschool.response.StudentsList_Response;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Fragment_CCE_ExamReport extends Fragment implements StudentsListListener
{

    public static String MODULE = "Fragment_CCE_ExamReport";
    public static String TAG = "";

    int mSelectedPosition;
    SharedPreferences mPreferences;
    User mUser,mSelectedUser;
    ViewPager vp_student;
    FrameLayout frame_layout_cce_report;
    FragmentManager mManager;
    Toolbar mToolbar;

    ArrayList<User> mListStudents =new ArrayList<User>();

    StudentsList_Response studentListResponse;
    CCE_ExamReport_Response response;

    AppCompatActivity mActivity;
    String Str_Id="",Str_StudentId="";
    private Font font= MyApplication.getInstance().getFontInstance();
    String Str_StudentList_Url = ApiConstants.STUDENT_LIST;


    public Fragment_CCE_ExamReport()
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
            setHasOptionsMenu(true);
            mManager = getChildFragmentManager();
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
        View rootView = inflater.inflate(R.layout.fragment_cce_exam_report, container, false);
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
            frame_layout_cce_report = (FrameLayout) view.findViewById(R.id.frame_layout_cce_report);
            setProperties();
            SetActionBar();
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
            if(mListStudents.size()>0)
            {
                showStudentsList();
                Goto_Fragment_CCE_Report_List();
            }

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void SetActionBar()
    {
        TAG = "SetActionBar";
        Log.d(MODULE, TAG);
        try
        {
            if (mActivity != null)
            {
                mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
                mActivity.setSupportActionBar(mToolbar);
                mToolbar.setTitle(R.string.lbl_reports);
                mToolbar.setSubtitle("");
                if(!MainActivity.mTwoPane)
                {
                    FragmentDrawer.mDrawerLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            FragmentDrawer.mDrawerToggle.syncState();
                        }
                    });
                    mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
                else mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
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
                else  getCCEExamReportFromService();
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

    ViewPager.OnPageChangeListener _OnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mSelectedPosition=position;
            mSelectedUser = mListStudents.get(mSelectedPosition);
            Goto_Fragment_CCE_Report_List();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public void getCCEExamReportFromService()
    {
        TAG = "getCCEExamReportFromService";
        Log.d(MODULE, TAG);
        try
        {
            mSelectedUser = mListStudents.get(mSelectedPosition);
            Str_StudentId = mSelectedUser.getStudentId();
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
                showStudentsList();
                mSelectedPosition=0;
                getCCEExamReportFromService();
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

    public void Goto_Fragment_CCE_Report_List()
    {
        TAG = " Goto_Fragment_CCE_Report_List";
        Log.d(MODULE, TAG);

        try
        {
            FragmentTransaction mTransaction = mManager.beginTransaction();
            Bundle Args = new Bundle();
            Args.putParcelable(AppUtils.B_SELECTED_USER,mSelectedUser);
            Fragment_CCE_ExamReport_List mFragment = new Fragment_CCE_ExamReport_List();
            mFragment.setArguments(Args);
            mTransaction.replace(R.id.frame_layout_cce_report, mFragment, AppUtils.FRAGMENT_CCE_REPORT_LIST + "");
            mTransaction.addToBackStack(AppUtils.FRAGMENT_CCE_REPORT_LIST + "");
            mTransaction.commit();
        }
        catch (Exception e)
        {
            Log.e(MODULE, TAG + ", Exception Occurs " + e);
        }
    }

    public void Goto_Fragment_CCE_Report_Chart()
    {
        TAG = "Goto_Fragment_CCE_Report_Chart";
        Log.d(MODULE, TAG);

        try
        {
            FragmentTransaction mTransaction = mManager.beginTransaction();
            Bundle Args = new Bundle();
            Args.putParcelable(AppUtils.B_SELECTED_USER,mSelectedUser);
            Fragment_CCE_ExamReport_Chart mFragment = new Fragment_CCE_ExamReport_Chart();
            mFragment.setArguments(Args);
            mTransaction.replace(R.id.frame_layout_cce_report, mFragment, AppUtils.FRAGMENT_CCE_REPORT_CHART + "");
            mTransaction.addToBackStack(AppUtils.FRAGMENT_CCE_REPORT_CHART + "");
            mTransaction.commit();
        }
        catch (Exception e)
        {
            Log.e(MODULE, TAG + ", Exception Occurs " + e);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_settings).setVisible(false);
        menu.findItem(R.id.action_list_view).setVisible(true);
        menu.findItem(R.id.action_chart_view).setVisible(true);
        menu.findItem(R.id.action_help).setVisible(false);
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
            case R.id.action_list_view:
                 Goto_Fragment_CCE_Report_List();
                 break;
            case R.id.action_chart_view:
                 Goto_Fragment_CCE_Report_Chart();
                 break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

}
