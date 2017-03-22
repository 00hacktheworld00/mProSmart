package com.example.sadashivsinha.mprosmart.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.example.sadashivsinha.mprosmart.Adapters.AllMaterialIssueAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.AllBoqList;
import com.example.sadashivsinha.mprosmart.ModelLists.AllMaterialIssueList;
import com.example.sadashivsinha.mprosmart.ModelLists.AllPurchaseRequisitionList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.AppController;
import com.example.sadashivsinha.mprosmart.Utils.Communicator;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.example.sadashivsinha.mprosmart.Utils.CsvCreateUtility;
import com.github.clans.fab.FloatingActionButton;
import com.weiwangcn.betterspinner.library.BetterSpinner;

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

public class AllMaterialIssue extends AppCompatActivity implements View.OnClickListener {

    private List<AllMaterialIssueList> allBoqList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AllMaterialIssueAdapter allMaterialIssueAdapter;
    ProgressDialog pDialog;
    String currentProjectNo, currentProjectName, id, createdBy, createdDate, currentUser;
    JSONArray dataArray;
    JSONObject dataObject;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    View dialogView;
    AlertDialog show;
    BetterSpinner spinner_issue_as_per_boq;
    Spinner spinner_boq_item;
    LinearLayout hiddenLayout;
    EditText text_quantity;
    String boqId;
    String[] boqIdArray;
    Button createBtn;
    String[] boqItemNameArray, boqUomArray;
    String itemName,uom, currentItemId, currentUomId;
    String url, boq_url;
    public static final String TAG = AllMaterialIssue.class.getSimpleName();
    PreferenceManager pm;

