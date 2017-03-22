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
import com.example.sadashivsinha.mprosmart.Adapters.InvoiceListAdapter;
import com.example.sadashivsinha.mprosmart.Adapters.MyAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.MomList;
import com.example.sadashivsinha.mprosmart.ModelLists.PurchaseOrdersList;
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
import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class AllInvoices extends NewActivity implements View.OnClickListener  {
    private List<MomList> momList = new ArrayList<>();
    private RecyclerView recyclerView;
    private InvoiceListAdapter invoiceListAdapter;
    String flag = "false";
    JSONArray dataArray;
    JSONObject dataObject;
    String invoiceId, vendorInvoiceNo, purchaseOrderNumber, projectId, createdBy, createdDate, vendorId;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    public static final String TAG = AllInvoices.class.getSimpleName();
    MomList items;
    private ProgressDialog pDialog;
    String currentProjectNo;
    String url, searchText;
    PreferenceManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_inovices);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getIntent().hasExtra("search")) {
            if (getIntent().getStringExtra("search").equals("yes")) {

                searchText = getIntent().getStringExtra("searchText");

                getSupportActionBar().setTitle("Invoice Search Results : " + searchText);
            }
        }

        pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        url = pm.getString("SERVER_URL") + "/getAllInvoice?projectId=\""+currentProjectNo+"\"";

        invoiceListAdapter = new InvoiceListAdapter(momList);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(AllInvoices.this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(invoiceListAdapter);

        // check for Internet status
        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(AllInvoices.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(AllInvoices.this);
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
                            projectId = dataObject.getString("projectId");

                            if (projectId.equals(currentProjectNo)) {
                                invoiceId = dataObject.getString("invoiceId");
                                vendorInvoiceNo = dataObject.getString("vendorInvoiceNo");
                                vendorId = dataObject.getString("vendorId");
                                purchaseOrderNumber = dataObject.getString("purchaseOrderNumber");
                                createdBy = dataObject.getString("createdBy");
                                createdDate = dataObject.getString("createdDate");

                                if (getIntent().hasExtra("search"))
                                {
                                    if (getIntent().getStringExtra("search").equals("yes")) {

                                        if (invoiceId.toLowerCase().contains(searchText.toLowerCase()) ||
                                                vendorInvoiceNo.toLowerCase().contains(searchText.toLowerCase())){

                                            items = new MomList(String.valueOf(i + 1), invoiceId, vendorInvoiceNo, vendorId, purchaseOrderNumber, createdDate, createdBy);
                                            momList.add(items);
                                            invoiceListAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                                else
                                {
                                    items = new MomList(String.valueOf(i + 1), invoiceId, vendorInvoiceNo, vendorId, purchaseOrderNumber, createdDate, createdBy);
                                    momList.add(items);
                                    invoiceListAdapter.notifyDataSetChanged();
                                }


                                pDialog.dismiss();
                            }
                        }

                        Boolean createInvoicePending = pm.getBoolean("createInvoicePending");

                        if (createInvoicePending) {

                            String jsonObjectVal = pm.getString("objectInvoice");
                            Log.d("JSON QIR PENDING :", jsonObjectVal);

                            JSONObject jsonObjectPending = new JSONObject(jsonObjectVal);
                            Log.d("JSONObj QIR PENDING :", jsonObjectPending.toString());

                            vendorInvoiceNo = dataObject.getString("vendorInvoiceNo");
                            vendorId = dataObject.getString("vendorId");
                            purchaseOrderNumber = dataObject.getString("purchaseOrderNumber");
                            createdBy = dataObject.getString("createdBy");
                            createdDate = dataObject.getString("createdDate");

                            items = new MomList(String.valueOf(dataArray.length()+1),  getResources().getString(R.string.waiting_to_connect) , vendorInvoiceNo, vendorId, purchaseOrderNumber, createdDate, createdBy);
                            momList.add(items);
                            invoiceListAdapter.notifyDataSetChanged();
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
                Toast.makeText(AllInvoices.this, "Offline Data Not available for this Invoice", Toast.LENGTH_SHORT).show();
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

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.exportBtn:
            {
                //csv export
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP) {
                    if (ContextCompat.checkSelfPermission(AllInvoices.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(AllInvoices.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                    }
                    Environment.getExternalStorageState();

                    String invoice_no = null, ven_invoice_no = null, vendor_code = null, po_number = null, date = null, created_by = null;
                    int listSize = momList.size();
                    String cvsValues = "Invoice No." + ","+ "Vendor Invoice No." + ","+ "Vendor Code"  + ","+ "PO Number" + ","+ "Date"  + ","+ "Created By" + "\n";

                    for(int i=0; i<listSize;i++)
                    {
                        MomList items = momList.get(i);
                        invoice_no = items.getInvoice_no();
                        ven_invoice_no = items.getVen_invoice_no();
                        vendor_code = items.getVendor_code();
                        po_number = items.getPo_number();
                        date = items.getDate();
                        created_by = items.getCreated_by();

                        cvsValues = cvsValues +  invoice_no + ","+ ven_invoice_no + ","+ vendor_code +","+ po_number +","+ date +","+ created_by + "\n";
                    }

                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "Invoice-data.csv", cvsValues);
                }

                else

                {
                    Environment.getExternalStorageState();

                    String invoice_no = null, ven_invoice_no = null, vendor_code = null, po_number = null, date = null, created_by = null;
                    int listSize = momList.size();
                    String cvsValues = "Invoice No." + ","+ "Vendor Invoice No." + ","+ "Vendor Code"  + ","+ "PO Number" + ","+ "Date"  + ","+ "Created By" + "\n";

                    for(int i=0; i<listSize;i++)
                    {
                        MomList items = momList.get(i);
                        invoice_no = items.getInvoice_no();
                        ven_invoice_no = items.getVen_invoice_no();
                        vendor_code = items.getVendor_code();
                        po_number = items.getPo_number();
                        date = items.getDate();
                        created_by = items.getCreated_by();

                        cvsValues = cvsValues +  invoice_no + ","+ ven_invoice_no + ","+ vendor_code +","+ po_number +","+ date +","+ created_by + "\n";
                    }

                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "Invoice-data.csv", cvsValues);
                }

            }
            break;

            case R.id.fab_add:
            {
                Intent intent = new Intent(AllInvoices.this, InvoiceCreate.class);
                startActivity(intent);
            }
            break;
            case R.id.fab_search:
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Search Invoices by ID or Vendor Invoice ID !");
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
                            Intent intent = new Intent(AllInvoices.this, AllInvoices.class);
                            intent.putExtra("search", "yes");
                            intent.putExtra("searchText", input.getText().toString());

                            Log.d("SEARCH TEXT", input.getText().toString());

                            startActivity(intent);
                        }
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(AllInvoices.this, "Search cancelled .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
            }
            break;
        }
    }
    private void callJsonArrayRequest() {
        // TODO Auto-generated method stub

        pDialog = new ProgressDialog(AllInvoices.this);
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
                                projectId = dataObject.getString("projectId");

                                if(projectId.equals(currentProjectNo))
                                {
                                    invoiceId = dataObject.getString("invoiceId");
                                    vendorInvoiceNo = dataObject.getString("vendorInvoiceNo");
                                    vendorId = dataObject.getString("vendorId");
                                    purchaseOrderNumber = dataObject.getString("purchaseOrderNumber");
                                    createdBy = dataObject.getString("createdBy");
                                    createdDate = dataObject.getString("createdDate");

                                    if (getIntent().hasExtra("search"))
                                    {
                                        if (getIntent().getStringExtra("search").equals("yes")) {

                                            if (invoiceId.toLowerCase().contains(searchText.toLowerCase()) ||
                                                    vendorInvoiceNo.toLowerCase().contains(searchText.toLowerCase())){

                                                items = new MomList(String.valueOf(i + 1), invoiceId, vendorInvoiceNo, vendorId, purchaseOrderNumber, createdDate, createdBy);
                                                momList.add(items);
                                                invoiceListAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                    else
                                    {
                                        items = new MomList(String.valueOf(i + 1), invoiceId, vendorInvoiceNo, vendorId, purchaseOrderNumber, createdDate, createdBy);
                                        momList.add(items);
                                        invoiceListAdapter.notifyDataSetChanged();
                                    }
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
        Intent intent = new Intent(AllInvoices.this, SiteProcurementActivity.class);
        startActivity(intent);
    }

}