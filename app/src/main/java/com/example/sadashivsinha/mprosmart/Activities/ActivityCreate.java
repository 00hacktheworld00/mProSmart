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
import com.example.sadashivsinha.mprosmart.Utils.Communicator;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.thomashaertel.widget.MultiSpinner;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class ActivityCreate extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    EditText activity_name;
    TextView start_date, end_date;
    Spinner spinner_boq;
    String whichDate;
    String[] resourceIdArray, resourceNameArray;
    JSONArray dataArray;
    JSONObject dataObject;
    private ProgressDialog pDialog;
    PreferenceManager pm;
    String currentWbsId, currentProjectNo;
    String itemId, itemNames, currentResourceId, currentBoqId;
    String[] itemsNameArray, itemIdArray;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    MultiSpinner multi_spinner_resource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_create);

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        activity_name = (EditText) findViewById(R.id.activity_name);
        start_date = (TextView) findViewById(R.id.start_date);
        end_date = (TextView) findViewById(R.id.end_date);
        multi_spinner_resource = (MultiSpinner) findViewById(R.id.multi_spinner_resource);
        spinner_boq = (Spinner) findViewById(R.id.spinner_boq);

        Button createBtn = (Button) findViewById(R.id.createBtn);

        pm = new PreferenceManager(getApplicationContext());
        currentWbsId = pm.getString("wbsId");
        currentProjectNo = pm.getString("projectId");

        start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whichDate = "start";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        ActivityCreate.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );

                dpd.setMinDate(now);

                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });


        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Getting Resources...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        class MyTask extends AsyncTask<Void, Void, Void>
        {
            @Override
            protected Void doInBackground(Void... params)
            {
                getAllResources();
                prepareItemList();
                return null;
            }
        }

        new MyTask().execute();

