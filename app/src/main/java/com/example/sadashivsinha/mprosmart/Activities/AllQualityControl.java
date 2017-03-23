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
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
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
import com.example.sadashivsinha.mprosmart.Adapters.AllQualityAdapter;
import com.example.sadashivsinha.mprosmart.Adapters.MyAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.AllSiteDiaryList;
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
import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class AllQualityControl extends NewActivity implements View.OnClickListener  {
    private List<MomList> momList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AllQualityAdapter allQualityAdapter;
    MomList items;
    JSONArray dataArray;
    JSONObject dataObject;
    String qirNo, vendorId, receiptNo, projectId, createdBy, purchaseOrderNo, purchaseReceiptId, poId;
    ConnectionDetector cd;
    public static final String TAG = PurchaseOrders.class.getSimpleName();
    Boolean isInternetPresent = false;
    private ProgressDialog pDialog;
    String currentProjectNo, currentUserId;
    View dialogView;
    AlertDialog show;
    Spinner spinner_po, spinner_receipt_no;
    String[] purchaseOrdersArray, vendorIdArray, receiptArray;
    String currentVendor;
    String matchPurchaseOrderId;
    String url, po_url, searchText;
    PreferenceManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_quality_control);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentUserId = pm.getString("userId");

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);


        recyclerView.setLayoutManager(new LinearLayoutManager(AllQualityControl.this));
        recyclerView.setHasFixedSize(true);
        allQualityAdapter = new AllQualityAdapter(momList);
        recyclerView.setAdapter(allQualityAdapter);

        url = pm.getString("SERVER_URL") + "/getAllQualityControls?projectId='"+currentProjectNo+"'";

        po_url = pm.getString("SERVER_URL") + "/getPurchaseOrders?projectId='"+currentProjectNo+"'";

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        if (getIntent().hasExtra("search")) {
            if (getIntent().getStringExtra("search").equals("yes")) {

                searchText = getIntent().getStringExtra("searchText");

                getSupportActionBar().setTitle("Quality Control Search Results : " + searchText);
            }
        }

        // check for Internet status
        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(AllQualityControl.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(AllQualityControl.this);
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
                            qirNo = dataObject.getString("qualityInspectionReportId");
                            vendorId = dataObject.getString("vendorId");
                            receiptNo = dataObject.getString("purchaseReceiptId");
                            projectId = dataObject.getString("projectId");
                            createdBy = dataObject.getString("createdBy");
                            purchaseOrderNo = dataObject.getString("purchaseOrderId");

                            if (getIntent().hasExtra("search"))
                            {
                                if (getIntent().getStringExtra("search").equals("yes")) {

                                    if (qirNo.toLowerCase().contains(searchText.toLowerCase()) || vendorId.toLowerCase().contains(searchText.toLowerCase())) {

                                        items = new MomList(String.valueOf(i+1), qirNo, vendorId, receiptNo, projectId, createdBy, purchaseOrderNo, 0);
                                        momList.add(items);

                                        allQualityAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                            else
                            {
                                items = new MomList(String.valueOf(i+1), qirNo, vendorId, receiptNo, projectId, createdBy, purchaseOrderNo, 0);
                                momList.add(items);

                                allQualityAdapter.notifyDataSetChanged();
                            }
                            pDialog.dismiss();
                        }

                        Boolean createQirPending = pm.getBoolean("createQirPending");

                        if(createQirPending)
                        {

                            String jsonObjectVal = pm.getString("objectQIR");
                            Log.d("JSON QIR PENDING :", jsonObjectVal);

                            JSONObject jsonObjectPending = new JSONObject(jsonObjectVal);
                            Log.d("JSONObj QIR PENDING :", jsonObjectPending.toString());

                            vendorId = jsonObjectPending.getString("vendorId");
                            receiptNo = jsonObjectPending.getString("receiptNo");
                            projectId = jsonObjectPending.getString("projectId");
                            createdBy = jsonObjectPending.getString("createdBy");
                            purchaseOrderNo = jsonObjectPending.getString("purchaseOrderNo");

                            items = new MomList(String.valueOf(dataArray.length()), getResources().getString(R.string.waiting_to_connect)
                                    , vendorId, receiptNo, projectId, createdBy, purchaseOrderNo, 0);
                            momList.add(items);

                            allQualityAdapter.notifyDataSetChanged();
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

            else
            {
                Toast.makeText(AllQualityControl.this, "Offline Data Not available for this QIR", Toast.LENGTH_SHORT).show();
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
        exportBtn = (FloatingActionButton) findViewById(R.id.exportBtn);
        fab_search = (FloatingActionButton) findViewById(R.id.fab_search);

        fab_add.setLabelText("Add new Quality Control");

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
                    if (ContextCompat.checkSelfPermission(AllQualityControl.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(AllQualityControl.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                    }
                    Environment.getExternalStorageState();

                    String qir_no = null, vendor_id = null, receipt_no = null, project_id = null, created_by = null, purchase_order = null;
                    int listSize = momList.size();
                    String cvsValues = "QIR No." + ","+ "Vendor ID" + ","+ "Receipt No." + ","+ "Project ID" + ","+ "Created By" + ","+ "Purchase Order" + "\n";

                    for(int i=0; i<listSize;i++)
                    {
                        MomList items = momList.get(i);
                        qir_no = items.getPo_number();
                        vendor_id = items.getVendor_id();
                        receipt_no = items.getReceipt_no();
                        project_id = items.getProject_id();
                        created_by = items.getCreated_by();
                        purchase_order = items.getPurchase_order();

                        cvsValues = cvsValues +  qir_no + ","+ vendor_id + ","+ receipt_no + ","+ project_id +","+ created_by +","+ purchase_order + "\n";
                    }

                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "QualityControl-data.csv", cvsValues);
                }

                else

                {
                    Environment.getExternalStorageState();

                    String qir_no = null, vendor_id = null, receipt_no = null, project_id = null, created_by = null, purchase_order = null;
                    int listSize = momList.size();
                    String cvsValues = "PO No." + ","+ "Vendor Code" + ","+ "Created On" + ","+ "Created By" + "\n";

                    for(int i=0; i<listSize;i++)
                    {
                        MomList items = momList.get(i);
                        qir_no = items.getPo_number();
                        vendor_id = items.getVendor_id();
                        receipt_no = items.getReceipt_no();
                        project_id = items.getProject_id();
                        created_by = items.getCreated_by();
                        purchase_order = items.getPurchase_order();

                        cvsValues = cvsValues +  qir_no + ","+ vendor_id + ","+ receipt_no + ","+ project_id +","+ created_by +","+ purchase_order + "\n";
                    }

                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "QualityControl-data.csv", cvsValues);
                }

            }
            break;
            case R.id.fab_add:
            {
//                Intent intent = new Intent(AllQualityControl.this, QualityControlCreate.class);
//                startActivity(intent);



                final AlertDialog.Builder alert = new AlertDialog.Builder(AllQualityControl.this,android.R.style.Theme_Translucent_NoTitleBar);
                // Set an EditText view to get user input

                dialogView = LayoutInflater.from(AllQualityControl.this).inflate(R.layout.dialog_new_quality_control, null);
                alert.setView(dialogView);

                show = alert.show();

                spinner_po = (Spinner) dialogView.findViewById(R.id.spinner_po);
                spinner_receipt_no = (Spinner) dialogView.findViewById(R.id.spinner_receipt_no);


                Button createBtn = (Button) dialogView.findViewById(R.id.createBtn);

                if (!isInternetPresent)
                {
                    // Internet connection is not present
                    // Ask user to connect to Internet
                    RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_layout);
                    Crouton.cancelAllCroutons();
                    Crouton.makeText(AllQualityControl.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

                    pDialog = new ProgressDialog(AllQualityControl.this);
                    pDialog.setMessage("Getting cache data");
                    pDialog.show();

                    Cache cache = AppController.getInstance().getRequestQueue().getCache();
                    Cache.Entry entry = cache.get(po_url);
                    if (entry != null) {
                        //Cache data available.
                        try {
                            String data = new String(entry.data, "UTF-8");
                            Log.d("CACHE DATA", data);
                            JSONObject jsonObject = new JSONObject(data);
                            try {
                                dataArray = jsonObject.getJSONArray("data");
                                purchaseOrdersArray = new String[dataArray.length()+1];
                                vendorIdArray = new String[dataArray.length()+1];

                                purchaseOrdersArray[0]="Select Purchase Orders";
                                vendorIdArray[0]="Select PO";
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    poId = dataObject.getString("purchaseOrderId");
                                    vendorId = dataObject.getString("vendorId");

                                    purchaseOrdersArray[i+1]=poId;
                                    vendorIdArray[i+1] = vendorId;
                                }
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(AllQualityControl.this,
                                        android.R.layout.simple_dropdown_item_1line,purchaseOrdersArray);
                                spinner_po.setAdapter(adapter);
                                pDialog.dismiss();

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
                        Toast.makeText(AllQualityControl.this, "Offline Data Not available for PO", Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                    }
                }

                else
                {
                    // Cache data not exist.
                    preparePurchaseOrderList();
                }

                spinner_po.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(position==0)
                        {
                            spinner_receipt_no.setVisibility(View.GONE);
                            currentVendor = vendorIdArray[position];

                        }
                        else
                        {
                            final String currentPo = spinner_po.getSelectedItem().toString();
                            String receiptUrl = pm.getString("SERVER_URL") + "/getPurchaseReceipts?projectId=\""+currentProjectNo+"\"";

                            if (!isInternetPresent)
                            {
                                // Internet connection is not present
                                // Ask user to connect to Internet
                                RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_layout);
                                Crouton.cancelAllCroutons();
                                Crouton.makeText(AllQualityControl.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

                                pDialog = new ProgressDialog(AllQualityControl.this);
                                pDialog.setMessage("Getting cache data");
                                pDialog.show();

                                Cache cache = AppController.getInstance().getRequestQueue().getCache();
                                Cache.Entry entry = cache.get(receiptUrl);
                                if (entry != null) {
                                    //Cache data available.
                                    try {
                                        String data = new String(entry.data, "UTF-8");
                                        Log.d("CACHE DATA", data);
                                        JSONObject jsonObject = new JSONObject(data);
                                        try {
                                            dataArray = jsonObject.getJSONArray("data");
                                            int j=0;

                                            for(int i=0; i<dataArray.length();i++)
                                            {
                                                dataObject = dataArray.getJSONObject(i);
                                                matchPurchaseOrderId = dataObject.getString("purchaseOrderId");

                                                if(matchPurchaseOrderId.equals(currentPo))
                                                {
                                                    j++;
                                                }
                                            }

                                            receiptArray = new String[j+1];
                                            receiptArray[0]="No Purchase Receipt";

                                            j=0;

                                            for(int i=0; i<dataArray.length();i++)
                                            {
                                                dataObject = dataArray.getJSONObject(i);
                                                matchPurchaseOrderId = dataObject.getString("purchaseOrderId");

                                                if(matchPurchaseOrderId.equals(currentPo))
                                                {
                                                    purchaseReceiptId = dataObject.getString("purchaseReceiptId");
                                                    receiptArray[j+1] = purchaseReceiptId;
                                                    j++;
                                                }
                                            }

                                            if(j==0)
                                            {
                                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(AllQualityControl.this,
                                                        android.R.layout.simple_dropdown_item_1line,new String[] {"No Purchase Receipt"});
                                                spinner_receipt_no.setAdapter(adapter);
                                            }
                                            else
                                            {
                                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(AllQualityControl.this,
                                                        android.R.layout.simple_dropdown_item_1line,receiptArray);
                                                receiptArray[0]="Select Purchase Receipt";

                                                spinner_receipt_no.setAdapter(adapter);
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

                                else
                                {
                                    Toast.makeText(AllQualityControl.this, "Offline Data Not available for Receipt", Toast.LENGTH_SHORT).show();
                                    pDialog.dismiss();
                                }
                            }

                            else
                            {
                                // Cache data not exist.
                                prepareReceiptNos(currentPo, receiptUrl);
                            }
                            spinner_receipt_no.setVisibility(View.VISIBLE);
                            currentVendor = vendorIdArray[position];
                            Log.d("current Vendor :", currentVendor);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                createBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(spinner_po.getSelectedItem().toString().equals("Select Purchase Orders"))
                        {
                            Toast.makeText(AllQualityControl.this, "Select PO First", Toast.LENGTH_SHORT).show();
                        }
                        else if(spinner_receipt_no.getSelectedItem().toString().equals("Select Purchase Receipt"))
                        {
                            Toast.makeText(AllQualityControl.this, "Select Receipt First", Toast.LENGTH_SHORT).show();
                        }
                        else if(spinner_receipt_no.getSelectedItem().toString().equals("No Purchase Receipt Found."))
                        {
                            Toast.makeText(AllQualityControl.this, "No Receipts in this Purchase Order", Toast.LENGTH_SHORT).show();
                        }

                        else {

                            purchaseOrderNo = spinner_po.getSelectedItem().toString();
                            receiptNo = spinner_receipt_no.getSelectedItem().toString();
                            projectId = currentProjectNo;
                            createdBy = currentUserId;

                            saveData();

                        }
                    }
                });



            }
            break;
            case R.id.fab_search:
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Search Quality Control by QIR No or Vendor ID!");
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
                            Intent intent = new Intent(AllQualityControl.this, AllSiteDiary.class);
                            intent.putExtra("search", "yes");
                            intent.putExtra("searchText", input.getText().toString());

                            Log.d("SEARCH TEXT", input.getText().toString());

                            startActivity(intent);
                        }
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(AllQualityControl.this, "Search cancelled .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
            }
            break;
        }
    }

    public void saveData()
    {
        JSONObject object = new JSONObject();

        try {
            object.put("purchaseReceiptId",receiptNo);
            object.put("purchaseOrderId",purchaseOrderNo);
            object.put("projectId",currentProjectNo);
            object.put("createdBy",createdBy);
            object.put("vendorId",currentVendor);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(AllQualityControl.this);

        String url = AllQualityControl.this.pm.getString("SERVER_URL") + "/postQualityInspection";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("msg").equals("success"))
                            {
                                String successMsg = "Quality Control Created. ID - "+ response.getString("data");
                                Toast.makeText(AllQualityControl.this, successMsg, Toast.LENGTH_SHORT).show();

                                updateReceiptIsSelected(pDialog, response.getString("data"), receiptNo);

                                Intent intent = new Intent(AllQualityControl.this, AllQualityControl.class);
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
                        Toast.makeText(AllQualityControl.this, error.toString(), Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
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

            Boolean createQirPending = pm.getBoolean("createQirPending");

            if(createQirPending)
            {
                Toast.makeText(AllQualityControl.this, "Already a QIR creation is in progress. Please try after sometime.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(AllQualityControl.this, "Internet not currently available. QIR will automatically get created on internet connection.", Toast.LENGTH_SHORT).show();

                pm.putString("objectQIR", object.toString());
                pm.putString("urlQIR", url);
                pm.putString("toastMessageQIR", "QIR Created");
                pm.putBoolean("createQirPending", true);

                Intent intent = new Intent(AllQualityControl.this, AllQualityControl.class);
                startActivity(intent);
            }
        }
        else
        {
            requestQueue.add(jor);
        }
        if(pDialog!=null)
            pDialog.dismiss();
    }

    public void updateReceiptIsSelected(final ProgressDialog pDialog, String currentQir, String currentReceiptNo)
    {
        JSONObject object = new JSONObject();

        try {
            object.put("qualityInspectionReportId", currentQir);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(AllQualityControl.this);

        String url = AllQualityControl.this.pm.getString("SERVER_URL") + "/putPurchaseReceiptQirIs?purchaseReceiptId=\""+ currentReceiptNo + "\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.PUT, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("msg").equals("success"))
                            {
                                Intent intent = new Intent(AllQualityControl.this, AllQualityControl.class);
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
                        Toast.makeText(AllQualityControl.this, error.toString(), Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                    }
                }
        );
        requestQueue.add(jor);
    }

    private void callJsonArrayRequest() {
        // TODO Auto-generated method stub

        pDialog = new ProgressDialog(AllQualityControl.this);
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
                                poId = dataObject.getString("purchaseOrderId");
                                qirNo = dataObject.getString("qualityInspectionReportId");
                                vendorId = dataObject.getString("vendorId");
                                receiptNo = dataObject.getString("purchaseReceiptId");
                                projectId = dataObject.getString("projectId");
                                createdBy = dataObject.getString("createdBy");
                                purchaseOrderNo = dataObject.getString("purchaseOrderId");

                                if (getIntent().hasExtra("search"))
                                {
                                    if (getIntent().getStringExtra("search").equals("yes")) {

                                        if (qirNo.toLowerCase().contains(searchText.toLowerCase()) || vendorId.toLowerCase().contains(searchText.toLowerCase())) {

                                            items = new MomList(String.valueOf(i+1), qirNo, vendorId, receiptNo, projectId, createdBy, purchaseOrderNo, 0);
                                            momList.add(items);

                                            allQualityAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                                else
                                {
                                    items = new MomList(String.valueOf(i+1), qirNo, vendorId, receiptNo, projectId, createdBy, purchaseOrderNo, 0);
                                    momList.add(items);
                                }

                                allQualityAdapter.notifyDataSetChanged();
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

    private void preparePurchaseOrderList() {
        // TODO Auto-generated method stub

        pDialog = new ProgressDialog(AllQualityControl.this);
        pDialog.setMessage("Getting server data");
        pDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, po_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(AllQualityControl.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                purchaseOrdersArray = new String[dataArray.length()+1];
                                vendorIdArray = new String[dataArray.length()+1];

                                purchaseOrdersArray[0]="Select Purchase Orders";
                                vendorIdArray[0]="Select PO";
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    poId = dataObject.getString("purchaseOrderId");
                                    vendorId = dataObject.getString("vendorId");

                                    purchaseOrdersArray[i+1]=poId;
                                    vendorIdArray[i+1] = vendorId;
                                }
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(AllQualityControl.this,
                                        android.R.layout.simple_dropdown_item_1line,purchaseOrdersArray);
                                spinner_po.setAdapter(adapter);

                                pDialog.dismiss();

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


    public void prepareReceiptNos(final String poNumber, String receiptUrl)
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);


        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, receiptUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");
                            String msg = response.getString("msg");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(AllQualityControl.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                Log.d("current PO : ", poNumber);

                                Log.d("response full : ", response.toString());

                                dataArray = response.getJSONArray("data");

                                int j=0;

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    matchPurchaseOrderId = dataObject.getString("purchaseOrderId");

                                    if(matchPurchaseOrderId.equals(poNumber))
                                    {
                                        j++;
                                    }
                                }

                                receiptArray = new String[j+1];
                                receiptArray[0]="No Purchase Receipt";

                                j=0;

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    matchPurchaseOrderId = dataObject.getString("purchaseOrderId");

                                    if(matchPurchaseOrderId.equals(poNumber))
                                    {
                                        purchaseReceiptId = dataObject.getString("purchaseReceiptId");
                                        receiptArray[j+1] = purchaseReceiptId;
                                        j++;
                                    }
                                }

                                if(j==0)
                                {
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(AllQualityControl.this,
                                            android.R.layout.simple_dropdown_item_1line,new String[] {"No Purchase Receipt"});
                                    spinner_receipt_no.setAdapter(adapter);
                                }
                                else
                                {
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(AllQualityControl.this,
                                            android.R.layout.simple_dropdown_item_1line,receiptArray);
                                    receiptArray[0]="Select Purchase Receipt";

                                    spinner_receipt_no.setAdapter(adapter);
                                }
                                pDialog.dismiss();
                            }
                            if(msg.equals("No data"))
                            {
                                Toast.makeText(AllQualityControl.this, "No Purchase Receipt Found.", Toast.LENGTH_LONG).show();
                                pDialog.dismiss();
                                finish();
                            }

                        }catch(JSONException e){e.printStackTrace();
                            pDialog.dismiss();}
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        Log.e("Volley","Error");

                    }
                }
        );
        requestQueue.add(jor);
    }



    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AllQualityControl.this, QualityControlMain.class);
        intent.putExtra("projectNo","1");
        startActivity(intent);
    }

}