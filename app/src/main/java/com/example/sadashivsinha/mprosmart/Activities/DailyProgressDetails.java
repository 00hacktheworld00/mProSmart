package com.example.sadashivsinha.mprosmart.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.example.sadashivsinha.mprosmart.Adapters.DailyProgressDetailsAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.DailyProgressDetailsList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.font.HelveticaRegular;

import java.util.ArrayList;
import java.util.List;

public class DailyProgressDetails extends AppCompatActivity {

    private List<DailyProgressDetailsList> dailyList = new ArrayList<>();
    private RecyclerView recyclerView;
    private DailyProgressDetailsAdapter dailyListAdapter;
    DailyProgressDetailsList items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_progress_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Daily Field Progress Report");

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        HelveticaRegular from_date, to_date, item_no;

        from_date = (HelveticaRegular) findViewById(R.id.from_date);
        to_date = (HelveticaRegular) findViewById(R.id.to_date);

        item_no = (HelveticaRegular) findViewById(R.id.item_no);

        if(getIntent().hasExtra("fromDate"))
        {
            from_date.setText(getIntent().getStringExtra("fromDate"));
            to_date.setText(getIntent().getStringExtra("toDate"));

            item_no.setText(getIntent().getStringExtra("currentInventoryItem"));
        }

        dailyListAdapter = new DailyProgressDetailsAdapter(dailyList);

        recyclerView.setLayoutManager(new LinearLayoutManager(DailyProgressDetails.this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(dailyListAdapter);

        prepareItems();
    }

    public void prepareItems()
    {
        items = new DailyProgressDetailsList("Foundation", "Pour Concrete", "31-08-13", "In-Progress",
                "40 %", "Campbell" ,"Cloudy weather with high speed wind");
        dailyList.add(items);

        items = new DailyProgressDetailsList("Foundation", "Tie Rebar", "09-06-2015", "Completed",
                "100 %", "Campbell" ,"Sunny weather");
        dailyList.add(items);

        items = new DailyProgressDetailsList("Excavation", "Utility Clearance", "06-03-2014", "Completed",
                "100 %", "Campbell" ,"Raining weather with wind");
        dailyList.add(items);

        items = new DailyProgressDetailsList("Foundation", "Surface Scrapping", "06-06-2015", "Completed",
                "100 %", "Campbell" ,"Thunderstorms");
        dailyList.add(items);

        dailyListAdapter.notifyDataSetChanged();
    }
}
