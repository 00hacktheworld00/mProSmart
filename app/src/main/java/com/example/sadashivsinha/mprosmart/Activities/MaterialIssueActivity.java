package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.Adapters.MaterialIssueAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.MaterialIssueList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MaterialIssueActivity extends AppCompatActivity {

    TextView material_issue_no, project_id, issue_as_per_boq, issue_to, issue_on,issue_by;
    private List<MaterialIssueList> materialList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MaterialIssueAdapter materialIssueAdapter;
    FloatingActionButton fab_add;

    float currentMaterialQuantityIssued, currentBoqQuantity;

    MaterialIssueList qualityItem;
    String itemId, itemDesc, cost, uom;
    ProgressDialog pDialog1;
    String currentProjectNo, currentMaterialIssueId;
    ConnectionDetector cd;
    public static final String TAG = MaterialIssueActivity.class.getSimpleName();
    Boolean isInternetPresent = false;
    JSONArray dataArray;
    JSONObject dataObject;
    PreferenceManager pm;
    FloatingActionMenu menu;
    String id, issueAsPerBoq, issuedTo, issuesdOn, issuedBy, boqItem, quantityOfItem;
    String itemDescription, quantityIssued, uomId, quantity;
    String text_id, text_item, text_quantity, text_uom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_issue);

        material_issue_no = (TextView) findViewById(R.id.material_issue_no);
        project_id = (TextView) findViewById(R.id.project_id);
        issue_as_per_boq = (TextView) findViewById(R.id.issue_as_per_boq);
        issue_to = (TextView) findViewById(R.id.issue_to);
        issue_on = (TextView) findViewById(R.id.issue_on);
        issue_by = (TextView) findViewById(R.id.issue_by);

        pm = new PreferenceManager(MaterialIssueActivity.this);
        currentMaterialIssueId = pm.getString("currentMaterialIssueId");
        currentProjectNo = pm.getString("projectId");

        menu = (FloatingActionMenu) findViewById(R.id.menu);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(MaterialIssueActivity.this));
        recyclerView.setHasFixedSize(true);
        materialIssueAdapter = new MaterialIssueAdapter(materialList);
        recyclerView.setAdapter(materialIssueAdapter);

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_content = (RelativeLayout) findViewById(R.id.main_content);
            Snackbar snackbar = Snackbar.make(main_content, getResources().getString(R.string.no_internet_error), Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MaterialIssueActivity.this, MaterialItemCreate.class);
                startActivity(intent);
            }
        });

        pDialog1 = new ProgressDialog(MaterialIssueActivity.this);
        pDialog1.setMessage("Preparing Header ...");
        pDialog1.setIndeterminate(false);
        pDialog1.setCancelable(true);
        pDialog1.show();

        prepareHeader();

        pDialog1 = new ProgressDialog(MaterialIssueActivity.this);
        pDialog1.setMessage("Preparing Data ...");
        pDialog1.setIndeterminate(false);
        pDialog1.setCancelable(true);
        pDialog1.show();

        class MyTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... params) {
                prepareItems();
                return null;
            }

        }

        class MyTaskAsPerBoq extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... params) {

                String boqId = pm.getString("itemIdBoq");
                getAllUom(boqId);
                return null;
            }

        }



        if(pm.getString("issueAsPerBoq").equals("Yes"))
        {
            FloatingActionMenu menu = (FloatingActionMenu) findViewById(R.id.menu);
            menu.setVisibility(View.GONE);

            new MyTaskAsPerBoq().execute();
        }
        else
        {
            new MyTask().execute();
        }
    }

    public void prepareBoqItems(final String boqId, final String[] uomArray, final String[] uomNameArray)
    {
        Log.d("CURRENT BOQ ID", boqId);
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = pm.getString("SERVER_URL") + "/getBoqItems?projectId='"+currentProjectNo+"'";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{
                            Log.d("CURRENT RESPONSE", response.toString());

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(MaterialIssueActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            float issuedQuantity;

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    text_id = dataObject.getString("boqId");

                                    if(text_id.equals(boqId))
                                    {
                                        id = dataObject.getString("id");
                                        text_item = dataObject.getString("item");
                                        text_quantity = dataObject.getString("quantity");
                                        text_uom = dataObject.getString("uom");
//                                        text_cost = dataObject.getString("cost");
//                                        text_currency = dataObject.getString("currency");
//                                        text_totalCost = dataObject.getString("totalCost");


                                        Log.d("CURRENT ISSUE LINE", text_quantity);

                                        issuedQuantity = (currentMaterialQuantityIssued /currentBoqQuantity) * Float.parseFloat(text_quantity) ;

                                        Log.d("CURRENT ISSUED QUAN", String.valueOf(issuedQuantity));

                                        text_quantity = String.valueOf(issuedQuantity);

                                        Log.d("MIA OLD UOM", text_uom);

                                        for(int j=0; j<uomArray.length; j++)
                                        {
                                            if(text_uom.equals(uomArray[j]))
                                            text_uom = uomNameArray[j];

                                            Log.d("MIA UOM", text_uom);
                                        }
                                        qualityItem = new MaterialIssueList(String.valueOf(i+1), text_item, "", text_quantity, text_uom);
                                        materialList.add(qualityItem);

                                        materialIssueAdapter.notifyDataSetChanged();
                                    }
                                }
                            }

                            pDialog1.dismiss();

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

    public void getAllUom(final String boqId)
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
                                Toast.makeText(MaterialIssueActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            String[] uomArray, uomNameArray;

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                uomArray = new String[dataArray.length()+1];
                                uomNameArray = new String[dataArray.length()+1];

                                uomArray[0]="UOM";
                                uomNameArray[0]="UOM";

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    uomArray[i+1] = dataObject.getString("uomCode");
                                    uomNameArray[i+1] = dataObject.getString("uomName");
                                }

                                Log.d("MIA ALL UOM", Arrays.toString(uomArray));
                                getCurrentBoqQuantity(boqId, uomArray, uomNameArray);
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


    public void getCurrentBoqQuantity(final String boqId, final String[] uomArray, final String[] uomNameArray)
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = pm.getString("SERVER_URL") + "/getBoq?projectId='"+currentProjectNo+"'";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(MaterialIssueActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);

                                    id = dataObject.getString("id");
                                    quantity = dataObject.getString("unit");

                                    if(id.equals(boqId))
                                    {
                                        currentBoqQuantity = Float.parseFloat(quantity);
                                        Log.d("CURRENT BOQ QUAN", String.valueOf(currentBoqQuantity));
                                    }

                                }

                                prepareBoqItems(boqId, uomArray, uomNameArray);
                            }
                        }catch(JSONException e){
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

    public void prepareItems()
    {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = pm.getString("SERVER_URL") + "/getMaterialIssueLine?materialIssueId=\""+currentMaterialIssueId+"\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                pDialog1.dismiss();
                                Toast.makeText(MaterialIssueActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);

                                    itemId = dataObject.getString("itemId");
                                    itemDescription = dataObject.getString("itemDescription");
                                    quantityIssued = dataObject.getString("quantityIssued");
                                    uomId = dataObject.getString("uomId");

                                    qualityItem = new MaterialIssueList(String.valueOf(i+1), itemId, itemDescription, quantityIssued, uomId);
                                    materialList.add(qualityItem);

                                    materialIssueAdapter.notifyDataSetChanged();

                                }
                                pDialog1.dismiss();
                            }
                        }catch(JSONException e){
                            pDialog1.dismiss();
                            e.printStackTrace();}
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog1.dismiss();
                        Log.e("Volley","Error");

                    }
                }
        );
        requestQueue.add(jor);

        if (pDialog1!=null)
            pDialog1.dismiss();

