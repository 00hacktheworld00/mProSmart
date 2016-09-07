package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.Adapters.AllQualityStandardAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.AllQualityStandardList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.example.sadashivsinha.mprosmart.font.HelveticaBold;
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

public class AllQualityStandards extends AppCompatActivity implements View.OnClickListener {

    private List<AllQualityStandardList> qualityList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AllQualityStandardAdapter qualityAdapter;
    String currentProjectNo, currentProjectName, currentUser, currentDate;
    HelveticaBold text_desc_label;

    AllQualityStandardList qualityItem;
    ProgressDialog pDialog;
    JSONArray dataArray;
    JSONObject dataObject;
    Boolean isInternetPresent = false;
    String id, itemId, itemDescription, createdBy, createdDate;
    PreferenceManager pm;
    View dialogView;
    AlertDialog show;
    String[] itemsArray, itemDescArray, itemIdArray;
    String item, itemDesc, currentItemId;
    Spinner spinner_item;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_all_quality_standards);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            pm = new PreferenceManager(getApplicationContext());
            currentProjectNo = pm.getString("projectId");
            currentProjectName = pm.getString("projectName");
            currentProjectName = pm.getString("projectName");
            currentUser = pm.getString("userId");

            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            currentDate = sdf.format(c.getTime());

            ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
            isInternetPresent = cd.isConnectingToInternet();

            // check for Internet status
            if (!isInternetPresent) {
                // Internet connection is not present
                // Ask user to connect to Internet
                CoordinatorLayout main_content = (CoordinatorLayout) findViewById(R.id.main_content);
                Snackbar snackbar = Snackbar.make(main_content, getResources().getString(R.string.no_internet_error), Snackbar.LENGTH_LONG);
                snackbar.show();
            }

            pDialog = new ProgressDialog(AllQualityStandards.this);
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();


            class MyTask extends AsyncTask<Void, Void, Void> {

                @Override protected void onPreExecute() {

                    qualityAdapter = new AllQualityStandardAdapter(qualityList);
                    recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                    recyclerView.setLayoutManager(new LinearLayoutManager(AllQualityStandards.this));
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(qualityAdapter);
                }

                @Override
                protected Void doInBackground(Void... params) {
                    prepareItems();
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    qualityAdapter.notifyDataSetChanged();
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
//                    Intent intent = new Intent(AllQualityStandards.this, QualityStandardCreate.class);
//                    startActivity(intent);

                    createQualityStandard();
                }
                break;
                case R.id.fab_search:
                {
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setTitle("Search Quality Standards !");
                    // Set an EditText view to get user input
                    final EditText input = new EditText(this);
                    alert.setView(input);
                    alert.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Toast.makeText(AllQualityStandards.this, "Search for it .", Toast.LENGTH_SHORT).show();
                        }
                    });
                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Toast.makeText(AllQualityStandards.this, "Search cancelled .", Toast.LENGTH_SHORT).show();
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

        String url = getResources().getString(R.string.server_url) + "/getQualityStandard?projectId='"+currentProjectNo+"'";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(AllQualityStandards.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    id = dataObject.getString("id");
                                    itemDescription = dataObject.getString("itemDescription");
                                    itemId = dataObject.getString("itemId");
                                    createdBy = dataObject.getString("createdBy");
                                    createdDate = dataObject.getString("createdDate");

                                    Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(createdDate);
                                    createdDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);


                                    qualityItem = new AllQualityStandardList(String.valueOf(i+1), id,itemId, itemDescription,
                                            createdDate, createdBy, currentProjectNo);
                                    qualityList.add(qualityItem);

                                    qualityAdapter.notifyDataSetChanged();
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

    public void createQualityStandard()
    {
        final AlertDialog.Builder alert = new AlertDialog.Builder(AllQualityStandards.this,android.R.style.Theme_Translucent_NoTitleBar);
        // Set an EditText view to get user input

        dialogView = LayoutInflater.from(AllQualityStandards.this).inflate(R.layout.dialog_new_quality_standard, null);
        alert.setView(dialogView);

        show = alert.show();

        spinner_item = (Spinner) dialogView.findViewById(R.id.spinner_item);
        final TextView text_item_desc = (TextView) dialogView.findViewById(R.id.text_item_desc);
        text_desc_label = (HelveticaBold) dialogView.findViewById(R.id.text_desc_label);

        text_desc_label.setVisibility(View.GONE);


        Button createBtn = (Button) dialogView.findViewById(R.id.createBtn);

        pDialog = new ProgressDialog(dialogView.getContext());
        pDialog.setMessage("Getting Items...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        class MyTask extends AsyncTask<Void, Void, Void>
        {
            @Override
            protected Void doInBackground(Void... params)
            {
                prepareItemList();
                return null;
            }
        }

        new MyTask().execute();

        spinner_item.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0)
                {
                    text_item_desc.setVisibility(View.GONE);
                    text_desc_label.setVisibility(View.GONE);
                }
                else
                {
                    text_item_desc.setVisibility(View.VISIBLE);
                    text_desc_label.setVisibility(View.VISIBLE);
                    text_item_desc.setText(itemDescArray[position]);
                    currentItemId = itemIdArray[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(spinner_item.getSelectedItem().toString().equals("Select Items"))
                {
                    Toast.makeText(AllQualityStandards.this, "Select Items First", Toast.LENGTH_SHORT).show();
                }

                else {
                    pDialog = new ProgressDialog(AllQualityStandards.this);
                    pDialog.setMessage("Sending Data ...");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(true);
                    pDialog.show();

                    itemId = spinner_item.getSelectedItem().toString();
                    itemDesc = text_item_desc.getText().toString();

                    class MyTask extends AsyncTask<Void, Void, Void> {

                        @Override
                        protected Void doInBackground(Void... params) {
                            saveData();
                            return null;
                        }
                    }
                    new MyTask().execute();

                }
            }
        });


    }


    public void saveData()
    {
        JSONObject object = new JSONObject();

        try {
            object.put("projectId",currentProjectNo);
            object.put("itemId",currentItemId);
            object.put("itemDescription",itemDesc);
            object.put("createdBy",currentUser);
            object.put("createdDate",currentDate);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(AllQualityStandards.this);

        String url = AllQualityStandards.this.getResources().getString(R.string.server_url) + "/postQualityStandard";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("msg").equals("success"))
                            {
                                String successMsg = "Quality Control Created. ID - "+response.getString("data").toString();
                                Toast.makeText(AllQualityStandards.this, successMsg, Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                                Intent intent = new Intent(AllQualityStandards.this, AllQualityStandards.class);
                                startActivity(intent);
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
                        Log.e("Volley","Error");
                        Toast.makeText(AllQualityStandards.this, error.toString(), Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                    }
                }
        );
        requestQueue.add(jor);
    }

    public void prepareItemList()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = getResources().getString(R.string.server_url) + "/getItems?projectId='"+currentProjectNo+"'";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");
                            String msg = response.getString("msg");
                            Log.d("response->", String.valueOf(response));

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(AllQualityStandards.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                itemsArray = new String[dataArray.length()+1];
                                itemDescArray = new String[dataArray.length()+1];
                                itemIdArray = new String[dataArray.length()+1];
                                itemsArray[0]="Select Items";
                                itemDescArray[0]="Select Item";
                                itemIdArray[0]="Select Item";
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    item = dataObject.getString("itemName");
                                    itemDesc = dataObject.getString("itemDescription");
                                    itemId = dataObject.getString("itemId");

                                    itemsArray[i+1]=item;
                                    itemDescArray[i+1]=itemDesc;
                                    itemDescArray[i+1]=itemDesc;
                                    itemIdArray[i+1]=itemId;
                                }
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(AllQualityStandards.this,
                                        android.R.layout.simple_dropdown_item_1line,itemsArray);
                                spinner_item.setAdapter(adapter);
                                pDialog.dismiss();
                            }

                            if(msg.equals("No data"))
                            {
                                Toast.makeText(AllQualityStandards.this, "No Items Found.", Toast.LENGTH_LONG).show();
                                pDialog.dismiss();
                                finish();
                            }
                        }catch(JSONException e){e.printStackTrace();}
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AllQualityStandards.this, ViewPurchaseOrders.class);
        startActivity(intent);
    }
}