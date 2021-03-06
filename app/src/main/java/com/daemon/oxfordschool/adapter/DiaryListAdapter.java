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
import com.daemon.oxfordschool.classes.CHomework;
import com.daemon.oxfordschool.listeners.Diary_List_Item_Click_Listener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;


public class DiaryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private static ArrayList<CHomework> mListHomeWork = new ArrayList<CHomework>();
    Fragment mFragment;
    FragmentManager mManager;
    static FragmentActivity mActivity;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    static SharedPreferences mPreferences;
    static RecyclerView recycler_view;
    static Font font= MyApplication.getInstance().getFontInstance();
    static Typeface mTypeFace;
    private boolean isFooterEnabled = false;
    int mMargin=0;float mDensity=0;
    Diary_List_Item_Click_Listener mItemCallBack;

    private static String MODULE = "DiaryListAdapter";
    private static String TAG="";
    LinearLayout.LayoutParams params;

    public DiaryListAdapter(ArrayList<CHomework> mListHomeWork, Fragment mFragment)
    {
        TAG = "DiaryListAdapter";
        Log.d(MODULE, TAG);
        Log.d(MODULE, TAG + "mListHomeWork Size: " + mListHomeWork.size());
        this.mListHomeWork = mListHomeWork;
        this.mFragment = mFragment;
        this.mActivity = (FragmentActivity)mFragment.getActivity();
        mManager = mActivity.getSupportFragmentManager();
        mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
        mTypeFace = font.getHelveticaRegular();
        mDensity =  mActivity.getResources().getDisplayMetrics().density;
        mMargin = (int) (mActivity.getResources().getDimension(R.dimen.space_layout_margin) / mDensity);
        mItemCallBack = (Diary_List_Item_Click_Listener) mFragment;
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
            if(mHolder instanceof Diary_ListHolders)
            {
                Log.d(MODULE, TAG + "mHolder is instance of Station_ListHolders");
                Diary_ListHolders holder = (Diary_ListHolders) mHolder;
                final CHomework mHomeWork = mListHomeWork.get(position);

                Log.d(MODULE, TAG + "mHomeWork HomeWorkId : " + mHomeWork.getHomeWorkId());
                Log.d(MODULE, TAG + "mHomeWork ClassId : " + mHomeWork.getClassId());
                Log.d(MODULE, TAG + "mHomeWork SectionId : " +  mHomeWork.getSectionId());
                Log.d(MODULE, TAG + "mHomeWork SubjectId : " + mHomeWork.getSubjectId());
                Log.d(MODULE, TAG + "mHomeWork Assignment : " + mHomeWork.getAssignment());
                Log.d(MODULE, TAG + "mHomeWork Comments : " + mHomeWork.getComments());
                Log.d(MODULE, TAG + "mHomeWork HomeWorkDate : " + mHomeWork.getHomeWorkDate());
                Log.d(MODULE, TAG + "mHomeWork ClassName : " + mHomeWork.getClassName());
                Log.d(MODULE, TAG + "mHomeWork SectionName : " + mHomeWork.getSectionName());
                Log.d(MODULE, TAG + "mHomeWork SubjectName : " + mHomeWork.getSubjectName());

                holder.tv_subject_name.setText(mHomeWork.getSubjectName());
                holder.tv_Assignment.setText(mHomeWork.getAssignment());
                holder.tv_comments.setText(mHomeWork.getComments());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try
                        {
                            mItemCallBack.onDiaryListItemClicked(position);

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
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_diary, parent,false);
            mHolder = new Diary_ListHolders(layoutView);
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
        return  (isFooterEnabled) ? mListHomeWork.size() + 1 : mListHomeWork.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        return (isFooterEnabled && position >= mListHomeWork.size() ) ? VIEW_PROG : VIEW_ITEM;
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

    public static class Diary_ListHolders extends RecyclerView.ViewHolder
    {
        public TextView  tv_subject_name,tv_lbl_Assignment,tv_Assignment,tv_lbl_comments,
                tv_comments;
        public ImageView image_view;
        public View itemView;


        public Diary_ListHolders(View itemView)
        {
            super(itemView);
            try
            {
                this.itemView = itemView;
                tv_subject_name = (TextView) itemView.findViewById(R.id.tv_subject_name);
                tv_lbl_Assignment = (TextView) itemView.findViewById(R.id.tv_lbl_Assignment);
                tv_Assignment = (TextView) itemView.findViewById(R.id.tv_Assignment);
                tv_lbl_comments = (TextView) itemView.findViewById(R.id.tv_lbl_comments);
                tv_comments = (TextView) itemView.findViewById(R.id.tv_comments);
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
                tv_subject_name.setTypeface(font.getHelveticaBold());
                tv_lbl_Assignment.setTypeface(mTypeFace);
                tv_Assignment.setTypeface(mTypeFace);
                tv_lbl_comments.setTypeface(mTypeFace);
                tv_comments.setTypeface(mTypeFace);

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
