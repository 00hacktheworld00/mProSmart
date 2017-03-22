package com.example.sadashivsinha.mprosmart.Activities;

import android.Manifest;
import android.app.ProgressDialog;
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
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.sadashivsinha.mprosmart.Adapters.AllBudgetChangeAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.AllBudgetChangeList;
import com.example.sadashivsinha.mprosmart.ModelLists.MomList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.AppController;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.example.sadashivsinha.mprosmart.Utils.CsvCreateUtility;
import com.github.clans.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class AllBudgetChanges extends AppCompatActivity implements View.OnClickListener {

    private List<AllBudgetChangeList> budgetList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AllBudgetChangeAdapter budgetAdapter;
    String currentProjectNo, currentProjectName, currentUser, currentDate;
    View dialogView;
    AlertDialog show;

    AllBudgetChangeList budgetItems;
    private ProgressDialog pDialog;
    JSONArray dataArray;
    JSONObject dataObject;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    public static final String TAG = AllBudgetChanges.class.getSimpleName();
    String budgetChangesId, contractRefNo, originalBudget, currentBudget, createdBy, dateCreated, totalBudget, description;
    String url;
    PreferenceManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_budget_changes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pm = new PreferenceManager(getApplicationContext());
        pm.putString("currentBudget", "transfer");
        currentProjectNo = pm.getString("projectId");
        currentProjectName = pm.getString("projectName");
        currentUser = pm.getString("userId");


        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = sdf.format(c.getTime());

        budgetAdapter = new AllBudgetChangeAdapter(budgetList);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(budgetAdapter);

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        url = pm.getString("SERVER_URL") + "/getBudgetChanges?projectId='"+currentProjectNo+"'";

        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_content);
            Crouton.cancelAllCroutons();
            Crouton.makeText(AllBudgetChanges.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(AllBudgetChanges.this);
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
                            budgetChangesId = dataObject.getString("budgetChangesId");
                            contractRefNo = dataObject.getString("contractRefNo");
                            originalBudget = dataObject.getString("originalBudget");
                            currentBudget = dataObject.getString("currentBudget");
                            createdBy = dataObject.getString("createdBy");
                            totalBudget = dataObject.getString("totalBudget");
                            description = dataObject.getString("description");
                            dateCreated = dataObject.getString("dateCreated");

                            Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(dateCreated);
                            dateCreated = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                            budgetItems = new AllBudgetChangeList(String.valueOf(i+1), budgetChangesId, currentProjectNo, currentProjectName, createdBy, dateCreated,
                                    originalBudget, currentBudget, totalBudget, description, contractRefNo);
                            budgetList.add(budgetItems);

                            budgetAdapter.notifyDataSetChanged();
                        }

                        Boolean createBudgetChangePending = pm.getBoolean("createBudgetChangePending");

                        if(createBudgetChangePending)
                        {
                            String jsonObjectVal = pm.getString("objectBudgetChangeLine");
                            Log.d("JSON BC PENDING :", jsonObjectVal);

                            JSONObject jsonObjectPending = new JSONObject(jsonObjectVal);
                            Log.d("JSONObj BC PENDING :", jsonObjectPending.toString());

                            contractRefNo = jsonObjectPending.getString("contractRefNo");
                            originalBudget = jsonObjectPending.getString("originalBudget");
                            currentBudget = jsonObjectPending.getString("currentBudget");
                            createdBy = jsonObjectPending.getString("createdBy");
                            totalBudget = jsonObjectPending.getString("totalBudget");
                            description = jsonObjectPending.getString("description");
                            dateCreated = jsonObjectPending.getString("dateCreated");

                            Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(dateCreated);
                            dateCreated = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                            budgetItems = new AllBudgetChangeList(String.valueOf(dataArray.length()+1),  getResources().getString(R.string.waiting_to_connect), currentProjectNo, currentProjectName, createdBy, dateCreated,
                                    originalBudget, currentBudget, totalBudget, description, contractRefNo);
                            budgetList.add(budgetItems);

                            budgetAdapter.notifyDataSetChanged();
                        }

                        pDialog.dismiss();

                    }catch (JSONException | ParseException e) {
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
                Toast.makeText(AllBudgetChanges.this, "Offline Data Not available for this Budget Changes", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }
        else
        {
            // Cache data not exist.
            prepareItems();
        }


        FloatingActionButton fab_add, exportBtn;

        fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        exportBtn = (FloatingActionButton) findViewById(R.id.exportBtn);

        fab_add.setOnClickListener(this);
        exportBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.exportBtn:
            {
                //csv export
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP) {
                    if (ContextCompat.checkSelfPermission(AllBudgetChanges.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(AllBudgetChanges.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                    }
                    Environment.getExternalStorageState();

                    String budgetChangesId = null, contractRefNo = null, originalBudget = null, currentBudget = null, createdBy = null,
                            totalBudget = null, description = null, dateCreated = null;
                    int listSize = budgetList.size();
                    String cvsValues = "Budget Change ID" + ","+ "Contract Ref No" + ","+ "Original Budget"  + ","+ "Current Budget"
                            + ","+ "Created By"  + ","+ "Total Budget"+ ","+ "Description"  + ","+ "Date Created" + "\n";

                    for(int i=0; i<listSize;i++)
                    {
                        AllBudgetChangeList items = budgetList.get(i);
                        budgetChangesId = items.getChange_no();
                        contractRefNo = items.getContractRefNo();
                        originalBudget = items.getOriginal_budget();
                        currentBudget = items.getCurrent_budget();
                        createdBy = items.getCreated_by();
                        totalBudget = items.getTotal_budget();
                        description = items.getDescription();
                        dateCreated = items.getCreated_on();

                        cvsValues = cvsValues +  budgetChangesId + ","+ contractRefNo + ","+ originalBudget +","+ currentBudget
                                +","+ createdBy +","+ totalBudget +","+ description +","+ dateCreated + "\n";
                    }

                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "Budget Changes-data.csv", cvsValues);
                }

                else

                {
                    Environment.getExternalStorageState();

                    String budgetChangesId = null, contractRefNo = null, originalBudget = null, currentBudget = null, createdBy = null,
                            totalBudget = null, description = null, dateCreated = null;
                    int listSize = budgetList.size();
                    String cvsValues = "Budget Change ID" + ","+ "Contract Ref No" + ","+ "Original Budget"  + ","+ "Current Budget"
                            + ","+ "Created By"  + ","+ "Total Budget"+ ","+ "Description"  + ","+ "Date Created" + "\n";

                    for(int i=0; i<listSize;i++)
                    {
                        AllBudgetChangeList items = budgetList.get(i);
                        budgetChangesId = items.getChange_no();
                        contractRefNo = items.getContractRefNo();
                        originalBudget = items.getOriginal_budget();
                        currentBudget = items.getCurrent_budget();
                        createdBy = items.getCreated_by();
                        totalBudget = items.getTotal_budget();
                        description = items.getDescription();
                        dateCreated = items.getCreated_on();

                        cvsValues = cvsValues +  budgetChangesId + ","+ contractRefNo + ","+ originalBudget +","+ currentBudget
                                +","+ createdBy +","+ totalBudget +","+ description +","+ dateCreated + "\n";
                    }

                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "Budget Changes-data.csv", cvsValues);
                }

            }
            break;



            case R.id.fab_add:
            {
                Intent intent = new Intent(AllBudgetChanges.this, BudgetChangeAllCreate.class);
                startActivity(intent);
            }
            break;
        }
    }

    public void prepareItems()
    {
        pDialog = new ProgressDialog(AllBudgetChanges.this);
        pDialog.setMessage("Getting server data");
        pDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {
//                            dataObject = response.getJSONObject(0);
                            Log.d("RESPONSE JSON", response.toString());
                            dataArray = response.getJSONArray("data");
                            for(int i=0; i<dataArray.length();i++)
                            {
                                dataObject = dataArray.getJSONObject(i);
                                budgetChangesId = dataObject.getString("budgetChangesId");
                                contractRefNo = dataObject.getString("contractRefNo");
                                originalBudget = dataObject.getString("originalBudget");
                                currentBudget = dataObject.getString("currentBudget");
                                createdBy = dataObject.getString("createdBy");
                                totalBudget = dataObject.getString("totalBudget");
                                description = dataObject.getString("description");
                                dateCreated = dataObject.getString("dateCreated");

                                Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(dateCreated);
                                dateCreated = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                budgetItems = new AllBudgetChangeList(String.valueOf(i+1), budgetChangesId, currentProjectNo, currentProjectName, createdBy, dateCreated,
                                        originalBudget, currentBudget, totalBudget, description, contractRefNo);
                                budgetList.add(budgetItems);

                                budgetAdapter.notifyDataSetChanged();
                            }
                            pDialog.dismiss();
                        } catch (JSONException | ParseException e) {
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AllBudgetChanges.this, BudgetMainActivity.class);
        startActivity(intent);
    }
}