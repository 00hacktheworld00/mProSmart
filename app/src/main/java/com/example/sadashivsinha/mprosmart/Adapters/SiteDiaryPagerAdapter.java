package com.example.sadashivsinha.mprosmart.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.sadashivsinha.mprosmart.Fragments.FragmentSiteFive;
import com.example.sadashivsinha.mprosmart.Fragments.FragmentSiteFour;
import com.example.sadashivsinha.mprosmart.Fragments.FragmentSiteOne;
import com.example.sadashivsinha.mprosmart.Fragments.FragmentSiteSix;
import com.example.sadashivsinha.mprosmart.Fragments.FragmentSiteThree;
import com.example.sadashivsinha.mprosmart.Fragments.FragmentSiteTwo;

/**
 * Created by saDashiv sinha on 14-Mar-16.
 */
public class SiteDiaryPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public SiteDiaryPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new FragmentSiteOne();
            case 1:
                return new FragmentSiteTwo();
            case 2:
                return new FragmentSiteThree();
            case 3:
                return new FragmentSiteFour();
            case 4:
                return new FragmentSiteFive();
            case 5:
                return new FragmentSiteSix();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}