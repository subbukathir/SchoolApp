package com.daemon.oxfordschool.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.Utils.Font;
import com.daemon.oxfordschool.asyncprocess.SectionList_Process;
import com.daemon.oxfordschool.asyncprocess.SubjectList_Process;
import com.daemon.oxfordschool.classes.Common_Class;
import com.daemon.oxfordschool.classes.User;
import com.daemon.oxfordschool.listeners.SectionListListener;
import com.daemon.oxfordschool.listeners.SubjectListListener;
import com.daemon.oxfordschool.response.CommonList_Response;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by daemonsoft on 2/2/16.
 */
public class TimeTablePagerAdapter extends PagerAdapter
{

    public static String MODULE = "TimeTablePagerAdapter";
    public static String TAG = "";

    ArrayList<Common_Class> mSubjectList;
    AppCompatActivity mActivity;
    SharedPreferences mPreferences;
    Object[] ObjTbl;
    LayoutInflater mLayoutInflater;
    Font font = MyApplication.getInstance().getFontInstance();
    String Str_Sub1,Str_Sub2,Str_Sub3,Str_Sub4,Str_Sub5,Str_Sub6,Str_Sub7,Str_Sub8="";
    String[] day=null;
    int mDaysCount=0,mSubjectListCount=0;


    TextView tv_lbl_hour1 ,tv_lbl_hour2, tv_lbl_hour3 ,tv_lbl_hour4,tv_lbl_hour5,tv_lbl_hour6,tv_lbl_hour7,tv_lbl_hour8;
    TextView tv_hour1, tv_hour2, tv_hour3, tv_hour4, tv_hour5, tv_hour6,tv_hour7, tv_hour8,tv_day;
    String[] array_days;

