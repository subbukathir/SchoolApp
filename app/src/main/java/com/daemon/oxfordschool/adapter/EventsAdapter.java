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
import com.daemon.oxfordschool.classes.CEvents;
import com.daemon.oxfordschool.listeners.Event_List_Item_Click_Listener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class EventsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private static ArrayList<CEvents> mListEvents = new ArrayList<CEvents>();
    Fragment mFragment;
    FragmentManager mManager;
    static FragmentActivity mActivity;
    static SharedPreferences mPreferences;
    static RecyclerView recycler_view;
    static Font font= MyApplication.getInstance().getFontInstance();
    static Typeface mTypeFace;
    private boolean isFooterEnabled = false;
    int mMargin=0;float mDensity=0;
    Event_List_Item_Click_Listener mItemCallBack;

    private static String MODULE = "EventsAdapter";
    private static String TAG="";
    LinearLayout.LayoutParams params;

    public EventsAdapter(ArrayList<CEvents> mListEvents, Fragment mFragment)
    {
        TAG = "EventsAdapter";
        Log.d(MODULE, TAG);
        Log.d(MODULE, TAG + "mListEvents Size: " + mListEvents.size());
        this.mListEvents = mListEvents;
        this.mFragment = mFragment;
        this.mActivity = (FragmentActivity)mFragment.getActivity();
        mManager = mActivity.getSupportFragmentManager();
        mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
        mTypeFace = font.getHelveticaRegular();
        mDensity =  mActivity.getResources().getDisplayMetrics().density;
        mMargin = (int) (mActivity.getResources().getDimension(R.dimen.space_layout_margin) / mDensity);
        mItemCallBack = (Event_List_Item_Click_Listener) mFragment;
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
                final CEvents mEvent = mListEvents.get(position);

                Log.d(MODULE, TAG + " mEvent Name : " + mEvent.getName());
                Log.d(MODULE, TAG + " mEvent Description : " + mEvent.getDescription());
                Log.d(MODULE, TAG + " mEvent OrganizerId : " +  mEvent.getOrganizerId());
                Log.d(MODULE, TAG + " mEvent StartDate : " + mEvent.getStartDate());
                Log.d(MODULE, TAG + " mEvent EndDate : " + mEvent.getEndDate());
                Log.d(MODULE, TAG + " mEvent Organizer First Name : " + mEvent.getOrganizer_First_Name());
                Log.d(MODULE, TAG + " mEvent Organizer Last Name : " + mEvent.getOrganizer_Last_Name());

                holder.tv_event_name.setText(mEvent.getName());
                StringBuilder Str_OrganizerName = new StringBuilder();
                Str_OrganizerName.append(mActivity.getString(R.string.lbl_organizer)).append(" : ");
                Str_OrganizerName.append(mEvent.getOrganizer_First_Name()).append(" ");
                Str_OrganizerName.append(mEvent.getOrganizer_Last_Name());
                holder.tv_event_organizer.setText(Str_OrganizerName.toString());
                holder.tv_event_organizer.setLayoutParams(params);

                String Str_Sdate = mEvent.getStartDate();
                String Str_Edate = mEvent.getEndDate();

                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                DateFormat format1 = new SimpleDateFormat("E, MMM dd yy HH:mm a");

                Date Sdate, Edate;
                try {
                    Sdate = sdf1.parse(Str_Sdate);
                    Edate = sdf1.parse(Str_Edate);
                    String str_sdate = format1.format(Sdate);
                    String str_edate = format1.format(Edate);
                    Log.d(MODULE, TAG + "start date" + str_sdate);

                    StringBuilder Str_StartDate = new StringBuilder();
                    Str_StartDate.append(mActivity.getString(R.string.lbl_starts_on)).append(" : ");
                    Str_StartDate.append(str_sdate);
                    holder.tv_event_start_date.setText(Str_StartDate.toString());
                    holder.tv_event_end_date.setText(str_edate);

                } catch (Exception e) {
                    e.printStackTrace();
                }



                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try
                        {
                            mItemCallBack.onEventListItemClicked(position);

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
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_event,parent,false);
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
        return  (isFooterEnabled) ? mListEvents.size() + 1 : mListEvents.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        return (isFooterEnabled && position >= mListEvents.size() ) ? VIEW_PROG : VIEW_ITEM;
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
        public TextView  tv_event_name,tv_event_start_date,tv_event_end_date,tv_event_organizer;
        public ImageView image_view;
        public View itemView;


        public Event_ListHolders(View itemView)
        {
            super(itemView);
            try
            {
                this.itemView = itemView;
                tv_event_name = (TextView) itemView.findViewById(R.id.tv_event_name);
                tv_event_start_date = (TextView) itemView.findViewById(R.id.tv_event_start_date);
                tv_event_end_date = (TextView) itemView.findViewById(R.id.tv_event_end_date);
                tv_event_organizer = (TextView) itemView.findViewById(R.id.tv_event_organizer);
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
                tv_event_name.setTypeface(font.getHelveticaRegular());
                tv_event_start_date.setTypeface(mTypeFace);
                tv_event_end_date.setTypeface(mTypeFace);
                tv_event_organizer.setTypeface(mTypeFace);
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
