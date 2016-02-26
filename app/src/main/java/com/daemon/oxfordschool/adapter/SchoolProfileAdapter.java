package com.daemon.oxfordschool.adapter;

/**
 * Created by daemonsoft on 26/2/16.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.daemon.oxfordschool.fragment.Fragment_About_Us;
import com.daemon.oxfordschool.fragment.Fragment_Aims;
import com.daemon.oxfordschool.fragment.Fragment_Facilities;

public class SchoolProfileAdapter extends FragmentStatePagerAdapter
{
    int mNumOfTabs;

    public SchoolProfileAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                 Fragment_About_Us tab1 = new Fragment_About_Us();
                 return tab1;
            case 1:
                 Fragment_Aims tab2 = new Fragment_Aims();
                 return tab2;
            case 2:
                 Fragment_Facilities tab3 = new Fragment_Facilities();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}
