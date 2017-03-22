package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
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
import com.example.sadashivsinha.mprosmart.Adapters.InvoiceNewAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.InvoiceNewList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.AppController;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.example.sadashivsinha.mprosmart.font.HelveticaRegular;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class InvoiceNew extends AppCompatActivity {

    private List<InvoiceNewList> invoiceNewList = new ArrayList<>();
    private RecyclerView recyclerView;
    private InvoiceNewAdapter invoiceNewAdapter;
    private InvoiceNewList items;
    private ProgressDialog pDialog;
    JSONArray dataArray;
    JSONObject dataObject;
    ConnectionDetector cd;
    String itemId, quantityReceived;
    public static final String TAG = InvoiceNew.class.getSimpleName();
    Boolean isInternetPresent = false;
    float totalInvoiceFloat;

    HelveticaRegular text_total_invoice_cost, text_total_currency;
    HelveticaRegular text_invoice_no, text_pr_no, text_vendor;
    String currentProjectNo, currentVendorInvoice, currentPurchaseReceipt, currentVendor, unitCost, acceptedQuantity;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_new2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        totalInvoiceFloat = 0;

        text_total_invoice_cost = (HelveticaRegular) findViewById(R.id.text_total_invoice_cost);
        text_total_currency = (HelveticaRegular) findViewById(R.id.text_total_currency);

        text_pr_no = (HelveticaRegular) findViewById(R.id.text_pr_no);
        text_vendor = (HelveticaRegular) findViewById(R.id.text_vendor);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerInvoice);

        PreferenceManager pm = new PreferenceManager(InvoiceNew.this);
        currentProjectNo = pm.getString("projectId");
        currentVendorInvoice = pm.getString("currentVendorInvoice");
        currentPurchaseReceipt = pm.getString("prNo");
        currentVendor = pm.getString("vendor");
        text_total_currency.setText(pm.getString("currency"));

        text_invoice_no = (HelveticaRegular) findViewById(R.id.text_invoice_no);
        text_invoice_no.setText("Vendor Invoice No. - " + currentVendorInvoice);

        text_pr_no.setText(currentPurchaseReceipt);

        text_vendor.setText(currentVendor);

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        url = pm.getString("SERVER_URL") + "/getPurchaseReceiptItems?purchaseReceiptId=\""+currentPurchaseReceipt+"\"";

        invoiceNewAdapter = new InvoiceNewAdapter(invoiceNewList);
        recyclerView.setLayoutManager(new LinearLayoutManager(InvoiceNew.this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(invoiceNewAdapter);

        // check for Internet status
        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(InvoiceNew.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(InvoiceNew.this);
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

                        float totalCost, acceptedQuanVal, unitCostVal;
                        dataArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < dataArray.length(); i++) {
                            dataObject = dataArray.getJSONObject(i);
                            itemId = dataObject.getString("itemId");
                            quantityReceived = dataObject.getString("quantityReceived");
                            unitCost = dataObject.getString("unitCost");
                            acceptedQuantity = dataObject.getString("acceptedQuantity");

                            unitCostVal = Float.parseFloat(unitCost);
                            acceptedQuanVal = Float.parseFloat(acceptedQuantity);

                            totalCost = unitCostVal * acceptedQuanVal;

                            items = new InvoiceNewList(itemId, quantityReceived, acceptedQuantity, unitCost, totalCost);
                            invoiceNewList.add(items);

                            invoiceNewAdapter.notifyDataSetChanged();

                            totalInvoiceFloat = totalInvoiceFloat + totalCost;
                        }

                        text_total_invoice_cost.setText(String.valueOf(totalInvoiceFloat));

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
                Toast.makeText(InvoiceNew.this, "Offline Data Not available for this Invoice", Toast.LENGTH_SHORT).show();
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

        pDialog = new ProgressDialog(InvoiceNew.this);
        pDialog.setMessage("Getting server data");
        pDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            float totalCost, acceptedQuanVal, unitCostVal;

//                            dataObject = response.getJSONObject(0);
                            dataArray = response.getJSONArray("data");
                            for (int i = 0; i < dataArray.length(); i++)
                            {
                                dataObject = dataArray.getJSONObject(i);
                                itemId = dataObject.getString("itemId");
                                quantityReceived = dataObject.getString("quantityReceived");
                                unitCost = dataObject.getString("unitCost");
                                acceptedQuantity = dataObject.getString("acceptedQuantity");

                                unitCostVal = Float.parseFloat(unitCost);
                                acceptedQuanVal = Float.parseFloat(acceptedQuantity);

                                totalCost = unitCostVal * acceptedQuanVal;

                                items = new InvoiceNewList(itemId, quantityReceived, acceptedQuantity, unitCost, totalCost);
                                invoiceNewList.add(items);

                                invoiceNewAdapter.notifyDataSetChanged();

                                totalInvoiceFloat = totalInvoiceFloat + totalCost;
                            }

                            text_total_invoice_cost.setText(String.valueOf(totalInvoiceFloat));


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
}
