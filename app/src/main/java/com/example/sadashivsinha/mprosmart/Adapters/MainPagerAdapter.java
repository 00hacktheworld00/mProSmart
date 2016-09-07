package com.example.sadashivsinha.mprosmart.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.sadashivsinha.mprosmart.Fragments.FragmentMain;
import com.example.sadashivsinha.mprosmart.Fragments.FragmentMom;
import com.example.sadashivsinha.mprosmart.Fragments.FragmentResourceTimesheet;
import com.example.sadashivsinha.mprosmart.Fragments.FragmentSubcontractor;
import com.example.sadashivsinha.mprosmart.Fragments.FragmentSubmittals;
import com.example.sadashivsinha.mprosmart.Fragments.FragmentSubmittalsRegister;
import com.example.sadashivsinha.mprosmart.Fragments.ProjectLocationFragment;
import com.example.sadashivsinha.mprosmart.Fragments.QualityControlFragment;

/**
 * Created by saDashiv sinha on 01-Mar-16.
 */
public class MainPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public MainPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new FragmentMain();
            case 1:
                return new QualityControlFragment();
            case 2:
                return new FragmentMom();
            case 3:
                return new FragmentSubmittals();
            case 4:
                return new FragmentSubmittalsRegister();
            case 5:
                return new FragmentSubcontractor();
            case 6:
                return new FragmentResourceTimesheet();
            case 7:
                return new ProjectLocationFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}