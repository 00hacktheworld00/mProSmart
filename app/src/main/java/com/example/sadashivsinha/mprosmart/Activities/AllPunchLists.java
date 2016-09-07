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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.Adapters.MyAdapter;
import com.example.sadashivsinha.mprosmart.Adapters.PunchListsAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.MomList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.example.sadashivsinha.mprosmart.Utils.CsvCreateUtility;
import com.example.sadashivsinha.mprosmart.font.HelveticaRegular;
import com.github.clans.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
    LinearLayout hiddenLayout;
    Spinner spinner_vendor;
    HelveticaRegular text_vendor;
    String[] vendorIdArray, vendorNameArray;
    PreferenceManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_punch_lists);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentUserId = pm.getString("userId");

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
        pDialog = new ProgressDialog(AllPunchLists.this);
        pDialog.setMessage("Getting Data ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        class MyTask extends AsyncTask<Void, Void, Void>
        {
            @Override protected void onPreExecute()
            {
                punchListsAdapter = new PunchListsAdapter(momList);
                recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                recyclerView.setLayoutManager(new LinearLayoutManager(AllPunchLists.this));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(punchListsAdapter);

            }

            @Override
            protected Void doInBackground(Void... params)
            {
                prepareItems();
                return null;
            }

            @Override protected void onPostExecute(Void result)
            {
                punchListsAdapter.notifyDataSetChanged();
            }

        }

        new MyTask().execute();

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

                hiddenLayout = (LinearLayout) dialogView.findViewById(R.id.hiddenLayout);
                hiddenLayout.setVisibility(View.INVISIBLE);

                text_vendor = (HelveticaRegular) dialogView.findViewById(R.id.text_vendor);

                Button createBtn = (Button) dialogView.findViewById(R.id.createBtn);

                pDialog = new ProgressDialog(dialogView.getContext());
                pDialog.setMessage("Getting Vendors...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();

                class MyTask extends AsyncTask<Void, Void, Void>
                {
                    @Override
                    protected Void doInBackground(Void... params)
                    {
                        getAllVendors();
                        return null;
                    }
                }

                new MyTask().execute();

                spinner_vendor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(position==0)
                        {
                            hiddenLayout.setVisibility(View.INVISIBLE);
                        }
                        else
                        {
                            hiddenLayout.setVisibility(View.VISIBLE);
                            text_vendor.setText(vendorNameArray[position]);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
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
                            createPunchList(spinner_vendor.getSelectedItem().toString());
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

    public void prepareItems()
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
                                Toast.makeText(AllPunchLists.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
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

    public void getAllVendors()
    {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = getResources().getString(R.string.server_url) + "/getVendors";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
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
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(AllPunchLists.this,
                                        android.R.layout.simple_dropdown_item_1line,vendorIdArray);
                                spinner_vendor.setAdapter(adapter);
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

        String url = AllPunchLists.this.getResources().getString(R.string.server_url) + "/postPunchLists";

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
                                Intent intent = new Intent(AllPunchLists.this, PunchItemCreate.class);
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
        if(pDialog!=null)
            pDialog.dismiss();

        requestQueue.add(jor);
        Intent intent = new Intent(AllPunchLists.this, AllPunchLists.class);
        startActivity(intent);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AllPunchLists.this, ViewPurchaseOrders.class);
        intent.putExtra("projectNo","1");
        startActivity(intent);
    }

}