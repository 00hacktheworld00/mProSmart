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
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.Adapters.AllBudgetChangeAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.AllBudgetChangeList;
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


public class AllBudgetChanges extends AppCompatActivity implements View.OnClickListener {

    private List<AllBudgetChangeList> budgetList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AllBudgetChangeAdapter budgetAdapter;
    String currentProjectNo, currentProjectName, currentUser, currentDate;
    View dialogView;
    AlertDialog show;

    AllBudgetChangeList budgetItems;
    private ProgressDialog pDialog;
    JSONArray dataArray;
    JSONObject dataObject;
    String budgetChangesId, contractRefNo, originalBudget, currentBudget, createdBy, dateCreated, totalBudget, description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_budget_changes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final PreferenceManager pm = new PreferenceManager(getApplicationContext());
        pm.putString("currentBudget", "transfer");
        currentProjectNo = pm.getString("projectId");
        currentProjectName = pm.getString("projectName");
        currentUser = pm.getString("userId");


        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = sdf.format(c.getTime());


        pDialog = new ProgressDialog(AllBudgetChanges.this);
        pDialog.setMessage("Getting Data ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        budgetAdapter = new AllBudgetChangeAdapter(budgetList);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(budgetAdapter);

        class MyTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... params) {
                prepareItems();
                return null;
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
                Intent intent = new Intent(AllBudgetChanges.this, BudgetChangeAllCreate.class);
                startActivity(intent);
            }
            break;
        }
    }



    public void prepareItems()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = getResources().getString(R.string.server_url) + "/getBudgetChanges?projectId='"+currentProjectNo+"'";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{
                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(AllBudgetChanges.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    budgetChangesId = dataObject.getString("budgetChangesId");
                                    contractRefNo = dataObject.getString("contractRefNo");
                                    originalBudget = dataObject.getString("originalBudget");
                                    currentBudget = dataObject.getString("currentBudget");
                                    createdBy = dataObject.getString("createdBy");
                                    totalBudget = dataObject.getString("totalBudget");
                                    description = dataObject.getString("description");
                                    dateCreated = dataObject.getString("dateCreated");

                                    Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(dateCreated);
                                    dateCreated = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                    budgetItems = new AllBudgetChangeList(String.valueOf(i+1), budgetChangesId, currentProjectNo, currentProjectName, createdBy, dateCreated,
                                            originalBudget, currentBudget, totalBudget, description, contractRefNo);
                                    budgetList.add(budgetItems);

                                    budgetAdapter.notifyDataSetChanged();

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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AllBudgetChanges.this, ViewPurchaseOrders.class);
        startActivity(intent);
    }

}