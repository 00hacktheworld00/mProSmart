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
import com.example.sadashivsinha.mprosmart.Adapters.AllBudgetApprovalAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.AllBudgetApprovalList;
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

public class AllBudgetApproval extends AppCompatActivity implements View.OnClickListener {

    private List<AllBudgetApprovalList> budgetList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AllBudgetApprovalAdapter allBudgetApprovalAdapter;
    String currentProjectNo, currentProjectName, currentUser, currentDate;
    View dialogView;
    AlertDialog show;
    private ProgressDialog pDialog;
    JSONArray dataArray;
    JSONObject dataObject;

    AllBudgetApprovalList budgetItems;
    PreferenceManager pm;

    String budgetApprovalId, contractRefNo, amount, currencyCode, createdBy, dateCreated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_budget_approval);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pm = new PreferenceManager(getApplicationContext());
        pm.putString("currentBudget", "approval");
        currentProjectNo = pm.getString("projectId");
        currentProjectName = pm.getString("projectName");
        currentUser = pm.getString("userId");


        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = sdf.format(c.getTime());

        pDialog = new ProgressDialog(AllBudgetApproval.this);
        pDialog.setMessage("Getting Data ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        class MyTask extends AsyncTask<Void, Void, Void> {


            @Override protected void onPreExecute()
            {
                allBudgetApprovalAdapter = new AllBudgetApprovalAdapter(budgetList);
                recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                recyclerView.setLayoutManager(new LinearLayoutManager(AllBudgetApproval.this));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(allBudgetApprovalAdapter);

            }
            @Override
            protected Void doInBackground(Void... params) {
                prepareItems();
                return null;
            }

            @Override protected void onPostExecute(Void result)
            {
                allBudgetApprovalAdapter.notifyDataSetChanged();
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
                Intent intent = new Intent(AllBudgetApproval.this, AllBudgetApprovalCreate.class);
                startActivity(intent);
            }
            break;
        }
    }

    public void prepareItems()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = pm.getString("SERVER_URL") + "/getBudgetApproval?projectId='"+currentProjectNo+"'";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(AllBudgetApproval.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    budgetApprovalId = dataObject.getString("budgetApprovalId");
                                    contractRefNo = dataObject.getString("contractRefNo");
                                    amount = dataObject.getString("amount");
                                    currencyCode = dataObject.getString("currencyCode");
                                    createdBy = dataObject.getString("createdBy");
                                    dateCreated = dataObject.getString("dateCreated");

                                    Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(dateCreated);
                                    dateCreated = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                    budgetItems = new AllBudgetApprovalList(String.valueOf(i+1), budgetApprovalId, createdBy,
                                            dateCreated, "", "", "", contractRefNo, amount+" "+currencyCode);
                                    budgetList.add(budgetItems);

                                    allBudgetApprovalAdapter.notifyDataSetChanged();
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
        Intent intent = new Intent(AllBudgetApproval.this, ViewPurchaseOrders.class);
        startActivity(intent);
    }

}