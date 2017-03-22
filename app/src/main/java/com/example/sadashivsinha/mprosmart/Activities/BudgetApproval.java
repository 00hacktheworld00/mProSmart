package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
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

public class BudgetApproval extends AppCompatActivity {
    private List<BudgetList> budgetList = new ArrayList<>();
    private RecyclerView recyclerView;
    private BudgetAdapter budgetAdapter;
    private ProgressDialog pDialog, pDialog1;
    private BudgetList items;
    JSONArray dataArray;
    JSONObject dataObject;
    String currentProjectNo, currentProjectName, currentCreatedBy,currentApproval ;
    ConnectionDetector cd;
    public static final String TAG = BudgetApproval.class.getSimpleName();
    Boolean isInternetPresent = false;
    String currentBudgetId, budgetApprovalLineId, currencyCode, itemId, itemDescription, quantity, uomId, ammount, createdBy, dateCreated, budgetApprovalId;
    String contractRefNo, projectId, amount, description;
    TextView budget_id, project_id, project_name, date, desc, created_by;
    PreferenceManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_approval);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BudgetApproval.this, BudgetApprovalCreate.class);
                startActivity(intent);
            }
        });

        cd = new ConnectionDetector(getApplicationContext());

        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_content = (RelativeLayout) findViewById(R.id.main_content);
            Snackbar snackbar = Snackbar.make(main_content,getResources().getString(R.string.no_internet_error), Snackbar.LENGTH_LONG);
            snackbar.show();
        }



        pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentProjectName = pm.getString("projectName");
        currentCreatedBy = pm.getString("userId");
        currentApproval = pm.getString("currentApproval");

        budget_id = (TextView) findViewById(R.id.budget_id);
        project_id = (TextView) findViewById(R.id.project_id);
        project_name = (TextView) findViewById(R.id.project_name);
        date = (TextView) findViewById(R.id.date);
        desc = (TextView) findViewById(R.id.desc);
        created_by = (TextView) findViewById(R.id.created_by);

        project_id.setText(currentProjectNo);
        project_name.setText(currentProjectName);

        date.setText(pm.getString("created_on"));
        created_by.setText(pm.getString("created_by"));

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


//        pDialog1 = new ProgressDialog(BudgetApproval.this);
//        pDialog1.setMessage("Preparing Header ...");
//        pDialog1.setIndeterminate(false);
//        pDialog1.setCancelable(true);
//        pDialog1.show();
//        prepareHeader();


        pDialog = new ProgressDialog(BudgetApproval.this);
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
                recyclerView.setLayoutManager(new LinearLayoutManager(BudgetApproval.this));
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

        String url = pm.getString("SERVER_URL") + "/getBudgetApprovalLine?budgetApprovalId=\""+currentApproval+"\"";

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
                                Toast.makeText(BudgetApproval.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);

                                    budgetApprovalLineId = dataObject.getString("budgetApprovalLineId");
                                    currencyCode = dataObject.getString("currencyCode");
                                    itemId = dataObject.getString("itemId");
                                    itemDescription = dataObject.getString("itemDescription");
                                    quantity = dataObject.getString("quantity");
                                    uomId = dataObject.getString("uomId");
                                    ammount = dataObject.getString("ammount");
                                    createdBy = dataObject.getString("createdBy");
                                    dateCreated = dataObject.getString("dateCreated");
                                    budgetApprovalId = dataObject.getString(" budgetApprovalId");
                                    items = new BudgetList(String.valueOf(i+1), budgetApprovalLineId, currencyCode, "Rupee", itemId, itemDescription, quantity, uomId, ammount);
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
//                                Toast.makeText(BudgetApproval.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
//                            }
//
//                            if(type.equals("INFO"))
//                            {
//                                dataArray = response.getJSONArray("data");
//                                for(int i=0; i<dataArray.length();i++)
//                                {
//                                    dataObject = dataArray.getJSONObject(i);
//
//                                    budgetApprovalId = dataObject.getString("budgetApprovalId");
//                                    projectId = dataObject.getString("projectId");
//                                    contractRefNo = dataObject.getString("contractRefNo");
//                                    amount = dataObject.getString("amount");
//                                    description = dataObject.getString("description");
//                                    currencyCode = dataObject.getString("currencyCode");
//                                    createdBy = dataObject.getString("createdBy");
//                                    dateCreated = dataObject.getString("dateCreated");
//
//                                    budget_id.setText(budgetApprovalId);
//                                    date.setText(dateCreated);
//                                    desc.setText(description);
//                                    created_by.setText(createdBy);
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
