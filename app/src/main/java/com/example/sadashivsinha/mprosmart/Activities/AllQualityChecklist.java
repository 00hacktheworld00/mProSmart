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
import com.example.sadashivsinha.mprosmart.Adapters.AllQualityChecklistAdapter;
import com.example.sadashivsinha.mprosmart.Adapters.MyAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.AllQualityChecklistList;
import com.example.sadashivsinha.mprosmart.ModelLists.AllQualityStandardList;
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

public class AllQualityChecklist extends NewActivity implements View.OnClickListener {

    private List<AllQualityChecklistList> qualityList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AllQualityChecklistAdapter qualityAdapter;
    ProgressDialog pDialog;
    String currentProjectNo, currentProjectName, currentUserId, id, createdBy, createdDate;
    JSONArray dataArray;
    JSONObject dataObject;
    Boolean isInternetPresent = false;
    PreferenceManager pm;

    AllQualityChecklistList qualityItem;
    public static final String TAG = AllQualityChecklist.class.getSimpleName();
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_quality_checklist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentProjectName = pm.getString("projectName");
        currentUserId = pm.getString("userId");

        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        qualityAdapter = new AllQualityChecklistAdapter(qualityList);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(AllQualityChecklist.this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(qualityAdapter);

        url = pm.getString("SERVER_URL") + "/getQualityCheckList?projectId='"+currentProjectNo+"'";

        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_content);
            Crouton.cancelAllCroutons();
            Crouton.makeText(AllQualityChecklist.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(AllQualityChecklist.this);
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
                            id = dataObject.getString("id");
                            createdBy = dataObject.getString("createdBy");
                            createdDate = dataObject.getString("createdDate");

                            Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(createdDate);
                            createdDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                            qualityItem = new AllQualityChecklistList(String.valueOf(i + 1), id, currentProjectNo, currentProjectName,
                                    createdDate, createdBy);

                            qualityList.add(qualityItem);

                            qualityAdapter.notifyDataSetChanged();
                        }

                        pDialog.dismiss();

                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                    }
