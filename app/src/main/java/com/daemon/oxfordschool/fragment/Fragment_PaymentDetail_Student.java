package com.daemon.oxfordschool.fragment;

/**
 * Created by Ravi on 29/07/15.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.Utils.Font;
import com.daemon.oxfordschool.asyncprocess.GetFeesTermList;
import com.daemon.oxfordschool.asyncprocess.GetPaymentDetail;
import com.daemon.oxfordschool.asyncprocess.GetStudentProfile;
import com.daemon.oxfordschool.classes.CFees;
import com.daemon.oxfordschool.classes.Common_Class;
import com.daemon.oxfordschool.classes.User;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.listeners.PaymentDetailsListener;
import com.daemon.oxfordschool.listeners.Term_List_Listener;
import com.daemon.oxfordschool.listeners.ViewStudentProfileListener;
import com.daemon.oxfordschool.response.CommonList_Response;
import com.daemon.oxfordschool.response.FeesList_Response;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Fragment_PaymentDetail_Student extends Fragment implements ViewStudentProfileListener,Term_List_Listener,PaymentDetailsListener
{

    public static String MODULE = "Fragment_PaymentDetail_Student";
    public static String TAG = "";

    TextView text_view_empty,tv_select_term_fees,tv_lbl_tution_fees,tv_tution_fees,tv_lbl_development_fund,
            tv_development_fund,tv_lbl_exam_fees,tv_exam_fees,tv_lbl_library_fees,tv_library_fees,tv_lbl_total_fees,
            tv_total_fees,tv_lbl_paid_status_fees,tv_paid_status,tv_name,tv_class,tv_section;
    ImageView imageView;
    Spinner spinner_term_fees;
    RelativeLayout layout_empty;
    LinearLayout ll_payment_details;
    int mSelectedPosition;

    SharedPreferences mPreferences;
    User mUser,mStudent;

    ArrayList<Common_Class> mListTermFees =new ArrayList<Common_Class>();
    ArrayList<CFees> mListPaymentDetails =new ArrayList<CFees>();

    CommonList_Response termListResponse;
    FeesList_Response feesListResponse;

    AppCompatActivity mActivity;
    Bitmap mDecodedImage;
    String Str_Id,Str_TermFeesId,Str_StudentId="",Str_EncodeImage="";
    private Font font= MyApplication.getInstance().getFontInstance();
    String Str_Url = ApiConstants.STUDENT_PROFILE_URL;
    String Str_TermList_Url = ApiConstants.TERM_LIST_URL;
    String Str_PaymentDetail_Url = ApiConstants.PAYMENT_DETAIL_STUDENT_URL;

    public Fragment_PaymentDetail_Student()
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
            setHasOptionsMenu(true);
            new GetStudentProfile(Str_Url,Payload(),this).getStudentProfile();
            new GetFeesTermList(this).getFeesTermList();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_payment_student, container, false);
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

            imageView = (ImageView) view.findViewById(R.id.iv_profile);
            tv_name  = (TextView) view.findViewById(R.id.tv_header_name);
            tv_class  = (TextView) view.findViewById(R.id.tv_class_name);
            tv_section  = (TextView) view.findViewById(R.id.tv_section_name);
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

            tv_name.setTypeface(font.getHelveticaRegular());
            tv_class.setTypeface(font.getHelveticaRegular());
            tv_section.setTypeface(font.getHelveticaRegular());
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

    AdapterView.OnItemSelectedListener _OnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

            TAG = "_OnItemSelectedListener";
            Log.d(MODULE, TAG + "selected postion" + position);
            if(position>0)
            {
                Str_TermFeesId = mListTermFees.get(position-1).getID();
                if(!Str_TermFeesId.equals(""))getPaymentDetailFromService();
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
            Str_StudentId = mStudent.getStudentId();
            new GetPaymentDetail(Str_PaymentDetail_Url,Payload_PaymentDetail(),this).getPaymentDetail();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onStudentProfileReceived() {
        TAG = "onStudentProfileReceived";
        Log.d(MODULE, TAG);
        try
        {
            getStudentProfile();
            setProfile();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onStudentProfileReceivedError(String Str_Msg) {
        TAG = "onStudentProfileReceivedError";
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

    public void getStudentProfile()
    {
        TAG = "getStudentsList";
        Log.d(MODULE, TAG);
        try
        {
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            String Str_Json = mPreferences.getString(AppUtils.SHARED_STUDENT_PROFILE, "");
            Log.d(MODULE, TAG + " Str_Json : " + Str_Json);
            if(Str_Json.length()>0)
            {
                mStudent = (User) AppUtils.fromJson(Str_Json, new TypeToken<User>(){}.getType());
                Log.d(MODULE, TAG + " mStudent : " + mStudent.getClassName());
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void setProfile()
    {
        TAG = "setProfile";
        Log.d(MODULE, TAG);

        try
        {
            StringBuilder Str_Name = new StringBuilder();
            Str_Name.append(mStudent.getFirstName()).append(" ");
            Str_Name.append(mStudent.getLastName());
            StringBuilder Str_ClassName = new StringBuilder();
            Str_ClassName.append(mActivity.getString(R.string.lbl_class)).append(" ");
            Str_ClassName.append(mStudent.getClassName());
            StringBuilder Str_SectionName = new StringBuilder();
            Str_SectionName.append(mActivity.getString(R.string.lbl_section)).append(" ");
            Str_SectionName.append(mStudent.getSectionName());
            tv_name.setText(Str_Name.toString());
            tv_class.setText(Str_ClassName);
            tv_section.setText(Str_SectionName.toString());

            Str_EncodeImage = mStudent.getImageData();

            if(Str_EncodeImage.equals("")) imageView.setImageResource(R.drawable.ic_profile);
            else
            {
                Log.d(MODULE, TAG + "encoded string ***" + Str_EncodeImage);
                byte[] decodedString = Base64.decode(Str_EncodeImage, Base64.DEFAULT);
                mDecodedImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageView.setImageBitmap(mDecodedImage);

            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public JSONObject Payload()
    {
        TAG = "Payload";
        Log.d(MODULE, TAG);

        JSONObject obj = new JSONObject();
        try {
            obj.put("UserId", Str_Id);
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
