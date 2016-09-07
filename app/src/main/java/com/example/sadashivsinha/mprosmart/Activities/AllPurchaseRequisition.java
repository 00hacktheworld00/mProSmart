package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.Adapters.AllPurchaseRequisitionAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.AllPurchaseRequisitionList;
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

public class AllPurchaseRequisition extends AppCompatActivity implements View.OnClickListener {

    private List<AllPurchaseRequisitionList> purchaseList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AllPurchaseRequisitionAdapter purchaseAdapter;
    String currentProjectNo, currentProjectName, currentUser, currentDate;
    View dialogView;
    AlertDialog show;
    ProgressDialog pDialog;
    String id, department, createdBy, createdDate, isApproved, isPo;
    JSONArray dataArray;
    JSONObject dataObject;
    PreferenceManager pm;

    AllPurchaseRequisitionList items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_purchase_requisition);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentProjectName = pm.getString("projectName");
        currentUser = pm.getString("userId");

        pDialog = new ProgressDialog(AllPurchaseRequisition.this);
        pDialog.setMessage("Getting Data ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();


        class MyTask extends AsyncTask<Void, Void, Void> {

            @Override protected void onPreExecute() {

                purchaseAdapter = new AllPurchaseRequisitionAdapter(purchaseList);
                recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                recyclerView.setLayoutManager(new LinearLayoutManager(AllPurchaseRequisition.this));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(purchaseAdapter);
            }

            @Override
            protected Void doInBackground(Void... params) {
                prepareItems();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                purchaseAdapter.notifyDataSetChanged();
            }

        }

        new MyTask().execute();

        FloatingActionButton fab_add, fab_search, exportBtn;

        fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        fab_search = (FloatingActionButton) findViewById(R.id.fab_search);
        exportBtn = (FloatingActionButton) findViewById(R.id.exportBtn);

        fab_add.setOnClickListener(this);
        fab_search.setOnClickListener(this);
        exportBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add:
            {
                final AlertDialog.Builder alert = new AlertDialog.Builder(AllPurchaseRequisition.this,android.R.style.Theme_Translucent_NoTitleBar);
                // Set an EditText view to get user input

                dialogView = LayoutInflater.from(AllPurchaseRequisition.this).inflate(R.layout.dialog_new_purchase_requisition, null);
                alert.setView(dialogView);

                show = alert.show();


                final EditText text_department = (EditText) dialogView.findViewById(R.id.text_department);

                Button createBtn = (Button) dialogView.findViewById(R.id.createBtn);

                createBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(text_department.getText().toString().isEmpty())
                        {
                            text_department.setError("Field cannot be empty");
                        }

                        else
                        {
                            final String departmentText = text_department.getText().toString();
                            class MyTask extends AsyncTask<Void, Void, Void> {

                                @Override
                                protected Void doInBackground(Void... params) {
                                    saveData(departmentText);
                                    return null;
                                }

                            }

                            new MyTask().execute();

                        }
                    }
                });
            }
            break;
            case R.id.fab_search:
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Search Purchase Requisition !");
                // Set an EditText view to get user input
                final EditText input = new EditText(this);
                alert.setView(input);
                alert.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(AllPurchaseRequisition.this, "Search for it .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(AllPurchaseRequisition.this, "Search cancelled .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
            }
            break;
            case R.id.exportBtn:
            {
                // to do export
            }
            break;
        }
    }

    public void prepareItems()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = getResources().getString(R.string.server_url) + "/getPurchaseRequisition?projectId='"+currentProjectNo+"'";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(AllPurchaseRequisition.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    id = dataObject.getString("id");
                                    department = dataObject.getString("department");
                                    createdBy = dataObject.getString("createdBy");
                                    createdDate = dataObject.getString("createdDate");
                                    isApproved = dataObject.getString("approved");
                                    isPo = dataObject.getString("isPo");

                                    Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(createdDate);
                                    createdDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                    items = new AllPurchaseRequisitionList(String.valueOf(i+1), id, department,
                                            createdDate, createdBy, isApproved, isPo);
                                    purchaseList.add(items);

                                    purchaseAdapter.notifyDataSetChanged();
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

        if(pDialog!=null)
            pDialog.dismiss();
    }

    public void saveData(final String departmentName)
    {
        JSONObject object = new JSONObject();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String currentDate = sdf.format(c.getTime());

        try {
            object.put("projectId", currentProjectNo);
            object.put("department", departmentName);
            object.put("createdBy", currentUser);
            object.put("createdDate", currentDate);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = getResources().getString(R.string.server_url) + "/postPurchaseRequisition";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("msg").equals("success"))
                            {
                                Toast.makeText(AllPurchaseRequisition.this, "Purchase Requisition Created. ID - " + response.getString("data"), Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(AllPurchaseRequisition.this, AllPurchaseRequisition.class);
                                pm.putString("currentPr", response.getString("data"));
                                pm.putString("departmentPr", departmentName);
                                pm.putString("createdOnPr", currentDate);
                                pm.putString("createdByPr", currentUser);
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
        if(pDialog!=null)
            pDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AllPurchaseRequisition.this, ViewPurchaseOrders.class);
        startActivity(intent);
    }

}