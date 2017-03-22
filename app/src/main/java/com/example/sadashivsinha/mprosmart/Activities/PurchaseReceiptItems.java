package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.sadashivsinha.mprosmart.Adapters.PurchaseReceiptItemsAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.PurchaseReceiptCreateNewList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.AppController;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class PurchaseReceiptItems extends AppCompatActivity {

    TextView receipt_no, po_no, item_date;
    ProgressDialog pDialog;
    JSONArray dataArray;
    JSONObject dataObject;
    Boolean isInternetPresent = false;
    String currentPoNo, purchaseLineItemsId, quantity, currentReceiptNo, date, newQuantity, unitCost;
    PurchaseReceiptCreateNewList items;
    public static final String TAG = PurchaseReceiptItems.class.getSimpleName();

    private List<PurchaseReceiptCreateNewList> purchaseList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PurchaseReceiptItemsAdapter purchaseAdapter;
    String url;
    PreferenceManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_receipt_items);

        receipt_no = (TextView) findViewById(R.id.receipt_no);
        po_no = (TextView) findViewById(R.id.po_no);
        item_date = (TextView) findViewById(R.id.item_date);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        pm = new PreferenceManager(getApplicationContext());
        currentPoNo = pm.getString("poNumber");
        currentReceiptNo = pm.getString("currentReceiptNo");
        date = pm.getString("date");

        receipt_no.setText(currentReceiptNo);
        po_no.setText(currentPoNo);
        item_date.setText(date);

        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        url = pm.getString("SERVER_URL") + "/getPurchaseReceiptItems?purchaseReceiptId='"+currentReceiptNo+"'";

        purchaseAdapter = new PurchaseReceiptItemsAdapter(purchaseList);
        recyclerView.setLayoutManager(new LinearLayoutManager(PurchaseReceiptItems.this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(purchaseAdapter);

        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(PurchaseReceiptItems.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(PurchaseReceiptItems.this);
            pDialog.setMessage("Getting cache data");
            pDialog.show();

            if(currentReceiptNo.equals(getResources().getString(R.string.waiting_to_connect)))
            {
                String jsonObjectVal = pm.getString("objectPR");
                Log.d("JSON PR PENDING :", jsonObjectVal);

                JSONObject jsonObjectPending = null;
                try
                {
                    jsonObjectPending = new JSONObject(jsonObjectVal);
                    Log.d("JSONObj PR PENDING :", jsonObjectPending.toString());
                    JSONArray itemsJsonArray = jsonObjectPending.getJSONArray("items");

                    for(int i=0; i<itemsJsonArray.length(); i++)
                    {
                        JSONObject itemsJsonObject = itemsJsonArray.getJSONObject(i);
                        String itemId = itemsJsonObject.getString("itemId");
                        String quantityReceived = itemsJsonObject.getString("quantityReceived");
                        String poQuantity = itemsJsonObject.getString("poQuantity");
                        String unitCost = itemsJsonObject.getString("unitCost");

                        items = new PurchaseReceiptCreateNewList(0, itemId, poQuantity, quantityReceived, unitCost);
                        purchaseList.add(items);

                        purchaseAdapter.notifyDataSetChanged();

                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                pDialog.dismiss();
            }
            else
            {

                Cache cache = AppController.getInstance().getRequestQueue().getCache();
                Cache.Entry entry = cache.get(url);
                if (entry != null) {
                    //Cache data available.
                    try {
                        String data = new String(entry.data, "UTF-8");
                        Log.d("CACHE DATA", data);
                        JSONObject jsonObject = new JSONObject(data);
                        try
                        {
                            dataArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < dataArray.length(); i++)
                            {
                                dataObject = dataArray.getJSONObject(i);
                                purchaseLineItemsId =  dataObject.getString("itemId");
                                quantity =  dataObject.getString("poQuantity");
                                newQuantity =  dataObject.getString("quantityReceived");
                                unitCost =  dataObject.getString("unitCost");


                                items = new PurchaseReceiptCreateNewList(0, purchaseLineItemsId, quantity, newQuantity, unitCost);
                                purchaseList.add(items);

                                purchaseAdapter.notifyDataSetChanged();
                                pDialog.dismiss();
                            }

                        }
                        catch (JSONException e)
                        {
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
                    Toast.makeText(PurchaseReceiptItems.this, "Offline Data Not available for these Purchase Receipt Items", Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                }
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

        pDialog = new ProgressDialog(PurchaseReceiptItems.this);
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
                                purchaseLineItemsId =  dataObject.getString("itemId");
                                quantity =  dataObject.getString("poQuantity");
                                newQuantity =  dataObject.getString("quantityReceived");
                                unitCost =  dataObject.getString("unitCost");


                                items = new PurchaseReceiptCreateNewList(0, purchaseLineItemsId, quantity, newQuantity, unitCost);
                                purchaseList.add(items);

                                purchaseAdapter.notifyDataSetChanged();
                            }
                        }
                        catch (JSONException e)
                        {
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
