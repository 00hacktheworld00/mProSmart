package com.example.sadashivsinha.mprosmart.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.Adapters.BoqAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.AllBoqList;
import com.example.sadashivsinha.mprosmart.ModelLists.BoqList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.AppController;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.example.sadashivsinha.mprosmart.Utils.CsvCreateUtility;
import com.github.clans.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class BoqActivity extends AppCompatActivity implements View.OnClickListener {

    TextView boq_no, project_id,unit, uom, created_by, date_created, text_boq_name;

    private List<BoqList> boqList = new ArrayList<>();
    private RecyclerView recyclerView;
    private BoqAdapter boqAdapter;
    String itemText, quantityText, uomText, costText, currencyText, totalCostText;

    String text_id, text_item, text_quantity, text_uom, text_cost, text_currency, text_totalCost;
    String createdBy, createdDate;
    ProgressDialog pDialog, pDialog1;
    String currentProjectNo, currentProjectName, currentBoq;

    JSONArray dataArray;
    JSONObject dataObject;
    Boolean isInternetPresent = false;

    BoqList qualityItem;
    String[] uomArray, uomNameArray, itemIdArray, itemDescArray, itemNameArray;
    String itemName, itemId, itemDesc;
    PreferenceManager pm;
    String uom_url, item_url, boq_url;
    String searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boq);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getIntent().hasExtra("search")) {
            if (getIntent().getStringExtra("search").equals("yes")) {

                searchText = getIntent().getStringExtra("searchText");

                getSupportActionBar().setTitle("BOQ Item Search Results : " + searchText);
            }
        }


        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        pm = new PreferenceManager(getApplicationContext());

        currentProjectNo = pm.getString("projectId");
        currentProjectName = pm.getString("projectName");
        currentBoq = pm.getString("currentBoq");

        uom_url = pm.getString("SERVER_URL") + "/getUom";
        item_url = pm.getString("SERVER_URL") + "/getItems?projectId='"+currentProjectNo+"'";
        boq_url = pm.getString("SERVER_URL") + "/getBoqItems?projectId='"+currentProjectNo+"'";

        boq_no = (TextView) findViewById(R.id.boq_no);
        text_boq_name = (TextView) findViewById(R.id.text_boq_name);
        project_id = (TextView) findViewById(R.id.project_id);
        unit = (TextView) findViewById(R.id.unit);
        uom = (TextView) findViewById(R.id.uom);
        created_by = (TextView) findViewById(R.id.created_by);
        date_created = (TextView) findViewById(R.id.date_created);

        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        recyclerView.setLayoutManager(new LinearLayoutManager(BoqActivity.this));
        recyclerView.setHasFixedSize(true);
        boqAdapter = new BoqAdapter(boqList);
        recyclerView.setAdapter(boqAdapter);

        prepareHeader();

        // check for Internet status
        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            CoordinatorLayout main_content = (CoordinatorLayout) findViewById(R.id.main_content);
            Snackbar snackbar = Snackbar.make(main_content, getResources().getString(R.string.no_internet_error), Snackbar.LENGTH_LONG);
            snackbar.show();
            Crouton.cancelAllCroutons();
            Crouton.makeText(BoqActivity.this, R.string.no_internet_error, Style.ALERT, main_content).show();

            pDialog = new ProgressDialog(BoqActivity.this);
            pDialog.setMessage("Getting cache data");
            pDialog.show();

            Cache cache = AppController.getInstance().getRequestQueue().getCache();
            Cache.Entry entry = cache.get(uom_url);
            if (entry != null) {
                //Cache data available.
                try {
                    String data = new String(entry.data, "UTF-8");
                    Log.d("CACHE DATA", data);
                    JSONObject jsonObject = new JSONObject(data);
                    try {
                        dataArray = jsonObject.getJSONArray("data");
                        uomArray = new String[dataArray.length() + 1];
                        uomNameArray = new String[dataArray.length() + 1];

                        uomArray[0] = "UOM";
                        uomNameArray[0] = "UOM";

                        for (int i = 0; i < dataArray.length(); i++) {
                            dataObject = dataArray.getJSONObject(i);
                            uomArray[i + 1] = dataObject.getString("uomCode");
                            uomNameArray[i + 1] = dataObject.getString("uomName");
                        }

                        prepareItemList();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    Toast.makeText(getApplicationContext(), "Loading from cache.", Toast.LENGTH_SHORT).show();
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }
                if (pDialog != null)
                    pDialog.dismiss();
            }

            else
            {
                Toast.makeText(BoqActivity.this, "Offline Data Not available for this BOQ", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }

        else
        {
            // Cache data not exist.

            pDialog = new ProgressDialog(BoqActivity.this);
            pDialog.setMessage("Getting cache data");
            pDialog.show();

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, uom_url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try{

                                String type = response.getString("type");

                                if(type.equals("ERROR"))
                                {
                                    Toast.makeText(BoqActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                                }

                                if(type.equals("INFO"))
                                {
                                    dataArray = response.getJSONArray("data");
                                    uomArray = new String[dataArray.length() + 1];
                                    uomNameArray = new String[dataArray.length() + 1];

                                    uomArray[0] = "UOM";
                                    uomNameArray[0] = "UOM";

                                    for (int i = 0; i < dataArray.length(); i++) {
                                        dataObject = dataArray.getJSONObject(i);
                                        uomArray[i + 1] = dataObject.getString("uomCode");
                                        uomNameArray[i + 1] = dataObject.getString("uomName");
                                    }
                                    prepareItemList();
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
//
//        pDialog = new ProgressDialog(BoqActivity.this);
//        pDialog.setMessage("Getting Data ...");
//        pDialog.setIndeterminate(false);
//        pDialog.setCancelable(true);
//        pDialog.show();
//
//        class MyTask extends AsyncTask<Void, Void, Void> {
//
//            @Override
//            protected Void doInBackground(Void... params) {
//                prepareHeader();
//                prepareUom();
//                return null;
//            }
//
//        }
//        new MyTask().execute();

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
                Intent intent = new Intent(BoqActivity.this, AddBoqItems.class);
                startActivity(intent);
            }
            break;
            case R.id.fab_search:
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Search BOQ Item by Name or ID !");
                // Set an EditText view to get user input
                final EditText input = new EditText(this);
                input.setMaxLines(1);
                input.setImeOptions(EditorInfo.IME_ACTION_DONE);
                alert.setView(input);
                alert.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        if (input.getText().toString().isEmpty()) {
                            input.setError("Enter Search Field");
                        } else {
                            Intent intent = new Intent(BoqActivity.this, BoqActivity.class);
                            intent.putExtra("search", "yes");
                            intent.putExtra("searchText", input.getText().toString());

                            Log.d("SEARCH TEXT", input.getText().toString());

                            startActivity(intent);
                        }
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(BoqActivity.this, "Search cancelled .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
            }
            break;
            case R.id.exportBtn:
            {
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP) {
                    if (ContextCompat.checkSelfPermission(BoqActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(BoqActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                    }
                    Environment.getExternalStorageState();

                    String text_item = null, text_quantity = null, text_uom = null, itemName = null;
                    int listSize = boqList.size();
                    String cvsValues = "Item" + ","+ "Quantity" + ","+ "UOM" + ","+ "Item Name" + "\n";

                    for(int i=0; i<listSize;i++)
                    {
                        BoqList items = boqList.get(i);
                        text_item = items.getText_item();
                        text_quantity = items.getText_quantity();
                        text_uom = items.getText_uom();
                        itemName = items.getItemName();

                        cvsValues = cvsValues +  text_item + ","+ text_quantity + ","+ text_uom + ","+ itemName + "\n";
                    }

                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "BOQ-data.csv", cvsValues);
                }

                else

                {
                    Environment.getExternalStorageState();

                    String text_item = null, text_quantity = null, text_uom = null, itemName = null;
                    int listSize = boqList.size();
                    String cvsValues = "Item" + ","+ "Quantity" + ","+ "UOM" + ","+ "Item Name" + "\n";


                    for(int i=0; i<listSize;i++)
                    {
                        BoqList items = boqList.get(i);
                        text_item = items.getText_item();
                        text_quantity = items.getText_quantity();
                        text_uom = items.getText_uom();
                        itemName = items.getItemName();

                        cvsValues = cvsValues +  text_item + ","+ text_quantity + ","+ text_uom + ","+ itemName + "\n";
                    }
                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "BOQ-data.csv", cvsValues);

                }

            }
            break;
        }
    }

    public void prepareItemList()
    {
        if (!isInternetPresent) {

            pDialog = new ProgressDialog(BoqActivity.this);
            pDialog.setMessage("Getting cache data");
            pDialog.show();

            Cache cache = AppController.getInstance().getRequestQueue().getCache();
            Cache.Entry entry = cache.get(item_url);
            if (entry != null) {
                //Cache data available.
                try {
                    String data = new String(entry.data, "UTF-8");
                    Log.d("CACHE DATA", data);
                    JSONObject jsonObject = new JSONObject(data);
                    try {
                        dataArray = jsonObject.getJSONArray("data");
                        itemIdArray = new String[dataArray.length()+1];
                        itemDescArray = new String[dataArray.length()+1];
                        itemNameArray = new String[dataArray.length()+1];

                        itemIdArray[0]="Select Item";
                        itemDescArray[0]="Select Item to view description";
                        itemNameArray[0]="Select Item";

                        for(int i=0; i<dataArray.length();i++)
                        {
                            dataObject = dataArray.getJSONObject(i);
                            itemId = dataObject.getString("itemId");
                            itemDesc = dataObject.getString("itemDescription");
                            itemName = dataObject.getString("itemName");

                            itemIdArray[i+1]=itemId;
                            itemDescArray[i+1]=itemDesc;
                            itemNameArray[i+1]=itemName;
                        }

                        prepareBoqItems();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    Toast.makeText(getApplicationContext(), "Loading from cache.", Toast.LENGTH_SHORT).show();
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }
                if (pDialog != null)
                    pDialog.dismiss();
            }
        }

        else
        {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, item_url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try{

                                String type = response.getString("type");

                                if(type.equals("ERROR"))
                                {
                                    Toast.makeText(BoqActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                                }

                                if(type.equals("INFO"))
                                {
                                    dataArray = response.getJSONArray("data");
                                    itemIdArray = new String[dataArray.length()+1];
                                    itemDescArray = new String[dataArray.length()+1];
                                    itemNameArray = new String[dataArray.length()+1];

                                    itemIdArray[0]="Select Item";
                                    itemDescArray[0]="Select Item to view description";
                                    itemNameArray[0]="Select Item";

                                    for(int i=0; i<dataArray.length();i++)
                                    {
                                        dataObject = dataArray.getJSONObject(i);
                                        itemId = dataObject.getString("itemId");
                                        itemDesc = dataObject.getString("itemDescription");
                                        itemName = dataObject.getString("itemName");

                                        itemIdArray[i+1]=itemId;
                                        itemDescArray[i+1]=itemDesc;
                                        itemNameArray[i+1]=itemName;
                                    }

                                    prepareBoqItems();
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

    public void prepareBoqItems()
    {
        if (!isInternetPresent) {

            pDialog = new ProgressDialog(BoqActivity.this);
            pDialog.setMessage("Getting cache data");
            pDialog.show();

            Cache cache = AppController.getInstance().getRequestQueue().getCache();
            Cache.Entry entry = cache.get(boq_url);
            if (entry != null) {
                //Cache data available.
                try {
                    String data = new String(entry.data, "UTF-8");
                    Log.d("CACHE DATA", data);
                    JSONObject jsonObject = new JSONObject(data);
                    try {
                        dataArray = jsonObject.getJSONArray("data");
                        for(int i=0; i<dataArray.length();i++) {
                            dataObject = dataArray.getJSONObject(i);
                            text_id = dataObject.getString("boqId");

                            if (text_id.equals(currentBoq)) {
                                text_item = dataObject.getString("item");
                                text_quantity = dataObject.getString("quantity");
                                text_uom = dataObject.getString("uom");

                                for (int j = 0; j < uomArray.length; j++) {
                                    if (text_uom.equals(uomArray[j])) {
                                        text_uom = uomNameArray[j];
                                        break;
                                    }
                                }

                                for (int j = 0; j < itemIdArray.length; j++) {
                                    if (text_item.equals(itemIdArray[j])) {
                                        itemName = itemNameArray[j];
                                        break;
                                    }
                                }

                                if (getIntent().hasExtra("search"))
                                {
                                    if (getIntent().getStringExtra("search").equals("yes")) {

                                        if (itemName.toLowerCase().contains(searchText.toLowerCase()) || itemName.toLowerCase().contains(searchText.toLowerCase())) {

                                            qualityItem = new BoqList(itemName, text_quantity, text_uom, itemName);
                                            boqList.add(qualityItem);

                                            boqAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                                else
                                {
                                    qualityItem = new BoqList(text_item, text_quantity, text_uom, itemName);
                                    boqList.add(qualityItem);

                                    boqAdapter.notifyDataSetChanged();
                                }


                        Boolean createBOQPendingLine = pm.getBoolean("createBOQPendingLine");

                        if(createBOQPendingLine)
                        {

                            String jsonObjectVal = pm.getString("objectBOQLine");
                            Log.d("JSON BOQLINE PENDING :", jsonObjectVal);

                            JSONObject jsonObjectPending = new JSONObject(jsonObjectVal);
                            Log.d("JSONObj BOQLI PENDING :", jsonObjectPending.toString());

                            text_id = jsonObjectPending.getString("boqId");

                            if (text_id.equals(currentBoq)) {
                                text_item = jsonObjectPending.getString("item");
                                text_quantity = jsonObjectPending.getString("quantity");
                                text_uom = jsonObjectPending.getString("uom");

                                for (int j = 0; j < uomArray.length; j++) {
                                    if (text_uom.equals(uomArray[j])) {
                                        text_uom = uomNameArray[j];
                                        break;
                                    }
                                }

                                for (int j = 0; j < itemIdArray.length; j++) {
                                    if (text_item.equals(itemIdArray[j])) {
                                        itemName = itemNameArray[j];
                                        break;
                                    }
                                }

                                qualityItem = new BoqList(text_item, text_quantity, text_uom, itemName);
                                boqList.add(qualityItem);

                                boqAdapter.notifyDataSetChanged();
                                pDialog.dismiss();
                            }
                        }
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    Toast.makeText(getApplicationContext(), "Loading from cache.", Toast.LENGTH_SHORT).show();
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }
                if (pDialog != null)
                    pDialog.dismiss();
            }
        }

        else {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, boq_url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {

                                String type = response.getString("type");

                                if (type.equals("ERROR")) {
                                    Toast.makeText(BoqActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                                }

                                if (type.equals("INFO")) {
                                    dataArray = response.getJSONArray("data");
                                    for (int i = 0; i < dataArray.length(); i++) {
                                        dataObject = dataArray.getJSONObject(i);
                                        text_id = dataObject.getString("boqId");

                                        if (text_id.equals(currentBoq)) {
                                            text_item = dataObject.getString("item");
                                            text_quantity = dataObject.getString("quantity");
                                            text_uom = dataObject.getString("uom");

                                            for (int j = 0; j < uomArray.length; j++) {
                                                if (text_uom.equals(uomArray[j])) {
                                                    text_uom = uomNameArray[j];
                                                    break;
                                                }
                                            }

                                            for (int j = 0; j < itemIdArray.length; j++) {
                                                if (text_item.equals(itemIdArray[j])) {
                                                    itemName = itemNameArray[j];
                                                    break;
                                                }
                                            }

                                            if (getIntent().hasExtra("search"))
                                            {
                                                if (getIntent().getStringExtra("search").equals("yes")) {

                                                    if (itemName.toLowerCase().contains(searchText.toLowerCase()) || itemName.toLowerCase().contains(searchText.toLowerCase())) {

                                                        qualityItem = new BoqList(text_item, text_quantity, text_uom, itemName);
                                                        boqList.add(qualityItem);

                                                        boqAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }
                                            else
                                            {
                                                qualityItem = new BoqList(text_item, text_quantity, text_uom, itemName);
                                                boqList.add(qualityItem);

                                                boqAdapter.notifyDataSetChanged();
                                            }


                                        }
                                    }
                                }

                                pDialog.dismiss();
                            } catch (JSONException e) {
                                pDialog.dismiss();
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pDialog.dismiss();
                            Log.e("Volley", "Error");

                        }
                    }
            );
            requestQueue.add(jor);
        }

    }

    private void prepareHeader()
    {
        project_id.setText(pm.getString("currentBoqProjectId"));
        boq_no.setText(pm.getString("currentBoq"));
        text_boq_name.setText(pm.getString("currentBoqName"));
        unit.setText(pm.getString("currentBoqUnit"));
        uom.setText(pm.getString("currentBoqUom"));
        created_by.setText(pm.getString("currentBoqCreatedBy"));
        date_created.setText(pm.getString("currentBoqDate"));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(BoqActivity.this, AllBoq.class);
        startActivity(intent);
    }
}