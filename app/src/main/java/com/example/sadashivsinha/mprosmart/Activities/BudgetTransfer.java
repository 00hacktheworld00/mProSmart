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

public class BudgetTransfer extends AppCompatActivity {
    private List<BudgetList> budgetList = new ArrayList<>();
    private RecyclerView recyclerView;
    private BudgetAdapter budgetAdapter;
    private ProgressDialog pDialog, pDialog1;
    private JSONObject dataObject;
    private JSONArray dataArray;
    String currentProjectNo, currentProjectName, currentCreatedBy;
    String budgetTransferId, projectId, createdByHead, contractRefNo, budgetTransfer, description, dateCreatedHead;
    TextView budget_transfer_id, project_id, project_name, date, desc, created_by;
    BudgetList items;
    ConnectionDetector cd;
    public static final String TAG = BudgetTransfer.class.getSimpleName();
    Boolean isInternetPresent = false;
    String lineId, currencyCode, itemId, quantity, uomId, ammount, createdBy, dateCreated, currentTransferNo;
    PreferenceManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_transfer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pm = new PreferenceManager(getApplicationContext());
        currentTransferNo = pm.getString("currentTransfer");

        FloatingActionButton fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BudgetTransfer.this, BudgetTransferCreate.class);
                startActivity(intent);
            }
        });

        created_by = (TextView) findViewById(R.id.created_by);
        budget_transfer_id = (TextView) findViewById(R.id.budget_transfer_id);
        date = (TextView) findViewById(R.id.date);
        project_id = (TextView) findViewById(R.id.project_id);
        project_name = (TextView) findViewById(R.id.project_name);
        desc = (TextView) findViewById(R.id.desc);

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_content = (RelativeLayout) findViewById(R.id.main_layout);
            Snackbar snackbar = Snackbar.make(main_content,getResources().getString(R.string.no_internet_error), Snackbar.LENGTH_LONG);
            snackbar.show();
        }

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

        PreferenceManager pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentProjectName = pm.getString("projectName");
        currentCreatedBy = pm.getString("userId");

        project_id.setText(currentProjectNo);
        project_name.setText(currentProjectName);


        date.setText(pm.getString("created_on"));
        created_by.setText(pm.getString("created_by"));
//        pDialog1 = new ProgressDialog(BudgetTransfer.this);
//        pDialog1.setMessage("Preparing Header ...");
//        pDialog1.setIndeterminate(false);
//        pDialog1.setCancelable(true);
//        pDialog1.show();
//        prepareHeader();


        pDialog = new ProgressDialog(BudgetTransfer.this);
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
                recyclerView.setLayoutManager(new LinearLayoutManager(BudgetTransfer.this));
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
//                                Toast.makeText(BudgetTransfer.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
//                            }
//
//                            if(type.equals("INFO"))
//                            {
//                                dataArray = response.getJSONArray("data");
//                                for(int i=0; i<dataArray.length();i++)
//                                {
//                                    dataObject = dataArray.getJSONObject(i);
//
//                                    budgetTransferId = dataObject.getString("budgetTransferId");
//                                    projectId = dataObject.getString("projectId");
//                                    createdByHead = dataObject.getString("createdBy");
//                                    contractRefNo = dataObject.getString("contractRefNo");
//                                    budgetTransfer = dataObject.getString("budgetTransfer");
//                                    description = dataObject.getString("description");
//                                    dateCreatedHead = dataObject.getString("dateCreated");
//
//                                    budget_transfer_id.setText(budgetTransferId);
//                                    date.setText(dateCreatedHead);
//                                    created_by.setText(createdByHead);
//                                    project_id.setText(projectId);
//                                    project_name.setText(currentProjectName);
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

    public void prepareItems()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = pm.getString("SERVER_URL") + "/getBudgetTransferLine?budgetTransferId=\""+currentTransferNo+"\"";

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
                                Toast.makeText(BudgetTransfer.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);


                                    lineId = dataObject.getString("lineId");
                                    budgetTransferId = dataObject.getString("budgetTransferId");
                                    currencyCode = dataObject.getString("currencyCode");
                                    itemId = dataObject.getString("itemId");
                                    quantity = dataObject.getString("quantity");
                                    uomId = dataObject.getString("uomId");
                                    ammount = dataObject.getString("ammount");
                                    createdBy = dataObject.getString("createdBy");
                                    dateCreated = dataObject.getString("dateCreated");

                                    items = new BudgetList(String.valueOf(i+1),lineId, currencyCode, "", itemId, "", quantity, uomId, ammount);
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
}
