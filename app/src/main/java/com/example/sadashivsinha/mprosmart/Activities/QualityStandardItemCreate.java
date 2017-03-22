package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
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
import com.weiwangcn.betterspinner.library.BetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class QualityStandardItemCreate extends AppCompatActivity {

    EditText text_criteria, text_result , text_comments;
    BetterSpinner spinner_status;
    Button createBtn;
    String criteria, result, comments, status, currentQualityStandardId;
    ProgressDialog pDialog, pDialog1;
    JSONObject dataObject;
    JSONArray dataArray;
    String[] uomIdArray, uomNameArray;
    String uomId, uomName, currentSelectedUomId;
    Spinner spinner_uom;
    PreferenceManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quality_standard_item_create);

        pm = new PreferenceManager(getApplicationContext());
        currentQualityStandardId = pm.getString("currentStandardNo");

        text_criteria = (EditText) findViewById(R.id.text_criteria);
        text_result = (EditText) findViewById(R.id.text_result);
        text_comments = (EditText) findViewById(R.id.text_comments);

        spinner_status = (BetterSpinner) findViewById(R.id.spinner_status);

        spinner_uom = (Spinner) findViewById(R.id.spinner_uom);

        createBtn = (Button) findViewById(R.id.createBtn);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line,
                new String[]{"ACCEPT", "REJECT"});

        spinner_status.setAdapter(adapter);

        pDialog1 = new ProgressDialog(QualityStandardItemCreate.this);
        pDialog1.setMessage("Sending Data ...");
        pDialog1.setIndeterminate(false);
        pDialog1.setCancelable(true);
        pDialog1.show();

        class MyTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... params) {

                prepareUomList();
                return null;
            }
        }

        new MyTask().execute();

        spinner_uom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentSelectedUomId = uomIdArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text_criteria.getText().toString().isEmpty())
                {
                    text_criteria.setError("Field cannot be empty");
                }
                else if (spinner_uom.getSelectedItem().toString().equals("Select UOM"))
                {
                    Toast.makeText(QualityStandardItemCreate.this, "Select UOM", Toast.LENGTH_SHORT).show();
                }
                else if (text_result.getText().toString().isEmpty())
                {
                    text_result.setError("Field cannot be empty");
                }
                else if (text_comments.getText().toString().isEmpty())
                {
                    text_comments.setError("Field cannot be empty");
                }
                else if (spinner_status.getText().toString().equals("Activity"))
                {
                    Toast.makeText(QualityStandardItemCreate.this, "Select Status first", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    criteria = text_criteria.getText().toString();
                    result = text_result.getText().toString();
                    comments = text_comments.getText().toString();
                    status = spinner_status.getText().toString();

                    if(status.equals("ACCEPT"))
                        status = "1";
                    else
                        status = "2";

                    pDialog = new ProgressDialog(QualityStandardItemCreate.this);
                    pDialog.setMessage("Sending Data ...");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(true);
                    pDialog.show();

                    class MyTask extends AsyncTask<Void, Void, Void> {

                        @Override
                        protected Void doInBackground(Void... params) {

                            saveItems();
                            return null;
                        }
                    }

                    new MyTask().execute();
                }
            }
        });
    }
    public void saveItems() {
        JSONObject object = new JSONObject();

        try {
            object.put("qualityStandardLineId", currentQualityStandardId);
            object.put("criteria", criteria);
            object.put("uom", currentSelectedUomId);
            object.put("inspectionResult", result);
            object.put("comments", comments);
            object.put("statusId", status);

            Log.d("JSON OBJECT", object.toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(QualityStandardItemCreate.this);

        String url = QualityStandardItemCreate.this.pm.getString("SERVER_URL") + "/postQualityStandardLine";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            Log.d("JSON RESPONSE", response.toString());

                            if(response.getString("msg").equals("success"))
                            {
                                Toast.makeText(QualityStandardItemCreate.this, "Quality Standard Line Item created. ID - " +
                                        response.getString("data") , Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();

                                Intent intent = new Intent(QualityStandardItemCreate.this, QualityStandardActivity.class);
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
                        Log.e("Volley", "Error");
                    }
                }
        );
        requestQueue.add(jor);
    }

    public void prepareUomList()
    {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = pm.getString("SERVER_URL") + "/getUom";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(QualityStandardItemCreate.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");

                                uomIdArray = new String[dataArray.length()+1];
                                uomNameArray = new String[dataArray.length()+1];

                                uomIdArray[0] = "Select UOM";
                                uomNameArray[0] = "Select UOM";

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    uomId = dataObject.getString("uomCode");
                                    uomName = dataObject.getString("uomName");

                                    uomIdArray[i+1]=uomId;
                                    uomNameArray[i+1]=uomName;
                                }
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(QualityStandardItemCreate.this,
                                    android.R.layout.simple_dropdown_item_1line,uomNameArray);
                            spinner_uom.setAdapter(adapter);
                            pDialog1.dismiss();

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
