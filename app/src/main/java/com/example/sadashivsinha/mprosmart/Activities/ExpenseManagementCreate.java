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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class ExpenseManagementCreate extends AppCompatActivity {

    EditText text_quantity, text_amount, text_expense_type;
    Spinner spinner_item, spinner_wbs, spinner_activity;
    TextView item_desc, text_uom;
    Button createBtn;
    ProgressDialog pDialog;
    String currentProjectNo, item, itemDesc, uomId, currentCurrency, currentExpenseNo, currentUser;
    JSONArray dataArray;
    JSONObject dataObject;
    String[] itemArray, itemDescArray, uomIdArray;

    String[] wbsNameArray, activitiesNameArray, activityIdArray, wbsIdArray;
    String wbsName, activityName, activityId, wbsId, currentExpenseType;
    TextView text_currency;
    PreferenceManager pm;
    String[] uomArray, uomNameArray;
    String currentUomId;
    int totalAmount=0;
    RelativeLayout layout_personal;
    LinearLayout layout_project;

    Button attachBtn;
    ConnectionDetector cd;
    Boolean isInternetPresent=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_management_create);

        pm = new PreferenceManager(getApplicationContext());
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        currentProjectNo = pm.getString("projectId");
        currentCurrency = pm.getString("currency");
        currentExpenseNo = pm.getString("currentExpense");
        currentUser = pm.getString("userId");

        attachBtn = (Button) findViewById(R.id.attachBtn);
        attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExpenseManagementCreate.this, AttachmentActivity.class);
                intent.putExtra("class", "ExpenseManagementItem");
                startActivity(intent);
            }
        });

        item_desc = (TextView) findViewById(R.id.item_desc);
        text_uom = (TextView) findViewById(R.id.text_uom);
        text_quantity = (EditText) findViewById(R.id.text_quantity);
        text_amount = (EditText) findViewById(R.id.text_amount);
        text_currency = (TextView) findViewById(R.id.text_currency);

        text_expense_type = (EditText) findViewById(R.id.text_expense_type);

        text_currency.setText(currentCurrency);

        spinner_item = (Spinner) findViewById(R.id.spinner_item);
        spinner_wbs = (Spinner) findViewById(R.id.spinner_wbs);
        spinner_activity = (Spinner) findViewById(R.id.spinner_activity);

        layout_personal = (RelativeLayout) findViewById(R.id.layout_personal);
        layout_project = (LinearLayout) findViewById(R.id.layout_project);

        if(pm.getString("expenseType").equals("Personal"))
        {
            currentExpenseType = "Personal";
            layout_project.setVisibility(View.GONE);
        }
        else
        {
            currentExpenseType = "Project";
            layout_personal.setVisibility(View.GONE);
        }

        createBtn = (Button) findViewById(R.id.createBtn);

        pDialog = new ProgressDialog(ExpenseManagementCreate.this);
        pDialog.setMessage("Preparing Items...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        class MyTask extends AsyncTask<Void, Void, Void>
        {

            @Override
            protected Void doInBackground(Void... params)
            {
                getAllUom();
                return null;
            }
        }

        new MyTask().execute();

        spinner_item.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                item_desc.setText(itemDescArray[position]);
                for(int i=0; i<uomArray.length;i++)
                {
                    if(uomIdArray[position].equals(uomArray[i]))
                    {
                        currentUomId = uomIdArray[position];
                        text_uom.setText(uomNameArray[i]);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_activity.setVisibility(View.GONE);

        spinner_activity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                activityId = activityIdArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
                    spinner_activity.setVisibility(View.VISIBLE);
                    wbsId = wbsIdArray[position];

                    pDialog = new ProgressDialog(ExpenseManagementCreate.this);
                    pDialog.setMessage("Getting Activities in WBS - "+ wbsIdArray[position]);
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(true);
                    pDialog.show();

                    final String currentSelectedWbs = wbsIdArray[position];

                    prepareActivities(currentSelectedWbs, pDialog);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pm.getString("expenseType").equals("Personal")) {
                    if (text_expense_type.getText().toString().isEmpty()) {
                        text_expense_type.setError("Fields cannot be empty.");
                    } else if (text_amount.getText().toString().isEmpty()) {
                        text_amount.setError("Fields cannot be empty.");
                    } else {
                        pDialog = new ProgressDialog(ExpenseManagementCreate.this);
                        pDialog.setMessage("Sending Data ...");
                        pDialog.setIndeterminate(false);
                        pDialog.setCancelable(true);
                        pDialog.show();

                        class MyTask extends AsyncTask<Void, Void, Void> {

                            @Override
                            protected Void doInBackground(Void... params) {

                                saveExpenseManagement();
                                return null;
                            }
                        }

                        new MyTask().execute();
                    }
                }
                else
                {
                    if(spinner_item.getSelectedItem().toString().equals("Select Item"))
                    {
                        Toast.makeText(ExpenseManagementCreate.this, "Select Item", Toast.LENGTH_SHORT).show();
                    }
                    else if(spinner_wbs.getVisibility()==View.VISIBLE && spinner_wbs.getSelectedItem().toString().equals("Select WBS")
                            && currentExpenseType.equals("Project"))
                    {
                        Toast.makeText(ExpenseManagementCreate.this, "Select WBS", Toast.LENGTH_SHORT).show();
                    }
                    else if(spinner_wbs.getVisibility()==View.VISIBLE && spinner_activity.getSelectedItem().toString().equals("Select Activity")
                            && currentExpenseType.equals("Project"))
                    {
                        Toast.makeText(ExpenseManagementCreate.this, "Select Activity", Toast.LENGTH_SHORT).show();
                    }
                    else if(text_quantity.getText().toString().isEmpty())
                    {
                        text_quantity.setError("Fields cannot be empty.");
                    }
                    else if(text_amount.getText().toString().isEmpty())
                    {
                        text_amount.setError("Fields cannot be empty.");
                    }

                    else
                    {
                        pDialog = new ProgressDialog(ExpenseManagementCreate.this);
                        pDialog.setMessage("Sending Data ...");
                        pDialog.setIndeterminate(false);
                        pDialog.setCancelable(true);
                        pDialog.show();

                        class MyTask extends AsyncTask<Void, Void, Void> {

                            @Override
                            protected Void doInBackground(Void... params) {

                                saveExpenseManagement();
                                return null;
                            }
                        }

                        new MyTask().execute();
                }
                }
            }
        });
    }
    public void prepareLineItems()
    {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = pm.getString("SERVER_URL") + "/getItems?projectId='"+currentProjectNo+"'";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(ExpenseManagementCreate.this, response.getString("msg"), Toast.LENGTH_SHORT).show();

                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                itemArray = new String[dataArray.length()+1];
                                itemDescArray = new String[dataArray.length()+1];
                                uomIdArray = new String[dataArray.length()+1];

                                itemArray[0]="Select Item";
                                itemDescArray[0]="Select Item to view description";
                                uomIdArray[0]="Select Item to view UOM";

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    item = dataObject.getString("itemId");
                                    itemDesc = dataObject.getString("itemDescription");
                                    uomId = dataObject.getString("uomId");

                                    itemArray[i+1]=item;
                                    itemDescArray[i+1]=itemDesc;
                                    uomIdArray[i+1]=uomId;
                                }


                                getAllWbs();
                            }

                            if(itemArray==null)
                            {
                                Toast.makeText(ExpenseManagementCreate.this, "No Items to display", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ExpenseManagementCreate.this, ExpenseManagement.class);
                                startActivity(intent);
                            }
                            else {
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ExpenseManagementCreate.this,
                                        android.R.layout.simple_dropdown_item_1line, itemArray);
                                spinner_item.setAdapter(adapter);
                            }

                        }catch(JSONException e){
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
                                Toast.makeText(ExpenseManagementCreate.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                wbsNameArray = new String[dataArray.length()+1];
                                wbsIdArray = new String[dataArray.length()+1];
                                wbsNameArray[0]= "Select WBS";
                                wbsIdArray[0]= "";

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    wbsName = dataObject.getString("wbsName");
                                    wbsId = dataObject.getString("wbsId");

                                    wbsNameArray[i+1]=wbsName;
                                    wbsIdArray[i+1]=wbsId;
                                }
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ExpenseManagementCreate.this,
                                    android.R.layout.simple_dropdown_item_1line,wbsNameArray);
                            spinner_wbs.setAdapter(adapter);

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
        if(pDialog!=null)
            pDialog.dismiss();

        requestQueue.add(jor);

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
                                Toast.makeText(ExpenseManagementCreate.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {

                                JSONArray dataArray = response.getJSONArray("data");
                                JSONObject dataObject;
                                activityIdArray = new String[dataArray.length()+1];
                                activitiesNameArray = new String[dataArray.length()+1];
                                activityIdArray[0]= "Select Activity";
                                activitiesNameArray[0]= "Select Activity";

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);

                                    activityId = dataObject.getString("id");
                                    activityName = dataObject.getString("activityName");

                                    activityIdArray[i+1] = activityId;
                                    activitiesNameArray[i+1] = activityName;
                                }

                                ArrayAdapter<String> adapter;

                                if(dataArray.length()==0)
                                {
                                    adapter = new ArrayAdapter<String>(ExpenseManagementCreate.this,
                                            android.R.layout.simple_dropdown_item_1line, new String[] {"No Activity Found"});
                                }
                                else
                                {
                                    adapter = new ArrayAdapter<String>(ExpenseManagementCreate.this,
                                            android.R.layout.simple_dropdown_item_1line, activitiesNameArray);
                                }

                                spinner_activity.setAdapter(adapter);

                            }
                            pDialog.dismiss();
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

    public void getAllUom()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = pm.getString("SERVER_URL") + "/getUom";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{
                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(ExpenseManagementCreate.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                uomArray = new String[dataArray.length()+1];
                                uomNameArray = new String[dataArray.length()+1];

                                uomArray[0]="UOM";
                                uomNameArray[0]="UOM";

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    uomArray[i+1] = dataObject.getString("uomCode");
                                    uomNameArray[i+1] = dataObject.getString("uomName");
                                }

                                prepareLineItems();
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

    public void saveExpenseManagement()
    {

        JSONObject object = new JSONObject();

        try {

            if(pm.getString("expenseType").equals("Personal"))
            {
                currentExpenseType = "Personal";
                layout_project.setVisibility(View.GONE);

                totalAmount = Integer.parseInt(text_amount.getText().toString());

                object.put("expenseManagementId",currentExpenseNo);
                object.put("itemDescription",text_expense_type.getText().toString());
                object.put("amount", String.valueOf(totalAmount));
                object.put("createdBy", currentUser);
                object.put("currencyCode", currentCurrency);

                //false values (not to be shown)
                object.put("itemId", itemArray[1]);
                object.put("quantity", "0");
                object.put("uomId", uomIdArray[1]);

                if(pm.getString("className").equals("ExpenseManagementItem"))
                {
                    object.put("totalAttachments", pm.getString("totalImageUrlSize"));
                }
            }
            else
            {
                currentExpenseType = "Project";
                layout_personal.setVisibility(View.GONE);

                object.put("expenseManagementId",currentExpenseNo);
                object.put("currencyCode", currentCurrency);
                object.put("itemId", spinner_item.getSelectedItem().toString());
                object.put("itemDescription",item_desc.getText().toString());
                object.put("quantity", text_quantity.getText().toString());
                object.put("uomId", currentUomId);
                object.put("createdBy", currentUser);

                totalAmount = Integer.parseInt(text_amount.getText().toString()) * Integer.parseInt(text_quantity.getText().toString());

                object.put("amount", String.valueOf(totalAmount));

                if(!pm.getString("expenseType").equals("Personal"))
                {
                    object.put("wbsId", wbsId);
                    object.put("wbsActivityId", activityId);
                }

                Log.d("REQUEST SENT OF JSON :" , object.toString());

                if(pm.getString("className").equals("ExpenseManagementItem"))
                {
                    object.put("totalAttachments", pm.getString("totalImageUrlSize"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(ExpenseManagementCreate.this);

        String url = ExpenseManagementCreate.this.pm.getString("SERVER_URL") + "/postExpenseManagementLine";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            Log.d("SERVER RESPONSE :" , response.toString());

                            if(response.getString("msg").equals("success"))
                            {
                                String totalImageUrls = pm.getString("totalImageUrls");
                                if(pm.getString("className").equals("ExpenseManagementItem") && isInternetPresent
                                        && !totalImageUrls.isEmpty())
                                {
                                    uploadImage(response.getString("data"), totalImageUrls);
                                }
                                else
                                {
                                    Toast.makeText(ExpenseManagementCreate.this, "Expense Created. ID - "+ response.getString("data"), Toast.LENGTH_SHORT).show();
                                    updateTotalExpense(totalAmount);
                                }
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
                        Log.e("Volley", "Error");
                    }
                }
        );
        requestQueue.add(jor);
    }

    public void uploadImage(final String id, String totalUrls) {

        final List<String> imageList = Arrays.asList(totalUrls.split(","));
        String seperateImageUrl;


        for (int i = 0; i < imageList.size(); i++) {

            final int count = i;
            JSONObject object = new JSONObject();

            try {
                seperateImageUrl = imageList.get(i);

                object.put("expenseManagementLineId", id);
                object.put("url", seperateImageUrl);

                Log.d("JSON OBJ SENT", object.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }


            RequestQueue requestQueue = Volley.newRequestQueue(ExpenseManagementCreate.this);

            String url = ExpenseManagementCreate.this.pm.getString("SERVER_URL") + "/postExpenseManagementLineFiles";

            JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                Log.d("RESPONSE SERVER : ", response.toString());

                                if (response.getString("msg").equals("success")) {

                                    if (count == imageList.size() - 1) {
                                        Toast.makeText(ExpenseManagementCreate.this, "Expense Line created ID - " + id, Toast.LENGTH_SHORT).show();

                                        if (pDialog != null)
                                            pDialog.dismiss();
                                        Intent intent = new Intent(ExpenseManagementCreate.this, SubmittalRegisterActivity.class);
                                        startActivity(intent);
                                    }
                                    updateTotalExpense(totalAmount);

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
                            Log.e("Volley", "Error");
                            Toast.makeText(ExpenseManagementCreate.this, error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
            );

            requestQueue.add(jor);
        }
    }

    public void updateTotalExpense(int totalAmount)
    {
        int currentExpense = Integer.parseInt(pm.getString("expenseTotalExpense"));
        Log.d("TOTAL CURR EXPENSE", String.valueOf(currentExpense));

        totalAmount = totalAmount + currentExpense;

        final int totalNewAmount = totalAmount;

        Log.d("TOTAL NEW EXPENSE", String.valueOf(totalAmount));

        JSONObject object = new JSONObject();

        try {
            object.put("totalExpense",String.valueOf(totalAmount));

            Log.d("REQUEST SENT OF JSON :" , object.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(ExpenseManagementCreate.this);

        String currentExpenseId = pm.getString("currentExpense");

        String url = ExpenseManagementCreate.this.pm.getString("SERVER_URL") + "/putExpenseManagwmentTotal?expenseManagementId=\"" +
                currentExpenseId +"\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.PUT, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            Log.d("SERVER RESPONSE :" , response.toString());

                            if(response.getString("msg").equals("success"))
                            {
                                Toast.makeText(ExpenseManagementCreate.this, "Total Expense Updated"+ response.getString("data"), Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();

                                pm.putString("expenseTotalExpense", String.valueOf(totalNewAmount));

                                Intent intent = new Intent(ExpenseManagementCreate.this, ExpenseManagement.class);
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
