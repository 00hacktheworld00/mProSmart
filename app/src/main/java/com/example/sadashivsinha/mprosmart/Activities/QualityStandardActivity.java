package com.example.sadashivsinha.mprosmart.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sadashivsinha.mprosmart.Adapters.QualityStandardAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.QualityStandardList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class QualityStandardActivity extends AppCompatActivity implements View.OnClickListener {

    private List<QualityStandardList> qualityList = new ArrayList<>();
    private RecyclerView recyclerView;
    private QualityStandardAdapter qualityAdapter;
    TextView quality_standard_no, project_id, item_id, item_desc, date_created, created_by;

    QualityStandardList qualityItem;
    String criteria, uom, result, comments, status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quality_standard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        PreferenceManager pm = new PreferenceManager(getApplicationContext());

        quality_standard_no = (TextView) findViewById(R.id.quality_standard_no);
        project_id = (TextView) findViewById(R.id.project_id);
        item_id = (TextView) findViewById(R.id.item_id);
        item_desc = (TextView) findViewById(R.id.item_desc);
        date_created = (TextView) findViewById(R.id.date_created);
        created_by = (TextView) findViewById(R.id.created_by);

        quality_standard_no.setText(getIntent().getStringExtra("standardNo"));
        project_id.setText(getIntent().getStringExtra("projectId"));
        item_id.setText(getIntent().getStringExtra("itemId"));
        item_desc.setText(getIntent().getStringExtra("itemDesc"));
        date_created.setText(getIntent().getStringExtra("dateCreated"));
        created_by.setText(getIntent().getStringExtra("createdBy"));

        qualityAdapter = new QualityStandardAdapter(qualityList);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(qualityAdapter);

        class MyTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... params) {
                prepareItems();
                return null;
            }

        }
        new MyTask().execute();

        FloatingActionButton fab_add, fab_search, exportBtn;

        fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        fab_search = (FloatingActionButton) findViewById(R.id.fab_search);
        exportBtn = (FloatingActionButton) findViewById(R.id.exportBtn);

        fab_add.setOnClickListener(this);
        fab_search.setOnClickListener(this);
        exportBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add:
            {
                Intent intent = new Intent(QualityStandardActivity.this, QualityStandardItemCreate.class);
                startActivity(intent);
            }
            break;
            case R.id.fab_search:
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Search Quality Standard !");
                // Set an EditText view to get user input
                final EditText input = new EditText(this);
                alert.setView(input);
                alert.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(QualityStandardActivity.this, "Search for it .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(QualityStandardActivity.this, "Search cancelled .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
            }
            break;
            case R.id.exportBtn:
            {

            }
            break;
        }
    }


    public void prepareItems()
    {
//        qualityItem = new QualityStandardList("Criteria C", "Bags", "This is a sample result of this item", "ACCEPTED",
//                "This is sample comments for this item." );
//        qualityList.add(qualityItem);
//
//        qualityAdapter.notifyDataSetChanged();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(QualityStandardActivity.this, AllQualityStandards.class);
        startActivity(intent);
    }

}