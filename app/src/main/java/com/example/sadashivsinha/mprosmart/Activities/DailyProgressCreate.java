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
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class DailyProgressCreate extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    Spinner spinner_wbs, spinner_activity;
    String[] wbsNameArray, wbsIdArray, activityIdArray, activityNameArray, resourceIdArray, resourceNameArray;
    String currentProjectNo, id;
    ProgressDialog pDialog;
    JSONArray dataArray;
    JSONObject dataObject;
    ArrayAdapter<String> adapter;
    String currentWbsId, currentActivityId, wbsName, wbsId, dateToSend;
    TextView text_date;
    PreferenceManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_progress_create);

        pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");

        Button createBtn = (Button) findViewById(R.id.createBtn);

        text_date = (TextView) findViewById(R.id.text_date);

        spinner_wbs = (Spinner) findViewById(R.id.spinner_wbs);
        spinner_activity = (Spinner) findViewById(R.id.spinner_activity);

        spinner_activity.setVisibility(View.GONE);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spinner_wbs.getSelectedItem().toString().equals("Select WBS"))
                {
                    Toast.makeText(DailyProgressCreate.this, "Select WBS", Toast.LENGTH_SHORT).show();
                }
                else if(spinner_wbs.getSelectedItem().toString().equals("No DATA"))
                {
                    Toast.makeText(DailyProgressCreate.this, "NO DATA FOR WBS", Toast.LENGTH_SHORT).show();
                }
                else if(spinner_activity.getSelectedItem().toString().equals("No DATA"))
                {
                    Toast.makeText(DailyProgressCreate.this, "NO DATA FOR ACTIVITIES", Toast.LENGTH_SHORT).show();
                }
                else if(spinner_activity.getSelectedItem().toString().equals("Select Activity"))
                {
                    Toast.makeText(DailyProgressCreate.this, "Select Activity", Toast.LENGTH_SHORT).show();
                }
                else if(text_date.getText().toString().equals("Date"))
                {
                    Toast.makeText(DailyProgressCreate.this, "Select Date to View Report", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent intent = new Intent(DailyProgressCreate.this, DailyProgressDetails.class);
                    startActivity(intent);
                }
            }
        });

        text_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        DailyProgressCreate.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setMaxDate(now);
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        pDialog = new ProgressDialog(DailyProgressCreate.this);
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
                    spinner_activity.setVisibility(View.GONE);
                    spinner_activity.setVisibility(View.VISIBLE);

                    currentWbsId = wbsIdArray[position];

                    pDialog = new ProgressDialog(DailyProgressCreate.this);
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

        spinner_activity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position!=0)
                {
                    currentActivityId = activityIdArray[position];
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
                                Toast.makeText(DailyProgressCreate.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {

                                JSONArray dataArray = response.getJSONArray("data");
                                JSONObject dataObject;
                                activityIdArray = new String[dataArray.length()+1];
                                activityNameArray = new String[dataArray.length()+1];
                                activityIdArray[0]= "Select Activity";
                                activityNameArray[0]= "Select Activity";

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);

                                    activityIdArray[i+1] = dataObject.getString("id");
                                    activityNameArray[i+1] = dataObject.getString("activityName");
                                }

                                ArrayAdapter<String> adapter;

                                if(activityNameArray==null)
                                {
                                    adapter = new ArrayAdapter<String>(DailyProgressCreate.this,
                                            android.R.layout.simple_dropdown_item_1line, new String[] {"No DATA"});
                                }
                                else
                                {
                                    adapter = new ArrayAdapter<String>(DailyProgressCreate.this,
                                            android.R.layout.simple_dropdown_item_1line, activityNameArray);
                                }

                                spinner_activity.setAdapter(adapter);

                                pDialog.dismiss();

                            }
                            
                            if(type.equals("WARN"))
                            {
                                Toast.makeText(DailyProgressCreate.this, response.getString("msg"), Toast.LENGTH_SHORT).show();

                                pDialog.dismiss();
                            }
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
                                Toast.makeText(DailyProgressCreate.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                wbsNameArray = new String[dataArray.length()+1];
                                wbsIdArray = new String[dataArray.length()+1];

                                wbsNameArray[0]= "Select WBS";
                                wbsIdArray[0]= "0";
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    wbsName = dataObject.getString("wbsName");
                                    wbsId = dataObject.getString("wbsId");

                                    wbsNameArray[i+1]=wbsName;
                                    wbsIdArray[i+1]=wbsId;
                                }

                                if(dataArray!=null)
                                {
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(DailyProgressCreate.this,
                                            android.R.layout.simple_dropdown_item_1line,wbsNameArray);
                                    spinner_wbs.setAdapter(adapter);
                                }
                                else
                                {
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(DailyProgressCreate.this,
                                            android.R.layout.simple_dropdown_item_1line, new String[] {"No DATA"});
                                    spinner_wbs.setAdapter(adapter);
                                }

                                pDialog.dismiss();
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
        requestQueue.add(jor);

    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        String[] MONTHS = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        String date = (dayOfMonth + "-" + MONTHS[monthOfYear]) + "-" + year;

        dateToSend = (year + "-" + MONTHS[monthOfYear]) + "-" + dayOfMonth;

        text_date.setText(date);
    }
}
