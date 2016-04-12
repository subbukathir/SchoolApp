package com.daemon.oxfordschool.adapter;

/**
 * Created by Ravi on 29/07/15.
 */
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.Font;
import com.daemon.oxfordschool.classes.Common_Class;
import com.daemon.oxfordschool.classes.TimeTableItem;
import com.daemon.oxfordschool.fragment.Fragment_TimeTable;


import java.util.ArrayList;

public class TimeTableAdapter extends BaseAdapter
{
    public static String MODULE = "TimeTableAdapter";
    public static String TAG = "";

    AppCompatActivity mActivity;
    private static LayoutInflater inflater=null;
    ArrayList<TimeTableItem> mListSubjectId = new ArrayList<TimeTableItem>();
    private Font font= MyApplication.getInstance().getFontInstance();
    ArrayList<Common_Class> mSubjectList;

    public TimeTableAdapter(AppCompatActivity mActivity,ArrayList<TimeTableItem> mListSubjectId,ArrayList<Common_Class> mSubjectList)
    {
        TAG = "TimeTableAdapter";
        Log.d(MODULE, TAG);
        // TODO Auto-generated constructor stub
        this.mActivity=mActivity;
        this.mListSubjectId =mListSubjectId;
        this.mSubjectList=mSubjectList;
        inflater = ( LayoutInflater )mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount()
    {
        // TODO Auto-generated method stub
        return mListSubjectId.size();
    }

    @Override
    public Object getItem(int position)
    {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position)
    {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView tv_day;
        LinearLayout layout_timetable_root;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        TAG = "getView";
        Log.d(MODULE, TAG);

        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.view_item_time_table, null);
        holder.tv_day=(TextView) rowView.findViewById(R.id.tv_day);
        holder.layout_timetable_root = (LinearLayout) rowView.findViewById(R.id.layout_timetable_root);
        TimeTableItem item = mListSubjectId.get(position);
        Log.d(MODULE, TAG + " item : " + item.getType());
        if(item.getType() == Fragment_TimeTable.HEADER)
        {
            holder.tv_day.setTypeface(font.getHelveticaBold());
            holder.tv_day.setTextColor(Color.WHITE);
            holder.layout_timetable_root.setBackground(new ColorDrawable(Color.parseColor("#C81E5E")));
            holder.tv_day.setText(item.getName());
        }
        else
        {
            holder.tv_day.setTypeface(font.getHelveticaRegular());
            holder.tv_day.setTextColor(Color.parseColor("#C81E5E"));
            holder.layout_timetable_root.setBackground(new ColorDrawable(Color.WHITE));
            holder.tv_day.setText(getSubjectName(item.getName()));
        }


        Log.d(MODULE, TAG + " getName : " + item.getName());
        return rowView;
    }

    public String getSubjectName(String Str_SubjectId)
    {
        TAG = "getSubjectName";
        Log.d(MODULE, TAG);

        String Str="";
        try
        {
            if(mSubjectList!=null)
            {
                for(int i=0;i<mSubjectList.size();i++)
                {
                    if(Str_SubjectId.equals(mSubjectList.get(i).getID()))
                    {
                        Str=mSubjectList.get(i).getName();
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

} 