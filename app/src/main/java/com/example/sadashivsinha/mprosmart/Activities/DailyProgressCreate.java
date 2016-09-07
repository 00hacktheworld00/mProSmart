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
import com.thomashaertel.widget.MultiSpinner;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class DailyProgressCreate extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    Spinner spinner_wbs, spinner_activity;
    String[] wbsNameArray, resourceArray;
    String currentProjectNo;
    ProgressDialog pDialog;
    String wbsName, firstName, lastName, fullName;
    JSONArray dataArray;
    JSONObject dataObject;
    MultiSpinner spinner_resource;
    ArrayAdapter<String> adapter;
    TextView target_date;
    EditText weather, progress, percent_completed, completed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_progress_create);

        PreferenceManager pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");

        weather = (EditText) findViewById(R.id.weather);
        progress = (EditText) findViewById(R.id.progress);
        percent_completed = (EditText) findViewById(R.id.percent_completed);
        completed = (EditText) findViewById(R.id.completed);

        Button createBtn = (Button) findViewById(R.id.createBtn);

        target_date = (TextView) findViewById(R.id.target_date);

        spinner_wbs = (Spinner) findViewById(R.id.spinner_wbs);
        spinner_activity = (Spinner) findViewById(R.id.spinner_activity);

        spinner_resource = (MultiSpinner) findViewById(R.id.spinner_resource);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(weather.getText().toString().isEmpty())
                {
                    weather.setError("Field cannot be empty");
                }
                else if(progress.getText().toString().isEmpty())
                {
                    progress.setError("Field cannot be empty");
                }
                else if(percent_completed.getText().toString().isEmpty())
                {
                    percent_completed.setError("Field cannot be empty");
                }
                else if(completed.getText().toString().isEmpty())
                {
                    completed.setError("Field cannot be empty");
                }
                else if(target_date.getText().toString().isEmpty())
                {
                    target_date.setError("Select Target Date");
                }

                else if(spinner_wbs.getSelectedItem().toString().equals("Select WBS"))
                {
                    Toast.makeText(DailyProgressCreate.this, "Select WBS", Toast.LENGTH_SHORT).show();
                }

                else if(spinner_activity.getSelectedItem().toString().equals("Select Activity"))
                {
                    Toast.makeText(DailyProgressCreate.this, "Select Activity", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent intent = new Intent(DailyProgressCreate.this, DailyProgressActivity.class);
                    Toast.makeText(DailyProgressCreate.this, "Daily Progress Created", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }
            }
        });

        pDialog = new ProgressDialog(DailyProgressCreate.this);
        pDialog.setMessage("Getting WBS...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        getAllActivities();

        class MyTask extends AsyncTask<Void, Void, Void>
        {
            @Override
            protected Void doInBackground(Void... params)
            {
                getAllWbs();
                pDialog.setMessage("Getting Resources...");
                getAllResources();
                return null;
            }
        }

        new MyTask().execute();


        target_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        DailyProgressCreate.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setMinDate(now);
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });
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
                                Toast.makeText(DailyProgressCreate.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                wbsNameArray = new String[dataArray.length()+1];
                                wbsNameArray[0]= "Select WBS";
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    wbsName = dataObject.getString("wbsName");
                                    wbsNameArray[i+1]=wbsName;
                                }
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(DailyProgressCreate.this,
                                    android.R.layout.simple_dropdown_item_1line,wbsNameArray);
                            spinner_wbs.setAdapter(adapter);

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
        requestQueue.add(jor);

    }


    public void getAllResources()
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
                                Toast.makeText(DailyProgressCreate.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");

                                adapter = new ArrayAdapter<String>(DailyProgressCreate.this,
                                                android.R.layout.simple_spinner_item);

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    firstName = dataObject.getString("firstName");
                                    lastName = dataObject.getString("lastName");

                                    fullName = firstName + " " + lastName;

                                    adapter.add(fullName);
                                }
                            }

                            spinner_resource.setAdapter(adapter, false, onSelectedListener);
//                            boolean[] selectedItems = new boolean[adapter.getCount()];
//                            selectedItems[1] = true; // select second item
//                            spinner_resource.setSelected(selectedItems);

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
        if (pDialog!=null)
            pDialog.dismiss();

        requestQueue.add(jor);

    }

    private MultiSpinner.MultiSpinnerListener onSelectedListener = new MultiSpinner.MultiSpinnerListener() {
        public void onItemsSelected(boolean[] selected) {
            // Do something here with the selected items
        }
    };

    public void getAllActivities()
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(DailyProgressCreate.this,
                android.R.layout.simple_dropdown_item_1line,
                new String[] {"Select Activity", "Activity-1", "Activity-2", "Activity-3"});
        spinner_activity.setAdapter(adapter);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        String[] MONTHS = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        String date = (MONTHS[monthOfYear]) + "/" + dayOfMonth + "/" + year;

        target_date.setText(date);
    }
}
