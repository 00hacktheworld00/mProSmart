package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PurchaseOrderCreate extends AppCompatActivity {

    String currentProjectNo, currentUserName, vendorId;
    ProgressDialog pDialog,pDialog1;
    String[] vendorIdArray;
    JSONArray dataArray;
    JSONObject dataObject;
    BetterSpinner spinnerVendorId;
    TextView text_created_on, text_created_by, text_project_id;
    String email_send_to, email_send_from, text, email_username, email_password;
    PreferenceManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_order_create);

        pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentUserName = pm.getString("userId");

        text_created_on = (TextView) findViewById(R.id.text_created_on);
        text_created_by = (TextView) findViewById(R.id.text_created_by);
        text_project_id = (TextView) findViewById(R.id.text_project_id);
        spinnerVendorId = (BetterSpinner) findViewById(R.id.spinnerVendorId);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = sdf.format(c.getTime());
        text_created_on.setText(strDate);

        text_project_id.setText(currentProjectNo);
        text_created_by.setText(currentUserName);

        pDialog1 = new ProgressDialog(PurchaseOrderCreate.this);
        pDialog1.setMessage("Preparing Lists...");
        pDialog1.setIndeterminate(false);
        pDialog1.setCancelable(true);
        pDialog1.show();

        class MyTask extends AsyncTask<Void, Void, Void>
        {

            @Override
            protected Void doInBackground(Void... params)
            {
                prepareVendorList();
                return null;
            }
        }

        new MyTask().execute();


        Button createBtn;
        createBtn = (Button) findViewById(R.id.createBtn);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(PurchaseOrderCreate.this);
                pDialog.setMessage("Sending Data ...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();

                class MyTask extends AsyncTask<Void, Void, Void> {

                    @Override
                    protected Void doInBackground(Void... params) {
                        saveData();
                        return null;
                    }

                    @Override protected void onPostExecute(Void result)
                    {
                        Intent intent = new Intent(PurchaseOrderCreate.this,PurchaseOrders.class);
                        startActivity(intent);
                    }
                }
                new MyTask().execute();

            }
        });

    }

    public void saveData()
    {
        JSONObject object = new JSONObject();

        try
        {
            object.put("projectId",text_project_id.getText().toString());
            object.put("vendorId",spinnerVendorId.getText().toString());
            object.put("createdBy",text_created_by.getText().toString());
            object.put("createdDate",text_created_on.getText().toString());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(PurchaseOrderCreate.this);

        String url = PurchaseOrderCreate.this.pm.getString("SERVER_URL") + "/postPurchaseOrder";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
//                            Toast.makeText(PurchaseOrderCreate.this, response.getString("msg").toString(), Toast.LENGTH_SHORT).show();
                            String successMsg = "Purchase Order Created. ID - "+response.getString("data").toString();
                            Toast.makeText(PurchaseOrderCreate.this, successMsg, Toast.LENGTH_SHORT).show();

                            text = "New Purchase Order creation\n\n\n"+
                                    "\n"+"Project ID - "+text_project_id.getText().toString()+
                                    "\n"+"Vendor ID - "+spinnerVendorId.getText().toString()+
                                    "\n"+"Created By - "+text_created_by.getText().toString()+
                                    "\n"+"Created On - "+text_created_on.getText().toString()+
                                    "\n\n\n"+"Response from Server - "+response.getString("msg").toString();

                            email_username = PurchaseOrderCreate.this.getResources().getString(R.string.SENDGRID_USERNAME);
                            email_password = PurchaseOrderCreate.this.getResources().getString(R.string.SENDGRID_PASSWORD);
                            email_send_to = PurchaseOrderCreate.this.getResources().getString(R.string.SENDGRID_EMAIL_SEND_TO);
                            email_send_from = PurchaseOrderCreate.this.getResources().getString(R.string.SENDGRID_EMAIL_SEND_FROM);
                            SendEmailASyncTask  task = new SendEmailASyncTask(getApplicationContext(), email_send_to, email_send_from, "New Purchase Order Creation", text, email_username, email_password);
                            task.execute();


                            pDialog.dismiss();
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        //response success message display
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley","Error");
                        Toast.makeText(PurchaseOrderCreate.this, error.toString(), Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                    }
                }
        );
        requestQueue.add(jor);
    }

    private static class SendEmailASyncTask extends AsyncTask<Void, Void, Void> {

        private Context mAppContext;
        private String mMsgResponse;

        private String mTo;
        private String mFrom;
        private String mSubject;
        private String mText;
        private String username;
        private String password;

        public SendEmailASyncTask(Context context, String mTo, String mFrom, String mSubject,
                                  String mText, String username, String password) {
            this.mAppContext = context.getApplicationContext();
            this.mTo = mTo;
            this.mFrom = mFrom;
            this.mSubject = mSubject;
            this.mText = mText;
            this.username = username;
            this.password = password;
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                SendGrid sendgrid = new SendGrid(username, password);

                SendGrid.Email email = new SendGrid.Email();

                // Get values from edit text to compose email
                // TODO: Validate edit texts
                email.addTo(mTo);
                email.setFrom(mFrom);
                email.setSubject(mSubject);
                email.setText(mText);

                // Send email, execute http request
                SendGrid.Response response = sendgrid.send(email);
                mMsgResponse = response.getMessage();

                Log.d("SendAppExample", mMsgResponse);

            } catch (SendGridException e) {
                Log.e("SendAppExample", e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
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
                                Toast.makeText(PurchaseOrderCreate.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
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
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(PurchaseOrderCreate.this,
                                    android.R.layout.simple_dropdown_item_1line,vendorIdArray);
                            spinnerVendorId.setAdapter(adapter);
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
}
