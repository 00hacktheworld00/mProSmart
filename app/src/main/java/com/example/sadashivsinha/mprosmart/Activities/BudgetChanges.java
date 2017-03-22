package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.Adapters.BudgetAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.BudgetList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.github.clans.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BudgetChanges extends AppCompatActivity {
    private List<BudgetList> budgetList = new ArrayList<>();
    private RecyclerView recyclerView;
    private BudgetAdapter budgetAdapter;
    private BudgetList items;
    private String budgetChangesLineId, budgetChangesId, currencyCode, itemId, dateCreated, itemDescription, quantity, uomId,
            amount, createdBy;
    private TextView budget_id, contract_ref, original_budget, current_req, total_budget, desc;
    private String budgetChangesIdHead, projectId, createdByHead, contractRefNo, originalBudget, currentBudget, totalBudget,
            description, dateCreatedHead;
    JSONArray dataArray;
    JSONObject dataObject;
    ConnectionDetector cd;
    public static final String TAG = BudgetChanges.class.getSimpleName();
    Boolean isInternetPresent = false;
    ProgressDialog pDialog, pDialog1;
    String currentProjectNo, currentProjectName, currentCreatedBy, currentBudgetChangeId;
    PreferenceManager pm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_changes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BudgetChanges.this, BudgetChangeCreate.class);
                startActivity(intent);
            }
        });

        budget_id = (TextView) findViewById(R.id.budget_id);
        contract_ref = (TextView) findViewById(R.id.contract_ref);
        original_budget = (TextView) findViewById(R.id.original_budget);
        current_req = (TextView) findViewById(R.id.current_req);
        total_budget = (TextView) findViewById(R.id.total_budget);
        desc = (TextView) findViewById(R.id.desc);

//
//        final LinearLayout hiddenLayout = (LinearLayout) findViewById(R.id.hiddenLayout);
//
//        hiddenLayout.setVisibility(View.GONE);
//
//        CardView cardview = (CardView) findViewById(R.id.cardview);
//        cardview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(hiddenLayout.getVisibility()==View.GONE)
//                {
//                    hiddenLayout.setVisibility(View.VISIBLE);
//                    hiddenLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.view_show));
//                }
//                else
//                {
//                    hiddenLayout.setVisibility(View.GONE);
//                }
//            }
//        });


        pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentProjectName = pm.getString("projectName");
        currentCreatedBy = pm.getString("userId");
        currentBudgetChangeId = pm.getString("budgetChangeId");

        original_budget.setText(pm.getString("original_amount"));
        current_req.setText(pm.getString("current_budget"));
        total_budget.setText(pm.getString("total_budget"));
        desc.setText(pm.getString("description"));
        contract_ref.setText(pm.getString("contractRef"));

//        pDialog1 = new ProgressDialog(BudgetChanges.this);
//        pDialog1.setMessage("Preparing Header ...");
//        pDialog1.setIndeterminate(false);
//        pDialog1.setCancelable(true);
//        pDialog1.show();
//        prepareHeader();


        pDialog = new ProgressDialog(BudgetChanges.this);
        pDialog.setMessage("Getting Data ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        class MyTask extends AsyncTask<Void, Void, Void>
        {
            @Override protected void onPreExecute()
            {
                budgetAdapter = new BudgetAdapter(budgetList);
                recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                recyclerView.setLayoutManager(new LinearLayoutManager(BudgetChanges.this));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(budgetAdapter);

            }

            @Override
            protected Void doInBackground(Void... params)
            {
                prepareItems();
                return null;
            }

            @Override protected void onPostExecute(Void result)
            {
                budgetAdapter.notifyDataSetChanged();
            }

        }

        new MyTask().execute();
        budgetAdapter.notifyDataSetChanged();
    }

    private void rotate(float degree, ImageButton imageView)
    {
        final RotateAnimation rotateAnim = new RotateAnimation(0.0f, degree,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        rotateAnim.setDuration(500);
        rotateAnim.setFillAfter(true);
        imageView.startAnimation(rotateAnim);
    }

    public void prepareItems()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = pm.getString("SERVER_URL") + "/getBudgetChangesLine?budgetChangesId=\""+currentBudgetChangeId+"\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("TAG",response.toString());
                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                pDialog.dismiss();
                                Toast.makeText(BudgetChanges.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);

                                    budgetChangesLineId = dataObject.getString("budgetChangesLineId");
                                    budgetChangesId = dataObject.getString("budgetChangesId");
                                    currencyCode = dataObject.getString("currencyCode");
                                    itemId = dataObject.getString("itemId");
                                    itemDescription = dataObject.getString("itemDescription");
                                    quantity = dataObject.getString("quantity");
                                    uomId = dataObject.getString("uomId");
                                    amount = dataObject.getString("amount");
                                    createdBy = dataObject.getString("createdBy");
                                    dateCreated = dataObject.getString("dateCreated");

                                    items = new BudgetList(String.valueOf(i+1),budgetChangesLineId, currencyCode, "", itemId, itemDescription , quantity, uomId, amount);
                                    budgetList.add(items);

                                    budgetAdapter.notifyDataSetChanged();

                                }
                            }
                            pDialog.dismiss();
                        }catch(JSONException e){
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

//    public void prepareHeader()
//    {
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//
//
//        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//
//                        try{
//
//                            String type = response.getString("type");
//
//                            if(type.equals("ERROR"))
//                            {
//                                pDialog1.dismiss();
//                                Toast.makeText(BudgetChanges.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
//                            }
//
//                            if(type.equals("INFO"))
//                            {
//                                dataArray = response.getJSONArray("data");
//                                for(int i=0; i<dataArray.length();i++)
//                                {
//                                    dataObject = dataArray.getJSONObject(i);
//
//                                    budgetChangesIdHead = dataObject.getString("budgetChangesId");
//                                    projectId = dataObject.getString("projectId");
//                                    createdByHead = dataObject.getString("createdBy");
//                                    contractRefNo = dataObject.getString("contractRefNo");
//                                    originalBudget = dataObject.getString("originalBudget");
//                                    currentBudget = dataObject.getString("currentBudget");
//                                    totalBudget = dataObject.getString("totalBudget");
//                                    description = dataObject.getString("description");
//                                    dateCreatedHead = dataObject.getString("dateCreated");
//
//                                    budget_id.setText(budgetChangesIdHead);
//                                    contract_ref.setText(contractRefNo);
//                                    original_budget.setText(originalBudget);
//                                    current_req.setText(currentBudget);
//                                    total_budget.setText(totalBudget);
//                                    desc.setText(description);
//
//                                }
//                            }
//                            pDialog1.dismiss();
//                        }catch(JSONException e){
//                            pDialog1.dismiss();
//                            e.printStackTrace();}
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        pDialog1.dismiss();
//                        Log.e("Volley","Error");
//
//                    }
//                }
//        );
//        requestQueue.add(jor);
//    }
}
