package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
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
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.sadashivsinha.mprosmart.Adapters.MyAdapter;
import com.example.sadashivsinha.mprosmart.Adapters.NewAllProjectsAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.NewAllProjectList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.AppController;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.github.clans.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class NewAllProjects extends NewActivity {

    List<NewAllProjectList> modelList = new ArrayList<>();
    NewAllProjectsAdapter modelAdapter;
    private Toolbar toolbar;
    private CircleImageView circleView;
    String toolbar_name;
    JSONArray dataArray;
    JSONObject dataObject;
    String addressLine1, addressLine2, city, state, country, pin;
    String projectId, projectName, projectDesc, createdBy, createdDate, imageUrl, budget, currency, approved;
    NewAllProjectList items;
    ConnectionDetector cd;
    public static final String TAG = NewAllProjects.class.getSimpleName();
    Boolean isInternetPresent = false;
    ProgressDialog pDialog;
    Bitmap bmp;
    URL picUrl;
    Drawable mDrawable;
    Snackbar dataSnackbar;
    String currentUserId, currentCompanyId;
    FloatingActionButton fab_add, fab_search;
    PreferenceManager pm;
    String url;
    String searchText;
    Boolean search = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_all_projects);
        circleView = (CircleImageView) findViewById(R.id.circleView);

        pm = new PreferenceManager(this);
        currentCompanyId = pm.getString("companyId");
        currentUserId = pm.getString("userId");

        url = getResources().getString(R.string.server_url) + "/getProjects?companyId=\""+currentCompanyId+"\"";

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        fab_search = (FloatingActionButton) findViewById(R.id.fab_search);

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewAllProjects.this, CreateNewAllProject.class);
                startActivity(intent);
            }
        });


        fab_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(NewAllProjects.this);
                alert.setTitle("Search Project by Project Name or ID !");
                // Set an EditText view to get user input
                final EditText input = new EditText(NewAllProjects.this);
                input.setMaxLines(1);
                input.setImeOptions(EditorInfo.IME_ACTION_DONE);
                alert.setView(input);
                alert.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        if(input.getText().toString().isEmpty())
                        {
                            input.setError("Enter Search Field");
                        }
                        else
                        {
                            Intent intent = new Intent(NewAllProjects.this, NewAllProjects.class);
                            intent.putExtra("search", "yes");
                            intent.putExtra("searchText", input.getText().toString());
                            startActivity(intent);
                        }
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(NewAllProjects.this, "Search cancelled .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar_name = toolbar.getTransitionName();

        }

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.itemsRecyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(NewAllProjects.this));
        recyclerView.setHasFixedSize(true);
        modelAdapter = new NewAllProjectsAdapter(modelList);
        recyclerView.setAdapter(modelAdapter);


        if(getIntent().hasExtra("search"))
        {
            if (getIntent().getStringExtra("search").equals("yes"))
            {
                search = true;
                //searched values

                searchText = getIntent().getStringExtra("searchText");

                getSupportActionBar().setTitle("Project Search Results : " + searchText);
            }
        }


        // check for Internet status
        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(NewAllProjects.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(NewAllProjects.this);
            pDialog.setMessage("Getting cache data");
            pDialog.show();

            Cache cache = AppController.getInstance().getRequestQueue().getCache();
            Cache.Entry entry = cache.get(url);
            if(entry != null){
                //Cache data available.
                try {
                    String data = new String(entry.data, "UTF-8");
                    Log.d("CACHE DATA", data);
                    JSONObject jsonObject=new JSONObject(data);
                    try
                    {
                        dataArray = jsonObject.getJSONArray("data");
                        for(int i=0; i<dataArray.length();i++)
                        {
                            dataObject = dataArray.getJSONObject(i);
                            projectId = dataObject.getString("projectId");
                            projectName = dataObject.getString("projectName");
                            projectDesc = dataObject.getString("projectDescription");
                            createdBy = dataObject.getString("createdBy");
                            createdDate = dataObject.getString("createddate");
                            imageUrl = dataObject.getString("photoUrl");
                            addressLine1 = dataObject.getString("addressLine1");
                            addressLine2 = dataObject.getString("addressLine2");
                            city = dataObject.getString("city");
                            state = dataObject.getString("state");
                            country = dataObject.getString("country");
                            pin = dataObject.getString("pin");
                            budget = dataObject.getString("totalBudget");
                            currency = dataObject.getString("currencyCode");
                            approved = dataObject.getString("approved");

                            Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(createdDate);
                            createdDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                           if(search)
                           {
                               if(projectName.toLowerCase().contains(searchText.toLowerCase()) || projectId.toLowerCase().contains(searchText.toLowerCase()))
                               {
                                   items = new NewAllProjectList(projectName, projectId, projectDesc,createdBy,createdDate, imageUrl, addressLine1,
                                           addressLine2, city, state, pin, country, budget, currency, approved);
                                   modelList.add(items);

                                   modelAdapter.notifyDataSetChanged();
                               }
                           }
                           else
                           {
                               items = new NewAllProjectList(projectName, projectId, projectDesc,createdBy,createdDate, imageUrl, addressLine1,
                                       addressLine2, city, state, pin, country, budget, currency, approved);
                               modelList.add(items);

                               modelAdapter.notifyDataSetChanged();
                           }
                            pDialog.dismiss();
                        }

                        if(search)
                        {
                            if(modelList.size()==0)
                            {
                                Toast.makeText(NewAllProjects.this, "Search didn't match any data", Toast.LENGTH_SHORT).show();
                            }
                        }

                        Boolean createProjectPending = pm.getBoolean("createProjectPending");
                        Log.d("createProject Pending :", createProjectPending.toString());

                        if(createProjectPending) {
                            //if is in offline mode and data creation is pending, show the data in the list

                            String jsonObjectVal = pm.getString("objectProject");
                            Log.d("JSON Project PENDING :", jsonObjectVal);

                            JSONObject jsonObjectPending = new JSONObject(jsonObjectVal);
                            Log.d("JSONObj Project :", jsonObjectPending.toString());

                            projectName = jsonObjectPending.getString("projectName");
                            projectDesc = jsonObjectPending.getString("projectDescription");
                            createdBy = jsonObjectPending.getString("createdBy");
                            createdDate = jsonObjectPending.getString("createdDate");
                            imageUrl = jsonObjectPending.getString("photoUrl");
                            addressLine1 = jsonObjectPending.getString("addressLine1");
                            addressLine2 = jsonObjectPending.getString("addressLine2");
                            city = jsonObjectPending.getString("city");
                            state = jsonObjectPending.getString("state");
                            country = jsonObjectPending.getString("country");
                            pin = jsonObjectPending.getString("pin");
                            budget = jsonObjectPending.getString("totalBudget");
                            currency = jsonObjectPending.getString("currencyCode");

                            Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(createdDate);
                            createdDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);


                            items = new NewAllProjectList(projectName, getResources().getString(R.string.waiting_to_connect) , projectDesc ,createdBy ,createdDate , imageUrl, addressLine1,
                                    addressLine2, city, state, pin, country, budget, currency, "1");
                            modelList.add(items);

                            modelAdapter.notifyDataSetChanged();

                        }

                    } catch (ParseException | JSONException e) {
                        e.printStackTrace();
                    }
//                    Toast.makeText(getApplicationContext(), "Loading from cache.", Toast.LENGTH_SHORT).show();
                }
                catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }
                if(pDialog!=null)
                    pDialog.dismiss();
            }
        }

        else
        {
            // Cache data not exist.
            callJsonArrayRequest();
        }

