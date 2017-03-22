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
import com.example.sadashivsinha.mprosmart.Adapters.AllBudgetTransferAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.AllBudgetChangeList;
import com.example.sadashivsinha.mprosmart.ModelLists.AllBudgetTransferList;
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

public class AllBudgetTransfer extends AppCompatActivity implements View.OnClickListener {

private List<AllBudgetTransferList> budgetList = new ArrayList<>();
private RecyclerView recyclerView;
private AllBudgetTransferAdapter budgetAdapter;
        String currentProjectNo, currentProjectName, currentUser, currentDate;
        View dialogView;
        AlertDialog show;
    String[] wbsNameArray, wbsIdArray;
    String wbsName, wbsId;
    String fromWbs, toWbs, budgetTransfer, createdBy, dateCreated, budgetTransferId;
    JSONArray dataArray;
    JSONObject dataObject;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;

    AllBudgetTransferList budgetItems;
    ProgressDialog pDialog;
    String wbs_url;
    public static final String TAG = AllBudgetTransfer.class.getSimpleName();
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

            budgetAdapter = new AllBudgetTransferAdapter(budgetList);
            recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(AllBudgetTransfer.this));
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(budgetAdapter);

            cd = new ConnectionDetector(getApplicationContext());
            isInternetPresent = cd.isConnectingToInternet();

            wbs_url = pm.getString("SERVER_URL") + "/getWbs?projectId=\""+currentProjectNo+"\"";

            if (!isInternetPresent) {
                // Internet connection is not present
                // Ask user to connect to Internet
                RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_content);
                Crouton.cancelAllCroutons();
                Crouton.makeText(AllBudgetTransfer.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

                pDialog = new ProgressDialog(AllBudgetTransfer.this);
                pDialog.setMessage("Getting cache data");
                pDialog.show();

                Cache cache = AppController.getInstance().getRequestQueue().getCache();
                Cache.Entry entry = cache.get(wbs_url);
                if (entry != null) {
                    //Cache data available.
                    try {
                        String data = new String(entry.data, "UTF-8");
                        Log.d("CACHE DATA", data);
                        JSONObject jsonObject = new JSONObject(data);
                        try {
                            dataArray = jsonObject.getJSONArray("data");
                            wbsNameArray = new String[dataArray.length() + 1];
                            wbsIdArray = new String[dataArray.length() + 1];

                            for(int i=0; i<dataArray.length();i++)
                            {
                                dataObject = dataArray.getJSONObject(i);
                                wbsName = dataObject.getString("wbsName");
                                wbsId = dataObject.getString("wbsId");

                                wbsNameArray[i+1]=wbsName;
                                wbsIdArray[i+1]=wbsId;
                            }

                            pDialog.dismiss();
                            prepareItems(wbsIdArray, wbsNameArray);


                        }catch (JSONException e) {
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
                    Toast.makeText(AllBudgetTransfer.this, "Offline Data Not available for WBS", Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                }
            }
            else
            {
                // Cache data not exist.
                getAllWbs();
            }
                FloatingActionButton fab_add, exportBtn;

            fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
            exportBtn = (FloatingActionButton) findViewById(R.id.exportBtn);
            fab_add.setLabelText("Create New Budget Transfer");

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
                            if (ContextCompat.checkSelfPermission(AllBudgetTransfer.this,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED) {

                                ActivityCompat.requestPermissions(AllBudgetTransfer.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                            }
                            Environment.getExternalStorageState();
                            String budgetTransferId = null, fromWbs = null, toWbs = null, budgetTransfer = null, createdBy = null,
                                     dateCreated = null;
                            int listSize = budgetList.size();
                            String cvsValues = "Budget Transfer ID" + ","+ "From WBS" + ","+ "To WBS"  + ","+ "Budget Transfer"
                                    + ","+ "Created By"  + "," + "Date Created" + "\n";

                            for(int i=0; i<listSize;i++)
                            {
                                AllBudgetTransferList items = budgetList.get(i);
                                budgetTransferId = items.getTransfer_no();
                                fromWbs = items.getText_wbs_from();
                                toWbs = items.getText_wbs_to();
                                budgetTransfer = items.getBudget_amount();
                                createdBy = items.getTransfer_by();
                                dateCreated = items.getText_date();

                                cvsValues = cvsValues +  budgetTransferId + ","+ fromWbs + ","+ toWbs +","+ budgetTransfer
                                        +","+ createdBy +","+ dateCreated + "\n";
                            }

                            CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "Budget Transfer-data.csv", cvsValues);
                        }

                        else

                        {
                            Environment.getExternalStorageState();

                            String budgetTransferId = null, fromWbs = null, toWbs = null, budgetTransfer = null, createdBy = null,
                                    totalBudget = null, description = null, dateCreated = null;
                            int listSize = budgetList.size();
                            String cvsValues = "Budget Transfer ID" + ","+ "From WBS" + ","+ "To WBS"  + ","+ "Budget Transfer"
                                    + ","+ "Created By"  + "," + "Date Created" + "\n";

                            for(int i=0; i<listSize;i++)
                            {
                                AllBudgetTransferList items = budgetList.get(i);
                                budgetTransferId = items.getTransfer_no();
                                fromWbs = items.getText_wbs_from();
                                toWbs = items.getText_wbs_to();
                                budgetTransfer = items.getBudget_amount();
                                createdBy = items.getTransfer_by();
                                dateCreated = items.getText_date();

                                cvsValues = cvsValues +  budgetTransferId + ","+ fromWbs + ","+ toWbs +","+ budgetTransfer
                                        +","+ createdBy +","+ dateCreated + "\n";
                            }

                            CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "Budget Transfer-data.csv", cvsValues);
                        }

                    }
                    break;


                    case R.id.fab_add:
                {
                        Intent intent = new Intent(AllBudgetTransfer.this, AllBudgetTransferCreate.class);
                        startActivity(intent);
                }
                break;
                }
        }

    public void getAllWbs()
    {
        pDialog = new ProgressDialog(AllBudgetTransfer.this);
        pDialog.setMessage("Getting server data");
        pDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, wbs_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {
//                            dataObject = response.getJSONObject(0);
                            Log.d("RESPONSE JSON", response.toString());
                            dataArray = response.getJSONArray("data");
                            wbsNameArray = new String[dataArray.length() + 1];
                            wbsIdArray = new String[dataArray.length() + 1];

                            for(int i=0; i<dataArray.length();i++)
                            {
                                dataObject = dataArray.getJSONObject(i);
                                wbsName = dataObject.getString("wbsName");
                                wbsId = dataObject.getString("wbsId");

                                wbsNameArray[i+1]=wbsName;
                                wbsIdArray[i+1]=wbsId;
                            }

                            prepareItems(wbsIdArray, wbsNameArray);
                            pDialog.dismiss();

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

    public void prepareItems(final String[] wbsIdArray, final String[] wbsNameArray)
        {
            String url = pm.getString("SERVER_URL") + "/getBudgetTransfer?projectId='" + currentProjectNo+"'";

            if (!isInternetPresent) {
                // Internet connection is not present
                // Ask user to connect to Internet

                pDialog = new ProgressDialog(AllBudgetTransfer.this);
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
                            for(int i=0; i<dataArray.length();i++) {
                                dataObject = dataArray.getJSONObject(i);
                                budgetTransferId = dataObject.getString("budgetTransferId");
                                fromWbs = dataObject.getString("fromWbs");
                                toWbs = dataObject.getString("toWbs");
                                budgetTransfer = dataObject.getString("budgetTransfer");
                                createdBy = dataObject.getString("createdBy");
                                dateCreated = dataObject.getString("dateCreated");

                                Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(dateCreated);
                                dateCreated = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                for (int j = 0; j < wbsIdArray.length; j++) {
                                    if (fromWbs.equals(wbsIdArray[j])) {
                                        fromWbs = wbsNameArray[j];
                                    }
                                }

                                for (int j = 0; j < wbsIdArray.length; j++) {
                                    if (toWbs.equals(wbsIdArray[j])) {
                                        toWbs = wbsNameArray[j];
                                    }
                                }

                                budgetItems = new AllBudgetTransferList(String.valueOf(i + 1), dateCreated, createdBy, budgetTransfer, toWbs, fromWbs, budgetTransferId);
                                budgetList.add(budgetItems);

                                budgetAdapter.notifyDataSetChanged();
                            }

                            Boolean createBudgetTransferPending = pm.getBoolean("createBudgetTransferPending");

                            if(createBudgetTransferPending)
                            {
                                String jsonObjectVal = pm.getString("objectBudgetTransfer");
                                Log.d("JSON BT PENDING :", jsonObjectVal);

                                JSONObject jsonObjectPending = new JSONObject(jsonObjectVal);
                                Log.d("JSONObj BT PENDING :", jsonObjectPending.toString());

                                fromWbs = dataObject.getString("fromWbs");
                                toWbs = dataObject.getString("toWbs");
                                budgetTransfer = dataObject.getString("budgetTransfer");
                                createdBy = dataObject.getString("createdBy");
                                dateCreated = dataObject.getString("dateCreated");

                                Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(dateCreated);
                                dateCreated = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                for (int j = 0; j < wbsIdArray.length; j++) {
                                    if (fromWbs.equals(wbsIdArray[j])) {
                                        fromWbs = wbsNameArray[j];
                                    }
                                }

                                for (int j = 0; j < wbsIdArray.length; j++) {
                                    if (toWbs.equals(wbsIdArray[j])) {
                                        toWbs = wbsNameArray[j];
                                    }
                                }

                                budgetItems = new AllBudgetTransferList(String.valueOf(dataArray.length() + 1), dateCreated, createdBy, budgetTransfer, toWbs, fromWbs, getResources().getString(R.string.waiting_to_connect));
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
                    Toast.makeText(AllBudgetTransfer.this, "Offline Data Not available for WBS", Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                }
            }
            else
            {
                pDialog = new ProgressDialog(AllBudgetTransfer.this);
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
                                    for(int i=0; i<dataArray.length();i++) {
                                        dataObject = dataArray.getJSONObject(i);
                                        budgetTransferId = dataObject.getString("budgetTransferId");
                                        fromWbs = dataObject.getString("fromWbs");
                                        toWbs = dataObject.getString("toWbs");
                                        budgetTransfer = dataObject.getString("budgetTransfer");
                                        createdBy = dataObject.getString("createdBy");
                                        dateCreated = dataObject.getString("dateCreated");

                                        Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(dateCreated);
                                        dateCreated = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                        for (int j = 0; j < wbsIdArray.length; j++) {
                                            if (fromWbs.equals(wbsIdArray[j])) {
                                                fromWbs = wbsNameArray[j];
                                            }
                                        }

                                        for (int j = 0; j < wbsIdArray.length; j++) {
                                            if (toWbs.equals(wbsIdArray[j])) {
                                                toWbs = wbsNameArray[j];
                                            }
                                        }

                                        budgetItems = new AllBudgetTransferList(String.valueOf(i + 1), dateCreated, createdBy, budgetTransfer, toWbs, fromWbs, budgetTransferId);
                                        budgetList.add(budgetItems);

                                        budgetAdapter.notifyDataSetChanged();
                                        pDialog.dismiss();
                                    }

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

        }

        @Override
        public void onBackPressed()
        {
                Intent intent = new Intent(AllBudgetTransfer.this, BudgetMainActivity.class);
                startActivity(intent);

        }
}