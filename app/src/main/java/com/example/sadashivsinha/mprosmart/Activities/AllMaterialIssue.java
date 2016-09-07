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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.Adapters.AllMaterialIssueAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.AllMaterialIssueList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.github.clans.fab.FloatingActionButton;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AllMaterialIssue extends AppCompatActivity implements View.OnClickListener {

    private List<AllMaterialIssueList> allBoqList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AllMaterialIssueAdapter allMaterialIssueAdapter;
    ProgressDialog pDialog;
    String currentProjectNo, currentProjectName, id, createdBy, createdDate, currentUser;
    JSONArray dataArray;
    JSONObject dataObject;
    Boolean isInternetPresent = false;
    View dialogView;
    AlertDialog show;
    BetterSpinner spinner_issue_as_per_boq;
    Spinner spinner_boq_item;
    LinearLayout hiddenLayout;
    EditText text_quantity;
    String boqId;
    String[] boqIdArray;
    Button createBtn;
    String[] boqItemNameArray, boqUomArray;
    String itemName,uom, currentItemId, currentUomId;

    AllMaterialIssueList qualityItem;
    String issueAsPerBoq, boqItem, quantity, issuedTo, issuesdOn, issuedBy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_material_issue);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final PreferenceManager pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentProjectName = pm.getString("projectName");
        currentUser = pm.getString("userId");

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

        pDialog = new ProgressDialog(AllMaterialIssue.this);
        pDialog.setMessage("Getting Data ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();


        class MyTask extends AsyncTask<Void, Void, Void> {

            @Override protected void onPreExecute() {
                allMaterialIssueAdapter = new AllMaterialIssueAdapter(allBoqList);
                recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                recyclerView.setLayoutManager(new LinearLayoutManager(AllMaterialIssue.this));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(allMaterialIssueAdapter);
            }

            @Override
            protected Void doInBackground(Void... params) {
                prepareItems();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                allMaterialIssueAdapter.notifyDataSetChanged();
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

        if(getIntent().hasExtra("create"))
        {
            if(getIntent().getStringExtra("create").equals("yes"))
                create();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add:
            {

               create();

            }
            break;
            case R.id.fab_search:
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Search Material Issue !");
                // Set an EditText view to get user input
                final EditText input = new EditText(this);
                alert.setView(input);
                alert.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(AllMaterialIssue.this, "Search for it .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(AllMaterialIssue.this, "Search cancelled .", Toast.LENGTH_SHORT).show();
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


    public void  saveItems()
    {
        JSONObject object = new JSONObject();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(c.getTime());

        try {
            object.put("projectId", currentProjectNo);
            object.put("issueAsPerBoq", spinner_issue_as_per_boq.getText().toString());
            object.put("boqItem", currentItemId);
            object.put("quantity", text_quantity.getText().toString());
            object.put("issuedTo", currentUser);
            object.put("issuedBy", currentUser);
            object.put("issuesdOn", currentDate);

            Log.d("json of request : ", object.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(AllMaterialIssue.this);

        String url = getResources().getString(R.string.server_url) + "/postMaterialIssue";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("response", response.toString());
                            if(response.getString("msg").equals("success"))
                            {
                                Toast.makeText(AllMaterialIssue.this, "Material Issue Created. ID - " + response.getString("data"), Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                                Intent intent = new Intent(AllMaterialIssue.this, AllMaterialIssue.class);
                                startActivity(intent);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //response success message display
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                    }
                }
        );
        requestQueue.add(jor);
        if(pDialog!=null)
            pDialog.dismiss();
    }

    public void create()
    {
        final AlertDialog.Builder alert = new AlertDialog.Builder(AllMaterialIssue.this,android.R.style.Theme_Translucent_NoTitleBar);
        // Set an EditText view to get user input

        dialogView = LayoutInflater.from(AllMaterialIssue.this).inflate(R.layout.dialog_new_material_issue, null);
        alert.setView(dialogView);

        show = alert.show();

        text_quantity = (EditText) dialogView.findViewById(R.id.text_quantity);

        spinner_issue_as_per_boq = (BetterSpinner) dialogView.findViewById(R.id.spinner_issue_as_per_boq);
        spinner_boq_item = (Spinner) dialogView.findViewById(R.id.spinner_boq_item);

        hiddenLayout = (LinearLayout) dialogView.findViewById(R.id.hiddenLayout);
        hiddenLayout.setVisibility(View.GONE);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line,
                new String[] {"Yes", "No"});

        spinner_issue_as_per_boq.setAdapter(adapter);



        pDialog = new ProgressDialog(AllMaterialIssue.this);
        pDialog.setMessage("Getting WBS...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        class MyTask extends AsyncTask<Void, Void, Void>
        {
            @Override
            protected Void doInBackground(Void... params)
            {
                getAllBoq();
                return null;
            }
        }

        new MyTask().execute();

        spinner_boq_item.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0)
                {
                    currentItemId = boqIdArray[position];
                    currentUomId = boqUomArray[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_issue_as_per_boq.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(position==0)
                {
                    hiddenLayout.setVisibility(View.VISIBLE);
                }
                else
                {
                    hiddenLayout.setVisibility(View.GONE);
                }
            }
        });

        createBtn = (Button) dialogView.findViewById(R.id.createBtn);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(spinner_issue_as_per_boq.getText().toString().equals("Yes"))
                {
                    if(spinner_boq_item.getSelectedItem().toString().equals("Select BOQ"))
                    {
                        Toast.makeText(AllMaterialIssue.this, "Select BOQ first", Toast.LENGTH_SHORT).show();
                    }
                    else if(text_quantity.getText().toString().isEmpty())
                    {
                        text_quantity.setError("Quantity cannot be left empty");
                    }
                }

                class MyTask extends AsyncTask<Void, Void, Void> {

                    @Override protected void onPreExecute()
                    {
                        pDialog = new ProgressDialog(dialogView.getContext());
                        pDialog.setMessage("Sending Data ...");
                        pDialog.setIndeterminate(false);
                        pDialog.setCancelable(true);
                        pDialog.show();
                    }

                    @Override
                    protected Void doInBackground(Void... params) {
                        saveItems();
                        return null;
                    }
                }
                new MyTask().execute();
            }
        });
    }

    public void prepareItems()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = getResources().getString(R.string.server_url) + "/getMaterialIssue?projectId='"+currentProjectNo+"'";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(AllMaterialIssue.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    id = dataObject.getString("id");
                                    issueAsPerBoq = dataObject.getString("issueAsPerBoq");
                                    boqItem = dataObject.getString("boqItem");
                                    quantity = dataObject.getString("quantity");
                                    issuedTo = dataObject.getString("issuedTo");
                                    issuesdOn = dataObject.getString("issuesdOn");
                                    issuedBy = dataObject.getString("issuedBy");

                                    Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(issuesdOn);
                                    issuesdOn = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                    qualityItem = new AllMaterialIssueList(String.valueOf(i+1), currentProjectNo, id, issueAsPerBoq,boqItem, quantity,
                                            issuedTo, issuesdOn, issuedBy);
                                    allBoqList.add(qualityItem);
                                    allMaterialIssueAdapter.notifyDataSetChanged();
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

    public void getAllBoq()
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
                                Toast.makeText(AllMaterialIssue.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                            }


                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                boqIdArray = new String[dataArray.length()+1];
                                boqItemNameArray = new String[dataArray.length()+1];
                                boqUomArray = new String[dataArray.length()+1];

                                boqIdArray[0]= "Select BOQ";
                                boqItemNameArray[0]= "Select BOQ";
                                boqUomArray[0]= "Select BOQ";

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    boqId = dataObject.getString("id");
                                    itemName = dataObject.getString("itemName");
                                    uom = dataObject.getString("uom");

                                    boqIdArray[i+1]=boqId;
                                    boqItemNameArray[i+1]=itemName;
                                    boqUomArray[i+1]=uom;
                                }

                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(AllMaterialIssue.this,
                                        android.R.layout.simple_dropdown_item_1line,boqItemNameArray);
                                spinner_boq_item.setAdapter(adapter);
                            }
                            pDialog.dismiss();

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

        if(pDialog!=null)
            pDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AllMaterialIssue.this, ViewPurchaseOrders.class);
        startActivity(intent);
    }

}