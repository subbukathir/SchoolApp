package com.daemon.oxfordschool.fragment;

/**
 * Created by Ravi on 29/07/15.
 */
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.Utils.Font;
import com.daemon.oxfordschool.adapter.CCEReportAdapter;
import com.daemon.oxfordschool.adapter.StudentPagerAdapter;
import com.daemon.oxfordschool.asyncprocess.GetCCE_ExamReport;
import com.daemon.oxfordschool.asyncprocess.GetStudentList;
import com.daemon.oxfordschool.classes.CCEResult;
import com.daemon.oxfordschool.classes.Result;
import com.daemon.oxfordschool.classes.User;
import com.daemon.oxfordschool.components.RecycleEmptyErrorView;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.listeners.CCEExam_Report_Item_Click_Listener;
import com.daemon.oxfordschool.listeners.CCE_ExamReport_Listener;
import com.daemon.oxfordschool.listeners.StudentsListListener;
import com.daemon.oxfordschool.response.CCE_ExamReport_Response;
import com.daemon.oxfordschool.response.StudentsList_Response;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Fragment_CCE_ExamReport_List extends Fragment implements CCE_ExamReport_Listener,
        CCEExam_Report_Item_Click_Listener
{

    public static String MODULE = "Fragment_CCE_ExamReport_List";
    public static String TAG = "";

    CoordinatorLayout cl_main;
    TextView text_view_empty,tv_lbl_subject_name,tv_lbl_average,tv_lbl_grade;
    RelativeLayout layout_empty;
    int mSelectedRowPosition;
    RecycleEmptyErrorView recycler_view;
    RecyclerView.LayoutManager mLayoutManager;

    SharedPreferences mPreferences;
    User mSelectedUser;

    ArrayList<CCEResult> mListCCEReport =new ArrayList<CCEResult>();
    CCE_ExamReport_Response response;

    AppCompatActivity mActivity;
    String Str_Id="",Str_StudentId="";
    private Font font= MyApplication.getInstance().getFontInstance();
    String Str_CCEExamReport_Url = ApiConstants.CCE_EXAM_REPORT_URL;
    private AlertDialog alert;
    Bundle Args;

    public Fragment_CCE_ExamReport_List()
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
            Args = getArguments();
            if(Args!=null)
            {
                mSelectedUser = Args.getParcelable(AppUtils.B_SELECTED_USER);
                getCCEExamReportFromService();
            }
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
        View rootView = inflater.inflate(R.layout.fragment_cce_report_list_view, container, false);
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
            cl_main = (CoordinatorLayout) mActivity.findViewById(R.id.cl_main);
            recycler_view = (RecycleEmptyErrorView) view.findViewById(R.id.recycler_view_cce_exam_report);
            layout_empty = (RelativeLayout) view.findViewById(R.id.layout_empty);
            text_view_empty = (TextView) view.findViewById(R.id.text_view_empty);
            tv_lbl_subject_name = (TextView) view.findViewById(R.id.tv_lbl_subject_name);
            tv_lbl_average = (TextView) view.findViewById(R.id.tv_lbl_average);
            tv_lbl_grade = (TextView) view.findViewById(R.id.tv_lbl_grade);
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

    }

    public void setProperties()
    {
        TAG = "setProperties";
        Log.d(MODULE, TAG);
        try
        {
            mLayoutManager = new LinearLayoutManager(getActivity());
            recycler_view.setLayoutManager(mLayoutManager);
            text_view_empty.setTypeface(font.getHelveticaRegular());
            tv_lbl_subject_name.setTypeface(font.getHelveticaBold());
            tv_lbl_average.setTypeface(font.getHelveticaBold());
            tv_lbl_grade.setTypeface(font.getHelveticaBold());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void getCCEExamReportFromService()
    {
        TAG = "getCCEExamReportFromService";
        Log.d(MODULE, TAG);
        try
        {
            if(mSelectedUser!=null)
            {
                Str_StudentId = mSelectedUser.getStudentId();
                new GetCCE_ExamReport(Str_CCEExamReport_Url,Payload_CCE_Exam_Report(),this).getCCE_ExamReport();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onCCEExamReportReceived() {
        TAG = "onStudentsReceived";
        Log.d(MODULE, TAG);
        try
        {
            getCCE_ExamReport();
            showCCE_ExamReport();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onCCEExamReportReceivedError(String Str_Msg) {
        TAG = "onCCEExamReportReceivedError";
        Log.d(MODULE, TAG);
        try
        {
            text_view_empty.setText(Str_Msg);
            showEmptyView();
            showSnackBar(Str_Msg);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onReportItemClicked(int position) {
        TAG = "onReportItemClicked";
        Log.d(MODULE, TAG);
        try
        {
            mSelectedRowPosition=position;
            showDetail();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void getCCE_ExamReport()
    {
        TAG = "getCCE_ExamReport";
        Log.d(MODULE, TAG);
        try
        {
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            String Str_Json = mPreferences.getString(AppUtils.SHARED_CCE_EXAM_REPORT,"");
            Log.d(MODULE, TAG + " Str_Json : " + Str_Json);
            if(Str_Json.length()>0)
            {
                response = (CCE_ExamReport_Response) AppUtils.fromJson(Str_Json, new TypeToken<CCE_ExamReport_Response>(){}.getType());
                mListCCEReport = response.getCceresult();
                Log.d(MODULE, TAG + " mListCCEReport : " + mListCCEReport.size());
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void showCCE_ExamReport()
    {
        TAG = "showCCE_ExamReport";
        Log.d(MODULE, TAG);
        try
        {
            if(mListCCEReport.size()>0)
            {
                CCEReportAdapter adapter = new CCEReportAdapter(mListCCEReport,this);
                recycler_view.setAdapter(adapter);
                recycler_view.setVisibility(View.VISIBLE);
                layout_empty.setVisibility(View.GONE);
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

    public JSONObject Payload_CCE_Exam_Report()
    {
        TAG = "Payload_CCE_Exam_Report";
        Log.d(MODULE, TAG);

        JSONObject obj = new JSONObject();
        try {
           obj.put("StudentId", Str_StudentId);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(MODULE, TAG + " obj : " + obj.toString());

        return obj;
    }

    public void showDetail()
    {
        TAG = "showDetail";
        Log.d(MODULE, TAG);

        try
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            // Get the layout inflater
            LayoutInflater inflater = mActivity.getLayoutInflater();
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            View view = inflater.inflate(R.layout.view_item_cce_reports_row, null);
            ArrayList<Result> mList = mListCCEReport.get(mSelectedRowPosition).getResult();
            LinearLayout ll_mark_details = (LinearLayout) view.findViewById(R.id.ll_mark_details);
            ll_mark_details.removeAllViews();
            for(int i=0;i<mList.size();i++)
            {
                View view_inner = inflater.inflate(R.layout.view_item_cce_reports_inner_row, null);
                TextView tv_exam_name = (TextView) view_inner.findViewById(R.id.tv_exam_name);
                TextView tv_marks = (TextView) view_inner.findViewById(R.id.tv_marks);
                tv_exam_name.setTypeface(font.getHelveticaRegular());
                tv_marks.setTypeface(font.getHelveticaRegular());
                tv_exam_name.setText(mList.get(i).getExamName());
                tv_marks.setText(mList.get(i).getObtainedMarks());
                ll_mark_details.addView(view_inner);
            }
            builder.setView(view);
            builder.setTitle(mListCCEReport.get(mSelectedRowPosition).getSubjectName());
            builder.setCancelable(true);
            alert = builder.create();
            alert.show();
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

    public void showSnackBar(String Str_Msg)
    {
        Snackbar snackbar = Snackbar.make(cl_main, Str_Msg, Snackbar.LENGTH_LONG);
        snackbar.setAction(getString(R.string.lbl_retry), new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                getCCEExamReportFromService();
            }
        });
        // Changing message text color
        snackbar.setActionTextColor(Color.RED);
        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        textView.setTypeface(font.getHelveticaRegular());
        snackbar.show();
    }

}
