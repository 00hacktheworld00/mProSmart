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
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.sadashivsinha.mprosmart.Adapters.AllItemsAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.AllItemsList;
import com.example.sadashivsinha.mprosmart.ModelLists.PurchaseOrdersList;
import com.example.sadashivsinha.mprosmart.ModelLists.WbsList;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class AllItemsActivity extends AppCompatActivity implements View.OnClickListener {

    private List<AllItemsList> itemsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AllItemsAdapter allItemsAdapter;
    String[] uomArray, uomNameArray;
    String currentProjectNo, currentProjectName, currentUser, currentDate;
    View dialogView;
    AlertDialog show;
    private ProgressDialog pDialog;
    JSONArray dataArray;
    JSONObject dataObject;
    String currentUomName;
    String itemId, itemName, itemDescription, uomId, createdDate, createdBy;

    AllItemsList allItems;
    ConnectionDetector cd;
    Boolean isInternetPresent;
    public static final String TAG = AllItemsActivity.class.getSimpleName();

    String url, uom_url;
    PreferenceManager pm;
    String searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_items);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pm = new PreferenceManager(getApplicationContext());
        pm.putString("currentBudget", "approval");
        currentProjectNo = pm.getString("projectId");
        currentProjectName = pm.getString("projectName");
        currentUser = pm.getString("userId");

        if (getIntent().hasExtra("search")) {
            if (getIntent().getStringExtra("search").equals("yes")) {

                searchText = getIntent().getStringExtra("searchText");

                getSupportActionBar().setTitle("Items Search Results : " + searchText);
            }
        }

        url = pm.getString("SERVER_URL") + "/getItems?projectId='"+currentProjectNo+"'";
        uom_url = pm.getString("SERVER_URL") + "/getUom";

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = sdf.format(c.getTime());

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        allItemsAdapter = new AllItemsAdapter(itemsList);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(AllItemsActivity.this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(allItemsAdapter);

        // check for Internet status
        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(AllItemsActivity.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(AllItemsActivity.this);
            pDialog.setMessage("Getting cache data");
            pDialog.show();

            Cache cache = AppController.getInstance().getRequestQueue().getCache();
            Cache.Entry entry = cache.get(uom_url);
            if(entry != null){
                //Cache data available.
                try {
                    String data = new String(entry.data, "UTF-8");
                    Log.d("CACHE DATA", data);
                    JSONObject jsonObject=new JSONObject(data);
                    try
                    {
                        dataArray = jsonObject.getJSONArray("data");
                            uomArray = new String[dataArray.length()+1];
                            uomNameArray = new String[dataArray.length()+1];

                            uomNameArray[0]="Select UOM";
                            uomArray[0]="Select UOM";

                            for(int i=0; i<dataArray.length();i++)
                            {
                                dataObject = dataArray.getJSONObject(i);
                                uomArray[i+1] = dataObject.getString("uomCode");
                                uomNameArray[i+1] = dataObject.getString("uomName");
                            }
                            prepareItems();

                            pDialog.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    Toast.makeText(getApplicationContext(), "Loading from cache.", Toast.LENGTH_SHORT).show();
                }
                catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }
                if(pDialog!=null)
                    pDialog.dismiss();
            }
        }

        else
        {
            // Cache data not exist.
            getAllUom();
        }

        FloatingActionButton fab_add, exportBtn, fab_search;

        fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        exportBtn = (FloatingActionButton) findViewById(R.id.exportBtn);
        fab_search = (FloatingActionButton) findViewById(R.id.fab_search);

        fab_add.setOnClickListener(this);
        exportBtn.setOnClickListener(this);
        fab_search.setOnClickListener(this);
    }

    private void prepareItems() {
        // TODO Auto-generated method stub

        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(AllItemsActivity.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            Cache cache = AppController.getInstance().getRequestQueue().getCache();
            Cache.Entry entry = cache.get(url);
            if(entry != null){
                //Cache data available.
                try {
                    String data = new String(entry.data, "UTF-8");
                    Log.d("CACHE DATA", data);
                    JSONObject jsonObject=new JSONObject(data);
                    try
                    {
                        dataArray = jsonObject.getJSONArray("data");

                        for(int i=0; i<dataArray.length(); i++)
                        {
                            dataObject = dataArray.getJSONObject(i);
                            itemId = dataObject.getString("itemId");
                            itemName = dataObject.getString("itemName");
                            itemDescription = dataObject.getString("itemDescription");
                            uomId = dataObject.getString("uomId");
                            createdDate = dataObject.getString("createdDate");
                            createdBy = dataObject.getString("createdBy");

                            Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(createdDate);
                            createdDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                            for(int j=0;j<uomArray.length;j++)
                            {
                                if(uomId.equals(uomArray[j]))
                                    currentUomName = uomNameArray[j];
                            }

                            if (getIntent().hasExtra("search"))
                            {
                                if (getIntent().getStringExtra("search").equals("yes")) {

                                    if (itemId.toLowerCase().contains(searchText.toLowerCase()) || itemName.toLowerCase().contains(searchText.toLowerCase())) {
                                        allItems = new AllItemsList(String.valueOf(i+1), itemId, itemName, itemDescription, currentUomName);
                                        itemsList.add(allItems);

                                        allItemsAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                            else
                            {
                                allItems = new AllItemsList(String.valueOf(i+1), itemId, itemName, itemDescription, currentUomName);
                                itemsList.add(allItems);

                                allItemsAdapter.notifyDataSetChanged();
                            }
                        }


                        Boolean createItemPending = pm.getBoolean("createItemPending");
                        Log.d("createItemPending  :", createItemPending.toString());

                        if(createItemPending) {
                            //if is in offline mode and data creation is pending, show the data in the list

                            String jsonObjectVal = pm.getString("objectItem");
                            Log.d("JSON Project PENDING :", jsonObjectVal);

                            JSONObject jsonObjectPending = new JSONObject(jsonObjectVal);
                            Log.d("JSONObj Project :", jsonObjectPending.toString());

                            itemName = jsonObjectPending.getString("itemName");
                            itemDescription = jsonObjectPending.getString("itemDescription");
                            uomId = jsonObjectPending.getString("uomId");
                            createdDate = jsonObjectPending.getString("createdDate");
                            createdBy = jsonObjectPending.getString("createdBy");

                            allItems = new AllItemsList(String.valueOf(dataArray.length()+1), getResources().getString(R.string.waiting_to_connect), itemName, itemDescription, currentUomName);
                            itemsList.add(allItems);

                            allItemsAdapter.notifyDataSetChanged();
                        }

                        pDialog.dismiss();

                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                    }
//                    Toast.makeText(getApplicationContext(), "Loading from cache.", Toast.LENGTH_SHORT).show();
                }
                catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }
            }
            if(pDialog!=null)
                pDialog.dismiss();
        }

        else
        {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("response project : ", response.toString());
                            try
                            {
//                            dataObject = response.getJSONObject(0);
                                dataArray = response.getJSONArray("data");
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    itemId = dataObject.getString("itemId");
                                    itemName = dataObject.getString("itemName");
                                    itemDescription = dataObject.getString("itemDescription");
                                    uomId = dataObject.getString("uomId");
                                    createdDate = dataObject.getString("createdDate");
                                    createdBy = dataObject.getString("createdBy");

                                    Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(createdDate);
                                    createdDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                    for(int j=0;j<uomArray.length;j++)
                                    {
                                        if(uomId.equals(uomArray[j]))
                                            currentUomName = uomNameArray[j];
                                    }
                                    if (getIntent().hasExtra("search"))
                                    {
                                        if (getIntent().getStringExtra("search").equals("yes")) {

                                            if (itemId.toLowerCase().contains(searchText.toLowerCase()) || itemName.toLowerCase().contains(searchText.toLowerCase())) {
                                                allItems = new AllItemsList(String.valueOf(i+1), itemId, itemName, itemDescription, currentUomName);
                                                itemsList.add(allItems);

                                                allItemsAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                    else
                                    {
                                        allItems = new AllItemsList(String.valueOf(i+1), itemId, itemName, itemDescription, currentUomName);
                                        itemsList.add(allItems);

                                        allItemsAdapter.notifyDataSetChanged();
                                    }

                                }

                                pDialog.dismiss();

                            }
                            catch (ParseException | JSONException e)
                            {
                                e.printStackTrace();
                                pDialog.dismiss();
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
            if(pDialog!=null)
                pDialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add:
            {
                Intent intent = new Intent(AllItemsActivity.this, AddItemsActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.exportBtn:
            {
                //csv export
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP) {
                    if (ContextCompat.checkSelfPermission(AllItemsActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(AllItemsActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                    }
                    Environment.getExternalStorageState();

                    String itemId = null, itemName = null, itemDescription = null, uomId = null;
                    int listSize = itemsList.size();
                    String cvsValues = "Item ID" + ","+ "Item Name" + ","+ "Item Description" + ","+ "UOM" + "\n";

                    for(int i=0; i<listSize;i++)
                    {
                        AllItemsList items = itemsList.get(i);
                        itemId = items.getItem_id();
                        itemName = items.getItem_name();
                        itemDescription = items.getItem_desc();
                        uomId = items.getUom();

                        cvsValues = cvsValues +  itemId + ","+ itemName + ","+ itemDescription + ","+ uomId + "\n";
                    }

                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "AllItems-data.csv", cvsValues);
                }

                else

                {
                    Environment.getExternalStorageState();

                    String itemId = null, itemName = null, itemDescription = null, uomId = null;
                    int listSize = itemsList.size();
                    String cvsValues = "Item ID" + ","+ "Item Name" + ","+ "Item Description" + ","+ "UOM" + "\n";

                    for(int i=0; i<listSize;i++)
                    {
                        AllItemsList items = itemsList.get(i);
                        itemId = items.getItem_id();
                        itemName = items.getItem_name();
                        itemDescription = items.getItem_desc();
                        uomId = items.getUom();

                        cvsValues = cvsValues +  itemId + ","+ itemName + ","+ itemDescription + ","+ uomId + "\n";
                    }

                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "AllItems-data.csv", cvsValues);

                }

            }
            break;

            case R.id.fab_search :
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Search Items by Item Name or ID !");
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
                            Intent intent = new Intent(AllItemsActivity.this, AllItemsActivity.class);
                            intent.putExtra("search", "yes");
                            intent.putExtra("searchText", input.getText().toString());

                            Log.d("SEARCH TEXT", input.getText().toString());

                            startActivity(intent);
                        }
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(AllItemsActivity.this, "Search cancelled .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
            }
            break;
        }
    }

    public void getAllUom() {
        pDialog = new ProgressDialog(AllItemsActivity.this);
        pDialog.setMessage("Getting server data");
        pDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, uom_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response project : ", response.toString());
                        try {
//                            dataObject = response.getJSONObject(0);
                            dataArray = response.getJSONArray("data");
                            uomArray = new String[dataArray.length() + 1];
                            uomNameArray = new String[dataArray.length() + 1];

                            uomNameArray[0] = "Select UOM";
                            uomArray[0] = "Select UOM";

                            for (int i = 0; i < dataArray.length(); i++) {
                                dataObject = dataArray.getJSONObject(i);
                                uomArray[i + 1] = dataObject.getString("uomCode");
                                uomNameArray[i + 1] = dataObject.getString("uomName");
                            }
                            prepareItems();
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

        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AllItemsActivity.this, ProjectPlanningSchedulingActivity.class);
        startActivity(intent);
    }
}