package com.daemon.oxfordschool.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.Utils.Font;
import com.daemon.oxfordschool.classes.CCEResult;
import com.daemon.oxfordschool.classes.Result;
import com.daemon.oxfordschool.listeners.Event_List_Item_Click_Listener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class CCEReportAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;


    private static ArrayList<CCEResult> mListCCEReport= new ArrayList<CCEResult>();
    Fragment mFragment;
    FragmentManager mManager;
    static FragmentActivity mActivity;
    static SharedPreferences mPreferences;
    static RecyclerView recycler_view;
    static Font font= MyApplication.getInstance().getFontInstance();
    static Typeface mTypeFace;
    private boolean isFooterEnabled = false;
    int mMargin=0;float mDensity=0;

    private static String MODULE = "CCEReportAdapter";
    private static String TAG="";
    LinearLayout.LayoutParams params;
    LayoutInflater inflater;

    public CCEReportAdapter(ArrayList<CCEResult> mListCCEReport, Fragment mFragment)
    {
        TAG = "CCEReportAdapter";
        Log.d(MODULE, TAG);
        Log.d(MODULE, TAG + "mListCCEReport Size: " + mListCCEReport.size());
        this.mListCCEReport = mListCCEReport;
        this.mFragment = mFragment;
        this.mActivity = (FragmentActivity)mFragment.getActivity();
        mManager = mActivity.getSupportFragmentManager();
        mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
        mTypeFace = font.getHelveticaRegular();
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        params = new LinearLayout.LayoutParams(width,height,1);
        inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder mHolder, final int position)
    {
        TAG = "onBindViewHolder";
        Log.d(MODULE, TAG);

        try
        {
            if(mHolder instanceof CCEReport_ListHolders)
            {
                Log.d(MODULE, TAG + "mHolder is instance of CCEReport_ListHolders");
                final CCEReport_ListHolders holder = (CCEReport_ListHolders) mHolder;
                final CCEResult mCCEResult = mListCCEReport.get(position);
                Log.d(MODULE, TAG + " Subject Name : " + mCCEResult.getSubjectName());
                holder.tv_subject_name.setText(mCCEResult.getSubjectName());
                holder.tv_average.setText(mCCEResult.getAverage());
                holder.tv_grade.setText(mCCEResult.getGrade());
            }
            else if(mHolder instanceof LoadingMessageHolder)
            {
                LoadingMessageHolder holder = (LoadingMessageHolder) mHolder;
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                params.setMargins(mMargin,mMargin,mMargin,mMargin);
                holder.itemView.setLayoutParams(params);
                holder.layout_loading_message.setVisibility(View.VISIBLE);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        TAG = "onCreateViewHolder";
        Log.d(MODULE, TAG);
        recycler_view = (RecyclerView) parent;
        RecyclerView.ViewHolder mHolder=null;
        if(viewType == VIEW_ITEM)
        {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_cce_reports,parent,false);
            mHolder = new CCEReport_ListHolders(layoutView);
        }
        else if(viewType == VIEW_PROG)
        {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_loading_message,null);
            mHolder = new LoadingMessageHolder(layoutView);
        }
        return mHolder;
    }

    @Override
    public int getItemCount()
    {
        return  (isFooterEnabled) ? mListCCEReport.size() + 1 : mListCCEReport.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        return (isFooterEnabled && position >= mListCCEReport.size() ) ? VIEW_PROG : VIEW_ITEM;
    }

    /**
     * Enable or disable footer (Default is true)
     *
     * @param isEnabled boolean to turn on or off footer.
     */
    public void enableFooter(boolean isEnabled)
    {
        this.isFooterEnabled = isEnabled;
    }

    public static class CCEReport_ListHolders extends RecyclerView.ViewHolder
    {
        //Declaring parent view items
        public TextView  tv_subject_name,tv_average,tv_grade;
        public LinearLayout ll_marks_detail;
        public View itemView;


        public CCEReport_ListHolders(View itemView)
        {
            super(itemView);
            try
            {
                this.itemView = itemView;
                tv_subject_name = (TextView) itemView.findViewById(R.id.tv_subject_name);
                tv_average = (TextView) itemView.findViewById(R.id.tv_average);
                tv_grade = (TextView) itemView.findViewById(R.id.tv_grade);
                //ll_marks_detail = (LinearLayout) itemView.findViewById(R.id.ll_marks_detail);
                //Setting properties
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SetFont();
                    }
                });
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }


        }

        public void SetFont()
        {
            try
            {
                tv_subject_name.setTypeface(font.getHelveticaRegular());
                tv_average.setTypeface(font.getHelveticaRegular());
                tv_grade.setTypeface(font.getHelveticaRegular());
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

        }

    }

    public static class LoadingMessageHolder extends RecyclerView.ViewHolder
    {
        View itemView;
        TextView text_view_loading_message;
        LinearLayout layout_loading_message;

        public LoadingMessageHolder(View itemView)
        {
            super(itemView);
            this.itemView=itemView;
            layout_loading_message = (LinearLayout) itemView.findViewById(R.id.layout_loading);
            text_view_loading_message = (TextView) itemView.findViewById(R.id.text_view_message);
            text_view_loading_message.setTypeface(mTypeFace);
        }
    }

    public void AddDetail(CCEReport_ListHolders holder,ArrayList<Result> mList)
    {
        for(int i=0;i<mList.size();i++)
        {
            View view_inner = inflater.inflate(R.layout.view_item_cce_reports_inner_row, null);
            TextView tv_exam_name = (TextView) view_inner.findViewById(R.id.tv_exam_name);
            TextView tv_marks = (TextView) view_inner.findViewById(R.id.tv_marks);
            tv_exam_name.setTypeface(font.getHelveticaRegular());
            tv_marks.setTypeface(font.getHelveticaRegular());
            tv_exam_name.setText(mList.get(i).getExamName());
            tv_marks.setText(mList.get(i).getObtainedMarks());
            holder.ll_marks_detail.addView(view_inner);
        }
    }

}
