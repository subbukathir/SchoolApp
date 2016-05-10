package com.daemon.oxfordschool.fragment;

/**
 * Created by Ravi on 29/07/15.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.Utils.Font;
import com.daemon.oxfordschool.adapter.DiaryListAdapter;
import com.daemon.oxfordschool.adapter.StudentPagerAdapter;
import com.daemon.oxfordschool.asyncprocess.DisciplineReport_Process;
import com.daemon.oxfordschool.asyncprocess.GetDiaryNotesList;
import com.daemon.oxfordschool.asyncprocess.GetStudentList;
import com.daemon.oxfordschool.classes.CHomework;
import com.daemon.oxfordschool.classes.Discipline;
import com.daemon.oxfordschool.classes.User;
import com.daemon.oxfordschool.components.RecycleEmptyErrorView;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.listeners.DateSetListener;
import com.daemon.oxfordschool.listeners.DiaryNotesListListener;
import com.daemon.oxfordschool.listeners.Diary_List_Item_Click_Listener;
import com.daemon.oxfordschool.listeners.DisciplineReportListener;
import com.daemon.oxfordschool.listeners.StudentsListListener;
import com.daemon.oxfordschool.response.HomeWorkList_Response;
import com.daemon.oxfordschool.response.StudentsList_Response;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Fragment_Discipline_Report extends Fragment implements StudentsListListener,DisciplineReportListener
{

    public static String MODULE = "Fragment_Discipline_Report ";
    public static String TAG = "";

    CoordinatorLayout cl_main;
    TextView tv_lbl_self_control,tv_self_control,tv_lbl_obey_rules,tv_obey_rules,tv_lbl_obey_staff,tv_obey_staff,tv_lbl_dress_code,
     tv_dress_code,tv_lbl_time_keeping,tv_time_keeping,tv_lbl_conduct,tv_conduct,text_view_empty;
    LinearLayout ll_discipline_report;
    RelativeLayout layout_empty;
    SharedPreferences mPreferences;
    Toolbar mToolbar;
    User mUser,mSelectedUser;
    ViewPager vp_student;

    ArrayList<User> mListStudents =new ArrayList<User>();
    StudentsList_Response studentListresponse;
    Discipline mDiscipline;

    AppCompatActivity mActivity;
    Bundle mSavedInstanceState;
    String Str_Id="",Str_ClassId="",Str_SectionId="",Str_StudentId="";
    private Font font= MyApplication.getInstance().getFontInstance();
    String Str_StudentList_Url = ApiConstants.STUDENT_LIST;

    int mStudentListPosition=0;
    final static String ARG_STUDENT_LIST_POSITION = "Student_List_Position";

    public Fragment_Discipline_Report()
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
            setRetainInstance(false);
            mSavedInstanceState=savedInstanceState;
            getProfile();
            getStudentList();
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
        View rootView = inflater.inflate(R.layout.fragment_discipline, container, false);
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
            ll_discipline_report = (LinearLayout) view.findViewById(R.id.ll_discipline_report);
            layout_empty = (RelativeLayout) view.findViewById(R.id.layout_empty);
            tv_lbl_self_control = (TextView) view.findViewById(R.id.tv_lbl_self_control);
            tv_self_control = (TextView) view.findViewById(R.id.tv_self_control);
            tv_lbl_obey_rules = (TextView) view.findViewById(R.id.tv_lbl_obey_rules);
            tv_obey_rules = (TextView) view.findViewById(R.id.tv_obey_rules);
            tv_lbl_obey_staff = (TextView) view.findViewById(R.id.tv_lbl_obey_staff);
            tv_obey_staff = (TextView) view.findViewById(R.id.tv_obey_staff);
            tv_lbl_dress_code = (TextView) view.findViewById(R.id.tv_lbl_dress_code);
            tv_dress_code = (TextView) view.findViewById(R.id.tv_dress_code);
            tv_lbl_time_keeping = (TextView) view.findViewById(R.id.tv_lbl_time_keeping);
            tv_time_keeping = (TextView) view.findViewById(R.id.tv_time_keeping);
            tv_lbl_conduct = (TextView) view.findViewById(R.id.tv_lbl_conduct);
            tv_conduct = (TextView) view.findViewById(R.id.tv_conduct);
            text_view_empty = (TextView) view.findViewById(R.id.text_view_empty);
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
            if(mSavedInstanceState!=null)
            {
                mStudentListPosition=mSavedInstanceState.getInt(ARG_STUDENT_LIST_POSITION);
                Log.d(MODULE,TAG + "mStudentListPosition" + mStudentListPosition);
            }
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
            tv_lbl_self_control.setTypeface(font.getHelveticaRegular());
            tv_self_control.setTypeface(font.getHelveticaRegular());
            tv_lbl_obey_rules .setTypeface(font.getHelveticaRegular());
            tv_obey_rules.setTypeface(font.getHelveticaRegular());
            tv_lbl_obey_staff.setTypeface(font.getHelveticaRegular());
            tv_obey_staff.setTypeface(font.getHelveticaRegular());
            tv_lbl_dress_code.setTypeface(font.getHelveticaRegular());
            tv_dress_code.setTypeface(font.getHelveticaRegular());
            tv_lbl_time_keeping.setTypeface(font.getHelveticaRegular());
            tv_time_keeping.setTypeface(font.getHelveticaRegular());
            tv_lbl_conduct.setTypeface(font.getHelveticaRegular());
            tv_conduct.setTypeface(font.getHelveticaRegular());
            text_view_empty.setTypeface(font.getHelveticaRegular());
            vp_student.addOnPageChangeListener(_OnPageChangeListener);
            ll_discipline_report.setVisibility(View.VISIBLE);
            SetActionBar();
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
                mToolbar.setTitle(R.string.lbl_discipline_report);
                FragmentDrawer.mDrawerLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        FragmentDrawer.mDrawerToggle.syncState();
                    }
                });
                mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    public void getDisciplineReportFromService()
    {
        TAG = "getDisciplineReportFromService";
        Log.d(MODULE, TAG);
        try
        {
            mSelectedUser = mListStudents.get(mStudentListPosition);
            Str_ClassId = mSelectedUser.getClassId();
            Str_SectionId = mSelectedUser.getSectionId();
            Str_StudentId = mSelectedUser.getStudentId();
            new DisciplineReport_Process(mActivity,this,Payload_Discipline()).GetDisciplineReport();
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
            mStudentListPosition=position;
            getDisciplineReportFromService();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void onStudentsReceived()
    {
        TAG = "onStudentsReceived";
        Log.d(MODULE, TAG);
        getStudentList();
        showStudentsList();
    }

    @Override
    public void onStudentsReceivedError(String Str_Msg)
    {
        TAG = "onStudentsReceivedError";
        Log.d(MODULE, TAG);
        AppUtils.hideProgressDialog();
    }

    @Override
    public void onDisciplineReportReceived()
    {
        TAG = "onDisciplineReportReceived";
        Log.d(MODULE, TAG);
        try
        {
            getDisciplineReport();
            showDisciplineReport();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onDisciplineReportReceivedError(String Str_Msg)
    {
        TAG = "onDisciplineReportReceivedError";
        Log.d(MODULE, TAG);
        try
        {
            text_view_empty.setText(Str_Msg);
            showEmptyView();
            showSnackBar(Str_Msg);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void getStudentList()
    {
        TAG = "getStudentList";
        Log.d(MODULE, TAG);
        try
        {
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            String Str_Json = mPreferences.getString(AppUtils.SHARED_STUDENT_LIST, "");
            Log.d(MODULE, TAG + " Str_Json : " + Str_Json);
            if (Str_Json.length() > 0)
            {
                studentListresponse = (StudentsList_Response) AppUtils.fromJson(Str_Json, new TypeToken<StudentsList_Response>() { }.getType());
                mListStudents = studentListresponse.getCstudents();
                Log.d(MODULE, TAG + " mListStudent : " + mListStudents.size());
            }
            else
            {
                new GetStudentList(Str_StudentList_Url,Payload_Student_List(), this).getStudents();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void getDisciplineReport()
    {
        TAG = "getDisciplineReport";
        Log.d(MODULE, TAG);
        try
        {
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            String Str_Json = mPreferences.getString(AppUtils.SHARED_DISCIPLINE, "");
            Log.d(MODULE, TAG + " Str_Json : " + Str_Json);
            if(Str_Json.length()>0)
            {
                mDiscipline = (Discipline) AppUtils.fromJson(Str_Json, new TypeToken<Discipline>(){}.getType());
                Log.d(MODULE, TAG + " mDiscipline : " + mDiscipline.getConduct());
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
            if(mListStudents.size()>0)
            {
                StudentPagerAdapter adapter = new StudentPagerAdapter(mActivity,mListStudents);
                vp_student.setAdapter(adapter);
                getDisciplineReportFromService();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void showDisciplineReport()
    {
        TAG = "showDisciplineReport";
        Log.d(MODULE, TAG);
        try
        {
            if(mDiscipline!=null)
            {
                tv_self_control.setText(mDiscipline.getSelfControl());
                tv_obey_rules.setText(mDiscipline.getObeyRules());
                tv_obey_staff.setText(mDiscipline.getObeyStaff());
                tv_dress_code.setText(mDiscipline.getDressCode());
                tv_time_keeping.setText(mDiscipline.getTimeKeeping());
                tv_conduct.setText(mDiscipline.getConduct());
                layout_empty.setVisibility(View.GONE);
                ll_discipline_report.setVisibility(View.VISIBLE);
            }
            else
            {
                text_view_empty.setText(getString(R.string.lbl_no_discipline_report_found));
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

        try
        {
            layout_empty.setVisibility(View.VISIBLE);
            ll_discipline_report.setVisibility(View.GONE);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    public JSONObject Payload_Discipline()
    {
        TAG = "Payload_DiaryNotes";
        Log.d(MODULE, TAG);

        JSONObject obj = new JSONObject();
        try {

            obj.put("StudentId",Str_StudentId);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(MODULE, TAG + " obj : " + obj.toString());

        return obj;
    }

    public JSONObject Payload_Student_List()
    {
        TAG = "Payload_Student_List";
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

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_settings).setVisible(false);
        menu.findItem(R.id.action_list_view).setVisible(false);
        menu.findItem(R.id.action_chart_view).setVisible(false);
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
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void showSnackBar(String Str_Msg)
    {
        Snackbar snackbar = Snackbar.make(cl_main, Str_Msg, Snackbar.LENGTH_LONG);
        snackbar.setAction(getString(R.string.lbl_retry), new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                getDisciplineReportFromService();
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
