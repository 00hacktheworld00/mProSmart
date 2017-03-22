package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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

public class AllBudgetTransferCreate extends AppCompatActivity {

    Spinner spinner_wbs_from, spinner_wbs_to;
    EditText budget_allocated_from, budget_allocated_to, transfer_amount_from, new_budget_to;
    TextView currency_from, currency_to, currency_transfer_from, currency_budget_to;
    String[] wbsNameArray, wbsIdArray, currencyCodeArray, totalBudgetArray;
    String wbsName, wbsId, currencyCode, totalBudget;
    JSONArray dataArray;
    JSONObject dataObject;
    ProgressDialog pDialog;
    String currentProjectNo;
    CardView card_to;
    Button createBtn;
    String wbs_url;
    int totalBudgetFrom, totalBudgetTo, currentBudgetFrom, currentBudgetTo;
    String currentUserId, currentDate, currentWbsFromId, currentWbsToId;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    PreferenceManager pm;
    public static final String TAG = AllBudgetTransferCreate.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_budget_transfer_create);

        pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentUserId = pm.getString("userId");


        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = sdf.format(c.getTime());

        spinner_wbs_from = (Spinner) findViewById(R.id.spinner_wbs_from);
        spinner_wbs_to = (Spinner) findViewById(R.id.spinner_wbs_to);

        budget_allocated_from = (EditText) findViewById(R.id.budget_allocated_from);
        budget_allocated_to = (EditText) findViewById(R.id.budget_allocated_to);
        transfer_amount_from = (EditText) findViewById(R.id.transfer_amount_from);
        new_budget_to = (EditText) findViewById(R.id.new_budget_to);

        currency_from = (TextView) findViewById(R.id.currency_from);
        currency_to = (TextView) findViewById(R.id.currency_to);
        currency_transfer_from = (TextView) findViewById(R.id.currency_transfer_from);
        currency_budget_to = (TextView) findViewById(R.id.currency_budget_to);

        card_to = (CardView) findViewById(R.id.card_to);

        card_to.setVisibility(View.GONE);

        budget_allocated_to.setEnabled(false);
        budget_allocated_from.setEnabled(false);
        new_budget_to.setEnabled(false);

        spinner_wbs_from.requestFocus();

        wbs_url = pm.getString("SERVER_URL") + "/getWbs?projectId=\""+currentProjectNo+"\"";

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_content);
            Crouton.cancelAllCroutons();
            Crouton.makeText(AllBudgetTransferCreate.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(AllBudgetTransferCreate.this);
            pDialog.setMessage("Getting cache data");
            pDialog.show();

            Cache cache = AppController.getInstance().getRequestQueue().getCache();
            Cache.Entry entry = cache.get(wbs_url);
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

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(AllBudgetTransferCreate.this,
                            R.layout.spinner_small_text,wbsNameArray);
                        spinner_wbs_from.setAdapter(adapter);
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
                Toast.makeText(AllBudgetTransferCreate.this, "Offline Data Not available for WBS", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }
        else
        {
            // Cache data not exist.
            getAllWbs(spinner_wbs_from);
        }

        createBtn = (Button) findViewById(R.id.createBtn);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(spinner_wbs_from.getSelectedItem().toString().equals("Select WBS"))
                {
                    Toast.makeText(AllBudgetTransferCreate.this, "Select WBS From first", Toast.LENGTH_SHORT).show();
                }
                else if(spinner_wbs_to.getSelectedItem().toString().equals("Select WBS"))
                {
                    Toast.makeText(AllBudgetTransferCreate.this, "Select WBS From first", Toast.LENGTH_SHORT).show();
                }
                else if(transfer_amount_from.getText().toString().isEmpty())
                {
                    transfer_amount_from.setError("Enter amount to be transferred");
                }
                else
                {
                    saveBudgetTransfer();
                }
            }
        });

        spinner_wbs_from.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0)
                {
                    card_to.setVisibility(View.GONE);
                    budget_allocated_from.setText("0");
                    transfer_amount_from.setText("0");
                    currency_from.setText("");
                    currency_transfer_from.setText("");

                    currentBudgetFrom = 0;
                }

                else
                {
                    card_to.setVisibility(View.VISIBLE);

                    budget_allocated_from.setText(totalBudgetArray[position]);
                    currency_transfer_from.setText(currencyCodeArray[position]);
                    currency_from.setText(currencyCodeArray[position]);

                    currentWbsFromId = wbsIdArray[position];

                    if(budget_allocated_from.getText()!=null)
                    currentBudgetFrom = Integer.parseInt(budget_allocated_from.getText().toString());

                    if (!isInternetPresent) {
                        // Internet connection is not present
                        // Ask user to connect to Internet
                        RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_content);
                        Crouton.cancelAllCroutons();
                        Crouton.makeText(AllBudgetTransferCreate.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

                        pDialog = new ProgressDialog(AllBudgetTransferCreate.this);
                        pDialog.setMessage("Getting cache data");
                        pDialog.show();

                        Cache cache = AppController.getInstance().getRequestQueue().getCache();
                        Cache.Entry entry = cache.get(wbs_url);
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

                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(AllBudgetTransferCreate.this,
                                            R.layout.spinner_small_text,wbsNameArray);
                                    spinner_wbs_to.setAdapter(adapter);
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
                            Toast.makeText(AllBudgetTransferCreate.this, "Offline Data Not available for WBS", Toast.LENGTH_SHORT).show();
                            pDialog.dismiss();
                        }
                    }
                    else
                    {
                        // Cache data not exist.
                        getAllWbs(spinner_wbs_to);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinner_wbs_to.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0)
                {
                    budget_allocated_to.setText("0");
                    new_budget_to.setText("0");
                }

                else
                {
                    budget_allocated_to.setText(totalBudgetArray[position]);
                    currency_to.setText(currencyCodeArray[position]);
                    currency_budget_to.setText(currencyCodeArray[position]);

                    if(budget_allocated_to.getText()!=null)
                    currentBudgetTo = Integer.parseInt(budget_allocated_to.getText().toString());

                    if(!transfer_amount_from.getText().toString().isEmpty())
                    {
                        float transferAmountFrom = Float.parseFloat(transfer_amount_from.getText().toString());

                        if(budget_allocated_to.getText().toString().isEmpty())
                        {
                            budget_allocated_to.setText("0");
                        }

                        float budgetAllocatedTo = Float.parseFloat(budget_allocated_to.getText().toString());
                        float newAmount = transferAmountFrom + budgetAllocatedTo;

                        new_budget_to.setText(String.valueOf(newAmount));
                    }

                    currentWbsToId = wbsIdArray[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        TextWatcher inputTextWatcher = new TextWatcher() {

            float newToAmount, transferFromAmount, budgetAllocatedToAmount;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(transfer_amount_from.getText().toString().isEmpty())
                {
                    transfer_amount_from.setError("Enter Value");
                }
                else if (Float.parseFloat(transfer_amount_from.getText().toString())>Float.parseFloat(budget_allocated_from.getText().toString()))
                {
                    transfer_amount_from.setError("Transfer Amount cannot be more than Allocated Budget");
                }
            }

            public void afterTextChanged(Editable s) {
                if(transfer_amount_from.getText().toString().isEmpty())
                {
                    transfer_amount_from.setError("Enter Value");
                }
                else if (Float.parseFloat(transfer_amount_from.getText().toString())>Float.parseFloat(budget_allocated_from.getText().toString()))
                {
                    transfer_amount_from.setError("Transfer Amount cannot be more than Allocated Budget");
                }
                else
                {
                    transferFromAmount = Float.parseFloat(s.toString());
                    if(!budget_allocated_to.getText().toString().isEmpty())
                    {
                        budgetAllocatedToAmount = Float.parseFloat(budget_allocated_to.getText().toString());

                        newToAmount = transferFromAmount + budgetAllocatedToAmount ;
                        new_budget_to.setText(String.valueOf(newToAmount));
                    }
                }
            }
        };

        transfer_amount_from.addTextChangedListener(inputTextWatcher);
    }

    public void saveBudgetTransfer()
    {
        totalBudgetFrom = currentBudgetFrom - Integer.parseInt(transfer_amount_from.getText().toString());
        totalBudgetTo = currentBudgetTo + Integer.parseInt(transfer_amount_from.getText().toString());

        JSONObject object = new JSONObject();

        try {
            object.put("projectId",currentProjectNo);
            object.put("fromWbs", currentWbsFromId);
            object.put("toWbs",currentWbsToId);
            object.put("budgetTransfer", transfer_amount_from.getText().toString());
            object.put("createdBy", currentUserId);
            object.put("dateCreated", currentDate);

            Log.d("REQUEST SENT OF JSON :" , object.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(AllBudgetTransferCreate.this);

        String url = AllBudgetTransferCreate.this.pm.getString("SERVER_URL") + "/postBudgetTransfer";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if(response.getString("msg").equals("success"))
                            {
                                Toast.makeText(AllBudgetTransferCreate.this, "Budget Transfer Created. ID - "+ response.getString("data"), Toast.LENGTH_SHORT).show();
                                updateWbsBudgetFrom();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            if(pDialog!=null)
                                pDialog.dismiss();
                        }
                        //response success message display
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                        if(pDialog!=null)
                            pDialog.dismiss();
                    }
                }
        );
        if (!isInternetPresent)
        {
            // Internet connection is not present

            Communicator communicator = new Communicator();
            Log.d("object", object.toString());

            Boolean createBudgetTransferPending = pm.getBoolean("createBudgetTransferPending");

            if(createBudgetTransferPending)
            {
                Toast.makeText(AllBudgetTransferCreate.this, "Already a Budget Transfer creation is in progress. Please try after sometime.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(AllBudgetTransferCreate.this, "Internet not currently available. Budget Transfer will automatically get created on internet connection.", Toast.LENGTH_SHORT).show();

                pm.putString("objectBudgetTransfer", object.toString());
                pm.putString("urlBudgetTransfer", url);
                pm.putString("toastMessageBudgetTransfer", "Budget Transfer Created");
                pm.putBoolean("createBudgetTransferPending", true);
            }


            if(pDialog!=null)
                pDialog.dismiss();

            updateWbsBudgetFrom();
        }
        else
        {
            requestQueue.add(jor);
        }
    }

    public void updateWbsBudgetFrom()
    {
        JSONObject object = new JSONObject();

        try {
            object.put("totalBudget",String.valueOf(totalBudgetFrom));

            Log.d("REQUEST SENT OF JSON :" , object.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(AllBudgetTransferCreate.this);

        String url = AllBudgetTransferCreate.this.pm.getString("SERVER_URL") + "/putWbsTotalBudget?wbsId=\"" + currentWbsFromId + "\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.PUT, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if(response.getString("msg").equals("success"))
                            {
                                updateWbsBudgetTo();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            if(pDialog!=null)
                                pDialog.dismiss();
                        }
                        //response success message display
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                        if(pDialog!=null)
                            pDialog.dismiss();
                    }
                }
        );
        if (!isInternetPresent)
        {
            // Internet connection is not present

            Communicator communicator = new Communicator();
            Log.d("object", object.toString());

            Boolean updateWbsBudgetFromPending = pm.getBoolean("updateWbsBudgetFromPending");

            if(!updateWbsBudgetFromPending)
            {
                pm.putString("objectWbsBudgetFrom", object.toString());
                pm.putString("urlWbsBudgetFrom", url);
                pm.putString("toastMessageWbsBudgetFrom", "WBS Budget Updated");
                pm.putBoolean("updateWbsBudgetFromPending", true);
            }


            if(pDialog!=null)
                pDialog.dismiss();

            updateWbsBudgetTo();
        }
        else
        {
            requestQueue.add(jor);
        }

    }

    public void updateWbsBudgetTo()
    {
        JSONObject object = new JSONObject();

        try {
            object.put("totalBudget",String.valueOf(totalBudgetTo));

            Log.d("REQUEST SENT OF JSON :" , object.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(AllBudgetTransferCreate.this);

        String url = AllBudgetTransferCreate.this.pm.getString("SERVER_URL") + "/putWbsTotalBudget?wbsId=\"" + currentWbsToId + "\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.PUT, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if(response.getString("msg").equals("success"))
                            {
                                if(pDialog!=null)
                                    pDialog.dismiss();
                                Intent intent = new Intent(AllBudgetTransferCreate.this, AllBudgetTransfer.class);
                                startActivity(intent);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            if(pDialog!=null)
                                pDialog.dismiss();
                        }
                        //response success message display
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                        if(pDialog!=null)
                            pDialog.dismiss();
                    }
                }
        );

        if (!isInternetPresent)
        {
            // Internet connection is not present

            Communicator communicator = new Communicator();
            Log.d("object", object.toString());

            Boolean updateWbsBudgetToPending = pm.getBoolean("updateWbsBudgetToPending");

            if(!updateWbsBudgetToPending)
            {
                pm.putString("objectWbsBudgetTo", object.toString());
                pm.putString("urlWbsBudgetTo", url);
                pm.putString("toastMessageWbsBudgetTo", "WBS Budget Updated");
                pm.putBoolean("updateWbsBudgetToPending", true);
            }


            if(pDialog!=null)
                pDialog.dismiss();

            Intent intent = new Intent(AllBudgetTransferCreate.this, AllBudgetTransfer.class);
            startActivity(intent);
        }
        else
        {
            requestQueue.add(jor);
        }

    }


    public void getAllWbs(final Spinner spinner)
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, wbs_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{
                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(AllBudgetTransferCreate.this, response.getString("msg"), Toast.LENGTH_SHORT).show();

                                if(pDialog!=null)
                                    pDialog.dismiss();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                wbsNameArray = new String[dataArray.length() + 1];
                                wbsIdArray = new String[dataArray.length() + 1];
                                totalBudgetArray = new String[dataArray.length() + 1];
                                currencyCodeArray = new String[dataArray.length() + 1];

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
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(AllBudgetTransferCreate.this,
                                    R.layout.spinner_small_text,wbsNameArray);
                            spinner.setAdapter(adapter);
                            if(pDialog!=null)
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

}
