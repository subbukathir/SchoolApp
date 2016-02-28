package com.daemon.oxfordschool.fragment;

/**
 * Created by Ravi on 29/07/15.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.Utils.Font;
import com.daemon.oxfordschool.asyncprocess.ClassList_Process;
import com.daemon.oxfordschool.asyncprocess.GetAttendance;
import com.daemon.oxfordschool.asyncprocess.GetStudentList;
import com.daemon.oxfordschool.asyncprocess.SectionList_Process;
import com.daemon.oxfordschool.classes.CResult;
import com.daemon.oxfordschool.classes.Common_Class;
import com.daemon.oxfordschool.classes.StudentAttendance;
import com.daemon.oxfordschool.classes.User;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.listeners.AttendanceListener;
import com.daemon.oxfordschool.listeners.Attendance_List_Item_Click_Listener;
import com.daemon.oxfordschool.listeners.ClassListListener;
import com.daemon.oxfordschool.listeners.DateSetListener;
import com.daemon.oxfordschool.listeners.SectionListListener;
import com.daemon.oxfordschool.listeners.StudentsListListener;
import com.daemon.oxfordschool.response.Attendance_Response;
import com.daemon.oxfordschool.response.CommonList_Response;
import com.daemon.oxfordschool.response.StudentsList_Response;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Fragment_Attendance_Staff extends Fragment implements ClassListListener,StudentsListListener,
        SectionListListener,AttendanceListener,Attendance_List_Item_Click_Listener,DateSetListener
{

    public static String MODULE = "Fragment_Attendance_Staff ";
    public static String TAG = "";

    Spinner spinner_class,spinner_section;
    SharedPreferences mPreferences;
    Toolbar mToolbar;
    User mUser;
    int mClassListPosition=0,mSectionListPosition=0;
    Button btn_select_date,btn_take_attendance,btn_view_attendance;
    ArrayList<Common_Class> mListClass =new ArrayList<Common_Class>();
    ArrayList<Common_Class> mListSection =new ArrayList<Common_Class>();
    ArrayList<StudentAttendance> mListAttendance = new ArrayList<StudentAttendance>();
    ArrayList<CResult> mAttendanceResult =new ArrayList<CResult>();
    ArrayList<User> mListStudents =new ArrayList<User>();
    Integer mSuccess;
    CommonList_Response responseCommon;
    StudentsList_Response response;
    Attendance_Response attendance_response;
    StudentsList_Response studentListResponse;

    AppCompatActivity mActivity;
    String Str_Id="";
    private Font font= MyApplication.getInstance().getFontInstance();
    String Str_ClassId,Str_SectionId,Str_Date="";
    TextView tv_lbl_class,tv_lbl_section,tv_lbl_select_date,text_view_empty;
    String Str_StudentList_Url = ApiConstants.STUDENT_LIST;
    String Str_Attendance_Url = ApiConstants.ATTENDANCE_URL;
    FragmentManager mManager;
    boolean isTakeAttendancePressed=false;
    Bundle mSavedInstanceState;

    final static String ARG_CLASS_LIST_POSITION = "Class_List_Position";
    final static String ARG_SECTION_LIST_POSITION = "Section_List_Position";

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
            setHasOptionsMenu(true);
            setRetainInstance(false);
            mSavedInstanceState=savedInstanceState;
            getProfile();
            getClassList();
            getSectionList();
            Str_Date=GetTodayDate();
            ConvertedDate();
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
            tv_lbl_select_date = (TextView) view.findViewById(R.id.tv_lbl_select_date);
            btn_select_date = (Button) view.findViewById(R.id.btn_select_date);
            btn_take_attendance = (Button) view.findViewById(R.id.btn_take_attendance);
            btn_view_attendance = (Button) view.findViewById(R.id.btn_view_attendance);
            text_view_empty = (TextView) view.findViewById(R.id.text_view_empty);
            spinner_class = (Spinner) view.findViewById(R.id.spinner_class);
            spinner_section = (Spinner) view.findViewById(R.id.spinner_section);
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
                mClassListPosition=mSavedInstanceState.getInt(ARG_CLASS_LIST_POSITION);
                mSectionListPosition=mSavedInstanceState.getInt(ARG_SECTION_LIST_POSITION);
            }
            SetActionBar();
            if(mListClass.size()>0) showClassList();
            if(mListSection.size()>0) showSectionList();
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
                mToolbar.setTitle(R.string.lbl_attendance);
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
            tv_lbl_select_date.setTypeface(font.getHelveticaRegular());
            btn_select_date.setTypeface(font.getHelveticaRegular());
            btn_take_attendance.setTypeface(font.getHelveticaRegular());
            btn_view_attendance.setTypeface(font.getHelveticaRegular());
            btn_take_attendance.setEnabled(false);
            btn_view_attendance.setEnabled(false);
            btn_select_date.setOnClickListener(_OnClickListener);
            btn_take_attendance.setOnClickListener(_OnClickListener);
            btn_view_attendance.setOnClickListener(_OnClickListener);
            btn_select_date.setText(ConvertedDate());
            spinner_class.setOnItemSelectedListener(_OnClassItemSelectedListener);
            spinner_section.setOnItemSelectedListener(_OnSectionItemSelectedListener);
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
            Log.d(MODULE,TAG + " " + Str_Attendance_Url);
            AppUtils.showProgressDialog(mActivity);
            new GetAttendance(Str_Attendance_Url,Payload_Attendance(),Fragment_Attendance_Staff.this).getAttendance();
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
                    new GetStudentList(Str_StudentList_Url,Payload_StudentList(),Fragment_Attendance_Staff.this).getStudents();
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

    View.OnClickListener _OnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            switch (view.getId())
            {
                case R.id.btn_select_date:
                     selectDate(view);
                     break;
                case R.id.btn_take_attendance:
                     isTakeAttendancePressed=true;
                     getAttendanceFromService();
                     break;
                case R.id.btn_view_attendance:
                     isTakeAttendancePressed=false;
                     getAttendanceFromService();
                     break;
                default:
                     break;
            }
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
            btn_take_attendance.setEnabled(true);
            btn_view_attendance.setEnabled(true);
            getStudentsList();
            if(mListStudents.size()>0)
            {
                setDefaultAttendanceList();
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
            btn_take_attendance.setEnabled(false);
            btn_view_attendance.setEnabled(false);
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
                Log.d(MODULE, TAG + " mListStudents : " + mListStudents.size());
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onAttendanceReceived()
    {
        TAG = "onAttendanceReceived";
        Log.d(MODULE, TAG);

        try
        {
            AppUtils.hideProgressDialog();
            getAttendanceDetails();
            setAttendanceList();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    @Override
    public void onAttendanceReceivedError(String Str_Msg)
    {
        TAG = "onAttendanceReceivedError";
        Log.d(MODULE, TAG + "error " + Str_Msg);

        try
        {
            AppUtils.hideProgressDialog();
            mAttendanceResult.clear();
            setAttendanceList();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
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
                int count = mAttendanceResult.size();
                Log.d(MODULE, TAG + " ListSize of attendance : " + count);

                for (int i = 0; i < count; i++) {
                    final CResult mAttendance = mAttendanceResult.get(i);

                    Log.d(MODULE, TAG + " AttendanceId : " + mAttendance.getAttendanceId());
                    Log.d(MODULE, TAG + " AttendanceDate : " + mAttendance.getAttendanceDate());
                    Log.d(MODULE, TAG + " IsPresent : " + mAttendance.getIsPresent());
                    Log.d(MODULE, TAG + " IsAfterNoon : " + mAttendance.getIsAfterNoon());
                    Log.d(MODULE, TAG + " IsHalfDay : " + mAttendance.getIsHalfDay());
                }
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
            String[] items = AppUtils.getArray(mListSection,getString(R.string.lbl_select_section));
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity,android.R.layout.simple_spinner_item,items);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_section.setAdapter(adapter);
            spinner_section.setSelection(mSectionListPosition);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void setDefaultAttendanceList()
    {
        TAG = "setDefaultAttendanceList";
        Log.d(MODULE, TAG);
        try
        {
            mListAttendance = new ArrayList<StudentAttendance>();
            for(int i=0;i<mListStudents.size();i++)
            {
                StudentAttendance studentAttendance = new StudentAttendance();
                studentAttendance.setStudentId(mListStudents.get(i).getStudentId());
                studentAttendance.setFirstName(mListStudents.get(i).getFirstName());
                studentAttendance.setLastName(mListStudents.get(i).getLastName());
                studentAttendance.setParentId(mListStudents.get(i).getParentId());
                studentAttendance.setSelected(true);
                mListAttendance.add(studentAttendance);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void setAttendanceList()
    {
        TAG = "setAttendanceList";
        Log.d(MODULE, TAG);

        try
        {
            if(mAttendanceResult.size()>0)
            {
                int count = mListAttendance.size();
                boolean isSelected=false;
                String Str_Status="";
                if(count>0)
                {
                    if(isTakeAttendancePressed) AppUtils.DialogMessage(mActivity,getString(R.string.lbl_attendance_already_taken));
                    else
                    {
                        for(int i=0;i<count;i++)
                        {
                            for(int j=0;j<mAttendanceResult.size();j++)
                            {
                                if(mListAttendance.get(i).getStudentId().equals(mAttendanceResult.get(j).getStudentId()))
                                {
                                    Str_Status = mAttendanceResult.get(j).getIsPresent();
                                    if(Str_Status.equals("1")) isSelected=false;
                                    else isSelected=true;
                                    mListAttendance.get(i).setSelected(isSelected);
                                    mListAttendance.get(i).setStatus(Str_Status);
                                    mListAttendance.get(i).setAttendanceId(mAttendanceResult.get(j).getAttendanceId());
                                }
                            }
                        }
                        Goto_Fragment_Attendance_Add(AppUtils.MODE_UPDATE);
                    }
                }
            }
            else
            {
                if(isTakeAttendancePressed) Goto_Fragment_Attendance_Add(AppUtils.MODE_ADD);
                else AppUtils.DialogMessage(mActivity, getString(R.string.lbl_attendance_not_found));
            }

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public JSONObject Payload_StudentList()
    {
        TAG = "Payload";
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

    public JSONObject Payload_Attendance()
    {
        TAG = "Payload_Attendance";
        Log.d(MODULE,TAG);
        JSONObject obj = new JSONObject();
        try
        {
            obj.put("ClassId",Str_ClassId);
            obj.put("SectionId", Str_SectionId);
            obj.put("AttendanceDate", Str_Date);
        }
        catch (JSONException ex)
        {
            ex.printStackTrace();
        }
        Log.d(MODULE, TAG + " obj : " + obj.toString());
        return obj;
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

    public void selectDate(View view) {
        DialogFragment newFragment = new SelectDateFragment(Fragment_Attendance_Staff.this);
        newFragment.show(mActivity.getSupportFragmentManager(), "DatePicker");
    }

    public void populateSetDate(int year, int month, int day) {
        Str_Date = year + "-" + month + "-"+day;
        btn_select_date.setText(ConvertedDate());
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

    public void Goto_Fragment_Attendance_Add(int mMode)
    {
        TAG = "Goto_Fragment_Attendance_Add";
        Log.d(MODULE,TAG);
        try
        {
            mSavedInstanceState=getSavedState();
            Bundle Args=new Bundle();
            Args.putInt(AppUtils.B_MODE, mMode);
            Args.putString(AppUtils.B_USER_ID, Str_Id);
            Args.putString(AppUtils.B_CLASS_ID, Str_ClassId);
            Args.putString(AppUtils.B_SECTION_ID, Str_SectionId);
            Args.putString(AppUtils.B_DATE, Str_Date);
            Args.putParcelableArrayList(AppUtils.B_ATTENDANCE_LIST, mListAttendance);

            mManager = mActivity.getSupportFragmentManager();
            FragmentTransaction mTransaction = mManager.beginTransaction();
            Fragment_Attendance_Add fragment = new Fragment_Attendance_Add();
            fragment.setArguments(Args);
            mTransaction.replace(R.id.container_body, fragment, AppUtils.FRAGMENT_ADD_ATTENDANCE);
            mTransaction.addToBackStack(AppUtils.FRAGMENT_ADD_ATTENDANCE + "");
            mTransaction.commit();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

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
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        TAG = "onSaveInstanceState";
        Log.d(MODULE, TAG);
        mSavedInstanceState=getSavedState();
    }

    public Bundle getSavedState()
    {
        TAG = "getSavedState";
        Log.d(MODULE, TAG);

        Bundle outState = new Bundle();
        try
        {
            outState.putInt(ARG_CLASS_LIST_POSITION,spinner_class.getSelectedItemPosition());
            outState.putInt(ARG_SECTION_LIST_POSITION,spinner_section.getSelectedItemPosition());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }


        Log.d(MODULE, TAG);
        return outState;
    }

}
