package com.example.sadashivsinha.mprosmart.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sadashivsinha.mprosmart.Adapters.MyAdapter;
import com.example.sadashivsinha.mprosmart.Adapters.PurchaseItemsAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.PurchaseItemsList;
import com.example.sadashivsinha.mprosmart.R;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import java.util.ArrayList;
import java.util.List;

public class PurchaseItems extends NewActivity implements View.OnClickListener  {

    private List<PurchaseItemsList> purchaseItemsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PurchaseItemsAdapter purchaseItemsAdapter;
    TextView receipt_no;

    com.github.clans.fab.FloatingActionMenu menu;

    com.github.clans.fab.FloatingActionButton fab_search,fab_new_receipt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_receipts_new);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        purchaseItemsAdapter = new PurchaseItemsAdapter(purchaseItemsList);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(purchaseItemsAdapter);


        receipt_no = (TextView) findViewById(R.id.receipt_no);

        prepareItems();

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

        fab_search = (FloatingActionButton) findViewById(R.id.fab_search);
        fab_new_receipt = (FloatingActionButton) findViewById(R.id.fab_new_receipt);


        fab_search.setOnClickListener(this);
        fab_new_receipt.setOnClickListener(this);

        menu = (FloatingActionMenu) findViewById(R.id.menu);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.fab_search:
            {

                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Search");
                // Set an EditText view to get user input
                final EditText input = new EditText(this);
                alert.setView(input);
                alert.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(PurchaseItems.this, "Search for it .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(PurchaseItems.this, "Search cancelled .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();

            }
            break;
            case R.id.fab_new_receipt:
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("New Purchase Receipt :-");
                // Set an EditText view to get user input
                final TextView input = new TextView(this);

                final View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_new_receipt, null);

                alert.setView(dialogView);
                BetterSpinner spinner = (BetterSpinner )dialogView.findViewById(R.id.spinnerOrders);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_dropdown_item_1line,
                        new String[] {
                                "Purchase Order - 101","Purchase Order - 102","Purchase Order - 103","Purchase Order - 104"});
                spinner.setAdapter(adapter);

                alert.setPositiveButton("Create Receipt", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        Intent intent = new Intent(PurchaseItems.this, NewPurchaseReceipts.class);
                        startActivity(intent);
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(getApplicationContext(), "Cancelled .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
            }
            break;
        }

    }

    public void prepareItems()
    {
        if(getIntent().hasExtra("indexNo"))
        {
            PurchaseItemsList items;

            String index_no = getIntent().getStringExtra("indexNo");

            if(index_no.equals("1"))
            {
                items = new PurchaseItemsList("1", "Con1234","Cement", "100", "Bags", "25-03-16 ","100", "INR", "10000");
                purchaseItemsList.add(items);

                items = new PurchaseItemsList("1", "Con1234","Iron Rod", "50", "KGS", "26-03-16 ","150", "INR", "7500");
                purchaseItemsList.add(items);
            }
            else if(index_no.equals("2"))
            {
                items = new PurchaseItemsList("1", "Con1234","Cement", "150", "Bags", "25-03-16 ","175", "INR", "10000");
                purchaseItemsList.add(items);

                items = new PurchaseItemsList("1", "Con1234","Iron Rod", "100", "KGS", "26-03-16 ","140", "INR", "7500");
                purchaseItemsList.add(items);
            }
            else if(index_no.equals("3"))
            {
                items = new PurchaseItemsList("1", "ELEC01","Sensor Switches", "1000", "PCS", "25-03-16 ","20", "USD", "20000");
                purchaseItemsList.add(items);
            }
            else if(index_no.equals("4"))
            {
                items = new PurchaseItemsList("1", "Con1234","Cement", "100", "Bags", "42454 ","100", "INR", "10000");
                purchaseItemsList.add(items);

                items = new PurchaseItemsList("1", "Con1234","Iron Rod", "50", "KGS", "42455 ","150", "INR", "7500");
                purchaseItemsList.add(items);
            }

            else
            {
                items = new PurchaseItemsList("1", "Con1234","Cement", "100", "Bags", "25-03-16 ","100", "INR", "10000");
                purchaseItemsList.add(items);

                items = new PurchaseItemsList("1", "Con1234","Iron Rod", "50", "KGS", "26-03-16 ","150", "INR", "7500");
                purchaseItemsList.add(items);
            }



            purchaseItemsAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_purchase_order, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PurchaseItems.this, PurchaseOrders.class);
        intent.putExtra("projectNo","1");
        startActivity(intent);
    }

}
