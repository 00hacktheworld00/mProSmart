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
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.sadashivsinha.mprosmart.Adapters.AllResourceAdapter;
import com.example.sadashivsinha.mprosmart.Adapters.MyAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.MomList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.AppController;
import com.example.sadashivsinha.mprosmart.Utils.Communicator;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.example.sadashivsinha.mprosmart.Utils.CsvCreateUtility;
import com.example.sadashivsinha.mprosmart.font.HelveticaBold;
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

public class AllResource extends NewActivity implements View.OnClickListener  {
    private List<MomList> momList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AllResourceAdapter allResourceAdapter;
    String flag = "false";
    String resourceTimesheetsId, name, photoUrl, createdBy, createdDate, projectId;
    JSONArray dataArray;
    JSONObject dataObject;
    ConnectionDetector cd;
    public static final String TAG = AllInvoices.class.getSimpleName();
    MomList items;
    private ProgressDialog pDialog;
    String currentProjectNo, currentUser, currentDate;
    Boolean isInternetPresent = false;
    View dialogView;
    AlertDialog show;
    String[] resourceNameArray, resourceIdArray;
    String firstName, lastName, fullName, id, resourceId;
    Spinner spinner_resource;
    PreferenceManager pm;
    String currentSelectedResource;

    String url, resource_url, searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_inovices);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getIntent().hasExtra("search")) {
            if (getIntent().getStringExtra("search").equals("yes")) {

                searchText = getIntent().getStringExtra("searchText");

                getSupportActionBar().setTitle("Resource Timesheet Search Results : " + searchText);
            }
        }


        pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentUser = pm.getString("userId");

        resource_url = pm.getString("SERVER_URL") + "/getResource";
        url = pm.getString("SERVER_URL") + "/getResourceTimesheets?projectId='"+currentProjectNo+"'";

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = sdf.format(c.getTime());

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();


        allResourceAdapter = new AllResourceAdapter(momList);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(AllResource.this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(allResourceAdapter);

        // check for Internet status
        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(AllResource.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(AllResource.this);
            pDialog.setMessage("Getting cache data");
            pDialog.show();

            Cache cache = AppController.getInstance().getRequestQueue().getCache();
            Cache.Entry entry = cache.get(resource_url);
            if (entry != null) {
                //Cache data available.
                try {
                    String data = new String(entry.data, "UTF-8");
                    Log.d("CACHE DATA", data);
                    JSONObject jsonObject = new JSONObject(data);
                    try {
                        dataArray = jsonObject.getJSONArray("data");
                        resourceNameArray = new String[dataArray.length()+1];
                        resourceIdArray = new String[dataArray.length()+1];

                        resourceIdArray[0]="Select Resource";
                        resourceNameArray[0]= "Select Resource";

                        for(int i=0; i<dataArray.length();i++) {
                            dataObject = dataArray.getJSONObject(i);
                            firstName = dataObject.getString("firstName");
                            lastName = dataObject.getString("lastName");

                            id = dataObject.getString("id");

                            fullName = firstName + " " + lastName;

                            resourceNameArray[i + 1] = fullName;
                            resourceIdArray[i + 1] = id;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


                if (pDialog != null)
                    pDialog.dismiss();
            }


            entry = cache.get(url);
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

                            if(projectId.equals(currentProjectNo))
                            {
                                resourceTimesheetsId = dataObject.getString("resourceTimesheetsId");
                                name = dataObject.getString("name");
                                photoUrl = dataObject.getString("photoUrl");
                                createdBy = dataObject.getString("createdBy");
                                createdDate = dataObject.getString("createdDate");

                                for(int j=0; j<resourceIdArray.length; j++)
                                {
                                    if(resourceIdArray[j].equals(name))
                                    {
                                        name = resourceNameArray[j];
                                    }
                                }

                                if (getIntent().hasExtra("search"))
                                {
                                    if (getIntent().getStringExtra("search").equals("yes")) {

                                        if (resourceTimesheetsId.toLowerCase().contains(searchText.toLowerCase()) || name.toLowerCase().contains(searchText.toLowerCase())) {

                                            items = new MomList(String.valueOf(i+1),resourceTimesheetsId, name, "", createdDate, createdBy);
                                            momList.add(items);

                                            allResourceAdapter.notifyDataSetChanged();

                                        }
                                    }
                                }
                                else
                                {
                                    items = new MomList(String.valueOf(i+1),resourceTimesheetsId, name, "", createdDate, createdBy);
                                    momList.add(items);

                                    allResourceAdapter.notifyDataSetChanged();

                                }
                                }
                            if (pDialog != null)
                                pDialog.dismiss();
                        }

                        Boolean createResourceTimesheet = pm.getBoolean("createResourceTimesheet");

                        if (createResourceTimesheet) {

                            String jsonObjectVal = pm.getString("objectResourceTimesheet");
                            Log.d("JSON Res PENDING :", jsonObjectVal);

                            JSONObject jsonObjectPending = new JSONObject(jsonObjectVal);
                            Log.d("JSONObj Res PENDING :", jsonObjectPending.toString());

                            name = jsonObjectPending.getString("name");
                            createdBy = jsonObjectPending.getString("createdBy");
                            createdDate = jsonObjectPending.getString("createdDate");

                            for(int j=0; j<resourceIdArray.length; j++)
                            {
                                if(resourceIdArray[j].equals(name))
                                {
                                    name = resourceNameArray[j];
                                }
                            }

                            items = new MomList(String.valueOf(dataArray.length()),getResources().getString(R.string.waiting_to_connect), name, "", createdDate, createdBy);
                            momList.add(items);

                            allResourceAdapter.notifyDataSetChanged();

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
                Toast.makeText(AllResource.this, "Offline Data Not available for Resource Timesheets", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }

        else
        {
            // Cache data not exist.
            getAllResources();
        }


        if(getIntent().hasExtra("create"))
        {
            if(getIntent().getStringExtra("create").equals("yes"))
            {
                generateDialogForCreation();
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

        fab_add.setLabelText("Add new Resource Timesheet");

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
                    if (ContextCompat.checkSelfPermission(AllResource.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(AllResource.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                    }
                    Environment.getExternalStorageState();

                    String res_id = null, res_name = null, sub_id = null, date = null, created_by = null;
                    int listSize = momList.size();
                    String cvsValues = "Resource ID" + ","+ "Resource Name" + ","+ "Employee ID" + ","+ "Date"+ ","+ "Created By" + "\n";

                    for(int i=0; i<listSize;i++)
                    {
                        MomList items = momList.get(i);
                        res_id = items.getMom_rec_no();
                        res_name = items.getProject_id();
                        sub_id = items.getProject_name();
                        date = items.getDate();
                        created_by = items.getCreated_by();

                        cvsValues = cvsValues +  res_id + ","+ res_name + ","+ sub_id +","+ date +","+ created_by + "\n";
                    }

                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "Resource-data.csv", cvsValues);
                }

                else

                {
                    Environment.getExternalStorageState();

                    String res_id = null, res_name = null, sub_id = null, date = null, created_by = null;
                    int listSize = momList.size();
                    String cvsValues = "Resource ID" + ","+ "Resource Name" + ","+ "Employee ID" + ","+ "Date"+ ","+ "Created By" + "\n";

                    for(int i=0; i<listSize;i++)
                    {
                        MomList items = momList.get(i);
                        res_id = items.getMom_rec_no();
                        res_name = items.getProject_id();
                        sub_id = items.getProject_name();
                        date = items.getDate();
                        created_by = items.getCreated_by();

                        cvsValues = cvsValues +  res_id + ","+ res_name + ","+ sub_id +","+ date +","+ created_by + "\n";
                    }

                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "Resource-data.csv", cvsValues);
                }

            }
            break;
            case R.id.fab_add:
            {
                generateDialogForCreation();
            }
            break;
            case R.id.fab_search:
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Search Resource Timesheets by Name or ID !");
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
                            Intent intent = new Intent(AllResource.this, AllResource.class);
                            intent.putExtra("search", "yes");
                            intent.putExtra("searchText", input.getText().toString());

                            Log.d("SEARCH TEXT", input.getText().toString());

                            startActivity(intent);
                        }
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(AllResource.this, "Search cancelled .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
            }
            break;
        }
    }

    public void generateDialogForCreation()
    {

//                Intent intent = new Intent(AllResource.this, ResourceTimesheetCreate.class);
//                startActivity(intent);
        final AlertDialog.Builder alert = new AlertDialog.Builder(AllResource.this,android.R.style.Theme_Translucent_NoTitleBar);
        // Set an EditText view to get user input

        dialogView = LayoutInflater.from(AllResource.this).inflate(R.layout.dialog_new_res_timesheet, null);
        alert.setView(dialogView);

        show = alert.show();

        final HelveticaBold text_res_id_title;
        final HelveticaRegular text_res_id;

        text_res_id_title = (HelveticaBold) dialogView.findViewById(R.id.text_res_id_title);

        text_res_id = (HelveticaRegular) dialogView.findViewById(R.id.text_res_id);

        text_res_id_title.setVisibility(View.INVISIBLE);
        text_res_id.setVisibility(View.INVISIBLE);

        spinner_resource = (Spinner) dialogView.findViewById(R.id.spinner_resource);

        Button createBtn = (Button) dialogView.findViewById(R.id.createBtn);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AllResource.this,
                android.R.layout.simple_dropdown_item_1line,resourceNameArray);
        spinner_resource.setAdapter(adapter);

        spinner_resource.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0)
                {
                    text_res_id_title.setVisibility(View.INVISIBLE);
                    text_res_id.setVisibility(View.INVISIBLE);
                }
                else
                {
                    currentSelectedResource = resourceIdArray[position];
                    text_res_id_title.setVisibility(View.VISIBLE);
                    text_res_id.setVisibility(View.VISIBLE);

                    text_res_id.setText(currentSelectedResource);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(spinner_resource.getSelectedItem().toString().equals("Select Resource"))
                {
                    Toast.makeText(AllResource.this, "Select Resource First", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    createResource(currentSelectedResource);
                }
            }
        });
    }

    private void callJsonArrayRequest() {
        // TODO Auto-generated method stub

        pDialog = new ProgressDialog(AllResource.this);
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
                                projectId = dataObject.getString("projectId");

                                if(projectId.equals(currentProjectNo))
                                {
                                    resourceTimesheetsId = dataObject.getString("resourceTimesheetsId");
                                    name = dataObject.getString("name");
                                    photoUrl = dataObject.getString("photoUrl");
                                    createdBy = dataObject.getString("createdBy");
                                    createdDate = dataObject.getString("createdDate");

                                    for(int j=0; j<resourceIdArray.length; j++)
                                    {
                                        if(resourceIdArray[j].equals(name))
                                        {
                                            name = resourceNameArray[j];
                                        }
                                    }

                                    if (getIntent().hasExtra("search"))
                                    {
                                        if (getIntent().getStringExtra("search").equals("yes")) {

                                            if (resourceTimesheetsId.toLowerCase().contains(searchText.toLowerCase()) || name.toLowerCase().contains(searchText.toLowerCase())) {

                                                items = new MomList(String.valueOf(i+1),resourceTimesheetsId, name, "", createdDate, createdBy);
                                                momList.add(items);

                                                allResourceAdapter.notifyDataSetChanged();

                                            }
                                        }
                                    }
                                    else
                                    {
                                        items = new MomList(String.valueOf(i+1),resourceTimesheetsId, name, "", createdDate, createdBy);
                                        momList.add(items);

                                        allResourceAdapter.notifyDataSetChanged();

                                    }
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

    private void getAllResources() {
        // TODO Auto-generated method stub

        pDialog = new ProgressDialog(AllResource.this);
        pDialog.setMessage("Getting server data");
        pDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, resource_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {
//                            dataObject = response.getJSONObject(0);
                            dataArray = response.getJSONArray("data");
                            resourceNameArray = new String[dataArray.length()+1];
                            resourceIdArray = new String[dataArray.length()+1];

                            resourceIdArray[0]="Select Resource";
                            resourceNameArray[0]= "Select Resource";

                            for(int i=0; i<dataArray.length();i++)
                            {
                                dataObject = dataArray.getJSONObject(i);
                                firstName = dataObject.getString("firstName");
                                lastName = dataObject.getString("lastName");

                                id = dataObject.getString("id");

                                fullName = firstName + " " + lastName;

                                resourceNameArray[i+1]=fullName;
                                resourceIdArray[i+1]=id;
                            }

                            callJsonArrayRequest();
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

    public void createResource(final String resourceId)
    {
        JSONObject object = new JSONObject();

        try {
            object.put("projectId",currentProjectNo);
            object.put("name", resourceId);
            object.put("photoUrl","0");
            object.put("createdBy",currentUser);
            object.put("createdDate",currentDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(AllResource.this);

        String url = AllResource.this.pm.getString("SERVER_URL") + "/postResourceTimesheet";
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if(response.getString("msg").equals("success"))
                            {
                                Toast.makeText(AllResource.this, "Resource Created . ID - "+ response.getString("data"), Toast.LENGTH_SHORT).show();
                                pm.putString("resourceId", response.getString("data"));
                                pDialog.dismiss();
                                show.dismiss();

                                pm.putString("resourceId", response.getString("data"));
                                pm.putString("resourceName", spinner_resource.getSelectedItem().toString());
                                pm.putString("resourceDate", currentDate);
                                pm.putString("resourceCreatedBy", currentUser);

                                Intent intent = new Intent(AllResource.this, AllResource.class);
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

            Boolean createResourceTimesheet = pm.getBoolean("createResourceTimesheet");

            if(createResourceTimesheet)
            {
                Toast.makeText(AllResource.this, "Already a Resource Timesheet creation is in progress. Please try after sometime.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(AllResource.this, "Internet not currently available. Resource Timesheet will automatically get created on internet connection.", Toast.LENGTH_SHORT).show();

                pm.putString("objectResourceTimesheet", object.toString());
                pm.putString("urlResourceTimesheet", url);
                pm.putString("toastMessageResourceTimesheet", "Resource Timesheet Created");
                pm.putBoolean("createResourceTimesheet", true);

                Intent intent = new Intent(AllResource.this, AllResource.class);
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
        Intent intent = new Intent(AllResource.this, SiteProjectDelivery.class);
        startActivity(intent);
    }

}