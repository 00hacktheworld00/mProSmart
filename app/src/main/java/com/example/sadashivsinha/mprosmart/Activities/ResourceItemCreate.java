package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.AppController;
import com.example.sadashivsinha.mprosmart.Utils.Communicator;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ResourceItemCreate extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    String currentProjectNo, currentResourceId, currentResourceName;
    EditText text_total_hours;
    TextView select_date;
    Spinner spinner_wbs, spinner_activity;
    ProgressDialog pDialog;
    JSONArray dataArray;
    JSONObject dataObject;
    String dateTOSendServer;
    ConnectionDetector cd;
    public static final String TAG = ResourceItemCreate.class.getSimpleName();
    Boolean isInternetPresent = false;

    PreferenceManager pm;

    String[] wbsNameArray, activitiesNameArray, activityIdArray, wbsIdArray;
    String wbsName, activityName, activityId, wbsId, wbs_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_item_create);

        pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentResourceId = pm.getString("resourceId");
        currentResourceName = pm.getString("resourceName");

        wbs_url = pm.getString("SERVER_URL") + "/getWbs?projectId=\""+currentProjectNo+"\"";


        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        Button createBtn = (Button) findViewById(R.id.createBtn);

        spinner_wbs = (Spinner) findViewById(R.id.spinner_wbs);
        spinner_activity = (Spinner) findViewById(R.id.spinner_activity);

        spinner_wbs.requestFocus();

        text_total_hours = (EditText) findViewById(R.id.text_total_hours);

        TextWatcher watch = new TextWatcher(){

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence s, int a, int b, int c) {
                // TODO Auto-generated method stub
                int quantityVal;
                int unitCostVal;

                if(text_total_hours.getText().toString().equals(""))
                {
                    text_total_hours.setError("Enter Total Hours");
                }
                else if(Float.parseFloat(text_total_hours.getText().toString())>24)
                {
                    text_total_hours.setError("Total Hours cannot be more than 24");
                }
            }};


        text_total_hours.addTextChangedListener(watch);

        select_date = (TextView) findViewById(R.id.select_date);

        select_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        ResourceItemCreate.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setMaxDate(now);
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            LinearLayout main_layout = (LinearLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(ResourceItemCreate.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(ResourceItemCreate.this);
            pDialog.setMessage("Getting cache data");
            pDialog.show();

            Cache cache = AppController.getInstance().getRequestQueue().getCache();
            Cache.Entry entry = cache.get(wbs_url);
            if (entry != null) {
                //Cache data available.
                try {
                    String data = new String(entry.data, "UTF-8");
                    Log.d("CACHE DATA", data);
                    JSONObject jsonObject = new JSONObject(data);
                    try {
                        dataArray = jsonObject.getJSONArray("data");
                        wbsNameArray = new String[dataArray.length() + 1];
                        wbsIdArray = new String[dataArray.length() + 1];
                        wbsNameArray[0] = "Select WBS";
                        wbsIdArray[0] = "";

                        for (int i = 0; i < dataArray.length(); i++) {
                            dataObject = dataArray.getJSONObject(i);
                            wbsName = dataObject.getString("wbsName");
                            wbsId = dataObject.getString("wbsId");

                            wbsNameArray[i + 1] = wbsName;
                            wbsIdArray[i + 1] = wbsId;
                        }

                        ArrayAdapter<String> adapter;

                        if (wbsNameArray == null) {
                            adapter = new ArrayAdapter<String>(ResourceItemCreate.this,
                                    android.R.layout.simple_dropdown_item_1line, new String[]{"No WBS Found"});
                        } else {
                            adapter = new ArrayAdapter<String>(ResourceItemCreate.this,
                                    android.R.layout.simple_dropdown_item_1line, wbsNameArray);
                        }
                        spinner_wbs.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    Toast.makeText(getApplicationContext(), "Loading from cache.", Toast.LENGTH_SHORT).show();
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }
                if (pDialog != null)
                    pDialog.dismiss();
            } else {
                Toast.makeText(ResourceItemCreate.this, "Offline Data Not available for WBS", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }

        else
        {
            getAllWbs();
        }

        spinner_activity.setVisibility(View.GONE);

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

                    String currentSelectedWbs = wbsIdArray[position];

                    prepareActivities(currentSelectedWbs);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spinner_wbs.getSelectedItem().toString().equals("Select WBS"))
                {
                    Toast.makeText(ResourceItemCreate.this, "Select WBS", Toast.LENGTH_SHORT).show();
                }
                else if(spinner_wbs.getSelectedItem().toString().equals("No WBS Found"))
                {
                    Toast.makeText(ResourceItemCreate.this, "No WBS in this Project", Toast.LENGTH_SHORT).show();
                }
                else if(spinner_activity.getSelectedItem().toString().equals("No Activity Found") ||
                        spinner_activity.getSelectedItem().toString().equals(""))
                {
                    Toast.makeText(ResourceItemCreate.this, "No Activity in this WBS", Toast.LENGTH_SHORT).show();
                }
                else if(spinner_activity.getSelectedItem().toString().equals("Select Activity"))
                {
                    Toast.makeText(ResourceItemCreate.this, "Select Activity", Toast.LENGTH_SHORT).show();
                }
                else if (text_total_hours.getText().toString().isEmpty())
                {
                    text_total_hours.setError("Field cannot be empty");
                }
                else if(Float.parseFloat(text_total_hours.getText().toString())>24)
                {
                    text_total_hours.setError("Total Hours cannot be more than 24");
                }
                else if(select_date.getText().toString().equals("Select Date"))
                {
                    select_date.setError("Field cannot be empty");
                }
                else
                {

                    saveResourceLineItem();
                }
            }
        });
    }

    public void saveResourceLineItem()
    {
        JSONObject object = new JSONObject();

        try {
            object.put("resourceTimesheetsId",currentResourceId);
            object.put("wbs",spinner_wbs.getSelectedItem().toString());
            object.put("activities",spinner_activity.getSelectedItem().toString());
            object.put("name",currentResourceName);
            object.put("date",dateTOSendServer);
            object.put("totalHours",text_total_hours.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(ResourceItemCreate.this);

        String url = ResourceItemCreate.this.pm.getString("SERVER_URL") + "/postResourceLineItems";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if(response.getString("msg").equals("success"))
                                Toast.makeText(ResourceItemCreate.this, "Resource Timesheet Line Item has been created", Toast.LENGTH_SHORT).show();
                            pDialog.dismiss();

                            Intent intent = new Intent(ResourceItemCreate.this, ResourceTimesheetActivity.class);
                            startActivity(intent);

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
        if (!isInternetPresent)
        {
            // Internet connection is not present

            Communicator communicator = new Communicator();
            Log.d("object", object.toString());

            Boolean createResourceLineItem = pm.getBoolean("createResourceLineItem");

            if(createResourceLineItem)
            {
                Toast.makeText(ResourceItemCreate.this, "Already a Resource Timesheet Item creation is in progress. Please try after sometime.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(ResourceItemCreate.this, "Internet not currently available. Resource Timesheet Item will automatically get created on internet connection.", Toast.LENGTH_SHORT).show();

                pm.putString("objectResourceLineItem", object.toString());
                pm.putString("urlResourceLineItem", url);
                pm.putString("toastMessageResourceLineItem", "Resource Timesheet Line Item Created");
                pm.putBoolean("createResourceLineItem", true);

                Intent intent = new Intent(ResourceItemCreate.this, ResourceTimesheetActivity.class);
                startActivity(intent);
            }
        }
        else
        {
            requestQueue.add(jor);
        }
    }


    public void getAllWbs()
    {

        // Cache data not exist.
        // TODO Auto-generated method stub

        pDialog = new ProgressDialog(ResourceItemCreate.this);
        pDialog.setMessage("Getting server data");
        pDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, wbs_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {

                            String type = response.getString("type");
                            if(type.equals("INFO"))
                            {
                                Log.d("RESPONSE WBS :", response.toString());
//                            dataObject = response.getJSONObject(0);
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

                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ResourceItemCreate.this,
                                        android.R.layout.simple_dropdown_item_1line, wbsNameArray);
                                spinner_wbs.setAdapter(adapter);
                            }
                            else
                            {

                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ResourceItemCreate.this,
                                        android.R.layout.simple_dropdown_item_1line, new String[]{"No WBS Found"});
                                spinner_wbs.setAdapter(adapter);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        setData(response,false);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
        if(pDialog!=null)
            pDialog.dismiss();

    }


    public void prepareActivities(final String currentWbsId) {

        final ProgressDialog pDialog = new ProgressDialog(ResourceItemCreate.this);


        String activity_url = pm.getString("SERVER_URL") + "/getWbsActivity?wbsId=\"" + currentWbsId + "\"";


        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            LinearLayout main_layout = (LinearLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(ResourceItemCreate.this, R.string.no_internet_error, Style.ALERT, main_layout).show();


            pDialog.setMessage("Getting cache data");
            pDialog.show();

            Cache cache = AppController.getInstance().getRequestQueue().getCache();
            Cache.Entry entry = cache.get(activity_url);
            if (entry != null) {
                //Cache data available.
                try {
                    String data = new String(entry.data, "UTF-8");
                    Log.d("CACHE DATA", data);
                    JSONObject jsonObject = new JSONObject(data);
                    try {
                        dataArray = jsonObject.getJSONArray("data");
                        activityIdArray = new String[dataArray.length() + 1];
                        activitiesNameArray = new String[dataArray.length() + 1];
                        activityIdArray[0] = "Select Activity";
                        activitiesNameArray[0] = "Select Activity";

                        for (int i = 0; i < dataArray.length(); i++) {
                            dataObject = dataArray.getJSONObject(i);

                            activityId = dataObject.getString("id");
                            activityName = dataObject.getString("activityName");

                            activityIdArray[i + 1] = activityId;
                            activitiesNameArray[i + 1] = activityName;
                        }

                        ArrayAdapter<String> adapter;

                        if (activitiesNameArray == null) {
                            adapter = new ArrayAdapter<String>(ResourceItemCreate.this,
                                    android.R.layout.simple_dropdown_item_1line, new String[]{"No Activity Found"});
                        } else {
                            adapter = new ArrayAdapter<String>(ResourceItemCreate.this,
                                    android.R.layout.simple_dropdown_item_1line, activitiesNameArray);
                        }

                        spinner_activity.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    Toast.makeText(getApplicationContext(), "Loading from cache.", Toast.LENGTH_SHORT).show();
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }
                pDialog.dismiss();
            } else {
                Toast.makeText(ResourceItemCreate.this, "Offline Data Not available for Resources", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        } else {
            // Cache data not exist.
            // TODO Auto-generated method stub

            final ProgressDialog progressDialog = new ProgressDialog(ResourceItemCreate.this);
            progressDialog.setMessage("Getting server data");
            progressDialog.show();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, activity_url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
//                            dataObject = response.getJSONObject(0);

                                String type = response.getString("type");

                                if(type.equals("INFO"))
                                {
                                    dataArray = response.getJSONArray("data");
                                    activityIdArray = new String[dataArray.length() + 1];
                                    activitiesNameArray = new String[dataArray.length() + 1];
                                    activityIdArray[0] = "Select Activity";
                                    activitiesNameArray[0] = "Select Activity";

                                    for (int i = 0; i < dataArray.length(); i++) {
                                        dataObject = dataArray.getJSONObject(i);

                                        activityId = dataObject.getString("id");
                                        activityName = dataObject.getString("activityName");

                                        activityIdArray[i + 1] = activityId;
                                        activitiesNameArray[i + 1] = activityName;
                                    }

                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(ResourceItemCreate.this,
                                            android.R.layout.simple_dropdown_item_1line, activitiesNameArray);


                                    spinner_activity.setAdapter(adapter);

                                }

                                else
                                {
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(ResourceItemCreate.this,
                                            android.R.layout.simple_dropdown_item_1line, new String[]{"No Activity Found"});
                                    spinner_activity.setAdapter(adapter);

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
//                        setData(response,false);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(),
                            error.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjectRequest);
            progressDialog.dismiss();

            pDialog.dismiss();

        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String[] MONTHS = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        String date = dayOfMonth+"/"+(MONTHS[monthOfYear])+"/"+year;
        select_date.setText(date);

        dateTOSendServer = year+"-"+(MONTHS[monthOfYear])+"-"+dayOfMonth;
    }
}
