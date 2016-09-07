package com.example.sadashivsinha.mprosmart.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.sadashivsinha.mprosmart.Adapters.MainAdapter;
import com.example.sadashivsinha.mprosmart.Adapters.MyAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.ItemList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.Utils.WrappingLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class NewPurchase extends NewActivity {

    private List<ItemList> itemList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MainAdapter mainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_purchase);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton addBtn = (FloatingActionButton) findViewById(R.id.addBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewPurchase.this, PurchaseEntry.class);
                startActivity(intent);
            }
        });
        mainAdapter = new MainAdapter(itemList);
        recyclerView = (RecyclerView) findViewById(R.id.purchase_recycler_view);

        recyclerView.setLayoutManager(new WrappingLinearLayoutManager(getApplicationContext()));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(mainAdapter);

        prepareItemList();

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


    private void prepareItemList() {
        ItemList items = new ItemList("101", "80","100", "20", "10 Feb 2016");
        itemList.add(items);


        items = new ItemList("102", "10","20", "10", "3 Feb 2016");
        itemList.add(items);



        items = new ItemList("103", "25","50", "25", "1 Feb 2016");
        itemList.add(items);



        items = new ItemList("104", "5","100", "95", "30 Jan 2016");
        itemList.add(items);



        items = new ItemList("105", "5","7", "2", "7 Jan 2016");
        itemList.add(items);



        items = new ItemList("106", "10","50", "40", "30 Dec 2015");
        itemList.add(items);



        mainAdapter.notifyDataSetChanged();
    }

}
