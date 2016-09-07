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
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
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
import com.example.sadashivsinha.mprosmart.Adapters.MyAdapter;
import com.example.sadashivsinha.mprosmart.Adapters.PunchItemsAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.MomList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.example.sadashivsinha.mprosmart.Utils.CsvCreateUtility;
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

public class PunchListActivity extends NewActivity implements View.OnClickListener  {
    private List<MomList> momList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PunchItemsAdapter punchItemsAdapter;
    TextView punch_list_no, project_no, project_name, vendor_id, vendor_name, date, created_by;
    JSONArray dataArray;
    JSONObject dataObject;
    String projectId, projectName, vendorId, vendorName, createdDate, createdBy, punchListNo, description;
    ConnectionDetector cd;
    public static final String TAG = NewAllProjects.class.getSimpleName();
    Boolean isInternetPresent = false;
    MomList items;
    private ProgressDialog pDialog, pDialog1, pDialog2;
    String currentPunchListNo, currentProjectNo, lineNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_punch_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        punch_list_no = (TextView) findViewById(R.id.punch_list_no);
        project_no = (TextView) findViewById(R.id.project_no);
        project_name = (TextView) findViewById(R.id.project_name);
        vendor_id = (TextView) findViewById(R.id.vendor_id);
        vendor_name = (TextView) findViewById(R.id.vendor_name);
        date = (TextView) findViewById(R.id.date);
        created_by = (TextView) findViewById(R.id.created_by);

        final PreferenceManager pm = new PreferenceManager(getApplicationContext());
        currentPunchListNo = pm.getString("punchListNo");
        currentProjectNo = pm.getString("projectId");

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
        pDialog1 = new ProgressDialog(PunchListActivity.this);
        pDialog1.setMessage("Preparing Header ...");
        pDialog1.setIndeterminate(false);
        pDialog1.setCancelable(true);
        pDialog1.show();
        prepareHeader();

