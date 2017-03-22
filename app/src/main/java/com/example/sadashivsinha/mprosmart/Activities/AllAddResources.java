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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.sadashivsinha.mprosmart.Adapters.AllAddResourcesAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.AllAddResourcesList;
import com.example.sadashivsinha.mprosmart.ModelLists.MomList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.AppController;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.example.sadashivsinha.mprosmart.Utils.CsvCreateUtility;
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

public class AllAddResources extends AppCompatActivity implements View.OnClickListener {

    private List<AllAddResourcesList> list = new ArrayList<>();
    private RecyclerView recyclerView;
    private AllAddResourcesAdapter adapter;
    String currentProjectNo, currentProjectName, currentUser, currentDate;
    View dialogView;
    AlertDialog show;

    ConnectionDetector cd;
    public static final String TAG = NewAllProjects.class.getSimpleName();
    Boolean isInternetPresent = false;

    AllAddResourcesList items;
    PreferenceManager pm;
    ProgressDialog pDialog;
    JSONArray dataArray;
    JSONObject dataObject;
    String firstName, lastName, resourceTypeId, designationId, ratePerHour, currencyId, emailId, phone, houseNo ,streetName,
            locality, state, country, resId, subContractor;
    String url, searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_add_resources);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getIntent().hasExtra("search")) {
            if (getIntent().getStringExtra("search").equals("yes")) {

                searchText = getIntent().getStringExtra("searchText");

                getSupportActionBar().setTitle("MOM Search Results : " + searchText);
            }
        }

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        pm = new PreferenceManager(getApplicationContext());
        pm.putString("currentBudget", "approval");
        currentProjectNo = pm.getString("projectId");
        currentProjectName = pm.getString("projectName");
        currentUser = pm.getString("userId");

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = sdf.format(c.getTime());

        url = pm.getString("SERVER_URL") + "/getResource";

        adapter = new AllAddResourcesAdapter(list);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(AllAddResources.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(AllAddResources.this);
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
                            resId = dataObject.getString("id");
                            firstName = dataObject.getString("firstName");
                            lastName = dataObject.getString("lastName");
                            resourceTypeId = dataObject.getString("resourceTypeId");
                            designationId = dataObject.getString("designationId");
                            ratePerHour = dataObject.getString("ratePerHour");

                            currencyId = dataObject.getString("currencyId");

                            emailId = dataObject.getString("emailId");
                            phone = dataObject.getString("phone");
                            houseNo = dataObject.getString("houseNo");
                            streetName = dataObject.getString("streetName");
                            locality = dataObject.getString("locality");
                            state = dataObject.getString("state");
                            country = dataObject.getString("country");
                            subContractor = dataObject.getString("subContractor");

                            if (getIntent().hasExtra("search"))
                            {
                                if (getIntent().getStringExtra("search").equals("yes")) {

                                    if (resId.toLowerCase().contains(searchText.toLowerCase()) ||
                                            firstName.toLowerCase().contains(searchText.toLowerCase())||
                                            lastName.toLowerCase().contains(searchText.toLowerCase()))
                                    {
                                        items = new AllAddResourcesList(String.valueOf(i+1), resId, firstName+" "+lastName, resourceTypeId,
                                                subContractor, designationId, emailId, phone, ratePerHour, currencyId);
                                        list.add(items);

                                        adapter.notifyDataSetChanged();

                                    }
                                }
                            }
                            else
                            {
                                items = new AllAddResourcesList(String.valueOf(i+1), resId, firstName+" "+lastName, resourceTypeId,
                                        subContractor, designationId, emailId, phone, ratePerHour, currencyId);
                                list.add(items);

                                adapter.notifyDataSetChanged();

                            }

                            pDialog.dismiss();
                        }

                        Boolean createResourcePending = pm.getBoolean("createResourcePending");

                        if(createResourcePending)
                        {
                            String jsonObjectVal = pm.getString("objectResource");
                            Log.d("JSON RES PENDING :", jsonObjectVal);

                            JSONObject jsonObjectPending = new JSONObject(jsonObjectVal);
                            Log.d("JSONObj RESO PENDING :", jsonObjectPending.toString());

                            firstName = dataObject.getString("firstName");
                            lastName = dataObject.getString("lastName");
                            resourceTypeId = dataObject.getString("resourceTypeId");
                            designationId = dataObject.getString("designationId");
                            ratePerHour = dataObject.getString("ratePerHour");

                            currencyId = dataObject.getString("currencyId");

                            emailId = dataObject.getString("emailId");
                            phone = dataObject.getString("phone");
                            houseNo = dataObject.getString("houseNo");
                            streetName = dataObject.getString("streetName");
                            locality = dataObject.getString("locality");
                            state = dataObject.getString("state");
                            country = dataObject.getString("country");
                            subContractor = dataObject.getString("subContractor");

                            items = new AllAddResourcesList(String.valueOf(dataArray.length()+1), getResources().getString(R.string.waiting_to_connect), firstName+" "+lastName, resourceTypeId, subContractor, designationId, emailId,
                                    phone, ratePerHour, currencyId);
                            list.add(items);

                            adapter.notifyDataSetChanged();
                            pDialog.dismiss();
                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
//                    Toast.makeText(getApplicationContext(), "Loading from cache.", Toast.LENGTH_SHORT).show();
                }
                catch (UnsupportedEncodingException | JSONException e)
                {
                    e.printStackTrace();
                }
                if (pDialog != null)
                    pDialog.dismiss();
            }

            else
            {
                Toast.makeText(AllAddResources.this, "Offline Data Not available for Resources", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }

        else
        {
            // Cache data not exist.
            prepareItems();
        }


        FloatingActionButton fab_add, fab_search, fab_export;

        fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        fab_search = (FloatingActionButton) findViewById(R.id.fab_search);
        fab_export = (FloatingActionButton) findViewById(R.id.fab_export);

        fab_add.setOnClickListener(this);
        fab_search.setOnClickListener(this);
        fab_export.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add:
            {
                Intent intent = new Intent(AllAddResources.this, AddResourceActivity.class);
                startActivity(intent);
            }
            break;

            case R.id.fab_export :
            {
                //csv export
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP)
                {
                    if (ContextCompat.checkSelfPermission(AllAddResources.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED)
                    {

                        ActivityCompat.requestPermissions(AllAddResources.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                    }
                    Environment.getExternalStorageState();

                    String name = null, resourceTypeId = null, designationId = null, ratePerHour = null
                            , currencyId = null, emailId = null, phone = null, houseNo = null
                            , streetName = null, locality = null, state = null, country = null
                            , subContractor = null;

                    int listSize = list.size();
                    String cvsValues = "Name" + ","+ "Resource Type ID" + ","+ "Designation ID" + ","+ "Rate per hour" + ","+
                            "Currency ID" + ","+ "Email ID" + ","+ "Phone" + "," + ","+ "Subcontractor" + "\n";

                    for(int i=0; i<listSize;i++)
                    {
                        AllAddResourcesList items = list.get(i);
                        name = items.getRes_name();
                        resourceTypeId = items.getRes_name();
                        designationId = items.getDesignation();
                        ratePerHour = items.getRate_per_hour();
                        currencyId = items.getCurrency();
                        emailId = items.getEmail();
                        phone = items.getPhone();
                        subContractor = items.getSubContractor();

                        cvsValues = cvsValues + name+ ","+resourceTypeId+ ","+designationId+ ","+ratePerHour+ ","+currencyId
                                + ","+emailId+ ","+ phone+ ","+subContractor+ "\n";
                    }

                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "ResourceData-data.csv", cvsValues);
                }

                else

                {
                    String name = null, resourceTypeId = null, designationId = null, ratePerHour = null
                        , currencyId = null, emailId = null, phone = null, houseNo = null
                        , streetName = null, locality = null, state = null, country = null
                        , subContractor = null;
                    int listSize = list.size();
                    Environment.getExternalStorageState();

                    String cvsValues = "Name" + ","+ "Resource Type ID" + ","+ "Designation ID" + ","+ "Rate per hour" + ","+
                            "Currency ID" + ","+ "Email ID" + ","+ "Phone" + "," + ","+ "Subcontractor" + "\n";

                    for(int i=0; i<listSize;i++)
                    {
                        AllAddResourcesList items = list.get(i);
                        name = items.getRes_name();
                        resourceTypeId = items.getRes_name();
                        designationId = items.getDesignation();
                        ratePerHour = items.getRate_per_hour();
                        currencyId = items.getCurrency();
                        emailId = items.getEmail();
                        phone = items.getPhone();
                        subContractor = items.getSubContractor();

                        cvsValues = cvsValues + name+ ","+resourceTypeId+ ","+designationId+ ","+ratePerHour+ ","+currencyId
                                + ","+emailId+ ","+ phone+ ","+subContractor+ "\n";
                    }

                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "ResourceData-data.csv", cvsValues);

                }

            }
            break;

            case R.id.fab_search:
            {

                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Search Resources by ID !");
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
                            Intent intent = new Intent(AllAddResources.this, AllAddResources.class);
                            intent.putExtra("search", "yes");
                            intent.putExtra("searchText", input.getText().toString());

                            Log.d("SEARCH TEXT", input.getText().toString());

                            startActivity(intent);
                        }
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(AllAddResources.this, "Search cancelled .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
            }
            break;
        }
    }

    public void prepareItems()
    {
        pDialog = new ProgressDialog(AllAddResources.this);
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
                                resId = dataObject.getString("id");
                                firstName = dataObject.getString("firstName");
                                lastName = dataObject.getString("lastName");
                                resourceTypeId = dataObject.getString("resourceTypeId");
                                designationId = dataObject.getString("designationId");
                                ratePerHour = dataObject.getString("ratePerHour");

                                currencyId = dataObject.getString("currencyId");

                                emailId = dataObject.getString("emailId");
                                phone = dataObject.getString("phone");
                                houseNo = dataObject.getString("houseNo");
                                streetName = dataObject.getString("streetName");
                                locality = dataObject.getString("locality");
                                state = dataObject.getString("state");
                                country = dataObject.getString("country");
                                subContractor = dataObject.getString("subContractor");

                                if (getIntent().hasExtra("search"))
                                {
                                    if (getIntent().getStringExtra("search").equals("yes")) {

                                        if (resId.toLowerCase().contains(searchText.toLowerCase()) ||
                                                firstName.toLowerCase().contains(searchText.toLowerCase())||
                                                lastName.toLowerCase().contains(searchText.toLowerCase()))
                                        {
                                            items = new AllAddResourcesList(String.valueOf(i+1), resId, firstName+" "+lastName, resourceTypeId,
                                                    subContractor, designationId, emailId, phone, ratePerHour, currencyId);
                                            list.add(items);

                                            adapter.notifyDataSetChanged();

                                        }
                                    }
                                }
                                else
                                {
                                    items = new AllAddResourcesList(String.valueOf(i+1), resId, firstName+" "+lastName, resourceTypeId,
                                            subContractor, designationId, emailId, phone, ratePerHour, currencyId);
                                    list.add(items);

                                    adapter.notifyDataSetChanged();

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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AllAddResources.this, SiteProjectDelivery.class);
        startActivity(intent);
    }

}