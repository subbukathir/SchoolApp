package com.daemon.oxfordschool.fragment;

/**
 * Created by Ravi on 29/07/15.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
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
import com.daemon.oxfordschool.adapter.ExamResultAdapter;
import com.daemon.oxfordschool.adapter.StudentPagerAdapter;
import com.daemon.oxfordschool.asyncprocess.ExamTypeList_Process;
import com.daemon.oxfordschool.asyncprocess.GetExamResult;
import com.daemon.oxfordschool.asyncprocess.GetFeesTermList;
import com.daemon.oxfordschool.asyncprocess.GetPaymentDetail;
import com.daemon.oxfordschool.asyncprocess.GetStudentList;
import com.daemon.oxfordschool.classes.CExam;
import com.daemon.oxfordschool.classes.CFees;
import com.daemon.oxfordschool.classes.CResult;
import com.daemon.oxfordschool.classes.Common_Class;
import com.daemon.oxfordschool.classes.User;
import com.daemon.oxfordschool.components.RecycleEmptyErrorView;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.listeners.ExamResultListener;
import com.daemon.oxfordschool.listeners.ExamTypeListListener;
import com.daemon.oxfordschool.listeners.Exam_Result_List_Item_Click_Listener;
import com.daemon.oxfordschool.listeners.PaymentDetailsListener;
import com.daemon.oxfordschool.listeners.StudentsListListener;
import com.daemon.oxfordschool.listeners.Term_List_Listener;
import com.daemon.oxfordschool.response.CommonList_Response;
import com.daemon.oxfordschool.response.ExamList_Response;
import com.daemon.oxfordschool.response.ExamResult_Response;
import com.daemon.oxfordschool.response.FeesList_Response;
import com.daemon.oxfordschool.response.StudentsList_Response;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Fragment_PaymentDetail extends Fragment implements StudentsListListener,Term_List_Listener,PaymentDetailsListener
{

    public static String MODULE = "Fragment_PaymentDetail";
    public static String TAG = "";

    TextView text_view_empty,tv_select_term_fees,tv_lbl_tution_fees,tv_tution_fees,tv_lbl_development_fund,
            tv_development_fund,tv_lbl_exam_fees,tv_exam_fees,tv_lbl_library_fees,tv_library_fees,tv_lbl_total_fees,
            tv_total_fees,tv_lbl_paid_status_fees,tv_paid_status;
    Spinner spinner_term_fees;
    RelativeLayout layout_empty;
    LinearLayout ll_payment_details;
    int mSelectedPosition;

    SharedPreferences mPreferences;
    User mUser,mSelectedUser;
    ViewPager vp_student;

    ArrayList<User> mListStudents =new ArrayList<User>();
    ArrayList<Common_Class> mListTermFees =new ArrayList<Common_Class>();
    ArrayList<CFees> mListPaymentDetails =new ArrayList<CFees>();

    StudentsList_Response studentListResponse;
    CommonList_Response termListResponse;
    FeesList_Response feesListResponse;

    AppCompatActivity mActivity;
    String Str_Id="",Str_TermFeesId="",Str_StudentId="";
    private Font font= MyApplication.getInstance().getFontInstance();
    String Str_StudentList_Url = ApiConstants.STUDENT_LIST;
    String Str_TermList_Url = ApiConstants.TERM_LIST_URL;
    String Str_PaymentDetail_Url = ApiConstants.PAYMENT_DETAIL_STUDENT_URL;

    public Fragment_PaymentDetail()
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
        View rootView = inflater.inflate(R.layout.fragment_payment, container, false);
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
            vp_student = (ViewPager) view.findViewById(R.id.vp_student);
            tv_select_term_fees = (TextView) view.findViewById(R.id.tv_select_term_fees);
            tv_lbl_tution_fees = (TextView) view.findViewById(R.id.tv_lbl_tution_fees);
            tv_tution_fees = (TextView) view.findViewById(R.id.tv_tution_fees);
            tv_lbl_development_fund = (TextView) view.findViewById(R.id.tv_lbl_development_fund);
            tv_development_fund = (TextView) view.findViewById(R.id.tv_development_fund);
            tv_lbl_exam_fees = (TextView) view.findViewById(R.id.tv_lbl_exam_fees);
            tv_exam_fees = (TextView) view.findViewById(R.id.tv_exam_fees);
            tv_lbl_library_fees = (TextView) view.findViewById(R.id.tv_lbl_library_fees);
            tv_library_fees = (TextView) view.findViewById(R.id.tv_library_fees);
            tv_lbl_total_fees = (TextView) view.findViewById(R.id.tv_lbl_total_fees);
            tv_total_fees = (TextView) view.findViewById(R.id.tv_total_fees);
            tv_lbl_paid_status_fees = (TextView) view.findViewById(R.id.tv_lbl_paid_status_fees);
            tv_paid_status = (TextView) view.findViewById(R.id.tv_paid_status);
            spinner_term_fees = (Spinner) view.findViewById(R.id.spinner_term_fees);

            ll_payment_details = (LinearLayout) view.findViewById(R.id.ll_payment_details);
            layout_empty = (RelativeLayout) view.findViewById(R.id.layout_empty);
            text_view_empty = (TextView) view.findViewById(R.id.text_view_empty);
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
            layout_empty.setVisibility(View.VISIBLE);
            text_view_empty.setText(getString(R.string.lbl_please_select_term));
            ll_payment_details.setVisibility(View.GONE);
            tv_select_term_fees.setTypeface(font.getHelveticaRegular());
            tv_lbl_tution_fees.setTypeface(font.getHelveticaRegular());
            tv_tution_fees.setTypeface(font.getHelveticaRegular());
            tv_lbl_development_fund.setTypeface(font.getHelveticaRegular());
            tv_development_fund.setTypeface(font.getHelveticaRegular());
            tv_lbl_exam_fees.setTypeface(font.getHelveticaRegular());
            tv_exam_fees.setTypeface(font.getHelveticaRegular());
            tv_lbl_library_fees.setTypeface(font.getHelveticaRegular());
            tv_library_fees.setTypeface(font.getHelveticaRegular());
            tv_lbl_total_fees.setTypeface(font.getHelveticaRegular());
            tv_total_fees.setTypeface(font.getHelveticaRegular());
            tv_lbl_paid_status_fees.setTypeface(font.getHelveticaRegular());
            tv_paid_status.setTypeface(font.getHelveticaRegular());
            text_view_empty.setTypeface(font.getHelveticaRegular());
            vp_student.addOnPageChangeListener(_OnPageChangeListener);
            spinner_term_fees.setOnItemSelectedListener(_OnItemSelectedListener);
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
            try
            {
                mSelectedPosition=position;
                if(mListTermFees.size()>0 && (!Str_TermFeesId.equals("")))getPaymentDetailFromService();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    AdapterView.OnItemSelectedListener _OnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

            if(position>0)
            {
                Str_TermFeesId = mListTermFees.get(position-1).getID();
                if(!Str_StudentId.equals(""))getPaymentDetailFromService();
            }

        }
        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    public void getPaymentDetailFromService()
    {
        TAG = "getPaymentDetailFromService";
        Log.d(MODULE, TAG);
        try
        {
            mSelectedUser = mListStudents.get(mSelectedPosition);
            Str_StudentId = mSelectedUser.getStudentId();
            new GetPaymentDetail(Str_PaymentDetail_Url,Payload_PaymentDetail(),this).getPaymentDetail();
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
                termListResponse = (CommonList_Response) AppUtils.fromJson(Str_Json, new TypeToken<CommonList_Response>(){}.getType());
                mListTermFees = termListResponse.getCclass();
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

    public void showStudentsList()
    {
        TAG = "showStudentsList";
        Log.d(MODULE, TAG);
        try
        {
            StudentPagerAdapter adapter = new StudentPagerAdapter(mActivity,mListStudents);
            vp_student.setAdapter(adapter);
            Str_StudentId=mListStudents.get(0).getStudentId();
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
                    CFees cFees = mListPaymentDetails.get(0);
                    tv_tution_fees.setText(cFees.getTutionFees());
                    tv_development_fund.setText(cFees.getDevelopmentFund());
                    tv_exam_fees.setText(cFees.getExamFees());
                    tv_library_fees.setText(cFees.getLibraryFees());
                    tv_total_fees.setText(cFees.getTotalFees());
                    int status = Integer.parseInt(cFees.getStatus());
                    if(status==ApiConstants.PAID)
                    {
                        tv_paid_status.setTextColor(ContextCompat.getColor(mActivity,R.color.color_dark_green));
                        tv_paid_status.setText(getString(R.string.lbl_paid));
                    }
                    else if(status==ApiConstants.UNPAID)
                    {
                        tv_paid_status.setTextColor(Color.RED);
                        tv_paid_status.setText(getString(R.string.lbl_unpaid));

                    }
                    else if(status==ApiConstants.PARTIALLY_PAID)
                    {
                        tv_paid_status.setTextColor(Color.parseColor("#FF6600"));
                        tv_paid_status.setText(getString(R.string.lbl_partially_paid));
                    }
                    ll_payment_details.setVisibility(View.VISIBLE);
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
            ll_payment_details.setVisibility(View.GONE);
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

    public JSONObject Payload_PaymentDetail()
    {
        TAG = "Payload_PaymentDetail";
        Log.d(MODULE, TAG);

        JSONObject obj = new JSONObject();
        try {
            obj.put("StudentId", Str_StudentId);
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
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_settings).setVisible(false);
        menu.findItem(R.id.action_list_view).setVisible(false);
        menu.findItem(R.id.action_chart_view).setVisible(false);
        menu.findItem(R.id.action_help).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }
}
