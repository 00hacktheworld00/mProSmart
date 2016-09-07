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
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.Adapters.AllSubcontractorAdapter;
import com.example.sadashivsinha.mprosmart.Adapters.MyAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.MomList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.AppController;
import com.example.sadashivsinha.mprosmart.Utils.Communicator;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.example.sadashivsinha.mprosmart.Utils.CsvCreateUtility;
import com.example.sadashivsinha.mprosmart.font.HelveticaRegular;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

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

public class AllSubcontractor extends NewActivity implements View.OnClickListener  {
    private List<MomList> momList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AllSubcontractorAdapter allSubcontractorAdapter;
    String flag = "false";
    String vendorInvoiceNo, poNumber, date, invoiceNo;
    MomList items;
    private ProgressDialog pDialog;
    JSONArray dataArray;
    JSONObject dataObject;
    String subContractorId, name, createdBy, createdDate;
    ConnectionDetector cd;
    public static final String TAG = NewAllProjects.class.getSimpleName();
    Boolean isInternetPresent = false;
    String currentProjectNo, currentUser, currentDate;
    View dialogView;
    AlertDialog show;
    FloatingActionMenu menu;
    LinearLayout hiddenLayout;
    HelveticaRegular text_subcontractor;
    PreferenceManager pm;

    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_inovices);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentUser = pm.getString("userId");

        menu = (FloatingActionMenu) findViewById(R.id.menu);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = sdf.format(c.getTime());

        url = getResources().getString(R.string.server_url) + "/getSubContractors?projectId='"+currentProjectNo+"'";

        allSubcontractorAdapter = new AllSubcontractorAdapter(momList);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(AllSubcontractor.this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(allSubcontractorAdapter);

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status

        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(AllSubcontractor.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(AllSubcontractor.this);
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
                            subContractorId = dataObject.getString("subContractorId");
                            name = dataObject.getString("name");
                            createdBy = dataObject.getString("createdBy");
                            createdDate = dataObject.getString("createdDate");

                            items = new MomList(String.valueOf(i+1), subContractorId, name, createdDate, createdBy);
                            momList.add(items);

                            allSubcontractorAdapter.notifyDataSetChanged();
                            pDialog.dismiss();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    Toast.makeText(getApplicationContext(), "Loading from cache.", Toast.LENGTH_SHORT).show();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (pDialog != null)
                    pDialog.dismiss();
            }

            else
            {
                Toast.makeText(AllSubcontractor.this, "Offline Data Not available for Subcontractor Timesheets", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }

        else
        {
            // Cache data not exist.
            callJsonArrayRequest();
        }

        if(getIntent().hasExtra("create"))
        {
            if(getIntent().getStringExtra("create").equals("yes"))
            {
                createNewSubcontractor();
            }
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

        fab_add.setLabelText("Add new Subcontractor Timesheet");

        fab_add.setOnClickListener(this);
        fab_search.setOnClickListener(this);
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
                    if (ContextCompat.checkSelfPermission(AllSubcontractor.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(AllSubcontractor.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                    }
                    Environment.getExternalStorageState();

                    String sub_id = null, sub_name = null, date = null, created_by = null;
                    int listSize = momList.size();
                    String cvsValues = "Subcontractor ID" + ","+ "Subcontractor Name" + ","+ "Date"  + ","+ "Created By" + "\n";

                    for(int i=0; i<listSize;i++)
                    {
                        MomList items = momList.get(i);
                        sub_id = items.getText_matter();
                        sub_name = items.getText_responsible();
                        date = items.getText_attachments();
                        created_by = items.getText_date();

                        cvsValues = cvsValues +  sub_id + ","+ sub_name + ","+ date +","+ created_by + "\n";
                    }

                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "Subcontractor-data.csv", cvsValues);
                }

                else

                {
                    Environment.getExternalStorageState();

                    String sub_id = null, sub_name = null, date = null, created_by = null;
                    int listSize = momList.size();
                    String cvsValues = "Subcontractor ID" + ","+ "Subcontractor Name" + ","+ "Date"  + ","+ "Created By" + "\n";

                    for(int i=0; i<listSize;i++)
                    {
                        MomList items = momList.get(i);
                        sub_id = items.getText_matter();
                        sub_name = items.getText_responsible();
                        date = items.getText_attachments();
                        created_by = items.getText_date();

                        cvsValues = cvsValues +  sub_id + ","+ sub_name + ","+ date +","+ created_by + "\n";
                    }

                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "Subcontractor-data.csv", cvsValues);
                }

            }
            break;
            case R.id.fab_add:
            {
//                Intent intent = new Intent(AllSubcontractor.this, SubcontractorCreateActivity.class);
//                startActivity(intent);

                createNewSubcontractor();
            }
            break;
            case R.id.fab_search:
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Search Subcontractor Timesheet !");
                // Set an EditText view to get user input
                final EditText input = new EditText(this);
                alert.setView(input);
                alert.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(AllSubcontractor.this, "Search for it .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(AllSubcontractor.this, "Search cancelled .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
            }
            break;
        }
    }
    private void callJsonArrayRequest() {
        // TODO Auto-generated method stub

        pDialog = new ProgressDialog(AllSubcontractor.this);
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
                                subContractorId = dataObject.getString("subContractorId");
                                name = dataObject.getString("name");
                                createdBy = dataObject.getString("createdBy");
                                createdDate = dataObject.getString("createdDate");

                                items = new MomList(String.valueOf(i+1), subContractorId, name, createdDate, createdBy);
                                momList.add(items);

                                allSubcontractorAdapter.notifyDataSetChanged();
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

    public void createSubcontractor(String subContractorId)
    {
        JSONObject object = new JSONObject();

        try {
            object.put("name",subContractorId);
            object.put("projectId",currentProjectNo);
            object.put("createdBy",currentUser);
            object.put("createdDate", currentDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(AllSubcontractor.this);

        String url = AllSubcontractor.this.getResources().getString(R.string.server_url) + "/postSubContractor";
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            Toast.makeText(AllSubcontractor.this, "Subcontractor Created . ID - "+ response.getString("data"), Toast.LENGTH_SHORT).show();

                            pm.putString("subcontractorId", response.getString("data"));
                            pDialog.dismiss();
                            Intent intent = new Intent(AllSubcontractor.this, SubcontractorItemCreate.class);
                            startActivity(intent);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //response success message display
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley","Error");
                        pDialog.dismiss();
                    }
                }
        );
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        if (!isInternetPresent)
        {
            // Internet connection is not present

            Communicator communicator = new Communicator();
            Log.d("object", object.toString());

            Boolean createSubcontractorTimesheet = pm.getBoolean("createSubcontractorTimesheet");

            if(createSubcontractorTimesheet)
            {
                Toast.makeText(AllSubcontractor.this, "Already a Subcontractor Timesheet creation is in progress. Please try after sometime.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(AllSubcontractor.this, "Internet not currently available. Subcontractor Timesheet will automatically get created on internet connection.", Toast.LENGTH_SHORT).show();

                pm.putString("objectSubcontractorTimesheet", object.toString());
                pm.putString("urlSubcontractorTimesheet", url);
                pm.putString("toastMessageSubcontractorTimesheet", "Subcontractor Created");
                pm.putBoolean("createSubcontractorTimesheet", true);
            }
        }
        else
        {
            requestQueue.add(jor);
        }

        Intent intent = new Intent(AllSubcontractor.this, AllSubcontractor.class);
        startActivity(intent);

    }


    public void createNewSubcontractor()
    {
        final AlertDialog.Builder alert = new AlertDialog.Builder(AllSubcontractor.this,android.R.style.Theme_Translucent_NoTitleBar);
        // Set an EditText view to get user input

        dialogView = LayoutInflater.from(AllSubcontractor.this).inflate(R.layout.dialog_new_sub_timesheet, null);
        alert.setView(dialogView);

        show = alert.show();

        hiddenLayout = (LinearLayout) dialogView.findViewById(R.id.hiddenLayout);
        hiddenLayout.setVisibility(View.INVISIBLE);

        text_subcontractor = (HelveticaRegular) dialogView.findViewById(R.id.text_subcontractor);

        final Spinner spinner_subcontractor = (Spinner) dialogView.findViewById(R.id.spinner_subcontractor);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(dialogView.getContext(),
                android.R.layout.simple_dropdown_item_1line,
                new String[] {"Select Subcontractor", "SUB01", "SUB02", "SUB03", "SUB04", "SUB05"});

        spinner_subcontractor.setAdapter(adapter);

        spinner_subcontractor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position==0)
                {
                    hiddenLayout.setVisibility(View.INVISIBLE);
                }
                else if(position==1)
                {
                    text_subcontractor.setText("Subcontractor A");
                    hiddenLayout.setVisibility(View.VISIBLE);
                }
                else if(position==2)
                {
                    text_subcontractor.setText("Subcontractor B");
                    hiddenLayout.setVisibility(View.VISIBLE);
                }
                else if(position==3)
                {
                    text_subcontractor.setText("Subcontractor C");
                    hiddenLayout.setVisibility(View.VISIBLE);
                }
                else if(position==4)
                {
                    text_subcontractor.setText("Subcontractor D");
                    hiddenLayout.setVisibility(View.VISIBLE);
                }
                else if(position==5)
                {
                    text_subcontractor.setText("Subcontractor E");
                    hiddenLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Button createBtn = (Button) dialogView.findViewById(R.id.createBtn);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(spinner_subcontractor.getSelectedItem().toString().equals("Select Subcontractor"))
                {
                    Toast.makeText(AllSubcontractor.this, "Select Subcontractor First", Toast.LENGTH_SHORT).show();
                }

                else {
                    createSubcontractor(spinner_subcontractor.getSelectedItem().toString());
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AllSubcontractor.this, ViewPurchaseOrders.class);
        startActivity(intent);
    }

}