package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.RadioButton;
import android.widget.RelativeLayout;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class BudgetChangeAllCreate extends AppCompatActivity {


    JSONArray dataArray;
    JSONObject dataObject;
    ProgressDialog pDialog;
    String currentProjectNo, currentUser, currentDate;
    Spinner spinner_wbs;
    String[] wbsNameArray, wbsIdArray, currencyCodeArray, totalBudgetArray;
    String wbsName, wbsId, currencyCode, totalBudget, currentWbsId, currentCompanyId;
    EditText current_request, total_budget, original_amount, contract_ref, text_currency;
    RadioButton radio_btn_add, radio_btn_deduct;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    String url;
    PreferenceManager pm;
    public static final String TAG = BudgetChangeAllCreate.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_change_all_create);

        pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentUser = pm.getString("userId");
        currentCompanyId = pm.getString("companyId");

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = sdf.format(c.getTime());

        Button createBtn = (Button) findViewById(R.id.createBtn);

        radio_btn_add = (RadioButton) findViewById(R.id.radio_btn_add);
        radio_btn_deduct = (RadioButton) findViewById(R.id.radio_btn_deduct);

        current_request = (EditText) findViewById(R.id.current_request);
        total_budget = (EditText) findViewById(R.id.total_budget);
        original_amount = (EditText) findViewById(R.id.original_amount);
        contract_ref = (EditText) findViewById(R.id.contract_ref);
        text_currency = (EditText) findViewById(R.id.text_currency);

        spinner_wbs = (Spinner) findViewById(R.id.spinner_wbs);

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        url = pm.getString("SERVER_URL") + "/getWbs?projectId=\""+currentProjectNo+"\"";

        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(BudgetChangeAllCreate.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(BudgetChangeAllCreate.this);
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
                        wbsNameArray = new String[dataArray.length() + 1];
                        wbsIdArray = new String[dataArray.length() + 1];
                        totalBudgetArray = new String[dataArray.length() + 1];
                        currencyCodeArray = new String[dataArray.length() + 1];
                        wbsNameArray[0] = "Select WBS";
                        wbsIdArray[0] = "";
                        totalBudgetArray[0] = "";
                        currencyCodeArray[0] = "";

                        for (int i = 0; i < dataArray.length(); i++) {
                            dataObject = dataArray.getJSONObject(i);
                            wbsName = dataObject.getString("wbsName");
                            wbsId = dataObject.getString("wbsId");
                            totalBudget = dataObject.getString("totalBudget");
                            currencyCode = dataObject.getString("currencyCode");

                            wbsNameArray[i + 1] = wbsName;
                            wbsIdArray[i + 1] = wbsId;
                            totalBudgetArray[i + 1] = totalBudget;
                            currencyCodeArray[i + 1] = currencyCode;
                        }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(BudgetChangeAllCreate.this,
                            android.R.layout.simple_dropdown_item_1line,wbsNameArray);
                    spinner_wbs.setAdapter(adapter);

                        pDialog.dismiss();

                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    Toast.makeText(getApplicationContext(), "Loading from cache.", Toast.LENGTH_SHORT).show();
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }
                if (pDialog != null)
                    pDialog.dismiss();
            }

            else
            {
                Toast.makeText(BudgetChangeAllCreate.this, "Offline Data Not available for WBS", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }

        else
        {
            // Cache data not exist.
            getAllWbs();
        }

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

                    if(radio_btn_deduct.isChecked())
                    {
                        if(originalVal<currentReq)
                        {
                            total_budget.setError("Cannot deduct amount greater than original amount from WBS");
                        }
                        else
                        {
                            totalVal = originalVal - currentReq ;
                            total_budget.setText(String.valueOf(totalVal));
                        }
                    }
                    else
                    {
                        totalVal = originalVal + currentReq ;
                        total_budget.setText(String.valueOf(totalVal));
                    }
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
                else if(contract_ref.getText().toString().isEmpty())
                {
                    contract_ref.setError("Field cannot be empty.");
                }
                else if(spinner_wbs.getSelectedItem().toString().equals("Select WBS"))
                {
                    Toast.makeText(BudgetChangeAllCreate.this, "Select WBS", Toast.LENGTH_SHORT).show();
                }
                else if(radio_btn_deduct.isChecked() &&
                        (Float.parseFloat(original_amount.getText().toString())< Float.parseFloat(current_request.getText().toString())))
                {
                    current_request.setError("Cannot deduct amount greater than original amount from WBS");
                }
                else
                {
                    saveBudgetChanges();
                }
            }
        });
    }

    public void getAllWbs()
    {
        pDialog = new ProgressDialog(BudgetChangeAllCreate.this);
        pDialog.setMessage("Getting server data");
        pDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {
//                            dataObject = response.getJSONObject(0);
                            Log.d("RESPONSE JSON", response.toString());
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

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(BudgetChangeAllCreate.this,
                                android.R.layout.simple_dropdown_item_1line,wbsNameArray);
                        spinner_wbs.setAdapter(adapter);
                            pDialog.dismiss();
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

            if(radio_btn_deduct.isChecked())
            {
                object.put("description", "BUDGET DEDUCTED");
            }
            else
            {
                object.put("description", "BUDGET ADDED");
            }

            Log.d("REQUEST SENT OF JSON :" , object.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(BudgetChangeAllCreate.this);

        String url = BudgetChangeAllCreate.this.pm.getString("SERVER_URL") + "/postBudgetChanges";

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
        if (!isInternetPresent)
        {
            // Internet connection is not present

            Communicator communicator = new Communicator();
            Log.d("object", object.toString());

            Boolean createBudgetChangePending = pm.getBoolean("createBudgetChangePending");

            if(createBudgetChangePending)
            {
                Toast.makeText(BudgetChangeAllCreate.this, "Already a Budget Change creation is in progress. Please try after sometime.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(BudgetChangeAllCreate.this, "Internet not currently available. Budget Change will automatically get created on internet connection.", Toast.LENGTH_SHORT).show();

                pm.putString("objectBudgetChangeLine", object.toString());
                pm.putString("urlBudgetChangeLine", url);
                pm.putString("toastMessageBudgetChange", "Budget Change Created");
                pm.putBoolean("createBudgetChangePending", true);
            }


            if(pDialog!=null)
                pDialog.dismiss();

            updateWbsBudget();
        }
        else
        {
            requestQueue.add(jor);
        }
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

        String url = BudgetChangeAllCreate.this.pm.getString("SERVER_URL") + "/putWbsTotalBudget?wbsId=\"" + currentWbsId + "\"";

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

        if (!isInternetPresent)
        {
            // Internet connection is not present

            Communicator communicator = new Communicator();
            Log.d("object", object.toString());

            Boolean UpdateWbsBudgetPending = pm.getBoolean("UpdateWbsBudgetPending");

            if(UpdateWbsBudgetPending)
            {
                Toast.makeText(BudgetChangeAllCreate.this, "Already a Budget Change creation is in progress. Please try after sometime.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(BudgetChangeAllCreate.this, "Internet not currently available. Budget Change will automatically get created on internet connection.", Toast.LENGTH_SHORT).show();

                pm.putString("objectUpdateWbsBudget", object.toString());
                pm.putString("urlUpdateWbsBudget", url);
                pm.putString("toastMessageUpdateWbsBudget", "");
                pm.putBoolean("UpdateWbsBudgetPending", true);
            }


            if(pDialog!=null)
                pDialog.dismiss();

            getCurrentProjectBudget();
        }
        else
        {
            requestQueue.add(jor);
        }
    }

    public void getCurrentProjectBudget() {
        String url = pm.getString("SERVER_URL") + "/getProjects?companyId=\"" + currentCompanyId + "\"";

        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(BudgetChangeAllCreate.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(BudgetChangeAllCreate.this);
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

                    String budget = null, projectId;

                    try {
                        dataArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < dataArray.length(); i++) {
                            dataObject = dataArray.getJSONObject(i);
                            projectId = dataObject.getString("projectId");

                            if (projectId.equals(currentProjectNo)) {
                                budget = dataObject.getString("totalBudget");
                            }
                        }
                        updateProjectBudget(Integer.parseInt(budget));

                        pDialog.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    Toast.makeText(getApplicationContext(), "Loading from cache.", Toast.LENGTH_SHORT).show();
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }
                if (pDialog != null)
                    pDialog.dismiss();
            } else {
                Toast.makeText(BudgetChangeAllCreate.this, "Offline Data Not available for WBS", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }
        else {
            // Cache data not exist.
            RequestQueue requestQueue = Volley.newRequestQueue(this);

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

    }


    public void updateProjectBudget(int currentProjectBudget)
    {
        JSONObject object = new JSONObject();

        if(radio_btn_add.isChecked())
            currentProjectBudget = currentProjectBudget + Integer.parseInt(current_request.getText().toString());
        else
            currentProjectBudget = currentProjectBudget - Integer.parseInt(current_request.getText().toString());

        final String currentBudget = String.valueOf(currentProjectBudget);
        try {
            object.put("totalBudget",currentBudget);

            Log.d("REQUEST SENT OF JSON :" , object.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(BudgetChangeAllCreate.this);

        String url = BudgetChangeAllCreate.this.pm.getString("SERVER_URL") + "/putProjectTotalBudget?projectId=\"" + currentProjectNo + "\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.PUT, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if(response.getString("msg").equals("success"))
                            {
                                pDialog.dismiss();
                                pm.putString("budget", currentBudget);
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

        if (!isInternetPresent)
        {
            // Internet connection is not present

            Communicator communicator = new Communicator();
            Log.d("object", object.toString());

            Boolean UpdateProjectBudgetPending = pm.getBoolean("UpdateProjectBudgetPending");

            if(!UpdateProjectBudgetPending)
            {
                pm.putString("objectUpdateProjectBudget", object.toString());
                pm.putString("urlUpdateProjectBudget", url);
                pm.putString("toastMessageUpdateProjectBudget", "");
                pm.putBoolean("UpdateProjectBudgetPending", true);
            }

            if(pDialog!=null)
                pDialog.dismiss();
            Intent intent = new Intent(BudgetChangeAllCreate.this, AllBudgetChanges.class);
            startActivity(intent);

        }
        else
        {
            requestQueue.add(jor);
        }
    }
}
