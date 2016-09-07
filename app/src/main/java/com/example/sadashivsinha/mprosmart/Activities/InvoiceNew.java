package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.Adapters.InvoiceNewAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.InvoiceNewList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.example.sadashivsinha.mprosmart.font.HelveticaRegular;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

        // check for Internet status
        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_content = (RelativeLayout) findViewById(R.id.main_content);
            Snackbar snackbar = Snackbar.make(main_content,getResources().getString(R.string.no_internet_error), Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        pDialog = new ProgressDialog(InvoiceNew.this,R.style.MyTheme);
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        pDialog.setCancelable(false);
        pDialog.show();

        class MyTask extends AsyncTask<Void, Void, Void>
        {
            @Override protected void onPreExecute()
            {
                invoiceNewAdapter = new InvoiceNewAdapter(invoiceNewList);
                recyclerView.setLayoutManager(new LinearLayoutManager(InvoiceNew.this));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(invoiceNewAdapter);

            }

            @Override
            protected Void doInBackground(Void... params)
            {
                prepareItems();
                return null;
            }

            @Override protected void onPostExecute(Void result)
            {
                invoiceNewAdapter.notifyDataSetChanged();
            }

        }

        new MyTask().execute();
    }

    public void prepareItems()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = getResources().getString(R.string.server_url) + "/getPurchaseReceiptItems?purchaseReceiptId=\""+currentPurchaseReceipt+"\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");
                            float totalCost, acceptedQuanVal, unitCostVal;

                            if(type.equals("ERROR"))
                            {
                                pDialog.dismiss();
                                Toast.makeText(InvoiceNew.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);

                                    itemId = dataObject.getString("itemId");
                                    quantityReceived = dataObject.getString("quantityReceived");
                                    unitCost =  dataObject.getString("unitCost");
                                    acceptedQuantity =  dataObject.getString("acceptedQuantity");

                                    unitCostVal = Float.parseFloat(unitCost);
                                    acceptedQuanVal = Float.parseFloat(acceptedQuantity);

                                    totalCost = unitCostVal * acceptedQuanVal;

                                    items = new InvoiceNewList(itemId, quantityReceived, acceptedQuantity, unitCost, totalCost);
                                    invoiceNewList.add(items);

                                    invoiceNewAdapter.notifyDataSetChanged();

                                    totalInvoiceFloat = totalInvoiceFloat + totalCost;
                                }

                                text_total_invoice_cost.setText(String.valueOf(totalInvoiceFloat));
                            }
                            pDialog.dismiss();
                        }catch(JSONException e){
                            pDialog.dismiss();
                            e.printStackTrace();}
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
}