//                    Toast.makeText(getApplicationContext(), "Loading from cache.", Toast.LENGTH_SHORT).show();


                Boolean createQualityChecklistPending = pm.getBoolean("createQualityChecklistPending");

                if (createQualityChecklistPending) {
                    String jsonObjectVal = pm.getString("objectQualityChecklistLine");
                    Log.d("JSON QC PENDING :", jsonObjectVal);

                    JSONObject jsonObjectPending = new JSONObject(jsonObjectVal);
                    Log.d("JSONObj QC PENDING :", jsonObjectPending.toString());

                    createdBy = jsonObjectPending.getString("createdBy");
                    createdDate = jsonObjectPending.getString("createdDate");

                    Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(createdDate);
                    createdDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                    qualityItem = new AllQualityChecklistList(String.valueOf(dataArray.length() + 1), id + 1, currentProjectNo, currentProjectName,
                            createdDate, createdBy);

                    qualityList.add(qualityItem);

                    qualityAdapter.notifyDataSetChanged();

                    if (pDialog != null)
                        pDialog.dismiss();
                }
            }
            catch(UnsupportedEncodingException | JSONException | ParseException e){
                e.printStackTrace();
            }
        }

                else
            {
                Toast.makeText(AllQualityChecklist.this, "Offline Data Not available for Quality Checklist", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }
        else
        {
            // Cache data not exist.
            prepareItems();
        }

        FloatingActionButton fab_add, fab_search, exportBtn;

        fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        fab_search = (FloatingActionButton) findViewById(R.id.fab_search);
        exportBtn = (FloatingActionButton) findViewById(R.id.exportBtn);

        fab_add.setOnClickListener(this);
        fab_search.setOnClickListener(this);
        exportBtn.setOnClickListener(this);

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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add:
            {
//                Intent intent = new Intent(AllQualityChecklist.this, QualityChecklistCreate.class);
//                startActivity(intent);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder
                        .setMessage("Do you want to create new Quality Checklist ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        final String projectId = currentProjectNo;
                                        final Context mContext = getApplicationContext();
                                        saveNewQualityChecklist(projectId,currentUserId,mContext);

                                    }
                                })

                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
            break;
            case R.id.fab_search:
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Search Quality Checklist !");
                // Set an EditText view to get user input
                final EditText input = new EditText(this);
                alert.setView(input);
                alert.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(AllQualityChecklist.this, "Search for it .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(AllQualityChecklist.this, "Search cancelled .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
            }
            break;
            case R.id.exportBtn:
            {
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP) {
                    if (ContextCompat.checkSelfPermission(AllQualityChecklist.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(AllQualityChecklist.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                    }

                    String itemId = null, id = null, itemDescription = null, createdBy = null;
                    int listSize = qualityList.size();
                    String cvsValues = "ID" + ","+ "Item ID" + ","+ "Item Description" + ","+ "Created By\n";

                    for(int i=0; i<listSize;i++)
                    {
                        AllQualityChecklistList items = qualityList.get(i);
                        id = items.getQuality_sl_no();
                        itemId = items.getChecklist_no();
                        itemDescription = items.getProject_id();
                        createdBy = items.getCreated_by();

                        cvsValues = cvsValues +  id + ","+ itemId + ","+ itemDescription + ","+ createdBy + "\n";
                    }

                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "AllQualityChecklist-data.csv", cvsValues);
                }

                else

                {
                    Environment.getExternalStorageState();

                    String itemId = null, id = null, itemDescription = null, createdBy = null;
                    int listSize = qualityList.size();
                    String cvsValues = "ID" + ","+ "Item ID" + ","+ "Item Description" + ","+ "Created By\n";

                    for(int i=0; i<listSize;i++)
                    {
                        AllQualityChecklistList items = qualityList.get(i);
                        id = items.getQuality_sl_no();
                        itemId = items.getChecklist_no();
                        itemDescription = items.getProject_id();
                        createdBy = items.getCreated_by();

                        cvsValues = cvsValues +  id + ","+ itemId + ","+ itemDescription + ","+ createdBy + "\n";
                    }

                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "AllQualityChecklist-data.csv", cvsValues);

                }

            }
            break;
        }
    }

    public void saveNewQualityChecklist(String projectId, String currentUserId, final Context context)
    {
        JSONObject object = new JSONObject();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(c.getTime());

        try {
            object.put("projectId", currentProjectNo);
            object.put("projectName", currentProjectName);
            object.put("createdBy", currentUserId);
            object.put("createdDate", currentDate);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        String url = pm.getString("SERVER_URL") + "/postQualityCheckList";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("msg").equals("success"))
                            {
                                Toast.makeText(context, "Quality Checklist Created. ID - " + response.getString("data"), Toast.LENGTH_SHORT).show();

                                pDialog.dismiss();
                                pm.putString("currentQualityChecklist", response.getString("data"));
                                Intent intent = new Intent(AllQualityChecklist.this, QualityCheckListActivity.class);
                                startActivity(intent);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            pDialog.dismiss();
                        }
                        //response success message display
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                        pDialog.dismiss();
                    }
                }
        );
        if (!isInternetPresent)
        {
            // Internet connection is not present

            Communicator communicator = new Communicator();
            Log.d("object", object.toString());

            Boolean createQualityChecklistPending = pm.getBoolean("createQualityChecklistPending");

            if(createQualityChecklistPending)
            {
                Toast.makeText(AllQualityChecklist.this, "Already a Quality Checklist creation is in progress. Please try after sometime.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(AllQualityChecklist.this, "Internet not currently available. Quality Checklist will automatically get created on internet connection.", Toast.LENGTH_SHORT).show();

                pm.putString("objectQualityChecklistLine", object.toString());
                pm.putString("urlQualityChecklistLine", url);
                pm.putString("toastMessageQualityChecklist", "Quality Checklist Created");
                pm.putBoolean("createQualityChecklistPending", true);
            }


            if(pDialog!=null)
                pDialog.dismiss();

            Intent intent = new Intent(AllQualityChecklist.this, QualityCheckListActivity.class);
            startActivity(intent);
        }
        else
        {
            requestQueue.add(jor);
        }
    }

    public void prepareItems() {
        pDialog = new ProgressDialog(AllQualityChecklist.this);
        pDialog.setMessage("Getting server data");
        pDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
//                            dataObject = response.getJSONObject(0);
                            Log.d("RESPONSE JSON", response.toString());
                            dataArray = response.getJSONArray("data");

                            for (int i = 0; i < dataArray.length(); i++) {
                                dataObject = dataArray.getJSONObject(i);
                                id = dataObject.getString("id");
                                createdBy = dataObject.getString("createdBy");
                                createdDate = dataObject.getString("createdDate");

                                Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(createdDate);
                                createdDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                qualityItem = new AllQualityChecklistList(String.valueOf(i + 1), id, currentProjectNo, currentProjectName,
                                        createdDate, createdBy);

                                qualityList.add(qualityItem);

                                qualityAdapter.notifyDataSetChanged();
                            }
                            pDialog.dismiss();

                        } catch (JSONException | ParseException e) {
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

        if (pDialog != null)
            pDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AllQualityChecklist.this, QualityControlMain.class);
        startActivity(intent);
    }

}