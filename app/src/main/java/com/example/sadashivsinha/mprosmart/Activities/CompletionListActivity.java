package com.example.sadashivsinha.mprosmart.Activities;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.Adapters.MyAdapter;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

public class CompletionListActivity extends NewActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    Button date_one, date_two,date_three, date_four, date_five, date_six, date_seven, date_eight, date_nine, date_ten, date_eleven,
            date_twelve, date_two_one, date_two_two, date_two_three, date_three_one, date_three_two,date_three_three,
            date_three_four, date_three_five, date_three_six;

    TextView project_id, project_name, date, created_by;

    String selectedBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completion_list);

        PreferenceManager pm = new PreferenceManager(CompletionListActivity.this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        project_id = (TextView) findViewById(R.id.project_no);
        project_name = (TextView) findViewById(R.id.project_name);
        created_by = (TextView) findViewById(R.id.created_by);

        project_id.setText(pm.getString("projectId"));
        project_name.setText(pm.getString("projectName"));
        created_by.setText(pm.getString("userId"));

        date = (TextView) findViewById(R.id.date);
        created_by = (TextView) findViewById(R.id.created_by);


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


        date_one = (Button) findViewById(R.id.date_one);
        date_two = (Button) findViewById(R.id.date_two);
        date_three = (Button) findViewById(R.id.date_three);
        date_four = (Button) findViewById(R.id.date_four);
        date_five = (Button) findViewById(R.id.date_five);
        date_six = (Button) findViewById(R.id.date_six);
        date_seven = (Button) findViewById(R.id.date_seven);
        date_eight = (Button) findViewById(R.id.date_eight);
        date_nine = (Button) findViewById(R.id.date_nine);
        date_ten = (Button) findViewById(R.id.date_ten);
        date_eleven = (Button) findViewById(R.id.date_eleven);
        date_twelve = (Button) findViewById(R.id.date_twelve);
        date_two_one = (Button) findViewById(R.id.date_two_one);
        date_two_two = (Button) findViewById(R.id.date_two_two);
        date_two_three = (Button) findViewById(R.id.date_two_three);
        date_three_one = (Button) findViewById(R.id.date_three_one);
        date_three_two = (Button) findViewById(R.id.date_three_two);
        date_three_three = (Button) findViewById(R.id.date_three_three);
        date_three_four = (Button) findViewById(R.id.date_three_four);
        date_three_five = (Button) findViewById(R.id.date_three_five);
        date_three_six = (Button) findViewById(R.id.date_three_six);


        date_one.setOnClickListener(this);
        date_two.setOnClickListener(this);
        date_three.setOnClickListener(this);
        date_four.setOnClickListener(this);
        date_five.setOnClickListener(this);
        date_six.setOnClickListener(this);
        date_seven.setOnClickListener(this);
        date_eight.setOnClickListener(this);
        date_nine.setOnClickListener(this);
        date_ten.setOnClickListener(this);
        date_eleven.setOnClickListener(this);
        date_twelve.setOnClickListener(this);
        date_two_one.setOnClickListener(this);
        date_two_two.setOnClickListener(this);
        date_two_three.setOnClickListener(this);
        date_three_one.setOnClickListener(this);
        date_three_two.setOnClickListener(this);
        date_three_three.setOnClickListener(this);
        date_three_four.setOnClickListener(this);
        date_three_five.setOnClickListener(this);
        date_three_six.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.date_one:
            {
                selectedBtn = "1";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CompletionListActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
            break;
            case R.id.date_two:
            {
                selectedBtn = "2";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CompletionListActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
            break;
            case R.id.date_three:
            {
                selectedBtn = "3";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CompletionListActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
            break;

            case R.id.date_four:
            {
                selectedBtn = "4";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CompletionListActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
            break;
            case R.id.date_five:
            {
                selectedBtn = "5";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CompletionListActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
            break;
            case R.id.date_six:
            {
                selectedBtn = "6";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CompletionListActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
            break;

            case R.id.date_seven:
            {
                selectedBtn = "7";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CompletionListActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
            break;
            case R.id.date_eight:
            {
                selectedBtn = "8";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CompletionListActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
            break;
            case R.id.date_nine:
            {
                selectedBtn = "9";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CompletionListActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
            break;

            case R.id.date_ten:
            {
                selectedBtn = "10";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CompletionListActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
            break;
            case R.id.date_eleven:
            {
                selectedBtn = "11";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CompletionListActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
            break;
            case R.id.date_twelve:
            {
                selectedBtn = "12";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CompletionListActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
            break;

            case R.id.date_two_one:
            {
                selectedBtn = "13";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CompletionListActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
            break;
            case R.id.date_two_two:
            {
                selectedBtn = "14";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CompletionListActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
            break;
            case R.id.date_two_three:
            {
                selectedBtn = "15";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CompletionListActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
            break;

            case R.id.date_three_one:
            {
                selectedBtn = "16";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CompletionListActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
            break;
            case R.id.date_three_two:
            {
                selectedBtn = "17";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CompletionListActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
            break;
            case R.id.date_three_three:
            {
                selectedBtn = "18";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CompletionListActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
            break;

            case R.id.date_three_four:
            {
                selectedBtn = "19";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CompletionListActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
            break;
            case R.id.date_three_five:
            {
                selectedBtn = "20";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CompletionListActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
            break;
            case R.id.date_three_six:
            {
                selectedBtn = "21";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CompletionListActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
            break;
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        String[] MONTHS = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        String stringDate = dayOfMonth+"-"+(MONTHS[monthOfYear])+"-"+year;


        switch (selectedBtn) {

            case "1":
            {
                date_one.setText(stringDate);
            }
            break;

            case "2":
            {
                date_two.setText(stringDate);
            }
            break;

            case "3":
            {
                date_three.setText(stringDate);
            }
            break;

            case "4":
            {
                date_four.setText(stringDate);
            }
            break;

            case "5":
            {
                date_five.setText(stringDate);
            }
            break;

            case "6":
            {
                date_six.setText(stringDate);
            }
            break;

            case "7":
            {
                date_seven.setText(stringDate);
            }
            break;

            case "8":
            {
                date_eight.setText(stringDate);
            }
            break;

            case "9":
            {
                date_nine.setText(stringDate);
            }
            break;

            case "10":
            {
                date_ten.setText(stringDate);
            }
            break;

            case "11":
            {
                date_eleven.setText(stringDate);
            }
            break;

            case "12":
            {
                date_twelve.setText(stringDate);
            }
            break;

            case "13":
            {
                date_two_one.setText(stringDate);
            }
            break;

            case "14":
            {
                date_two_two.setText(stringDate);
            }
            break;

            case "15":
            {
                date_two_three.setText(stringDate);
            }
            break;

            case "16":
            {
                date_three_one.setText(stringDate);
            }
            break;

            case "17":
            {
                date_three_two.setText(stringDate);
            }
            break;

            case "18":
            {
                date_three_three.setText(stringDate);
            }
            break;

            case "19":
            {
                date_three_four.setText(stringDate);
            }
            break;

            case "20":
            {
                date_three_five.setText(stringDate);
            }
            break;

            case "21":
            {
                date_three_six.setText(stringDate);
            }
            break;

        }

    }
}
