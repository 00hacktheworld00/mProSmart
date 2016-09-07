package com.example.sadashivsinha.mprosmart.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sadashivsinha.mprosmart.Adapters.InventoryAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.InventoryList;
import com.example.sadashivsinha.mprosmart.R;
import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class InventoryManagementActivity extends AppCompatActivity implements View.OnClickListener {

    TextView inventory_no, project_id, project_desc, item_id, item_desc, date, from_date, to_date;
    private List<InventoryList> inventoryList = new ArrayList<>();
    private RecyclerView recyclerView;
    private InventoryAdapter inventoryAdapter;

    InventoryList items;
    String dateText, receivedText, issuedText, closingBalanceText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_management);

        inventory_no = (TextView) findViewById(R.id.inventory_no);
        project_id = (TextView) findViewById(R.id.project_id);
        project_desc = (TextView) findViewById(R.id.project_desc);
        item_id = (TextView) findViewById(R.id.item_id);
        item_desc = (TextView) findViewById(R.id.item_desc);
        date = (TextView) findViewById(R.id.date);
        from_date = (TextView) findViewById(R.id.from_date);
        to_date = (TextView) findViewById(R.id.to_date);


        if(getIntent().hasExtra("inventoryNo"))
        {
            inventory_no.setText(getIntent().getStringExtra("inventoryNo"));
            project_id.setText(getIntent().getStringExtra("projectId"));
            project_desc.setText(getIntent().getStringExtra("projectDesc"));
            item_id.setText(getIntent().getStringExtra("itemId"));
            item_desc.setText(getIntent().getStringExtra("itemDesc"));
            date.setText(getIntent().getStringExtra("date"));
            from_date.setText(getIntent().getStringExtra("toDate"));
            to_date.setText(getIntent().getStringExtra("fromDate"));
        }

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(InventoryManagementActivity.this));
        recyclerView.setHasFixedSize(true);
        inventoryAdapter = new InventoryAdapter(inventoryList);
        recyclerView.setAdapter(inventoryAdapter);


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
                Intent intent = new Intent(InventoryManagementActivity.this, InventoryItemCreate.class);
                startActivity(intent);
            }
            break;
            case R.id.fab_search:
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Search Inventory Item !");
                // Set an EditText view to get user input
                final EditText input = new EditText(this);
                alert.setView(input);
                alert.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(InventoryManagementActivity.this, "Search for it .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(InventoryManagementActivity.this, "Search cancelled .", Toast.LENGTH_SHORT).show();
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
        items = new InventoryList("01/July/2016", "10 Kgs", "20 Kgs", "10 Kgs");
        inventoryList.add(items);

        items = new InventoryList("25/June/2016", "15 Litres", "20 Litres", "5 Litres");
        inventoryList.add(items);

        items = new InventoryList("05/June/2016", "40 Kgs", "50 Kgs", "10 Kgs");
        inventoryList.add(items);


        if(getIntent().hasExtra("closingBalance"))
        {
            dateText = getIntent().getStringExtra("date");
            receivedText = getIntent().getStringExtra("received");
            issuedText = getIntent().getStringExtra("issued");
            closingBalanceText = getIntent().getStringExtra("closingBalance");

            items = new InventoryList(dateText, receivedText, issuedText, closingBalanceText);
            inventoryList.add(items);
        }

        inventoryAdapter.notifyDataSetChanged();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(InventoryManagementActivity.this, AllMaterialIssue.class);
        startActivity(intent);
    }

}