    AllMaterialIssueList qualityItem;
    String issueAsPerBoq, boqItem, quantity, issuedTo, issuesdOn, issuedBy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_material_issue);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentProjectName = pm.getString("projectName");
        currentUser = pm.getString("userId");

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        url = pm.getString("SERVER_URL") + "/getMaterialIssue?projectId='"+currentProjectNo+"'";
        boq_url = pm.getString("SERVER_URL") + "/getBoq?projectId='"+currentProjectNo+"'";

        if (getIntent().hasExtra("search")) {
            if (getIntent().getStringExtra("search").equals("yes")) {

                //searched values

                final String searchText = getIntent().getStringExtra("searchText");

                getSupportActionBar().setTitle("Material Issue Search Results : " + searchText);

                class MyTask extends AsyncTask<Void, Void, Void> {

                    @Override protected void onPreExecute() {
                        allMaterialIssueAdapter = new AllMaterialIssueAdapter(allBoqList);
                        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                        recyclerView.setLayoutManager(new LinearLayoutManager(AllMaterialIssue.this));
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setAdapter(allMaterialIssueAdapter);

                        pDialog = new ProgressDialog(AllMaterialIssue.this);
                        pDialog.setMessage("Searching Material Issue");
                        pDialog.show();
                    }

                    @Override
                    protected Void doInBackground(Void... params) {
                        prepareSearchedValues(searchText);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        allMaterialIssueAdapter.notifyDataSetChanged();
                    }

                }

                new MyTask().execute();

            }
        }
        else
        {
                allMaterialIssueAdapter = new AllMaterialIssueAdapter(allBoqList);
                recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                recyclerView.setLayoutManager(new LinearLayoutManager(AllMaterialIssue.this));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(allMaterialIssueAdapter);

            if (!isInternetPresent) {
                // Internet connection is not present
                // Ask user to connect to Internet
                RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_layout);
                Crouton.cancelAllCroutons();
                Crouton.makeText(AllMaterialIssue.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

                pDialog = new ProgressDialog(AllMaterialIssue.this);
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
                                issueAsPerBoq = dataObject.getString("issueAsPerBoq");
                                boqItem = dataObject.getString("boqItem");
                                quantity = dataObject.getString("quantity");
                                issuedTo = dataObject.getString("issuedTo");
                                issuesdOn = dataObject.getString("issuesdOn");
                                issuedBy = dataObject.getString("issuedBy");

                                Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(issuesdOn);
                                issuesdOn = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                qualityItem = new AllMaterialIssueList(String.valueOf(i+1), currentProjectNo, id, issueAsPerBoq,boqItem, quantity,
                                        issuedTo, issuesdOn, issuedBy);
                                allBoqList.add(qualityItem);
                                allMaterialIssueAdapter.notifyDataSetChanged();
                            }

                            Boolean createMaterialIssuePending = pm.getBoolean("createMaterialIssuePending");

                            if(createMaterialIssuePending)
                            {
                                String jsonObjectVal = pm.getString("objectMaterialIssue");
                                Log.d("JSON MOM PENDING :", jsonObjectVal);

                                JSONObject jsonObjectPending = new JSONObject(jsonObjectVal);
                                Log.d("JSONObj MOM PENDING :", jsonObjectPending.toString());

                                issueAsPerBoq = jsonObjectPending.getString("issueAsPerBoq");
                                boqItem = jsonObjectPending.getString("boqItem");
                                quantity = jsonObjectPending.getString("quantity");
                                issuedTo = jsonObjectPending.getString("issuedTo");
                                issuesdOn = jsonObjectPending.getString("issuesdOn");
                                issuedBy = jsonObjectPending.getString("issuedBy");

                                Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(issuesdOn);
                                issuesdOn = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                qualityItem = new AllMaterialIssueList(String.valueOf(dataArray.length()+1), currentProjectNo,  getResources().getString(R.string.waiting_to_connect), issueAsPerBoq,boqItem, quantity,
                                        issuedTo, issuesdOn, issuedBy);
                                allBoqList.add(qualityItem);
                                allMaterialIssueAdapter.notifyDataSetChanged();
                            }

                            pDialog.dismiss();

                        }catch (JSONException | ParseException e) {
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
                    Toast.makeText(AllMaterialIssue.this, "Offline Data Not available for this Material Issue", Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                }
            }

            else
            {
                // Cache data not exist.
                prepareItems();
            }
        }

        FloatingActionButton fab_add, fab_search, exportBtn;

        fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        fab_search = (FloatingActionButton) findViewById(R.id.fab_search);
        exportBtn = (FloatingActionButton) findViewById(R.id.exportBtn);

        fab_add.setOnClickListener(this);
        fab_search.setOnClickListener(this);
        exportBtn.setOnClickListener(this);

        if(getIntent().hasExtra("create"))
        {
            if(getIntent().getStringExtra("create").equals("yes"))
                create();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add:
            {

               create();

            }
            break;
            case R.id.fab_search:
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Search Material Issue !");
                // Set an EditText view to get user input
                final EditText input = new EditText(this);
                alert.setView(input);
                alert.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (input.getText().toString().isEmpty()) {
                            input.setError("Enter Search Field");
                        } else {
                            Intent intent = new Intent(AllMaterialIssue.this, AllMaterialIssue.class);
                            intent.putExtra("search", "yes");
                            intent.putExtra("searchText", input.getText().toString());
                            startActivity(intent);
                        }
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(AllMaterialIssue.this, "Search cancelled .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
            }
            break;
            case R.id.exportBtn:

            {
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP) {
                    if (ContextCompat.checkSelfPermission(AllMaterialIssue.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(AllMaterialIssue.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                    }
                    Environment.getExternalStorageState();

                    String currentProjectNo = null, issueAsPerBoq = null, boqItem = null, quantity = null, issuedTo = null
                            , issuesdOn = null, issuedBy = null;
                    int listSize = allBoqList.size();
                    String cvsValues = "Project No" + ","+ "Issue As per BOQ" + ","+ "BOQ Item" + ","+ "Quantity"
                            +","+ "Issue To" + ","+ "Issue On" + ","+ "Issued By\n";

                    for(int i=0; i<listSize;i++)
                    {
                        AllMaterialIssueList items = allBoqList.get(i);
                        currentProjectNo = items.getText_project_id();
                        issueAsPerBoq = items.getText_issue_as_per_bq();
                        boqItem = items.getText_boq_item();
                        quantity = items.getText_quantity();
                        issuedTo = items.getText_issued_to();
                        issuesdOn = items.getText_issued_on();
                        issuedBy = items.getText_issued_by();

                        cvsValues = cvsValues +  currentProjectNo + ","+ issueAsPerBoq + ","+ boqItem + ","+ quantity + ","+
                                issuedTo + issuesdOn + issuedBy + "\n";
                    }

                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "AllMaterialIssue-data.csv", cvsValues);
                }

                else

                {
                    Environment.getExternalStorageState();

                    String currentProjectNo = null, issueAsPerBoq = null, boqItem = null, quantity = null, issuedTo = null
                            , issuesdOn = null, issuedBy = null;
                    int listSize = allBoqList.size();
                    String cvsValues = "Project No" + ","+ "Issue As per BOQ" + ","+ "BOQ Item" + ","+ "Quantity"
                            +","+ "Issue To" + ","+ "Issue On" + ","+ "Issued By\n";

                    for(int i=0; i<listSize;i++)
                    {
                        AllMaterialIssueList items = allBoqList.get(i);
                        currentProjectNo = items.getText_project_id();
                        issueAsPerBoq = items.getText_issue_as_per_bq();
                        boqItem = items.getText_boq_item();
                        quantity = items.getText_quantity();
                        issuedTo = items.getText_issued_to();
                        issuesdOn = items.getText_issued_on();
                        issuedBy = items.getText_issued_by();

                        cvsValues = cvsValues +  currentProjectNo + ","+ issueAsPerBoq + ","+ boqItem + ","+ quantity + ","+
                                issuedTo + issuesdOn + issuedBy + "\n";
                    }

                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "AllMaterialIssue-data.csv", cvsValues);

                }

            }
            break;
        }
    }

    public void  saveItems()
    {
        JSONObject object = new JSONObject();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(c.getTime());

        try {
            object.put("projectId", currentProjectNo);
            object.put("issueAsPerBoq", spinner_issue_as_per_boq.getText().toString());
            object.put("boqItem", currentItemId);
            object.put("quantity", text_quantity.getText().toString());
            object.put("issuedTo", currentUser);
            object.put("issuedBy", currentUser);
            object.put("issuesdOn", currentDate);

            Log.d("json of request : ", object.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(AllMaterialIssue.this);

        String url = pm.getString("SERVER_URL") + "/postMaterialIssue";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("response", response.toString());
                            if(response.getString("msg").equals("success"))
                            {
                                Toast.makeText(AllMaterialIssue.this, "Material Issue Created. ID - " + response.getString("data"), Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                                Intent intent = new Intent(AllMaterialIssue.this, AllMaterialIssue.class);
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

        if (!isInternetPresent)
        {
            // Internet connection is not present

            Communicator communicator = new Communicator();
            Log.d("object", object.toString());

            Boolean createMaterialIssuePending = pm.getBoolean("createMaterialIssuePending");

            if(createMaterialIssuePending)
            {
                Toast.makeText(AllMaterialIssue.this, "Already a Material Issue creation is in progress. Please try after sometime.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(AllMaterialIssue.this, "Internet not currently available. Material Issue will automatically get created on internet connection.", Toast.LENGTH_SHORT).show();

                pm.putString("objectMaterialIssue", object.toString());
                pm.putString("urlMaterialIssue", url);
                pm.putString("toastMessageMaterialIssue", "Material Issue Created");
                pm.putBoolean("createMaterialIssuePending", true);
            }


            if(pDialog!=null)
                pDialog.dismiss();

            Intent intent = new Intent(AllMaterialIssue.this, AllMaterialIssue.class);
            startActivity(intent);
        }
        else
        {
            requestQueue.add(jor);
        }
    }

    public void create()
    {
        final AlertDialog.Builder alert = new AlertDialog.Builder(AllMaterialIssue.this,android.R.style.Theme_Translucent_NoTitleBar);
        // Set an EditText view to get user input

        dialogView = LayoutInflater.from(AllMaterialIssue.this).inflate(R.layout.dialog_new_material_issue, null);
        alert.setView(dialogView);

        show = alert.show();

        text_quantity = (EditText) dialogView.findViewById(R.id.text_quantity);

        spinner_issue_as_per_boq = (BetterSpinner) dialogView.findViewById(R.id.spinner_issue_as_per_boq);
        spinner_boq_item = (Spinner) dialogView.findViewById(R.id.spinner_boq_item);

        hiddenLayout = (LinearLayout) dialogView.findViewById(R.id.hiddenLayout);
        hiddenLayout.setVisibility(View.GONE);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line,
                new String[] {"Yes", "No"});

        spinner_issue_as_per_boq.setAdapter(adapter);


        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet

            pDialog = new ProgressDialog(AllMaterialIssue.this);
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
                        boqIdArray = new String[dataArray.length()+1];
                        boqItemNameArray = new String[dataArray.length()+1];
                        boqUomArray = new String[dataArray.length()+1];

                        boqIdArray[0]= "Select BOQ";
                        boqItemNameArray[0]= "Select BOQ";
                        boqUomArray[0]= "Select BOQ";

                        for(int i=0; i<dataArray.length();i++)
                        {
                            dataObject = dataArray.getJSONObject(i);
                            boqId = dataObject.getString("id");
                            itemName = dataObject.getString("itemName");
                            uom = dataObject.getString("uom");

                            boqIdArray[i+1]=boqId;
                            boqItemNameArray[i+1]=itemName;
                            boqUomArray[i+1]=uom;
                        }

                        ArrayAdapter<String> boqadapter = new ArrayAdapter<String>(AllMaterialIssue.this,
                                android.R.layout.simple_dropdown_item_1line,boqItemNameArray);
                        spinner_boq_item.setAdapter(boqadapter);
                        pDialog.dismiss();

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
                Toast.makeText(AllMaterialIssue.this, "Offline Data Not available for BOQ", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }

        else
        {
            // Cache data not exist.
            getAllBoq();
        }

        spinner_boq_item.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0)
                {
                    currentItemId = boqIdArray[position];
                    currentUomId = boqUomArray[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_issue_as_per_boq.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(position==0)
                {
                    hiddenLayout.setVisibility(View.VISIBLE);
                }
                else
                {
                    hiddenLayout.setVisibility(View.GONE);
                }
            }
        });

        createBtn = (Button) dialogView.findViewById(R.id.createBtn);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(spinner_issue_as_per_boq.getText().toString().equals("Yes"))
                {
                    if(spinner_boq_item.getSelectedItem().toString().equals("Select BOQ"))
                    {
                        Toast.makeText(AllMaterialIssue.this, "Select BOQ first", Toast.LENGTH_SHORT).show();
                    }
                    else if(text_quantity.getText().toString().isEmpty())
                    {
                        text_quantity.setError("Quantity cannot be left empty");
                    }
                    else
                    {
                        saveItems();
                    }
                }
                else
                {
                    saveItems();
                }
            }
        });
    }

    public void prepareItems()
    {
        pDialog = new ProgressDialog(AllMaterialIssue.this);
        pDialog.setMessage("Getting server data");
        pDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {
//                            dataObject = response.getJSONObject(0);
                            Log.d("RESPONSE JSON", response.toString());
                            dataArray = response.getJSONArray("data");
                            for(int i=0; i<dataArray.length();i++)
                            {
                                dataObject = dataArray.getJSONObject(i);
                                id = dataObject.getString("id");
                                issueAsPerBoq = dataObject.getString("issueAsPerBoq");
                                boqItem = dataObject.getString("boqItem");
                                quantity = dataObject.getString("quantity");
                                issuedTo = dataObject.getString("issuedTo");
                                issuesdOn = dataObject.getString("issuesdOn");
                                issuedBy = dataObject.getString("issuedBy");

                                Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(issuesdOn);
                                issuesdOn = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                qualityItem = new AllMaterialIssueList(String.valueOf(i+1), currentProjectNo, id, issueAsPerBoq,boqItem, quantity,
                                        issuedTo, issuesdOn, issuedBy);
                                allBoqList.add(qualityItem);
                                allMaterialIssueAdapter.notifyDataSetChanged();
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

        if(pDialog!=null)
            pDialog.dismiss();
    }
    public void prepareSearchedValues(final String searchedText) {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = pm.getString("SERVER_URL") + "/getMaterialIssue?projectId='"+currentProjectNo+"'";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            String type = response.getString("type");

                            if (type.equals("ERROR")) {
                                pDialog.dismiss();
                                Toast.makeText(AllMaterialIssue.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if (type.equals("INFO")) {
                                dataArray = response.getJSONArray("data");
                                for (int i = 0; i < dataArray.length(); i++) {
                                    dataObject = dataArray.getJSONObject(i);

                                    dataObject = dataArray.getJSONObject(i);
                                    id = dataObject.getString("id");
                                    issueAsPerBoq = dataObject.getString("issueAsPerBoq");
                                    boqItem = dataObject.getString("boqItem");
                                    quantity = dataObject.getString("quantity");
                                    issuedTo = dataObject.getString("issuedTo");
                                    issuesdOn = dataObject.getString("issuesdOn");
                                    issuedBy = dataObject.getString("issuedBy");

                                    Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(issuesdOn);
                                    issuesdOn = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                    if (id.toLowerCase().contains(searchedText.toLowerCase())) {
                                        qualityItem = new AllMaterialIssueList(String.valueOf(i+1), currentProjectNo, id, issueAsPerBoq,boqItem, quantity,
                                                issuedTo, issuesdOn, issuedBy);
                                        allBoqList.add(qualityItem);
                                        allMaterialIssueAdapter.notifyDataSetChanged();
                                    }
                                }

                                if (allBoqList.size() == 0) {
                                    Toast.makeText(AllMaterialIssue.this, "Search didn't match any data", Toast.LENGTH_SHORT).show();
                                }
                            }
                            pDialog.dismiss();
                        } catch (JSONException e) {
                            pDialog.dismiss();
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        Log.e("Volley", "Error");
                    }
                }
        );
        requestQueue.add(jor);

    }


    public void getAllBoq()
    {
        pDialog = new ProgressDialog(AllMaterialIssue.this);
        pDialog.setMessage("Getting server data");
        pDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, boq_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {
//                            dataObject = response.getJSONObject(0);
                            Log.d("RESPONSE JSON", response.toString());
                            dataArray = response.getJSONArray("data");
                            boqIdArray = new String[dataArray.length()+1];
                            boqItemNameArray = new String[dataArray.length()+1];
                            boqUomArray = new String[dataArray.length()+1];

                            boqIdArray[0]= "Select BOQ";
                            boqItemNameArray[0]= "Select BOQ";
                            boqUomArray[0]= "Select BOQ";

                            for(int i=0; i<dataArray.length();i++)
                            {
                                dataObject = dataArray.getJSONObject(i);
                                boqId = dataObject.getString("id");
                                itemName = dataObject.getString("itemName");
                                uom = dataObject.getString("uom");

                                boqIdArray[i+1]=boqId;
                                boqItemNameArray[i+1]=itemName;
                                boqUomArray[i+1]=uom;
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(AllMaterialIssue.this,
                                    android.R.layout.simple_dropdown_item_1line,boqItemNameArray);
                            spinner_boq_item.setAdapter(adapter);
                            pDialog.dismiss();
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AllMaterialIssue.this, InventoryMainActivity.class);
        startActivity(intent);
    }

}