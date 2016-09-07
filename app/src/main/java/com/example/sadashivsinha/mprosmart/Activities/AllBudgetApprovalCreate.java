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
import com.example.sadashivsinha.mprosmart.font.HelveticaBold;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AllBudgetApprovalCreate extends AppCompatActivity {

    Spinner spinner_wbs;
    EditText contract_ref;
    EditText text_budget_amount;
    TextView text_currency, text_start_date, text_end_date;
    String[] wbsNameArray, activitiesNameArray, activityIdArray, wbsIdArray, currencyCodeArray, totalBudgetArray;
    String wbsName, activityName, activityId, wbsId, currencyCode, totalBudget;
    JSONArray dataArray;
    JSONObject dataObject;
    ProgressDialog pDialog;
    String currentProjectNo, currentUser, currentDate;
    HelveticaBold title_start_date, title_end_date;
    Button createBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_budget_approval_create);

        PreferenceManager pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentUser = pm.getString("userId");

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = sdf.format(c.getTime());

        title_start_date = (HelveticaBold) findViewById(R.id.title_start_date);
        title_end_date = (HelveticaBold) findViewById(R.id.title_end_date);


        spinner_wbs = (Spinner) findViewById(R.id.spinner_wbs);

        text_start_date = (TextView) findViewById(R.id.text_start_date);
        text_end_date = (TextView) findViewById(R.id.text_end_date);

        contract_ref = (EditText) findViewById(R.id.contract_ref);

        text_budget_amount = (EditText) findViewById(R.id.text_budget_amount);
        text_currency = (TextView) findViewById(R.id.text_currency);

        text_start_date.setVisibility(View.GONE);
        text_end_date.setVisibility(View.GONE);
        title_end_date.setVisibility(View.GONE);
        title_start_date.setVisibility(View.GONE);

        pDialog = new ProgressDialog(AllBudgetApprovalCreate.this);
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
                    text_start_date.setVisibility(View.GONE);
                    text_end_date.setVisibility(View.GONE);
                    title_end_date.setVisibility(View.GONE);
                    title_start_date.setVisibility(View.GONE);
                }

                else
                {
                    text_start_date.setVisibility(View.VISIBLE);
                    text_end_date.setVisibility(View.VISIBLE);
                    title_end_date.setVisibility(View.VISIBLE);
                    title_start_date.setVisibility(View.VISIBLE);

                    text_budget_amount.setText(totalBudgetArray[position]);
                    text_currency.setText(currencyCodeArray[position]);

//                    pDialog = new ProgressDialog(AllBudgetApprovalCreate.this);
//                    pDialog.setMessage("Getting Activities in WBS - "+ wbsIdArray[position]);
//                    pDialog.setIndeterminate(false);
//                    pDialog.setCancelable(true);
//                    pDialog.show();
//
//                    final String currentSelectedWbs = wbsIdArray[position];
//
//                    class MyTask extends AsyncTask<Void, Void, Void>
//                    {
//                        @Override
//                        protected Void doInBackground(Void... params)
//                        {
//                            prepareActivities(currentSelectedWbs, pDialog);
//                            return null;
//                        }
//                    }
//
//                    new MyTask().execute();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        createBtn = (Button) findViewById(R.id.createBtn);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spinner_wbs.getSelectedItem().toString().equals("Select WBS"))
                {
                    Toast.makeText(AllBudgetApprovalCreate.this, "Select WBS", Toast.LENGTH_SHORT).show();
                }
                else if(spinner_wbs.getSelectedItem().toString().equals("Select Activity"))
                {
                    Toast.makeText(AllBudgetApprovalCreate.this, "Select Activity", Toast.LENGTH_SHORT).show();
                }
                else if(contract_ref.getText().toString().isEmpty())
                {
                    contract_ref.setError("Field cannot be empty");
                }
                else if(text_budget_amount.getText().toString().isEmpty())
                {
                    text_budget_amount.setError("Field cannot be empty");
                }
                else if(text_currency.getText().toString().isEmpty())
                {
                    text_currency.setError("Field cannot be empty");
                }
                else
                {
                    pDialog = new ProgressDialog(AllBudgetApprovalCreate.this);
                    pDialog.setMessage("Sending Data ...");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(true);
                    pDialog.show();

                    class MyTask extends AsyncTask<Void, Void, Void> {

                        @Override
                        protected Void doInBackground(Void... params) {

                            saveBudgetApproval();
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

        String url = getResources().getString(R.string.server_url) + "/getWbs?projectId=\""+currentProjectNo+"\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{
                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(AllBudgetApprovalCreate.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                wbsNameArray = new String[dataArray.length()+1];
                                wbsIdArray = new String[dataArray.length()+1];
                                totalBudgetArray = new String[dataArray.length()+1];
                                currencyCodeArray = new String[dataArray.length()+1];
                                wbsNameArray[0]= "Select WBS";
                                wbsIdArray[0]= "";
                                totalBudgetArray[0]= "";
                                currencyCodeArray[0]= "";

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    wbsName = dataObject.getString("wbsName");
                                    wbsId = dataObject.getString("wbsId");
                                    totalBudget = dataObject.getString("totalBudget");
                                    currencyCode = dataObject.getString("currencyCode");

                                    wbsNameArray[i+1]=wbsName;
                                    wbsIdArray[i+1]=wbsId;
                                    totalBudgetArray[i+1]=totalBudget;
                                    currencyCodeArray[i+1]=currencyCode;
                                }
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(AllBudgetApprovalCreate.this,
                                    android.R.layout.simple_dropdown_item_1line,wbsNameArray);
                            spinner_wbs.setAdapter(adapter);
                            pDialog.dismiss();

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

        if(pDialog!=null)
            pDialog.dismiss();

    }

    public void saveBudgetApproval()
    {
        JSONObject object = new JSONObject();

        try {
            object.put("projectId",currentProjectNo);
            object.put("contractRefNo", contract_ref.getText().toString());
            object.put("amount", text_budget_amount.getText().toString());
            object.put("description","");
            object.put("currencyCode", text_currency.getText().toString());
            object.put("createdBy", currentUser);
            object.put("dateCreated", currentDate);

            Log.d("REQUEST SENT OF JSON :" , object.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(AllBudgetApprovalCreate.this);

        String url = AllBudgetApprovalCreate.this.getResources().getString(R.string.server_url) + "/postBudgetApproval";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if(response.getString("msg").equals("success"))
                            {
                                Toast.makeText(AllBudgetApprovalCreate.this, "Budget Approval Created. ID - "+ response.getString("data"), Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                                Intent intent = new Intent(AllBudgetApprovalCreate.this, AllBudgetApproval.class);
                                startActivity(intent);
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
    }
}
