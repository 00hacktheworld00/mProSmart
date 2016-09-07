package com.example.sadashivsinha.mprosmart.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sadashivsinha.mprosmart.Adapters.MainPagerAdapter;
import com.example.sadashivsinha.mprosmart.Adapters.MyAdapter;
import com.example.sadashivsinha.mprosmart.R;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import de.hdodenhof.circleimageview.CircleImageView;

public class Abc extends NewActivity implements View.OnClickListener {

    TextView title, descriptionOne, descriptionTwo, startedDate, finishByDate, purchaseReceipts, itemsReceived,
            last_updated_time, project_number;

    CircleImageView company_logo;
    private Toolbar toolbar;
    com.github.clans.fab.FloatingActionMenu menu;

    com.github.clans.fab.FloatingActionButton fab_search,fab_new_receipt,fab_mom, fab_site_diary, fab_sub,
            fab_sub_register, fab_change_order, fab_sub_timesheet, fab_res_timesheet, fab_more;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_purchase_orders);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String projectNo = getIntent().getStringExtra("projectNo");

        title = (TextView) findViewById(R.id.title);
        descriptionOne = (TextView) findViewById(R.id.descriptionOne);
        descriptionTwo = (TextView) findViewById(R.id.descriptionTwo);
        startedDate = (TextView) findViewById(R.id.startedDate);
        finishByDate = (TextView) findViewById(R.id.finishByDate);
        purchaseReceipts = (TextView) findViewById(R.id.purchaseReceipts);
        itemsReceived = (TextView) findViewById(R.id.itemsReceived);
        last_updated_time = (TextView) findViewById(R.id.last_updated_time);
        project_number = (TextView) findViewById(R.id.project_number);
        company_logo = (CircleImageView) findViewById(R.id.company_logo);

        if (projectNo.equals("1") || projectNo.isEmpty())
        {
            startedDate.setText("16 Dec 2015");
            finishByDate.setText("20 Jan 2016");
            purchaseReceipts.setText("7");
            itemsReceived.setText("60%");
            descriptionOne.setText("4G Tablet Project");
            descriptionTwo.setText("Development Stage");
            company_logo.setImageResource(R.drawable.logo_one);
            last_updated_time.setText("4:00 PM");
            title.setText("Design New Project");
            project_number.setText("1");
        }

        else if (projectNo.equals("2"))
        {
            startedDate.setText("26 Dec 2015");
            finishByDate.setText("18 Dec 2016");
            purchaseReceipts.setText("10");
            itemsReceived.setText("80%");
            descriptionOne.setText("Juniper Nursing Home");
            descriptionTwo.setText("Structure");
            company_logo.setImageResource(R.drawable.logo_two);
            last_updated_time.setText("4 days ago");
            title.setText("Fourth Floor Slab and Collar Beam");
            project_number.setText("2");
        }


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Purchase Orders"));
        tabLayout.addTab(tabLayout.newTab().setText("Quality Control"));

        tabLayout.addTab(tabLayout.newTab().setText("MOM"));
        tabLayout.addTab(tabLayout.newTab().setText("Submittals"));
        tabLayout.addTab(tabLayout.newTab().setText("Submittal Register"));
        tabLayout.addTab(tabLayout.newTab().setText("Subcontractor Timesheet"));
        tabLayout.addTab(tabLayout.newTab().setText("Resource Timesheet"));
        tabLayout.addTab(tabLayout.newTab().setText("Project Location"));
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
        mAdapter = new MyAdapter(TITLES, ICONS, NAME, EMAIL, PROFILE);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,header view name, header view email,
        // and header view profile picture
        mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView
        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager
        mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager
        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);        // Drawer object Assigned to the view
        mDrawerToggle = new ActionBarDrawerToggle(this, Drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {
        }; // Drawer Toggle Object Made
        Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State


        fab_search = (FloatingActionButton) findViewById(R.id.fab_search);
        fab_new_receipt = (FloatingActionButton) findViewById(R.id.fab_new_receipt);

        fab_mom = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_mom);
        fab_site_diary = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_site_diary);
        fab_sub = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_sub);
        fab_sub_register = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_sub_register);
        fab_change_order = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_change_order);
        fab_sub_timesheet = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_sub_timesheet);
        fab_res_timesheet = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_res_timesheet);
        fab_more = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_more);


        fab_mom.setOnClickListener(this);
        fab_site_diary.setOnClickListener(this);
        fab_sub.setOnClickListener(this);
        fab_sub_register.setOnClickListener(this);
        fab_change_order.setOnClickListener(this);
        fab_sub_timesheet.setOnClickListener(this);
        fab_res_timesheet.setOnClickListener(this);
        fab_search.setOnClickListener(this);
        fab_new_receipt.setOnClickListener(this);
        fab_more.setOnClickListener(this);

        menu = (FloatingActionMenu) findViewById(R.id.menu);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_mom:
            {
                Intent intent = new Intent(Abc.this, MomCreateActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.fab_site_diary:
            {
                Intent intent = new Intent(Abc.this, SiteDiaryActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.fab_sub:
            {
                Intent intent = new Intent(Abc.this, SubmittalsCreateActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.fab_sub_register:
            {
                Intent intent = new Intent(Abc.this, SubmittalRegisterCreateActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.fab_change_order:
            {
                Intent intent = new Intent(Abc.this, ChangeOrders.class);
                startActivity(intent);
            }
            break;
            case R.id.fab_sub_timesheet:
            {
                Intent intent = new Intent(Abc.this, SubcontractorCreateActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.fab_res_timesheet:
            {
                Intent intent = new Intent(Abc.this, ResourceTimesheetCreate.class);
                startActivity(intent);
            }
            break;
            case R.id.fab_more:
            {
                //on clicked more button

                if(fab_more.getLabelText().toString().equals("MORE"))
                {
                    fab_sub_register.setVisibility(View.VISIBLE);
                    fab_sub.setVisibility(View.VISIBLE);
                    fab_site_diary.setVisibility(View.VISIBLE);
                    fab_mom.setVisibility(View.VISIBLE);


                    fab_change_order.setVisibility(View.GONE);
                    fab_sub_timesheet.setVisibility(View.GONE);
                    fab_res_timesheet.setVisibility(View.GONE);
                    fab_new_receipt.setVisibility(View.GONE);
                    fab_search.setVisibility(View.GONE);

                    fab_more.setLabelText("BACK");
                    fab_more.setImageResource(R.drawable.icon_back);
                }

                else if(fab_more.getLabelText().toString().equals("BACK"))
                {
                    fab_sub_register.setVisibility(View.GONE);
                    fab_sub.setVisibility(View.GONE);
                    fab_site_diary.setVisibility(View.GONE);
                    fab_mom.setVisibility(View.GONE);


                    fab_change_order.setVisibility(View.VISIBLE);
                    fab_sub_timesheet.setVisibility(View.VISIBLE);
                    fab_res_timesheet.setVisibility(View.VISIBLE);
                    fab_new_receipt.setVisibility(View.VISIBLE);
                    fab_search.setVisibility(View.VISIBLE);

                    fab_more.setLabelText("MORE");
                    fab_more.setImageResource(R.drawable.icon_more);

                }

            }
            break;

            case R.id.fab_search:
            {

                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Search");
                // Set an EditText view to get user input
                final EditText input = new EditText(this);
                alert.setView(input);
                alert.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(Abc.this, "Search for it .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(Abc.this, "Search cancelled .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();

            }
            break;
            case R.id.fab_new_receipt:
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("New Purchase Receipt :-");
                // Set an EditText view to get user input
                final TextView input = new TextView(this);

                final View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_new_receipt, null);

                alert.setView(dialogView);
                BetterSpinner spinner = (BetterSpinner )dialogView.findViewById(R.id.spinnerOrders);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_dropdown_item_1line,
                        new String[] {
                                "Purchase Order - 101","Purchase Order - 102","Purchase Order - 103","Purchase Order - 104",});
                spinner.setAdapter(adapter);

                alert.setPositiveButton("Create Receipt", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        Intent intent = new Intent(Abc.this, NewPurchaseReceipts.class);
                        startActivity(intent);
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(getApplicationContext(), "Cancelled .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
            }
            break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Abc.this, AllProjectsActivity.class);
        intent.putExtra("projectNo","1");
        startActivity(intent);
    }
}