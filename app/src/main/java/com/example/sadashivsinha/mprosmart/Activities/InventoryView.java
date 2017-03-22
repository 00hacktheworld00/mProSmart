package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.sadashivsinha.mprosmart.Adapters.InventoryViewAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.InventoryViewList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.AppController;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.example.sadashivsinha.mprosmart.font.HelveticaRegular;

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

public class InventoryView extends AppCompatActivity {

    private List<InventoryViewList> inventoryViewList = new ArrayList<>();
    private RecyclerView recyclerView;
    private InventoryViewAdapter inventoryAdapter;
    InventoryViewList items;
    String dateFrom, dateTo, itemId, url, currentProjectNo;
    PreferenceManager pm;
    ConnectionDetector cd;
    public static final String TAG = InventoryView.class.getSimpleName();
    Boolean isInternetPresent = false;
    ProgressDialog pDialog;
    JSONObject dataObject;
    JSONArray dataArray;
    int totalInventoryVal=0;

    HelveticaRegular from_date, to_date, item_no, inventory_total;

    String quantity, type, date, item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("View Inventory");

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        inventory_total = (HelveticaRegular) findViewById(R.id.inventory_total);

        from_date = (HelveticaRegular) findViewById(R.id.from_date);
        to_date = (HelveticaRegular) findViewById(R.id.to_date);

        item_no = (HelveticaRegular) findViewById(R.id.item_no);

        if(getIntent().hasExtra("fromDate"))
        {
            from_date.setText(getIntent().getStringExtra("fromDate"));
            to_date.setText(getIntent().getStringExtra("toDate"));

            itemId = getIntent().getStringExtra("currentInventoryItem");
            item_no.setText(itemId);

            Date tradeDateFrom = null, tradeDateTo = null;

            try
            {
                tradeDateFrom = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(getIntent().getStringExtra("fromDate"));
                tradeDateTo = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(getIntent().getStringExtra("toDate"));
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }

            dateFrom = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(tradeDateFrom);
            dateTo = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(tradeDateTo);

            url = pm.getString("SERVER_URL") + "/getInventoryManagement?projectId=\"" + currentProjectNo +
                   "\"&fromDate=\""+ dateFrom + "\"&toDate=\"" + dateTo + "\"";

            Log.d("URL Inventory", url);
        }

        inventoryAdapter = new InventoryViewAdapter(inventoryViewList);

        recyclerView.setLayoutManager(new LinearLayoutManager(InventoryView.this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(inventoryAdapter);

        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(InventoryView.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(InventoryView.this);
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

                            item = dataObject.getString("itemId");

                            if(item.equals(itemId))
                            {
                                quantity = dataObject.getString("quantity");
                                type = dataObject.getString("type");
                                date = dataObject.getString("date");

                                Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);

                                date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                if(type.equals("1"))
                                {
                                    totalInventoryVal = totalInventoryVal - Integer.parseInt(quantity);
                                }
                                else
                                {
                                    totalInventoryVal = totalInventoryVal + Integer.parseInt(quantity);
                                }

                                inventory_total.setText(String.valueOf(totalInventoryVal));

                                items = new InventoryViewList(date, type, quantity);
                                inventoryViewList.add(items);

                                inventoryAdapter.notifyDataSetChanged();
                            }
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
                Toast.makeText(InventoryView.this, "Offline Data Not available for this Inventory", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }

        else
        {
            // Cache data not exist.
            callJsonArrayRequest();
        }
    }

    private void callJsonArrayRequest() {
        // TODO Auto-generated method stub

        pDialog = new ProgressDialog(InventoryView.this);
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
                                item = dataObject.getString("itemId");

                                if(item.equals(itemId))
                                {
                                    quantity = dataObject.getString("quantity");
                                    type = dataObject.getString("type");
                                    date = dataObject.getString("date");

                                    Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);

                                    date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                    if(type.equals("1"))
                                    {
                                        totalInventoryVal = totalInventoryVal - Integer.parseInt(quantity);
                                    }
                                    else
                                    {
                                        totalInventoryVal = totalInventoryVal + Integer.parseInt(quantity);
                                    }

                                    inventory_total.setText(String.valueOf(totalInventoryVal));


                                    items = new InventoryViewList(date, type, quantity);
                                    inventoryViewList.add(items);

                                    inventoryAdapter.notifyDataSetChanged();
                                }
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(InventoryView.this, InventoryMainActivity.class);
        startActivity(intent);
    }
}
