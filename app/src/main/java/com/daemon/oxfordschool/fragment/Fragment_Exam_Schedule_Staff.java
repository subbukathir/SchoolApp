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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.Utils.Font;
import com.daemon.oxfordschool.activity.MainActivity;
import com.daemon.oxfordschool.adapter.ExamListAdapter;
import com.daemon.oxfordschool.asyncprocess.ClassList_Process;
import com.daemon.oxfordschool.asyncprocess.ExamTypeList_Process;
import com.daemon.oxfordschool.asyncprocess.GetExamList;
import com.daemon.oxfordschool.asyncprocess.SectionList_Process;
import com.daemon.oxfordschool.classes.CExam;
import com.daemon.oxfordschool.classes.Common_Class;
import com.daemon.oxfordschool.classes.User;
import com.daemon.oxfordschool.components.RecycleEmptyErrorView;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.listeners.ClassListListener;
import com.daemon.oxfordschool.listeners.ExamListListener;
import com.daemon.oxfordschool.listeners.ExamTypeListListener;
import com.daemon.oxfordschool.listeners.Exam_List_Item_Click_Listener;
import com.daemon.oxfordschool.listeners.SectionListListener;
import com.daemon.oxfordschool.response.CommonList_Response;
import com.daemon.oxfordschool.response.ExamList_Response;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Fragment_Exam_Schedule_Staff extends Fragment implements ClassListListener,
        SectionListListener,ExamTypeListListener,ExamListListener,Exam_List_Item_Click_Listener
{

    public static String MODULE = "Fragment_Exam_Schedule_Staff";
    public static String TAG = "";

    Toolbar mToolbar;
    TextView tv_lbl_class,tv_lbl_section,tv_lbl_select_exam_type,text_view_empty,tv_lbl_subject_name,tv_lbl_exam_date;
    Spinner spinner_class,spinner_section,spinner_exam_type;
    RelativeLayout layout_empty;
    LinearLayout layout_section;
    RecycleEmptyErrorView recycler_view;
    RecyclerView.LayoutManager mLayoutManager;

    SharedPreferences mPreferences;
    User mUser;

    ArrayList<Common_Class> mListClass =new ArrayList<Common_Class>();
    ArrayList<Common_Class> mListSection =new ArrayList<Common_Class>();
    ArrayList<CExam> mListExams =new ArrayList<CExam>();
    ArrayList<Common_Class> mListExamType =new ArrayList<Common_Class>();

    CommonList_Response responseCommon;
    CommonList_Response examListResponse;
    ExamList_Response response;

    AppCompatActivity mActivity;
    String Str_Id,Str_ClassId,Str_SectionId,Str_ExamTypeId="";
    int mMargin=0;int mMarginBottom=0;float mDensity=0;
    private Font font= MyApplication.getInstance().getFontInstance();
    String Str_ExamList_Url = ApiConstants.EXAM_LIST_URL;
    LinearLayout.LayoutParams params;

    public Fragment_Exam_Schedule_Staff()
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
            params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            mDensity =  mActivity.getResources().getDisplayMetrics().density;
            mMargin = (int) (mActivity.getResources().getDimension(R.dimen.space_layout_margin)); // mDensity);
            mMarginBottom = (int) (mActivity.getResources().getDimension(R.dimen.space_layout_margin_small)); // mDensity);
            params.setMargins(mMargin,mMargin ,mMargin,mMarginBottom);
            getProfile();
            getClassList();
            getSectionList();
            setHasOptionsMenu(true);
            new ExamTypeList_Process(mActivity,Fragment_Exam_Schedule_Staff.this).GetExamTypeList();
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
        View rootView = inflater.inflate(R.layout.fragment_exam_schedule_staff, container, false);
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
            layout_empty = (RelativeLayout) view.findViewById(R.id.layout_empty);
            text_view_empty = (TextView) view.findViewById(R.id.text_view_empty);

            tv_lbl_select_exam_type = (TextView) view.findViewById(R.id.tv_select_exam_type);
            spinner_exam_type = (Spinner) view.findViewById(R.id.spinner_exam_type);

            tv_lbl_subject_name = (TextView) view.findViewById(R.id.tv_lbl_subject_name);
            tv_lbl_exam_date = (TextView) view.findViewById(R.id.tv_lbl_exam_date);

            spinner_class = (Spinner) view.findViewById(R.id.spinner_class);
            spinner_section = (Spinner) view.findViewById(R.id.spinner_section);
            recycler_view = (RecycleEmptyErrorView) view.findViewById(R.id.recycler_view_exam_list);

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
            if(mListClass.size()>0) showClassList();
            if(mListSection.size()>0) showSectionList();
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
            layout_section.setVisibility(View.GONE);

            tv_lbl_class.setTypeface(font.getHelveticaRegular());
            tv_lbl_section.setTypeface(font.getHelveticaRegular());

            tv_lbl_select_exam_type.setLayoutParams(params);
            tv_lbl_select_exam_type.setPadding(mMargin,0,mMargin,0);
            tv_lbl_select_exam_type.setTypeface(font.getHelveticaRegular());
            tv_lbl_subject_name.setTypeface(font.getHelveticaBold());
            tv_lbl_exam_date.setTypeface(font.getHelveticaBold());

            text_view_empty.setTypeface(font.getHelveticaRegular());
            mLayoutManager = new LinearLayoutManager(getActivity());
            recycler_view.setLayoutManager(mLayoutManager);
            spinner_class.setOnItemSelectedListener(_OnClassItemSelectedListener);
            spinner_section.setOnItemSelectedListener(_OnSectionItemSelectedListener);
            spinner_exam_type.setOnItemSelectedListener(_OnItemSelectedListener);
            text_view_empty.setText(getString(R.string.lbl_no_exams));
            showSectionList();
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
                mToolbar.setTitle(R.string.lbl_exam_schedule);
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

    public void getExamListFromService()
    {
        TAG = "getExamListFromService";
        Log.d(MODULE, TAG);
        try
        {
            new GetExamList(Str_ExamList_Url,Payload_ExamList(),this).getExams();
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
                getExamListFromService();
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
    public void onExamListReceived() {
        TAG = "onExamTypeListReceived";
        Log.d(MODULE, TAG);
        try
        {
            getExamList();
            showExamList();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onExamListReceivedError(String Str_Msg) {
        TAG = "onClassListReceived";
        Log.d(MODULE, TAG);

        try
        {
            showEmptyView();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onExamListItemClicked(int position) {

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

    public void getExamList()
    {
        TAG = "getExamList";
        Log.d(MODULE, TAG);
        try
        {
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            String Str_Json = mPreferences.getString(AppUtils.SHARED_EXAM_LIST,"");
            Log.d(MODULE, TAG + " Str_Json : " + Str_Json);
            if(Str_Json.length()>0)
            {
                response = (ExamList_Response) AppUtils.fromJson(Str_Json, new TypeToken<ExamList_Response>(){}.getType());
                mListExams = response.getCexam();
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
            String[] items = null;
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

    public void showExamList()
    {
        TAG = "showExamList";
        Log.d(MODULE, TAG);
        try
        {
            if(mListExams.size()>0)
            {
                ExamListAdapter adapter = new ExamListAdapter(mListExams,this);
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

    public JSONObject Payload_ExamList()
    {
        TAG = "Payload_ExamList";
        Log.d(MODULE, TAG);

        JSONObject obj = new JSONObject();
        try {
            obj.put("ExamType", Str_ExamTypeId);
            obj.put("ClassId", Str_ClassId);
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
