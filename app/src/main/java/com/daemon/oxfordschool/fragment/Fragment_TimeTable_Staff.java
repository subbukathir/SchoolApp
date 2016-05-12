package com.daemon.oxfordschool.fragment;

/**
 * Created by Ravi on 29/07/15.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.GridView;
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
import com.daemon.oxfordschool.adapter.TimeTableAdapter;
import com.daemon.oxfordschool.asyncprocess.AllSubjectList_Process;
import com.daemon.oxfordschool.asyncprocess.ClassList_Process;
import com.daemon.oxfordschool.asyncprocess.GetTimeTable;
import com.daemon.oxfordschool.asyncprocess.SectionList_Process;
import com.daemon.oxfordschool.classes.Common_Class;
import com.daemon.oxfordschool.classes.TimeTable;
import com.daemon.oxfordschool.classes.TimeTableItem;
import com.daemon.oxfordschool.classes.User;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.listeners.ClassListListener;
import com.daemon.oxfordschool.listeners.SectionListListener;
import com.daemon.oxfordschool.listeners.SubjectListListener;
import com.daemon.oxfordschool.listeners.TimeTableListener;
import com.daemon.oxfordschool.response.CommonList_Response;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Fragment_TimeTable_Staff extends Fragment implements TimeTableListener,SubjectListListener,
        ClassListListener,SectionListListener
{

    public static String MODULE = "Fragment_TimeTable_Staff";
    public static String TAG = "";

    public static int HEADER=0;
    public static int NON_HEADER=1;


    int mSelectedPosition;
    Spinner spinner_class,spinner_section;
    TextView tv_lbl_class,tv_lbl_section,text_view_empty;
    SharedPreferences mPreferences;
    User mUser,mSelectedUser;
    GridView grid_view;
    RelativeLayout layout_empty;
    Toolbar mToolbar;

    ArrayList<Common_Class> mSubjectList;
    ArrayList<User> mListStudents =new ArrayList<User>();
    ArrayList<Common_Class> mListClass =new ArrayList<Common_Class>();
    ArrayList<Common_Class> mListSection =new ArrayList<Common_Class>();
    ArrayList<TimeTableItem> mListSubjectId = new ArrayList<TimeTableItem>();
    TimeTable mTimeTable;

    CommonList_Response responseCommon;
    CommonList_Response response;

    AppCompatActivity mActivity;
    String Str_Id="",Str_ClassId="",Str_SectionId="";
    private Font font= MyApplication.getInstance().getFontInstance();
    String Str_StudentList_Url = ApiConstants.STUDENT_LIST;
    String Str_TimeTable_Url = ApiConstants.TIME_TABLE_URL;

    TimeTableAdapter adapter;

    public Fragment_TimeTable_Staff()
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
            getClassList();
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
        View rootView = inflater.inflate(R.layout.fragment_timetable_staff, container, false);
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
            grid_view=(GridView) view.findViewById(R.id.grid_view);
            layout_empty = (RelativeLayout) view.findViewById(R.id.layout_empty);
            text_view_empty = (TextView) view.findViewById(R.id.text_view_empty);
            spinner_class = (Spinner) view.findViewById(R.id.spinner_class);
            spinner_section = (Spinner) view.findViewById(R.id.spinner_section);
            setProperties();
            SetActionBar();
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
            text_view_empty.setTypeface(font.getHelveticaRegular());
            spinner_class.setOnItemSelectedListener(_OnClassItemSelectedListener);
            spinner_section.setOnItemSelectedListener(_OnSectionItemSelectedListener);
            layout_empty.setVisibility(View.VISIBLE);
            grid_view.setVisibility(View.GONE);
            showSectionList();
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
            if(mListClass.size() > 0) showClassList();
            if(mListSection.size() > 0) showSectionList();
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
                mToolbar.setTitle(R.string.lbl_time_table);
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

    public void getTimeTableFromService()
    {
        TAG = "getTimeTableFromService";
        Log.d(MODULE, TAG);
        try
        {
            new GetTimeTable(Str_TimeTable_Url,Payload_TimeTable(),this).getTimeTable();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    AdapterView.OnItemSelectedListener _OnClassItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            if(position>0)
            {
                Str_ClassId=mListClass.get(position-1).getID();
                getSectionListFromService();
                getSubjectsListFromService();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    AdapterView.OnItemSelectedListener _OnSectionItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            if(position > 0)
            {
                Str_SectionId = mListSection.get(position-1).getID();
                if(mSubjectList.size()>0) getTimeTableFromService();
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
            new AllSubjectList_Process(this).GetSubjectsList();
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
            text_view_empty.setText(Str_Msg);
            showEmptyView();
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
        TAG = "getSectionListFromService";
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
            String[] items =null;
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

    public void showTimeTable()
    {
        TAG = "showTimeTable";
        Log.d(MODULE, TAG);
        try
        {
            if(mListSubjectId.size()>0)
            {
                adapter = new TimeTableAdapter(mActivity,mListSubjectId,mSubjectList);
                grid_view.setAdapter(adapter);
                grid_view.setVisibility(View.VISIBLE);
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
        }
        catch (Exception ex)
        {

        }
    }

    @Override
    public void onSubjectListReceivedError(String Str_Msg) {

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

    public void showEmptyView()
    {
        TAG = "showEmptyView";
        Log.d(MODULE, TAG);
        AppUtils.hideProgressDialog();
        try
        {
            layout_empty.setVisibility(View.VISIBLE);
            grid_view.setVisibility(View.GONE);
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

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_settings).setVisible(false);
        menu.findItem(R.id.action_list_view).setVisible(false);
        menu.findItem(R.id.action_chart_view).setVisible(false);
        menu.findItem(R.id.action_help).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

}
