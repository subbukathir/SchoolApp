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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.Utils.Font;
import com.daemon.oxfordschool.activity.MainActivity;
import com.daemon.oxfordschool.adapter.ExamResultAdapter;
import com.daemon.oxfordschool.adapter.StudentPagerAdapter;
import com.daemon.oxfordschool.asyncprocess.ExamTypeList_Process;
import com.daemon.oxfordschool.asyncprocess.GetExamResult;
import com.daemon.oxfordschool.asyncprocess.GetStudentList;
import com.daemon.oxfordschool.classes.CExam;
import com.daemon.oxfordschool.classes.CResult;
import com.daemon.oxfordschool.classes.Common_Class;
import com.daemon.oxfordschool.classes.User;
import com.daemon.oxfordschool.components.RecycleEmptyErrorView;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.listeners.ExamResultListener;
import com.daemon.oxfordschool.listeners.ExamTypeListListener;
import com.daemon.oxfordschool.listeners.Exam_Result_List_Item_Click_Listener;
import com.daemon.oxfordschool.listeners.StudentsListListener;
import com.daemon.oxfordschool.response.CommonList_Response;
import com.daemon.oxfordschool.response.ExamResult_Response;
import com.daemon.oxfordschool.response.StudentsList_Response;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Fragment_ExamResult extends Fragment implements StudentsListListener,ExamTypeListListener,
        ExamResultListener,Exam_Result_List_Item_Click_Listener
{

    public static String MODULE = "Fragment_ExamResult";
    public static String TAG = "";

    Toolbar mToolbar;
    CoordinatorLayout cl_main;
    TextView tv_select_exam_type,text_view_empty,tv_lbl_exam_subject,
            tv_lbl_exam_marks,tv_lbl_exam_result;
    Spinner spinner_exam_type;
    RelativeLayout layout_empty;
    int mSelectedExamTypePosition=0;
    RecycleEmptyErrorView recycler_view;
    RecyclerView.LayoutManager mLayoutManager;

    SharedPreferences mPreferences;
    User mUser,mSelectedUser;
    ViewPager vp_student;

    ArrayList<User> mListStudents =new ArrayList<User>();
    ArrayList<Common_Class> mListExamType =new ArrayList<Common_Class>();
    ArrayList<CResult> mListResult =new ArrayList<CResult>();

    StudentsList_Response studentListResponse;
    CommonList_Response examListTypeResponse;
    ExamResult_Response response;

    AppCompatActivity mActivity;
    String Str_Id="",Str_ExamTypeId="",Str_ClassId="",Str_StudentId="";
    int mSelectedPosition=0;
    private Font font= MyApplication.getInstance().getFontInstance();
    String Str_StudentList_Url = ApiConstants.STUDENT_LIST;
    String Str_ExamResult_Url = ApiConstants.EXAM_RESULT_URL;

    public Fragment_ExamResult()
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
            new ExamTypeList_Process(mActivity,this).GetExamTypeList();
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
        View rootView = inflater.inflate(R.layout.fragment_exam_result, container, false);
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
            tv_select_exam_type = (TextView) view.findViewById(R.id.tv_select_exam_type);
            tv_lbl_exam_subject = (TextView) view.findViewById(R.id.tv_lbl_exam_subject);
            tv_lbl_exam_marks = (TextView) view.findViewById(R.id.tv_lbl_exam_marks);
            tv_lbl_exam_result = (TextView) view.findViewById(R.id.tv_lbl_exam_result);
            spinner_exam_type = (Spinner) view.findViewById(R.id.spinner_exam_type);
            recycler_view = (RecycleEmptyErrorView) view.findViewById(R.id.recycler_view_exam_result);
            layout_empty = (RelativeLayout) view.findViewById(R.id.layout_empty);
            text_view_empty = (TextView) view.findViewById(R.id.text_view_empty);
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
            layout_empty.setVisibility(View.GONE);
            tv_select_exam_type.setTypeface(font.getHelveticaRegular());
            tv_lbl_exam_subject.setTypeface(font.getHelveticaBold());
            tv_lbl_exam_marks.setTypeface(font.getHelveticaBold());
            tv_lbl_exam_result.setTypeface(font.getHelveticaBold());
            text_view_empty.setTypeface(font.getHelveticaRegular());
            mLayoutManager = new LinearLayoutManager(getActivity());
            recycler_view.setLayoutManager(mLayoutManager);
            vp_student.addOnPageChangeListener(_OnPageChangeListener);
            spinner_exam_type.setOnItemSelectedListener(_OnItemSelectedListener);
            text_view_empty.setText(getString(R.string.lbl_no_result));
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
                mToolbar.setTitle(R.string.lbl_exam_result);
                mToolbar.setSubtitle("");
                if(!MainActivity.mTwoPane)
                {
                    FragmentDrawer.mDrawerLayout.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
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


    public void getProfile()
    {
        TAG = "getProfile";
        Log.d(MODULE, TAG);
        try
        {
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            String Str_Json = mPreferences.getString(AppUtils.SHARED_LOGIN_PROFILE,"");
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
            if(mListExamType.size()>0)getExamResultFromService(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    AdapterView.OnItemSelectedListener _OnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

            Str_ExamTypeId = mListExamType.get(position).getID();
            mSelectedExamTypePosition=position;
            getExamResultFromService(position);

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    public void getExamResultFromService(int position)
    {
        TAG = "getExamResultFromService";
        Log.d(MODULE, TAG);
        try
        {
            mSelectedUser = mListStudents.get(mSelectedPosition);
            Str_ClassId = mSelectedUser.getClassId();
            Str_StudentId = mSelectedUser.getStudentId();
            new GetExamResult(Str_ExamResult_Url,Payload_ExamResult(),this).getExamResult();
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
    public void onExamTypeListReceived()
    {
        TAG = "onExamTypeListReceived";
        Log.d(MODULE, TAG);
        try
        {
            getExamTypeList();
            setExamTypeList();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onExamTypeListReceivedError(String Str_Msg)
    {
        TAG = "onClassListReceived";
        Log.d(MODULE, TAG);

        try
        {
            showSnackBar(Str_Msg,0);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onExamResultReceived() {
        TAG = "onExamTypeListReceived";
        Log.d(MODULE, TAG);
        try
        {
            getExamResult();
            showExamResult();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onExamResultReceivedError(String Str_Msg) {
        TAG = "onClassListReceived";
        Log.d(MODULE, TAG);

        try
        {
            showEmptyView();
            showSnackBar(Str_Msg,1);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onExamResultListItemClicked(int position) {

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

    public void getExamTypeList()
    {
        TAG = "getExamTypeList";
        Log.d(MODULE, TAG);
        try
        {
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            String Str_Json = mPreferences.getString(AppUtils.SHARED_EXAM_TYPE_LIST,"");
            Log.d(MODULE, TAG + " Str_Json : " + Str_Json);
            if(Str_Json.length()>0)
            {
                examListTypeResponse = (CommonList_Response) AppUtils.fromJson(Str_Json, new TypeToken<CommonList_Response>(){}.getType());
                mListExamType = examListTypeResponse.getCclass();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void setExamTypeList()
    {
        TAG = "setExamTypeList";
        Log.d(MODULE, TAG);
        try
        {
           if(mListExamType.size()>0)
           {
               String[] items = AppUtils.getArray(mListExamType);
               ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity,android.R.layout.simple_spinner_item,items);
               adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
               spinner_exam_type.setAdapter(adapter);
           }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void getExamResult()
    {
        TAG = "getExamResult";
        Log.d(MODULE, TAG);
        try
        {
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            String Str_Json = mPreferences.getString(AppUtils.SHARED_EXAM_RESULT,"");
            Log.d(MODULE, TAG + " Str_Json : " + Str_Json);
            if(Str_Json.length()>0)
            {
                response = (ExamResult_Response) AppUtils.fromJson(Str_Json, new TypeToken<ExamResult_Response>(){}.getType());
                mListResult = response.getCresult();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void showExamResult()
    {
        TAG = "showExamResult";
        Log.d(MODULE, TAG);
        try
        {
            if(mListResult.size()>0)
            {
                ExamResultAdapter adapter = new ExamResultAdapter(mListResult,this);
                recycler_view.setAdapter(adapter);
                layout_empty.setVisibility(View.GONE);
                recycler_view.setVisibility(View.VISIBLE);
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

        try
        {
            layout_empty.setVisibility(View.VISIBLE);
            recycler_view.setVisibility(View.GONE);
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

    public JSONObject Payload_ExamResult()
    {
        TAG = "Payload_ExamList";
        Log.d(MODULE, TAG);

        JSONObject obj = new JSONObject();
        try {
            obj.put("ExamTypeId", Str_ExamTypeId);
            obj.put("StudentId", Str_StudentId);
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
                if(!MainActivity.mTwoPane)
                {
                    if(FragmentDrawer.mDrawerLayout.isDrawerOpen(GravityCompat.START))
                        FragmentDrawer.mDrawerLayout.closeDrawer(GravityCompat.START);
                    else
                        FragmentDrawer.mDrawerLayout.openDrawer(GravityCompat.START);
                }
                return true;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void showSnackBar(String Str_Msg,final int mService)
    {
        Snackbar snackbar = Snackbar.make(cl_main, Str_Msg, Snackbar.LENGTH_LONG);
        snackbar.setAction(getString(R.string.lbl_retry), new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(mService==0) new ExamTypeList_Process(mActivity,Fragment_ExamResult.this).GetExamTypeList();
                else if(mService==1)
                {
                    if(mSelectedExamTypePosition > 0)
                    {
                        getExamResultFromService(mSelectedExamTypePosition);
                    }
                }
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
