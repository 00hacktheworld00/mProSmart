package com.example.sadashivsinha.mprosmart.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
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
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
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
import com.example.sadashivsinha.mprosmart.Adapters.AllChangeOrdersAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.AllBoqList;
import com.example.sadashivsinha.mprosmart.ModelLists.AllChangeOrdersList;
import com.example.sadashivsinha.mprosmart.ModelLists.MomList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.AppController;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.example.sadashivsinha.mprosmart.Utils.CsvCreateUtility;
import com.github.clans.fab.FloatingActionButton;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class AllChangeOrders extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private List<AllChangeOrdersList> list = new ArrayList<>();
    private RecyclerView recyclerView;
    private AllChangeOrdersAdapter ordersAdapter;
    String currentProjectNo, currentProjectName, currentDate, currentUser, orderName;
    View dialogView;
    AlertDialog show;
    EditText order_name;
    TextView due_date;
    ProgressDialog pDialog;

    PreferenceManager pm;

    Boolean isInternetPresent = false;
    ConnectionDetector cd;

    AllChangeOrdersList items;
    String url;
    JSONArray dataArray;
    JSONObject dataObject;

    String changeOrdersId, dueDate, createdDate, searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_change_orders);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getIntent().hasExtra("search")) {
            if (getIntent().getStringExtra("search").equals("yes")) {

                searchText = getIntent().getStringExtra("searchText");

                getSupportActionBar().setTitle("Change Order Search Results : " + searchText);
            }
        }

        pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentProjectName = pm.getString("projectName");
        currentUser = pm.getString("userId");

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = sdf.format(c.getTime());
        currentDate = strDate;

        ordersAdapter = new AllChangeOrdersAdapter(list);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(ordersAdapter);

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        url = pm.getString("SERVER_URL") + "/getChangeOrders?projectId=\""+currentProjectNo+"\"";

        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_content);
            Crouton.cancelAllCroutons();
            Crouton.makeText(AllChangeOrders.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(AllChangeOrders.this);
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

                        for(int i=0; i<dataArray.length();i++)
                        {
                            dataObject = dataArray.getJSONObject(i);
                            changeOrdersId = dataObject.getString("changeOrdersId");
                            dueDate = dataObject.getString("dueDate");
                            createdDate = dataObject.getString("createdDate");
                            orderName = dataObject.getString("orderName");

                            if (getIntent().hasExtra("search"))
                            {
                                if (getIntent().getStringExtra("search").equals("yes")) {

                                    if (changeOrdersId.toLowerCase().contains(searchText.toLowerCase()) || orderName.toLowerCase().contains(searchText.toLowerCase())) {


                                        items = new AllChangeOrdersList(String.valueOf(i), changeOrdersId, orderName, currentProjectNo,
                                                currentProjectName,  createdDate, dueDate);

                                        list.add(items);
                                        ordersAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                            else
                            {

                                items = new AllChangeOrdersList(String.valueOf(i), changeOrdersId, orderName, currentProjectNo,
                                        currentProjectName,  createdDate, dueDate);

                                list.add(items);
                                ordersAdapter.notifyDataSetChanged();
                            }
                        }

                        pDialog.dismiss();

                    }catch (JSONException e) {
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
                Toast.makeText(AllChangeOrders.this, "Offline Data Not available for Changed Orders", Toast.LENGTH_SHORT).show();
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
//                Intent intent = new Intent(AllChangeOrders.this, BOQCreate.class);
//                startActivity(intent);

                final AlertDialog.Builder alert = new AlertDialog.Builder(AllChangeOrders.this,android.R.style.Theme_Translucent_NoTitleBar);
                // Set an EditText view to get user input

                dialogView = LayoutInflater.from(AllChangeOrders.this).inflate(R.layout.dialog_new_order, null);
                alert.setView(dialogView);

                show = alert.show();

                order_name = (EditText) dialogView.findViewById(R.id.order_name);
                due_date = (TextView) dialogView.findViewById(R.id.due_date);

                due_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar now = Calendar.getInstance();
                        DatePickerDialog dpd = DatePickerDialog.newInstance(
                                AllChangeOrders.this,
                                now.get(Calendar.YEAR),
                                now.get(Calendar.MONTH),
                                now.get(Calendar.DAY_OF_MONTH)
                        );
                        dpd.show(getFragmentManager(), "Datepickerdialog");
                    }
                });

                Button createBtn = (Button) dialogView.findViewById(R.id.createBtn);

                createBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(order_name.getText().toString().isEmpty())
                        {
                            order_name.setError("Field cannot be empty");
                        }
                        else if(due_date.getText().toString().isEmpty())
                        {
                            Toast.makeText(AllChangeOrders.this, "Select Due Date", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            saveChangeOrders();
                        }
                    }
                });
            }
            break;
            case R.id.fab_search:
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Search Change Orders by Title or ID !");
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
                            Intent intent = new Intent(AllChangeOrders.this, AllChangeOrders.class);
                            intent.putExtra("search", "yes");
                            intent.putExtra("searchText", input.getText().toString());

                            Log.d("SEARCH TEXT", input.getText().toString());

                            startActivity(intent);
                        }
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(AllChangeOrders.this, "Search cancelled .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
            }
            break;
            case R.id.exportBtn:
            {
                //csv export
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP) {
                    if (ContextCompat.checkSelfPermission(AllChangeOrders.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(AllChangeOrders.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                    }
                    Environment.getExternalStorageState();

                    String changeOrdersId = null, dueDate = null, createdDate = null, orderName = null;
                    int listSize = list.size();
                    String cvsValues = "Change Order ID" + ","+ "Due Date" + ","+ "Created Date"  + ","+ "Order Name" + "\n";

                    for(int i=0; i<listSize;i++)
                    {
                        AllChangeOrdersList items = list.get(i);
                        changeOrdersId = items.getText_orders_no();
                        dueDate = items.getText_due_date();
                        createdDate = items.getText_date_created();
                        orderName = items.getText_title();
                        cvsValues = cvsValues +  changeOrdersId + ","+ dueDate + ","+ createdDate +","+ orderName + "\n";
                    }

                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "ChangeOrders-data.csv", cvsValues);
                }

                else

                {
                    Environment.getExternalStorageState();


                    String changeOrdersId = null, dueDate = null, createdDate = null, orderName = null;
                    int listSize = list.size();
                    String cvsValues = "Change Order ID" + ","+ "Due Date" + ","+ "Created Date"  + ","+ "Order Name" + "\n";

                    for(int i=0; i<listSize;i++)
                    {
                        AllChangeOrdersList items = list.get(i);
                        changeOrdersId = items.getText_orders_no();
                        dueDate = items.getText_due_date();
                        createdDate = items.getText_date_created();
                        orderName = items.getText_title();
                        cvsValues = cvsValues +  changeOrdersId + ","+ dueDate + ","+ createdDate +","+ orderName + "\n";
                    }

                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "ChangeOrders-data.csv", cvsValues);
                }

            }
            break;
        }
    }


    public void saveChangeOrders()
    {
        JSONObject object = new JSONObject();

        try {
            object.put("projectId",currentProjectNo);
            object.put("createdBy", currentUser);
            object.put("dueDate", due_date.getText().toString());
            object.put("statusId","1");
            object.put("createdDate", currentDate);
            object.put("orderName", order_name.getText().toString());

            Log.d("REQUEST SENT OF JSON :" , object.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(AllChangeOrders.this);

        String url = pm.getString("SERVER_URL") + "/postChangeOrder";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            Log.d("SERVER RESPONSE :", response.toString());

                            if(response.getString("msg").equals("success"))
                            {
                                Toast.makeText(AllChangeOrders.this, "Change Orders Created. ID - "+ response.getString("data"), Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                                Intent intent = new Intent(AllChangeOrders.this, AllChangeOrders.class);
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

    public void prepareItems()
    {
        pDialog = new ProgressDialog(AllChangeOrders.this);
        pDialog.setMessage("Getting server data");
        pDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {
//                            dataObject = response.getJSONObject(0);
                            Log.d("RESPONSE JSON", response.toString());
                            dataArray = response.getJSONArray("data");

                            for(int i=0; i<dataArray.length();i++)
                            {
                                dataObject = dataArray.getJSONObject(i);
                                changeOrdersId = dataObject.getString("changeOrdersId");
                                dueDate = dataObject.getString("dueDate");
                                createdDate = dataObject.getString("createdDate");
                                orderName = dataObject.getString("orderName");
                                if (getIntent().hasExtra("search"))
                                {
                                    if (getIntent().getStringExtra("search").equals("yes")) {

                                        if (changeOrdersId.toLowerCase().contains(searchText.toLowerCase()) || orderName.toLowerCase().contains(searchText.toLowerCase())) {


                                            items = new AllChangeOrdersList(String.valueOf(i), changeOrdersId, orderName, currentProjectNo,
                                                    currentProjectName,  createdDate, dueDate);

                                            list.add(items);
                                            ordersAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                                else
                                {

                                    items = new AllChangeOrdersList(String.valueOf(i), changeOrdersId, orderName, currentProjectNo,
                                            currentProjectName,  createdDate, dueDate);

                                    list.add(items);
                                    ordersAdapter.notifyDataSetChanged();
                                }
                            }

                            pDialog.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        setData(response,false);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("", "Error: " + error.getMessage());
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AllChangeOrders.this, SiteProjectDelivery.class);
        startActivity(intent);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        String[] MONTHS = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        String date = dayOfMonth+"/"+(MONTHS[monthOfYear])+"/"+year;
        due_date.setText(date);
    }
}
