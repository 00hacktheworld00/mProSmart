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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BOQCreate extends AppCompatActivity {

    EditText text_boq_item, text_boq_name, text_quantity;
    BetterSpinner spinner_uom;
    Button createBtn;
    String currentProjectId, currentProjectName, currentUserId, currentDate, currentProjectDesc;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boq_create);

        PreferenceManager pm = new PreferenceManager(getApplicationContext());

        text_boq_item = (EditText) findViewById(R.id.text_boq_item);
        text_boq_name = (EditText) findViewById(R.id.text_boq_name);
        text_quantity = (EditText) findViewById(R.id.text_quantity);

        spinner_uom = (BetterSpinner) findViewById(R.id.spinner_uom);

        createBtn = (Button) findViewById(R.id.createBtn);

        currentProjectId = pm.getString("projectId");
        currentProjectName = pm.getString("projectName");
        currentUserId = pm.getString("userId");
        currentProjectDesc = pm.getString("projectDesc");


        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        currentDate = sdf.format(c.getTime());


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(BOQCreate.this,
                android.R.layout.simple_dropdown_item_1line,new String[] {"KGs", "Litres"});
        spinner_uom.setAdapter(adapter);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(text_boq_item.getText().toString().isEmpty())
                {
                    text_boq_item.setError("Field cannot be empty");
                }
                else if(text_boq_name.getText().toString().isEmpty())
                {
                    text_boq_name.setError("Field cannot be empty");
                }
                else if(text_quantity.getText().toString().isEmpty())
                {
                    text_quantity.setError("Field cannot be empty");
                }
                else if(spinner_uom.getText().toString().isEmpty())
                {
                    Toast.makeText(BOQCreate.this, "Select UOM", Toast.LENGTH_SHORT).show();
                }
                else
                {

                    pDialog = new ProgressDialog(BOQCreate.this);
                    pDialog.setMessage("Saving Data ...");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(true);
                    pDialog.show();

                    class MyTask extends AsyncTask<Void, Void, Void>
                    {
                        @Override
                        protected Void doInBackground(Void... params)
                        {
                            saveItems();
                            return null;
                        }
                    }
                    new MyTask().execute();
                }
            }
        });
    }

    public void saveItems()
    {
        JSONObject object = new JSONObject();


        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String strDate = sdf.format(c.getTime());

        try {
            object.put("projectId", currentProjectId);
            object.put("projectName", currentProjectName);
            object.put("projectDescription", currentProjectDesc);
            object.put("boqItem", text_boq_item.getText().toString());
            object.put("itemName", text_boq_name.getText().toString());
            object.put("unit", text_quantity.getText().toString());
            object.put("uom", spinner_uom.getText().toString());
            object.put("createdBy", currentUserId);
            object.put("createdDate", strDate);


            Log.d("tag", String.valueOf(object));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(BOQCreate.this);

        String url = BOQCreate.this.getResources().getString(R.string.server_url) + "/postBoq";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if(response.getString("msg").toString().equals("success"))
                            {
                                String successMsg = "BOQ Created. ID - "+response.getString("data").toString();
                                Toast.makeText(BOQCreate.this, successMsg, Toast.LENGTH_SHORT).show();
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
                        Log.e("Volley", "Error");
                        Toast.makeText(BOQCreate.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jor);
        if(pDialog!=null)
            pDialog.dismiss();

        Intent intent = new Intent(BOQCreate.this, AllBoq.class);
        startActivity(intent);
    }
}
