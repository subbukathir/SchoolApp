package com.daemon.oxfordschool.fragment;

/**
 * Created by Ravi on 29/07/15.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.daemon.oxfordschool.adapter.HomeWorkAdapter;
import com.daemon.oxfordschool.asyncprocess.ClassList_Process;
import com.daemon.oxfordschool.asyncprocess.GetHomeWorkList;
import com.daemon.oxfordschool.asyncprocess.SectionList_Process;
import com.daemon.oxfordschool.classes.CHomework;
import com.daemon.oxfordschool.classes.Common_Class;
import com.daemon.oxfordschool.classes.User;
import com.daemon.oxfordschool.components.RecycleEmptyErrorView;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.listeners.ClassListListener;
import com.daemon.oxfordschool.listeners.DateSetListener;
import com.daemon.oxfordschool.listeners.HomeWorkListListener;
import com.daemon.oxfordschool.listeners.Homework_List_Item_Click_Listener;
import com.daemon.oxfordschool.listeners.SectionListListener;
import com.daemon.oxfordschool.response.CommonList_Response;
import com.daemon.oxfordschool.response.HomeWorkList_Response;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Fragment_HomeWork_Staff extends Fragment implements ClassListListener,
        SectionListListener,HomeWorkListListener,Homework_List_Item_Click_Listener,DateSetListener
{

    public static String MODULE = "Fragment_HomeWork_Staff ";
    public static String TAG = "";

    TextView tv_lbl_class,tv_lbl_section,tv_lbl_select_date,text_view_empty;
    Button btn_select_date;
    RelativeLayout layout_empty;
    RecycleEmptyErrorView recycler_view;
    RecyclerView.LayoutManager mLayoutManager;
    Spinner spinner_class,spinner_section;
    SharedPreferences mPreferences;
    User mUser;

    ArrayList<Common_Class> mListClass =new ArrayList<Common_Class>();
    ArrayList<Common_Class> mListSection =new ArrayList<Common_Class>();
    ArrayList<CHomework> mListHomeWork =new ArrayList<CHomework>();

    CommonList_Response responseCommon;
    HomeWorkList_Response response;

    AppCompatActivity mActivity;
    String Str_Id="";
    private Font font= MyApplication.getInstance().getFontInstance();
    String Str_ClassId,Str_SectionId="",Str_Date="";

    String Str_HomeWorkList_Url = ApiConstants.HOMEWORK_LIST_URL;

    public Fragment_HomeWork_Staff()
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
            getProfile();
            getClassList();
            getSectionList();
            Str_Date=GetTodayDate();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_homework_staff, container, false);
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

            layout_empty = (RelativeLayout) view.findViewById(R.id.layout_empty);
            text_view_empty = (TextView) view.findViewById(R.id.text_view_empty);

            tv_lbl_select_date = (TextView) view.findViewById(R.id.tv_lbl_select_date);
            btn_select_date = (Button) view.findViewById(R.id.btn_select_date);

            spinner_class = (Spinner) view.findViewById(R.id.spinner_class);
            spinner_section = (Spinner) view.findViewById(R.id.spinner_section);
            recycler_view = (RecycleEmptyErrorView) view.findViewById(R.id.recycler_view_homework);
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
            tv_lbl_class.setTypeface(font.getHelveticaRegular());
            tv_lbl_section.setTypeface(font.getHelveticaRegular());
            tv_lbl_select_date.setTypeface(font.getHelveticaRegular());
            btn_select_date.setTypeface(font.getHelveticaRegular());
            text_view_empty.setTypeface(font.getHelveticaRegular());
            mLayoutManager = new LinearLayoutManager(getActivity());
            recycler_view.setLayoutManager(mLayoutManager);
            spinner_class.setOnItemSelectedListener(_OnClassItemSelectedListener);
            spinner_section.setOnItemSelectedListener(_OnSectionItemSelectedListener);
            btn_select_date.setOnClickListener(_OnClickListener);
            btn_select_date.setText(Str_Date);
            StringBuilder Str_EmptyMessage = new StringBuilder();
            Str_EmptyMessage.append(getString(R.string.lbl_no_homework)).append(" ");
            Str_EmptyMessage.append(Str_Date);
            text_view_empty.setText(Str_EmptyMessage.toString());

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

    public void getHomeWorksFromService(String Str_Date)
    {
        TAG = "getHomeWorksFromService";
        Log.d(MODULE, TAG);
        try
        {
            new GetHomeWorkList(Str_HomeWorkList_Url,Payload_HomeWork(Str_Date),this).getHomeWorks();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    View.OnClickListener _OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            selectDate(view);
        }
    };

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
    public void onHomeWorkListReceived() {
        TAG = "onHomeWorkListReceived";
        Log.d(MODULE, TAG);
        try
        {
            getHomeWorksList();
            showHomeWorkList();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onHomeWorkListReceivedError(String Str_Msg) {
        TAG = "onHomeWorkListReceivedError";
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
    public void onHomeWorkListItemClicked(int position) {

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

    public void getHomeWorksList()
    {
        TAG = "getHomeWorksList";
        Log.d(MODULE, TAG);
        try
        {
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            String Str_Json = mPreferences.getString(AppUtils.SHARED_HOMEWORK_LIST,"");
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
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void showHomeWorkList()
    {
        TAG = "showHomeWorkList";
        Log.d(MODULE, TAG);
        try
        {
            if(mListHomeWork.size()>0)
            {
                HomeWorkAdapter adapter = new HomeWorkAdapter(mListHomeWork,this);
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

    public JSONObject Payload_HomeWork(String Str_Date)
    {
        TAG = "Payload";
        Log.d(MODULE, TAG);

        JSONObject obj = new JSONObject();
        try {
            obj.put("ClassId", Str_Date);
            obj.put("SectionId",Str_SectionId);
            obj.put("HomeWorkDate", Str_Date);
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

    public void selectDate(View view) {
        DialogFragment newFragment = new SelectDateFragment(Fragment_HomeWork_Staff.this);
        newFragment.show(mActivity.getSupportFragmentManager(), "DatePicker");
    }

    public void populateSetDate(int year, int month, int day) {
        Str_Date = year + "-" + month + "-"+day;
        getHomeWorksFromService(Str_Date);
    }

}
