package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.Adapters.PurchaseReceiptItemsAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.PurchaseReceiptCreateNewList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PurchaseReceiptItems extends AppCompatActivity {

    TextView receipt_no, po_no, item_date;
    ProgressDialog pDialog;
    JSONArray dataArray;
    JSONObject dataObject;
    Boolean isInternetPresent = false;
    String currentPoNo, purchaseLineItemsId, quantity, currentReceiptNo, date, newQuantity, unitCost;
    PurchaseReceiptCreateNewList items;

    private List<PurchaseReceiptCreateNewList> purchaseList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PurchaseReceiptItemsAdapter purchaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_receipt_items);

        receipt_no = (TextView) findViewById(R.id.receipt_no);
        po_no = (TextView) findViewById(R.id.po_no);
        item_date = (TextView) findViewById(R.id.item_date);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        final PreferenceManager pm = new PreferenceManager(getApplicationContext());
        currentPoNo = pm.getString("poNumber");
        currentReceiptNo = pm.getString("currentReceiptNo");
        date = pm.getString("date");

        receipt_no.setText(currentReceiptNo);
        po_no.setText(currentPoNo);
        item_date.setText(date);

        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            CoordinatorLayout main_content = (CoordinatorLayout) findViewById(R.id.main_content);
            Snackbar snackbar = Snackbar.make(main_content, getResources().getString(R.string.no_internet_error), Snackbar.LENGTH_LONG);
            snackbar.show();
        }


        pDialog = new ProgressDialog(PurchaseReceiptItems.this);
        pDialog.setMessage("Getting Data ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();


        class MyTask extends AsyncTask<Void, Void, Void> {

            @Override protected void onPreExecute() {
                purchaseAdapter = new PurchaseReceiptItemsAdapter(purchaseList);
                recyclerView.setLayoutManager(new LinearLayoutManager(PurchaseReceiptItems.this));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(purchaseAdapter);
            }

            @Override
            protected Void doInBackground(Void... params) {
                prepareLineItems();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                purchaseAdapter.notifyDataSetChanged();
            }

        }

        new MyTask().execute();
    }

    public void prepareLineItems()
    {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = getResources().getString(R.string.server_url) + "/getPurchaseReceiptItems?purchaseReceiptId='"+currentReceiptNo+"'";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(PurchaseReceiptItems.this, response.getString("msg"), Toast.LENGTH_SHORT).show();

                            }

                            if(type.equals("INFO"))
                            {
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
