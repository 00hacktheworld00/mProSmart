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
import com.example.sadashivsinha.mprosmart.Adapters.AllVendorAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.AllChangeOrdersList;
import com.example.sadashivsinha.mprosmart.ModelLists.AllVendorList;
import com.example.sadashivsinha.mprosmart.ModelLists.WbsList;
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

public class AllVendors extends AppCompatActivity implements View.OnClickListener {

    private List<AllVendorList> vendorList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AllVendorAdapter vendorAdapter;
    String currentProjectNo, currentProjectName, currentUser, currentDate;
    View dialogView;
    AlertDialog show;
    ProgressDialog pDialog;
    JSONArray dataArray;
    JSONObject dataObject;
    String vendorId, vendorName, vendorTypeId, decipline, taxID, license, companyName;
    public static final String TAG = AllVendors.class.getSimpleName();

    AllVendorList items;
    PreferenceManager pm;
    String[] vendorTypeIdArray, vendorTypeArray;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    String vendorTypeUrl, mainUrl;
    String searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_vendors);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getIntent().hasExtra("search")) {
            if (getIntent().getStringExtra("search").equals("yes")) {

                searchText = getIntent().getStringExtra("searchText");

                getSupportActionBar().setTitle("Vendors Search Results : " + searchText);
            }
        }

        pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentProjectName = pm.getString("projectName");
        currentUser = pm.getString("userId");

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = sdf.format(c.getTime());

        vendorTypeUrl = pm.getString("SERVER_URL") + "/getVendorType";
        mainUrl = pm.getString("SERVER_URL") + "/getVendors";

        vendorAdapter = new AllVendorAdapter(vendorList);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(vendorAdapter);

        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(AllVendors.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(AllVendors.this);
            pDialog.setMessage("Getting cache data");
            pDialog.show();

            Cache cache = AppController.getInstance().getRequestQueue().getCache();
            Cache.Entry entry = cache.get(vendorTypeUrl);
            if (entry != null) {
                //Cache data available.
                try {
                    String data = new String(entry.data, "UTF-8");
                    Log.d("CACHE DATA", data);
                    JSONObject jsonObject = new JSONObject(data);
                    try {
                        dataArray = jsonObject.getJSONArray("data");
                        vendorTypeIdArray = new String[dataArray.length()+1];
                        vendorTypeArray = new String[dataArray.length()+1];

                        vendorTypeIdArray[0]="Select Vendor Type";
                        vendorTypeArray[0]="Select Vendor Type";

                        for(int i=0; i<dataArray.length();i++)
                        {
                            dataObject = dataArray.getJSONObject(i);
                            vendorTypeIdArray[i+1] = dataObject.getString("id");
                            vendorTypeArray[i+1] = dataObject.getString("type");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    Toast.makeText(getApplicationContext(), "Loading from cache.", Toast.LENGTH_SHORT).show();
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }
                    entry = cache.get(mainUrl);
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

                                    vendorId = dataObject.getString("vendorId");
                                    vendorName = dataObject.getString("vendorName");
                                    vendorTypeId = dataObject.getString("vendorTypeId");
                                    decipline = dataObject.getString("decipline");
                                    taxID = dataObject.getString("taxID");
                                    license = dataObject.getString("license");
                                    companyName = dataObject.getString("companyName");

                                    for(int j=0; j<vendorTypeArray.length; j++)
                                    {
                                        if(vendorTypeId.equals(vendorTypeIdArray[j]))
                                            vendorTypeId = vendorTypeArray[j];
                                    }

                                    switch (decipline) {
                                        case "1":
                                            decipline = "Electrical";
                                            break;
                                        case "2":
                                            decipline = "Mechanical";
                                            break;
                                        case "3":
                                            decipline = "Civil";
                                            break;
                                        case "4":
                                            decipline = "Architectural";
                                            break;
                                    }

                                    if (getIntent().hasExtra("search"))
                                    {
                                        if (getIntent().getStringExtra("search").equals("yes")) {

                                            if (vendorName.toLowerCase().contains(searchText.toLowerCase()) || vendorId.toLowerCase().contains(searchText.toLowerCase())) {

                                                items = new AllVendorList(String.valueOf(i+1), vendorId, vendorName, vendorTypeId, decipline,
                                                        taxID, license, companyName);
                                                vendorList.add(items);

                                                vendorAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                    else
                                    {
                                        items = new AllVendorList(String.valueOf(i+1), vendorId, vendorName, vendorTypeId, decipline,
                                                taxID, license, companyName);
                                        vendorList.add(items);

                                        vendorAdapter.notifyDataSetChanged();
                                    }
                                }

                                Boolean createVendorPending = pm.getBoolean("createVendorPending");

                                if(createVendorPending) {

                                    String jsonObjectVal = pm.getString("objectVendor");
                                    Log.d("JSON BOQLINE PENDING :", jsonObjectVal);

                                    JSONObject jsonObjectPending = new JSONObject(jsonObjectVal);
                                    Log.d("JSONObj BOQIT PENDING :", jsonObjectPending.toString());

                                    vendorName = jsonObjectPending.getString("vendorName");
                                    vendorTypeId = jsonObjectPending.getString("vendorTypeId");
                                    decipline = jsonObjectPending.getString("decipline");
                                    taxID = jsonObjectPending.getString("taxID");
                                    license = jsonObjectPending.getString("license");
                                    companyName = jsonObjectPending.getString("companyName");

                                    for(int j=0; j<vendorTypeArray.length; j++)
                                    {
                                        if(vendorTypeId.equals(vendorTypeIdArray[j]))
                                            vendorTypeId = vendorTypeArray[j];
                                    }

                                    switch (decipline) {
                                        case "1":
                                            decipline = "Electrical";
                                            break;
                                        case "2":
                                            decipline = "Mechanical";
                                            break;
                                        case "3":
                                            decipline = "Civil";
                                            break;
                                        case "4":
                                            decipline = "Architectural";
                                            break;
                                    }

                                    items = new AllVendorList(String.valueOf(dataArray.length()+1), getResources().getString(R.string.waiting_to_connect), vendorName, vendorTypeId, decipline,
                                            taxID, license, companyName);
                                    vendorList.add(items);

                                    vendorAdapter.notifyDataSetChanged();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
//                    Toast.makeText(getApplicationContext(), "Loading from cache.", Toast.LENGTH_SHORT).show();
                        } catch (UnsupportedEncodingException | JSONException e) {
                            e.printStackTrace();
                        }
                        if (pDialog != null)
                            pDialog.dismiss();
                    }

            }
            else
            {
                Toast.makeText(AllVendors.this, "Offline Daily Progress Report Not available for this Date", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }

        else
        {
            // Cache data not exist.
            getVendorType();
        }

        FloatingActionButton fab_add, fab_search, exportBtn;

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
            case R.id.fab_add: {
                Intent intent = new Intent(AllVendors.this, AddVendorsActivity.class);
                startActivity(intent);
            }
            break;

            case R.id.fab_search: {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Search Vendor by Vendor Name or ID !");
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
                            Intent intent = new Intent(AllVendors.this, AllVendors.class);
                            intent.putExtra("search", "yes");
                            intent.putExtra("searchText", input.getText().toString());

                            Log.d("SEARCH TEXT", input.getText().toString());

                            startActivity(intent);
                        }
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(AllVendors.this, "Search cancelled .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
            }
            break;

            case R.id.exportBtn: {
                //csv export
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP) {
                    if (ContextCompat.checkSelfPermission(AllVendors.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(AllVendors.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                    }
                    Environment.getExternalStorageState();

                    String vendorId = null, vendorName = null, vendorTypeId = null, decipline = null
                            , taxID = null, license = null, companyName = null;
                    int listSize = vendorList.size();
                    String cvsValues = "Vendor ID" + "," + "Vendor Name" + "," + "Vendor Type ID" + "," + "Discipline" +
                             "Tax ID" + "," + "Licence" + "," + "Company Name" + "\n";

                    for (int i = 0; i < listSize; i++) {
                        AllVendorList items = vendorList.get(i);
                        vendorId = items.getVendor_id();
                        vendorName = items.getVendor_name();
                        vendorTypeId = items.getVendor_type();
                        decipline = items.getDiscipline();
                        taxID = items.getText_tax_id();
                        license = items.getText_licence_no();
                        companyName = items.getText_company_name();

                        switch (decipline) {
                            case "1":
                                decipline = "Electrical";
                                break;
                            case "2":
                                decipline = "Mechanical";
                                break;
                            case "3":
                                decipline = "Civil";
                                break;
                            case "4":
                                decipline = "Architectural";
                                break;
                        }

                        cvsValues = cvsValues + vendorId + "," + vendorName + "," + vendorTypeId + "," + decipline +
                                taxID + "," + license +  companyName + "\n";
                    }

                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "Vendors-data.csv", cvsValues);
                } else

                {
                    Environment.getExternalStorageState();

                    String vendorId = null, vendorName = null, vendorTypeId = null, decipline = null
                            , taxID = null, license = null, companyName = null;
                    int listSize = vendorList.size();
                    String cvsValues = "Vendor ID" + "," + "Vendor Name" + "," + "Vendor Type ID" + "," + "Discipline" +
                            "Tax ID" + "," + "Licence" + "," + "Company Name" + "\n";

                    for (int i = 0; i < listSize; i++) {
                        AllVendorList items = vendorList.get(i);
                        vendorId = items.getVendor_id();
                        vendorName = items.getVendor_name();
                        vendorTypeId = items.getVendor_type();
                        decipline = items.getDiscipline();
                        taxID = items.getText_tax_id();
                        license = items.getText_licence_no();
                        companyName = items.getText_company_name();

                        switch (decipline) {
                            case "1":
                                decipline = "Electrical";
                                break;
                            case "2":
                                decipline = "Mechanical";
                                break;
                            case "3":
                                decipline = "Civil";
                                break;
                            case "4":
                                decipline = "Architectural";
                                break;
                        }

                        cvsValues = cvsValues + vendorId + "," + vendorName + "," + vendorTypeId + "," + decipline +
                                taxID + "," + license +  companyName + "\n";
                    }

                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "Vendors-data.csv", cvsValues);
                }

            }
            break;
        }
    }

    public void getVendorType()
    {
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, vendorTypeUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                try{
                    String type = response.getString("type");

                    if(type.equals("ERROR"))
                    {
                        Toast.makeText(AllVendors.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                    }

                    if(type.equals("INFO"))
                    {
                        dataArray = response.getJSONArray("data");
                        vendorTypeIdArray = new String[dataArray.length()+1];
                        vendorTypeArray = new String[dataArray.length()+1];

                        vendorTypeIdArray[0]="Select Vendor Type";
                        vendorTypeArray[0]="Select Vendor Type";

                        for(int i=0; i<dataArray.length();i++)
                        {
                            dataObject = dataArray.getJSONObject(i);
                            vendorTypeIdArray[i+1] = dataObject.getString("id");
                            vendorTypeArray[i+1] = dataObject.getString("type");
                        }

                            pDialog = new ProgressDialog(AllVendors.this);
                            pDialog.setMessage("Getting server data");
                            pDialog.show();

                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, mainUrl, null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try
                                        {
                                            dataArray = response.getJSONArray("data");
                                            for(int i=0; i<dataArray.length();i++)
                                            {
                                                dataObject = dataArray.getJSONObject(i);
                                                dataObject = dataArray.getJSONObject(i);
                                                vendorId = dataObject.getString("vendorId");
                                                vendorName = dataObject.getString("vendorName");
                                                vendorTypeId = dataObject.getString("vendorTypeId");
                                                decipline = dataObject.getString("decipline");
                                                taxID = dataObject.getString("taxID");
                                                license = dataObject.getString("license");
                                                companyName = dataObject.getString("companyName");

                                                for(int j=0; j<vendorTypeArray.length; j++)
                                                {
                                                    if(vendorTypeId.equals(vendorTypeIdArray[j]))
                                                        vendorTypeId = vendorTypeArray[j];
                                                }

                                                switch (decipline) {
                                                    case "1":
                                                        decipline = "Electrical";
                                                        break;
                                                    case "2":
                                                        decipline = "Mechanical";
                                                        break;
                                                    case "3":
                                                        decipline = "Civil";
                                                        break;
                                                    case "4":
                                                        decipline = "Architectural";
                                                        break;
                                                }

                                                if (getIntent().hasExtra("search"))
                                                {
                                                    if (getIntent().getStringExtra("search").equals("yes")) {

                                                        if (vendorName.toLowerCase().contains(searchText.toLowerCase()) || vendorId.toLowerCase().contains(searchText.toLowerCase())) {

                                                            items = new AllVendorList(String.valueOf(i+1), vendorId, vendorName, vendorTypeId, decipline,
                                                                    taxID, license, companyName);
                                                            vendorList.add(items);

                                                            vendorAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                                else
                                                {
                                                    items = new AllVendorList(String.valueOf(i+1), vendorId, vendorName, vendorTypeId, decipline,
                                                            taxID, license, companyName);
                                                    vendorList.add(items);

                                                    vendorAdapter.notifyDataSetChanged();
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

                }
                catch(JSONException e){
                    e.printStackTrace();}
            }
        },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley","Error");
            }
        }
        );
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jor);
        if(pDialog!=null)
            pDialog.dismiss();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AllVendors.this, SiteProcurementActivity.class);
        startActivity(intent);
    }
}