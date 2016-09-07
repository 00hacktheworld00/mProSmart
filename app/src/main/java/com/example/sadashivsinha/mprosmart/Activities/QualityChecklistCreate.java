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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class QualityChecklistCreate extends AppCompatActivity {
    EditText text_project_id, text_project_name, text_date_created, text_created_by;
    Button createBtn;
    ProgressDialog pDialog;
    JSONArray dataArray;
    JSONObject dataObject;
    String currentProjectNo, currentProjectName, currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quality_checklist_create);


        final PreferenceManager pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentProjectName = pm.getString("projectName");
        currentUserId = pm.getString("userId");

        text_project_id = (EditText) findViewById(R.id.text_project_id);
        text_project_name = (EditText) findViewById(R.id.text_project_name);
        text_created_by = (EditText) findViewById(R.id.text_created_by);
        text_date_created = (EditText) findViewById(R.id.text_date_created);

        createBtn = (Button) findViewById(R.id.createBtn);

        text_project_id.setText(pm.getString("projectId"));
        text_project_name.setText(pm.getString("projectName"));
        text_created_by.setText(pm.getString("userId"));


        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = sdf.format(c.getTime());

        text_date_created.setText(strDate);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pDialog = new ProgressDialog(QualityChecklistCreate.this);
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




    public void prepareItems() {
        JSONObject object = new JSONObject();

        try {
            object.put("projectId", currentProjectNo);
            object.put("projectName", currentProjectName);
            object.put("createdBy", text_created_by.getText().toString());
            object.put("createdDate", text_date_created.getText().toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(QualityChecklistCreate.this);

        String url = QualityChecklistCreate.this.getResources().getString(R.string.server_url) + "/postQualityCheckList";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(QualityChecklistCreate.this, response.getString("msg").toString(), Toast.LENGTH_SHORT).show();
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
                        Log.e("Volley", "Error");
                        pDialog.dismiss();
                    }
                }
        );
        requestQueue.add(jor);
        Intent intent = new Intent(QualityChecklistCreate.this,AllQualityChecklist.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(QualityChecklistCreate.this, AllQualityChecklist.class);
        startActivity(intent);
    }
}
