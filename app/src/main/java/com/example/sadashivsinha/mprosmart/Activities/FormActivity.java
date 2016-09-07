package com.example.sadashivsinha.mprosmart.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sadashivsinha.mprosmart.Adapters.MyAdapter;
import com.example.sadashivsinha.mprosmart.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;


public class FormActivity extends NewActivity implements DatePickerDialog.OnDateSetListener {
    TextView dateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button cancelBtn, createBtn;
        cancelBtn = (Button) findViewById(R.id.cancelBtn);
        createBtn = (Button) findViewById(R.id.createBtn);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FormActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FormActivity.this, "Created", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(FormActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        dateTextView = (TextView) findViewById(R.id.dateBtn);

        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        FormActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

//        Spinner spinner_date = (Spinner) findViewById(R.id.date);
//        Spinner spinner_month = (Spinner) findViewById(R.id.month);
//        Spinner spinner_year = (Spinner) findViewById(R.id.year);
//
//
//        DateFormat dateFormat = new SimpleDateFormat("dd");
//        DateFormat monthFormat = new SimpleDateFormat("MM");
//        DateFormat yearFormat = new SimpleDateFormat("yyyy");
//        //get current date
//        Date date = new Date();
//
//
//        ArrayAdapter<String> adapter;
//        List<String> list;
//
//        String curr_date = String.valueOf(dateFormat.format(date));
//
//        list = new ArrayList<String>();
//        list.add(curr_date);
//
//        for(int i=1; i<=31; i++)
//        {
//            list.add(""+i);
//        }
//
//        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_items, list);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner_date.setAdapter(adapter);
//
//        String curr_month = String.valueOf(monthFormat.format(date));
//
//        list = new ArrayList<String>();
//        list.add(curr_month);
//
//        for(int i=1; i<=12; i++)
//        {
//            list.add(""+i);
//        }
//
//        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_items, list);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner_month.setAdapter(adapter);
//
//
//
//        String curr_year = String.valueOf(yearFormat.format(date));
//        list = new ArrayList<String>();
//        list.add(curr_year);
//
//        for(int i=1960; i<2060; i++)
//        {
//            list.add(""+i);
//        }
//
//        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_items, list);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner_year.setAdapter(adapter);

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
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        String date = "Selected Date : "+dayOfMonth+" - "+(MONTHS[monthOfYear])+" - "+year;
        dateTextView.setText(date);
    }
}
