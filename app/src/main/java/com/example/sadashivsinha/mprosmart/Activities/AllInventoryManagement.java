package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.Adapters.AllInventoryManagementAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.AllInventoryList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.github.clans.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AllInventoryManagement extends AppCompatActivity implements View.OnClickListener {

    private List<AllInventoryList> inventoryList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AllInventoryManagementAdapter allInventoryAdapter;
    String currentProjectNo, currentProjectName, id, projectDescription, itemId, itemDescription, date, showTransaction, fromDate,
            toDate;
    ProgressDialog pDialog;
    JSONArray dataArray;
    JSONObject dataObject;
    Boolean isInternetPresent = false;

    AllInventoryList items;
    PreferenceManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_inventory_management);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentProjectName = pm.getString("projectName");

        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            CoordinatorLayout main_content = (CoordinatorLayout) findViewById(R.id.main_content);
            Snackbar snackbar = Snackbar.make(main_content, getResources().getString(R.string.no_internet_error), Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        pDialog = new ProgressDialog(AllInventoryManagement.this);
        pDialog.setMessage("Getting Data ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();


        class MyTask extends AsyncTask<Void, Void, Void> {

            @Override protected void onPreExecute() {
                allInventoryAdapter = new AllInventoryManagementAdapter(inventoryList);
                recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                recyclerView.setLayoutManager(new LinearLayoutManager(AllInventoryManagement.this));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(allInventoryAdapter);
            }

            @Override
            protected Void doInBackground(Void... params) {
                prepareItems();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                allInventoryAdapter.notifyDataSetChanged();
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
                Intent intent = new Intent(AllInventoryManagement.this, InventoryCreate.class);
                startActivity(intent);
            }
            break;
            case R.id.fab_search:
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Search Inventory !");
                // Set an EditText view to get user input
                final EditText input = new EditText(this);
                alert.setView(input);
                alert.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(AllInventoryManagement.this, "Search for it .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(AllInventoryManagement.this, "Search cancelled .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
            }
            break;
            case R.id.exportBtn:
            {
                // to do export
            }
            break;
        }
    }



    public void prepareItems()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = pm.getString("SERVER_URL") + "/getInventoryManagement?projectId='"+currentProjectNo+"'";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(AllInventoryManagement.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    id = dataObject.getString("id");
                                    projectDescription = dataObject.getString("projectDescription");
                                    itemId = dataObject.getString("itemId");
                                    itemDescription = dataObject.getString("itemDescription");
                                    showTransaction = dataObject.getString("showTransaction");
                                    date = dataObject.getString("date");
                                    fromDate = dataObject.getString("fromDate");
                                    toDate = dataObject.getString("toDate");

                                    Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
                                    date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                    Date tradeDate1 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(fromDate);
                                    fromDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate1);

                                    Date tradeDate2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(toDate);
                                    toDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate2);

                                    items = new AllInventoryList(String.valueOf(i+1), id, currentProjectNo, projectDescription,
                                            itemId, itemDescription, date, showTransaction, fromDate, toDate);

                                    inventoryList.add(items);

                                    allInventoryAdapter.notifyDataSetChanged();
                                }
                            }

                            pDialog.dismiss();

                        }catch(JSONException e){e.printStackTrace();} catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley","Error");

                    }
                }
        );
        requestQueue.add(jor);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AllInventoryManagement.this, ViewPurchaseOrders.class);
        startActivity(intent);
    }

}