    public TimeTablePagerAdapter(AppCompatActivity mActivity, Object[] ObjTbl,ArrayList<Common_Class> mSubjectList)
    {
        this.mActivity = mActivity;
        this.mSubjectList=mSubjectList;
        this.ObjTbl=ObjTbl;
        mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS,Context.MODE_PRIVATE);
        mLayoutInflater = (LayoutInflater) mActivity.getSystemService(mActivity.LAYOUT_INFLATER_SERVICE);
        array_days = mActivity.getResources().getStringArray(R.array.array_days);
        mSubjectListCount = mSubjectList.size();
    }

    @Override
    public int getCount() {
        return ObjTbl.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ScrollView) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView=null;
        try
        {
            itemView = mLayoutInflater.inflate(R.layout.view_item_time_table, container, false);

            day = (String[]) ObjTbl[position];

            mDaysCount = day.length;

            tv_day = (TextView) itemView.findViewById(R.id.tv_day);
            tv_lbl_hour1 = (TextView) itemView.findViewById(R.id.tv_lbl_hour1);
            tv_lbl_hour2 = (TextView) itemView.findViewById(R.id.tv_lbl_hour2);
            tv_lbl_hour3 = (TextView) itemView.findViewById(R.id.tv_lbl_hour3);
            tv_lbl_hour4 = (TextView) itemView.findViewById(R.id.tv_lbl_hour4);
            tv_lbl_hour5 = (TextView) itemView.findViewById(R.id.tv_lbl_hour5);
            tv_lbl_hour6 = (TextView) itemView.findViewById(R.id.tv_lbl_hour6);
            tv_lbl_hour7 = (TextView) itemView.findViewById(R.id.tv_lbl_hour7);
            tv_lbl_hour8 = (TextView) itemView.findViewById(R.id.tv_lbl_hour8);

            tv_hour1 = (TextView) itemView.findViewById(R.id.tv_hour1);
            tv_hour2 = (TextView) itemView.findViewById(R.id.tv_hour2);
            tv_hour3 = (TextView) itemView.findViewById(R.id.tv_hour3);
            tv_hour4 = (TextView) itemView.findViewById(R.id.tv_hour4);
            tv_hour5 = (TextView) itemView.findViewById(R.id.tv_hour5);
            tv_hour6 = (TextView) itemView.findViewById(R.id.tv_hour6);
            tv_hour7 = (TextView) itemView.findViewById(R.id.tv_hour7);
            tv_hour8 = (TextView) itemView.findViewById(R.id.tv_hour8);

            if(tv_day!=null) tv_day.setTypeface(font.getHelveticaBold());
            if(tv_lbl_hour1!=null) tv_lbl_hour1.setTypeface(font.getHelveticaBold());
            if(tv_lbl_hour2!=null) tv_lbl_hour2.setTypeface(font.getHelveticaBold());
            if(tv_lbl_hour3!=null) tv_lbl_hour3.setTypeface(font.getHelveticaBold());
            if(tv_lbl_hour4!=null) tv_lbl_hour4.setTypeface(font.getHelveticaBold());
            if(tv_lbl_hour5!=null) tv_lbl_hour5.setTypeface(font.getHelveticaBold());
            if(tv_lbl_hour6!=null) tv_lbl_hour6.setTypeface(font.getHelveticaBold());
            if(tv_lbl_hour7!=null) tv_lbl_hour7.setTypeface(font.getHelveticaBold());
            if(tv_lbl_hour8!=null) tv_lbl_hour8.setTypeface(font.getHelveticaBold());

            if(tv_hour1!=null) tv_hour1.setTypeface(font.getHelveticaRegular());
            if(tv_hour2!=null) tv_hour2.setTypeface(font.getHelveticaRegular());
            if(tv_hour3!=null) tv_hour3.setTypeface(font.getHelveticaRegular());
            if(tv_hour4!=null) tv_hour4.setTypeface(font.getHelveticaRegular());
            if(tv_hour5!=null) tv_hour5.setTypeface(font.getHelveticaRegular());
            if(tv_hour6!=null) tv_hour6.setTypeface(font.getHelveticaRegular());
            if(tv_hour7!=null) tv_hour7.setTypeface(font.getHelveticaRegular());
            if(tv_hour8!=null) tv_hour8.setTypeface(font.getHelveticaRegular());

            if(tv_day!=null)
            {
                tv_day.setText(array_days[position]);
            }

            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setSubjects();
                }
            });

            container.addView(itemView);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ScrollView) object);
    }

    /*@Override
    public float getPageWidth(int position) {

        if (position < mListStudents.size()) {
            return 0.8f;
        }
        return 1f;
    }*/



    public String getSubjectName(String Str_SubjectId)
    {
        TAG = "getSubjectName";
        Log.d(MODULE, TAG);

        String Str="";
        try
        {
            if(mSubjectList!=null && day!=null)
            {
                if(mDaysCount>0 && mSubjectListCount>0)
                {
                    for(int i=0;i<mSubjectListCount;i++)
                    {
                        if(Str_SubjectId.equals(mSubjectList.get(i).getID()))
                        {
                            Str=mSubjectList.get(i).getName();
                        }
                    }
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return Str;
    }

    public void setSubjects()
    {
        TAG = "setSubjects";
        Log.d(MODULE, TAG);
        try
        {
            Str_Sub1 = getSubjectName(day[0]);
            Str_Sub2 = getSubjectName(day[1]);
            Str_Sub3 = getSubjectName(day[2]);
            Str_Sub4 = getSubjectName(day[3]);
            Str_Sub5 = getSubjectName(day[4]);
            Str_Sub6 = getSubjectName(day[5]);
            Str_Sub7 = getSubjectName(day[6]);
            Str_Sub8 = getSubjectName(day[7]);

            if(tv_hour1!=null)
            {
                tv_hour1.setText(Str_Sub1);
            }
            if(tv_hour2!=null)
            {
                tv_hour2.setText(Str_Sub2);
            }
            if(tv_hour3!=null)
            {
                tv_hour3.setText(Str_Sub3);
            }
            if(tv_hour4!=null)
            {
                tv_hour4.setText(Str_Sub4);
            }
            if(tv_hour5!=null)
            {
                tv_hour5.setText(Str_Sub5);
            }
            if(tv_hour6!=null)
            {
                tv_hour6.setText(Str_Sub6);
            }
            if(tv_hour7!=null)
            {
                tv_hour7.setText(Str_Sub7);
            }
            if(tv_hour8!=null)
            {
                tv_hour8.setText(Str_Sub8);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    @Override
    public CharSequence getPageTitle(int position) {
       return array_days[position];
    }


}