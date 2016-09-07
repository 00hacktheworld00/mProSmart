package com.example.sadashivsinha.mprosmart.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.Adapters.ResourceTimesheetAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.SubcontractorList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.example.sadashivsinha.mprosmart.Utils.CsvCreateUtility;
import com.github.clans.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_subcontractor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        pm = new PreferenceManager(this);
        currentResourceId = pm.getString("resourceId");
        currentProjectNo = pm.getString("projectId");

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

        // check for Internet status
        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_content = (RelativeLayout) findViewById(R.id.main_content);
            Snackbar snackbar = Snackbar.make(main_content,getResources().getString(R.string.no_internet_error), Snackbar.LENGTH_LONG);
            snackbar.show();
        }
        prepareHeader();

        class MyTask extends AsyncTask<Void, Void, Void>
        {
            @Override protected void onPreExecute()
            {
                pDialog = new ProgressDialog(ResourceTimesheetActivity.this);
                pDialog.setMessage("Getting Data ...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();

                resourceAdapter = new ResourceTimesheetAdapter(subcontractorList);
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerView.setHasFixedSize(false);
                recyclerView.setAdapter(resourceAdapter);

            }

            @Override
            protected Void doInBackground(Void... params)
            {
                prepareItems();
                return null;
            }

            @Override protected void onPostExecute(Void result)
            {
                pDialog.dismiss();
                resourceAdapter.notifyDataSetChanged();
            }

        }

        new MyTask().execute();



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
                alert.setTitle("Search Resource Timesheet !");
                // Set an EditText view to get user input
                final EditText input = new EditText(this);
                alert.setView(input);
                alert.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(ResourceTimesheetActivity.this, "Search for it .", Toast.LENGTH_SHORT).show();
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

    private void prepareItems() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = getResources().getString(R.string.server_url) + "/getResourceLineItems?resourceTimesheetsId='"+currentResourceId+"'";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(ResourceTimesheetActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
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


                                    items = new SubcontractorList(lineId, wbs, activities, name,date,totalHours);
                                    subcontractorList.add(items);

                                    resourceAdapter.notifyDataSetChanged();
                                }
                            }

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