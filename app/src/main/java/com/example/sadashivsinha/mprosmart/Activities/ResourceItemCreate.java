package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
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
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class ResourceItemCreate extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    String currentProjectNo, currentResourceId, currentResourceName;
    EditText text_total_hours;
    TextView select_date;
    Spinner spinner_wbs, spinner_activity;
    ProgressDialog pDialog;
    JSONArray dataArray;
    JSONObject dataObject;
    String dateTOSendServer;

    String[] wbsNameArray, activitiesNameArray, activityIdArray, wbsIdArray;
    String wbsName, activityName, activityId, wbsId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_item_create);

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

        PreferenceManager pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentResourceId = pm.getString("resourceId");
        currentResourceName = pm.getString("resourceName");

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
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        pDialog = new ProgressDialog(ResourceItemCreate.this);
        pDialog.setMessage("Getting WBS...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        class MyTask extends AsyncTask<Void, Void, Void>
        {
            @Override
            protected Void doInBackground(Void... params)
            {
                getAllWbs();
                return null;
            }
        }

        new MyTask().execute();

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

                    pDialog = new ProgressDialog(ResourceItemCreate.this);
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

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spinner_wbs.getSelectedItem().toString().equals("Select WBS"))
                {
                    Toast.makeText(ResourceItemCreate.this, "Select WBS", Toast.LENGTH_SHORT).show();
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
                    pDialog = new ProgressDialog(ResourceItemCreate.this);
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
            }
        });
    }

    public void prepareItems()
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

        String url = ResourceItemCreate.this.getResources().getString(R.string.server_url) + "/postResourceLineItems";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if(response.getString("msg").equals("success"))
                                Toast.makeText(ResourceItemCreate.this, "Resource Timesheet Line Item has been created", Toast.LENGTH_SHORT).show();
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
        if(pDialog!=null)
            pDialog.dismiss();

        Intent intent = new Intent(ResourceItemCreate.this, ResourceTimesheetActivity.class);
        startActivity(intent);
    }


    public void getAllWbs()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = getResources().getString(R.string.server_url) + "/getWbs?projectId=\""+currentProjectNo+"\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(ResourceItemCreate.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
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
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ResourceItemCreate.this,
                                    android.R.layout.simple_dropdown_item_1line,wbsNameArray);
                            spinner_wbs.setAdapter(adapter);

                        }
                        catch(JSONException e){
                            e.printStackTrace();}

                        pDialog.dismiss();
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
        if(pDialog!=null)
            pDialog.dismiss();

        requestQueue.add(jor);

    }


    public void prepareActivities(final String currentWbsId, final ProgressDialog pDialog)
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = getString(R.string.server_url) + "/getWbsActivity?wbsId=\""+currentWbsId+"\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{
                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                pDialog.dismiss();
                                Toast.makeText(ResourceItemCreate.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
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
                                    adapter = new ArrayAdapter<String>(ResourceItemCreate.this,
                                            android.R.layout.simple_dropdown_item_1line, new String[] {"No Activity Found"});
                                }
                                else
                                {
                                    adapter = new ArrayAdapter<String>(ResourceItemCreate.this,
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

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String[] MONTHS = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        String date = dayOfMonth+"/"+(MONTHS[monthOfYear])+"/"+year;
        select_date.setText(date);

        dateTOSendServer = year+"-"+(MONTHS[monthOfYear])+"-"+dayOfMonth;
    }
}