//        spinner_resource.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                currentResourceId = resourceIdArray[position];
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        spinner_boq.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0)
                {
                    currentBoqId = itemIdArray[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activity_name.getText().toString().isEmpty())
                {
                    activity_name.setError("Field cannot be empty");
                }
                else if(start_date.getText().toString().isEmpty())
                {
                    start_date.setError("Field cannot be empty");
                }
                else if(end_date.getText().toString().isEmpty())
                {
                    end_date.setError("Field cannot be empty");
                }
                else if(multi_spinner_resource.getText().toString().equals("Select Resources"))
                {
                    Toast.makeText(ActivityCreate.this, "Select Resource", Toast.LENGTH_SHORT).show();
                }
//                else if(spinner_boq.getSelectedItem().toString().equals("Select BOQ"))
//                {
//                    Toast.makeText(ActivityCreate.this, "Select BOQ", Toast.LENGTH_SHORT).show();
//                }

                else
                {
                    pDialog = new ProgressDialog(ActivityCreate.this);
                    pDialog.setMessage("Sending Data ...");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(true);
                    pDialog.show();

                    class MyTask extends AsyncTask<Void, Void, Void> {

                        @Override
                        protected Void doInBackground(Void... params) {

                            saveActivity();
                            return null;
                        }
                    }

                    new MyTask().execute();
                }
            }
        });
    }

    public void saveActivity() {
        JSONObject object = new JSONObject();

        try {
            object.put("wbsId",currentWbsId);
            object.put("activityName", activity_name.getText().toString());
            object.put("resourceAllocated", multi_spinner_resource.getText().toString());

            if(!spinner_boq.getSelectedItem().toString().equals("Select BOQ"))
            {
                object.put("boq", currentBoqId);
            }
            Date tradeDate = null;
            String startDate, endDate;
            try
            {
                tradeDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(start_date.getText().toString());
                startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(tradeDate);

                tradeDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(end_date.getText().toString());
                endDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(tradeDate);

                object.put("startDate", startDate);
                object.put("endDate", endDate);

            } catch (ParseException e)

            {
                e.printStackTrace();
            }

            Log.d("REQUEST SENT OF JSON :" , object.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(ActivityCreate.this);

        String url = pm.getString("SERVER_URL") + "/postWbsActivity";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {
                            if(response.getString("msg").equals("success"))
                            {
                                Toast.makeText(ActivityCreate.this, "Activity Created. ID - "+ response.getString("data"), Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(ActivityCreate.this, WbsActivity.class);
                                startActivity(intent);
                            }
                            else
                            {
                                Toast.makeText(ActivityCreate.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }
                            pDialog.dismiss();

                        }
                        catch (JSONException e) {
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

        if (!isInternetPresent)
        {
            // Internet connection is not present

            Communicator communicator = new Communicator();
            Log.d("object", object.toString());

            Boolean createWbsActivityPending = pm.getBoolean("createWbsActivityPending");

            if(createWbsActivityPending)
            {
                Toast.makeText(ActivityCreate.this, "Already a Wbs Activity creation is in progress. Please try after sometime.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(ActivityCreate.this, "Internet not currently available. Wbs Activity will automatically get created on internet connection.", Toast.LENGTH_SHORT).show();

                pm.putString("objectWbsActivity", object.toString());
                pm.putString("urlWbsActivity", url);
                pm.putString("toastMessageWbsActivity", "Wbs Activity Created");
                pm.putBoolean("createWbsActivityPending", true);

                if(pDialog!=null)
                    pDialog.dismiss();

                Intent intent = new Intent(ActivityCreate.this, WbsActivity.class);
                startActivity(intent);
            }
        }
        else
        {
            requestQueue.add(jor);
        }
    }

    public void prepareItemList()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = pm.getString("SERVER_URL") + "/getBoq?projectId='"+currentProjectNo+"'";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");
                            String msg = response.getString("msg");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(ActivityCreate.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                itemIdArray = new String[dataArray.length()+1];
                                itemsNameArray = new String[dataArray.length()+1];

                                itemsNameArray[0]="Select BOQ";
                                itemIdArray[0]="Select BOQ";

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);

                                    itemId = dataObject.getString("id");
                                    itemNames = dataObject.getString("itemName");

                                    itemIdArray[i+1]=itemId;
                                    itemsNameArray[i+1]=itemNames;
                                }

                                if(dataArray==null)
                                {
                                    itemsNameArray = new String[1];
                                    itemsNameArray[0]="No BOQ";
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(ActivityCreate.this,
                                            android.R.layout.simple_dropdown_item_1line,itemsNameArray);

                                    spinner_boq.setAdapter(adapter);
                                }
                                else
                                {
                                    itemsNameArray[0]="Select BOQ";
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(ActivityCreate.this,
                                            android.R.layout.simple_dropdown_item_1line,itemsNameArray);

                                    spinner_boq.setAdapter(adapter);
                                }
                            }
                            else
                            {
                                itemsNameArray = new String[1];
                                itemsNameArray[0]="No BOQ";
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ActivityCreate.this,
                                        android.R.layout.simple_dropdown_item_1line,itemsNameArray);

                                spinner_boq.setAdapter(adapter);
                            }

                            if(pDialog != null)
                                pDialog.dismiss();

                        }catch(JSONException e){e.printStackTrace();}
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
        if(pDialog != null)
            pDialog.dismiss();
    }


    public void getAllResources()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = pm.getString("SERVER_URL") + "/getResource";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{
                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(ActivityCreate.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                resourceIdArray = new String[dataArray.length()+1];
                                resourceNameArray = new String[dataArray.length()+1];

                                resourceIdArray[0] = "Select Resource";
                                resourceNameArray[0] = "Select Resource";

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    resourceIdArray[i+1]= dataObject.getString("id");
                                    resourceNameArray[i+1]= dataObject.getString("firstName") + " " + dataObject.getString("lastName");
                                }

                                if(dataArray==null)
                                {
                                    resourceNameArray = new String[1];
                                    resourceNameArray[0]="No Resource";

//                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(ActivityCreate.this,
//                                      android.R.layout.simple_dropdown_item_1line,resourceNameArray);

                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(ActivityCreate.this, android.R.layout.simple_spinner_item);
                                    adapter.addAll(resourceNameArray);
                                    multi_spinner_resource.setAdapter(adapter, false, onSelectedListener);

                                    multi_spinner_resource.setText("No Resource");
                                }
                                else
                                {
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(ActivityCreate.this, android.R.layout.simple_spinner_item);
                                    adapter.addAll(resourceNameArray);
                                    adapter.addAll(resourceNameArray);
                                    multi_spinner_resource.setAdapter(adapter, false, onSelectedListener);

                                    multi_spinner_resource.setText("Select Resources");
                                }
                            }
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

    @Override
    public void onDateSet(DatePickerDialog view, final int year, final int monthOfYear, final int dayOfMonth) {
        String[] MONTHS = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        String date = dayOfMonth + "-" + (MONTHS[monthOfYear])  + "-" + year;

        if(whichDate.equals("start"))
        {
            start_date.setText(date);

            end_date.setText(date);

            end_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    whichDate = "end";
                    Calendar now = Calendar.getInstance();
                    now.set(year, monthOfYear, dayOfMonth+1);

                    DatePickerDialog dpd = DatePickerDialog.newInstance(
                            ActivityCreate.this,
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                    );

                    dpd.setMinDate(new GregorianCalendar(year, monthOfYear, dayOfMonth));

                    dpd.show(getFragmentManager(), "Datepickerdialog");
                }
            });
        }
        else
        {
            end_date.setText(date);
        }
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ActivityCreate.this, WbsActivity.class);
        startActivity(intent);
    }
}
