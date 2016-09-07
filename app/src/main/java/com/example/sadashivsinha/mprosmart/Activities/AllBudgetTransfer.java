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
import com.example.sadashivsinha.mprosmart.Adapters.AllBudgetTransferAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.AllBudgetTransferList;
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

public class AllBudgetTransfer extends AppCompatActivity implements View.OnClickListener {

private List<AllBudgetTransferList> budgetList = new ArrayList<>();
private RecyclerView recyclerView;
private AllBudgetTransferAdapter budgetAdapter;
        String currentProjectNo, currentProjectName, currentUser, currentDate;
        View dialogView;
        AlertDialog show;
    String[] wbsNameArray, wbsIdArray;
    String wbsName, wbsId;

    String fromWbs, toWbs, budgetTransfer, createdBy, dateCreated, budgetTransferId;

    JSONArray dataArray;
    JSONObject dataObject;

    AllBudgetTransferList budgetItems;
        ProgressDialog pDialog;

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

                pDialog = new ProgressDialog(AllBudgetTransfer.this);
                pDialog.setMessage("Getting Data ...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();

                class MyTask extends AsyncTask<Void, Void, Void> {


                        @Override protected void onPreExecute()
                        {

                                budgetAdapter = new AllBudgetTransferAdapter(budgetList);
                                recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                                recyclerView.setLayoutManager(new LinearLayoutManager(AllBudgetTransfer.this));
                                recyclerView.setHasFixedSize(true);
                                recyclerView.setAdapter(budgetAdapter);

                        }
                        @Override
                        protected Void doInBackground(Void... params) {
                                getAllWbs();
                                return null;
                        }

                        @Override protected void onPostExecute(Void result)
                        {
                                budgetAdapter.notifyDataSetChanged();
                                if(pDialog!=null)
                                        pDialog.dismiss();
                        }

                }
                new MyTask().execute();


                FloatingActionButton fab_add;

                fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
                fab_add.setLabelText("Create New Budget Transfer");

                fab_add.setOnClickListener(this);
                }

        @Override
        public void onClick(View v) {
                switch (v.getId()) {
                case R.id.fab_add:
                {
                        Intent intent = new Intent(AllBudgetTransfer.this, AllBudgetTransferCreate.class);
                        startActivity(intent);
                }
                break;
                }
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
                                Toast.makeText(AllBudgetTransfer.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                wbsNameArray = new String[dataArray.length() + 1];
                                wbsIdArray = new String[dataArray.length() + 1];

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    wbsName = dataObject.getString("wbsName");
                                    wbsId = dataObject.getString("wbsId");

                                    wbsNameArray[i+1]=wbsName;
                                    wbsIdArray[i+1]=wbsId;
                                }

                                prepareItems(wbsIdArray, wbsNameArray);
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


public void prepareItems(final String[] wbsIdArray, final String[] wbsNameArray)
        {
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            String url = getResources().getString(R.string.server_url) + "/getBudgetTransfer?projectId='"+currentProjectNo+"'";

            JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try{

                                String type = response.getString("type");

                                if(type.equals("ERROR"))
                                {
                                    Toast.makeText(AllBudgetTransfer.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                                }

                                if(type.equals("INFO"))
                                {
                                    dataArray = response.getJSONArray("data");
                                    for(int i=0; i<dataArray.length();i++)
                                    {
                                        dataObject = dataArray.getJSONObject(i);
                                        budgetTransferId = dataObject.getString("budgetTransferId");
                                        fromWbs = dataObject.getString("fromWbs");
                                        toWbs = dataObject.getString("toWbs");
                                        budgetTransfer = dataObject.getString("budgetTransfer");
                                        createdBy = dataObject.getString("createdBy");
                                        dateCreated = dataObject.getString("dateCreated");

                                        Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(dateCreated);
                                        dateCreated = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                        for(int j=0; j<wbsIdArray.length;j++)
                                        {
                                            if(fromWbs.equals(wbsIdArray[j]))
                                            {
                                                fromWbs = wbsNameArray[j];
                                            }
                                        }

                                        for(int j=0; j<wbsIdArray.length;j++)
                                        {
                                            if(toWbs.equals(wbsIdArray[j]))
                                            {
                                                toWbs = wbsNameArray[j];
                                            }
                                        }

                                        budgetItems = new AllBudgetTransferList(String.valueOf(i+1), dateCreated, createdBy, budgetTransfer, toWbs, fromWbs, budgetTransferId);
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
        public void onBackPressed()
        {
                Intent intent = new Intent(AllBudgetTransfer.this, ViewPurchaseOrders.class);
                startActivity(intent);

        }
}