//        class MyTask extends AsyncTask<Void, Void, Void>
//        {
//            @Override
//            protected void onPreExecute()
//            {
//                pDialog = new ProgressDialog(NewAllProjects.this);
//                pDialog.setMessage("Getting Data ...");
//                pDialog.setIndeterminate(false);
//                pDialog.setCancelable(true);
//                pDialog.show();
//                modelAdapter = new NewAllProjectsAdapter(modelList);
//                recyclerView.setAdapter(modelAdapter);
//            }
//
//            @Override
//            protected Void doInBackground(Void... params)
//            {
//                prepareItems();
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void result)
//            {
//                modelAdapter.notifyDataSetChanged();
//            }
//
//        }
//
//        new MyTask().execute();

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
        }; // Drawer Toggle Object Made
        Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State
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

    private void callJsonArrayRequest() {
        // TODO Auto-generated method stub

        pDialog = new ProgressDialog(NewAllProjects.this);
        pDialog.setMessage("Getting server data");
        pDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response project : ", response.toString());
                        try
                        {
//                            dataObject = response.getJSONObject(0);
                            dataArray = response.getJSONArray("data");
                            for(int i=0; i<dataArray.length();i++)
                            {
                                dataObject = dataArray.getJSONObject(i);
                                projectId = dataObject.getString("projectId");
                                projectName = dataObject.getString("projectName");
                                projectDesc = dataObject.getString("projectDescription");
                                createdBy = dataObject.getString("createdBy");
                                createdDate = dataObject.getString("createddate");
                                imageUrl = dataObject.getString("photoUrl");
                                addressLine1 = dataObject.getString("addressLine1");
                                addressLine2 = dataObject.getString("addressLine2");
                                city = dataObject.getString("city");
                                state = dataObject.getString("state");
                                country = dataObject.getString("country");
                                pin = dataObject.getString("pin");
                                budget = dataObject.getString("totalBudget");
                                currency = dataObject.getString("currencyCode");
                                approved = dataObject.getString("approved");

                                Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(createdDate);
                                createdDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                if(search)
                                {
                                    if(projectName.toLowerCase().contains(searchText.toLowerCase()) || projectId.toLowerCase().contains(searchText.toLowerCase()))
                                    {
                                        items = new NewAllProjectList(projectName, projectId, projectDesc,createdBy,createdDate, imageUrl, addressLine1,
                                                addressLine2, city, state, pin, country, budget, currency, approved);
                                        modelList.add(items);

                                        modelAdapter.notifyDataSetChanged();
                                    }
                                }
                                else
                                {
                                    items = new NewAllProjectList(projectName, projectId, projectDesc,createdBy,createdDate, imageUrl, addressLine1,
                                            addressLine2, city, state, pin, country, budget, currency, approved);
                                    modelList.add(items);

                                    modelAdapter.notifyDataSetChanged();
                                    pDialog.dismiss();
                                }
                            }

                            if(search)
                            {
                                if(modelList.size()==0)
                                {
                                    Toast.makeText(NewAllProjects.this, "Search didn't match any data", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (ParseException | JSONException e) {
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

    @Override
    public void onBackPressed() {
        final Intent intent = new Intent(NewAllProjects.this, WelcomeActivity.class);
        startActivity(intent);
    }
}