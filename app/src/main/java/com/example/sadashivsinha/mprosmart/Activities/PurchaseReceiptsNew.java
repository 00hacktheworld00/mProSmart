package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.sadashivsinha.mprosmart.Adapters.MyAdapter;
import com.example.sadashivsinha.mprosmart.Adapters.PurchaseReceiptsNewAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.PurchaseOrdersList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.AppController;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class PurchaseReceiptsNew extends NewActivity implements View.OnClickListener  {

    private List<PurchaseOrdersList> PurchaseOrdersList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PurchaseReceiptsNewAdapter PurchaseReceiptsNewAdapter;
    TextView po_number, project_no, vendor_code, item_date;
    String purchaseReceiptId, purchaseOrderId, date;
    JSONArray dataArray;
    JSONObject dataObject;
    String poId, vendorCode, createdOn, createdBy;
    ConnectionDetector cd;
    public static final String TAG = PurchaseReceiptsNew.class.getSimpleName();
    Boolean isInternetPresent = false;
    private ProgressDialog pDialog, pDialog1, pDialog2;
    com.github.clans.fab.FloatingActionMenu menu;
    String currentPurchaseOrderNo;
    PurchaseOrdersList items;

    String[] purchaseOrdersArray;

    FloatingActionButton fab_search,fab_new_receipt;

    String currentProjectNo;
    PreferenceManager pm;

    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_receipts_new);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pm = new PreferenceManager(getApplicationContext());
        currentPurchaseOrderNo=pm.getString("poNumber");
        currentProjectNo = pm.getString("projectId");

        url = pm.getString("SERVER_URL") + "/getPurchaseReceipts?projectId=\""+currentProjectNo+"\"";


        po_number = (TextView) findViewById(R.id.po_number);
        project_no = (TextView) findViewById(R.id.project_no);
        vendor_code = (TextView) findViewById(R.id.vendor_code);
        item_date = (TextView) findViewById(R.id.item_date);


        PurchaseReceiptsNewAdapter = new PurchaseReceiptsNewAdapter(PurchaseOrdersList);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(PurchaseReceiptsNewAdapter);

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(PurchaseReceiptsNew.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(PurchaseReceiptsNew.this);
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
                            purchaseOrderId = dataObject.getString("purchaseOrderId");
                            if(purchaseOrderId.equals(currentPurchaseOrderNo))
                            {
                                purchaseReceiptId = dataObject.getString("purchaseReceiptId");
                                date = dataObject.getString("date");

                                Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
                                date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                items = new PurchaseOrdersList(purchaseReceiptId, purchaseOrderId,
                                        currentProjectNo, date, String.valueOf(i+1),0);
                                PurchaseOrdersList.add(items);

                                PurchaseReceiptsNewAdapter.notifyDataSetChanged();
                            }
                        }

                        Boolean createPrPending = pm.getBoolean("createPrPending");

                        if(createPrPending)
                        {

                            String jsonObjectVal = pm.getString("objectPR");
                            Log.d("JSON PR PENDING :", jsonObjectVal);

                            JSONObject jsonObjectPending = new JSONObject(jsonObjectVal);
                            Log.d("JSONObj PR PENDING :", jsonObjectPending.toString());

                            purchaseOrderId = jsonObjectPending.getString("purchaseOrderId");
                            date = jsonObjectPending.getString("date");

                            Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
                            date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                            items = new PurchaseOrdersList(getResources().getString(R.string.waiting_to_connect),
                                    purchaseOrderId, currentProjectNo, date, String.valueOf(dataArray.length()),0);

                            PurchaseOrdersList.add(items);

                            PurchaseReceiptsNewAdapter.notifyDataSetChanged();
                        }

                        pDialog.dismiss();
                    } catch (ParseException | JSONException e) {
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
                Toast.makeText(PurchaseReceiptsNew.this, "Offline Data Not available for these Purchase Receipts", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }

        else
        {
            // Cache data not exist.
            callJsonArrayRequest();
        }

        prepareHeader();


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

        fab_search = (FloatingActionButton) findViewById(R.id.fab_search);
        fab_new_receipt = (FloatingActionButton) findViewById(R.id.fab_new_receipt);


        fab_search.setOnClickListener(this);
        fab_new_receipt.setOnClickListener(this);

        menu = (FloatingActionMenu) findViewById(R.id.menu);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.fab_search:
            {

                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Search");
                // Set an EditText view to get user input
                final EditText input = new EditText(this);
                alert.setView(input);
                alert.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(PurchaseReceiptsNew.this, "Search for it .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(PurchaseReceiptsNew.this, "Search cancelled .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();

            }
            break;
            case R.id.fab_new_receipt:
            {
                Intent intent = new Intent(PurchaseReceiptsNew.this, PurchaseReceiptCreateNew.class);
                startActivity(intent);
            }
            break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_purchase_order, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void prepareHeader()
    {
        po_number.setText(currentPurchaseOrderNo);
        project_no.setText(currentProjectNo);
        vendor_code.setText(pm.getString("vendorCode"));
        item_date.setText(pm.getString("createdOn"));
    }

    private void callJsonArrayRequest() {
        // TODO Auto-generated method stub

        pDialog = new ProgressDialog(PurchaseReceiptsNew.this);
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
                            for(int i=0; i<dataArray.length();i++) {
                                dataObject = dataArray.getJSONObject(i);
                                purchaseOrderId = dataObject.getString("purchaseOrderId");
                                if (purchaseOrderId.equals(currentPurchaseOrderNo)) {
                                    purchaseReceiptId = dataObject.getString("purchaseReceiptId");
                                    date = dataObject.getString("date");

                                    Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
                                    date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                    items = new PurchaseOrdersList(purchaseReceiptId, purchaseOrderId,
                                            currentProjectNo, date, String.valueOf(i + 1), 0);
                                    PurchaseOrdersList.add(items);

                                    PurchaseReceiptsNewAdapter.notifyDataSetChanged();
                                }
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
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

//
//    public void getPurchaseOrdersList()
//    {
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//
//
//        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//
//                        try{
//
//                            String type = response.getString("type");
//
//                            if(type.equals("ERROR"))
//                            {
//                                Toast.makeText(PurchaseReceiptsNew.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
//                            }
//
//                            if(type.equals("INFO"))
//                            {
//                                dataArray = response.getJSONArray("data");
//                                purchaseOrdersArray = new String[dataArray.length()];
//                                for(int i=0; i<dataArray.length();i++)
//                                {
//                                    dataObject = dataArray.getJSONObject(i);
//                                    poId = dataObject.getString("purchaseOrderId");
//                                    purchaseOrdersArray[i]=poId;
//                                }
//                            }
//                            pDialog2.dismiss();
//                        }catch(JSONException e){e.printStackTrace();}
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.e("Volley","Error");
//
//                    }
//                }
//        );
//        requestQueue.add(jor);
//    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PurchaseReceiptsNew.this, PurchaseOrders.class);
        startActivity(intent);
    }

}
