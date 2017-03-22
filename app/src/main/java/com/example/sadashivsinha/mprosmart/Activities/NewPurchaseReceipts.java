package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NewPurchaseReceipts extends NewActivity {

    Spinner itemsSpinner;
    Spinner spinnerVendorId;
    EditText text_purchase_order_no, text_project_no, text_date, quantity;
    Button createBtn;
    String[] vendorIdArray, itemsNameArray, itemsIdArray, quantityArray;
    ProgressDialog pDialog, pDialog1;
    JSONArray dataArray;
    JSONObject dataObject;
    String currentProjectNo, currentPoNumber, vendorId, itemNames, itemId, textQuantity;
    PreferenceManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_purchase_receipts);

        pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentPoNumber = pm.getString("poNumber");

        itemsSpinner = (Spinner) findViewById(R.id.itemsSpinner);
        spinnerVendorId = (Spinner) findViewById(R.id.spinnerVendorId);

        itemsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                quantity.setText(quantityArray[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        text_purchase_order_no = (EditText) findViewById(R.id.text_purchase_order_no);
        text_project_no = (EditText) findViewById(R.id.text_project_no);
        text_date = (EditText) findViewById(R.id.text_date);
        quantity = (EditText) findViewById(R.id.quantity);

        text_project_no.setText(currentProjectNo);
        text_purchase_order_no.setText(currentPoNumber);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String strDate = sdf.format(c.getTime());
        text_date.setText(strDate);

        pDialog = new ProgressDialog(NewPurchaseReceipts.this);
        pDialog.setMessage("Preparing Lists...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        class MyTask extends AsyncTask<Void, Void, Void>
        {
            @Override
            protected Void doInBackground(Void... params)
            {
                prepareVendorList();
                prepareItemList();
                return null;
            }
        }

        new MyTask().execute();

        createBtn = (Button) findViewById(R.id.createBtn);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemPosition = itemsSpinner.getSelectedItemPosition();
                if(quantity.getText().toString().isEmpty())
                {
                    quantity.setError("Enter Quantity");
                }
                else if (Integer.parseInt(quantity.getText().toString())>Integer.parseInt(quantityArray[itemPosition]))
                {
                    quantity.setError("Quantity cannot be less than previous quantity");
                }
                else
                {
                    pDialog1 = new ProgressDialog(NewPurchaseReceipts.this);
                    pDialog1.setMessage("Preparing Lists...");
                    pDialog1.setIndeterminate(false);
                    pDialog1.setCancelable(true);
                    pDialog1.show();

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
                }
            }
        });
    }

    public void prepareVendorList()
    {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = pm.getString("SERVER_URL") + "/getVendors";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(NewPurchaseReceipts.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                vendorIdArray = new String[dataArray.length()];
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    vendorId = dataObject.getString("vendorId");
                                    vendorIdArray[i]=vendorId;
                                }
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(NewPurchaseReceipts.this,
                                    android.R.layout.simple_dropdown_item_1line,vendorIdArray);
                            spinnerVendorId.setAdapter(adapter);

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

    public void prepareItemList()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = pm.getString("SERVER_URL") + "/getPurchaseLineItems?purchaseOrderId=\""+currentPoNumber+"\"";
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");
                            String msg = response.getString("msg");

                            if(type.equals("ERROR"))
                            {
                                pDialog.dismiss();
                                Toast.makeText(NewPurchaseReceipts.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                itemsNameArray = new String[dataArray.length()];
                                itemsIdArray = new String[dataArray.length()];
                                quantityArray = new String[dataArray.length()];

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);

                                    itemNames = dataObject.getString("purchaseLineItemsId");
//                                    itemId = dataObject.getString("itemId");
                                    textQuantity = dataObject.getString("quantity");

                                    itemsNameArray[i]=itemNames;
//                                    itemsIdArray[i]=itemId;
                                    quantityArray[i]=textQuantity;
                                }

                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(NewPurchaseReceipts.this,
                                        android.R.layout.simple_dropdown_item_1line,itemsNameArray);
                                itemsSpinner.setAdapter(adapter);
                                pDialog.dismiss();
                            }

                            if(msg.equals("No data"))
                            {
                                Toast.makeText(NewPurchaseReceipts.this, "No Purchase Items Found.", Toast.LENGTH_LONG).show();
                                pDialog.dismiss();
                                finish();
                            }

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

    public void prepareItems()
    {
        JSONObject object = new JSONObject();

        try
        {
            int itemPosition = itemsSpinner.getSelectedItemPosition();
            object.put("quantity",quantity.getText().toString());
            object.put("itemId",itemsIdArray[itemPosition]);
            object.put("purchaseOrderId",text_purchase_order_no.getText().toString());
            object.put("projectId",text_project_no.getText().toString());
            object.put("vendorId",spinnerVendorId.getSelectedItem().toString());
            object.put("date",text_date.getText().toString());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(NewPurchaseReceipts.this);

        String url = NewPurchaseReceipts.this.pm.getString("SERVER_URL") + "/postPurchaseReceipts";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(NewPurchaseReceipts.this, response.getString("msg").toString(), Toast.LENGTH_SHORT).show();
                            pDialog1.dismiss();

                            Intent intent = new Intent(NewPurchaseReceipts.this, PurchaseReceiptsNew.class);
                            startActivity(intent);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            pDialog1.dismiss();
                        }
                        //response success message display
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley","Error");
                        pDialog1.dismiss();
                    }
                }
        );
        requestQueue.add(jor);
    }
}
