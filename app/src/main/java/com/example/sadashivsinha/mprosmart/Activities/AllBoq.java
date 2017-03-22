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
import com.example.sadashivsinha.mprosmart.Adapters.AllBoqAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.AllBoqList;
import com.example.sadashivsinha.mprosmart.ModelLists.AllItemsList;
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

public class AllBoq extends AppCompatActivity implements View.OnClickListener {

    private List<AllBoqList> allBoqList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AllBoqAdapter allBoqAdapter;
    String currentProjectNo, currentProjectName, currentProjectDesc, currentUser, currentDate, currentUom, itemName;
    JSONObject dataObject;
    JSONArray dataArray;
    AllBoqList qualityItem;
    View dialogView;
    AlertDialog show;
    Spinner spinner_boq_item;
    EditText text_quantity;
    Button createBtn;
    String item, uomId;
    String[] itemArray, uomIdArray, uomArray, uomNameArray;
    ProgressDialog pDialog;
    String id, boqItem, unit, uom, createdBy, createdDate, projectDescription;
    ConnectionDetector cd;
    public static final String TAG = AllBoq.class.getSimpleName();
    Boolean isInternetPresent = false;
    String url, boq_url, add_boq_url;
    PreferenceManager pm;
    String searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_boq);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getIntent().hasExtra("search")) {
            if (getIntent().getStringExtra("search").equals("yes")) {

                searchText = getIntent().getStringExtra("searchText");

                getSupportActionBar().setTitle("BOQ Search Results : " + searchText);
            }
        }

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentProjectName = pm.getString("projectName");
        currentProjectDesc = pm.getString("projectDesc");
        currentUser = pm.getString("userId");

        add_boq_url = pm.getString("SERVER_URL") + "/getBoqItems?projectId=\""+currentProjectNo+"\"";

        boq_url = pm.getString("SERVER_URL") + "/getBoq?projectId='"+currentProjectNo+"'";

        url = pm.getString("SERVER_URL") + "/getUom";

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = sdf.format(c.getTime());


        allBoqAdapter = new AllBoqAdapter(allBoqList);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(AllBoq.this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(allBoqAdapter);

        // check for Internet status
        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_content);
            Crouton.cancelAllCroutons();
            Crouton.makeText(AllBoq.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

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
                        uomArray = new String[dataArray.length()+1];
                        uomNameArray = new String[dataArray.length()+1];

                        uomArray[0]="UOM";
                        uomNameArray[0] = "UOM";

                        for(int i=0; i<dataArray.length();i++)
                        {
                            dataObject = dataArray.getJSONObject(i);
                            uomArray[i+1] = dataObject.getString("uomCode");
                            uomNameArray[i+1] = dataObject.getString("uomName");
                        }

                        prepareItems(boq_url);

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

            else
            {
                Toast.makeText(AllBoq.this, "Offline Data Not available for this BOQ", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }

        else
        {
            // Cache data not exist.
            prepareUom();
        }


        FloatingActionButton fab_add, fab_search, exportBtn;

        fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        fab_search = (FloatingActionButton) findViewById(R.id.fab_search);
        exportBtn = (FloatingActionButton) findViewById(R.id.exportBtn);

        fab_add.setOnClickListener(this);
        fab_search.setOnClickListener(this);
        exportBtn.setOnClickListener(this);

        if(getIntent().hasExtra("create"))
            if(getIntent().getStringExtra("create").equals("yes"))
            {
                final AlertDialog.Builder alert = new AlertDialog.Builder(AllBoq.this,android.R.style.Theme_Translucent_NoTitleBar);
                // Set an EditText view to get user input

                dialogView = LayoutInflater.from(AllBoq.this).inflate(R.layout.dialog_new_boq, null);
                alert.setView(dialogView);

                show = alert.show();

                createBtn = (Button) dialogView.findViewById(R.id.createBtn);

                spinner_boq_item = (Spinner) dialogView.findViewById(R.id.spinner_boq_item);
                text_quantity = (EditText) dialogView.findViewById(R.id.text_quantity);

                getAllBoq();

                spinner_boq_item.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        currentUom = uomIdArray[position];
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                createBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(text_quantity.getText().toString().isEmpty())
                        {
                            text_quantity.setError("Field cannot be empty.");
                        }
                        else if(spinner_boq_item.getSelectedItem().toString().equals("Select BOQ Item"))
                        {
                            Toast.makeText(AllBoq.this, "Select BOQ Item first", Toast.LENGTH_SHORT).show();
                        }

                        else
                        {
                            pDialog = new ProgressDialog(AllBoq.this);
                            pDialog.setMessage("Sending Data ...");
                            pDialog.setIndeterminate(false);
                            pDialog.setCancelable(true);
                            pDialog.show();

                            saveBoq();
                        }
                    }
                });
            }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add:
            {
//                Intent intent = new Intent(AllBoq.this, BOQCreate.class);
//                startActivity(intent);

                final AlertDialog.Builder alert = new AlertDialog.Builder(AllBoq.this,android.R.style.Theme_Translucent_NoTitleBar);
                // Set an EditText view to get user input

                dialogView = LayoutInflater.from(AllBoq.this).inflate(R.layout.dialog_new_boq, null);
                alert.setView(dialogView);

                show = alert.show();

                createBtn = (Button) dialogView.findViewById(R.id.createBtn);

                spinner_boq_item = (Spinner) dialogView.findViewById(R.id.spinner_boq_item);
                text_quantity = (EditText) dialogView.findViewById(R.id.text_quantity);

                getAllBoq();

                spinner_boq_item.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        currentUom = uomIdArray[position];
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                createBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(text_quantity.getText().toString().isEmpty())
                        {
                            text_quantity.setError("Field cannot be empty.");
                        }
                        else if(spinner_boq_item.getSelectedItem().toString().equals("Select BOQ Item"))
                        {
                            Toast.makeText(AllBoq.this, "Select BOQ Item first", Toast.LENGTH_SHORT).show();
                        }

                        else
                        {
                            pDialog = new ProgressDialog(AllBoq.this);
                            pDialog.setMessage("Sending Data ...");
                            pDialog.setIndeterminate(false);
                            pDialog.setCancelable(true);
                            pDialog.show();

                            saveBoq();
                        }
                    }
                });
            }
            break;
            case R.id.fab_search:
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Search BOQ by Name or ID !");
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
                            Intent intent = new Intent(AllBoq.this, AllBoq.class);
                            intent.putExtra("search", "yes");
                            intent.putExtra("searchText", input.getText().toString());

                            Log.d("SEARCH TEXT", input.getText().toString());

                            startActivity(intent);
                        }
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(AllBoq.this, "Search cancelled .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
            }
            break;
            case R.id.exportBtn:
            {
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP) {
                    if (ContextCompat.checkSelfPermission(AllBoq.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(AllBoq.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                    }
                    Environment.getExternalStorageState();

                    String id = null, unit = null, uom = null, itemName = null, createdBy = null, createdDate = null;
                    int listSize = allBoqList.size();
                    String cvsValues = "BOQ ID" + ","+ "Units" + ","+ "UOM" + ","+ "Item Name" + ","+ "Created By" +
                            ","+ "Created Date" + "\n";

                    for(int i=0; i<listSize;i++)
                    {
                        AllBoqList items = allBoqList.get(i);
                        id = items.getText_boq_no();
                        unit = items.getText_unit();
                        uom = items.getText_uom();
                        itemName = items.getItemName();
                        createdBy = items.getText_created_by();
                        createdDate = items.getText_date_created();

                        cvsValues = cvsValues +  id + ","+ unit + ","+ uom + ","+ itemName +","+ createdBy +","+ createdDate + "\n";
                    }

                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "AllBOQ-data.csv", cvsValues);
                }

                else

                {
                    Environment.getExternalStorageState();

                    String id = null, unit = null, uom = null, itemName = null, createdBy = null, createdDate = null;
                    int listSize = allBoqList.size();
                    String cvsValues = "BOQ ID" + ","+ "Units" + ","+ "UOM" + ","+ "Item Name" + ","+ "Created By" +
                            ","+ "Created Date" + "\n";

                    for(int i=0; i<listSize;i++)
                    {
                        AllBoqList items = allBoqList.get(i);
                        id = items.getText_boq_no();
                        unit = items.getText_unit();
                        uom = items.getText_uom();
                        itemName = items.getItemName();
                        createdBy = items.getText_created_by();
                        createdDate = items.getText_date_created();

                        cvsValues = cvsValues +  id + ","+ unit + ","+ uom + ","+ itemName +","+ createdBy +","+ createdDate + "\n";
                    }
                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "AllBOQ-data.csv", cvsValues);

                }

            }
            break;
        }
    }

    public void prepareUom()
    {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {
//                            dataObject = response.getJSONObject(0);
                            dataArray = response.getJSONArray("data");
                            uomArray = new String[dataArray.length()+1];
                            uomNameArray = new String[dataArray.length()+1];

                            uomArray[0]="UOM";
                            uomNameArray[0] = "UOM";

                            for(int i=0; i<dataArray.length();i++)
                            {
                                dataObject = dataArray.getJSONObject(i);
                                uomArray[i+1] = dataObject.getString("uomCode");
                                uomNameArray[i+1] = dataObject.getString("uomName");
                            }


                            if (!isInternetPresent) {

                                Cache cache = AppController.getInstance().getRequestQueue().getCache();
                                Cache.Entry entry = cache.get(boq_url);
                                if (entry != null) {
                                    //Cache data available.
                                    try {
                                        String data = new String(entry.data, "UTF-8");
                                        Log.d("CACHE DATA", data);
                                        JSONObject jsonObject = new JSONObject(data);
                                        try {
                                            dataArray = jsonObject.getJSONArray("data");

                                            for(int i=0; i<dataArray.length();i++) {

                                                dataObject = dataArray.getJSONObject(i);

                                                id = dataObject.getString("id");
                                                boqItem = dataObject.getString("boqItem");
                                                projectDescription = dataObject.getString("projectDescription");
                                                unit = dataObject.getString("unit");
                                                uom = dataObject.getString("uom");
                                                createdBy = dataObject.getString("createdBy");
                                                createdDate = dataObject.getString("createdDate");
                                                itemName = dataObject.getString("itemName");

                                                for (int j = 0; j < uomArray.length; j++) {
                                                    if (uom.equals(uomArray[j])) {
                                                        uom = uomNameArray[j];
                                                        break;
                                                    }
                                                }

                                                Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(createdDate);
                                                createdDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                                qualityItem = new AllBoqList(String.valueOf(i + 1), id, currentProjectNo, currentProjectName,
                                                        unit, uom, itemName, createdBy, createdDate);
                                                allBoqList.add(qualityItem);

                                                allBoqAdapter.notifyDataSetChanged();
                                            }

                                        Boolean createBOQPending = pm.getBoolean("createBOQPending");

                                        if(createBOQPending)
                                        {

                                            String jsonObjectVal = pm.getString("objectBOQ");
                                            Log.d("JSON BOQLINE PENDING :", jsonObjectVal);

                                            JSONObject jsonObjectPending = new JSONObject(jsonObjectVal);
                                            Log.d("JSONObj BOQIT PENDING :", jsonObjectPending.toString());

                                            boqItem = jsonObjectPending.getString("boqItem");
                                            projectDescription = jsonObjectPending.getString("projectDescription");
                                            unit = jsonObjectPending.getString("unit");
                                            uom = jsonObjectPending.getString("uom");
                                            createdBy = jsonObjectPending.getString("createdBy");
                                            createdDate = jsonObjectPending.getString("createdDate");
                                            itemName = jsonObjectPending.getString("itemName");

                                            for (int j = 0; j < uomArray.length; j++) {
                                                if (uom.equals(uomArray[j])) {
                                                    uom = uomNameArray[j];
                                                    break;
                                                }
                                            }

                                            Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(createdDate);
                                            createdDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                            qualityItem = new AllBoqList(String.valueOf(dataArray.length() + 1), getResources().getString(R.string.waiting_to_connect), currentProjectNo, currentProjectName,
                                                    unit, uom, itemName, createdBy, createdDate);
                                            allBoqList.add(qualityItem);

                                            allBoqAdapter.notifyDataSetChanged();
                                            pDialog.dismiss();
                                        }

                                        } catch (JSONException | ParseException e) {
                                            e.printStackTrace();
                                        }

                                    } catch (UnsupportedEncodingException | JSONException e) {
                                        e.printStackTrace();
                                    }
                                    if (pDialog != null)
                                        pDialog.dismiss();
                                }

                                else
                                {
                                    Toast.makeText(AllBoq.this, "Offline Data Not available for this BOQ", Toast.LENGTH_SHORT).show();
                                    pDialog.dismiss();
                                }
                            }

                            else
                            {
                                prepareItems(boq_url);
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
    }


    public void prepareItems(String boq_url) {
        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(AllBoq.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(AllBoq.this);
            pDialog.setMessage("Getting cache data");
            pDialog.show();

            Cache cache = AppController.getInstance().getRequestQueue().getCache();
            Cache.Entry entry = cache.get(boq_url);
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
                            boqItem = dataObject.getString("boqItem");
                            projectDescription = dataObject.getString("projectDescription");
                            unit = dataObject.getString("unit");
                            uom = dataObject.getString("uom");
                            createdBy = dataObject.getString("createdBy");
                            createdDate = dataObject.getString("createdDate");
                            itemName = dataObject.getString("itemName");

                            for (int j = 0; j < uomArray.length; j++) {
                                if (uom.equals(uomArray[j])) {
                                    uom = uomNameArray[j];
                                    break;
                                }
                            }

                            Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(createdDate);
                            createdDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                            if (getIntent().hasExtra("search"))
                            {
                                if (getIntent().getStringExtra("search").equals("yes")) {

                                    if (id.toLowerCase().contains(searchText.toLowerCase()) || itemName.toLowerCase().contains(searchText.toLowerCase())) {
                                        qualityItem = new AllBoqList(String.valueOf(i + 1), id, currentProjectNo, currentProjectName,
                                                unit, uom, itemName, createdBy, createdDate);
                                        allBoqList.add(qualityItem);

                                        allBoqAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                            else
                            {
                                qualityItem = new AllBoqList(String.valueOf(i + 1), id, currentProjectNo, currentProjectName,
                                        unit, uom, itemName, createdBy, createdDate);
                                allBoqList.add(qualityItem);

                                allBoqAdapter.notifyDataSetChanged();
                            }
                        }
                        pDialog.dismiss();

                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                    }

                    pDialog.dismiss();
//                    Toast.makeText(getApplicationContext(), "Loading from cache.", Toast.LENGTH_SHORT).show();
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }
                if (pDialog != null)
                    pDialog.dismiss();
            } else {
                Toast.makeText(AllBoq.this, "Offline Data Not available for this BOQ", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
            if(pDialog!=null)
                pDialog.dismiss();
        } else {
            pDialog = new ProgressDialog(AllBoq.this);
            pDialog.setMessage("Getting server data");
            pDialog.show();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, boq_url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
//                            dataObject = response.getJSONObject(0);
                                dataArray = response.getJSONArray("data");

                                for (int i = 0; i < dataArray.length(); i++) {
                                    dataObject = dataArray.getJSONObject(i);
                                    id = dataObject.getString("id");
                                    boqItem = dataObject.getString("boqItem");
                                    projectDescription = dataObject.getString("projectDescription");
                                    unit = dataObject.getString("unit");
                                    uom = dataObject.getString("uom");
                                    createdBy = dataObject.getString("createdBy");
                                    createdDate = dataObject.getString("createdDate");
                                    itemName = dataObject.getString("itemName");

                                    for (int j = 0; j < uomArray.length; j++) {
                                        if (uom.equals(uomArray[j])) {
                                            uom = uomNameArray[j];
                                            break;
                                        }
                                    }

                                    Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(createdDate);
                                    createdDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                    if (getIntent().hasExtra("search"))
                                    {
                                        if (getIntent().getStringExtra("search").equals("yes")) {

                                            if (id.toLowerCase().contains(searchText.toLowerCase()) || itemName.toLowerCase().contains(searchText.toLowerCase())) {

                                                qualityItem = new AllBoqList(String.valueOf(i + 1), id, currentProjectNo, currentProjectName,
                                                        unit, uom, itemName, createdBy, createdDate);
                                                allBoqList.add(qualityItem);

                                                allBoqAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                    else
                                    {
                                        qualityItem = new AllBoqList(String.valueOf(i + 1), id, currentProjectNo, currentProjectName,
                                                unit, uom, itemName, createdBy, createdDate);
                                        allBoqList.add(qualityItem);

                                        allBoqAdapter.notifyDataSetChanged();
                                    }
                                    pDialog.dismiss();
                                }
                            } catch (JSONException | ParseException e1) {
                                e1.printStackTrace();

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

            AppController.getInstance().addToRequestQueue(jsonObjectRequest);

            if(pDialog!=null)
                pDialog.dismiss();
        }
    }

    public void getAllBoq()
    {
        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(AllBoq.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(AllBoq.this);
            pDialog.setMessage("Getting cache data");
            pDialog.show();

            Cache cache = AppController.getInstance().getRequestQueue().getCache();
            Cache.Entry entry = cache.get(add_boq_url);
            if (entry != null) {
                //Cache data available.
                try {
                    String data = new String(entry.data, "UTF-8");
                    Log.d("CACHE DATA", data);
                    JSONObject jsonObject = new JSONObject(data);
                    try {
                        dataArray = jsonObject.getJSONArray("data");
                        itemArray = new String[dataArray.length()+1];
                        uomIdArray = new String[dataArray.length()+1];

                        itemArray[0] = "Select BOQ Item";
                        uomIdArray[0] = "Select BOQ";

                        for(int i=0; i<dataArray.length();i++)
                        {
                            dataObject = dataArray.getJSONObject(i);
                            item = dataObject.getString("item");
                            uomId = dataObject.getString("uom");

                            itemArray[i+1] = item;
                            uomIdArray[i+1] = uomId;
                        }

                        if(itemArray!=null)
                        {
                            ArrayAdapter<String> adapterCurrency = new ArrayAdapter<String>(dialogView.getContext(),
                                    android.R.layout.simple_dropdown_item_1line, itemArray);

                            spinner_boq_item.setAdapter(adapterCurrency);
                        }

                        else
                        {
                            Toast.makeText(AllBoq.this, "No BOQ Items in this project", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AllBoq.this, AllBoq.class);
                            startActivity(intent);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    pDialog.dismiss();
//                    Toast.makeText(getApplicationContext(), "Loading from cache.", Toast.LENGTH_SHORT).show();
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }
                if (pDialog != null)
                    pDialog.dismiss();
            }

            else
            {
                Toast.makeText(AllBoq.this, "Offline Data Not available for this BOQ", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }

        else
        {
            pDialog = new ProgressDialog(AllBoq.this);
            pDialog.setMessage("Getting server data");
            pDialog.show();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, add_boq_url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try
                            {
//                            dataObject = response.getJSONObject(0);
                                dataArray = response.getJSONArray("data");
                                itemArray = new String[dataArray.length()+1];
                                uomIdArray = new String[dataArray.length()+1];

                                itemArray[0] = "Select BOQ Item";
                                uomIdArray[0] = "Select BOQ";

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    item = dataObject.getString("item");
                                    uomId = dataObject.getString("uom");

                                    itemArray[i+1] = item;
                                    uomIdArray[i+1] = uomId;
                                }

                                if(itemArray!=null)
                                {
                                    ArrayAdapter<String> adapterCurrency = new ArrayAdapter<String>(dialogView.getContext(),
                                            android.R.layout.simple_dropdown_item_1line, itemArray);

                                    spinner_boq_item.setAdapter(adapterCurrency);
                                }

                                else
                                {
                                    Toast.makeText(AllBoq.this, "No BOQ Items in this project", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(AllBoq.this, AllBoq.class);
                                    startActivity(intent);
                                }
                                pDialog.dismiss();
                            } catch (JSONException e1) {
                                e1.printStackTrace();

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

    public void saveBoq()
    {
        JSONObject object = new JSONObject();

        try {
            object.put("projectId",currentProjectNo);
            object.put("projectName", currentProjectName);
            object.put("projectDescription", currentProjectDesc);
            object.put("boqItem",spinner_boq_item.getSelectedItem().toString());
            object.put("unit", text_quantity.getText().toString());
//            object.put("itemName", text_currency.getText().toString());
            object.put("uom", currentUom);
            object.put("createdBy", currentUser);
            object.put("createdDate", currentDate);

            Log.d("REQUEST SENT OF JSON :" , object.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(AllBoq.this);

        String url = AllBoq.this.pm.getString("SERVER_URL") + "/postBoq";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            Log.d("SERVER RESPONSE :", response.toString());

                            if(response.getString("msg").equals("success"))
                            {
                                Toast.makeText(AllBoq.this, "BOQ Created. ID - "+ response.getString("data"), Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                                Intent intent = new Intent(AllBoq.this, AllBoq.class);
                                startActivity(intent);
                            }
                            pDialog.dismiss();

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

            Boolean createBOQPending = pm.getBoolean("createBOQPending");

            if(createBOQPending)
            {
                Toast.makeText(AllBoq.this, "Already a BOQ creation is in progress. Please try after sometime.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(AllBoq.this, "Internet not currently available. BOQ will automatically get created on internet connection.", Toast.LENGTH_SHORT).show();

                pm.putString("objectBOQ", object.toString());
                pm.putString("urlBOQ", url);
                pm.putString("toastMessageBOQ", "BOQ Created");
                pm.putBoolean("createBOQPending", true);

                pDialog.dismiss();
                Intent intent = new Intent(AllBoq.this, AllBoq.class);
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
        Intent intent = new Intent(AllBoq.this, ProjectPlanningSchedulingActivity.class);
        startActivity(intent);
    }

}