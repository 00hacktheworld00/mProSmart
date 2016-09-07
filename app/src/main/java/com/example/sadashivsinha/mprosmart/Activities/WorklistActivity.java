package com.example.sadashivsinha.mprosmart.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.sadashivsinha.mprosmart.Adapters.MyAdapter;
import com.example.sadashivsinha.mprosmart.R;

public class WorklistActivity extends NewActivity {

    EditText remarks_text;
    RelativeLayout acceptBtn, rejectBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worklist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        remarks_text = (EditText) findViewById(R.id.remarks_text);
        acceptBtn = (RelativeLayout) findViewById(R.id.acceptBtn);
        rejectBtn = (RelativeLayout) findViewById(R.id.rejectBtn);

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(remarks_text.getText().toString().equals(""))
                {
                    Toast.makeText(WorklistActivity.this, "Remarks field cannot be empty.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(WorklistActivity.this, "Request Accepted", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(WorklistActivity.this, NewAllProjects.class);
                    startActivity(intent);
                }
            }
        });


        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(remarks_text.getText().toString().equals(""))
                {
                    Toast.makeText(WorklistActivity.this, "Remarks field cannot be empty.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(WorklistActivity.this, "Request Rejected", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(WorklistActivity.this, NewAllProjects.class);
                    startActivity(intent);
                }
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
        Intent intent = new Intent(WorklistActivity.this, NewAllProjects.class);
        startActivity(intent);
    }
}