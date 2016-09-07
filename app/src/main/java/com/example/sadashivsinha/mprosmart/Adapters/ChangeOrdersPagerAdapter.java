package com.example.sadashivsinha.mprosmart.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.sadashivsinha.mprosmart.Fragments.FragmentChangeOrderFour;
import com.example.sadashivsinha.mprosmart.Fragments.FragmentChangeOrderOne;
import com.example.sadashivsinha.mprosmart.Fragments.FragmentChangeOrderThree;
import com.example.sadashivsinha.mprosmart.Fragments.FragmentChangeOrderTwo;

/**
 * Created by saDashiv sinha on 26-May-16.
 */
public class ChangeOrdersPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public ChangeOrdersPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new FragmentChangeOrderOne();
            case 1:
                return new FragmentChangeOrderTwo();
            case 2:
                return new FragmentChangeOrderThree();
            case 3:
                return new FragmentChangeOrderFour();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}