package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class QualityPlanItemCreate extends AppCompatActivity {

    TextView text_process_desc, text_procedure, text_criteria;
    BetterSpinner spinnerSupplier, spinnerSubcontractor, spinnerThirdParty, spinnerCustomerClient;

    Spinner spinner_wbs, spinner_activity;
    Button createBtn;
    ProgressDialog pDialog;
    PreferenceManager pm;
    String currentQualityPlan, currentUserId, currentDate;
    String currentProjectNo;
    JSONArray dataArray;
    JSONObject dataObject;
    ConnectionDetector cd;
    public static final String TAG = QualityPlanItemCreate.class.getSimpleName();
    Boolean isInternetPresent = false;

    String[] wbsNameArray, activitiesNameArray, activityIdArray, wbsIdArray;
    String wbsName, activityName, activityId, wbsId;

    Button attachBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quality_plan_item_create);

        pm = new PreferenceManager(getApplicationContext());
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        currentProjectNo = pm.getString("projectId");

        attachBtn = (Button) findViewById(R.id.attachBtn);
        attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QualityPlanItemCreate.this, AttachmentActivity.class);
                intent.putExtra("class", "QualityPlanItem");
                startActivity(intent);
            }
        });

        text_process_desc = (TextView) findViewById(R.id.text_process_desc);
        text_procedure = (TextView) findViewById(R.id.text_procedure);
        text_criteria = (TextView) findViewById(R.id.text_criteria);

        spinner_wbs = (Spinner) findViewById(R.id.spinner_wbs);
        spinner_activity = (Spinner) findViewById(R.id.spinner_activity);

        spinnerSupplier = (BetterSpinner) findViewById(R.id.spinnerSupplier);
        spinnerSubcontractor = (BetterSpinner) findViewById(R.id.spinnerSubcontractor);
        spinnerThirdParty = (BetterSpinner) findViewById(R.id.spinnerThirdParty);
        spinnerCustomerClient = (BetterSpinner) findViewById(R.id.spinnerCustomerClient);

        createBtn = (Button) findViewById(R.id.createBtn);

        getAllWbs();

        spinner_wbs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0)
                {
                    //hide layout below

                    spinner_activity.setVisibility(View.GONE);
                }

                else
                {
                    spinner_activity.setVisibility(View.VISIBLE);

                    pDialog = new ProgressDialog(QualityPlanItemCreate.this);
                    pDialog.setMessage("Getting Activities in WBS - "+ wbsIdArray[position]);
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(true);
                    pDialog.show();

                    final String currentSelectedWbs = wbsIdArray[position];

                    class MyTask extends AsyncTask<Void, Void, Void>
                    {
                        @Override
                        protected Void doInBackground(Void... params)
                        {
                            prepareActivities(currentSelectedWbs, pDialog);
                            return null;
                        }
                    }

                    new MyTask().execute();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line,
                new String[] {"Yes", "No"});

        spinnerSupplier.setAdapter(adapter);
        spinnerSubcontractor.setAdapter(adapter);
        spinnerThirdParty.setAdapter(adapter);
        spinnerCustomerClient.setAdapter(adapter);

        currentQualityPlan = pm.getString("currentQualityPlan");
        currentUserId = pm.getString("userId");



        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(text_process_desc.getText().toString().isEmpty())
                {
                    text_process_desc.setError("Field cannot be empty");
                }
                else if(text_procedure.getText().toString().isEmpty())
                {
                    text_procedure.setError("Field cannot be empty");
                }
                else if(text_criteria.getText().toString().isEmpty())
                {
                    text_criteria.setError("Field cannot be empty");
                }
                else if(spinnerSupplier.getText().toString().equals("Supplier"))
                {
                    Toast.makeText(QualityPlanItemCreate.this, "Select Supplier first", Toast.LENGTH_SHORT).show();
                }
                else if(spinnerSubcontractor.getText().toString().equals("Subcontractor"))
                {
                    Toast.makeText(QualityPlanItemCreate.this, "Select Subcontractor first", Toast.LENGTH_SHORT).show();
                }
                else if(spinnerThirdParty.getText().toString().equals("Third Party"))
                {
                    Toast.makeText(QualityPlanItemCreate.this, "Select Third Party first", Toast.LENGTH_SHORT).show();
                }
                else if(spinnerCustomerClient.getText().toString().equals("Customer/Client"))
                {
                    Toast.makeText(QualityPlanItemCreate.this, "Select Customer/Client first", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String strDate = sdf.format(c.getTime());

                    currentDate = strDate;


                    pDialog = new ProgressDialog(QualityPlanItemCreate.this);
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
                    }

                    new MyTask().execute();


                }

            }
        });
    }

    public void getAllWbs()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = pm.getString("SERVER_URL") + "/getWbs?projectId=\""+currentProjectNo+"\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(QualityPlanItemCreate.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                wbsNameArray = new String[dataArray.length()+1];
                                wbsIdArray = new String[dataArray.length()+1];
                                wbsNameArray[0]= "Select WBS";
                                wbsIdArray[0]= "";

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    wbsName = dataObject.getString("wbsName");
                                    wbsId = dataObject.getString("wbsId");

                                    wbsNameArray[i+1]=wbsName;
                                    wbsIdArray[i+1]=wbsId;
                                }


                                if(dataArray==null)
                                {
                                    Toast.makeText(QualityPlanItemCreate.this, "No WBS Found in this project", Toast.LENGTH_SHORT).show();

                                    wbsNameArray = new String[0];
                                    wbsNameArray[0] = "No WBS";

                                }
                                else
                                {
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(QualityPlanItemCreate.this,
                                            android.R.layout.simple_dropdown_item_1line,wbsNameArray);
                                    spinner_wbs.setAdapter(adapter);
                                }
                            }

                        }
                        catch(JSONException e){
                            e.printStackTrace();}

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley","Error");

                    }
                }
        );
        if(pDialog!=null)
            pDialog.dismiss();

        requestQueue.add(jor);

    }


    public void prepareActivities(final String currentWbsId, final ProgressDialog pDialog)
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = pm.getString("SERVER_URL") + "/getWbsActivity?wbsId=\""+currentWbsId+"\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{
                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                pDialog.dismiss();
                                Toast.makeText(QualityPlanItemCreate.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {

                                JSONArray dataArray = response.getJSONArray("data");
                                JSONObject dataObject;
                                activityIdArray = new String[dataArray.length()+1];
                                activitiesNameArray = new String[dataArray.length()+1];
                                activityIdArray[0]= "Select Activity";
                                activitiesNameArray[0]= "Select Activity";

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);

                                    activityId = dataObject.getString("id");
                                    activityName = dataObject.getString("activityName");

                                    activityIdArray[i+1] = activityId;
                                    activitiesNameArray[i+1] = activityName;
                                }

                                ArrayAdapter<String> adapter;

                                if(activitiesNameArray==null)
                                {
                                    adapter = new ArrayAdapter<String>(QualityPlanItemCreate.this,
                                            android.R.layout.simple_dropdown_item_1line, new String[] {"No Activity Found"});
                                }
                                else
                                {
                                    adapter = new ArrayAdapter<String>(QualityPlanItemCreate.this,
                                            android.R.layout.simple_dropdown_item_1line, activitiesNameArray);
                                }

                                spinner_activity.setAdapter(adapter);

                            }
                            pDialog.dismiss();
                        }
                        catch(JSONException e){
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


    public void saveData() {
        JSONObject object = new JSONObject();

        try {
            object.put("qualityPlanId", currentQualityPlan);
            object.put("processDescription", text_process_desc.getText().toString());
            object.put("Activity", spinner_activity.getSelectedItem().toString());
            object.put("procedure", text_procedure.getText().toString());
            object.put("acceptanceCriteria", text_criteria.getText().toString());
            object.put("supplier", spinnerSupplier.getText().toString());
            object.put("subContractor", spinnerSubcontractor.getText().toString());
            object.put("thirdParty", spinnerThirdParty.getText().toString());
            object.put("customerClient", spinnerCustomerClient.getText().toString());
            object.put("createdBy", currentUserId);
            object.put("createdDate", currentDate);

            if(pm.getString("className").equals("QualityPlanItem"))
            {
                object.put("totalAttachments", pm.getString("totalImageUrlSize"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(QualityPlanItemCreate.this);

        String url = QualityPlanItemCreate.this.pm.getString("SERVER_URL") + "/postQualityPlanStatus";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("msg").equals("success"))
                            {
                                String totalImageUrls = pm.getString("totalImageUrls");
                                if(pm.getString("className").equals("QualityPlanItem") && isInternetPresent
                                        && !totalImageUrls.isEmpty())
                                {
                                    uploadImage(response.getString("data"), totalImageUrls);
                                }

                                else
                                {
                                    if (pDialog != null)
                                        pDialog.dismiss();

                                    Toast.makeText(QualityPlanItemCreate.this, "Quality Plan Line Item Created. ID - " + response.getString("data"), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(QualityPlanItemCreate.this, QualityPlanActivity.class);
                                    startActivity(intent);
                                }
                            }
                            else
                            {
                                Toast.makeText(QualityPlanItemCreate.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                            }


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
        Intent intent = new Intent(QualityPlanItemCreate.this,QualityPlanActivity.class);
        startActivity(intent);
    }

    public void uploadImage(final String id, String totalUrls) {

        final List<String> imageList = Arrays.asList(totalUrls.split(","));
        String seperateImageUrl;


        for (int i = 0; i < imageList.size(); i++) {

            final int count = i;
            JSONObject object = new JSONObject();

            try {
                seperateImageUrl = imageList.get(i);

                object.put("qualityPlanStatusId", id);
                object.put("url", seperateImageUrl);

                Log.d("JSON OBJ SENT", object.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }


            RequestQueue requestQueue = Volley.newRequestQueue(QualityPlanItemCreate.this);

            String url = QualityPlanItemCreate.this.pm.getString("SERVER_URL") + "/postQualityPlanStatusFiles";

            JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                Log.d("RESPONSE SERVER : ", response.toString());

                                if (response.getString("msg").equals("success")) {

                                    if (count == imageList.size() - 1) {
                                        Toast.makeText(QualityPlanItemCreate.this, "Quality Plan Line created ID - " + id, Toast.LENGTH_SHORT).show();

                                        if (pDialog != null)
                                            pDialog.dismiss();
                                        Intent intent = new Intent(QualityPlanItemCreate.this, QualityPlanActivity.class);
                                        startActivity(intent);
                                    }
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
                            Toast.makeText(QualityPlanItemCreate.this, error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
            );

            requestQueue.add(jor);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(QualityPlanItemCreate.this, AllQualityPlans.class);
        startActivity(intent);
    }
}
