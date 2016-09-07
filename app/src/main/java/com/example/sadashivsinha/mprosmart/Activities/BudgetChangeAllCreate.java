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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BudgetChangeAllCreate extends AppCompatActivity {


    JSONArray dataArray;
    JSONObject dataObject;
    ProgressDialog pDialog;
    String currentProjectNo, currentUser, currentDate;
    Spinner spinner_wbs;
    String[] wbsNameArray, wbsIdArray, currencyCodeArray, totalBudgetArray;
    String wbsName, wbsId, currencyCode, totalBudget, currentWbsId, currentCompanyId;
    EditText current_request, total_budget, original_amount, desc, contract_ref, text_currency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_change_all_create);

        PreferenceManager pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentUser = pm.getString("userId");
        currentCompanyId = pm.getString("companyId");

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = sdf.format(c.getTime());

        Button createBtn = (Button) findViewById(R.id.createBtn);

        current_request = (EditText) findViewById(R.id.current_request);
        total_budget = (EditText) findViewById(R.id.total_budget);
        original_amount = (EditText) findViewById(R.id.original_amount);
        desc = (EditText) findViewById(R.id.desc);
        contract_ref = (EditText) findViewById(R.id.contract_ref);
        text_currency = (EditText) findViewById(R.id.text_currency);

        spinner_wbs = (Spinner) findViewById(R.id.spinner_wbs);

        pDialog = new ProgressDialog(BudgetChangeAllCreate.this);
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
                    total_budget.setText("0");
                    text_currency.setText("");
                }

                else
                {
                    original_amount.setText(totalBudgetArray[position]);
                    total_budget.setText(totalBudgetArray[position]);
                    text_currency.setText(currencyCodeArray[position]);

                    currentWbsId = wbsIdArray[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        TextWatcher inputTextWatcher = new TextWatcher() {

            int originalVal, currentReq, totalVal;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(current_request.getText().toString().isEmpty())
                {
                    current_request.setError("Enter Value");
                }
            }

            public void afterTextChanged(Editable s) {
                if(current_request.getText().toString().isEmpty())
                {
                    current_request.setError("Enter Value");
                }
                else
                {
                    currentReq = Integer.parseInt(current_request.getText().toString());
                    originalVal = Integer.parseInt(original_amount.getText().toString());

                    totalVal = currentReq + originalVal ;
                    total_budget.setText(String.valueOf(totalVal));
                }
            }
        };

        current_request.addTextChangedListener(inputTextWatcher);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(current_request.getText().toString().isEmpty())
                {
                    current_request.setError("Field cannot be empty.");
                }
                else if(desc.getText().toString().isEmpty())
                {
                    desc.setError("Field cannot be empty.");
                }
                else if(contract_ref.getText().toString().isEmpty())
                {
                    contract_ref.setError("Field cannot be empty.");
                }
                else if(spinner_wbs.getSelectedItem().toString().equals("Select WBS"))
                {
                    Toast.makeText(BudgetChangeAllCreate.this, "Select WBS", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    pDialog = new ProgressDialog(BudgetChangeAllCreate.this);
                    pDialog.setMessage("Sending Data ...");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(true);
                    pDialog.show();

                    class MyTask extends AsyncTask<Void, Void, Void> {

                        @Override
                        protected Void doInBackground(Void... params) {

                            saveBudgetChanges();
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
                                Toast.makeText(BudgetChangeAllCreate.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
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
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(BudgetChangeAllCreate.this,
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

    public void saveBudgetChanges()
    {
        JSONObject object = new JSONObject();

        try {
            object.put("projectId",currentProjectNo);
            object.put("contractRefNo", contract_ref.getText().toString());
            object.put("createdBy", currentUser);
            object.put("originalBudget", original_amount.getText().toString());
            object.put("currentBudget",current_request.getText().toString());
            object.put("totalBudget", total_budget.getText().toString());
            object.put("dateCreated", currentDate);
            object.put("description", desc.getText().toString());

            Log.d("REQUEST SENT OF JSON :" , object.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(BudgetChangeAllCreate.this);

        String url = BudgetChangeAllCreate.this.getResources().getString(R.string.server_url) + "/postBudgetChanges";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if(response.getString("msg").equals("success"))
                            {
                                Toast.makeText(BudgetChangeAllCreate.this, "Budget Changes Created. ID - "+ response.getString("data"), Toast.LENGTH_SHORT).show();
                                updateWbsBudget();
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

    public void updateWbsBudget()
    {
        JSONObject object = new JSONObject();

        try {
            object.put("totalBudget",String.valueOf(total_budget.getText().toString()));

            Log.d("REQUEST SENT OF JSON :" , object.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(BudgetChangeAllCreate.this);

        String url = BudgetChangeAllCreate.this.getResources().getString(R.string.server_url) + "/putWbsTotalBudget?wbsId=\"" + currentWbsId + "\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.PUT, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if(response.getString("msg").equals("success"))
                            {
                                getCurrentProjectBudget();
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

    public void getCurrentProjectBudget()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = getResources().getString(R.string.server_url) + "/getProjects?companyId=\""+currentCompanyId+"\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{
                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(BudgetChangeAllCreate.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                            }

                            String budget = null, projectId;

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    projectId = dataObject.getString("projectId");

                                    if(projectId.equals(currentProjectNo))
                                    {
                                        budget = dataObject.getString("totalBudget");
                                    }
                                }
                                updateProjectBudget(Integer.parseInt(budget));
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


    public void updateProjectBudget(int currentProjectBudget)
    {
        JSONObject object = new JSONObject();

        currentProjectBudget = currentProjectBudget + Integer.parseInt(current_request.getText().toString());

        try {
            object.put("totalBudget",String.valueOf(currentProjectBudget));

            Log.d("REQUEST SENT OF JSON :" , object.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(BudgetChangeAllCreate.this);

        String url = BudgetChangeAllCreate.this.getResources().getString(R.string.server_url) + "/putProjectTotalBudget?projectId=\"" + currentProjectNo + "\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.PUT, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if(response.getString("msg").equals("success"))
                            {
                                pDialog.dismiss();
                                Intent intent = new Intent(BudgetChangeAllCreate.this, AllBudgetChanges.class);
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
