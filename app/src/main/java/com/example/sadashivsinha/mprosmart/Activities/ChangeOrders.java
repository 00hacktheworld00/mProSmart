package com.example.sadashivsinha.mprosmart.Activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.Adapters.ChangeOrdersPagerAdapter;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;

public class ChangeOrders extends AppCompatActivity {
    TextView project_id, project_name, created_by, date_created, due_date, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_orders);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        project_id = (TextView) findViewById(R.id.project_id);
        project_name = (TextView) findViewById(R.id.project_name);
        created_by = (TextView) findViewById(R.id.created_by);
        date_created = (TextView) findViewById(R.id.date_created);
        due_date = (TextView) findViewById(R.id.due_date);
        title = (TextView) findViewById(R.id.title);

        PreferenceManager pm = new PreferenceManager(ChangeOrders.this);
        project_id.setText(pm.getString("text_project_id"));
        project_name.setText(pm.getString("text_project_name"));
        created_by.setText(pm.getString("userId"));
        title.setText(pm.getString("text_title"));
        date_created.setText(pm.getString("text_date_created"));
        due_date.setText(pm.getString("text_due_date"));

//        HorizontalScrollView tab_scroll = (HorizontalScrollView) findViewById(R.id.tab_scroll);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Section - 1"));
        tabLayout.addTab(tabLayout.newTab().setText("Section - 2"));
        tabLayout.addTab(tabLayout.newTab().setText("Section - 3"));
        tabLayout.addTab(tabLayout.newTab().setText("Section - 4"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final ChangeOrdersPagerAdapter adapter = new ChangeOrdersPagerAdapter
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