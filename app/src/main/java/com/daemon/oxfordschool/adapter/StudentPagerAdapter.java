package com.daemon.oxfordschool.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.Font;
import com.daemon.oxfordschool.classes.User;

import java.util.ArrayList;

/**
 * Created by daemonsoft on 2/2/16.
 */
public class StudentPagerAdapter extends PagerAdapter
{

    AppCompatActivity mActivity;
    ArrayList<User> mListStudents;
    LayoutInflater mLayoutInflater;
    Font font = MyApplication.getInstance().getFontInstance();
    String Str_EncodeImage="";
    Bitmap mDecodedImage;

    public StudentPagerAdapter(AppCompatActivity mActivity,ArrayList<User> mListStudents) {
        this.mActivity = mActivity;
        this.mListStudents=mListStudents;
        mLayoutInflater = (LayoutInflater) mActivity.getSystemService(mActivity.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mListStudents.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView=null;
        try
        {
            itemView = mLayoutInflater.inflate(R.layout.view_student_pager_item, container, false);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.iv_profile);
            TextView tv_name = (TextView) itemView.findViewById(R.id.tv_header_name);
            TextView tv_class = (TextView) itemView.findViewById(R.id.tv_class_name);
            TextView tv_section = (TextView) itemView.findViewById(R.id.tv_section_name);
            User mStudent = mListStudents.get(position);
            StringBuilder Str_Name = new StringBuilder();
            Str_Name.append(mStudent.getFirstName()).append(" ");
            Str_Name.append(mStudent.getLastName());
            StringBuilder Str_ClassName = new StringBuilder();
            Str_ClassName.append(mActivity.getString(R.string.lbl_class)).append(" ");
            Str_ClassName.append(mStudent.getClassName());
            StringBuilder Str_SectionName = new StringBuilder();
            Str_SectionName.append(mActivity.getString(R.string.lbl_section)).append(" ");
            Str_SectionName.append(mStudent.getSectionName());
            Str_EncodeImage=mStudent.getImageData();

            if(!Str_EncodeImage.equals(""))
            {
                byte[] decodedString = Base64.decode(Str_EncodeImage, Base64.DEFAULT);
                mDecodedImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageView.setImageBitmap(mDecodedImage);
            }

            if(tv_name!=null)
            {
                tv_name.setTypeface(font.getHelveticaRegular());
                tv_name.setText(Str_Name.toString());
            }
            if(tv_class!=null)
            {
                tv_class.setTypeface(font.getHelveticaRegular());
                tv_class.setText(Str_ClassName);
            }
            if(tv_section!=null)
            {
                tv_section.setTypeface(font.getHelveticaRegular());
                tv_section.setText(Str_SectionName.toString());
            }
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
        container.removeView((RelativeLayout) object);
    }

    /*@Override
    public float getPageWidth(int position) {

        if (position < mListStudents.size()) {
            return 0.8f;
        }
        return 1f;
    }*/

}
