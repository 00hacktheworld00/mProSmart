package com.example.sadashivsinha.mprosmart.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.example.sadashivsinha.mprosmart.R;

public class InventoryItemsDisplay extends AppCompatActivity {

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_items_display);

        recyclerView = (RecyclerView) findViewById(R.id.RecyclerView);
    }
}
