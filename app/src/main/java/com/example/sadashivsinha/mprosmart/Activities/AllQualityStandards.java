package com.example.sadashivsinha.mprosmart.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.Adapters.AllQualityStandardAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.AllQualityStandardList;
import com.example.sadashivsinha.mprosmart.ModelLists.AllSiteDiaryList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.AppController;
import com.example.sadashivsinha.mprosmart.Utils.Communicator;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.example.sadashivsinha.mprosmart.Utils.CsvCreateUtility;
import com.example.sadashivsinha.mprosmart.font.HelveticaBold;
import com.github.clans.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

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
    String url;
    public static final String TAG = AllQualityStandards.class.getSimpleName();

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

            url = pm.getString("SERVER_URL") + "/getQualityStandard?projectId='"+currentProjectNo+"'";

            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            currentDate = sdf.format(c.getTime());

            ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
            isInternetPresent = cd.isConnectingToInternet();


                qualityAdapter = new AllQualityStandardAdapter(qualityList);
                recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                recyclerView.setLayoutManager(new LinearLayoutManager(AllQualityStandards.this));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(qualityAdapter);

            if (!isInternetPresent) {
                // Internet connection is not present
                // Ask user to connect to Internet
                RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_content);
                Crouton.cancelAllCroutons();
                Crouton.makeText(AllQualityStandards.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

                pDialog = new ProgressDialog(AllQualityStandards.this);
                pDialog.setMessage("Getting cache data");
                pDialog.show();

                Cache cache = AppController.getInstance().getRequestQueue().getCache();
                Cache.Entry entry = cache.get(url);
                if (entry != null) {
                    //Cache data available.
                    try {
                        String data = new String(entry.data, "UTF-8");
                        Log.d("CACHE DATA", data);
                        JSONObject jsonObject = new JSONObject(data);
                        try {
                            dataArray = jsonObject.getJSONArray("data");

                            for (int i = 0; i < dataArray.length(); i++) {
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

                            pDialog.dismiss();

                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                        }
//                    Toast.makeText(getApplicationContext(), "Loading from cache.", Toast.LENGTH_SHORT).show();


                        Boolean createQualityStandardPending = pm.getBoolean("createQualityStandardPending");

                        if (createQualityStandardPending) {
                            String jsonObjectVal = pm.getString("objectQualityStandardLine");
                            Log.d("JSON QS PENDING :", jsonObjectVal);

                            JSONObject jsonObjectPending = new JSONObject(jsonObjectVal);
                            Log.d("JSONObj QS PENDING :", jsonObjectPending.toString());

                            itemDescription = jsonObjectPending.getString("itemDescription");
                            itemId = jsonObjectPending.getString("itemId");
                            createdBy = jsonObjectPending.getString("createdBy");
                            createdDate = jsonObjectPending.getString("createdDate");

                            Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(createdDate);
                            createdDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);


                            qualityItem = new AllQualityStandardList(String.valueOf(dataArray.length()+1), getResources().getString(R.string.waiting_to_connect),itemId, itemDescription,
                                    createdDate, createdBy, currentProjectNo);
                            qualityList.add(qualityItem);

                            qualityAdapter.notifyDataSetChanged();

                            if (pDialog != null)
                                pDialog.dismiss();
                        }
                    }
                    catch(UnsupportedEncodingException | JSONException | ParseException e){
                        e.printStackTrace();
                    }
                }

                else
                {
                    Toast.makeText(AllQualityStandards.this, "Offline Data Not available for Quality Standard", Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                }
            }
            else
            {
                // Cache data not exist.
                prepareItems();
            }

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
                    int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                    if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP) {
                        if (ContextCompat.checkSelfPermission(AllQualityStandards.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(AllQualityStandards.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                        }
                        Environment.getExternalStorageState();

                        String itemId = null, id = null, itemDescription = null, createdBy = null;
                        int listSize = qualityList.size();
                        String cvsValues = "ID" + ","+ "Item ID" + ","+ "Item Description" + ","+ "Created By\n";

                        for(int i=0; i<listSize;i++)
                        {
                            AllQualityStandardList items = qualityList.get(i);
                            id = items.getQuality_sl_no();
                            itemId = items.getItem_id();
                            itemDescription = items.getProject_id();
                            createdBy = items.getCreated_by();

                            cvsValues = cvsValues +  id + ","+ itemId + ","+ itemDescription + ","+ createdBy + "\n";
                        }

                        CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "AllQualityStandard-data.csv", cvsValues);
                    }

                    else

                    {
                        Environment.getExternalStorageState();

                        String itemId = null, id = null, itemDescription = null, createdBy = null;
                        int listSize = qualityList.size();
                        String cvsValues = "ID" + ","+ "Item ID" + ","+ "Item Description" + ","+ "Created By\n";

                        for(int i=0; i<listSize;i++)
                        {
                            AllQualityStandardList items = qualityList.get(i);
                            id = items.getQuality_sl_no();
                            itemId = items.getItem_id();
                            itemDescription = items.getProject_id();
                            createdBy = items.getCreated_by();

                            cvsValues = cvsValues +  id + ","+ itemId + ","+ itemDescription + ","+ createdBy + "\n";
                        }

                        CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "AllQualityStandard-data.csv", cvsValues);

                    }

                }
                break;
            }
        }



    public void prepareItems()
    {

        pDialog = new ProgressDialog(AllQualityStandards.this);
        pDialog.setMessage("Getting server data");
        pDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
//                            dataObject = response.getJSONObject(0);
                            Log.d("RESPONSE JSON", response.toString());
                            dataArray = response.getJSONArray("data");

                            for (int i = 0; i < dataArray.length(); i++) {
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
                            pDialog.dismiss();

                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                        }
//                        setData(response,false);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);

        if (pDialog != null)
            pDialog.dismiss();

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

        String item_list_url = pm.getString("SERVER_URL") + "/getItems?projectId='"+currentProjectNo+"'";

        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_content);
            Crouton.cancelAllCroutons();
            Crouton.makeText(AllQualityStandards.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(AllQualityStandards.this);
            pDialog.setMessage("Getting cache data");
            pDialog.show();

            Cache cache = AppController.getInstance().getRequestQueue().getCache();
            Cache.Entry entry = cache.get(item_list_url);
            if (entry != null) {
                //Cache data available.
                try {
                    String data = new String(entry.data, "UTF-8");
                    Log.d("CACHE DATA", data);
                    JSONObject jsonObject = new JSONObject(data);
                    try {
                        dataArray = jsonObject.getJSONArray("data");

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

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                catch(UnsupportedEncodingException | JSONException e){
                    e.printStackTrace();
                }
            }

            else
            {
                Toast.makeText(AllQualityStandards.this, "Offline Data Not available for Quality Standard", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }
        else
        {
            pDialog = new ProgressDialog(AllQualityStandards.this);
            pDialog.setMessage("Getting server data");
            pDialog.show();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, item_list_url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
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

                                if(dataArray==null)
                                {
                                    Toast.makeText(AllQualityStandards.this, "No Items Found.", Toast.LENGTH_LONG).show();
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(AllQualityStandards.this,
                                            android.R.layout.simple_dropdown_item_1line,new String[] {"No Items"});
                                    spinner_item.setAdapter(adapter);
                                    pDialog.dismiss();
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
//                        setData(response,false);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(),
                            error.getMessage(), Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                }
            });
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjectRequest);

            if (pDialog != null)
                pDialog.dismiss();

        }

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
                else if(spinner_item.getSelectedItem().toString().equals("No Items"))
                {
                    Toast.makeText(AllQualityStandards.this, "No items present in this project", Toast.LENGTH_SHORT).show();
                }

                else {
                    itemId = spinner_item.getSelectedItem().toString();
                    itemDesc = text_item_desc.getText().toString();

                    saveData();

                }
            }
        });


    }


    public void saveData()
    {
        pDialog = new ProgressDialog(AllQualityStandards.this);
        pDialog.setMessage("Getting server data");
        pDialog.show();

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

        String url = AllQualityStandards.this.pm.getString("SERVER_URL") + "/postQualityStandard";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("msg").equals("success"))
                            {
                                String successMsg = "Quality Control Created. ID - "+ response.getString("data");
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
        if (!isInternetPresent)
        {
            // Internet connection is not present

            Communicator communicator = new Communicator();
            Log.d("object", object.toString());

            Boolean createQualityStandardPending = pm.getBoolean("createQualityStandardPending");

            if(createQualityStandardPending)
            {
                Toast.makeText(AllQualityStandards.this, "Already a Quality Standard creation is in progress. Please try after sometime.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(AllQualityStandards.this, "Internet not currently available. Quality Standard will automatically get created on internet connection.", Toast.LENGTH_SHORT).show();

                pm.putString("objectQualityStandardLine", object.toString());
                pm.putString("urlQualityStandardLine", url);
                pm.putString("toastMessageQualityStandard", "Quality Standard Created");
                pm.putBoolean("createQualityStandardPending", true);
            }


            if(pDialog!=null)
                pDialog.dismiss();

            Intent intent = new Intent(AllQualityStandards.this, AllQualityStandards.class);
            startActivity(intent);
        }
        else
        {
            requestQueue.add(jor);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AllQualityStandards.this, QualityControlMain.class);
        startActivity(intent);
    }
}