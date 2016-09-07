package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import org.json.JSONException;
import org.json.JSONObject;

public class CustomerSupport extends AppCompatActivity {

    ProgressDialog pDialog;
    EditText text_acc_no, text_id, text_desc, text_type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_support);
        Button send_btn;

        text_acc_no = (EditText) findViewById(R.id.text_acc_no);
        text_id = (EditText) findViewById(R.id.text_id);
        text_desc = (EditText) findViewById(R.id.text_desc);
        text_type = (EditText) findViewById(R.id.text_type);

        send_btn = (Button) findViewById(R.id.send_btn);

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(CustomerSupport.this);
                pDialog.setMessage("Sending Data ...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();

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
            object.put("clientAccountNo",text_acc_no.getText().toString());
            object.put("userId",text_id.getText().toString());
            object.put("type",text_type.getText().toString());
            object.put("description",text_desc.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(CustomerSupport.this);

        String url = CustomerSupport.this.getResources().getString(R.string.server_url) + "/postCustomerSupport";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(CustomerSupport.this, response.getString("msg").toString(), Toast.LENGTH_SHORT).show();
                            pDialog.dismiss();
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
                        pDialog.dismiss();
                    }
                }
        );
        requestQueue.add(jor);
        Intent intent = new Intent(CustomerSupport.this, WelcomeActivity.class);
        startActivity(intent);
    }
}
