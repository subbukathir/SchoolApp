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
import com.daemon.oxfordschool.adapter.PaymentAdapter;
import com.daemon.oxfordschool.asyncprocess.ClassList_Process;
import com.daemon.oxfordschool.asyncprocess.ExamTypeList_Process;
import com.daemon.oxfordschool.asyncprocess.GetFeesTermList;
import com.daemon.oxfordschool.asyncprocess.GetPaymentDetail;
import com.daemon.oxfordschool.asyncprocess.SectionList_Process;
import com.daemon.oxfordschool.classes.CFees;
import com.daemon.oxfordschool.classes.Common_Class;
import com.daemon.oxfordschool.classes.User;
import com.daemon.oxfordschool.components.RecycleEmptyErrorView;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.listeners.ClassListListener;
import com.daemon.oxfordschool.listeners.PaymentDetailsListener;
import com.daemon.oxfordschool.listeners.SectionListListener;
import com.daemon.oxfordschool.listeners.Term_List_Listener;
import com.daemon.oxfordschool.response.CommonList_Response;
import com.daemon.oxfordschool.response.FeesList_Response;
import com.daemon.oxfordschool.response.StudentsList_Response;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Fragment_PaymentDetail_Staff extends Fragment implements ClassListListener,SectionListListener,
        Term_List_Listener,PaymentDetailsListener
{

    public static String MODULE = "Fragment_PaymentDetail_Staff";
    public static String TAG = "";

    TextView tv_lbl_class,tv_lbl_section,text_view_empty,tv_select_term_fees,tv_lbl_name,tv_lbl_status;
    Spinner spinner_class,spinner_section,spinner_term_fees;
    int mClassListPosition=0,mSectionListPosition=0;
    RelativeLayout layout_empty;
    LinearLayout layout_section;
    RecycleEmptyErrorView recycler_view;
    RecyclerView.LayoutManager mLayoutManager;
    Toolbar mToolbar;

    SharedPreferences mPreferences;
    User mUser;

    ArrayList<Common_Class> mListClass =new ArrayList<Common_Class>();
    ArrayList<Common_Class> mListSection =new ArrayList<Common_Class>();
    ArrayList<Common_Class> mListTermFees =new ArrayList<Common_Class>();
    ArrayList<CFees> mListPaymentDetails =new ArrayList<CFees>();

    CommonList_Response responseCommon;
    StudentsList_Response studentListResponse;
    FeesList_Response feesListResponse;

    AppCompatActivity mActivity;
    String Str_Id,Str_ClassId,Str_SectionId,Str_TermFeesId="";
    int mMargin=0;int mMarginBottom=0;float mDensity=0;
    private Font font= MyApplication.getInstance().getFontInstance();
    String Str_PaymentDetail_Url = ApiConstants.PAYMENT_DETAIL_URL;
    Fragment mFragment;

    public Fragment_PaymentDetail_Staff()
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
            mDensity =  mActivity.getResources().getDisplayMetrics().density;
            mMargin = (int) (mActivity.getResources().getDimension(R.dimen.space_layout_margin)); // mDensity);
            mMarginBottom = (int) (mActivity.getResources().getDimension(R.dimen.space_layout_margin_small)); // mDensity);
            mFragment = this;
            getProfile();
            getClassList();
            setHasOptionsMenu(true);
            new GetFeesTermList(this).getFeesTermList();
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
        View rootView = inflater.inflate(R.layout.fragment_paymentdetail_staff, container, false);
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
            tv_select_term_fees = (TextView) view.findViewById(R.id.tv_select_term_fees);
            spinner_term_fees = (Spinner) view.findViewById(R.id.spinner_term_fees);
            tv_lbl_name = (TextView) view.findViewById(R.id.tv_lbl_name);
            tv_lbl_status = (TextView) view.findViewById(R.id.tv_lbl_status);
            spinner_class = (Spinner) view.findViewById(R.id.spinner_class);
            spinner_section = (Spinner) view.findViewById(R.id.spinner_section);
            recycler_view = (RecycleEmptyErrorView) view.findViewById(R.id.recycler_view_payment_detail);
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
            layout_empty.setVisibility(View.VISIBLE);
            text_view_empty.setText(getString(R.string.lbl_please_select_term_class));

            tv_lbl_class.setTypeface(font.getHelveticaRegular());
            tv_lbl_section.setTypeface(font.getHelveticaRegular());

            tv_select_term_fees.setTypeface(font.getHelveticaRegular());
            tv_lbl_name.setTypeface(font.getHelveticaBold());
            tv_lbl_status.setTypeface(font.getHelveticaBold());

            text_view_empty.setTypeface(font.getHelveticaRegular());
            mLayoutManager = new LinearLayoutManager(getActivity());
            recycler_view.setLayoutManager(mLayoutManager);

            spinner_class.setOnItemSelectedListener(_OnClassItemSelectedListener);
            spinner_section.setOnItemSelectedListener(_OnSectionItemSelectedListener);
            spinner_term_fees.setOnItemSelectedListener(_OnItemSelectedListener);
            text_view_empty.setText(getString(R.string.lbl_no_result));

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
                mToolbar.setTitle(R.string.lbl_fees_detail);
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
                    {
                        getSectionListFromService();
                        new ExamTypeList_Process(mActivity,Fragment_PaymentDetail_Staff.this).GetExamTypeList();
                    }

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
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

            if(position>0)
            {
                Str_TermFeesId = mListTermFees.get(position-1).getID();
                if(!Str_ClassId.equals("") && (!Str_SectionId.equals("")))getPaymentDetailFromService();
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

    public void getPaymentDetailFromService()
    {
        TAG = "getPaymentDetailFromService";
        Log.d(MODULE, TAG);
        try
        {
           new GetPaymentDetail(Str_PaymentDetail_Url,Payload_PaymentDetail(),this).getPaymentDetail();
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
    public void onTermListReceived() {
        TAG = "onStudentsReceived";
        Log.d(MODULE, TAG);
        try
        {
            getTermFeesList();
            setTermFeesList();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onTermListReceivedError(String Str_Msg) {
        TAG = "onStudentsReceived";
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
    public void onPaymentDetailsReceived() {
        TAG = "onPaymentDetailsReceived";
        Log.d(MODULE, TAG);
        try
        {
            getPaymentList();
            showPaymentDetail();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onPaymentDetailsReceivedError(String Str_Msg) {
        TAG = "onPaymentDetailsReceivedError";
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

    public void getTermFeesList()
    {
        TAG = "getTermFeesList";
        Log.d(MODULE, TAG);
        try
        {
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            String Str_Json = mPreferences.getString(AppUtils.SHARED_TERM_LIST,"");
            Log.d(MODULE, TAG + " Str_Json : " + Str_Json);
            if(Str_Json.length()>0)
            {
                responseCommon = (CommonList_Response) AppUtils.fromJson(Str_Json, new TypeToken<CommonList_Response>(){}.getType());
                mListTermFees = responseCommon.getCclass();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void getPaymentList()
    {
        TAG = "getPaymentList()";
        Log.d(MODULE, TAG);
        try
        {
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            String Str_Json = mPreferences.getString(AppUtils.SHARED_PAYMENT_DETAIL,"");
            Log.d(MODULE, TAG + " Str_Json : " + Str_Json);
            if(Str_Json.length()>0)
            {
                feesListResponse = (FeesList_Response) AppUtils.fromJson(Str_Json, new TypeToken<FeesList_Response>(){}.getType());
                mListPaymentDetails = feesListResponse.getCfees();
                System.out.println(Log.d(MODULE, TAG + " mListPaymentDetails : " + mListPaymentDetails.size()));
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
            String[] items=null;
            if(mListClass.size()>0)
            {
                items = AppUtils.getArray(mListClass,getString(R.string.lbl_select_class));
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity,android.R.layout.simple_spinner_item,items);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_class.setAdapter(adapter);
                spinner_class.setSelection(mClassListPosition);
            }
            else
            {
                items = new String[1];
                items[0] = getString(R.string.lbl_select_class);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity,android.R.layout.simple_spinner_item,items);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_class.setAdapter(adapter);
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
            String[] items =null;
            if(mListSection.size()>0)
            {
                items=AppUtils.getArray(mListSection,getString(R.string.lbl_select_section));
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

    public void setTermFeesList()
    {
        TAG = "setTermFeesList";
        Log.d(MODULE, TAG);
        try
        {
            if(mListTermFees.size()>0)
            {
                String[] items = AppUtils.getArray(mListTermFees,getString(R.string.lbl_select_term));
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity,android.R.layout.simple_spinner_item,items);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_term_fees.setAdapter(adapter);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void showPaymentDetail()
    {
        TAG = "showFeesDetail";
        Log.d(MODULE, TAG);
        try
        {
            if(mListPaymentDetails!=null)
            {
                if(mListPaymentDetails.size()>0)
                {
                    PaymentAdapter adapter = new PaymentAdapter(mListPaymentDetails,this);
                    recycler_view.setAdapter(adapter);
                    recycler_view.setVisibility(View.VISIBLE);
                    layout_empty.setVisibility(View.GONE);
                }
                else
                {
                    text_view_empty.setText(getString(R.string.lbl_payment_details_not_found));
                    showEmptyView();
                }
            }
            else
            {
                text_view_empty.setText(getString(R.string.lbl_payment_details_not_found));
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

    public JSONObject Payload_PaymentDetail()
    {
        TAG = "Payload_PaymentDetail";
        Log.d(MODULE, TAG);

        JSONObject obj = new JSONObject();
        try {
            obj.put("ClassId", Str_ClassId);
            obj.put("SectionId", Str_SectionId);
            obj.put("TermFeesId", Str_TermFeesId);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(MODULE, TAG + " obj : " + obj.toString());

        return obj;
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
