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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.Utils.Font;
import com.daemon.oxfordschool.activity.MainActivity;
import com.daemon.oxfordschool.adapter.CCEReportAdapter;
import com.daemon.oxfordschool.adapter.StudentPagerAdapter;
import com.daemon.oxfordschool.asyncprocess.GetCCE_ExamReport;
import com.daemon.oxfordschool.asyncprocess.GetStudentList;
import com.daemon.oxfordschool.classes.CCEResult;
import com.daemon.oxfordschool.classes.Result;
import com.daemon.oxfordschool.classes.User;
import com.daemon.oxfordschool.components.MyMarkerView;
import com.daemon.oxfordschool.components.RecycleEmptyErrorView;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.listeners.CCEExam_Report_Item_Click_Listener;
import com.daemon.oxfordschool.listeners.CCE_ExamReport_Listener;
import com.daemon.oxfordschool.listeners.StudentsListListener;
import com.daemon.oxfordschool.response.CCE_ExamReport_Response;
import com.daemon.oxfordschool.response.StudentsList_Response;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Fragment_CCE_ExamReport_Chart extends Fragment implements CCE_ExamReport_Listener,
        CCEExam_Report_Item_Click_Listener, OnChartGestureListener
{

    public static String MODULE = "Fragment_CCE_ExamReport_Chart";
    public static String TAG = "";

    SharedPreferences mPreferences;
    User mSelectedUser;
    int mSelectedRowPosition;

    ArrayList<CCEResult> mListCCEReport =new ArrayList<CCEResult>();
    CCE_ExamReport_Response response;
    CoordinatorLayout cl_main;
    AppCompatActivity mActivity;
    String Str_Id="",Str_StudentId="";
    private Font font= MyApplication.getInstance().getFontInstance();
    String Str_CCEExamReport_Url = ApiConstants.CCE_EXAM_REPORT_URL;
    private AlertDialog alert;
    Bundle Args;
    private BarChart mChart;
    int mTextSize=0;float mDensity=0;
    XAxis xAxis;


    public Fragment_CCE_ExamReport_Chart()
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
            mDensity =  mActivity.getResources().getDisplayMetrics().density;
            mTextSize = (int) (mActivity.getResources().getDimension(R.dimen.lbl_size) / mDensity);
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
        View rootView = inflater.inflate(R.layout.fragment_cce_report_chart_view, container, false);
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
            CreateChart(view);
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

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void CreateChart(View view)
    {
        TAG = "CreateChart";
        Log.d(MODULE, TAG);

        try
        {
            // create a new chart object
            mChart = new BarChart(getActivity());
            mChart.setDescription("");
            mChart.setOnChartGestureListener(this);
            MyMarkerView mv = new MyMarkerView(getActivity(), R.layout.view_custom_marker);
            mChart.setMarkerView(mv);
            mChart.setDrawGridBackground(false);
            mChart.setDrawBarShadow(false);
            Legend l = mChart.getLegend();
            l.setTypeface(font.getHelveticaRegular());
            YAxis leftAxis = mChart.getAxisLeft();
            leftAxis.setTypeface(font.getHelveticaRegular());
            leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)
            mChart.getAxisRight().setEnabled(false);
            xAxis = mChart.getXAxis();
            xAxis.setTypeface(font.getHelveticaRegular());
            xAxis.setTextSize(mTextSize);
            xAxis.setEnabled(true);
            //programatically add the chart
            FrameLayout parent = (FrameLayout) view.findViewById(R.id.parentLayout);
            parent.addView(mChart);
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

    protected BarData generateBarData(int dataSets, float range, int count)
    {
        ArrayList<IBarDataSet> sets = new ArrayList<IBarDataSet>();
        List<String> items = new ArrayList<String>();
        for(int i = 0; i < dataSets; i++)
        {
            ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
            float floatValue=0f;
            String Str_Subject="";
            items = new ArrayList<String>();
            for(int j = 0; j < count; j++)
            {
                floatValue= Float.parseFloat(mListCCEReport.get(j).getAverage());
                Str_Subject = mListCCEReport.get(j).getSubjectName().substring(0,3);
                BarEntry barEntry = new BarEntry(floatValue,j,Str_Subject);
                entries.add(barEntry);
                items.add(Str_Subject);
            }
            xAxis.setValues(items);
            BarDataSet ds = new BarDataSet(entries, "");
            ds.setColors(ColorTemplate.VORDIPLOM_COLORS);
            sets.add(ds);
        }
        BarData d = new BarData(items, sets);
        d.setValueTypeface(font.getHelveticaRegular());
        d.setValueTextSize(mTextSize);
        return d;
    }

    public void showCCE_ExamReport()
    {
        TAG = "showCCE_ExamReport";
        Log.d(MODULE, TAG);
        try
        {
            if(mListCCEReport.size()>0)
            {
                mChart.setData(generateBarData(1,100,mListCCEReport.size()));
                mChart.performClick();
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
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "START");
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "END");
        mChart.highlightValues(null);
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.i("LongPress", "Chart longpressed.");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.i("DoubleTap", "Chart double-tapped.");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i("SingleTap", "Chart single-tapped.");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        Log.i("Fling", "Chart flinged. VeloX: " + velocityX + ", VeloY: " + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
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
