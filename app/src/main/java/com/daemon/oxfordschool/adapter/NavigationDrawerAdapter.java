package com.daemon.oxfordschool.adapter;

/**
 * Created by Ravi on 29/07/15.
 */
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.Font;
import com.daemon.oxfordschool.model.NavDrawerItem;

public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.MyViewHolder>
{
    List<NavDrawerItem> data = Collections.emptyList();
    private LayoutInflater inflater;
    private AppCompatActivity mActivity;
    private Font font= MyApplication.getInstance().getFontInstance();
    private int mSelectedPosition;
    private int mTouchedPosition = -1;

    public NavigationDrawerAdapter(AppCompatActivity mActivity, List<NavDrawerItem> data)
    {
        this.mActivity = mActivity;
        inflater = LayoutInflater.from(mActivity);
        this.data = data;
    }

    public void delete(int position)
    {
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = inflater.inflate(R.layout.nav_drawer_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)
    {
        NavDrawerItem current = data.get(position);
        holder.title.setText(current.getTitle());
        if (mSelectedPosition == position || mTouchedPosition == position)
        {
            holder.itemView.setBackground(mActivity.getResources().getDrawable(R.drawable.list_item_bg_pressed));
        }
        else
        {
            holder.itemView.setBackground(mActivity.getResources().getDrawable(R.drawable.list_item_bg_normal));
        }
    }

    @Override
    public int getItemCount()
    {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView title;
        View itemView;

        public MyViewHolder(View itemView)
        {
            super(itemView);
            this.itemView = itemView;
            title = (TextView) itemView.findViewById(R.id.title);
            title.setTypeface(font.getHelveticaRegular());
        }
    }

    public void selectPosition(int position)
    {
        int lastPosition = mSelectedPosition;
        mSelectedPosition = position;
        notifyItemChanged(lastPosition);
        notifyItemChanged(position);
    }


}
