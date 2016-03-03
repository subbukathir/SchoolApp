package com.daemon.oxfordschool.fragment;

/**
 * Created by Ravi on 29/07/15.
 */
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.Utils.Font;
import com.daemon.oxfordschool.activity.MainActivity;
import com.daemon.oxfordschool.asyncprocess.ClassList_Process;
import com.daemon.oxfordschool.asyncprocess.SubjectList_Process;
import com.daemon.oxfordschool.asyncprocess.SectionList_Process;
import com.daemon.oxfordschool.asyncprocess.AddHomeWork;
import com.daemon.oxfordschool.classes.CHomework;
import com.daemon.oxfordschool.classes.Common_Class;
import com.daemon.oxfordschool.classes.User;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.listeners.DateSetListener;
import com.daemon.oxfordschool.listeners.SubjectListListener;
import com.daemon.oxfordschool.listeners.ClassListListener;
import com.daemon.oxfordschool.listeners.SectionListListener;
import com.daemon.oxfordschool.listeners.AddHomeWorkListener;

import com.daemon.oxfordschool.response.CommonList_Response;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.DataFormatException;

public class Fragment_Add_HomeWork extends Fragment implements AddHomeWorkListener,DateSetListener,SubjectListListener,ClassListListener,SectionListListener
{

    public static String MODULE = "Fragment_Add_HomeWork ";
    public static String TAG = "";

    SharedPreferences mPreferences;
    Toolbar mToolbar;
    User mUser;
    ArrayList<Common_Class> mListSubject =new ArrayList<Common_Class>();
    ArrayList<Common_Class> mListClass =new ArrayList<Common_Class>();
    ArrayList<Common_Class> mListSection =new ArrayList<Common_Class>();
    CHomework cHomework;

    CommonList_Response responseCommon;
    Bundle mBundle;

    EditText et_add_assignment_I, et_add_assignment_II;
    TextInputLayout til_add_assignment_I,til_add_assignment_II;
    Spinner spinner_class,spinner_section,spinner_subject;
    String Str_ClassId="",Str_SectionId="",Str_SubjectId="",Str_SubjectName="",Str_Date="",Str_AssignmentI="",Str_AssignmentII="",Str_HomeWorkId="";
    TextView tv_lbl_class,tv_lbl_section,tv_lbl_subject,tv_lbl_select_date;
    Button btn_select_date,btn_add_homework;
    Date tdate,selectedDate;

    AppCompatActivity mActivity;
    FragmentManager mManager;
    String Str_Id="";
    int mMode=0;

