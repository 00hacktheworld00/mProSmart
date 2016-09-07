package com.example.sadashivsinha.mprosmart.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.example.sadashivsinha.mprosmart.Adapters.InventoryViewAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.InventoryViewList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.font.HelveticaRegular;

import java.util.ArrayList;
import java.util.List;

public class InventoryView extends AppCompatActivity {

    private List<InventoryViewList> inventoryViewList = new ArrayList<>();
    private RecyclerView recyclerView;
    private InventoryViewAdapter inventoryAdapter;
    InventoryViewList items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("View Inventory");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
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

        inventoryAdapter = new InventoryViewAdapter(inventoryViewList);

        recyclerView.setLayoutManager(new LinearLayoutManager(InventoryView.this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(inventoryAdapter);

        prepareItems();
    }

    public void prepareItems()
    {

        items = new InventoryViewList("PR001", "1200", "23/July/2016", "0", "200");
        inventoryViewList.add(items);

        items = new InventoryViewList("PR002", "1050", "15/July/2016", "0", "100");
        inventoryViewList.add(items);

        items = new InventoryViewList("M101", "985", "02/July/2016", "1", "400");
        inventoryViewList.add(items);

        items = new InventoryViewList("PR003", "920", "28/June/2016", "0", "300");
        inventoryViewList.add(items);

        items = new InventoryViewList("M102", "870", "14/May/2016", "1", "500");
        inventoryViewList.add(items);

        items = new InventoryViewList("PR005", "800", "02/April/2016", "0", "250");
        inventoryViewList.add(items);

        inventoryAdapter.notifyDataSetChanged();
    }
}
