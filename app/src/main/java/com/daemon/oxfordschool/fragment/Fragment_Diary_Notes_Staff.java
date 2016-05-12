package com.daemon.oxfordschool.fragment;

/**
 * Created by Ravi on 29/07/15.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
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
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.Utils.Font;
import com.daemon.oxfordschool.activity.MainActivity;
import com.daemon.oxfordschool.adapter.DiaryListAdapter;
import com.daemon.oxfordschool.asyncprocess.ClassList_Process;
import com.daemon.oxfordschool.asyncprocess.GetDiaryNotesList;
import com.daemon.oxfordschool.asyncprocess.GetStudentList;
import com.daemon.oxfordschool.asyncprocess.SectionList_Process;
import com.daemon.oxfordschool.classes.CHomework;
import com.daemon.oxfordschool.classes.Common_Class;
import com.daemon.oxfordschool.classes.User;
import com.daemon.oxfordschool.components.RecycleEmptyErrorView;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.listeners.ClassListListener;
import com.daemon.oxfordschool.listeners.DateSetListener;
import com.daemon.oxfordschool.listeners.DiaryNotesListListener;
import com.daemon.oxfordschool.listeners.Diary_List_Item_Click_Listener;
import com.daemon.oxfordschool.listeners.SectionListListener;
import com.daemon.oxfordschool.listeners.StudentsListListener;
import com.daemon.oxfordschool.response.CommonList_Response;
import com.daemon.oxfordschool.response.HomeWorkList_Response;
import com.daemon.oxfordschool.response.StudentsList_Response;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Fragment_Diary_Notes_Staff extends Fragment implements ClassListListener,SectionListListener,
        StudentsListListener,DiaryNotesListListener,Diary_List_Item_Click_Listener,DateSetListener
{

    public static String MODULE = "Fragment_Diary_Notes_Staff ";
    public static String TAG = "";

    TextView tv_lbl_class,tv_lbl_section,tv_lbl_select_date,tv_lbl_select_student,text_view_empty;
    Button btn_select_date;
    RelativeLayout layout_empty;
    RecycleEmptyErrorView recycler_view;
    RecyclerView.LayoutManager mLayoutManager;
    Spinner spinner_class,spinner_section,spinner_student;
    SharedPreferences mPreferences;
    Toolbar mToolbar;
    User mUser;
    FloatingActionButton fab_add_homework;

    ArrayList<Common_Class> mListClass =new ArrayList<Common_Class>();
    ArrayList<Common_Class> mListSection =new ArrayList<Common_Class>();
    ArrayList<CHomework> mListHomeWork =new ArrayList<CHomework>();
    ArrayList<User> mListStudent =new ArrayList<User>();

    CommonList_Response responseCommon;
    StudentsList_Response studentListresponse;
    HomeWorkList_Response response;
    CHomework cHomework;

    AppCompatActivity mActivity;
    Bundle mSavedInstanceState;
    String Str_Id="";
    private Font font= MyApplication.getInstance().getFontInstance();
    String Str_ClassId,Str_SectionId="",Str_Date="",Str_StudentId="";
    String Str_Url = ApiConstants.DIARY_NOTES_LIST_STAFF_URL;
    String Str_StudentList_Url = ApiConstants.STUDENT_LIST;

    int mClassListPosition=0,mSectionListPosition=0,mStudentListPosition=0;
    final static String ARG_CLASS_LIST_POSITION = "Class_List_Position";
    final static String ARG_SECTION_LIST_POSITION = "Section_List_Position";
    final static String ARG_STUDENT_LIST_POSITION = "Student_List_Position";

    public Fragment_Diary_Notes_Staff()
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
            Str_Date=GetTodayDate();
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
        View rootView = inflater.inflate(R.layout.fragment_diary_staff, container, false);
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

            layout_empty = (RelativeLayout) view.findViewById(R.id.layout_empty);
            text_view_empty = (TextView) view.findViewById(R.id.text_view_empty);
            fab_add_homework = (FloatingActionButton) view.findViewById(R.id.fab);
            tv_lbl_select_date = (TextView) view.findViewById(R.id.tv_lbl_select_date);
            btn_select_date = (Button) view.findViewById(R.id.btn_select_date);

            spinner_class = (Spinner) view.findViewById(R.id.spinner_class);
            spinner_section = (Spinner) view.findViewById(R.id.spinner_section);
            spinner_student = (Spinner) view.findViewById(R.id.spinner_student);

            recycler_view = (RecycleEmptyErrorView) view.findViewById(R.id.recycler_view_diary);
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
                mStudentListPosition=mSavedInstanceState.getInt(ARG_STUDENT_LIST_POSITION);
                Log.d(MODULE,TAG + "mClassListPosition" + mClassListPosition);
                Log.d(MODULE,TAG + "mSectionListPosition" + mSectionListPosition);
                Log.d(MODULE,TAG + "mStudentListPosition" + mStudentListPosition);
            }
            if(mListClass.size()>0) showClassList();
            if(mListSection.size()>0) showSectionList();
            if(mListStudent.size()>0) showStudentsList();
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
            tv_lbl_select_student.setTypeface(font.getHelveticaRegular());
            btn_select_date.setTypeface(font.getHelveticaRegular());
            text_view_empty.setTypeface(font.getHelveticaRegular());

            mLayoutManager = new LinearLayoutManager(getActivity());
            recycler_view.setLayoutManager(mLayoutManager);
            layout_empty.setVisibility(View.VISIBLE);
            recycler_view.setVisibility(View.GONE);

            spinner_class.setOnItemSelectedListener(_OnClassItemSelectedListener);
            spinner_section.setOnItemSelectedListener(_OnSectionItemSelectedListener);
            spinner_student.setOnItemSelectedListener(_OnStudentItemSelectedListener);
            btn_select_date.setOnClickListener(_OnClickListener);
            btn_select_date.setText(ConvertedDate());

            text_view_empty.setText(getString(R.string.lbl_select_class_date));
            fab_add_homework.setOnClickListener(_OnClickListener);
            fab_add_homework.setVisibility(View.VISIBLE);
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
                mToolbar.setTitle(R.string.lbl_diary);
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

    public void getDiaryNotesFromService(String Str_Date)
    {
        TAG = "getDiaryNotesFromService";
        Log.d(MODULE, TAG);
        try
        {
            if(mUser.getUserType().equals(ApiConstants.ADMIN))
                Str_Url = ApiConstants.DIARY_NOTES_LIST_URL;
            else if(mUser.getUserType().equals(ApiConstants.STAFF))
                Str_Url = ApiConstants.DIARY_NOTES_LIST_STAFF_URL;
            Log.d(MODULE, TAG + " Str_Url : " + Str_Url);
            new GetDiaryNotesList(Str_Url,Payload_DiaryNotes(Str_Date),this).getDiaryNotes();
            AppUtils.showProgressDialog(mActivity);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    View.OnClickListener _OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view)
        {
            switch (view.getId())
            {
                case R.id.fab:
                     goto_Fragment_AddDiaryNotes();
                     break;
                case R.id.btn_select_date:
                     selectDate();
                     break;
                default:
                     break;
            }
        }
    };

    public void goto_Fragment_AddDiaryNotes()
    {
        TAG = "goto_Fragment_AddDiaryNotes";
        Log.d(MODULE, TAG);

        FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
        Fragment fragment=new Fragment_Add_DiaryNotes();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment,AppUtils.FRAGMENT_ADD_DIARY_NOTES);
        fragmentTransaction.addToBackStack(AppUtils.FRAGMENT_ADD_DIARY_NOTES);
        fragmentTransaction.commit();
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
                    new GetStudentList(Str_StudentList_Url,Payload_Student_List(),Fragment_Diary_Notes_Staff.this).getStudents();
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
    public void onDiaryNotesListReceived() {
        TAG = "onDiaryNotesListReceived";
        Log.d(MODULE, TAG);
        try
        {
            AppUtils.hideProgressDialog();
            getDiaryNotesList();
            showDiaryNotesList();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onDiaryNotesListReceivedError(String Str_Msg) {
        TAG = "onDiaryNotesListReceivedError";
        Log.d(MODULE, TAG);
        try
        {
            AppUtils.hideProgressDialog();
            text_view_empty.setText(Str_Msg);
            showEmptyView();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onDiaryListItemClicked(int position)
    {
        TAG = "onHomeWorkListItemClicked";
        Log.d(MODULE, TAG);

        if(mUser.getUserType().equals(ApiConstants.STAFF)) gotoFragmentUpdate(position);
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

    public void getDiaryNotesList()
    {
        TAG = "getDiaryNotesList";
        Log.d(MODULE, TAG);
        try
        {
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            String Str_Json = mPreferences.getString(AppUtils.SHARED_DIARY_NOTES_LIST,"");
            Log.d(MODULE, TAG + " Str_Json : " + Str_Json);
            if(Str_Json.length()>0)
            {
                response = (HomeWorkList_Response) AppUtils.fromJson(Str_Json, new TypeToken<HomeWorkList_Response>(){}.getType());
                mListHomeWork = response.getChomework();
                Log.d(MODULE, TAG + " mListHomeWork : " + mListHomeWork.size());
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
            if(mListStudent.size()>0)
            {
                String[] items = AppUtils.getStudentArray(mListStudent, getString(R.string.lbl_select_student));
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity,android.R.layout.simple_spinner_item,items);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_student.setAdapter(adapter);
                spinner_student.setSelection(mStudentListPosition);
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

    public void showDiaryNotesList()
    {
        TAG = "showDiaryNotesList";
        Log.d(MODULE, TAG);
        try
        {
            if(mListHomeWork.size()>0)
            {
                DiaryListAdapter adapter = new DiaryListAdapter(mListHomeWork,this);
                recycler_view.setAdapter(adapter);
                layout_empty.setVisibility(View.GONE);
                recycler_view.setVisibility(View.VISIBLE);
            }
            else
            {
                text_view_empty.setText(getString(R.string.lbl_no_diary_notes));
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

    public JSONObject Payload_DiaryNotes(String Str_Date)
    {
        TAG = "Payload_DiaryNotes";
        Log.d(MODULE, TAG);

        JSONObject obj = new JSONObject();
        try {
            obj.put("UserId", Str_Id);
            obj.put("ClassId", Str_ClassId);
            obj.put("SectionId",Str_SectionId);
            obj.put("StudentId",Str_StudentId);
            obj.put("HomeWorkDate", Str_Date);
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

    public void populateSetDate(int year, int month, int day) {
        try
        {
            Str_Date = year + "-" + month + "-"+day;
            btn_select_date.setText(ConvertedDate());
            getDiaryNotesFromService(Str_Date);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    public void gotoFragmentUpdate(int position)
    {
        TAG = "gotoFragmentUpdate";
        Log.d(MODULE, TAG);

        if(mListHomeWork.size()>0)
        {
            mSavedInstanceState=getSavedState();
            cHomework = mListHomeWork.get(position);
            cHomework.setStudentId(Str_StudentId);
            Log.d(MODULE, TAG + "values of list " + cHomework.getClassId() + cHomework.getClassName());
            Log.d(MODULE, TAG + "getSectionId of list " + cHomework.getSectionId());
            Log.d(MODULE, TAG + "getSectionId of list " + cHomework.getHomeWorkDate());
            Log.d(MODULE, TAG + "getSectionId of list " + cHomework.getAssignment_I());

            Bundle  mBundle = new Bundle();

            mBundle.putParcelable(AppUtils.B_DIARY,cHomework);
            mBundle.putInt(AppUtils.B_MODE, AppUtils.MODE_UPDATE);

            Fragment mFragment = new Fragment_Add_DiaryNotes();
            FragmentManager mManager = mActivity.getSupportFragmentManager();
            FragmentTransaction mTransaction = mManager.beginTransaction();
            mFragment.setArguments(mBundle);
            mTransaction.replace(R.id.container_body, mFragment,AppUtils.FRAGMENT_ADD_DIARY_NOTES);
            mTransaction.addToBackStack(AppUtils.FRAGMENT_ADD_DIARY_NOTES + "");
            mTransaction.commit();
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
            outState.putInt(ARG_STUDENT_LIST_POSITION,spinner_student.getSelectedItemPosition());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }


        Log.d(MODULE, TAG);
        return outState;
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
