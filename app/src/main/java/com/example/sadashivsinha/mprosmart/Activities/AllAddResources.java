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
import com.example.sadashivsinha.mprosmart.Adapters.AllAddResourcesAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.AllAddResourcesList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.github.clans.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AllAddResources extends AppCompatActivity implements View.OnClickListener {

    private List<AllAddResourcesList> list = new ArrayList<>();
    private RecyclerView recyclerView;
    private AllAddResourcesAdapter adapter;
    String currentProjectNo, currentProjectName, currentUser, currentDate;
    View dialogView;
    AlertDialog show;

    AllAddResourcesList items;
    PreferenceManager pm;
    ProgressDialog pDialog;
    JSONArray dataArray;
    JSONObject dataObject;
    String firstName, lastName, resourceTypeId, designationId, ratePerHour, currencyId, emailId, phone, houseNo ,streetName,
            locality, state, country, resId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_add_resources);
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

        adapter = new AllAddResourcesAdapter(list);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        pDialog = new ProgressDialog(AllAddResources.this);
        pDialog.setMessage("Getting Details ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

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
                Intent intent = new Intent(AllAddResources.this, AddResourceActivity.class);
                startActivity(intent);
            }
            break;
        }
    }

    public void prepareItems()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = getResources().getString(R.string.server_url) + "/getResource";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(AllAddResources.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                Log.d("response resources : ", response.toString());

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);

                                        dataObject = dataArray.getJSONObject(i);
                                        resId = dataObject.getString("id");
                                        firstName = dataObject.getString("firstName");
                                        lastName = dataObject.getString("lastName");
                                        resourceTypeId = dataObject.getString("resourceTypeId");
                                        designationId = dataObject.getString("designationId");
                                        ratePerHour = dataObject.getString("ratePerHour");

                                        currencyId = dataObject.getString("currencyId");

                                        emailId = dataObject.getString("emailId");
                                        phone = dataObject.getString("phone");
                                        houseNo = dataObject.getString("houseNo");
                                        streetName = dataObject.getString("streetName");
                                        locality = dataObject.getString("locality");
                                        state = dataObject.getString("state");
                                        country = dataObject.getString("country");


                                    items = new AllAddResourcesList(String.valueOf(i+1), resId, firstName+" "+lastName, resourceTypeId, designationId, emailId,
                                            phone, ratePerHour, currencyId);
                                    list.add(items);

                                    adapter.notifyDataSetChanged();

                                }
                                pDialog.dismiss();
                            }
                            pDialog.dismiss();
                        }catch(JSONException e){e.printStackTrace();
                            pDialog.dismiss();}
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

        if(pDialog!=null)
            pDialog.dismiss();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AllAddResources.this, ViewPurchaseOrders.class);
        startActivity(intent);
    }

}