//
//
//        if(pm.getString("issueAsPerBoq").equals("Yes"))
//        {
//            menu.setVisibility(View.GONE);
//
//            String itemNameBoq = pm.getString("itemNameBoq");
//            String itemQuantityBoq = pm.getString("itemQuantityBoq");
//
//            qualityItem = new MaterialIssueList("01", itemNameBoq, "This is a sample description", itemQuantityBoq, "KGs");
//            materialList.add(qualityItem);
//
//            materialIssueAdapter.notifyDataSetChanged();
//        }
//
//
//        if (getIntent().hasExtra("itemId"))
//        {
//            String itemId = getIntent().getStringExtra("itemId");
//            String itemDesc = getIntent().getStringExtra("itemDesc");
//            String uom = getIntent().getStringExtra("uom");
//            String quantity = getIntent().getStringExtra("quantity");
//
//            qualityItem = new MaterialIssueList("02", itemId, itemDesc, quantity, uom);
//            materialList.add(qualityItem);
//
//            materialIssueAdapter.notifyDataSetChanged();
//
//        }

        //
    }

    public void prepareHeader()
    {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = pm.getString("SERVER_URL") + "/getMaterialIssue?projectId='"+currentProjectNo+"'";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(MaterialIssueActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    id = dataObject.getString("id");
                                    if(id.equals(currentMaterialIssueId))
                                    {
                                        dataObject = dataArray.getJSONObject(i);
                                        issueAsPerBoq = dataObject.getString("issueAsPerBoq");
                                        boqItem = dataObject.getString("boqItem");
                                        quantityOfItem = dataObject.getString("quantity");
                                        issuedTo = dataObject.getString("issuedTo");
                                        issuesdOn = dataObject.getString("issuesdOn");
                                        issuedBy = dataObject.getString("issuedBy");

                                        Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(issuesdOn);
                                        issuesdOn = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                        material_issue_no.setText(id);
                                        project_id.setText(currentProjectNo);
                                        issue_as_per_boq.setText(issueAsPerBoq);
                                        issue_to.setText(issuedTo);
                                        issue_on.setText(issuesdOn);
                                        issue_by.setText(issuedBy);

                                        if(quantityOfItem.isEmpty())
                                        {
                                            currentMaterialQuantityIssued = 0;
                                        }
                                        else
                                        {
                                            currentMaterialQuantityIssued = Float.parseFloat(quantityOfItem);
                                        }
                                        Log.d("CURRENT MATERIAL QUAN", String.valueOf(currentMaterialQuantityIssued));
                                    }
                                }
                                pDialog1.dismiss();
                            }
                        }catch(JSONException e){e.printStackTrace();
                            pDialog1.dismiss();} catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog1.dismiss();
                        Log.e("Volley","Error");

                    }
                }
        );
        requestQueue.add(jor);
        if (pDialog1!=null)
            pDialog1.dismiss();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MaterialIssueActivity.this, AllMaterialIssue.class);
        startActivity(intent);
    }

}