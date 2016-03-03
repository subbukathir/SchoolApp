package com.daemon.oxfordschool.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
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
import com.daemon.oxfordschool.classes.CFees;
import com.daemon.oxfordschool.constants.ApiConstants;

import java.util.ArrayList;

public class PaymentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    ArrayList<CFees> mListPaymentDetails =new ArrayList<CFees>();
    Fragment mFragment;
    FragmentManager mManager;
    static FragmentActivity mActivity;
    static SharedPreferences mPreferences;
    static RecyclerView recycler_view;
    Font font= MyApplication.getInstance().getFontInstance();
    static Typeface mTypeFace;
    private boolean isFooterEnabled = false;
    int mMargin=0;float mDensity=0;


    private static String MODULE = "PaymentAdapter";
    private static String TAG="";
    LinearLayout.LayoutParams params;

    public PaymentAdapter( ArrayList<CFees> mListPaymentDetails, Fragment mFragment)
    {
        TAG = "PaymentAdapter";
        Log.d(MODULE, TAG);
        Log.d(MODULE, TAG + " mListExams Size : " + mListPaymentDetails.size());
        this.mListPaymentDetails = mListPaymentDetails;
        this.mFragment = mFragment;
        this.mActivity = (FragmentActivity)mFragment.getActivity();
        mManager = mActivity.getSupportFragmentManager();
        mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
        mTypeFace = font.getHelveticaRegular();
        mDensity =  mActivity.getResources().getDisplayMetrics().density;
        mMargin = (int) (mActivity.getResources().getDimension(R.dimen.space_layout_margin) / mDensity);
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
            if(mHolder instanceof Payment_ListHolders)
            {
                Log.d(MODULE, TAG + "mHolder is instance of Exam_ListHolders");
                Payment_ListHolders holder = (Payment_ListHolders) mHolder;
                final CFees cFees = mListPaymentDetails.get(position);

                StringBuilder Str_Name = new StringBuilder();
                Str_Name.append(cFees.getFirstName()).append(" ");
                Str_Name.append(cFees.getLastName());
                holder.tv_name.setText(Str_Name.toString());
                int status = Integer.parseInt(cFees.getStatus());
                if(status== ApiConstants.PAID)
                {
                    holder.tv_status.setTextColor(ContextCompat.getColor(mActivity, R.color.color_dark_green));
                    holder.tv_status.setText(mActivity.getString(R.string.lbl_paid));
                }
                else if(status==ApiConstants.UNPAID)
                {
                    holder.tv_status.setTextColor(Color.RED);
                    holder.tv_status.setText(mActivity.getString(R.string.lbl_unpaid));

                }
                else if(status==ApiConstants.PARTIALLY_PAID)
                {
                    holder.tv_status.setTextColor(Color.parseColor("#FF6600"));
                    holder.tv_status.setText(mActivity.getString(R.string.lbl_partially_paid));
                }

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
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_payment_detail,parent,false);
            mHolder = new Payment_ListHolders(layoutView);
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
        return  (isFooterEnabled) ? mListPaymentDetails.size() + 1 : mListPaymentDetails.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        return (isFooterEnabled && position >= mListPaymentDetails.size() ) ? VIEW_PROG : VIEW_ITEM;
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

    public static class Payment_ListHolders extends RecyclerView.ViewHolder
    {
        public TextView  tv_name,tv_status;
        public ImageView image_view;
        public View itemView;


        public Payment_ListHolders(View itemView)
        {
            super(itemView);
            try
            {
                this.itemView = itemView;
                tv_name = (TextView) itemView.findViewById(R.id.tv_name);
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
                tv_name.setTypeface(mTypeFace);
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
