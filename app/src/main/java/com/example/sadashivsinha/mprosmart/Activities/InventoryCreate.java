package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.weiwangcn.betterspinner.library.BetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class InventoryCreate extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    EditText text_project_id, text_project_desc, text_item_desc, text_date;
    BetterSpinner spinner_item_id, spinner_show_transact;
    TextView  text_from_date, text_to_date;
    Button createBtn;
    String whichDate;
    String currentProjectNo, currentProjectName, currentUserId;
    ProgressDialog pDialog;
    JSONArray dataArray;
    JSONObject dataObject;
    PreferenceManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_create);

        pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentProjectName = pm.getString("projectName");
        currentUserId = pm.getString("userId");

        text_project_id = (EditText) findViewById(R.id.text_project_id);
        text_project_desc = (EditText) findViewById(R.id.text_project_desc);
        text_item_desc = (EditText) findViewById(R.id.text_item_desc);
        text_date = (EditText) findViewById(R.id.text_date);
        text_from_date = (TextView) findViewById(R.id.text_from_date);
        text_to_date = (TextView) findViewById(R.id.text_to_date);

        spinner_item_id = (BetterSpinner) findViewById(R.id.spinner_item_id);
        spinner_show_transact = (BetterSpinner) findViewById(R.id.spinner_show_transact);

        text_item_desc.setEnabled(false);

        spinner_item_id.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0)
                {
                    text_item_desc.setText("This is description of Item-001");
                }
                else if (position==1)
                {
                    text_item_desc.setText("This is description of Item-002");
                }
                else if (position==2)
                {
                    text_item_desc.setText("This is description of Item-003");
                }
                else if (position==3)
                {
                    text_item_desc.setText("This is description of Item-004");
                }
            }
        });

        text_from_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                whichDate = "FROM";

                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        InventoryCreate.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });



        text_to_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                whichDate = "TO";

                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        InventoryCreate.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line,
                new String[]{"YES", "NO"});

        spinner_show_transact.setAdapter(adapter);


        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line,
                new String[]{"Item 001", "Item 002", "Item 003", "Item 004"});

        spinner_item_id.setAdapter(adapter2);

        createBtn = (Button) findViewById(R.id.createBtn);

        text_project_id.setText(pm.getString("projectId"));
        text_project_desc.setText(pm.getString("Sample Project Description"));


        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = sdf.format(c.getTime());

        text_date.setText(strDate);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pDialog = new ProgressDialog(InventoryCreate.this);
                pDialog.setMessage("Sending Data ...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();

                prepareItems();
            }
        });
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        String date = dayOfMonth+"/"+(monthOfYear+1)+"/"+year;

        if(whichDate.equals("FROM"))
        {
            text_from_date.setText(date);
        }

        else
        {
            text_to_date.setText(date);
        }
    }

    public void prepareItems() {
        JSONObject object = new JSONObject();

        try {
            object.put("projectId", currentProjectNo);
            object.put("projectDescription", text_project_desc.getText().toString());
            object.put("itemId", spinner_item_id.getText().toString());
            object.put("itemDescription", text_item_desc.getText().toString());
            object.put("date", text_date.getText().toString());
            object.put("showTransaction", spinner_show_transact.getText().toString());
            object.put("fromDate", text_from_date.getText().toString());
            object.put("toDate", text_to_date.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(InventoryCreate.this);

        String url = InventoryCreate.this.pm.getString("SERVER_URL") + "/postInventoryManagement";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(InventoryCreate.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
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
                        Log.e("Volley", "Error");
                        pDialog.dismiss();
                    }
                }
        );
        requestQueue.add(jor);
        Intent intent = new Intent(InventoryCreate.this, AllInventoryManagement.class);
        startActivity(intent);
    }
}