    DateFormat mDateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private Font font= MyApplication.getInstance().getFontInstance();
    String Str_Url = ApiConstants.ADD_HOMEWORK_URL;
    Fragment fragment;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public Fragment_Add_HomeWork()
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
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            mManager = mActivity.getSupportFragmentManager();
            mBundle = this.getArguments();
            Str_Date=GetTodayDate();
            if(mBundle !=null)
            {
                getBundle();
            }
            getProfile();
            getClassList();
            getSectionList();
            getSubjectList();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_add_homework, container, false);
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
            tv_lbl_select_date = (TextView)view.findViewById(R.id.tv_lbl_select_date);
            tv_lbl_subject = (TextView)view.findViewById(R.id.tv_lbl_select_subject);

            et_add_assignment_I = (EditText) view.findViewById(R.id.et_add_assignment_I);
            et_add_assignment_II = (EditText) view.findViewById(R.id.et_add_assignment_II);

            til_add_assignment_I= (TextInputLayout) view.findViewById(R.id.til_add_assignment_I);
            til_add_assignment_II= (TextInputLayout) view.findViewById(R.id.til_add_assignment_II);

            spinner_class = (Spinner) view.findViewById(R.id.spinner_class);
            spinner_section = (Spinner) view.findViewById(R.id.spinner_section);
            spinner_subject = (Spinner) view.findViewById(R.id.spinner_subject);

            btn_select_date = (Button) view.findViewById(R.id.btn_select_date);
            btn_add_homework = (Button) view.findViewById(R.id.btn_add_homework);


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
            if(mListSubject.size()>0) showSubjectList();
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

            SetActionBar();
            tv_lbl_class.setTypeface(font.getHelveticaRegular());
            tv_lbl_section.setTypeface(font.getHelveticaRegular());
            tv_lbl_select_date.setTypeface(font.getHelveticaRegular());
            tv_lbl_subject.setTypeface(font.getHelveticaRegular());
            btn_select_date.setTypeface(font.getHelveticaRegular());
            btn_add_homework.setTypeface(font.getHelveticaRegular());

            et_add_assignment_I.setTypeface(font.getHelveticaRegular());
            et_add_assignment_II.setTypeface(font.getHelveticaRegular());
            til_add_assignment_I.setTypeface(font.getHelveticaRegular());
            til_add_assignment_II.setTypeface(font.getHelveticaRegular());

            spinner_class.setOnItemSelectedListener(_OnClassItemSelectedListener);
            spinner_section.setOnItemSelectedListener(_OnSectionItemSelectedListener);
            spinner_subject.setOnItemSelectedListener(_OnSubjectItemSelectedListener);

            et_add_assignment_I.addTextChangedListener(new MyTextWatcher(et_add_assignment_I));
            et_add_assignment_II.addTextChangedListener(new MyTextWatcher(et_add_assignment_II));

            btn_select_date.setOnClickListener(_onClickListener);
            btn_add_homework.setOnClickListener(_onClickListener);

            if (mMode==AppUtils.MODE_UPDATE)
            {
                TAG = "setProperties from bundle";
                Log.d(MODULE, TAG);
                et_add_assignment_I.setText(Str_AssignmentI);
                et_add_assignment_II.setText(Str_AssignmentII);
                btn_select_date.setText(ConvertedDate());
                btn_add_homework.setText(getResources().getString(R.string.lbl_update));
            }
            else btn_select_date.setText(ConvertedDate());
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
                if(mMode==AppUtils.MODE_ADD) mToolbar.setTitle(R.string.lbl_add_homework);
                else mToolbar.setTitle(R.string.lbl_update_homework);
                final ActionBar ab = mActivity.getSupportActionBar();
                ab.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
                ab.setDisplayHomeAsUpEnabled(true);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        }
    }

    private boolean validateAssignmentI() {
        if (et_add_assignment_I.getText().toString().trim().isEmpty()) {
            til_add_assignment_I.setError(getString(R.string.lbl_msg_blank_assignmentI));
            requestFocus(et_add_assignment_I);
            return false;
        }else if(et_add_assignment_I.getText().length()<3){
            til_add_assignment_I.setError(getString(R.string.lbl_msg_valid_assignmentI));
            requestFocus(et_add_assignment_I);
            return false;
        }else {
            Str_AssignmentI=et_add_assignment_I.getText().toString().trim();
            til_add_assignment_I.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateAssignmentII() {
        if (et_add_assignment_II.getText().toString().trim().isEmpty()) {
            til_add_assignment_II.setError(getString(R.string.lbl_msg_blank_assignmentII));
            requestFocus(et_add_assignment_II);
            return false;
        }else if(et_add_assignment_II.getText().length()<3  ){
            til_add_assignment_II.setError(getString(R.string.lbl_msg_valid_assignmentII));
            requestFocus(et_add_assignment_II);
            return false;
        }else {
            Str_AssignmentII=et_add_assignment_II.getText().toString().trim();
            til_add_assignment_II.setErrorEnabled(false);
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
                case R.id.et_add_assignment_I:
                    validateAssignmentI();
                    break;
                case R.id.et_add_assignment_II:
                    validateAssignmentII();
                    break;
                default:
                    break;

            }
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

    AdapterView.OnItemSelectedListener _OnSubjectItemSelectedListener =  new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id)
        {
            TAG = "_OnSubjectItem";
            Log.d(MODULE, TAG);
            try
            {
                if(position>0)
                {
                    Log.d(MODULE, TAG + " Spinner Subject : " + position);
                    Str_SubjectId=mListSubject.get(position-1).getID();
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

    View.OnClickListener _onClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            switch (view.getId())
            {
                case R.id.btn_select_date:
                    selectDate();
                    break;
                case R.id.btn_add_homework:
                    if(IsValid())
                    {
                    addHomeWork();
                    }
                    else AppUtils.showDialog(mActivity, getResources().getString(R.string.lbl_msg_validation));
                    break;
                default:
                    break;
            }
        }
    };



    @Override
    public void onClassListReceived()
    {
        TAG = "onClassListReceived";
        Log.d(MODULE, TAG);
        getClassList();
        showClassList();

    }

    @Override
    public void onClassListReceivedError(String Str_Msg)
    {
        TAG = "onClassListReceivedError";
        Log.d(MODULE, TAG);
        AppUtils.showDialog(mActivity, Str_Msg);
    }

    @Override
    public void onSectionListReceived()
    {
        TAG = "onSectionListReceived";
        Log.d(MODULE, TAG);
        getSectionList();
        showSectionList();
    }

    @Override
    public void onSectionListReceivedError(String Str_Msg)
    {
        TAG = "onSectionListReceivedError";
        Log.d(MODULE, TAG);
        AppUtils.showDialog(mActivity, Str_Msg);
    }

    @Override
    public void onSubjectListReceived()
    {
        TAG = "onSubjectListReceived";
        Log.d(MODULE, TAG);
        getSubjectList();
        showSubjectList();
    }

    @Override
    public void onSubjectListReceivedError(String Str_Msg)
    {
        TAG = "onSubjectListReceivedError";
        Log.d(MODULE, TAG);
        AppUtils.showDialog(mActivity, Str_Msg);
    }

    @Override
    public void onAddHomeWorkReceived(String Str_Msg)
    {
        TAG = "onAddHomeWorkReceived";
        Log.d(MODULE, TAG);
        try
        {
            AppUtils.hideProgressDialog();
            AppUtils.showDialog(mActivity,Str_Msg);
            mManager.popBackStack();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    @Override
    public void onAddHomeWorkReceivedError(String Str_Msg)
    {
        TAG = "onAddHomeWorkReceivedError";
        Log.d(MODULE, TAG);
        AppUtils.hideProgressDialog();
        AppUtils.showDialog(mActivity, Str_Msg);
    }

    public void getBundle()
    {
        TAG = "getBundle";
        Log.d(MODULE, TAG);
        try
        {
            mMode = mBundle.getInt(AppUtils.B_MODE);
            cHomework = mBundle.getParcelable(AppUtils.B_HOMEWORK);
            Str_AssignmentI = cHomework.getAssignment_I();
            Str_AssignmentII = cHomework.getAssignment_II();
            Str_HomeWorkId = cHomework.getHomeWorkId();
            Str_SubjectName=cHomework.getSubjectName();
            Str_SubjectId=cHomework.getSubjectId();
            Str_ClassId=cHomework.getClassId();
            Str_SectionId=cHomework.getSectionId();
            Str_Date=cHomework.getHomeWorkDate();

            Log.d(MODULE, TAG + " ClassId : " + Str_ClassId);
            Log.d(MODULE, TAG + " Str_SectionId : " + Str_SectionId);
        }
        catch (Exception ex)
        {

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
                responseCommon = (CommonList_Response) AppUtils.fromJson(Str_Json, new TypeToken<CommonList_Response>() {}.getType());
                mListSubject = responseCommon.getCclass();
                Log.d(MODULE, TAG + " mListSubject : " + mListSubject.size());
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
            if(mMode == AppUtils.MODE_UPDATE)
            {
                spinner_class.setSelection(AppUtils.getPosition(mListClass,Str_ClassId));
            }
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
            if(mMode == AppUtils.MODE_UPDATE)
            {
                spinner_section.setSelection(AppUtils.getPosition(mListSection,Str_SectionId));
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void showSubjectList()
    {
        TAG = "showSubjectList";
        Log.d(MODULE, TAG);
        try
        {
            String[] items = AppUtils.getArray(mListSubject,getString(R.string.lbl_select_subject));
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity,android.R.layout.simple_spinner_item,items);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_subject.setAdapter(adapter);
            if(mMode==AppUtils.MODE_UPDATE)
            {
                spinner_subject.setSelection(AppUtils.getPosition(mListSubject,Str_SubjectId));
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

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
        DialogFragment newFragment = new SelectDateFragment(Fragment_Add_HomeWork.this);
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

        if(validateAssignmentI() && validateAssignmentII()==true && !Str_Date.equals("") && !Str_SectionId.equals("") && !Str_SubjectId.equals("") && !Str_ClassId.equals("")) IsValid=true;
        else IsValid=false;

        return IsValid;
    }

    public void addHomeWork()
    {
        TAG = "addHomeWork";
        Log.d(MODULE, TAG);

        AppUtils.showProgressDialog(mActivity);
        new AddHomeWork(Str_Url,this,homework_PayLoad()).addHomeWork();

    }

    public JSONObject homework_PayLoad()
    {
        TAG = "homework_PayLoad";
        Log.d(MODULE, TAG);

        JSONObject obj = new JSONObject();
        try
        {
            obj.put("UserId",Str_Id);
            obj.put("ClassId",Str_ClassId);
            obj.put("SectionId",Str_SectionId);
            obj.put("SubjectId",Str_SubjectId);
            obj.put("HomeWorkDate",Str_Date);
            obj.put("Assignment_I",Str_AssignmentI);
            obj.put("Assignment_II",Str_AssignmentII);
            obj.put("Mode",Integer.toString(mMode));
            obj.put("HomeWorkId",Str_HomeWorkId);

        }
        catch (JSONException ex)
        {
            ex.printStackTrace();
        }
        return obj;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        TAG = "onOptionsItemSelected";
        Log.d(MODULE, TAG);

        switch (item.getItemId())
        {
            case android.R.id.home:
                FragmentDrawer.mDrawerLayout.closeDrawer(GravityCompat.START);
                mManager = mActivity.getSupportFragmentManager();
                mManager.popBackStack();
                return true;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
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

}
