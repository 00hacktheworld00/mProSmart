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
import com.example.sadashivsinha.mprosmart.Adapters.MyAdapter;
import com.example.sadashivsinha.mprosmart.Adapters.PunchListsAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.MomList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.AppController;
import com.example.sadashivsinha.mprosmart.Utils.Communicator;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.example.sadashivsinha.mprosmart.Utils.CsvCreateUtility;
import com.example.sadashivsinha.mprosmart.font.HelveticaRegular;
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

public class AllPunchLists extends NewActivity implements View.OnClickListener  {
    private List<MomList> momList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PunchListsAdapter punchListsAdapter;
    JSONArray dataArray;
    JSONObject dataObject;
    String projectId, projectName, vendorId, vendorName, createdDate, createdBy, punchListId;
    ConnectionDetector cd;
    public static final String TAG = AllPunchLists.class.getSimpleName();
    Boolean isInternetPresent = false;
    MomList items;
    private ProgressDialog pDialog;
    String currentProjectNo, vendorNameText, currentUserId;
    View dialogView;
    AlertDialog show;
    Spinner spinner_vendor;
    String[] vendorIdArray, vendorNameArray;
    PreferenceManager pm;
    String url, vendor_url;
    ArrayAdapter<String> vendorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_punch_lists);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentUserId = pm.getString("userId");

        url = pm.getString("SERVER_URL") + "/getAllPunchLists?projectId='"+currentProjectNo+"'";
        vendor_url = pm.getString("SERVER_URL") + "/getVendors";

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        punchListsAdapter = new PunchListsAdapter(momList);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(AllPunchLists.this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(punchListsAdapter);

        // check for Internet status
        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(AllPunchLists.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(AllPunchLists.this);
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
                            projectId = dataObject.getString("projectId");
                            projectName = dataObject.getString("projectName");
                            vendorId = dataObject.getString("vendorId");
                            vendorName = dataObject.getString("vendorName");
                            createdDate = dataObject.getString("createdDate");
                            createdBy = dataObject.getString("createdBy");
                            punchListId = dataObject.getString("punchListId");

                            items = new MomList(String.valueOf(i+1), punchListId ,projectId, projectName, createdDate,
                                    vendorId, createdBy, vendorName);
                            momList.add(items);
                            punchListsAdapter.notifyDataSetChanged();
                            pDialog.dismiss();
                        }

                        Boolean createPunchList = pm.getBoolean("createPunchList");

                        if (createPunchList) {

                            String jsonObjectVal = pm.getString("objectPunchList");
                            Log.d("JSON subC PENDING :", jsonObjectVal);

                            JSONObject jsonObjectPending = new JSONObject(jsonObjectVal);
                            Log.d("JSONObj subC PENDING :", jsonObjectPending.toString());

                            projectId = dataObject.getString("projectId");
                            projectName = dataObject.getString("projectName");
                            vendorId = dataObject.getString("vendorId");
                            vendorName = dataObject.getString("vendorName");
                            createdDate = dataObject.getString("createdDate");
                            createdBy = dataObject.getString("createdBy");

                            items = new MomList(String.valueOf(dataArray.length()), getResources().getString(R.string.waiting_to_connect) ,projectId, projectName, createdDate,
                                    vendorId, createdBy, vendorName);
                            momList.add(items);
                            punchListsAdapter.notifyDataSetChanged();
                        }
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
                Toast.makeText(AllPunchLists.this, "Offline Data Not available for Punch Lists", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }

            entry = cache.get(vendor_url);
            if (entry != null) {
                //Cache data available.
                try {
                    String data = new String(entry.data, "UTF-8");
                    Log.d("CACHE DATA", data);
                    JSONObject jsonObject = new JSONObject(data);
                    try {
                        dataArray = jsonObject.getJSONArray("data");
                        vendorIdArray = new String[dataArray.length() + 1];
                        vendorNameArray = new String[dataArray.length() + 1];

                        vendorIdArray[0] = "Select Vendor";
                        vendorNameArray[0] = "Select Vendor";

                        for (int i = 0; i < dataArray.length(); i++) {
                            dataObject = dataArray.getJSONObject(i);
                            vendorId = dataObject.getString("vendorId");
                            vendorNameText = dataObject.getString("vendorName");
                            vendorIdArray[i + 1] = vendorId;
                            vendorNameArray[i + 1] = vendorNameText;
                        }
                        Log.d("VENDOR'S NAMES: ", vendorNameArray.toString());

                        vendorAdapter = new ArrayAdapter<String>(AllPunchLists.this,
                                android.R.layout.simple_dropdown_item_1line, vendorNameArray);

                        pDialog.dismiss();

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
                Toast.makeText(AllPunchLists.this, "Offline Data Not available for Punch Lists", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }

        else
        {
            // Cache data not exist.
            callJsonArrayRequest();
            getAllVendors();
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
        FloatingActionButton fab_add, fab_search,exportBtn;

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

            case R.id.exportBtn:
            {
                //csv export
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP) {
                    if (ContextCompat.checkSelfPermission(AllPunchLists.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(AllPunchLists.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                    }
                    Environment.getExternalStorageState();

                    String punch_list_no = null, project_id = null, project_name = null, date = null, vendor_id = null, vendor_name = null, created_by = null;
                    int listSize = momList.size();
                    String cvsValues = "Punch List No." + ","+ "Project ID" + ","+ "Project Name"  + ","+ "Vendor ID" + ","+ "Vendor Name" + ","+ "Date"  + ","+ "Created By" + "\n";

                    for(int i=0; i<listSize;i++)
                    {
                        MomList items = momList.get(i);
                        punch_list_no = items.getPunch_list_no();
                        project_id = items.getProject_id();
                        project_name = items.getProject_name();
                        date = items.getDate();
                        vendor_id = items.getVendor_id();
                        vendor_name = items.getVendor_name();
                        created_by = items.getCreated_by();

                        cvsValues = cvsValues +  punch_list_no + ","+ project_id + ","+ project_name +","+ date +","+ vendor_id +","+ vendor_name +","+ created_by + "\n";
                    }

                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "PunchList-data.csv", cvsValues);
                }

                else

                {
                Environment.getExternalStorageState();

                String punch_list_no = null, project_id = null, project_name = null, date = null, vendor_id = null, vendor_name = null, created_by = null;
                int listSize = momList.size();
                String cvsValues = "Punch List No." + ","+ "Project ID" + ","+ "Project Name"  + ","+ "Vendor ID" + ","+ "Vendor Name" + ","+ "Date"  + ","+ "Created By" + "\n";

                for(int i=0; i<listSize;i++)
                {
                    MomList items = momList.get(i);
                    punch_list_no = items.getPunch_list_no();
                    project_id = items.getProject_id();
                    project_name = items.getProject_name();
                    date = items.getDate();
                    vendor_id = items.getVendor_id();
                    vendor_name = items.getVendor_name();
                    created_by = items.getCreated_by();

                    cvsValues = cvsValues +  punch_list_no + ","+ project_id + ","+ project_name +","+ date +","+ vendor_id +","+ vendor_name +","+ created_by + "\n";
                }

                CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "PunchList-data.csv", cvsValues);
                }

            }
            break;
            case R.id.fab_add:
            {
//                Intent intent = new Intent(AllPunchLists.this, PunchListCreate.class);
//                startActivity(intent);

                final AlertDialog.Builder alert = new AlertDialog.Builder(AllPunchLists.this,android.R.style.Theme_Translucent_NoTitleBar);
                // Set an EditText view to get user input

                dialogView = LayoutInflater.from(AllPunchLists.this).inflate(R.layout.dialog_new_punch_list, null);
                alert.setView(dialogView);

                show = alert.show();

                spinner_vendor = (Spinner) dialogView.findViewById(R.id.spinner_vendor);

                spinner_vendor.setAdapter(vendorAdapter);

                Button createBtn = (Button) dialogView.findViewById(R.id.createBtn);

                createBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(spinner_vendor.getSelectedItem().toString().equals("Select Vendor"))
                        {
                            Toast.makeText(AllPunchLists.this, "Select Vendor First", Toast.LENGTH_SHORT).show();
                        }

                        else
                        {
                            pDialog = new ProgressDialog(dialogView.getContext());
                            pDialog.setMessage("Saving Data...");
                            pDialog.setIndeterminate(false);
                            pDialog.setCancelable(true);
                            pDialog.show();

                            for(int i=0; i<vendorNameArray.length;i++)
                            {
                                if(vendorNameArray[i] == spinner_vendor.getSelectedItem().toString())
                                    createPunchList(vendorIdArray[i]);
                            }

//                            createPunchList(spinner_vendor.getSelectedItem().toString());
                        }
                    }
                });
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
                        Toast.makeText(AllPunchLists.this, "Search for it .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(AllPunchLists.this, "Search cancelled .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
            }
            break;
        }
    }

    private void callJsonArrayRequest() {
        // TODO Auto-generated method stub

        pDialog = new ProgressDialog(AllPunchLists.this);
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

                                projectId = dataObject.getString("projectId");
                                projectName = dataObject.getString("projectName");
                                vendorId = dataObject.getString("vendorId");
                                vendorName = dataObject.getString("vendorName");
                                createdDate = dataObject.getString("createdDate");
                                createdBy = dataObject.getString("createdBy");
                                punchListId = dataObject.getString("punchListId");

                                items = new MomList(String.valueOf(i+1), punchListId ,projectId, projectName, createdDate,
                                        vendorId, createdBy, vendorName);
                                momList.add(items);
                                punchListsAdapter.notifyDataSetChanged();
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

    public void getAllVendors()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, vendor_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(AllPunchLists.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                vendorIdArray = new String[dataArray.length()+1];
                                vendorNameArray = new String[dataArray.length()+1];

                                vendorIdArray[0]="Select Vendor";
                                vendorNameArray[0]= "Select Vendor";
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    vendorId = dataObject.getString("vendorId");
                                    vendorNameText = dataObject.getString("vendorName");
                                    vendorIdArray[i+1]=vendorId;
                                    vendorNameArray[i+1]=vendorNameText;
                                }

                                vendorAdapter = new ArrayAdapter<String>(AllPunchLists.this,
                                        android.R.layout.simple_dropdown_item_1line,vendorNameArray);
                                pDialog.dismiss();

                            }
                        }catch(JSONException e){e.printStackTrace();}
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
        if(pDialog!=null)
            pDialog.dismiss();

        requestQueue.add(jor);
    }


    public void createPunchList(String vendorId)
    {
        JSONObject object = new JSONObject();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = sdf.format(c.getTime());

        try {
            object.put("projectId", currentProjectNo);
            object.put("vendorId",vendorId);
            object.put("createdBy",currentUserId);
            object.put("createdDate",strDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(AllPunchLists.this);

        String url = AllPunchLists.this.pm.getString("SERVER_URL") + "/postPunchLists";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if(response.getString("msg").equals("success"))
                            {
                                Toast.makeText(AllPunchLists.this, "Punchlist Created . ID - "+ response.getString("data"), Toast.LENGTH_SHORT).show();
                                pm.putString("punchListNo",response.getString("data"));
                                pDialog.dismiss();

                                Intent intent = new Intent(AllPunchLists.this, AllPunchLists.class);
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
                        Log.e("Volley","Error");
                        pDialog.dismiss();
                    }
                }
        );
        if (!isInternetPresent)
        {
            // Internet connection is not present

            Communicator communicator = new Communicator();
            Log.d("object", object.toString());

            Boolean createPunchList = pm.getBoolean("createPunchList");

            if(createPunchList)
            {
                Toast.makeText(AllPunchLists.this, "Already a Punch List creation is in progress. Please try after sometime.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(AllPunchLists.this, "Internet not currently available. Punch List will automatically get created on internet connection.", Toast.LENGTH_SHORT).show();

                pm.putString("objectPunchList", object.toString());
                pm.putString("urlPunchList", url);
                pm.putString("toastMessagePunchList", "Punch List Created");
                pm.putBoolean("createPunchList", true);

                Intent intent = new Intent(AllPunchLists.this, AllPunchLists.class);
                startActivity(intent);
            }
        }
        else
        {
            requestQueue.add(jor);
        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AllPunchLists.this, QualityControlMain.class);
        startActivity(intent);
    }

}