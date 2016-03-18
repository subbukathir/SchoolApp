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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.Utils.Font;
import com.daemon.oxfordschool.classes.Common_Class;
import com.daemon.oxfordschool.listeners.Subject_List_Item_Click_Listener;

import java.util.ArrayList;


public class SubjectsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private static ArrayList<Common_Class> mListSubjects = new ArrayList<Common_Class>();
    Fragment mFragment;
    FragmentManager mManager;
    static FragmentActivity mActivity;
    static SharedPreferences mPreferences;
    static RecyclerView recycler_view;
    static Font font= MyApplication.getInstance().getFontInstance();
    static Typeface mTypeFace;
    private boolean isFooterEnabled = false;
    int mMargin=0;float mDensity=0;
    Subject_List_Item_Click_Listener mItemCallBack;

    private static String MODULE = "SubjectsAdapter";
    private static String TAG="";
    LinearLayout.LayoutParams params;

    public SubjectsAdapter(ArrayList<Common_Class> mListSubjects, Fragment mFragment)
    {
        TAG = "SubjectsAdapter";
        Log.d(MODULE, TAG);
        Log.d(MODULE, TAG + "mListSubjects Size: " + mListSubjects.size());
        this.mListSubjects = mListSubjects;
        this.mFragment = mFragment;
        this.mActivity = (FragmentActivity)mFragment.getActivity();
        mManager = mActivity.getSupportFragmentManager();
        mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
        mTypeFace = font.getHelveticaRegular();
        mDensity =  mActivity.getResources().getDisplayMetrics().density;
        mMargin = (int) (mActivity.getResources().getDimension(R.dimen.space_layout_margin) / mDensity);
        mItemCallBack = (Subject_List_Item_Click_Listener) mFragment;
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
            if(mHolder instanceof Event_ListHolders)
            {
                Log.d(MODULE, TAG + "mHolder is instance of Event_ListHolders");
                Event_ListHolders holder = (Event_ListHolders) mHolder;
                final Common_Class mSubjects = mListSubjects.get(position);

                Log.d(MODULE, TAG + " mSubject Name : " + mSubjects.getName());
                Log.d(MODULE, TAG + " Subject Id : " + mSubjects.getID());

                holder.tv_subject_name.setText(mSubjects.getName());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try
                        {
                            mItemCallBack.onSubjectListItemClicked(position);
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
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_subject,parent,false);
            mHolder = new Event_ListHolders(layoutView);
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
        return  (isFooterEnabled) ? mListSubjects.size() + 1 : mListSubjects.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        return (isFooterEnabled && position >= mListSubjects.size() ) ? VIEW_PROG : VIEW_ITEM;
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

    public static class Event_ListHolders extends RecyclerView.ViewHolder
    {
        //Declaring parent view items
        public TextView  tv_subject_name;
        public View itemView;


        public Event_ListHolders(View itemView)
        {
            super(itemView);
            try
            {
                this.itemView = itemView;
                tv_subject_name = (TextView) itemView.findViewById(R.id.tv_subject_name);
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
