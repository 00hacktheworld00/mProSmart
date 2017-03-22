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
import android.support.v7.app.AppCompatActivity;
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
import com.example.sadashivsinha.mprosmart.Adapters.ResourceTimesheetAdapter;
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

public class ResourceTimesheetActivity extends AppCompatActivity implements View.OnClickListener {

    private List<SubcontractorList> subcontractorList = new ArrayList<>();
    private ResourceTimesheetAdapter resourceAdapter;
    SubcontractorList items;
    JSONArray dataArray;
    JSONObject dataObject;
    String lineId, wbs, activities, name, date, totalHours;
    String resourceTimesheetsId, resName, createdBy, createdDate;
    ConnectionDetector cd;
    public static final String TAG = NewAllProjects.class.getSimpleName();
    Boolean isInternetPresent = false;
    TextView title, subtitle, res_name, res_no;
    String currentResourceId, currentProjectNo;
    ProgressDialog pDialog, pDialog1;
    TextView sub_id, sub_name, created_by, sub_date;
    PreferenceManager pm;
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

                getSupportActionBar().setTitle("Resource Timesheet Search Results : " + searchText);
            }
        }
        pm = new PreferenceManager(this);
        currentResourceId = pm.getString("resourceId");
        currentProjectNo = pm.getString("projectId");

        url = pm.getString("SERVER_URL") + "/getResourceLineItems?resourceTimesheetsId='"+currentResourceId+"'";

        title = (TextView) findViewById(R.id.title);
        subtitle = (TextView) findViewById(R.id.subtitle);

        title.setText("Resource ID : ");
        subtitle.setText("Resource Name : ");

        res_name = (TextView) findViewById(R.id.res_name);
        res_no = (TextView) findViewById(R.id.res_no);

        sub_id = (TextView) findViewById(R.id.sub_id);
        sub_name = (TextView) findViewById(R.id.sub_name);
        created_by = (TextView) findViewById(R.id.created_by);
        sub_date = (TextView) findViewById(R.id.date);

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        resourceAdapter = new ResourceTimesheetAdapter(subcontractorList);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(resourceAdapter);

        prepareHeader();

        // check for Internet status
        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(ResourceTimesheetActivity.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(ResourceTimesheetActivity.this);
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
                            lineId = dataObject.getString("resourceLineItemsId");
                            wbs = dataObject.getString("wbs");
                            activities = dataObject.getString("activities");
                            name = dataObject.getString("name");
                            date = dataObject.getString("date");
                            totalHours = dataObject.getString("totalHours");

                            if (getIntent().hasExtra("search"))
                            {
                                if (getIntent().getStringExtra("search").equals("yes")) {

                                    if (lineId.toLowerCase().contains(searchText.toLowerCase()) || name.toLowerCase().contains(searchText.toLowerCase())) {

                                        items = new SubcontractorList(lineId, wbs, activities, name,date,totalHours);
                                        subcontractorList.add(items);

                                        resourceAdapter.notifyDataSetChanged();

                                    }
                                }
                            }
                            else
                            {
                                items = new SubcontractorList(lineId, wbs, activities, name,date,totalHours);
                                subcontractorList.add(items);

                                resourceAdapter.notifyDataSetChanged();

                            }
                            pDialog.dismiss();
                        }

                        Boolean createResourceLineItem = pm.getBoolean("createResourceLineItem");

                        if (createResourceLineItem) {

                            String jsonObjectVal = pm.getString("objectResourceLineItem");
                            Log.d("JSON ResLin PENDING :", jsonObjectVal);

                            JSONObject jsonObjectPending = new JSONObject(jsonObjectVal);
                            Log.d("JSONObj ResLi PENDING :", jsonObjectPending.toString());

                            wbs = dataObject.getString("wbs");
                            activities = dataObject.getString("activities");
                            name = dataObject.getString("name");
                            date = dataObject.getString("date");
                            totalHours = dataObject.getString("totalHours");

                            Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
                            date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);


                            items = new SubcontractorList(String.valueOf(dataArray.length()), wbs, activities, name,date,totalHours);
                            subcontractorList.add(items);

                            resourceAdapter.notifyDataSetChanged();

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
                Toast.makeText(ResourceTimesheetActivity.this, "Offline Data Not available for this Resource Timesheet", Toast.LENGTH_SHORT).show();
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

        fab_add.setLabelText("Add new resource item");

        fab_add.setOnClickListener(this);
        fab_search.setOnClickListener(this);
        exportBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add:
            {
                Intent intent = new Intent(ResourceTimesheetActivity.this, ResourceItemCreate.class);
                startActivity(intent);
            }
            break;
            case R.id.exportBtn:
            {
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP)
                {
                    if (ContextCompat.checkSelfPermission(ResourceTimesheetActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(ResourceTimesheetActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }

                    Environment.getExternalStorageState();

                    String line_no=null, text_wbs=null, text_activities=null, text_resourceName=null, text_date=null, status=null, text_totalHours=null;
                    int listSize = subcontractorList.size();
                    String cvsValues = "Resource ID" + ","+"Line No." + ","+ "WBS" + ","+ "Activities" + ","+ "Resource Name" + ","+ "Date" + ","+ "Total Hours"+ "\n";

                    for(int i=0; i<listSize;i++)
                    {
                        SubcontractorList items = subcontractorList.get(i);
                        line_no = items.getText_line_no();
                        text_wbs = items.getText_wbs();
                        text_activities = items.getText_activities();
                        text_resourceName = items.getText_res_name();
                        text_date = items.getText_date();
                        text_totalHours = items.getText_total_hours();

                        cvsValues = cvsValues + currentResourceId + ","+  line_no + ","+  text_wbs +  ","+ text_activities + ","+ text_resourceName + ","+ text_date + ","+ text_totalHours + "\n";
                    }
                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "ResourceTimesheet-ID-"+currentResourceId+".csv", cvsValues);
                }

                else

                {
                    Environment.getExternalStorageState();

                    String line_no=null, text_wbs=null, text_activities=null, text_resourceName=null, text_date=null, status=null, text_totalHours=null;
                    int listSize = subcontractorList.size();
                    String cvsValues = "Resource ID" + ","+"Line No." + ","+ "WBS" + ","+ "Activities" + ","+ "Resource Name" + ","+ "Date" + ","+ "Total Hours"+ "\n";

                    for(int i=0; i<listSize;i++)
                    {
                        SubcontractorList items = subcontractorList.get(i);
                        line_no = items.getText_line_no();
                        text_wbs = items.getText_wbs();
                        text_activities = items.getText_activities();
                        text_resourceName = items.getText_res_name();
                        text_date = items.getText_date();
                        text_totalHours = items.getText_total_hours();

                        cvsValues = cvsValues + currentResourceId + ","+  line_no + ","+  text_wbs +  ","+ text_activities + ","+ text_resourceName + ","+ text_date + ","+ text_totalHours + "\n";
                    }
                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "ResourceTimesheet-ID-"+currentResourceId+".csv", cvsValues);
                }
            }
            break;
            case R.id.fab_search:
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Search Resource Timesheet by Name or ID !");
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
                            Intent intent = new Intent(ResourceTimesheetActivity.this, ResourceTimesheetActivity.class);
                            intent.putExtra("search", "yes");
                            intent.putExtra("searchText", input.getText().toString());

                            Log.d("SEARCH TEXT", input.getText().toString());

                            startActivity(intent);
                        }
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(ResourceTimesheetActivity.this, "Search cancelled .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
            }
            break;
        }
    }

    private void callJsonArrayRequest() {
        // TODO Auto-generated method stub

        pDialog = new ProgressDialog(ResourceTimesheetActivity.this);
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

                                lineId = dataObject.getString("resourceLineItemsId");
                                wbs = dataObject.getString("wbs");
                                activities = dataObject.getString("activities");
                                name = dataObject.getString("name");
                                date = dataObject.getString("date");
                                totalHours = dataObject.getString("totalHours");

                                if (getIntent().hasExtra("search"))
                                {
                                    if (getIntent().getStringExtra("search").equals("yes")) {

                                        if (lineId.toLowerCase().contains(searchText.toLowerCase()) || name.toLowerCase().contains(searchText.toLowerCase())) {

                                            items = new SubcontractorList(lineId, wbs, activities, name,date,totalHours);
                                            subcontractorList.add(items);

                                            resourceAdapter.notifyDataSetChanged();

                                        }
                                    }
                                }
                                else
                                {
                                    items = new SubcontractorList(lineId, wbs, activities, name,date,totalHours);
                                    subcontractorList.add(items);

                                    resourceAdapter.notifyDataSetChanged();

                                }
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

    public void prepareHeader()
    {
        sub_id.setText(pm.getString("resourceId"));
        sub_name.setText(pm.getString("resourceName"));
        created_by.setText(pm.getString("resourceCreatedBy"));
        sub_date.setText(pm.getString("resourceDate"));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ResourceTimesheetActivity.this, AllResource.class);
        startActivity(intent);
    }
}