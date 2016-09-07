package com.example.sadashivsinha.mprosmart.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.sadashivsinha.mprosmart.Adapters.DailyProgressAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.DailyProgressList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.github.clans.fab.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DailyProgressActivity extends AppCompatActivity implements View.OnClickListener {

    private List<DailyProgressList> dailyList = new ArrayList<>();
    private RecyclerView recyclerView;
    private DailyProgressAdapter dailyAdapter;
    String currentProjectNo, currentProjectName, currentUser, currentDate;
    View dialogView;
    AlertDialog show;

    DailyProgressList items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_progress);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final PreferenceManager pm = new PreferenceManager(getApplicationContext());
        pm.putString("currentBudget", "approval");
        currentProjectNo = pm.getString("projectId");
        currentProjectName = pm.getString("projectName");
        currentUser = pm.getString("userId");


        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = sdf.format(c.getTime());

        dailyAdapter = new DailyProgressAdapter(dailyList);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(dailyAdapter);

        class MyTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... params) {
                prepareItems();
                return null;
            }

        }
        new MyTask().execute();


        FloatingActionButton fab_add;

        fab_add = (FloatingActionButton) findViewById(R.id.fab_add);

        fab_add.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add:
            {
                Intent intent = new Intent(DailyProgressActivity.this, DailyProgressCreate.class);
                startActivity(intent);
            }
            break;
        }
    }

    public void prepareItems()
    {
        items = new DailyProgressList("1", "USER001", "Rainy", "22/07/2016");
        dailyList.add(items);

        items = new DailyProgressList("2", "USER002", "Thunderstorm", "19/07/2016");
        dailyList.add(items);

        items = new DailyProgressList("3", "USER004", "Cloudy", "01/07/2016");
        dailyList.add(items);

        items = new DailyProgressList("4", "USER007", "Rainy", "27/06/2016");
        dailyList.add(items);

        dailyAdapter.notifyDataSetChanged();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DailyProgressActivity.this, ViewPurchaseOrders.class);
        startActivity(intent);
    }

}