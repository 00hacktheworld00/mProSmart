package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.Adapters.MyAdapter;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import mehdi.sakout.fancybuttons.FancyButton;

public class InvoiceSeperateItems extends NewActivity {

    ScrollView hiddenTextboxLayout;
    RelativeLayout main_content;
    ConnectionDetector cd;
    public static final String TAG = PunchListItems.class.getSimpleName();
    Boolean isInternetPresent = false;
    String current_line_no, currentInvoiceNo, lineId;
    private ProgressDialog pDialog;
    JSONArray dataArray;
    JSONObject dataObject;

    String quantityDelivered, itemId ,uomId ,itemDescription, unitCost,total , currencyId;

    TextView text_line_no, text_item_id,text_item_desc, text_quan_delivered, text_uom,
            text_unit_cost, text_currency, text_total_amount;

    PreferenceManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_items);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pm = new PreferenceManager(getApplicationContext());
        current_line_no = pm.getString("invoiceLineNo");
        currentInvoiceNo = pm.getString("invoiceNo");
        main_content = (RelativeLayout) findViewById(R.id.main_content);

        hiddenTextboxLayout = (ScrollView) findViewById(R.id.hiddenTextboxLayout);

        text_item_id = (TextView) findViewById(R.id.text_item_id);
        text_item_desc = (TextView) findViewById(R.id.text_item_desc);
        text_quan_delivered = (TextView) findViewById(R.id.text_quan_delivered);
        text_uom = (TextView) findViewById(R.id.text_uom);
        text_unit_cost = (TextView) findViewById(R.id.text_unit_cost);
        text_currency = (TextView) findViewById(R.id.text_currency);
        text_total_amount = (TextView) findViewById(R.id.text_total_amount);
        text_line_no = (TextView) findViewById(R.id.text_line_no);

        FancyButton editBtn = (FancyButton) findViewById(R.id.editBtn);

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            Snackbar snackbar = Snackbar.make(main_content,getResources().getString(R.string.no_internet_error), Snackbar.LENGTH_LONG);
            snackbar.show();
        }
        pDialog = new ProgressDialog(InvoiceSeperateItems.this);
        pDialog.setMessage("Getting Data ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();


        class MyTask extends AsyncTask<Void, Void, Void>
        {
            @Override
            protected Void doInBackground(Void... params)
            {
                prepareItems();
                return null;
            }

        }

        new MyTask().execute();


        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hiddenTextboxLayout.getVisibility() == View.GONE) {
                    hiddenTextboxLayout.setVisibility(View.VISIBLE);
                    hiddenTextboxLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.view_show));
                } else {
                    hiddenTextboxLayout.setVisibility(View.GONE);
                    hiddenTextboxLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.view_hide));
                }
            }
        });

        TextView saveBtn = (TextView) findViewById(R.id.saveBtn);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hiddenTextboxLayout.setVisibility(View.GONE);
                hiddenTextboxLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.view_hide));

                Snackbar snackbar = Snackbar.make(main_content, "Values saved.", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View
        mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size
        mAdapter = new MyAdapter(TITLES, ICONS, NAME, EMAIL, PROFILE);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,header view name, header view email,
        // and header view profile picture
        mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView
        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager
        mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager
        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);        // Drawer object Assigned to the view
        mDrawerToggle = new ActionBarDrawerToggle(this, Drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

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

        String url = getResources().getString(R.string.server_url) + "/getInvoiceLineItems?lineNo=\""+current_line_no+"\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(InvoiceSeperateItems.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    lineId = dataObject.getString("lineNo");

                                    if(lineId.equals(current_line_no))
                                    {
                                        itemId = dataObject.getString("itemId");
                                        itemDescription = dataObject.getString("itemDescription");
                                        quantityDelivered = dataObject.getString("quantityDelivered");
                                        uomId = dataObject.getString("uomId");
                                        unitCost = dataObject.getString("unitCost");
                                        total = dataObject.getString("total");
                                        currencyId = dataObject.getString("currencyId");

                                        text_line_no.setText(lineId);
                                        text_item_id.setText(itemId);
                                        text_item_desc.setText(itemDescription);
                                        text_quan_delivered.setText(quantityDelivered);
                                        text_uom.setText(uomId);
                                        text_unit_cost.setText(unitCost);
                                        text_total_amount.setText(total);
                                        text_currency.setText(currencyId);
                                    }
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
}
