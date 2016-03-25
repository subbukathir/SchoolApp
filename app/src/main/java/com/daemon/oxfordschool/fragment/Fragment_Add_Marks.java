package com.daemon.oxfordschool.fragment;

/**
 * Created by Ravi on 29/07/15.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.daemon.oxfordschool.asyncprocess.ClassList_Process;
import com.daemon.oxfordschool.asyncprocess.ExamTypeList_Process;
import com.daemon.oxfordschool.asyncprocess.AddMarks;
import com.daemon.oxfordschool.asyncprocess.SectionList_Process;
import com.daemon.oxfordschool.asyncprocess.GetStudentList;
import com.daemon.oxfordschool.asyncprocess.SubjectList_Process;
import com.daemon.oxfordschool.classes.Common_Class;
import com.daemon.oxfordschool.classes.User;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.listeners.ClassListListener;
import com.daemon.oxfordschool.listeners.AddMarksListener;
import com.daemon.oxfordschool.listeners.ExamTypeListListener;
import com.daemon.oxfordschool.listeners.SectionListListener;
import com.daemon.oxfordschool.listeners.SubjectListListener;
import com.daemon.oxfordschool.listeners.StudentsListListener;
import com.daemon.oxfordschool.response.CommonList_Response;
import com.daemon.oxfordschool.response.StudentsList_Response;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Fragment_Add_Marks extends Fragment implements ClassListListener,SectionListListener,
        ExamTypeListListener,SubjectListListener,AddMarksListener,StudentsListListener
{

    public static String MODULE = "Fragment_Add_Marks";
    public static String TAG = "";

    EditText et_add_theorymark, et_add_practicalmark;
    TextInputLayout til_add_theorymark,til_add_practicalmark;
    TextView tv_lbl_class,tv_lbl_section,tv_select_exam_type,tv_select_subject,tv_select_student;
    Spinner spinner_class,spinner_section,spinner_exam_type,spinner_subject,spinner_student;
    Button btn_Add_Marks;
    LinearLayout layout_section;

    SharedPreferences mPreferences;
    User mUser;
    Toolbar mToolbar;

    ArrayList<Common_Class> mListClass =new ArrayList<Common_Class>();
    ArrayList<Common_Class> mListSection =new ArrayList<Common_Class>();
    ArrayList<Common_Class> mListExamType =new ArrayList<Common_Class>();
    ArrayList<Common_Class> mListSubjects = new ArrayList<Common_Class>();
    ArrayList<User> mListStudent =new ArrayList<User>();

    CommonList_Response responseCommon;
    CommonList_Response subjectListResponse;
    CommonList_Response examListResponse;
    StudentsList_Response studentListresponse;

    AppCompatActivity mActivity;
    String Str_Id,Str_ClassId="",Str_SectionId,Str_ExamTypeId,Str_SubjectId="",Str_StudentId="";
    String Str_TheoryMark="", Str_PracticalMark="";
    private Font font= MyApplication.getInstance().getFontInstance();
    String Str_AddMarks_Url = ApiConstants.ADD_MARKS_URL;
    String Str_StudentList_Url = ApiConstants.STUDENT_LIST;
    Fragment mFragment;

    public Fragment_Add_Marks()
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
            getProfile();
            getClassList();
            getExamTypeList();
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
        View rootView = inflater.inflate(R.layout.fragment_add_marks, container, false);
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
            btn_Add_Marks = (Button) view.findViewById(R.id.btn_add_marks);

            tv_select_exam_type = (TextView) view.findViewById(R.id.tv_select_exam_type);
            spinner_exam_type = (Spinner) view.findViewById(R.id.spinner_exam_type);

            tv_select_subject = (TextView) view.findViewById(R.id.tv_select_subject);
            spinner_subject = (Spinner) view.findViewById(R.id.spinner_subject);

            tv_select_student = (TextView) view.findViewById(R.id.tv_select_student);
            spinner_student = (Spinner) view.findViewById(R.id.spinner_student);

            spinner_class = (Spinner) view.findViewById(R.id.spinner_class);
            spinner_section = (Spinner) view.findViewById(R.id.spinner_section);

            et_add_theorymark = (EditText) view.findViewById(R.id.et_add_theorymark);
            et_add_practicalmark = (EditText) view.findViewById(R.id.et_add_practicalmark);

            til_add_theorymark= (TextInputLayout) view.findViewById(R.id.til_add_theorymark);
            til_add_practicalmark= (TextInputLayout) view.findViewById(R.id.til_add_practicalmark);

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
            if(mListStudent.size()>0) showStudentsList();
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
                mToolbar.setTitle(R.string.lbl_add_marks);
                mToolbar.setSubtitle("");
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

    public void setProperties()
    {
        TAG = "setProperties";
        Log.d(MODULE, TAG);
        try
        {

            tv_lbl_class.setTypeface(font.getHelveticaRegular());
            tv_lbl_section.setTypeface(font.getHelveticaRegular());
            tv_select_student.setTypeface(font.getHelveticaRegular());

            tv_select_exam_type.setTypeface(font.getHelveticaRegular());
            tv_select_subject.setTypeface(font.getHelveticaRegular());

            et_add_theorymark.setTypeface(font.getHelveticaRegular());
            et_add_practicalmark.setTypeface(font.getHelveticaRegular());
            til_add_theorymark.setTypeface(font.getHelveticaRegular());
            til_add_practicalmark.setTypeface(font.getHelveticaRegular());

            spinner_class.setOnItemSelectedListener(_OnClassItemSelectedListener);
            spinner_section.setOnItemSelectedListener(_OnSectionItemSelectedListener);
            spinner_exam_type.setOnItemSelectedListener(_OnItemSelectedListener);
            spinner_subject.setOnItemSelectedListener(_OnSubjectItemSelectedListener);
            spinner_student.setOnItemSelectedListener(_OnStudentItemSelectedListener);
            btn_Add_Marks.setOnClickListener(_OnClickListener);

            et_add_theorymark.addTextChangedListener(new MyTextWatcher(et_add_theorymark));
            et_add_practicalmark.addTextChangedListener(new MyTextWatcher(et_add_practicalmark));
            showSectionList();
            showStudentsList();
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
                mListStudent = studentListresponse.getCstudents();
                Log.d(MODULE, TAG + " mListStudent : " + mListStudent.size());
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
                    new GetStudentList(Str_StudentList_Url,Payload_Student_List(), Fragment_Add_Marks.this).getStudents();
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
                    Str_StudentId=mListStudent.get(position-1).getUserId();
                    Log.d(MODULE, TAG + " Str_StudentId : " + Str_StudentId);
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
    public void onSectionListReceived() {
        TAG = "onSectionListReceived";
        Log.d(MODULE, TAG);
        try {
            getSectionList();
            showSectionList();
        }
        catch (Exception ex)
        {

        }
    }

    @Override
    public void onSectionListReceivedError(String Str_Msg) {  }

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
    public void onAddMarksReceived(String Str_Msg)
    {
        TAG = "onAddMarksReceived";
        Log.d(MODULE, TAG);
        AppUtils.hideProgressDialog();
        AppUtils.showDialog(mActivity, Str_Msg);
    }

    @Override
    public void onAddMarksReceivedError(String Str_Msg)
    {
        TAG = "onAddMarksReceivedError";
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

    public void showStudentsList()
    {
        TAG = "showStudentsList";
        Log.d(MODULE, TAG);
        try
        {
            if(mListStudent.size()>0)
            {
                String[] items = AppUtils.getStudentArray(mListStudent, getString(R.string.lbl_select_student));
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity,android.R.layout.simple_spinner_item,items);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_student.setAdapter(adapter);
            }
            else
            {
                String[] items = new String[1];
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

    public JSONObject Payload_AddMarks()
    {
        TAG = "Payload_AddMarks";
        Log.d(MODULE, TAG);

        JSONObject obj = new JSONObject();
        try
        {
            obj.put("UserId", Str_Id);
            obj.put("ExamId", Str_ExamTypeId);
            obj.put("ClassId", Str_ClassId);
            obj.put("SectionId", Str_SectionId);
            obj.put("StudentId", Str_StudentId);
            obj.put("SubjectId", Str_SubjectId);
            obj.put("TheoryMarks", Str_TheoryMark);
            obj.put("PracticalMarks", Str_PracticalMark);
            obj.put("Mode", "0");
            obj.put("ResultId", "1");

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
            obj.put("SectionId",Str_SectionId);
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
                case R.id.btn_add_marks:
                    if(IsValid()) new AddMarks(Str_AddMarks_Url,mFragment,Payload_AddMarks()).addMarks();
                    break;
            }

        }
    };

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

}
