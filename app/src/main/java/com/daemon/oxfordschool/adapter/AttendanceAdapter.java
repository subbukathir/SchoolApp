package com.daemon.oxfordschool.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import com.daemon.oxfordschool.classes.CExam;
import com.daemon.oxfordschool.classes.CResult;
import com.daemon.oxfordschool.listeners.Attendance_List_Item_Click_Listener;
import com.daemon.oxfordschool.listeners.Exam_List_Item_Click_Listener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class AttendanceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private static ArrayList<CResult> mListAttendance = new ArrayList<CResult>();
    Fragment mFragment;
    FragmentManager mManager;
    static FragmentActivity mActivity;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    static SharedPreferences mPreferences;
    static RecyclerView recycler_view;
    Font font= MyApplication.getInstance().getFontInstance();
    static Typeface mTypeFace;
    private boolean isFooterEnabled = false;
    int mMargin=0;float mDensity=0;
    Attendance_List_Item_Click_Listener mItemCallBack;

    private static String MODULE = "AttendanceAdapter";
    private static String TAG="";
    LinearLayout.LayoutParams params;
    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    DateFormat mDateFormat = new SimpleDateFormat("dd-MM-yyyy");

    public AttendanceAdapter(ArrayList<CResult> mListAttendance, Fragment mFragment)
    {
        TAG = "AttendanceAdapter";
        Log.d(MODULE, TAG);
        Log.d(MODULE, TAG + " mListAttendance Size : " + mListAttendance.size());
        this.mListAttendance = mListAttendance;
        this.mFragment = mFragment;
        this.mActivity = (FragmentActivity)mFragment.getActivity();
        mManager = mActivity.getSupportFragmentManager();
        mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
        mTypeFace = font.getHelveticaRegular();
        mDensity =  mActivity.getResources().getDisplayMetrics().density;
        mMargin = (int) (mActivity.getResources().getDimension(R.dimen.space_layout_margin) / mDensity);
        mItemCallBack = (Attendance_List_Item_Click_Listener) mFragment;
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
            if(mHolder instanceof Attendance_ListHolders)
            {
                Log.d(MODULE, TAG + "mHolder is instance of Exam_ListHolders");
                Attendance_ListHolders holder = (Attendance_ListHolders) mHolder;
                final CResult mResult = mListAttendance.get(position);

                Log.d(MODULE, TAG + "mResult AttendanceId : " + mResult.getAttendanceId());
                Log.d(MODULE, TAG + "mResult AttendanceDate : " + mResult.getAttendanceDate());
                Log.d(MODULE, TAG + "mResult IsPresent : " +  mResult.getIsPresent());
                Log.d(MODULE, TAG + "mResult IsHalfDay : " + mResult.getIsHalfDay());
                Log.d(MODULE, TAG + "mResult IsAfterNoon : " + mResult.getIsAfterNoon());

                Date mDate = sdf1.parse(mResult.getAttendanceDate());
                String Str_Date = mDateFormat.format(mDate);

                holder.tv_date.setText(Str_Date);

                if(mResult.getIsPresent().equals("0"))
                {
                    holder.tv_status.setText(mActivity.getString(R.string.lbl_present));
                    holder.tv_status.setTextColor(Color.parseColor("#006400"));
                }
                else
                {
                    holder.tv_status.setText(mActivity.getString(R.string.lbl_absent));
                    holder.tv_status.setTextColor(Color.RED);
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try
                        {
                            mItemCallBack.onAttendanceListItemClicked(position);

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
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_attendance,null);
            mHolder = new Attendance_ListHolders(layoutView);
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
        return  (isFooterEnabled) ? mListAttendance.size() + 1 : mListAttendance.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        return (isFooterEnabled && position >= mListAttendance.size() ) ? VIEW_PROG : VIEW_ITEM;
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

    public static class Attendance_ListHolders extends RecyclerView.ViewHolder
    {
        public TextView  tv_date,tv_status;
        public ImageView image_view;
        public View itemView;


        public Attendance_ListHolders(View itemView)
        {
            super(itemView);
            try
            {
                this.itemView = itemView;
                tv_date = (TextView) itemView.findViewById(R.id.tv_date);
                tv_status = (TextView) itemView.findViewById(R.id.tv_status);
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
                tv_date.setTypeface(mTypeFace);
                tv_status.setTypeface(mTypeFace);
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
