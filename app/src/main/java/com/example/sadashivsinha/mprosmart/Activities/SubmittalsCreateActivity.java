package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.weiwangcn.betterspinner.library.BetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class SubmittalsCreateActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    EditText text_desc;
    String submittalno, submittalRegister, status, type, desc, dueDate, dateRegister;
    String currentProjectNo, currentUserName, currentDate;
    ProgressDialog pDialog, pDialog1;
    Spinner spinnerSubReg;
    String submittalRegistersId, formattedDate;
    JSONArray dataArray;
    JSONObject dataObject;
    String[] submittalRegArray;
    Button select_due_date, createBtn;
    ConnectionDetector cd;
    public static final String TAG = SubmittalsCreateActivity.class.getSimpleName();
    Boolean isInternetPresent = false;
    PreferenceManager pm;
    String url;
    BetterSpinner spinner_status, spinner_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submittals_create);

        pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentUserName = pm.getString("userId");

        createBtn = (Button) findViewById(R.id.createBtn);

        select_due_date = (Button) findViewById(R.id.select_due_date);

        select_due_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        SubmittalsCreateActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        spinner_status = (BetterSpinner) findViewById(R.id.spinner_status);
        spinner_type = (BetterSpinner) findViewById(R.id.spinner_type);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SubmittalsCreateActivity.this,
                android.R.layout.simple_dropdown_item_1line,new String[] {"ACTIVE", "INACTIVE"});
        spinner_status.setAdapter(adapter);


        adapter = new ArrayAdapter<String>(SubmittalsCreateActivity.this,
                android.R.layout.simple_dropdown_item_1line,new String[] {"INCOMING", "OUTGOING"});
        spinner_type.setAdapter(adapter);

        text_desc = (EditText) findViewById(R.id.text_desc);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = sdf.format(c.getTime());

        spinnerSubReg = (Spinner) findViewById(R.id.spinnerSubReg);

        url = getResources().getString(R.string.server_url) + "/getSubmittialRegister?projectId='"+currentProjectNo+"'";

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            LinearLayout main_layout = (LinearLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(SubmittalsCreateActivity.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(SubmittalsCreateActivity.this);
            pDialog.setMessage("Getting cache data");
            pDialog.show();

            Cache cache = AppController.getInstance().getRequestQueue().getCache();
            Cache.Entry entry = cache.get(url);
            if (entry != null) {
                //Cache data available.
                try {
                    String data = new String(entry.data, "UTF-8");
                    Log.d("CACHE DATA", data);
                    JSONObject jsonObject = new JSONObject(data);
                    try {
                        dataArray = jsonObject.getJSONArray("data");

                        submittalRegArray = new String[dataArray.length()+1];

                        submittalRegArray[0] = "Select Submittal Register";

                        for (int i = 0; i < dataArray.length(); i++)
                        {
                            dataObject = dataArray.getJSONObject(i);
                            submittalRegistersId = dataObject.getString("submittalRegistersId");

                            submittalRegArray[i+1]=submittalRegistersId;

                            adapter = new ArrayAdapter<String>(SubmittalsCreateActivity.this,
                                    android.R.layout.simple_dropdown_item_1line,submittalRegArray);
                            spinnerSubReg.setAdapter(adapter);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    Toast.makeText(getApplicationContext(), "Loading from cache.", Toast.LENGTH_SHORT).show();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (pDialog != null)
                    pDialog.dismiss();
            }

            else
            {
                Toast.makeText(SubmittalsCreateActivity.this, "Offline Data Not available for Submittal Registers", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }

        else
        {
            // Cache data not exist.
            prepareSubmittalRegisterList();
        }

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(spinnerSubReg.getSelectedItem().toString().equals("Select Submittal Register"))
                {
                    Toast.makeText(SubmittalsCreateActivity.this, "Select Submittal Register", Toast.LENGTH_SHORT).show();
                }
                else if(spinner_status.getText().toString().isEmpty())
                {
                    Toast.makeText(SubmittalsCreateActivity.this, "Select Status", Toast.LENGTH_SHORT).show();
                }
                else if(spinner_type.getText().toString().isEmpty())
                {
                    Toast.makeText(SubmittalsCreateActivity.this, "Select Type", Toast.LENGTH_SHORT).show();
                }
                else if(text_desc.getText().toString().isEmpty())
                {
                    text_desc.setError("Field cannot be empty");
                }
                else if(select_due_date.getText().toString().isEmpty())
                {
                    Toast.makeText(SubmittalsCreateActivity.this, "Select Due Date", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    pDialog = new ProgressDialog(SubmittalsCreateActivity.this);
                    pDialog.setMessage("Sending Data ...");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(true);
                    pDialog.show();

                    submittalRegister = spinnerSubReg.getSelectedItem().toString();
                    status = spinner_status.getText().toString();
                    type = spinner_type.getText().toString();
                    desc = text_desc.getText().toString();

                    saveSubmittals();
                }
            }
        });
    }

    public void saveSubmittals()
    {
        JSONObject object = new JSONObject();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String strDate = sdf.format(c.getTime());

        try {
            object.put("submittalId",submittalno);
            object.put("projectId",currentProjectNo);
            object.put("submittalRegisterId",submittalRegister);
            object.put("createdDate",currentDate);
            object.put("dueDate",formattedDate);
            object.put("createdBy",currentUserName);
            object.put("description",desc);

            if(type.equals("INCOMING"))
            {
                type = "1";
            }
            else
            {
                type = "2";
            }


            if(status.equals("ACTIVE"))
            {
                status = "1";
            }
            else
            {
                status = "2";
            }

            object.put("submittalsType",type);
            object.put("status",status);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(SubmittalsCreateActivity.this);

        String url = SubmittalsCreateActivity.this.getResources().getString(R.string.server_url) + "/postSubmittals";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            Log.d("RESPONSE SERVER : ", response.toString());

                            if(response.getString("msg").equals("success"))
                            {
                                Toast.makeText(SubmittalsCreateActivity.this, "Submittals created ID - " + response.getString("data"), Toast.LENGTH_SHORT).show();
                            }

                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        //response success message display
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley","Error");
                        Toast.makeText(SubmittalsCreateActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        if (!isInternetPresent)
        {
            // Internet connection is not present

            Communicator communicator = new Communicator();
            Log.d("object", object.toString());

            Boolean createSubmittalPending = pm.getBoolean("createSubmittalPending");

            if(createSubmittalPending)
            {
                Toast.makeText(SubmittalsCreateActivity.this, "Already a Submittal creation is in progress. Please try after sometime.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(SubmittalsCreateActivity.this, "Internet not currently available. Submittal will automatically get created on internet connection.", Toast.LENGTH_SHORT).show();

                pm.putString("objectSubmittal", object.toString());
                pm.putString("urlSubmittal", url);
                pm.putString("toastMessageSubmittal", "Submittal Created");
                pm.putBoolean("createSubmittalPending", true);
            }
        }
        else
        {
            requestQueue.add(jor);
        }
        if(pDialog!=null)
            pDialog.dismiss();

        Intent intent = new Intent(SubmittalsCreateActivity.this, AllSubmittals.class);
        startActivity(intent);
    }


    public void prepareSubmittalRegisterList()
    {
        pDialog = new ProgressDialog(SubmittalsCreateActivity.this);
        pDialog.setMessage("Getting server data");
        pDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {
//                            dataObject = response.getJSONObject(0);
                            dataArray = response.getJSONArray("data");
                            submittalRegArray = new String[dataArray.length()+1];

                            submittalRegArray[0] = "Select Submittal Register";

                            for(int i=0; i<dataArray.length();i++)
                            {
                                dataObject = dataArray.getJSONObject(i);
                                submittalRegistersId = dataObject.getString("submittalRegistersId");

                                submittalRegArray[i+1]=submittalRegistersId;
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(SubmittalsCreateActivity.this,
                                    android.R.layout.simple_dropdown_item_1line,submittalRegArray);
                            spinnerSubReg.setAdapter(adapter);
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

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String[] MONTHS = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};

        formattedDate = year+"-"+(MONTHS[monthOfYear])+"-"+dayOfMonth;

        String date = dayOfMonth+"-"+(MONTHS[monthOfYear])+"-"+year;
        select_due_date.setText(date);
    }
}