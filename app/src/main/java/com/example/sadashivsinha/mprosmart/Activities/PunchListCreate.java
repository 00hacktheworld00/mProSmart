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
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PunchListCreate extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    EditText text_project_no, text_created_by;
    Button btn_date;
    ProgressDialog pDialog, pDialog1;
    BetterSpinner spinnerVendorId;
    String currentProjectNo, currentUserName, vendorId, currentUserId;
    JSONArray dataArray;
    JSONObject dataObject;
    String[] vendorIdArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_punch_list_create);

        PreferenceManager pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentUserName = pm.getString("name");
        currentUserId = pm.getString("userId");

        Button createBtn = (Button) findViewById(R.id.createBtn);

        spinnerVendorId = (BetterSpinner) findViewById(R.id.spinnerVendorId);

        text_project_no = (EditText) findViewById(R.id.text_project_no);
        text_created_by = (EditText) findViewById(R.id.text_created_by);

        text_created_by.setText(currentUserId);

        btn_date = (Button) findViewById(R.id.btn_date);

        text_project_no.setText(currentProjectNo);

        pDialog1 = new ProgressDialog(PunchListCreate.this);
        pDialog1.setMessage("Preparing Lists ...");
        pDialog1.setIndeterminate(false);
        pDialog1.setCancelable(true);
        pDialog1.show();

        class MyTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... params) {
                prepareVendorList();
                return null;
            }
        }
        new MyTask().execute();

        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        PunchListCreate.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });


        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(PunchListCreate.this);
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
        });
    }

    public void prepareItems()
    {
        JSONObject object = new JSONObject();

        try {
            object.put("projectId",text_project_no.getText().toString());
            object.put("vendorId",spinnerVendorId.getText().toString());
            object.put("createdBy",text_created_by.getText().toString());

            Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(btn_date.getText().toString());
            String formattedDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

            object.put("createdDate",formattedDate);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(PunchListCreate.this);

        String url = PunchListCreate.this.getResources().getString(R.string.server_url) + "/postPunchLists";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(PunchListCreate.this, response.getString("msg").toString(), Toast.LENGTH_SHORT).show();
                            pDialog.dismiss();
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
                        pDialog.dismiss();
                    }
                }
        );
        requestQueue.add(jor);
        Intent intent = new Intent(PunchListCreate.this, AllPunchLists.class);
        startActivity(intent);
    }

    public void prepareVendorList()
    {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = getResources().getString(R.string.server_url) + "/getVendors";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(PunchListCreate.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                vendorIdArray = new String[dataArray.length()];
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    vendorId = dataObject.getString("vendorId");
                                    vendorIdArray[i]=vendorId;
                                }
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(PunchListCreate.this,
                                    android.R.layout.simple_dropdown_item_1line,vendorIdArray);
                            spinnerVendorId.setAdapter(adapter);
                            pDialog1.dismiss();

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
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String[] MONTHS = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        String date = year+"-"+(MONTHS[monthOfYear])+"-"+dayOfMonth;
        btn_date.setText(date);
    }

}
