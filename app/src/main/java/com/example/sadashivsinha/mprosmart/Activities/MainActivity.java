package com.example.sadashivsinha.mprosmart.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.Adapters.MainPagerAdapter;
import com.example.sadashivsinha.mprosmart.Adapters.MyAdapter;
import com.example.sadashivsinha.mprosmart.R;

public class MainActivity extends NewActivity implements View.OnClickListener {

    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView view_details_btn = (TextView) findViewById(R.id.view_details_btn);
        view_details_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,FormActivity.class);
                startActivity(intent);
            }
        });
        com.github.clans.fab.FloatingActionButton fab_mom, fab_site_diary, fab_sub, fab_sub_register, fab_change_order,
                fab_sub_timesheet, fab_res_timesheet;

        fab_mom = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_mom);
        fab_site_diary = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_site_diary);
        fab_sub = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_sub);
        fab_sub_register = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_sub_register);
        fab_change_order = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_change_order);
        fab_sub_timesheet = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_sub_timesheet);
        fab_res_timesheet = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_res_timesheet);


        fab_mom.setOnClickListener(this);
        fab_site_diary.setOnClickListener(this);
        fab_sub.setOnClickListener(this);
        fab_sub_register.setOnClickListener(this);
        fab_change_order.setOnClickListener(this);
        fab_sub_timesheet.setOnClickListener(this);
        fab_res_timesheet.setOnClickListener(this);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Purchase Receipts"));
        tabLayout.addTab(tabLayout.newTab().setText("Quality Control"));
        tabLayout.addTab(tabLayout.newTab().setText("Statistics"));
        tabLayout.addTab(tabLayout.newTab().setText("Project location"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final MainPagerAdapter adapter = new MainPagerAdapter
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

        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View

        mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size

        mAdapter = new MyAdapter(TITLES,ICONS,NAME,EMAIL,PROFILE);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,header view name, header view email,
        // and header view profile picture

        mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView

        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager

        mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager


        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);        // Drawer object Assigned to the view
        mDrawerToggle = new ActionBarDrawerToggle(this,Drawer,toolbar,R.string.openDrawer,R.string.closeDrawer){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
                // open I am not going to put anything here)
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
            }



        }; // Drawer Toggle Object Made
        Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MainActivity.this, PurchaseOrders.class);
        startActivity(intent);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_mom:
            {
                Intent intent = new Intent(MainActivity.this, MomActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.fab_site_diary:
            {
                Intent intent = new Intent(MainActivity.this, SiteDiaryActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.fab_sub:
            {
                Intent intent = new Intent(MainActivity.this, SubmittalActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.fab_sub_register:
            {
                Intent intent = new Intent(MainActivity.this, SubmittalRegisterActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.fab_change_order:
            {
                Intent intent = new Intent(MainActivity.this, ChangeOrders.class);
                startActivity(intent);
            }
            break;
            case R.id.fab_sub_timesheet:
            {
                Intent intent = new Intent(MainActivity.this, SubcontractorActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.fab_res_timesheet:
            {
                Intent intent = new Intent(MainActivity.this, ResourceTimesheetActivity.class);
                startActivity(intent);
            }
            break;
        }
    }
}
