package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.example.sadashivsinha.mprosmart.Adapters.PurchaseRequisitionAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.PurchaseRequisitionList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
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

public class PurchaseRequisition extends AppCompatActivity implements View.OnClickListener {

    TextView text_pr_no, text_department, text_created_on, text_created_by, text_project_id;

    private List<PurchaseRequisitionList> purchaseList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PurchaseRequisitionAdapter purchaseAdapter;

    PurchaseRequisitionList qualityItem;
    String itemId, itemDesc, quantity, uom;
    String currentPr;
    String currentProjectId, id, itemDescription, neededBy;
    JSONArray dataArray;
    JSONObject dataObject;
    ProgressDialog progressDialog;
    PreferenceManager pm;
    String[] uomIdArray, uomNameArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_requisition);

        pm = new PreferenceManager(this);
        currentProjectId = pm.getString("projectId");
        currentPr = pm.getString("currentPr");

        text_pr_no = (TextView) findViewById(R.id.text_pr_no);
        text_department = (TextView) findViewById(R.id.text_department);
        text_created_on = (TextView) findViewById(R.id.text_created_on);
        text_created_by = (TextView) findViewById(R.id.text_created_by);
        text_project_id = (TextView) findViewById(R.id.text_project_id);

        if(getIntent().hasExtra("create"))
        {
            if(getIntent().getStringExtra("create").equals("yes"))
            {
                Intent intent = new Intent(PurchaseRequisition.this, PurchaseRequisitionItemCreate.class);
                startActivity(intent);
            }

        }

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(PurchaseRequisition.this));
        recyclerView.setHasFixedSize(true);
        purchaseAdapter = new PurchaseRequisitionAdapter(purchaseList);
        recyclerView.setAdapter(purchaseAdapter);

        prepareHeader();

        progressDialog = new ProgressDialog(PurchaseRequisition.this);
        progressDialog.setMessage("Getting Items...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.show();

        class MyTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... params) {
                getAllUom();
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

    public void prepareHeader()
    {
        text_pr_no.setText(pm.getString("currentPr"));
        text_department.setText(pm.getString("departmentPr"));
        text_created_on.setText(pm.getString("createdOnPr"));
        text_created_by.setText(pm.getString("createdByPr"));
        text_project_id.setText(pm.getString("projectId"));
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add:
            {
                Intent intent = new Intent(PurchaseRequisition.this, PurchaseRequisitionItemCreate.class);
                startActivity(intent);
            }
            break;
            case R.id.fab_search:
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Search Purchase Requisition Item !");
                // Set an EditText view to get user input
                final EditText input = new EditText(this);
                alert.setView(input);
                alert.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(PurchaseRequisition.this, "Search for it .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(PurchaseRequisition.this, "Search cancelled .", Toast.LENGTH_SHORT).show();
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

        String url = pm.getString("SERVER_URL") + "/getPurchaseRequisitionItem?purchaseRequisitionId=\""+currentPr+"\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(response.getString("msg").equals("No data"))
                                Toast.makeText(PurchaseRequisition.this, response.getString("msg"), Toast.LENGTH_SHORT).show();

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    id = dataObject.getString("id");
                                    itemId = dataObject.getString("itemId");
                                    itemDescription = dataObject.getString("itemDescription");
                                    quantity = dataObject.getString("quantity");
                                    uom = dataObject.getString("uom");
                                    neededBy = dataObject.getString("neededBy");

                                    Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(neededBy);
                                    neededBy = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                    for(int j=0; j<uomIdArray.length; j++)
                                    {
                                        if(uomIdArray[j].equals(uom))
                                            uom = uomNameArray[j];
                                    }

                                    qualityItem = new PurchaseRequisitionList(id, itemId, itemDescription,quantity, uom, neededBy);
                                    purchaseList.add(qualityItem);

                                    purchaseAdapter.notifyDataSetChanged();
                                }
                            }

                            progressDialog.dismiss();

                        }catch(JSONException e){e.printStackTrace();
                            progressDialog.dismiss();} catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Log.e("Volley","Error");

                    }
                }
        );
        requestQueue.add(jor);
    }


    public void getAllUom()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = pm.getString("SERVER_URL") + "/getUom";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{
                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(PurchaseRequisition.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                uomIdArray = new String[dataArray.length()+1];
                                uomNameArray = new String[dataArray.length()+1];

                                uomIdArray[0]="UOM";
                                uomNameArray[0]="UOM";

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    uomIdArray[i+1] = dataObject.getString("uomCode");
                                    uomNameArray[i+1] = dataObject.getString("uomName");
                                }

                                prepareItems();
                            }

                        }
                        catch(JSONException e){
                            e.printStackTrace();}
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
        Intent intent = new Intent(PurchaseRequisition.this, AllPurchaseRequisition.class);
        startActivity(intent);
    }
}