        pDialog = new ProgressDialog(PunchListActivity.this);
        pDialog.setMessage("Getting Data ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        punchItemsAdapter = new PunchItemsAdapter(momList);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(punchItemsAdapter);

        if(getIntent().hasExtra("search"))
        {
            if(getIntent().getStringExtra("search").equals("yes"))
            {
                //searched values

                final String searchText = getIntent().getStringExtra("searchText");

                getSupportActionBar().setTitle("Punch List Search Results : " + searchText);

                class MyTask extends AsyncTask<Void, Void, Void> {

                    @Override
                    protected Void doInBackground(Void... params) {
                        prepareSearchedValues(searchText);
                        return null;
                    }
                }
                new MyTask().execute();

            }
        }
        else
        {
            class MyTask extends AsyncTask<Void, Void, Void>
            {
                @Override
                protected Void doInBackground(Void... params)
                {
                    prepareItems();
                    return null;
                }
            }

            new MyTask().execute();
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View
        mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size
        mAdapter = new MyAdapter(TITLES,ICONS,NAME,EMAIL,PROFILE);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,header view name, header view email,
        // and header view profile picture
        mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView
        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager
        mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager
        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);        // Drawer object Assigned to the view
        mDrawerToggle = new ActionBarDrawerToggle(this,Drawer,toolbar,R.string.openDrawer,R.string.closeDrawer){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
                // open I am not going to put anything here)
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
            }

        }; // Drawer Toggle Object Made
        Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State
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
                Intent intent = new Intent(PunchListActivity.this, PunchItemCreate.class);
                startActivity(intent);
            }
            break;
            case R.id.exportBtn:
            {
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP)
                {
                    if (ContextCompat.checkSelfPermission(PunchListActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(PunchListActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                    pDialog2 = new ProgressDialog(PunchListActivity.this);
                    pDialog2.setMessage("Loading Data from server ...");
                    pDialog2.setIndeterminate(false);
                    pDialog2.setCancelable(true);
                    pDialog2.show();

                    class MyTask extends AsyncTask<Void, Void, Void>
                    {

                        @Override
                        protected Void doInBackground(Void... params)
                        {
                            Environment.getExternalStorageState();

                            RequestQueue requestQueue = Volley.newRequestQueue(PunchListActivity.this);

                            String url = getResources().getString(R.string.server_url) + "/getPunchListLines?punchListId=\""+currentPunchListNo+"\"";

                            JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {

                                            try{
                                                String cvsValues = null;

                                                String type = response.getString("type");

                                                if(type.equals("ERROR"))
                                                {
                                                    Toast.makeText(PunchListActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                                                }

                                                if(type.equals("INFO"))
                                                {
                                                    String line_no=null, location=null, desc=null, item_type=null, notes=null, responsible=null, priority=null,
                                                            status=null, scheduled=null, date=null, architect=null;
                                                    cvsValues = "Punch List ID" + ","+"Line No." + ","+ "Location" + ","+ "Item description" + ","+ "Item Type"
                                                            + ","+ "Notes" + ","+ "Responsible Party" + ","+ "Priority" + ","+ "Status" + ","+ "Scheduled"+
                                                            ","+ "Date Complete" + ","+ "Architect accepted"+ "\n";

                                                    dataArray = response.getJSONArray("data");
                                                    for(int i=0; i<dataArray.length();i++)
                                                    {
                                                        dataObject = dataArray.getJSONObject(i);

                                                        line_no = dataObject.getString("puncListLinesId");
                                                        location = dataObject.getString("location");
                                                        desc = dataObject.getString("description");
                                                        item_type = dataObject.getString("itemType");
                                                        notes = dataObject.getString("notes");
                                                        responsible = dataObject.getString("responsible");
                                                        priority = dataObject.getString("priority");
                                                        status = dataObject.getString("status");
                                                        scheduled = dataObject.getString("scheduleToComplete");
                                                        date = dataObject.getString("dateComplted");
                                                        architect = dataObject.getString("architectAccepted");


                                                        cvsValues = cvsValues + currentPunchListNo + ","+  line_no + ","+  location +  ","+ desc + ","+ item_type + ","+ notes +
                                                                ","+ responsible + "," + priority + ","+ status + ","+ scheduled + ","+ date +  ","+ architect + "\n";
                                                    }
                                                }
                                                pDialog2.dismiss();
                                                CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "PunchList-ID-"+currentPunchListNo+".csv", cvsValues);

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
                            return null;
                        }
                    }

                    new MyTask().execute();
                }

                else
                {
                    pDialog2 = new ProgressDialog(PunchListActivity.this);
                    pDialog2.setMessage("Loading Data from server ...");
                    pDialog2.setIndeterminate(false);
                    pDialog2.setCancelable(true);
                    pDialog2.show();

                    class MyTask extends AsyncTask<Void, Void, Void> {

                        @Override
                        protected Void doInBackground(Void... params) {
                            Environment.getExternalStorageState();

                            RequestQueue requestQueue = Volley.newRequestQueue(PunchListActivity.this);

                            String url = getResources().getString(R.string.server_url) + "/getPunchListLines?punchListId=\"" + currentPunchListNo + "\"";

                            JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {

                                            try {
                                                String cvsValues = null;

                                                String type = response.getString("type");

                                                if (type.equals("ERROR")) {
                                                    Toast.makeText(PunchListActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                                                }

                                                if (type.equals("INFO")) {

                                                    String line_no = null, location = null, desc = null, item_type = null, notes = null, responsible = null, priority = null,
                                                            status = null, scheduled = null, date = null, architect = null;
                                                    cvsValues = "Punch List ID" + "," + "Line No." + "," + "Location" + "," + "Item description" + "," + "Item Type"
                                                            + "," + "Notes" + "," + "Responsible Party" + "," + "Priority" + "," + "Status" + "," + "Scheduled" +
                                                            "," + "Date Complete" + "," + "Architect accepted" + "\n";

                                                    dataArray = response.getJSONArray("data");
                                                    for (int i = 0; i < dataArray.length(); i++) {
                                                        dataObject = dataArray.getJSONObject(i);

                                                        line_no = dataObject.getString("puncListLinesId");
                                                        location = dataObject.getString("location");
                                                        desc = dataObject.getString("description");
                                                        item_type = dataObject.getString("itemType");
                                                        notes = dataObject.getString("notes");
                                                        responsible = dataObject.getString("responsible");
                                                        priority = dataObject.getString("priority");
                                                        status = dataObject.getString("status");
                                                        scheduled = dataObject.getString("scheduleToComplete");
                                                        date = dataObject.getString("dateComplted");
                                                        architect = dataObject.getString("architectAccepted");


                                                        cvsValues = cvsValues + currentPunchListNo + "," + line_no + "," + location + "," + desc + "," + item_type + "," + notes +
                                                                "," + responsible + "," + priority + "," + status + "," + scheduled + "," + date + "," + architect + "\n";
                                                    }
                                                }
                                                pDialog2.dismiss();
                                                CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "PunchList-ID-" + currentPunchListNo + ".csv", cvsValues);

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
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
                            return null;
                        }
                    }
                    new MyTask().execute();
                }
            }
            break;
            case R.id.fab_search:
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Search Punch item !");
                // Set an EditText view to get user input
                final EditText input = new EditText(this);
                alert.setView(input);
                alert.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(input.getText().toString().isEmpty())
                        {
                            input.setError("Enter Search Field");
                        }
                        else
                        {
                            Intent intent = new Intent(PunchListActivity.this, PunchListActivity.class);
                            intent.putExtra("search", "yes");
                            intent.putExtra("searchText", input.getText().toString());
                            startActivity(intent);
                        }
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(PunchListActivity.this, "Search cancelled .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
            }
            break;
        }
    }

    public void prepareItems()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = getResources().getString(R.string.server_url) + "/getPunchListLines?punchListId='"+currentPunchListNo+"'";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(PunchListActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                Log.d("response pl:", response.toString());
                                dataArray = response.getJSONArray("data");
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    lineNo = dataObject.getString("puncListLinesId");
                                    description = dataObject.getString("description");

                                    items = new MomList(lineNo, description);
                                    momList.add(items);

                                    punchItemsAdapter.notifyDataSetChanged();
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


    public void prepareSearchedValues(final String searchText)
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = getResources().getString(R.string.server_url) + "/getPunchListLines?punchListId='"+currentPunchListNo+"'";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{
                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(PunchListActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                Log.d("response pl:", response.toString());
                                dataArray = response.getJSONArray("data");
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    lineNo = dataObject.getString("puncListLinesId");
                                    description = dataObject.getString("description");

                                    if(lineNo.toLowerCase().contains(searchText.toLowerCase()))
                                    {
                                        items = new MomList(lineNo, description);
                                        momList.add(items);
                                    }

                                    punchItemsAdapter.notifyDataSetChanged();
                                }

                                if(momList.size()==0)
                                {
                                    Toast.makeText(PunchListActivity.this, "Search didn't match any data", Toast.LENGTH_SHORT).show();
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
        String url = getResources().getString(R.string.server_url) + "/getAllPunchLists?projectId='"+currentProjectNo+"'";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(PunchListActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    if(dataObject.getString("punchListId").equals(currentPunchListNo))
                                    {
                                        projectName = dataObject.getString("projectName");
                                        vendorId = dataObject.getString("vendorId");
                                        vendorName = dataObject.getString("vendorName");
                                        createdDate = dataObject.getString("createdDate");
                                        createdBy = dataObject.getString("createdBy");
                                        punchListNo = dataObject.getString("punchListId");

                                        Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(createdDate);

                                        String formattedDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);
                                        date.setText(String.valueOf(formattedDate));

                                        punch_list_no.setText(punchListNo);
                                        project_no.setText(currentProjectNo);
                                        project_name.setText(projectName);
                                        vendor_id.setText(vendorId);
                                        vendor_name.setText(vendorName);
                                        created_by.setText(createdBy);
                                    }
                                }
                            }
                            pDialog1.dismiss();
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
        Intent intent = new Intent(PunchListActivity.this, AllPunchLists.class);
        startActivity(intent);
    }

}
