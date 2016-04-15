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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import com.daemon.oxfordschool.adapter.StudentPagerAdapter;
import com.daemon.oxfordschool.asyncprocess.ClassList_Process;
import com.daemon.oxfordschool.asyncprocess.DisciplineReport_Process;
import com.daemon.oxfordschool.asyncprocess.GetStudentList;
import com.daemon.oxfordschool.asyncprocess.SectionList_Process;
import com.daemon.oxfordschool.classes.CHomework;
import com.daemon.oxfordschool.classes.Common_Class;
import com.daemon.oxfordschool.classes.Discipline;
import com.daemon.oxfordschool.classes.User;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.listeners.ClassListListener;
import com.daemon.oxfordschool.listeners.DisciplineReportListener;
import com.daemon.oxfordschool.listeners.SectionListListener;
import com.daemon.oxfordschool.listeners.StudentsListListener;
import com.daemon.oxfordschool.response.CommonList_Response;
import com.daemon.oxfordschool.response.StudentsList_Response;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Fragment_Discipline_Report_Staff extends Fragment implements ClassListListener,SectionListListener,
        StudentsListListener,DisciplineReportListener
{

    public static String MODULE = "Fragment_Discipline_Report_Staff ";
    public static String TAG = "";

    TextView tv_lbl_self_control,tv_self_control,tv_lbl_obey_rules,tv_obey_rules,tv_lbl_obey_staff,tv_obey_staff,tv_lbl_dress_code,
             tv_dress_code,tv_lbl_time_keeping,tv_time_keeping,tv_lbl_conduct,tv_conduct,tv_lbl_class,tv_lbl_section,
             tv_lbl_select_student,text_view_empty;
    Spinner spinner_class,spinner_section,spinner_student;
    LinearLayout ll_discipline_report;
    RelativeLayout layout_empty;
    SharedPreferences mPreferences;
    Toolbar mToolbar;
    User mUser,mSelectedUser;

    ArrayList<User> mListStudents =new ArrayList<User>();
    ArrayList<Common_Class> mListClass =new ArrayList<Common_Class>();
    ArrayList<Common_Class> mListSection =new ArrayList<Common_Class>();
    StudentsList_Response studentListresponse;
    CommonList_Response responseCommon;
    Discipline mDiscipline;

    AppCompatActivity mActivity;
    Bundle mSavedInstanceState;
    String Str_Id="",Str_ClassId="",Str_SectionId="",Str_StudentId="";
    private Font font= MyApplication.getInstance().getFontInstance();
    String Str_StudentList_Url = ApiConstants.STUDENT_LIST;

    int mClassListPosition=0,mSectionListPosition=0,mStudentListPosition=0;
    final static String ARG_CLASS_LIST_POSITION = "Class_List_Position";
    final static String ARG_SECTION_LIST_POSITION = "Section_List_Position";
    final static String ARG_STUDENT_LIST_POSITION = "Student_List_Position";

    public Fragment_Discipline_Report_Staff()
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
            getClassList();
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
        View rootView = inflater.inflate(R.layout.fragment_discipline_staff, container, false);
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
            tv_lbl_select_student = (TextView) view.findViewById(R.id.tv_lbl_select_student);

            spinner_class = (Spinner) view.findViewById(R.id.spinner_class);
            spinner_section = (Spinner) view.findViewById(R.id.spinner_section);
            spinner_student = (Spinner) view.findViewById(R.id.spinner_student);

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

        super.onStart();

        TAG = "onStart";
        Log.d(MODULE, TAG);

        try
        {
            if(mSavedInstanceState!=null)
            {
                mClassListPosition=mSavedInstanceState.getInt(ARG_CLASS_LIST_POSITION);
                mSectionListPosition=mSavedInstanceState.getInt(ARG_SECTION_LIST_POSITION);
                mStudentListPosition=mSavedInstanceState.getInt(ARG_STUDENT_LIST_POSITION);
                Log.d(MODULE,TAG + "mClassListPosition" + mClassListPosition);
                Log.d(MODULE,TAG + "mSectionListPosition" + mSectionListPosition);
                Log.d(MODULE,TAG + "mStudentListPosition" + mStudentListPosition);
            }
            if(mListClass.size()>0) showClassList();
            if(mListSection.size()>0) showSectionList();
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
            tv_lbl_class.setTypeface(font.getHelveticaRegular());
            tv_lbl_section.setTypeface(font.getHelveticaRegular());
            tv_lbl_select_student.setTypeface(font.getHelveticaRegular());
            tv_lbl_class.setTypeface(font.getHelveticaRegular());
            tv_lbl_section.setTypeface(font.getHelveticaRegular());
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

            text_view_empty.setText(getString(R.string.lbl_select_class_student));
            showEmptyView();

            spinner_class.setOnItemSelectedListener(_OnClassItemSelectedListener);
            spinner_section.setOnItemSelectedListener(_OnSectionItemSelectedListener);
            spinner_student.setOnItemSelectedListener(_OnStudentItemSelectedListener);

            SetActionBar();
            showSectionList();
            showStudentsList();
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

    AdapterView.OnItemSelectedListener _OnClassItemSelectedListener =  new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id)
        {
            TAG = "onItemSelected";
            Log.d(MODULE, TAG);
            try
            {
                if (position > 0)
                {
                    Str_ClassId = mListClass.get(position - 1).getID();
                    getSectionListFromService();
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
                    new GetStudentList(Str_StudentList_Url,Payload_Student_List(),Fragment_Discipline_Report_Staff.this).getStudents();
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
                    Str_StudentId=mListStudents.get(position-1).getUserId();
                    Log.d(MODULE, TAG + " Str_StudentId : " + Str_StudentId);
                    mStudentListPosition=position-1;
                    getDisciplineReportFromService();
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
            ex.printStackTrace();
        }
    }

    @Override
    public void onSectionListReceivedError(String Str_Msg) {

    }

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
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void getSectionListFromService()
    {
        TAG = "getSectionListFromService";
        Log.d(MODULE, TAG);
        try
        {
            new SectionList_Process(this, PayloadSection()).GetSectionList();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
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
            spinner_class.setSelection(mClassListPosition);
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
            String[] items=null;
            if(mListSection.size()>0)
            {
                items = AppUtils.getArray(mListSection,getString(R.string.lbl_select_section));
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity,android.R.layout.simple_spinner_item,items);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_section.setAdapter(adapter);
                spinner_section.setSelection(mSectionListPosition);
            }
            else
            {
                items = new String[1];
                items[0] = getString(R.string.lbl_select_section);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity,android.R.layout.simple_spinner_item,items);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_section.setAdapter(adapter);
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
            String[] items=null;
            if(mListStudents.size()>0)
            {
                items = AppUtils.getStudentArray(mListStudents, getString(R.string.lbl_select_student));
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity,android.R.layout.simple_spinner_item,items);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_student.setAdapter(adapter);
                spinner_student.setSelection(mStudentListPosition);
            }
            else
            {
                items = new String[1];
                items[0] = getString(R.string.lbl_select_student);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity,android.R.layout.simple_spinner_item,items);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_student.setAdapter(adapter);
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

}
