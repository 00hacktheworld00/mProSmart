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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.Adapters.AllResourceAdapter;
import com.example.sadashivsinha.mprosmart.Adapters.MyAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.MomList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.example.sadashivsinha.mprosmart.Utils.CsvCreateUtility;
import com.example.sadashivsinha.mprosmart.font.HelveticaBold;
import com.example.sadashivsinha.mprosmart.font.HelveticaRegular;
import com.github.clans.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
    String firstName, lastName, fullName, id;
    Spinner spinner_resource;
    PreferenceManager pm;
    String currentSelectedResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_inovices);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentUser = pm.getString("userId");

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = sdf.format(c.getTime());

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
        pDialog = new ProgressDialog(AllResource.this);
        pDialog.setMessage("Getting Data ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        class MyTask extends AsyncTask<Void, Void, Void>
        {
            @Override protected void onPreExecute()
            {
                allResourceAdapter = new AllResourceAdapter(momList);
                recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                recyclerView.setLayoutManager(new LinearLayoutManager(AllResource.this));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(allResourceAdapter);
            }

            @Override
            protected Void doInBackground(Void... params)
            {
                prepareItems();
                return null;
            }

            @Override protected void onPostExecute(Void result)
            {
                allResourceAdapter.notifyDataSetChanged();
            }

        }

        new MyTask().execute();

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
                alert.setTitle("Search Resource Timesheet !");
                // Set an EditText view to get user input
                final EditText input = new EditText(this);
                alert.setView(input);
                alert.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(AllResource.this, "Search for it .", Toast.LENGTH_SHORT).show();
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

        pDialog = new ProgressDialog(dialogView.getContext());
        pDialog.setMessage("Getting Resources...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        class MyTask extends AsyncTask<Void, Void, Void>
        {
            @Override
            protected Void doInBackground(Void... params)
            {
                getAllResources();
                return null;
            }
        }

        new MyTask().execute();

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

    public void prepareItems()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = getResources().getString(R.string.server_url) + "/getResourceTimesheets?projectId='"+currentProjectNo+"'";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(AllResource.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
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

                                        items = new MomList(String.valueOf(i+1),resourceTimesheetsId, name, "", createdDate, createdBy);
                                        momList.add(items);

                                        allResourceAdapter.notifyDataSetChanged();
                                    }
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

        String url = AllResource.this.getResources().getString(R.string.server_url) + "/postResourceTimesheet";
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

                                Intent intent = new Intent(AllResource.this, ResourceItemCreate.class);
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
        requestQueue.add(jor);
    }

    public void getAllResources()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = getResources().getString(R.string.server_url) + "/getResource";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{
                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(AllResource.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
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
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(AllResource.this,
                                        android.R.layout.simple_dropdown_item_1line,resourceNameArray);
                                spinner_resource.setAdapter(adapter);
                            }

                        }
                        catch(JSONException e){
                            e.printStackTrace();}
                        pDialog.dismiss();
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
        if (pDialog!=null)
            pDialog.dismiss();

        requestQueue.add(jor);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AllResource.this, ViewPurchaseOrders.class);
        startActivity(intent);
    }

}