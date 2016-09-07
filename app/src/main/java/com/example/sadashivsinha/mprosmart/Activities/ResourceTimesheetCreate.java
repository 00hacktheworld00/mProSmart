package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class ResourceTimesheetCreate extends AppCompatActivity {
    EditText text_project_id, text_created_by, text_date_created;
    ProgressDialog pDialog, pDialog1;
    String currentProjectNo, currentUserId, resource;
    JSONArray dataArray;
    JSONObject dataObject;
    Spinner spinnerResName;
    String[] resourceArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_timesheet_create);

        Button createBtn = (Button) findViewById(R.id.createBtn);

        spinnerResName = (Spinner) findViewById(R.id.spinnerResName);

        final PreferenceManager pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentUserId = pm.getString("userId");

        text_project_id = (EditText) findViewById(R.id.text_project_id);
        text_created_by = (EditText) findViewById(R.id.text_created_by);
        text_date_created = (EditText) findViewById(R.id.text_date_created);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String strDate = sdf.format(c.getTime());
        text_date_created.setText(strDate);

        pDialog1 = new ProgressDialog(ResourceTimesheetCreate.this);
        pDialog1.setMessage("Preparing Lists...");
        pDialog1.setIndeterminate(false);
        pDialog1.setCancelable(true);
        pDialog1.show();

        class MyTask extends AsyncTask<Void, Void, Void>
        {

            @Override
            protected Void doInBackground(Void... params)
            {
                prepareResources();
                return null;
            }
        }

        new MyTask().execute();

        text_created_by.setText(currentUserId);
        text_project_id.setText(currentProjectNo);


        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(ResourceTimesheetCreate.this);
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
            object.put("projectId",currentProjectNo);
            object.put("name",spinnerResName.getSelectedItem().toString());
            object.put("photoUrl","0");
            object.put("createdBy",text_created_by.getText().toString());
            object.put("createdDate",text_date_created.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestQueue requestQueue = Volley.newRequestQueue(ResourceTimesheetCreate.this);

        String url = ResourceTimesheetCreate.this.getResources().getString(R.string.server_url) + "/postResourceTimesheet";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(ResourceTimesheetCreate.this, response.getString("msg").toString(), Toast.LENGTH_SHORT).show();
                            pDialog.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            pDialog.dismiss();
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
        Intent intent = new Intent(ResourceTimesheetCreate.this, AllResource.class);
        startActivity(intent);
        }

    public void prepareResources()
    {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = getResources().getString(R.string.server_url) + "/getResource";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(ResourceTimesheetCreate.this, response.getString("msg"), Toast.LENGTH_SHORT).show();

                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                resourceArray = new String[dataArray.length()];
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    resource = dataObject.getString("firstName");
                                    resourceArray[i]=resource;
                                }
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ResourceTimesheetCreate.this,
                                    android.R.layout.simple_dropdown_item_1line,resourceArray);
                            spinnerResName.setAdapter(adapter);
                            pDialog1.dismiss();
                        }catch(JSONException e){
                            pDialog1.dismiss();
                            e.printStackTrace();}
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