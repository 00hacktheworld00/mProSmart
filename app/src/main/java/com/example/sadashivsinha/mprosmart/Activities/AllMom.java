package com.example.sadashivsinha.mprosmart.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.Adapters.AllMomAdapter;
import com.example.sadashivsinha.mprosmart.Adapters.MyAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.AllBoqList;
import com.example.sadashivsinha.mprosmart.ModelLists.MomList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.AppController;
import com.example.sadashivsinha.mprosmart.Utils.Communicator;
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

public class AllMom extends NewActivity implements View.OnClickListener  {
    private List<MomList> momList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AllMomAdapter allMomAdapter;
    String flag = "false";
    MomList items;
    String currentProjectNo;
    JSONArray dataArray;
    JSONObject dataObject;
    String momId,createdBy, createDate, projectName;
    ConnectionDetector cd;
    public static final String TAG = PurchaseOrders.class.getSimpleName();
    Boolean isInternetPresent = false;
    private ProgressDialog pDialog;
    PreferenceManager pm;
    String url;
    String searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_mom);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getIntent().hasExtra("search")) {
            if (getIntent().getStringExtra("search").equals("yes")) {

                searchText = getIntent().getStringExtra("searchText");

                getSupportActionBar().setTitle("MOM Search Results : " + searchText);
            }
        }

        pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");

        allMomAdapter = new AllMomAdapter(momList);

        url = pm.getString("SERVER_URL") + "/getAllMom?projectId='"+currentProjectNo+"'";

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();


        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(AllMom.this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(allMomAdapter);

        // check for Internet status
        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(AllMom.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(AllMom.this);
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
                            momId = dataObject.getString("momId");
                            projectName = dataObject.getString("projectName");
                            createDate = dataObject.getString("createDate");
                            createdBy = dataObject.getString("createdBy");


                            Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(createDate);
                            createDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                            if (getIntent().hasExtra("search"))
                            {
                                if (getIntent().getStringExtra("search").equals("yes")) {

                                    if (momId.toLowerCase().contains(searchText.toLowerCase())) {

                                        items = new MomList(String.valueOf(i+1), momId, currentProjectNo, projectName, createDate, createdBy);
                                        momList.add(items);

                                        allMomAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                            else
                            {
                                items = new MomList(String.valueOf(i+1), momId, currentProjectNo, projectName, createDate, createdBy);
                                momList.add(items);

                                allMomAdapter.notifyDataSetChanged();
                            }
                            pDialog.dismiss();
                        }

                        Boolean createMomPending = pm.getBoolean("createMomPending");

                        if(createMomPending)
                        {

                            String jsonObjectVal = pm.getString("objectMom");
                            Log.d("JSON MOM PENDING :", jsonObjectVal);

                            JSONObject jsonObjectPending = new JSONObject(jsonObjectVal);
                            Log.d("JSONObj MOM PENDING :", jsonObjectPending.toString());

                            projectName = jsonObjectPending.getString("projectName");
                            createDate = jsonObjectPending.getString("createDate");
                            createdBy = jsonObjectPending.getString("createdBy");

                            Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(createDate);
                            createDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                            items = new MomList(String.valueOf(dataArray.length()+1),  getResources().getString(R.string.waiting_to_connect), currentProjectNo, projectName, createDate, createdBy);
                            momList.add(items);

                            allMomAdapter.notifyDataSetChanged();
                        }

                    } catch (ParseException | JSONException e) {
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
                Toast.makeText(AllMom.this, "Offline Data Not available for this MOM", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }

        else
        {
            // Cache data not exist.
            callJsonArrayRequest();
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

        if(getIntent().hasExtra("create"))
        {
            if(getIntent().getStringExtra("create").equals("YES"))
            {
                createNewMom();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add:
            {
                createNewMom();
            }
            break;

            case R.id.exportBtn:
            {
                //csv export
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP)
                {
                    if (ContextCompat.checkSelfPermission(AllMom.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED)
                    {

                        ActivityCompat.requestPermissions(AllMom.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                    }
                            Environment.getExternalStorageState();

                            String mom_rec_no = null, mom_proj_no = null, mom_proj_name = null, mom_date = null, mom_created_by = null;
                            int listSize = momList.size();
                            String cvsValues = "Record No." + ","+ "Project ID" + ","+ "Project Name" + ","+ "Date" + ","+ "Created By" + "\n";

                            for(int i=0; i<listSize;i++)
                            {
                                MomList items = momList.get(i);
                                mom_rec_no = items.getMom_rec_no();
                                mom_proj_no = items.getProject_id();
                                mom_proj_name = items.getProject_name();
                                mom_date = items.getDate();
                                mom_created_by = items.getCreated_by();

                                cvsValues = cvsValues +  mom_rec_no + ","+ mom_proj_no + ","+ mom_proj_name + ","+ mom_date+ ","+ mom_created_by + "\n";
                            }

                            CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "MOM-data.csv", cvsValues);
                    }

                else

                {
                    Environment.getExternalStorageState();

                    String mom_rec_no = null, mom_proj_no = null, mom_proj_name = null, mom_date = null, mom_created_by = null;
                    int listSize = momList.size();
                    String cvsValues = "Record No." + ","+ "Project ID" + ","+ "Project Name" + ","+ "Date" + ","+ "Created By" + "\n";

                    for(int i=0; i<listSize;i++)
                    {
                        MomList items = momList.get(i);
                        mom_rec_no = items.getMom_rec_no();
                        mom_proj_no = items.getProject_id();
                        mom_proj_name = items.getProject_name();
                        mom_date = items.getDate();
                        mom_created_by = items.getCreated_by();

                        cvsValues = cvsValues +  mom_rec_no + ","+ mom_proj_no + ","+ mom_proj_name + ","+ mom_date+ ","+ mom_created_by + "\n";
                    }

                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "MOM-data.csv", cvsValues);

                }

            }
            break;
            case R.id.fab_search:
            {

                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Search MOM by ID !");
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
                            Intent intent = new Intent(AllMom.this, AllMom.class);
                            intent.putExtra("search", "yes");
                            intent.putExtra("searchText", input.getText().toString());

                            Log.d("SEARCH TEXT", input.getText().toString());

                            startActivity(intent);
                        }
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(AllMom.this, "Search cancelled .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
            }
            break;
        }
    }

    private void callJsonArrayRequest() {
        // TODO Auto-generated method stub

        pDialog = new ProgressDialog(AllMom.this);
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
                                momId = dataObject.getString("momId");
                                projectName = dataObject.getString("projectName");
                                createDate = dataObject.getString("createDate");
                                createdBy = dataObject.getString("createdBy");


                                Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(createDate);
                                createDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                if (getIntent().hasExtra("search"))
                                {
                                    if (getIntent().getStringExtra("search").equals("yes")) {

                                        if (momId.toLowerCase().contains(searchText.toLowerCase())) {

                                            items = new MomList(String.valueOf(i+1), momId, currentProjectNo, projectName, createDate, createdBy);
                                            momList.add(items);

                                            allMomAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                                else
                                {
                                    items = new MomList(String.valueOf(i+1), momId, currentProjectNo, projectName, createDate, createdBy);
                                    momList.add(items);

                                    allMomAdapter.notifyDataSetChanged();
                                }
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
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

    public void saveNewMom(String projectId, String currentUserId, final Context context) {
        JSONObject object = new JSONObject();


        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(c.getTime());

        try {
            object.put("projectId", projectId);
            object.put("photoUrl", "0");
            object.put("dueDate", "0");
            object.put("createdBy", currentUserId);
            object.put("createDate", currentDate);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        String url = pm.getString("SERVER_URL") + "/postMom";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("msg").equals("success"))
                            {
                                Toast.makeText(context, "MOM Created. ID - " + response.getString("data"), Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(AllMom.this, AllMom.class);
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
                        Log.e("Volley", "Error");
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

            Boolean createMomPending = pm.getBoolean("createMomPending");

            if(createMomPending)
            {
                Toast.makeText(AllMom.this, "Already a MOM creation is in progress. Please try after sometime.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(AllMom.this, "Internet not currently available. MOM will automatically get created on internet connection.", Toast.LENGTH_SHORT).show();

                pm.putString("objectMom", object.toString());
                pm.putString("urlMom", url);
                pm.putString("toastMessageMom", "MOM Created");
                pm.putBoolean("createMomPending", true);
            }
        }
        else
        {
            requestQueue.add(jor);
        }
        if(pDialog!=null)
            pDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AllMom.this, SiteProjectDelivery.class);
        intent.putExtra("projectNo","1");
        startActivity(intent);
    }


    public void createNewMom()
    {
        final PreferenceManager pm = new PreferenceManager(getApplicationContext());

        currentProjectNo = pm.getString("projectId");
        final String currentUserId = pm.getString("userId");

        saveNewMom(currentProjectNo,currentUserId,getApplicationContext());
        }


    }