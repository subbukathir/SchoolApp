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
import com.daemon.oxfordschool.classes.CResult;
import com.daemon.oxfordschool.listeners.Exam_Result_List_Item_Click_Listener;

import java.util.ArrayList;


public class ExamResultStaffAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private static ArrayList<CResult> mListResult = new ArrayList<CResult>();
    Fragment mFragment;
    FragmentManager mManager;
    static FragmentActivity mActivity;
    static SharedPreferences mPreferences;
    static RecyclerView recycler_view;
    Font font= MyApplication.getInstance().getFontInstance();
    static Typeface mTypeFace;
    private boolean isFooterEnabled = false;
    int mMargin=0;float mDensity=0;
    Exam_Result_List_Item_Click_Listener mItemCallBack;

    private static String MODULE = "ExamResultStaffAdapter";
    private static String TAG="";
    LinearLayout.LayoutParams params;

    public ExamResultStaffAdapter(ArrayList<CResult> mListResult, Fragment mFragment)
    {
        TAG = "ExamResultStaffAdapter";
        Log.d(MODULE, TAG);
        Log.d(MODULE, TAG + "mListResult Size: " + mListResult.size());
        this.mListResult = mListResult;
        this.mFragment = mFragment;
        this.mActivity = (FragmentActivity)mFragment.getActivity();
        mManager = mActivity.getSupportFragmentManager();
        mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
        mTypeFace = font.getHelveticaRegular();
        mDensity =  mActivity.getResources().getDisplayMetrics().density;
        mMargin = (int) (mActivity.getResources().getDimension(R.dimen.space_layout_margin) / mDensity);
        mItemCallBack = (Exam_Result_List_Item_Click_Listener) mFragment;
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        params = new LinearLayout.LayoutParams(width,height,1);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder mHolder, final int position)
    {
        TAG = "onBindViewHolder";
        Log.d(MODULE, TAG);

        try
        {
            if(mHolder instanceof ExamResult_ListHolders)
            {
                Log.d(MODULE, TAG + "mHolder is instance of Station_ListHolders");
                ExamResult_ListHolders holder = (ExamResult_ListHolders) mHolder;
                final CResult CResult = mListResult.get(position);

                Log.d(MODULE, TAG + "CResult Name : " + CResult.getSubjectName());
                Log.d(MODULE, TAG + "CResult Marks : " + CResult.getObtainedMarks());
                Log.d(MODULE, TAG + "CResult OrganizerId : " + CResult.getResult());

                StringBuilder Str_Name = new StringBuilder();
                Str_Name.append(CResult.getStudentFirstName()).append(" ");
                Str_Name.append(CResult.getStudentLastName());
                holder.tv_exam_subject.setText(Str_Name.toString());
                holder.tv_exam_marks.setText(CResult.getObtainedMarks());
                holder.tv_exam_result.setText(CResult.getResult());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try
                        {
                            mItemCallBack.onExamResultListItemClicked(position);

                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }
                    }
                });

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
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_exam_result,parent,false);
            mHolder = new ExamResult_ListHolders(layoutView);
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
        return  (isFooterEnabled) ? mListResult.size() + 1 : mListResult.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        return (isFooterEnabled && position >= mListResult.size() ) ? VIEW_PROG : VIEW_ITEM;
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

    public static class ExamResult_ListHolders extends RecyclerView.ViewHolder
    {
        //Declaring parent view items
        public TextView  tv_exam_subject,tv_exam_marks,tv_exam_result;
        public ImageView image_view;
        public View itemView;


        public ExamResult_ListHolders(View itemView)
        {
            super(itemView);
            try
            {
                this.itemView = itemView;
                tv_exam_subject = (TextView) itemView.findViewById(R.id.tv_exam_subject);
                tv_exam_marks = (TextView) itemView.findViewById(R.id.tv_exam_marks);
                tv_exam_result = (TextView) itemView.findViewById(R.id.tv_exam_result);
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
                tv_exam_subject.setTypeface(mTypeFace);
                tv_exam_marks.setTypeface(mTypeFace);
                tv_exam_result.setTypeface(mTypeFace);
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

}
