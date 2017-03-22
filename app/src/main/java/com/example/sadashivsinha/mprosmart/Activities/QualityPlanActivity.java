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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.Adapters.QualityPlanAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.QualityPlanList;
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

public class QualityPlanActivity extends AppCompatActivity implements View.OnClickListener {

    private List<QualityPlanList> qualityList = new ArrayList<>();
    private RecyclerView recyclerView;
    private QualityPlanAdapter qualityAdapter;
    TextView quality_plan_no, project_id, project_name, created_on, created_by;

    QualityPlanList qualityItem;
    String currentProjectNo, currentProjectName, currentQualityPlan;

    JSONArray dataArray;
    JSONObject dataObject;
    Boolean isInternetPresent = false;

    String id, processDescription, Activity, procedure, acceptanceCriteria, supplier, subContractor, thirdParty, customerClient,
            totalAttachments;
    String createdBy, createdDate;
    ProgressDialog pDialog, pDialog1;
    PreferenceManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quality_plan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        pm = new PreferenceManager(getApplicationContext());

        quality_plan_no = (TextView) findViewById(R.id.quality_plan_no);
        project_id = (TextView) findViewById(R.id.project_id);
        project_name = (TextView) findViewById(R.id.project_name);
        created_on = (TextView) findViewById(R.id.created_on);
        created_by = (TextView) findViewById(R.id.created_by);

        currentProjectNo = pm.getString("projectId");
        currentProjectName = pm.getString("projectName");
        currentQualityPlan = pm.getString("currentQualityPlan");

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

        qualityAdapter = new QualityPlanAdapter(qualityList);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(qualityAdapter);



        pDialog1 = new ProgressDialog(QualityPlanActivity.this);
        pDialog1.setMessage("Preparing Header ...");
        pDialog1.setIndeterminate(false);
        pDialog1.setCancelable(true);
        pDialog1.show();

        prepareHeader();

        pDialog = new ProgressDialog(QualityPlanActivity.this);
        pDialog.setMessage("Getting Data ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

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
                Intent intent = new Intent(QualityPlanActivity.this, QualityPlanItemCreate.class);
                startActivity(intent);
            }
            break;
            case R.id.fab_search:
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Search Quality Plan !");
                // Set an EditText view to get user input
                final EditText input = new EditText(this);
                alert.setView(input);
                alert.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(QualityPlanActivity.this, "Search for it .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(QualityPlanActivity.this, "Search cancelled .", Toast.LENGTH_SHORT).show();
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

        String url = pm.getString("SERVER_URL") + "/getQualityPlanStatus?qualityPlanId='"+currentQualityPlan+"'";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(QualityPlanActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    id = dataObject.getString("id");
                                    processDescription = dataObject.getString("processDescription");
                                    Activity = dataObject.getString("Activity");
                                    procedure = dataObject.getString("procedure");
                                    acceptanceCriteria = dataObject.getString("acceptanceCriteria");
                                    supplier = dataObject.getString("supplier");
                                    subContractor = dataObject.getString("subContractor");
                                    thirdParty = dataObject.getString("thirdParty");
                                    customerClient = dataObject.getString("customerClient");
                                    totalAttachments = dataObject.getString("totalAttachments");

                                    qualityItem = new QualityPlanList(String.valueOf(i+1),id, processDescription,Activity, procedure,
                                            acceptanceCriteria, supplier, subContractor, thirdParty, customerClient, totalAttachments);
                                    qualityList.add(qualityItem);


                                    qualityAdapter.notifyDataSetChanged();
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

    public void prepareHeader()
    {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = pm.getString("SERVER_URL") + "/getQualityPlan?projectId='"+currentProjectNo+"'";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            String type = response.getString("type");

                            if (type.equals("ERROR")) {
                                Toast.makeText(QualityPlanActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if (type.equals("INFO")) {
                                dataArray = response.getJSONArray("data");
                                for (int i = 0; i < dataArray.length(); i++) {
                                    dataObject = dataArray.getJSONObject(i);
                                    id = dataObject.getString("id");

                                    if(id.equals(currentQualityPlan))
                                    {
                                        createdBy = dataObject.getString("createdBy");
                                        createdDate = dataObject.getString("createdDate");

                                        Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(createdDate);
                                        createdDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                        quality_plan_no.setText(id);
                                        project_id.setText(currentProjectNo);
                                        project_name.setText(currentProjectName);
                                        created_on.setText(createdDate);
                                        created_by.setText(createdBy);

                                    }

                                }
                                pDialog1.dismiss();
                            }
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
        Intent intent = new Intent(QualityPlanActivity.this, AllQualityPlans.class);
        startActivity(intent);
    }
}