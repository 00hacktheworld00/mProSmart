package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.example.sadashivsinha.mprosmart.Adapters.AllExpensesAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.AllExpensesList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.github.clans.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AllExpenses extends AppCompatActivity implements View.OnClickListener {

    private List<AllExpensesList> expenseList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AllExpensesAdapter allExpensesAdapter;
    String currentProjectNo, currentProjectName, currentUser, currentDate;
    View dialogView;
    AlertDialog show;
    JSONArray dataArray;
    JSONObject dataObject;
    ProgressDialog pDialog;
    String expenseManagementId, expenseType, totalExpense, expenseDescription, createdBy, createdDate;

    AllExpensesList qualityItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_expenses);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final PreferenceManager pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentProjectName = pm.getString("projectName");
        currentUser = pm.getString("userId");


        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = sdf.format(c.getTime());

        pDialog = new ProgressDialog(AllExpenses.this);
        pDialog.setMessage("Getting Data ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        class MyTask extends AsyncTask<Void, Void, Void> {


            @Override protected void onPreExecute()
            {
                allExpensesAdapter = new AllExpensesAdapter(expenseList);
                recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                recyclerView.setLayoutManager(new LinearLayoutManager(AllExpenses.this));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(allExpensesAdapter);

            }
            @Override
            protected Void doInBackground(Void... params) {
                prepareItems();
                return null;
            }

            @Override protected void onPostExecute(Void result)
            {
                allExpensesAdapter.notifyDataSetChanged();
                if(pDialog!=null)
                    pDialog.dismiss();
            }

        }
        new MyTask().execute();

        FloatingActionButton fab_add;

        fab_add = (FloatingActionButton) findViewById(R.id.fab_add);

        fab_add.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add:
            {
                final AlertDialog.Builder alert = new AlertDialog.Builder(AllExpenses.this,android.R.style.Theme_Translucent_NoTitleBar);
                // Set an EditText view to get user input

                dialogView = LayoutInflater.from(AllExpenses.this).inflate(R.layout.dialog_new_expense, null);
                alert.setView(dialogView);

                show = alert.show();

                final Spinner spinner_expense = (Spinner) dialogView.findViewById(R.id.spinner_expense);

                final EditText expense_desc;
                expense_desc = (EditText) dialogView.findViewById(R.id.expense_desc);

                Button createBtn = (Button) dialogView.findViewById(R.id.createBtn);


                ArrayAdapter<String> adapter = new ArrayAdapter<String>(AllExpenses.this,
                        android.R.layout.simple_dropdown_item_1line, new String[] {"Select Expense Type" ,"Personal", "Project"});
                spinner_expense.setAdapter(adapter);

                createBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(spinner_expense.getSelectedItem().toString().equals("Select Expense Type"))
                        {
                            Toast.makeText(AllExpenses.this, "Select Expense Type First", Toast.LENGTH_SHORT).show();
                        }
                        else if(expense_desc.getText().toString().isEmpty())
                        {
                            expense_desc.setError("Field cannot be empty.");
                        }

                        else
                        {
                            pDialog = new ProgressDialog(AllExpenses.this);
                            pDialog.setMessage("Sending Data ...");
                            pDialog.setIndeterminate(false);
                            pDialog.setCancelable(true);
                            pDialog.show();

                            final String expenseType = spinner_expense.getSelectedItem().toString();
                            final String expenseDesc = expense_desc.getText().toString();

                            class MyTask extends AsyncTask<Void, Void, Void> {

                                @Override
                                protected Void doInBackground(Void... params) {

                                    saveExpenses(expenseType, expenseDesc);
                                    return null;
                                }
                            }

                            new MyTask().execute();

                        }
                    }
                });
            }
            break;
        }
    }



    public void prepareItems()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = getResources().getString(R.string.server_url) + "/getExpenseManagement?projectId='"+currentProjectNo+"'";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(AllExpenses.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    expenseManagementId = dataObject.getString("expenseManagementId");
                                    expenseType = dataObject.getString("expenseType");
                                    totalExpense = dataObject.getString("totalExpense");
                                    expenseDescription = dataObject.getString("expenseDescription");
                                    createdBy = dataObject.getString("createdBy");
                                    createdDate = dataObject.getString("createdDate");

                                    Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(createdDate);
                                    createdDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                    if(expenseType.equals("1"))
                                        expenseType = "Personal";
                                    else
                                        expenseType = "Project";

                                    qualityItem = new AllExpensesList(String.valueOf(i+1), expenseManagementId, createdDate, createdBy, expenseType, totalExpense,
                                            expenseDescription);
                                    expenseList.add(qualityItem);

                                    allExpensesAdapter.notifyDataSetChanged();

                                }
                            }
                            pDialog.dismiss();
                        }catch(JSONException e){e.printStackTrace();} catch (ParseException e) {
                            e.printStackTrace();
                        }
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
    public void saveExpenses(String expenseType, String expenseDesc)
    {
        JSONObject object = new JSONObject();

        try {
            object.put("projectId",currentProjectNo);
            object.put("totalExpense", "0");
            object.put("expenseDescription",expenseDesc);
            object.put("createdBy", currentUser);
            object.put("createdDate", currentDate);

            if(expenseType.equals("Personal"))
                object.put("expenseType", "1");
            else
                object.put("expenseType", "2");


            Log.d("REQUEST SENT OF JSON :" , object.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(AllExpenses.this);

        String url = AllExpenses.this.getResources().getString(R.string.server_url) + "/postExpenseManagement";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if(response.getString("msg").equals("success"))
                            {
                                Toast.makeText(AllExpenses.this, "Expense Created. ID - "+ response.getString("data"), Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                                Intent intent = new Intent(AllExpenses.this, AllExpenses.class);
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AllExpenses.this, ViewPurchaseOrders.class);
        startActivity(intent);
    }

}