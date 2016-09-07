package com.example.sadashivsinha.mprosmart.Activities;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.sadashivsinha.mprosmart.Adapters.InspectionAdapter;
import com.example.sadashivsinha.mprosmart.Adapters.MyAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.InspectionList;
import com.example.sadashivsinha.mprosmart.R;
import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class InspectionReportActivity extends NewActivity implements View.OnClickListener   {

    private List<InspectionList> inspectionList = new ArrayList<>();
    private RecyclerView recyclerView;
    private InspectionAdapter inspectionAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inspectionAdapter = new InspectionAdapter(inspectionList);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(inspectionAdapter);
        prepareItems();

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
        FloatingActionButton fab_mom, fab_site_diary, fab_sub, fab_sub_register, fab_change_order,
                fab_sub_timesheet, fab_res_timesheet;

        fab_mom = (FloatingActionButton) findViewById(R.id.fab_mom);
        fab_site_diary = (FloatingActionButton) findViewById(R.id.fab_site_diary);
        fab_sub = (FloatingActionButton) findViewById(R.id.fab_sub);
        fab_sub_register = (FloatingActionButton) findViewById(R.id.fab_sub_register);
        fab_change_order = (FloatingActionButton) findViewById(R.id.fab_change_order);
        fab_sub_timesheet = (FloatingActionButton) findViewById(R.id.fab_sub_timesheet);
        fab_res_timesheet = (FloatingActionButton) findViewById(R.id.fab_res_timesheet);


        fab_mom.setOnClickListener(this);
        fab_site_diary.setOnClickListener(this);
        fab_sub.setOnClickListener(this);
        fab_sub_register.setOnClickListener(this);
        fab_change_order.setOnClickListener(this);
        fab_sub_timesheet.setOnClickListener(this);
        fab_res_timesheet.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_mom:
            {
                Intent intent = new Intent(InspectionReportActivity.this, MomActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.fab_site_diary:
            {
                Intent intent = new Intent(InspectionReportActivity.this, SiteDiaryActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.fab_sub:
            {
                Intent intent = new Intent(InspectionReportActivity.this, SubmittalActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.fab_sub_register:
            {
                Intent intent = new Intent(InspectionReportActivity.this, SubmittalRegisterActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.fab_change_order:
            {
                Intent intent = new Intent(InspectionReportActivity.this, ChangeOrders.class);
                startActivity(intent);
            }
            break;
            case R.id.fab_sub_timesheet:
            {
                Intent intent = new Intent(InspectionReportActivity.this, SubcontractorActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.fab_res_timesheet:
            {
                Intent intent = new Intent(InspectionReportActivity.this, ResourceTimesheetActivity.class);
                startActivity(intent);
            }
            break;
        }
    }

    public void prepareItems()
    {
        InspectionList items = new InspectionList("001", "1","Item desciption will be written here. This is just a sample of desciption.", "kgs");
        inspectionList.add(items);

        items = new InspectionList("002", "2","Item desciption will be written here. This is just a sample of desciption.", "ml");
        inspectionList.add(items);

        items = new InspectionList("003", "3","Item desciption will be written here. This is just a sample of desciption.", "litre");
        inspectionList.add(items);

        items = new InspectionList("004", "4","Item desciption will be written here. This is just a sample of desciption.", "items");
        inspectionList.add(items);

        items = new InspectionList("005", "5","Item desciption will be written here. This is just a sample of desciption.", "cm");
        inspectionList.add(items);

        items = new InspectionList("006", "6","Item desciption will be written here. This is just a sample of desciption.", "kms");
        inspectionList.add(items);


        inspectionAdapter.notifyDataSetChanged();
    }

}
