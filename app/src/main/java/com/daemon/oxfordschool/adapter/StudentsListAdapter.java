package com.daemon.oxfordschool.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
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
import com.daemon.oxfordschool.classes.User;
import com.daemon.oxfordschool.listeners.Student_List_Item_Click_Listener;
import java.util.ArrayList;



public class StudentsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private static ArrayList<User> mListStudents = new ArrayList<User>();
    Fragment mFragment;
    FragmentManager mManager;
    static FragmentActivity mActivity;
    static SharedPreferences mPreferences;
    static RecyclerView recycler_view;
    static Font font= MyApplication.getInstance().getFontInstance();
    static Typeface mTypeFace;
    private boolean isFooterEnabled = false;
    int mMargin=0;float mDensity=0;
    String Str_EncodeImage="";
    Bitmap mDecodedImage;
    Student_List_Item_Click_Listener mItemCallBack;

    private static String MODULE = "SchoolListAdapter";
    private static String TAG="";
    LinearLayout.LayoutParams params;

    public StudentsListAdapter(ArrayList<User> mListStudents, Fragment mFragment)
    {
        TAG = "StudentsListAdapter";
        Log.d(MODULE, TAG);
        Log.d(MODULE, TAG + "mListStudents Size: " + mListStudents.size());
        this.mListStudents = mListStudents;
        this.mFragment = mFragment;
        this.mActivity = (FragmentActivity)mFragment.getActivity();
        mManager = mActivity.getSupportFragmentManager();
        mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
        mTypeFace = font.getHelveticaRegular();
        mDensity =  mActivity.getResources().getDisplayMetrics().density;
        mMargin = (int) (mActivity.getResources().getDimension(R.dimen.space_layout_margin) / mDensity);
        mItemCallBack = (Student_List_Item_Click_Listener) mFragment;
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
            if(mHolder instanceof Students_ListHolders)
            {
                Log.d(MODULE, TAG + "mHolder is instance of Event_ListHolders");
                Students_ListHolders holder = (Students_ListHolders) mHolder;
                final User mUser = mListStudents.get(position);

                Log.d(MODULE, TAG + " mListStudents First Name : " + mUser.getFirstName());
                Log.d(MODULE, TAG + " mListStudents Last Name : " + mUser.getLastName());

                StringBuilder Str_Name = new StringBuilder();
                Str_Name.append(mUser.getFirstName()).append(" ");
                Str_Name.append(mUser.getLastName());

                holder.tv_student_name.setText(Str_Name.toString());
                holder.tv_phone_no.setText(mUser.getMobile_Number());
                holder.tv_email.setText(mUser.getEmail());

                Str_EncodeImage = mUser.getImageData();


                if(Str_EncodeImage.equals("")) holder.iv_profile.setImageResource(R.drawable.ic_profile);
                else
                {
                    Log.d(MODULE, TAG + "encoded string ***" + Str_EncodeImage);
                    byte[] decodedString = Base64.decode(Str_EncodeImage, Base64.DEFAULT);
                    mDecodedImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    holder.iv_profile.setImageBitmap(mDecodedImage);

                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            mItemCallBack.onStudentListItemClicked(position);

                        } catch (Exception ex) {
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
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_student_list_item,parent,false);
            mHolder = new Students_ListHolders(layoutView);
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
        return  (isFooterEnabled) ? mListStudents.size() + 1 : mListStudents.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        return (isFooterEnabled && position >= mListStudents.size() ) ? VIEW_PROG : VIEW_ITEM;
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

    public static class Students_ListHolders extends RecyclerView.ViewHolder
    {
        //Declaring parent view items
        public TextView  tv_student_name,tv_phone_no,tv_email;
        public ImageView iv_profile;
        public View itemView;


        public Students_ListHolders(View itemView)
        {
            super(itemView);
            try
            {
                this.itemView = itemView;
                tv_student_name = (TextView) itemView.findViewById(R.id.tv_student_name);
                tv_phone_no = (TextView) itemView.findViewById(R.id.tv_phone_no);
                tv_email = (TextView) itemView.findViewById(R.id.tv_email);
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
                tv_student_name.setTypeface(font.getHelveticaRegular());
                tv_phone_no.setTypeface(mTypeFace);
                tv_email.setTypeface(mTypeFace);
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
