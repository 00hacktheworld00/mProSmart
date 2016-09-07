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
import android.widget.AdapterView;
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
import com.example.sadashivsinha.mprosmart.Adapters.AllBoqAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.AllBoqList;
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

public class AllBoq extends AppCompatActivity implements View.OnClickListener {

    private List<AllBoqList> allBoqList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AllBoqAdapter allBoqAdapter;
    String currentProjectNo, currentProjectName, currentProjectDesc, currentUser, currentDate, currentUom, itemName;

    JSONObject dataObject;
    JSONArray dataArray;
    AllBoqList qualityItem;
    View dialogView;
    AlertDialog show;
    Spinner spinner_boq_item;
    EditText text_quantity;
    Button createBtn;
    String item, uomId;
    String[] itemArray, uomIdArray;
    ProgressDialog pDialog;
    String id, boqItem, unit, uom, createdBy, createdDate, projectDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_boq);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final PreferenceManager pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentProjectName = pm.getString("projectName");
        currentProjectDesc = pm.getString("projectDesc");
        currentUser = pm.getString("userId");


        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = sdf.format(c.getTime());

        class MyTask extends AsyncTask<Void, Void, Void> {


            @Override protected void onPreExecute()
            {
                pDialog = new ProgressDialog(AllBoq.this);
                pDialog.setMessage("Getting Data ...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();

                allBoqAdapter = new AllBoqAdapter(allBoqList);
                recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                recyclerView.setLayoutManager(new LinearLayoutManager(AllBoq.this));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(allBoqAdapter);

            }
            @Override
            protected Void doInBackground(Void... params) {
                prepareItems();
                return null;
            }

            @Override protected void onPostExecute(Void result)
            {
                allBoqAdapter.notifyDataSetChanged();
                if(pDialog!=null)
                    pDialog.dismiss();
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
//                Intent intent = new Intent(AllBoq.this, BOQCreate.class);
//                startActivity(intent);

                final AlertDialog.Builder alert = new AlertDialog.Builder(AllBoq.this,android.R.style.Theme_Translucent_NoTitleBar);
                // Set an EditText view to get user input

                dialogView = LayoutInflater.from(AllBoq.this).inflate(R.layout.dialog_new_boq, null);
                alert.setView(dialogView);

                show = alert.show();

                createBtn = (Button) dialogView.findViewById(R.id.createBtn);

                spinner_boq_item = (Spinner) dialogView.findViewById(R.id.spinner_boq_item);
                text_quantity = (EditText) dialogView.findViewById(R.id.text_quantity);

                pDialog = new ProgressDialog(AllBoq.this);
                pDialog.setMessage("Getting BOQ Items ...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();


                class MyTask extends AsyncTask<Void, Void, Void> {

                    @Override
                    protected Void doInBackground(Void... params) {
                        getAllBoq();
                        return null;
                    }

                }

                new MyTask().execute();

                spinner_boq_item.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        currentUom = uomIdArray[position];
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                createBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(text_quantity.getText().toString().isEmpty())
                        {
                            text_quantity.setError("Field cannot be empty.");
                        }
                        else if(spinner_boq_item.getSelectedItem().toString().equals("Select BOQ Item"))
                        {
                            Toast.makeText(AllBoq.this, "Select BOQ Item first", Toast.LENGTH_SHORT).show();
                        }

                        else
                        {
                            pDialog = new ProgressDialog(AllBoq.this);
                            pDialog.setMessage("Sending Data ...");
                            pDialog.setIndeterminate(false);
                            pDialog.setCancelable(true);
                            pDialog.show();

                            class MyTask extends AsyncTask<Void, Void, Void> {

                                @Override
                                protected Void doInBackground(Void... params) {
                                    saveBoq();
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
                alert.setTitle("Search BOQ !");
                // Set an EditText view to get user input
                final EditText input = new EditText(this);
                alert.setView(input);
                alert.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(AllBoq.this, "Search for it .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(AllBoq.this, "Search cancelled .", Toast.LENGTH_SHORT).show();
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

        String url = getResources().getString(R.string.server_url) + "/getBoq?projectId='"+currentProjectNo+"'";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(AllBoq.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    id = dataObject.getString("id");
                                    boqItem = dataObject.getString("boqItem");
                                    projectDescription = dataObject.getString("projectDescription");
                                    unit = dataObject.getString("unit");
                                    uom = dataObject.getString("uom");
                                    createdBy = dataObject.getString("createdBy");
                                    createdDate = dataObject.getString("createdDate");
                                    itemName = dataObject.getString("itemName");


                                    Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(createdDate);
                                    createdDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                    qualityItem = new AllBoqList(String.valueOf(i+1), id, currentProjectNo, currentProjectName,
                                            unit, uom, itemName , createdBy, createdDate);
                                    allBoqList.add(qualityItem);

                                    allBoqAdapter.notifyDataSetChanged();
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

    public void getAllBoq()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = getResources().getString(R.string.server_url) + "/getBoqItems?projectId=\""+currentProjectNo+"\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                itemArray = new String[dataArray.length()+1];
                                uomIdArray = new String[dataArray.length()+1];

                                itemArray[0] = "Select BOQ Item";
                                uomIdArray[0] = "Select BOQ";

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    item = dataObject.getString("item");
                                    uomId = dataObject.getString("uom");

                                    itemArray[i+1] = item;
                                    uomIdArray[i+1] = uomId;
                                }

                                if(itemArray!=null)
                                {
                                    ArrayAdapter<String> adapterCurrency = new ArrayAdapter<String>(dialogView.getContext(),
                                            android.R.layout.simple_dropdown_item_1line, itemArray);

                                    spinner_boq_item.setAdapter(adapterCurrency);
                                }

                                else
                                {
                                    Toast.makeText(AllBoq.this, "No BOQ Items in this project", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(AllBoq.this, AllBoq.class);
                                    startActivity(intent);
                                }
                                pDialog.dismiss();

                            }

                        }catch(JSONException e){e.printStackTrace();
                            pDialog.dismiss();}
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley","Error");
                        pDialog.dismiss();

                    }
                }
        );
        requestQueue.add(jor);
        if(pDialog!=null)
        {
            pDialog.dismiss();
        }
    }

    public void saveBoq()
    {
        JSONObject object = new JSONObject();

        try {
            object.put("projectId",currentProjectNo);
            object.put("projectName", currentProjectName);
            object.put("projectDescription", currentProjectDesc);
            object.put("boqItem",spinner_boq_item.getSelectedItem().toString());
            object.put("unit", text_quantity.getText().toString());
//            object.put("itemName", text_currency.getText().toString());
            object.put("uom", currentUom);
            object.put("createdBy", currentUser);
            object.put("createdDate", currentDate);

            Log.d("REQUEST SENT OF JSON :" , object.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(AllBoq.this);

        String url = AllBoq.this.getResources().getString(R.string.server_url) + "/postBoq";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            Log.d("SERVER RESPONSE :", response.toString());

                            if(response.getString("msg").equals("success"))
                            {
                                Toast.makeText(AllBoq.this, "BOQ Created. ID - "+ response.getString("data"), Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                                Intent intent = new Intent(AllBoq.this, AllBoq.class);
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
        Intent intent = new Intent(AllBoq.this, ViewPurchaseOrders.class);
        startActivity(intent);
    }

}