package com.daemon.oxfordschool.fragment;

/**
 * Created by Ravi on 29/07/15.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.Utils.Font;
import com.daemon.oxfordschool.activity.MainActivity;
import com.daemon.oxfordschool.asyncprocess.AddExam;
import com.daemon.oxfordschool.asyncprocess.AddMarks;
import com.daemon.oxfordschool.asyncprocess.ClassList_Process;
import com.daemon.oxfordschool.asyncprocess.ExamTypeList_Process;
import com.daemon.oxfordschool.asyncprocess.GetStudentList;
import com.daemon.oxfordschool.asyncprocess.SectionList_Process;
import com.daemon.oxfordschool.asyncprocess.SubjectList_Process;
import com.daemon.oxfordschool.classes.Common_Class;
import com.daemon.oxfordschool.classes.User;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.listeners.AddExamListener;
import com.daemon.oxfordschool.listeners.AddMarksListener;
import com.daemon.oxfordschool.listeners.ClassListListener;
import com.daemon.oxfordschool.listeners.DateSetListener;
import com.daemon.oxfordschool.listeners.ExamTypeListListener;
import com.daemon.oxfordschool.listeners.SectionListListener;
import com.daemon.oxfordschool.listeners.StudentsListListener;
import com.daemon.oxfordschool.listeners.SubjectListListener;
import com.daemon.oxfordschool.response.CommonList_Response;
import com.daemon.oxfordschool.response.StudentsList_Response;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class Fragment_Add_Exam extends Fragment implements ClassListListener,DateSetListener,
        ExamTypeListListener,SubjectListListener,AddExamListener
{

    public static String MODULE = "Fragment_Add_Exam";
    public static String TAG = "";

    EditText et_add_theorymark, et_add_practicalmark;
    TextInputLayout til_add_theorymark,til_add_practicalmark;
    TextView tv_lbl_class,tv_lbl_section,tv_select_exam_type,tv_select_subject,tv_lbl_select_date;
    Spinner spinner_class,spinner_exam_type,spinner_subject;
    Button btn_add_exam,btn_select_date;
    LinearLayout layout_section;

    SharedPreferences mPreferences;
    User mUser;
    Toolbar mToolbar;

    ArrayList<Common_Class> mListClass =new ArrayList<Common_Class>();
    ArrayList<Common_Class> mListSection =new ArrayList<Common_Class>();
    ArrayList<Common_Class> mListExamType =new ArrayList<Common_Class>();
    ArrayList<Common_Class> mListSubjects = new ArrayList<Common_Class>();

    CommonList_Response responseCommon;
    CommonList_Response subjectListResponse;
    CommonList_Response examListResponse;

    AppCompatActivity mActivity;
    String Str_Id,Str_ClassId="",Str_ExamTypeId,Str_SubjectId="",Str_Date="";
    String Str_TheoryMark="", Str_PracticalMark="";
    private Font font= MyApplication.getInstance().getFontInstance();
    String Str_AddExam_Url = ApiConstants.ADD_EXAM_URL;
    Fragment mFragment;
    FragmentManager mManager;

    DateFormat mDateFormat = new SimpleDateFormat("dd-MM-yyyy");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    Date tdate,selectedDate;

    public Fragment_Add_Exam()
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
            mFragment = this;
            mManager = mActivity.getSupportFragmentManager();
            getProfile();
            getClassList();
            getExamTypeList();
            setHasOptionsMenu(true);
            Str_Date=GetTodayDate();
            new ExamTypeList_Process(mActivity,this).GetExamTypeList();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_add_exam, container, false);
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

            layout_section = (LinearLayout) view.findViewById(R.id.layout_section);
            btn_add_exam = (Button) view.findViewById(R.id.btn_add_exam);

            tv_select_exam_type = (TextView) view.findViewById(R.id.tv_select_exam_type);
            spinner_exam_type = (Spinner) view.findViewById(R.id.spinner_exam_type);

            tv_select_subject = (TextView) view.findViewById(R.id.tv_select_subject);
            spinner_subject = (Spinner) view.findViewById(R.id.spinner_subject);

            spinner_class = (Spinner) view.findViewById(R.id.spinner_class);

            et_add_theorymark = (EditText) view.findViewById(R.id.et_add_theorymark);
            et_add_practicalmark = (EditText) view.findViewById(R.id.et_add_practicalmark);

            til_add_theorymark= (TextInputLayout) view.findViewById(R.id.til_add_theorymark);
            til_add_practicalmark= (TextInputLayout) view.findViewById(R.id.til_add_practicalmark);

            tv_lbl_select_date = (TextView)view.findViewById(R.id.tv_lbl_select_date);
            btn_select_date = (Button) view.findViewById(R.id.btn_select_date);

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
            if(mListSubjects.size()>0) showSubjectsList();
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
                mToolbar.setTitle(R.string.lbl_add_exam);
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
            layout_section.setVisibility(View.GONE);

            tv_lbl_class.setTypeface(font.getHelveticaRegular());
            tv_lbl_section.setTypeface(font.getHelveticaRegular());

            tv_select_exam_type.setTypeface(font.getHelveticaRegular());
            tv_select_subject.setTypeface(font.getHelveticaRegular());

            btn_select_date.setTypeface(font.getHelveticaRegular());
            tv_lbl_select_date.setTypeface(font.getHelveticaRegular());

            et_add_theorymark.setTypeface(font.getHelveticaRegular());
            et_add_practicalmark.setTypeface(font.getHelveticaRegular());
            til_add_theorymark.setTypeface(font.getHelveticaRegular());
            til_add_practicalmark.setTypeface(font.getHelveticaRegular());

            spinner_class.setOnItemSelectedListener(_OnClassItemSelectedListener);
            spinner_exam_type.setOnItemSelectedListener(_OnItemSelectedListener);
            spinner_subject.setOnItemSelectedListener(_OnSubjectItemSelectedListener);

            btn_add_exam.setOnClickListener(_OnClickListener);
            btn_select_date.setOnClickListener(_OnClickListener);;

            et_add_theorymark.addTextChangedListener(new MyTextWatcher(et_add_theorymark));
            et_add_practicalmark.addTextChangedListener(new MyTextWatcher(et_add_practicalmark));
            btn_select_date.setText(ConvertedDate());
            showSubjectsList();
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

    public void getSubjectList()
    {
        TAG = "getSubjectList";
        Log.d(MODULE, TAG);
        try
        {
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            String Str_Json = mPreferences.getString(AppUtils.SHARED_SUBJECT_LIST, "");
            Log.d(MODULE, TAG + " Str_Json : " + Str_Json);
            if (Str_Json.length() > 0)
            {
                subjectListResponse = (CommonList_Response) AppUtils.fromJson(Str_Json, new TypeToken<CommonList_Response>() { }.getType());
                mListSubjects = subjectListResponse.getCclass();
                Log.d(MODULE, TAG + " mListSubjects : " + mListSubjects.size());
            }
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
                    getSubjectsListFromService();
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

    AdapterView.OnItemSelectedListener _OnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

            if(position>0)
            {
                Str_ExamTypeId = mListExamType.get(position-1).getID();
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    AdapterView.OnItemSelectedListener _OnSubjectItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

            if(position>0)
            {
                Str_SubjectId = mListSubjects.get(position-1).getID();
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    public void getSubjectsListFromService()
    {
        TAG = "getSubjectsListFromService";
        Log.d(MODULE, TAG);
        try
        {
            new SubjectList_Process(this, PayloadSection()).GetSubjectsList();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

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
    public void onExamTypeListReceived()
    {
        TAG = "onExamTypeListReceived";
        Log.d(MODULE, TAG);
        try
        {
            getExamTypeList();
            showExamTypeList();
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

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onSubjectListReceived()
    {
        TAG = "onSubjectListReceived";
        Log.d(MODULE, TAG);
        try
        {
            getSubjectList();
            showSubjectsList();
        }
        catch (Exception ex)
        {

        }
    }

    @Override
    public void onSubjectListReceivedError(String Str_Msg) {

    }

    @Override
    public void onAddExamReceived(String Str_Msg)
    {
        TAG = "onAddExamReceived";
        Log.d(MODULE, TAG);
        AppUtils.hideProgressDialog();
        AppUtils.showDialog(mActivity, Str_Msg);
    }

    @Override
    public void onAddExamReceivedError(String Str_Msg)
    {
        TAG = "onAddExamReceivedError";
        Log.d(MODULE, TAG);
        AppUtils.hideProgressDialog();
        AppUtils.showDialog(mActivity, Str_Msg);
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
                examListResponse = (CommonList_Response) AppUtils.fromJson(Str_Json, new TypeToken<CommonList_Response>(){}.getType());
                mListExamType = examListResponse.getCclass();
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

    public void showExamTypeList()
    {
        TAG = "showExamTypeList";
        Log.d(MODULE, TAG);
        try
        {
            if(mListExamType.size()>0)
            {
                String[] items = AppUtils.getArray(mListExamType,getString(R.string.lbl_select_exam_type));
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

    public void showSubjectsList()
    {
        TAG = "showSubjectsList";
        Log.d(MODULE, TAG);
        try
        {
            String[] items = null;

            if(mListSubjects.size()>0)
            {
                items = AppUtils.getArray(mListSubjects,getString(R.string.lbl_select_subject));
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity,android.R.layout.simple_spinner_item,items);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_subject.setAdapter(adapter);
            }
            else
            {
                items = new String[1];
                items[0] = getString(R.string.lbl_select_subject);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity,android.R.layout.simple_spinner_item,items);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_subject.setAdapter(adapter);
            }
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

    public JSONObject Payload_AddExam()
    {
        TAG = "Payload_AddExam";
        Log.d(MODULE, TAG);

        JSONObject obj = new JSONObject();
        try
        {
            obj.put("UserId", Str_Id);
            obj.put("ExamType", Str_ExamTypeId);
            obj.put("ClassId", Str_ClassId);
            obj.put("ExamDate",Str_Date);
            obj.put("SubjectId", Str_SubjectId);
            obj.put("TheoryMarks", Str_TheoryMark);
            obj.put("PracticalMarks", Str_PracticalMark);
            obj.put("OtherMarks", "0");
            obj.put("Mode", "0");
            obj.put("ExamId", "0");

        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(MODULE, TAG + " obj : " + obj.toString());

        return obj;
    }

    View.OnClickListener _OnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            switch (view.getId())
            {
                case R.id.btn_add_exam:
                     if(IsValid())
                     {
                         AppUtils.showProgressDialog(mActivity);
                         new AddExam(Str_AddExam_Url,mFragment,Payload_AddExam()).addExam();
                     }
                     break;
                case R.id.btn_select_date:
                     selectDate();
                     break;
                default:
                     break;
            }

        }
    };

    public String GetTodayDate() {
        String Str_TodayDate = "";
        try
        {
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            Str_TodayDate = date;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return Str_TodayDate;
    }

    public void selectDate() {
        SelectDateFragment newFragment = new SelectDateFragment();
        newFragment.setListener(this);
        newFragment.setDate(Str_Date);
        newFragment.show(mActivity.getSupportFragmentManager(), "DatePicker");
    }

    public void populateSetDate(int year, int month, int day)
    {
        String currDate="";
        currDate = year + "-" + month + "-"+day;
        //currDate = mDateFormat.format(currDate);
        try
        {
            tdate=mDateFormat.parse(Str_Date);
            selectedDate = mDateFormat.parse(currDate);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        if(selectedDate.before(tdate))
        {
            AppUtils.showDialog(mActivity, getResources().getString(R.string.msg_future_date));
            btn_select_date.setText(ConvertedDate());
        }
        else
        {
            Str_Date=currDate;
            btn_select_date.setText(ConvertedDate());
        }
    }

    public boolean IsValid() {
        TAG = "IsValid";
        Log.d(MODULE, TAG);

        boolean IsValid = true;

        if(validateTheoryMark() && validatePracticalMark()==true) IsValid=true;
        else IsValid=false;

        return IsValid;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
           // mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        }
    }

    private boolean validateTheoryMark() {
        if (et_add_theorymark.getText().toString().trim().isEmpty()) {
            til_add_theorymark.setError(getString(R.string.lbl_msg_blank_theory_mark));
            requestFocus(et_add_theorymark);
            return false;
        }else if(et_add_theorymark.getText().length()<1){
            til_add_theorymark.setError(getString(R.string.lbl_msg_valid_theory_mark));
            requestFocus(et_add_theorymark);
            return false;
        }else {
            Str_TheoryMark=et_add_theorymark.getText().toString().trim();
            til_add_theorymark.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePracticalMark() {
        if (et_add_practicalmark.getText().toString().trim().isEmpty()) {
            til_add_practicalmark.setError(getString(R.string.lbl_msg_blank_practical_mark));
            requestFocus(et_add_practicalmark);
            return false;
        }else if(et_add_practicalmark.getText().length()<1  ){
            til_add_practicalmark.setError(getString(R.string.lbl_msg_valid_practical_mark));
            requestFocus(et_add_practicalmark);
            return false;
        }else {
            Str_PracticalMark=et_add_practicalmark.getText().toString().trim();
            til_add_practicalmark.setErrorEnabled(false);
        }

        return true;
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.et_add_theorymark:
                    validateTheoryMark();
                    break;
                case R.id.et_add_practicalmark:
                    validatePracticalMark();
                    break;
                default:
                    break;

            }
        }
    }

    public String ConvertedDate()
    {
        TAG = "ConvertedDate";
        Log.d(MODULE,TAG);
        String Str_ReturnValue="";
        try
        {
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat format1 = new SimpleDateFormat("E, MMM dd yyyy");
            Date date;
            date = sdf1.parse(Str_Date);
            Str_ReturnValue = format1.format(date);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return Str_ReturnValue;
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

}
