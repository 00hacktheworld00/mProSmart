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
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SubmittalRegisterCreateActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{


    EditText text_desc, text_status;
    ProgressDialog pDialog;
    String currentProjectNo, currentProjectName, currentUserId, selected_btn, currentDate;
    BetterSpinner spinnerPriority;
    String[] priorityArray;
    Button select_start_date, select_end_date, createBtn;
    PreferenceManager pm;

    ConnectionDetector cd;
    public static final String TAG = SubmittalRegisterCreateActivity.class.getSimpleName();
    Boolean isInternetPresent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submittal_register_create);


        pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentProjectName = pm.getString("projectName");
        currentUserId = pm.getString("userId");

        createBtn = (Button) findViewById(R.id.createBtn);

        select_start_date = (Button) findViewById(R.id.select_start_date);
        select_end_date = (Button) findViewById(R.id.select_end_date);

        select_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_btn = "start";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        SubmittalRegisterCreateActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setMinDate(now);
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        select_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SubmittalRegisterCreateActivity.this, "Select start date first", Toast.LENGTH_SHORT).show();
            }
        });

        text_desc = (EditText) findViewById(R.id.text_desc);
        text_status = (EditText) findViewById(R.id.text_status);

        text_status.setText("PENDING");
        text_status.setEnabled(false);

        spinnerPriority = (BetterSpinner) findViewById(R.id.spinnerPriority);

        priorityArray = new String[]{"LOW","MEDIUM","HIGH"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SubmittalRegisterCreateActivity.this,
                android.R.layout.simple_dropdown_item_1line,priorityArray);
        spinnerPriority.setAdapter(adapter);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = sdf.format(c.getTime());

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (spinnerPriority.getText().toString().isEmpty()) {
                    Toast.makeText(SubmittalRegisterCreateActivity.this, "Select Priority First", Toast.LENGTH_SHORT).show();
                } else if (select_start_date.getText().toString().isEmpty()) {
                    select_start_date.setError("Select Start Date");
                } else if (select_end_date.getText().toString().isEmpty()) {
                    select_end_date.setError("Select End Date");
                } else if (text_desc.getText().toString().isEmpty()) {
                    text_desc.setError("Field cannot be empty");
                } else {

                    prepareItems();
                }
            }
        });
    }
    public void prepareItems()
    {
        JSONObject object = new JSONObject();

        try {
            object.put("projectId",currentProjectNo);
            object.put("Description",text_desc.getText().toString());
            object.put("priority",spinnerPriority.getText().toString());
            object.put("startDate",select_start_date.getText().toString());
            object.put("EndDate",select_end_date.getText().toString());
            object.put("Status",text_status.getText().toString());
            object.put("createdDate",currentDate);
            object.put("createdBy", currentUserId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(SubmittalRegisterCreateActivity.this);

        String url = SubmittalRegisterCreateActivity.this.getResources().getString(R.string.server_url) + "/postSubmittalRegisters";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("msg").equals("success"))
                            {
                                Toast.makeText(SubmittalRegisterCreateActivity.this, "Submittal Resgiter Created. ID - " + response.getString("data"), Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(SubmittalRegisterCreateActivity.this, SubmittalRegisterActivity.class);
                                pm.putString("currentProjectName",currentProjectName);
                                pm.putString("submittalRegistersId",response.getString("data"));
                                pm.putString("projectId",currentProjectNo);
                                pm.putString("startDate",select_start_date.getText().toString());
                                pm.putString("EndDate",select_end_date.getText().toString());
                                pm.putString("createdDate",currentDate);
                                pm.putString("Status",text_status.getText().toString());
                                pm.putString("priority",spinnerPriority.getText().toString());
                                pm.putString("createdBy",currentUserId);

                                startActivity(intent);
                            }
                            else
                            {
                                Toast.makeText(SubmittalRegisterCreateActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(SubmittalRegisterCreateActivity.this, AllSubmittalsRegister.class);
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
                        Log.e("Volley","Error");
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

            Boolean createSubmittalRegisterPending = pm.getBoolean("createSubmittalRegisterPending");

            if(createSubmittalRegisterPending)
            {
                Toast.makeText(SubmittalRegisterCreateActivity.this, "Already a Submittal Register creation is in progress. Please try after sometime.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(SubmittalRegisterCreateActivity.this, "Internet not currently available. Submittal Register will automatically get created on internet connection.", Toast.LENGTH_SHORT).show();

                pm.putString("objectSubmittalRegister", object.toString());
                pm.putString("urlSubmittalRegister", url);
                pm.putString("toastMessageSubmittalRegister", "Submittal Register Created");
                pm.putBoolean("createSubmittalRegisterPending", true);

                Intent intent = new Intent(SubmittalRegisterCreateActivity.this, AllSubmittalsRegister.class);
                startActivity(intent);
            }
        }
        else {
            requestQueue.add(jor);
        }

        }

    @Override
    public void onDateSet(DatePickerDialog view, final int year, final int monthOfYear, final int dayOfMonth) {
        String[] MONTHS = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        final String date = year+"-"+(MONTHS[monthOfYear])+"-"+dayOfMonth;
        if(selected_btn.equals("start"))
        {
            select_start_date.setText(date);

            select_end_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selected_btn = "end";
                    Calendar now = Calendar.getInstance();
                    DatePickerDialog dpd = DatePickerDialog.newInstance(
                            SubmittalRegisterCreateActivity.this,
                            year,
                            monthOfYear,
                            dayOfMonth+1
                    );

                    now.set(year, monthOfYear, dayOfMonth+1);
                    dpd.setMinDate(now);
                    dpd.show(getFragmentManager(), "Datepickerdialog");
                }
            });

        }
        else if(selected_btn.equals("end"))
        {
            select_end_date.setText(date);
        }
    }
}