package com.example.sadashivsinha.mprosmart.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.sadashivsinha.mprosmart.Adapters.SubcontractorAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.MomList;
import com.example.sadashivsinha.mprosmart.ModelLists.SubcontractorList;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class SubcontractorActivity extends NewActivity implements View.OnClickListener {

    private List<SubcontractorList> subcontractorList = new ArrayList<>();
    private SubcontractorAdapter subcontractorAdapter;
    JSONArray dataArray;
    JSONObject dataObject;
    String subContractorLineItems, wbs, activities, resourceName, date, totalHours;
    ConnectionDetector cd;
    public static final String TAG = NewAllProjects.class.getSimpleName();
    Boolean isInternetPresent = false;
    SubcontractorList items;
    private ProgressDialog pDialog1, pDialog2;
    TextView sub_id, sub_name, date_created, created_by;
    String subId, subName, dateCreated, createdBy;
    String currentSubId;
    String currentProjectNo;

    PreferenceManager pm;
    ProgressDialog pDialog;
    String url, searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_subcontractor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getIntent().hasExtra("search")) {
            if (getIntent().getStringExtra("search").equals("yes")) {

                searchText = getIntent().getStringExtra("searchText");

                getSupportActionBar().setTitle("Subcontractor Search Results : " + searchText);
            }
        }

        pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentSubId = pm.getString("subcontractorId");

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        sub_id = (TextView) findViewById(R.id.sub_id);
        sub_name = (TextView) findViewById(R.id.sub_name);
        date_created = (TextView) findViewById(R.id.date);
        created_by = (TextView) findViewById(R.id.created_by);


        prepareHeader();

        url = pm.getString("SERVER_URL") + "/subContractorLineItems?subContractor='"+currentSubId+"'";

        subcontractorAdapter = new SubcontractorAdapter(subcontractorList);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(subcontractorAdapter);

        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(SubcontractorActivity.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(SubcontractorActivity.this);
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
                            subContractorLineItems = dataObject.getString("subContractorLineItems");
                            wbs = dataObject.getString("wbs");
                            activities = dataObject.getString("activities");
                            resourceName = dataObject.getString("resourceName");
                            date = dataObject.getString("date");
                            totalHours = dataObject.getString("totalHours");

                            Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
                            date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                            if (getIntent().hasExtra("search"))
                            {
                                if (getIntent().getStringExtra("search").equals("yes")) {

                                    if (subContractorLineItems.toLowerCase().contains(searchText.toLowerCase()) || resourceName.toLowerCase().contains(searchText.toLowerCase())) {
                                        items = new SubcontractorList(String.valueOf(i + 1), wbs, activities, resourceName, date
                                                , totalHours);
                                        subcontractorList.add(items);

                                        subcontractorAdapter.notifyDataSetChanged();


                                    }
                                }
                            }
                            else
                            {items = new SubcontractorList(String.valueOf(i + 1), wbs, activities, resourceName, date
                                    , totalHours);
                                subcontractorList.add(items);

                                subcontractorAdapter.notifyDataSetChanged();


                            }
                            pDialog.dismiss();
                        }

                        Boolean createSubcontractorLineItem = pm.getBoolean("createSubcontractorLineItem");

                        if (createSubcontractorLineItem) {

                            String jsonObjectVal = pm.getString("objectSubcontractorLineItem");
                            Log.d("JSON Subc PENDING :", jsonObjectVal);

                            JSONObject jsonObjectPending = new JSONObject(jsonObjectVal);
                            Log.d("JSONObj Subc PENDING :", jsonObjectPending.toString());

                            wbs = jsonObjectPending.getString("wbs");
                            activities = jsonObjectPending.getString("activities");
                            resourceName = jsonObjectPending.getString("resourceName");
                            date = jsonObjectPending.getString("date");
                            totalHours = jsonObjectPending.getString("totalHours");

                            Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
                            date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                            items = new SubcontractorList(String.valueOf(dataArray.length()+1), wbs, activities, resourceName, date
                                    ,totalHours);
                            subcontractorList.add(items);

                            subcontractorAdapter.notifyDataSetChanged();
                        }
                    }catch (ParseException | JSONException e) {
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
                Toast.makeText(SubcontractorActivity.this, "Offline Data Not available for this Subcontractor Timesheet", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }

        else
        {
            // Cache data not exist.
            callJsonArrayRequest();
        }

        FloatingActionButton fab_add, fab_search, exportBtn;

        fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        fab_search = (FloatingActionButton) findViewById(R.id.fab_search);
        exportBtn = (FloatingActionButton) findViewById(R.id.exportBtn);

        fab_add.setLabelText("Add new Subcontractor item");

        fab_add.setOnClickListener(this);
        fab_search.setOnClickListener(this);
        exportBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add:
            {
                Intent intent = new Intent(SubcontractorActivity.this, SubcontractorItemCreate.class);
                startActivity(intent);
            }
            break;
            case R.id.exportBtn:
            {
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP)
                {
                    if (ContextCompat.checkSelfPermission(SubcontractorActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(SubcontractorActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }

                    Environment.getExternalStorageState();

                    String line_no=null, text_wbs=null, text_activities=null, text_resourceName=null, text_date=null, status=null, text_totalHours=null;
                    int listSize = subcontractorList.size();
                    String cvsValues = "Subcontractor ID" + ","+"Line No." + ","+ "WBS" + ","+ "Activities" + ","+ "Resource Name" + ","+ "Date" + ","+ "Total Hours"+ "\n";

                    for(int i=0; i<listSize;i++)
                    {
                        SubcontractorList items = subcontractorList.get(i);
                        line_no = items.getText_line_no();
                        text_wbs = items.getText_wbs();
                        text_activities = items.getText_activities();
                        text_resourceName = items.getText_res_name();
                        text_date = items.getText_date();
                        text_totalHours = items.getText_total_hours();

                        cvsValues = cvsValues + currentSubId + ","+  line_no + ","+  text_wbs +  ","+ text_activities + ","+ text_resourceName + ","+ text_date + ","+ text_totalHours + "\n";
                    }
                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "Subcontractor-ID-"+currentSubId+".csv", cvsValues);
                }

                else

                {
                    Environment.getExternalStorageState();

                    String line_no=null, text_wbs=null, text_activities=null, text_resourceName=null, text_date=null, status=null, text_totalHours=null;
                    int listSize = subcontractorList.size();
                    String cvsValues = "Subcontractor ID" + ","+"Line No." + ","+ "WBS" + ","+ "Activities" + ","+ "Resource Name" + ","+ "Date" + ","+ "Total Hours"+ "\n";

                    for(int i=0; i<listSize;i++)
                    {
                        SubcontractorList items = subcontractorList.get(i);
                        line_no = items.getText_line_no();
                        text_wbs = items.getText_wbs();
                        text_activities = items.getText_activities();
                        text_resourceName = items.getText_res_name();
                        text_date = items.getText_date();
                        text_totalHours = items.getText_total_hours();

                        cvsValues = cvsValues + currentSubId + ","+  line_no + ","+  text_wbs +  ","+ text_activities + ","+ text_resourceName + ","+ text_date + ","+ text_totalHours + "\n";
                    }
                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "Subcontractor-ID-"+currentSubId+".csv", cvsValues);
                }
            }
            break;
            case R.id.fab_search:
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Search Subcontractors by Name or ID !");
                // Set an EditText view to get user input
                final EditText input = new EditText(this);
                input.setMaxLines(1);
                input.setImeOptions(EditorInfo.IME_ACTION_DONE);
                alert.setView(input);
                alert.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        if (input.getText().toString().isEmpty()) {
                            input.setError("Enter Search Field");
                        } else {
                            Intent intent = new Intent(SubcontractorActivity.this, SubcontractorActivity.class);
                            intent.putExtra("search", "yes");
                            intent.putExtra("searchText", input.getText().toString());

                            Log.d("SEARCH TEXT", input.getText().toString());

                            startActivity(intent);
                        }
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(SubcontractorActivity.this, "Search cancelled .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
            }
            break;
        }
    }

    private void callJsonArrayRequest() {
        // TODO Auto-generated method stub

        pDialog = new ProgressDialog(SubcontractorActivity.this);
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

                                subContractorLineItems = dataObject.getString("subContractorLineItems");
                                wbs = dataObject.getString("wbs");
                                activities = dataObject.getString("activities");
                                resourceName = dataObject.getString("resourceName");
                                date = dataObject.getString("date");
                                totalHours = dataObject.getString("totalHours");

                                Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
                                date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                if (getIntent().hasExtra("search"))
                                {
                                    if (getIntent().getStringExtra("search").equals("yes")) {

                                        if (subContractorLineItems.toLowerCase().contains(searchText.toLowerCase()) || resourceName.toLowerCase().contains(searchText.toLowerCase())) {
                                            items = new SubcontractorList(String.valueOf(i + 1), wbs, activities, resourceName, date
                                                    , totalHours);
                                            subcontractorList.add(items);

                                            subcontractorAdapter.notifyDataSetChanged();


                                        }
                                    }
                                }
                                else
                                {items = new SubcontractorList(String.valueOf(i + 1), wbs, activities, resourceName, date
                                        , totalHours);
                                    subcontractorList.add(items);

                                    subcontractorAdapter.notifyDataSetChanged();


                                }
                            }
                        } catch (ParseException | JSONException e) {
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

    public void prepareHeader()
    {
        sub_id.setText(pm.getString("subcontractorId"));
        sub_name.setText(pm.getString("subcontractorName"));
        date_created.setText(pm.getString("subcontractorDate"));
        created_by.setText(pm.getString("subcontractorCreatedBy"));
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SubcontractorActivity.this, AllSubcontractor.class);
        startActivity(intent);
    }
}