package com.example.sadashivsinha.mprosmart.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.Adapters.AllQualityPlansAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.AllQualityPlansList;
import com.example.sadashivsinha.mprosmart.ModelLists.AllSiteDiaryList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.AppController;
import com.example.sadashivsinha.mprosmart.Utils.Communicator;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.example.sadashivsinha.mprosmart.Utils.CsvCreateUtility;
import com.github.clans.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class AllQualityPlans extends AppCompatActivity implements View.OnClickListener {

    private List<AllQualityPlansList> qualityList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AllQualityPlansAdapter qualityAdapter;
    String currentProjectNo, currentProjectName;

    AllQualityPlansList qualityItem;
    ProgressDialog pDialog;
    JSONArray dataArray;
    JSONObject dataObject;

    String id, createdBy, createdDate;
    PreferenceManager pm;
    ConnectionDetector cd;
    public static final String TAG = AllQualityPlans.class.getSimpleName();
    Boolean isInternetPresent = false;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_quality_plans);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final PreferenceManager pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentProjectName = pm.getString("projectName");

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        url = pm.getString("SERVER_URL") + "/getQualityPlan?projectId='"+currentProjectNo+"'";

        qualityAdapter = new AllQualityPlansAdapter(qualityList);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(AllQualityPlans.this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(qualityAdapter);


        // check for Internet status
        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(AllQualityPlans.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(AllQualityPlans.this);
            pDialog.setMessage("Getting cache data");
            pDialog.show();

            Cache cache = AppController.getInstance().getRequestQueue().getCache();
            Cache.Entry entry = cache.get(url);
            if (entry != null) {
                //Cache data available.
                try {
                    String data = new String(entry.data, "UTF-8");
                    Log.d("CACHE DATA", data);
                    JSONObject jsonObject = new JSONObject(data);
                    try {
                        dataArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < dataArray.length(); i++) {
                            dataObject = dataArray.getJSONObject(i);
                            id = dataObject.getString("id");
                            createdBy = dataObject.getString("createdBy");
                            createdDate = dataObject.getString("createdDate");

                            qualityItem = new AllQualityPlansList(String.valueOf(i+1), id, currentProjectNo, currentProjectName,
                                    createdDate, createdBy);
                            qualityList.add(qualityItem);

                            qualityAdapter.notifyDataSetChanged();
                            pDialog.dismiss();
                        }

                        Boolean createMOMPendingLine = pm.getBoolean("createMOMPendingLine");

                        if(createMOMPendingLine)
                        {

                            String jsonObjectVal = pm.getString("objectMOMLine");
                            Log.d("JSON QP PENDING :", jsonObjectVal);

                            JSONObject jsonObjectPending = new JSONObject(jsonObjectVal);
                            Log.d("JSONObj QP PENDING :", jsonObjectPending.toString());

                            createdBy = dataObject.getString("createdBy");
                            createdDate = dataObject.getString("createdDate");

                            qualityItem = new AllQualityPlansList(String.valueOf(dataArray.length()+1), getResources().getString(R.string.waiting_to_connect), currentProjectNo, currentProjectName,
                                    createdDate, createdBy);
                            qualityList.add(qualityItem);

                            qualityAdapter.notifyDataSetChanged();

                            pDialog.dismiss();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    Toast.makeText(getApplicationContext(), "Loading from cache.", Toast.LENGTH_SHORT).show();
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }
                if (pDialog != null)
                    pDialog.dismiss();
            }

            else
            {
                Toast.makeText(AllQualityPlans.this, "Offline Data Not available for this Quality Plan", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }

        else
        {
            // Cache data not exist.
            prepareItems();
        }

        if(getIntent().hasExtra("create"))
        {
            if(getIntent().getStringExtra("create").equals("yes"))
            {
                createNewQualityPlan();
            }
        }


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
               createNewQualityPlan();
            }
            break;
            case R.id.fab_search:
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Search Quality Plans !");
                // Set an EditText view to get user input
                final EditText input = new EditText(this);
                alert.setView(input);
                alert.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(AllQualityPlans.this, "Search for it .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(AllQualityPlans.this, "Search cancelled .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
            }
            break;


            case R.id.exportBtn:
            {
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP) {
                    if (ContextCompat.checkSelfPermission(AllQualityPlans.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(AllQualityPlans.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                    }
                    Environment.getExternalStorageState();

                    String currentProjectName = null, id = null, currentProjectNo = null, createdBy = null;
                    int listSize = qualityList.size();
                    String cvsValues = "ID" + ","+ "Project Name" + ","+ "Project No" + ","+ "Created By\n";

                    for(int i=0; i<listSize;i++)
                    {
                        AllQualityPlansList items = qualityList.get(i);
                        id = items.getPlan_no();
                        currentProjectName = items.getProject_name();
                        currentProjectNo = items.getProject_id();
                        createdBy = items.getCreated_by();

                        cvsValues = cvsValues +  id + ","+ currentProjectName + ","+ currentProjectNo + ","+ createdBy + "\n";
                    }

                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "AllQualityPlan-data.csv", cvsValues);
                }

                else

                {
                    Environment.getExternalStorageState();

                    String currentProjectName = null, id = null, currentProjectNo = null, createdBy = null;
                    int listSize = qualityList.size();
                    String cvsValues = "ID" + ","+ "Project Name" + ","+ "Project No" + ","+ "Created By\n";

                    for(int i=0; i<listSize;i++)
                    {
                        AllQualityPlansList items = qualityList.get(i);
                        id = items.getPlan_no();
                        currentProjectName = items.getProject_name();
                        currentProjectNo = items.getProject_id();
                        createdBy = items.getCreated_by();

                        cvsValues = cvsValues +  id + ","+ currentProjectName + ","+ currentProjectNo + ","+ createdBy + "\n";
                    }

                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "AllQualityPlan-data.csv", cvsValues);

                }

            }
            break;
        }
    }

    public void prepareItems()
    {
        pDialog = new ProgressDialog(AllQualityPlans.this);
        pDialog.setMessage("Getting server data");
        pDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {
//                            dataObject = response.getJSONObject(0);
                            dataArray = response.getJSONArray("data");
                            for(int i=0; i<dataArray.length();i++)
                            {
                                dataObject = dataArray.getJSONObject(i);
                                id = dataObject.getString("id");
                                createdBy = dataObject.getString("createdBy");
                                createdDate = dataObject.getString("createdDate");

                                qualityItem = new AllQualityPlansList(String.valueOf(i+1), id, currentProjectNo, currentProjectName,
                                        createdDate, createdBy);
                                qualityList.add(qualityItem);

                                qualityAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        setData(response,false);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
        if(pDialog!=null)
            pDialog.dismiss();

    }

    public void saveNewQualityPlan(String projectId, String currentUserId, final Context context) {
        JSONObject object = new JSONObject();


        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(c.getTime());

        try {
            object.put("projectId", projectId);
            object.put("projectName", currentProjectName);
            object.put("createdBy", currentUserId);
            object.put("createdDate", currentDate);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        String url = pm.getString("SERVER_URL") + "/postQualityPlan";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("msg").toString().equals("success"))
                            {
                                Toast.makeText(context, "Quality Plan Created. ID - " + response.getString("data").toString(), Toast.LENGTH_SHORT).show();

                                pDialog.dismiss();
                                pm.putString("currentQualityPlan", response.getString("data").toString());
                                Intent intent = new Intent(AllQualityPlans.this, QualityPlanItemCreate.class);
                                startActivity(intent);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            pDialog.dismiss();
                        }
                        //response success message display
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                        pDialog.dismiss();
                    }
                }
        );

        if (!isInternetPresent)
        {
            // Internet connection is not present

            Communicator communicator = new Communicator();
            Log.d("object", object.toString());

            Boolean createMOMPendingLine = pm.getBoolean("createMOMPendingLine");

            if(createMOMPendingLine)
            {
                Toast.makeText(AllQualityPlans.this, "Already a Quality Plan creation is in progress. Please try after sometime.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(AllQualityPlans.this, "Internet not currently available. Quality Plan will automatically get created on internet connection.", Toast.LENGTH_SHORT).show();

                pm.putString("objectMOMLine", object.toString());
                pm.putString("urlMOMLine", url);
                pm.putString("toastMessageMOMLine", "Quality Plan Created");
                pm.putBoolean("createMOMPendingLine", true);
            }


            if(pDialog!=null)
                pDialog.dismiss();

            Intent intent = new Intent(AllQualityPlans.this, MomActivity.class);
            startActivity(intent);
        }
        else
        {
            requestQueue.add(jor);
        }
    }

    public void createNewQualityPlan()
    {
        pm = new PreferenceManager(getApplicationContext());

        currentProjectNo = pm.getString("projectId");
        final String currentUserId = pm.getString("userId");


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("Do you want to create new Quality Plan ?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                final String projectId = currentProjectNo;
                                final Context mContext = getApplicationContext();

                                saveNewQualityPlan(projectId,currentUserId,mContext);
                            }
                        })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AllQualityPlans.this, QualityControlMain.class);
        startActivity(intent);
    }

}