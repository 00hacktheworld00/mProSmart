package com.example.sadashivsinha.mprosmart.Activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.sadashivsinha.mprosmart.Adapters.SiteDiaryPagerAdapter;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;

public class SiteDiaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_diary_new);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PreferenceManager pm = new PreferenceManager(getApplicationContext());

        if(getIntent().hasExtra("date"))
        {
            getSupportActionBar().setTitle("Site Diary for : " + pm.getString("currentSiteDate"));
        }
        else
        {
            getSupportActionBar().setTitle("Site Diary");
        }

//        HorizontalScrollView tab_scroll = (HorizontalScrollView) findViewById(R.id.tab_scroll);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Visitors on site"));
        tabLayout.addTab(tabLayout.newTab().setText("Hours"));
        tabLayout.addTab(tabLayout.newTab().setText("Material Receipt"));
        tabLayout.addTab(tabLayout.newTab().setText("Material Issued"));
        tabLayout.addTab(tabLayout.newTab().setText("Project Delays"));
        tabLayout.addTab(tabLayout.newTab().setText("Variations"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final SiteDiaryPagerAdapter adapter = new SiteDiaryPagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}