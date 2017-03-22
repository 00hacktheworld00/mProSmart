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
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.example.sadashivsinha.mprosmart.Adapters.AllPurchaseRequisitionAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.AllBoqList;
import com.example.sadashivsinha.mprosmart.ModelLists.AllPurchaseRequisitionList;
import com.example.sadashivsinha.mprosmart.ModelLists.AllQualityStandardList;
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

public class AllPurchaseRequisition extends AppCompatActivity implements View.OnClickListener {

    private List<AllPurchaseRequisitionList> purchaseList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AllPurchaseRequisitionAdapter purchaseAdapter;
    String currentProjectNo, currentProjectName, currentUser, currentDate;
    View dialogView;
    AlertDialog show;
    ProgressDialog pDialog;
    String id, department, createdBy, createdDate, isApproved, isPo;
    JSONArray dataArray;
    JSONObject dataObject;
    PreferenceManager pm;
    AllPurchaseRequisitionList items;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    String url, searchText;
    public static final String TAG = AllPurchaseRequisition.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_purchase_requisition);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getIntent().hasExtra("search")) {
            if (getIntent().getStringExtra("search").equals("yes")) {

                searchText = getIntent().getStringExtra("searchText");

                getSupportActionBar().setTitle("PR Search Results : " + searchText);
            }
        }

        pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentProjectName = pm.getString("projectName");
        currentUser = pm.getString("userId");

        url = pm.getString("SERVER_URL") + "/getPurchaseRequisition?projectId='"+currentProjectNo+"'";

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        purchaseAdapter = new AllPurchaseRequisitionAdapter(purchaseList);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(AllPurchaseRequisition.this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(purchaseAdapter);

        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(AllPurchaseRequisition.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(AllPurchaseRequisition.this);
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

                        for(int i=0; i<dataArray.length();i++)
                        {
                            dataObject = dataArray.getJSONObject(i);
                            id = dataObject.getString("id");
                            department = dataObject.getString("department");
                            createdBy = dataObject.getString("createdBy");
                            createdDate = dataObject.getString("createdDate");
                            isApproved = dataObject.getString("approved");
                            isPo = dataObject.getString("isPo");

                            Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(createdDate);
                            createdDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                            if (getIntent().hasExtra("search"))
                            {
                                if (getIntent().getStringExtra("search").equals("yes")) {

                                    if (id.toLowerCase().contains(searchText.toLowerCase()) || department.toLowerCase().contains(searchText.toLowerCase())) {
                                        items = new AllPurchaseRequisitionList(String.valueOf(i+1), id, department,
                                                createdDate, createdBy, isApproved, isPo);
                                        purchaseList.add(items);

                                        purchaseAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                            else
                            { items = new AllPurchaseRequisitionList(String.valueOf(i+1), id, department,
                                    createdDate, createdBy, isApproved, isPo);
                                purchaseList.add(items);

                                purchaseAdapter.notifyDataSetChanged();
                            }

                        }

                        Boolean createPurchaseRequisitionPending = pm.getBoolean("createPurchaseRequisitionPending");

                        if(createPurchaseRequisitionPending)
                        {
                            String jsonObjectVal = pm.getString("objectPurchaseRequisition");
                            Log.d("JSON BC PENDING :", jsonObjectVal);

                            JSONObject jsonObjectPending = new JSONObject(jsonObjectVal);
                            Log.d("JSONObj BC PENDING :", jsonObjectPending.toString());

                            department = jsonObjectPending.getString("department");
                            createdBy = jsonObjectPending.getString("createdBy");
                            createdDate = jsonObjectPending.getString("createdDate");
                            isApproved = jsonObjectPending.getString("approved");
                            isPo = jsonObjectPending.getString("isPo");

                            Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(createdDate);
                            createdDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                            items = new AllPurchaseRequisitionList(String.valueOf(dataArray.length()+1), getResources().getString(R.string.waiting_to_connect), department,
                                    createdDate, createdBy, isApproved, isPo);
                            purchaseList.add(items);

                            purchaseAdapter.notifyDataSetChanged();
                        }

                    } catch (JSONException | ParseException e) {
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
                Toast.makeText(AllPurchaseRequisition.this, "Offline Data Not available for Purchase Requisition", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }

        else
        {
            // Cache data not exist.
            callJsonArrayRequest();
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
            case R.id.fab_add:
            {
                final AlertDialog.Builder alert = new AlertDialog.Builder(AllPurchaseRequisition.this,android.R.style.Theme_Translucent_NoTitleBar);
                // Set an EditText view to get user input

                dialogView = LayoutInflater.from(AllPurchaseRequisition.this).inflate(R.layout.dialog_new_purchase_requisition, null);
                alert.setView(dialogView);

                show = alert.show();


                final BetterSpinner spinner_department = (BetterSpinner) dialogView.findViewById(R.id.spinner_department);

                Button createBtn = (Button) dialogView.findViewById(R.id.createBtn);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(AllPurchaseRequisition.this,
                            android.R.layout.simple_dropdown_item_1line, new String[]{"Electrical", "Mechanical", "Civil",
                "Architectural"});
                spinner_department.setAdapter(adapter);

                createBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(spinner_department.getText().toString().isEmpty())
                        {
                            spinner_department.setError("Field cannot be empty");
                        }

                        else
                        {
                            final String departmentText = spinner_department.getText().toString();
                            saveData(departmentText);

                        }
                    }
                });
            }
            break;
            case R.id.fab_search:
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Search Purchase Requisition by Department or ID !");
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
                            Intent intent = new Intent(AllPurchaseRequisition.this, AllPurchaseRequisition.class);
                            intent.putExtra("search", "yes");
                            intent.putExtra("searchText", input.getText().toString());

                            Log.d("SEARCH TEXT", input.getText().toString());

                            startActivity(intent);
                        }
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(AllPurchaseRequisition.this, "Search cancelled .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
            }
            break;
            case R.id.exportBtn:

            {
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP) {
                    if (ContextCompat.checkSelfPermission(AllPurchaseRequisition.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(AllPurchaseRequisition.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                    }
                    Environment.getExternalStorageState();

                    String department = null, id = null, createdDate = null, createdBy = null, isApproved = null;
                    int listSize = purchaseList.size();
                    String cvsValues = "ID" + ","+ "Department" + ","+ "Created Date" + ","+ "Created By"+ ","+ "Approved\n";

                    for(int i=0; i<listSize;i++)
                    {
                        AllPurchaseRequisitionList items = purchaseList.get(i);
                        id = items.getPr_sl_no();
                        department = items.getText_department();
                        createdDate = items.getText_created_on();
                        createdBy = items.getText_created_by();
                        isApproved = items.getApproved();

                        cvsValues = cvsValues +  id + ","+ department + ","+ createdDate + ","+ createdBy + ","+ isApproved + "\n";
                    }

                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "AllPR-data.csv", cvsValues);
                }

                else

                {
                    Environment.getExternalStorageState();

                    String department = null, id = null, createdDate = null, createdBy = null, isApproved = null;
                    int listSize = purchaseList.size();
                    String cvsValues = "ID" + ","+ "Department" + ","+ "Created Date" + ","+ "Created By"+ ","+ "Approved\n";

                    for(int i=0; i<listSize;i++)
                    {
                        AllPurchaseRequisitionList items = purchaseList.get(i);
                        id = items.getPr_sl_no();
                        department = items.getText_department();
                        createdDate = items.getText_created_on();
                        createdBy = items.getText_created_by();
                        isApproved = items.getApproved();

                        cvsValues = cvsValues +  id + ","+ department + ","+ createdDate + ","+ createdBy + ","+ isApproved + "\n";
                    }


                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "AllPR-data.csv", cvsValues);

                }

            }
            break;
        }
    }

    private void callJsonArrayRequest() {
        // TODO Auto-generated method stub

        pDialog = new ProgressDialog(AllPurchaseRequisition.this);
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
                                id = dataObject.getString("id");
                                department = dataObject.getString("department");
                                createdBy = dataObject.getString("createdBy");
                                createdDate = dataObject.getString("createdDate");
                                isApproved = dataObject.getString("approved");
                                isPo = dataObject.getString("isPo");

                                Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(createdDate);
                                createdDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);


                                if (getIntent().hasExtra("search"))
                                {
                                    if (getIntent().getStringExtra("search").equals("yes")) {

                                        if (id.toLowerCase().contains(searchText.toLowerCase()) || department.toLowerCase().contains(searchText.toLowerCase())) {
                                            items = new AllPurchaseRequisitionList(String.valueOf(i+1), id, department,
                                                    createdDate, createdBy, isApproved, isPo);
                                            purchaseList.add(items);

                                            purchaseAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                                else
                                { items = new AllPurchaseRequisitionList(String.valueOf(i+1), id, department,
                                        createdDate, createdBy, isApproved, isPo);
                                    purchaseList.add(items);

                                    purchaseAdapter.notifyDataSetChanged();
                                }
                            }
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
    public void saveData(final String departmentName)
    {
        JSONObject object = new JSONObject();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String currentDate = sdf.format(c.getTime());

        try {
            object.put("projectId", currentProjectNo);
            object.put("department", departmentName);
            object.put("createdBy", currentUser);
            object.put("createdDate", currentDate);
            object.put("approved", "0");

            Log.d("JSON SENT", object.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = pm.getString("SERVER_URL") + "/postPurchaseRequisition";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("JSON RESPONSE", response.toString());

                            if(response.getString("msg").equals("success"))
                            {
                                Toast.makeText(AllPurchaseRequisition.this, "Purchase Requisition Created. ID - " + response.getString("data"), Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(AllPurchaseRequisition.this, AllPurchaseRequisition.class);
                                pm.putString("currentPr", response.getString("data"));
                                pm.putString("departmentPr", departmentName);
                                pm.putString("createdOnPr", currentDate);
                                pm.putString("createdByPr", currentUser);
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

            Boolean createPurchaseRequisitionPending = pm.getBoolean("createPurchaseRequisitionPending");

            if(createPurchaseRequisitionPending)
            {
                Toast.makeText(AllPurchaseRequisition.this, "Already a Purchase Requisition creation is in progress. Please try after sometime.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(AllPurchaseRequisition.this, "Internet not currently available. Purchase Requisition will automatically get created on internet connection.", Toast.LENGTH_SHORT).show();

                pm.putString("objectPurchaseRequisition", object.toString());
                pm.putString("urlPurchaseRequisition", url);
                pm.putString("toastMessagePurchaseRequisition", "Purchase Requisition Created");
                pm.putBoolean("createPurchaseRequisitionPending", true);
            }

            if(pDialog!=null)
                pDialog.dismiss();

            Intent intent = new Intent(AllPurchaseRequisition.this, AllPurchaseRequisition.class);
            startActivity(intent);
        }
        else
        {
            requestQueue.add(jor);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AllPurchaseRequisition.this, SiteProcurementActivity.class);
        startActivity(intent);
    }
}