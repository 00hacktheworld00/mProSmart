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
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.Adapters.BoqAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.BoqList;
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

public class BoqActivity extends AppCompatActivity implements View.OnClickListener {

    TextView boq_no, project_id,unit, uom, created_by, date_created, text_boq_name;

    private List<BoqList> boqList = new ArrayList<>();
    private RecyclerView recyclerView;
    private BoqAdapter boqAdapter;
    String itemText, quantityText, uomText, costText, currencyText, totalCostText;

    String text_id, text_item, text_quantity, text_uom, text_cost, text_currency, text_totalCost;
    String createdBy, createdDate;
    ProgressDialog pDialog, pDialog1;
    String currentProjectNo, currentProjectName, currentBoq;

    JSONArray dataArray;
    JSONObject dataObject;
    Boolean isInternetPresent = false;

    BoqList qualityItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boq);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        PreferenceManager pm = new PreferenceManager(getApplicationContext());

        currentProjectNo = pm.getString("projectId");
        currentProjectName = pm.getString("projectName");
        currentBoq = pm.getString("currentBoq");

        boq_no = (TextView) findViewById(R.id.boq_no);
        text_boq_name = (TextView) findViewById(R.id.text_boq_name);
        project_id = (TextView) findViewById(R.id.project_id);
        unit = (TextView) findViewById(R.id.unit);
        uom = (TextView) findViewById(R.id.uom);
        created_by = (TextView) findViewById(R.id.created_by);
        date_created = (TextView) findViewById(R.id.date_created);

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

        recyclerView.setLayoutManager(new LinearLayoutManager(BoqActivity.this));
        recyclerView.setHasFixedSize(true);
        boqAdapter = new BoqAdapter(boqList);
        recyclerView.setAdapter(boqAdapter);

        pDialog = new ProgressDialog(BoqActivity.this);
        pDialog.setMessage("Getting Data ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        class MyTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... params) {
                prepareHeader();
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
                Intent intent = new Intent(BoqActivity.this, AddBoqItems.class);
                startActivity(intent);
            }
            break;
            case R.id.fab_search:
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Search BOQ Item !");
                // Set an EditText view to get user input
                final EditText input = new EditText(this);
                alert.setView(input);
                alert.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(BoqActivity.this, "Search for it .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(BoqActivity.this, "Search cancelled .", Toast.LENGTH_SHORT).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = getResources().getString(R.string.server_url) + "/getBoqItems?projectId='"+currentProjectNo+"'";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(BoqActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    text_id = dataObject.getString("boqId");

                                    if(text_id.equals(currentBoq))
                                    {
                                        text_item = dataObject.getString("item");
                                        text_quantity = dataObject.getString("quantity");
                                        text_uom = dataObject.getString("uom");
                                        text_cost = dataObject.getString("cost");
                                        text_currency = dataObject.getString("currency");
                                        text_totalCost = dataObject.getString("totalCost");

                                        qualityItem = new BoqList(text_item, text_quantity, text_uom, text_cost, text_currency, text_totalCost);
                                        boqList.add(qualityItem);

                                        boqAdapter.notifyDataSetChanged();
                                    }
                                }
                            }

                            pDialog.dismiss();

                        }catch(JSONException e){e.printStackTrace();}
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

    private void prepareHeader()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = getResources().getString(R.string.server_url) + "/getBoq?projectId='"+currentProjectNo+"'";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(BoqActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                String id;

                                dataArray = response.getJSONArray("data");
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    id = dataObject.getString("id");

                                    if(currentBoq.equals(id))
                                    {
                                        createdDate = dataObject.getString("createdDate");

                                        Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(createdDate);
                                        createdDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                        project_id.setText(currentProjectNo);
                                        boq_no.setText(currentBoq);
                                        text_boq_name.setText(dataObject.getString("itemName"));
                                        unit.setText(dataObject.getString("unit"));
                                        uom.setText(dataObject.getString("uom"));
                                        created_by.setText(dataObject.getString("createdBy"));
                                        date_created.setText(createdDate);

                                        break;
                                    }
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
        Intent intent = new Intent(BoqActivity.this, AllBoq.class);
        startActivity(intent);
    }

}