package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class QualityControlCreate extends AppCompatActivity {

    EditText  text_project_id, text_created_by;
    String qirNo, purchaseOrderNo, receiptNo, projectId, vendorId, createdBy;
    ProgressDialog pDialog, pDialog1;
    String currentProjectNo, currentUserId, currentUserName, poId, purchaseReceiptId;
    JSONArray dataArray;
    JSONObject dataObject;
    String[] purchaseOrdersArray, vendorIdArray, receiptArray;
    BetterSpinner spinnerOrders, spinnerVendorId, spinnerReceiptId;
    PreferenceManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quality_control_create2);

        pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentUserId = pm.getString("userId");
        currentUserName = pm.getString("name");

        Button createBtn;
        createBtn = (Button) findViewById(R.id.createBtn);

        text_project_id = (EditText) findViewById(R.id.text_project_id);
        text_created_by = (EditText) findViewById(R.id.text_created_by);

        text_created_by.setText(currentUserId);

        spinnerOrders = (BetterSpinner) findViewById(R.id.spinnerOrders);
        spinnerVendorId = (BetterSpinner) findViewById(R.id.spinnerVendorId);
        spinnerReceiptId = (BetterSpinner) findViewById(R.id.spinnerReceiptId);

        text_project_id.setText(currentProjectNo);
        text_project_id.setEnabled(false);

        pDialog1 = new ProgressDialog(QualityControlCreate.this);
        pDialog1.setMessage("Preparing Lists...");
        pDialog1.setIndeterminate(false);
        pDialog1.setCancelable(true);
        pDialog1.show();

        class MyTask extends AsyncTask<Void, Void, Void>
        {

            @Override
            protected Void doInBackground(Void... params)
            {
                preparePurchaseOrderList();
                prepareVendorList();
                prepareReceiptNos();
                return null;
            }
        }

        new MyTask().execute();

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(QualityControlCreate.this);
                pDialog.setMessage("Sending Data ...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();

                purchaseOrderNo = spinnerOrders.getText().toString();
                receiptNo = spinnerReceiptId.getText().toString();
                projectId = text_project_id.getText().toString();
                vendorId = spinnerVendorId.getText().toString();
                createdBy = currentUserId;

                pm.putString("currentReceiptNo", receiptNo);

                class MyTask extends AsyncTask<Void, Void, Void> {

                    @Override
                    protected Void doInBackground(Void... params) {
                        prepareItems();
                        return null;
                    }
                }
                new MyTask().execute();
            }
        });
    }

    public void prepareItems()
    {
        JSONObject object = new JSONObject();

        try {
            object.put("qualityInspectionReportId",qirNo);
            object.put("purchaseReceiptId",receiptNo);
            object.put("purchaseOrderId",purchaseOrderNo);
            object.put("projectId",currentProjectNo);
            object.put("createdBy",createdBy);
            object.put("vendorId",vendorId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(QualityControlCreate.this);

        String url = QualityControlCreate.this.getResources().getString(R.string.server_url) + "/postQualityInspection";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("msg").toString().equals("success"))
                            {
                                String successMsg = "Quality Control Created. ID - "+response.getString("data").toString();
                                Toast.makeText(QualityControlCreate.this, successMsg, Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();

                                pm.putString("qirNo",response.getString("data").toString());

                                pm.putString("receiptNo", receiptNo);

                                pm.putString("currentQualityPoNo", purchaseOrderNo);

                                Intent intent = new Intent(QualityControlCreate.this, QualityControlItemCreate.class);
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //response success message display
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley","Error");
                        Toast.makeText(QualityControlCreate.this, error.toString(), Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                    }
                }
        );
        requestQueue.add(jor);
    }

    public void preparePurchaseOrderList()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = getResources().getString(R.string.server_url) + "/getPurchaseOrders?projectId='"+currentProjectNo+"'";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");
                            String msg = response.getString("msg");
                            Log.d("response->", String.valueOf(response));

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(QualityControlCreate.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                purchaseOrdersArray = new String[dataArray.length()];
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    poId = dataObject.getString("purchaseOrderId");
                                    purchaseOrdersArray[i]=poId;
                                }
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(QualityControlCreate.this,
                                        android.R.layout.simple_dropdown_item_1line,purchaseOrdersArray);
                                spinnerOrders.setAdapter(adapter);
                            }

                            if(msg.equals("No data"))
                            {
                                Toast.makeText(QualityControlCreate.this, "No Purchase Order Found.", Toast.LENGTH_LONG).show();
                                pDialog1.dismiss();
                                finish();
                            }
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

    public void prepareVendorList()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = getResources().getString(R.string.server_url) + "/getVendors";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");
                            String msg = response.getString("msg");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(QualityControlCreate.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
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
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(QualityControlCreate.this,
                                        android.R.layout.simple_dropdown_item_1line,vendorIdArray);
                                spinnerVendorId.setAdapter(adapter);
                            }

                            if(msg.equals("No data"))
                            {
                                Toast.makeText(QualityControlCreate.this, "No Vendors Found.", Toast.LENGTH_LONG).show();
                                pDialog1.dismiss();
                                finish();
                            }

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

    public void prepareReceiptNos()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = getResources().getString(R.string.server_url) + "/getPurchaseReceipts?projectId=\""+currentProjectNo+"\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");
                            String msg = response.getString("msg");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(QualityControlCreate.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                receiptArray = new String[dataArray.length()+1];
                                receiptArray[0]="";
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    purchaseReceiptId = dataObject.getString("purchaseReceiptId");
                                    receiptArray[i+1]=purchaseReceiptId;

                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(QualityControlCreate.this,
                                            android.R.layout.simple_dropdown_item_1line,receiptArray);
                                    spinnerReceiptId.setAdapter(adapter);
                                    pDialog1.dismiss();
                                }
                            }
                            if(msg.equals("No data"))
                            {
                                Toast.makeText(QualityControlCreate.this, "No Purchase Receipt Found.", Toast.LENGTH_LONG).show();
                                pDialog1.dismiss();
                                finish();
                            }

                        }catch(JSONException e){e.printStackTrace();
                            pDialog1.dismiss();}
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog1.dismiss();
                        Log.e("Volley","Error");
                    }
                }
        );
        requestQueue.add(jor);
    }
}