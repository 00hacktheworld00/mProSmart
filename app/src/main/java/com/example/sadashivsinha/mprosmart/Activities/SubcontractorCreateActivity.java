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
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SubcontractorCreateActivity extends AppCompatActivity {
    EditText text_subcontractor_name, text_created_by, text_date_created;
    ProgressDialog pDialog;
    String currentProjectNo, currentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subcontractor_create);

        PreferenceManager pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentUserName = pm.getString("name");

        Button createBtn = (Button) findViewById(R.id.createBtn);

        text_subcontractor_name = (EditText) findViewById(R.id.text_subcontractor_name);
        text_created_by = (EditText) findViewById(R.id.text_created_by);
        text_date_created = (EditText) findViewById(R.id.text_date_created);

        text_created_by.setText(currentUserName);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = sdf.format(c.getTime());
        text_date_created.setText(strDate);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                class MyTask extends AsyncTask<Void, Void, Void>
                {
                    @Override protected void onPreExecute()
                    {
                        pDialog = new ProgressDialog(SubcontractorCreateActivity.this);
                        pDialog.setMessage("Sending Data ...");
                        pDialog.setIndeterminate(false);
                        pDialog.setCancelable(true);
                        pDialog.show();

                    }
                    @Override
                    protected Void doInBackground(Void... params)
                    {
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
            object.put("name",text_subcontractor_name.getText().toString());
            object.put("projectId",currentProjectNo);
            object.put("createdBy",text_created_by.getText().toString());
            object.put("createdDate",text_date_created.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(SubcontractorCreateActivity.this);

        String url = SubcontractorCreateActivity.this.getResources().getString(R.string.server_url) + "/postSubContractor";
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(SubcontractorCreateActivity.this, response.getString("msg").toString(), Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(SubcontractorCreateActivity.this, AllSubcontractor.class);
        startActivity(intent);
    }
}