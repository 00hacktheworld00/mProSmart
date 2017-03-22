package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.Adapters.AllProjectsAdapter;
import com.example.sadashivsinha.mprosmart.Adapters.MyAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.AllProjectsList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
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

import de.hdodenhof.circleimageview.CircleImageView;


public class AllProjectsActivity extends NewActivity implements View.OnClickListener  {

    List<AllProjectsList> modelList = new ArrayList<>();
    AllProjectsAdapter modelAdapter;
    private Toolbar toolbar;
    private CircleImageView circleView;
    String toolbar_name;
    JSONArray dataArray;
    JSONObject dataObject;
    String wbsId, wbsName, projectName, startDate, endDate, LastUpdatedTime, progress, notes;
    ConnectionDetector cd;
    public static final String TAG = AllProjectsActivity.class.getSimpleName();
    Boolean isInternetPresent = false;
    AllProjectsList items;
    ProgressDialog pDialog;
    String currentProjectNo;
    PreferenceManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_projects);
        circleView = (CircleImageView) findViewById(R.id.circleView);

        pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar_name = toolbar.getTransitionName();
        }

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            CoordinatorLayout main_content = (CoordinatorLayout) findViewById(R.id.main_content);
            Snackbar snackbar = Snackbar.make(main_content, getResources().getString(R.string.no_internet_error), Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        pDialog = new ProgressDialog(AllProjectsActivity.this);
        pDialog.setMessage("Getting Data ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.itemsRecyclerView);


        class MyTask extends AsyncTask<Void, Void, Void> {

            @Override protected void onPreExecute() {
                modelAdapter = new AllProjectsAdapter(modelList);
                recyclerView.setLayoutManager(new LinearLayoutManager(AllProjectsActivity.this));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(modelAdapter);
            }

            @Override
            protected Void doInBackground(Void... params) {
                prepareItems();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                modelAdapter.notifyDataSetChanged();
            }

        }

        new MyTask().execute();

        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View
        mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size
        mAdapter = new MyAdapter(TITLES, ICONS, NAME, EMAIL, PROFILE);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,header view name, header view email,
        // and header view profile picture
        mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView
        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager
        mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager
        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);        // Drawer object Assigned to the view
        mDrawerToggle = new ActionBarDrawerToggle(this, Drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {
        }; // Drawer Toggle Object Made
        Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State


        FloatingActionButton fab_add, fab_search;

        fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        fab_search = (FloatingActionButton) findViewById(R.id.fab_search);


        fab_add.setOnClickListener(this);
        fab_search.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void prepareItems()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = pm.getString("SERVER_URL") + "/getWbs?projectId='"+currentProjectNo+"'";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(AllProjectsActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    wbsId = dataObject.getString("wbsId");
                                    wbsName = dataObject.getString("wbsName");
                                    projectName = dataObject.getString("projectName");
                                    startDate = dataObject.getString("startDate");
                                    endDate = dataObject.getString("endDate");
                                    LastUpdatedTime = dataObject.getString("LastUpdatedTime");
                                    progress = dataObject.getString("progress");
                                    notes = dataObject.getString("notes");



                                    Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(startDate);
                                    startDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                    tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(endDate);
                                    endDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                    tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(LastUpdatedTime);
                                    LastUpdatedTime = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                    items = new AllProjectsList(i+1,startDate,endDate,"1",progress,
                                            projectName,"",R.drawable.abc,R.drawable.logo_one, LastUpdatedTime, wbsName, wbsId, notes);
                                    modelList.add(items);
                                    modelAdapter.notifyDataSetChanged();
                                }
                            }

                            pDialog.dismiss();

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add:
            {
                Intent intent = new Intent(AllProjectsActivity.this, WbsCreateActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.fab_search:
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Search WBS !");
                // Set an EditText view to get user input
                final EditText input = new EditText(this);
                alert.setView(input);
                alert.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(AllProjectsActivity.this, "Search for it .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(AllProjectsActivity.this, "Search cancelled .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
            }
            break;
        }
    }

    @Override
    public void onBackPressed() {
        final Intent intent = new Intent(AllProjectsActivity.this, ViewPurchaseOrders.class);
        startActivity(intent);
    }

}