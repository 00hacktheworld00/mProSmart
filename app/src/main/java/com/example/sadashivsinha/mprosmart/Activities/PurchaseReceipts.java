package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.Adapters.MyAdapter;
import com.example.sadashivsinha.mprosmart.Adapters.PurchaseReceiptsAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.PurchaseReceiptList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PurchaseReceipts extends NewActivity {
    private List<PurchaseReceiptList> purchaseReceiptList = new ArrayList<>();
    RecyclerView recyclerView;
    private PurchaseReceiptsAdapter purchaseReceiptsAdapter;
    TextView text_receipt_no, text_po_number, text_vendor_code, text_project_no, text_date;
    public static final String MyPREFERENCES = "MyPrefs" ;
    JSONArray dataArray;
    JSONObject dataObject;
    String itemId, quantity, maxQuantity, balance, date;
    ConnectionDetector cd;
    public static final String TAG = PurchaseReceipts.class.getSimpleName();
    Boolean isInternetPresent = false;
    PurchaseReceiptList items;
    ProgressDialog pDialog, pDialog1;
    String currentProjectNo, currentReceiptNo, purchaseReceiptId, purchaseOrderId, projectId, vendorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_receipts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final PreferenceManager pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentReceiptNo = pm.getString("receiptNo");

        text_receipt_no = (TextView) findViewById(R.id.text_receipt_no);
        text_po_number = (TextView) findViewById(R.id.text_po_number);
        text_vendor_code = (TextView) findViewById(R.id.text_vendor_code);
        text_project_no = (TextView) findViewById(R.id.text_project_no);
        text_date = (TextView) findViewById(R.id.text_date);

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


        pDialog1 = new ProgressDialog(PurchaseReceipts.this);
        pDialog1.setMessage("Preparing Header...");
        pDialog1.setIndeterminate(false);
        pDialog1.setCancelable(true);
        pDialog1.show();

        prepareHeader();


        pDialog = new ProgressDialog(PurchaseReceipts.this);
        pDialog.setMessage("Getting Data ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
        class MyTask extends AsyncTask<Void, Void, Void> {

            @Override protected void onPreExecute() {

                purchaseReceiptsAdapter = new PurchaseReceiptsAdapter(purchaseReceiptList);
                recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                recyclerView.setLayoutManager(new LinearLayoutManager(PurchaseReceipts.this));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(purchaseReceiptsAdapter);
            }

            @Override
            protected Void doInBackground(Void... params) {
                prepareItems();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                purchaseReceiptsAdapter.notifyDataSetChanged();
            }

        }

        new MyTask().execute();

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

    public void prepareItems()
    {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = getResources().getString(R.string.server_url) + "/getPurchaseReceiptItems?purchaseReceiptId=\""+currentReceiptNo+"\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(PurchaseReceipts.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);

                                        purchaseReceiptId = dataObject.getString("purchaseReceiptId");
                                        itemId = dataObject.getString("itemId");
                                        quantity = dataObject.getString("quantity");
                                        maxQuantity = dataObject.getString("maxQuantity");
                                        balance = dataObject.getString("balance");
                                        date = dataObject.getString("date");

                                        items = new PurchaseReceiptList(itemId,quantity,maxQuantity,balance,date);
                                        purchaseReceiptList.add(items);

                                        purchaseReceiptsAdapter.notifyDataSetChanged();
                                }
                            }

                            pDialog.dismiss();

                        }catch(JSONException e){e.printStackTrace();}
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

    public void prepareHeader()
    {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = getResources().getString(R.string.server_url) + "/getPurchaseReceipts?projectId=\""+currentProjectNo+"\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(PurchaseReceipts.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    purchaseReceiptId = dataObject.getString("purchaseReceiptId");

                                    if(purchaseReceiptId.equals(currentReceiptNo))
                                    {
                                        purchaseOrderId = dataObject.getString("purchaseOrderId");
                                        projectId = dataObject.getString("projectId");
                                        vendorId = dataObject.getString("vendorId");
                                        date = dataObject.getString("date");

                                        text_receipt_no.setText(currentReceiptNo);
                                        text_po_number.setText(purchaseOrderId);
                                        text_vendor_code.setText(vendorId);
                                        text_project_no.setText(projectId);
                                        text_date.setText(date);

                                    }
                                }
                            }

                            pDialog1.dismiss();

                        }catch(JSONException e){e.printStackTrace();}
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
    public void onBackPressed() {
        Intent intent = new Intent(PurchaseReceipts.this, PurchaseReceiptsNew.class);
        startActivity(intent);
    }

}
