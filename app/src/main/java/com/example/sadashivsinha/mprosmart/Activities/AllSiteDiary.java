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
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
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
import com.example.sadashivsinha.mprosmart.Adapters.AllSiteDiaryAdapter;
import com.example.sadashivsinha.mprosmart.Adapters.MyAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.AllBoqList;
import com.example.sadashivsinha.mprosmart.ModelLists.AllSiteDiaryList;
import com.example.sadashivsinha.mprosmart.ModelLists.BoqList;
import com.example.sadashivsinha.mprosmart.ModelLists.SiteList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.AppController;
import com.example.sadashivsinha.mprosmart.Utils.Communicator;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.example.sadashivsinha.mprosmart.Utils.CsvCreateUtility;
import com.github.clans.fab.FloatingActionButton;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class AllSiteDiary extends NewActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private List<AllSiteDiaryList> siteList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AllSiteDiaryAdapter siteDiaryAdapter;
    AllSiteDiaryList items;
    JSONArray dataArray;
    JSONObject dataObject;
    ConnectionDetector cd;
    public static final String TAG = AllSiteDiary.class.getSimpleName();
    Boolean isInternetPresent = false;
    private ProgressDialog pDialog;
    String currentProjectNo, currentUserId;
    View dialogView;
    AlertDialog show;
    TextView text_date;
    String id,date,createdBy;
    String[] datesSiteDiary;
    String url, searchText;
    PreferenceManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_site_diary);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getIntent().hasExtra("search")) {
            if (getIntent().getStringExtra("search").equals("yes")) {

                searchText = getIntent().getStringExtra("searchText");

                getSupportActionBar().setTitle("Site Diary Search Results : " + searchText);
            }
        }

        pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentUserId = pm.getString("userId");

        url = pm.getString("SERVER_URL") + "/getSiteDiary?projectId='"+currentProjectNo+"'";

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        siteDiaryAdapter = new AllSiteDiaryAdapter(siteList);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(siteDiaryAdapter);

        // check for Internet status
        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(AllSiteDiary.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(AllSiteDiary.this);
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
                        datesSiteDiary = new String[dataArray.length()];

                        for(int i=0; i<dataArray.length();i++)
                        {
                            dataObject = dataArray.getJSONObject(i);

                            id = dataObject.getString("id");
                            date = dataObject.getString("date");
                            createdBy = dataObject.getString("createdBy");

                            Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
                            date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                            if (getIntent().hasExtra("search"))
                            {
                                if (getIntent().getStringExtra("search").equals("yes")) {

                                    if (id.toLowerCase().contains(searchText.toLowerCase()) || date.toLowerCase().contains(searchText.toLowerCase())) {

                                        items = new AllSiteDiaryList(String.valueOf(i+1) , date, id, currentProjectNo, createdBy);
                                        siteList.add(items);

                                        datesSiteDiary[i] = date;

                                        siteDiaryAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                            else
                            {

                                items = new AllSiteDiaryList(String.valueOf(i+1) , date, id, currentProjectNo, createdBy);
                                siteList.add(items);

                                datesSiteDiary[i] = date;

                                siteDiaryAdapter.notifyDataSetChanged();
                            }
                            pDialog.dismiss();
                        }

                        Boolean createSiteDiaryPending = pm.getBoolean("createSiteDiaryPending");

                        if (createSiteDiaryPending) {

                            String jsonObjectVal = pm.getString("objectSiteDiary");
                            Log.d("JSON QIR PENDING :", jsonObjectVal);

                            JSONObject jsonObjectPending = new JSONObject(jsonObjectVal);
                            Log.d("JSONObj QIR PENDING :", jsonObjectPending.toString());

                            date = dataObject.getString("date");
                            createdBy = dataObject.getString("createdBy");

                            Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
                            date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                            items = new AllSiteDiaryList(String.valueOf(dataArray.length()+1) , date, id, currentProjectNo, createdBy);
                            siteList.add(items);

                            datesSiteDiary[dataArray.length()-1] = date;

                            siteDiaryAdapter.notifyDataSetChanged();
                            pDialog.dismiss();
                        }

                    } catch (JSONException | ParseException e) {
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
                Toast.makeText(AllSiteDiary.this, "Offline Data Not available for this SiteDiary", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }

        else
        {
            // Cache data not exist.
            callJsonArrayRequest();
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View
        mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size
        mAdapter = new MyAdapter(TITLES,ICONS,NAME,EMAIL,PROFILE);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,header view name, header view email,
        // and header view profile picture
        mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView
        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager
        mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager
        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);        // Drawer object Assigned to the view
        mDrawerToggle = new ActionBarDrawerToggle(this,Drawer,toolbar,R.string.openDrawer,R.string.closeDrawer){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
                // open I am not going to put anything here)
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
            }



        }; // Drawer Toggle Object Made
        Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State
        FloatingActionButton fab_add, fab_search,exportBtn;

        fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        fab_search = (FloatingActionButton) findViewById(R.id.fab_search);
        exportBtn = (FloatingActionButton) findViewById(R.id.exportBtn);

        fab_add.setLabelText("Add new Site Diary");

        fab_add.setOnClickListener(this);
        fab_search.setOnClickListener(this);
        exportBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.exportBtn:
            {
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP) {
                    if (ContextCompat.checkSelfPermission(AllSiteDiary.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(AllSiteDiary.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                    }
                    Environment.getExternalStorageState();

                    String date = null, id = null, currentProjectNo = null, createdBy = null;
                    int listSize = siteList.size();
                    String cvsValues = "Date" + ","+ "ID" + ","+ "Project No" + ","+ "Created By\n";

                    for(int i=0; i<listSize;i++)
                    {
                        AllSiteDiaryList items = siteList.get(i);
                        date = items.getText_site_date();
                        id = items.getText_site_id();
                        currentProjectNo = items.getProject_id();
                        createdBy = items.getCreated_by();

                        cvsValues = cvsValues +  date + ","+ id + ","+ currentProjectNo + ","+ createdBy + "\n";
                    }

                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "SiteDiary-data.csv", cvsValues);
                }

                else

                {
                    Environment.getExternalStorageState();

                    String date = null, id = null, currentProjectNo = null, createdBy = null;
                    int listSize = siteList.size();
                    String cvsValues = "Date" + ","+ "ID" + ","+ "Project No" + ","+ "Created By\n";

                    for(int i=0; i<listSize;i++)
                    {
                        AllSiteDiaryList items = siteList.get(i);
                        date = items.getText_site_date();
                        id = items.getText_site_id();
                        currentProjectNo = items.getProject_id();
                        createdBy = items.getCreated_by();

                        cvsValues = cvsValues +  date + ","+ id + ","+ currentProjectNo + ","+ createdBy + "\n";
                    }
                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "SiteDiary-data.csv", cvsValues);

                }

            }
            break;

            case R.id.fab_add:
            {
                final AlertDialog.Builder alert = new AlertDialog.Builder(AllSiteDiary.this,android.R.style.Theme_Translucent_NoTitleBar);
                // Set an EditText view to get user input

                dialogView = LayoutInflater.from(AllSiteDiary.this).inflate(R.layout.dialog_new_site_diary, null);
                alert.setView(dialogView);

                show = alert.show();

                text_date = (TextView) dialogView.findViewById(R.id.text_date);

                text_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Calendar now = Calendar.getInstance();
                        DatePickerDialog dpd = DatePickerDialog.newInstance(
                                AllSiteDiary.this,
                                now.get(Calendar.YEAR),
                                now.get(Calendar.MONTH),
                                now.get(Calendar.DAY_OF_MONTH)
                        );
                        dpd.setMaxDate(now);
                        dpd.show(getFragmentManager(), "Datepickerdialog");

                    }
                });

                Button createBtn = (Button) dialogView.findViewById(R.id.createBtn);

                createBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(text_date.getText().toString().isEmpty())
                        {
                            text_date.setError("Select Date");
                        }
                        else
                        {
                            Boolean dateAlreadyDone = false;

                            if(datesSiteDiary!=null)
                            {
                                for(int i=0; i<datesSiteDiary.length; i++)
                                {
                                    if(text_date.getText().toString().equals(datesSiteDiary[i]))
                                        dateAlreadyDone = true;
                                }
                            }

                            Log.d("ALL DATES ", Arrays.toString(datesSiteDiary));
                            if(dateAlreadyDone)
                            {
                                Toast.makeText(AllSiteDiary.this, "Site Diary for this date already exist", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                pDialog = new ProgressDialog(AllSiteDiary.this);
                                pDialog.setMessage("Sending Data ...");
                                pDialog.setIndeterminate(false);
                                pDialog.setCancelable(true);
                                pDialog.show();

                                final String selecteddate = text_date.getText().toString();
                                saveSiteDiary(selecteddate);
                            }
                        }
                    }
                });
            }
            break;
            case R.id.fab_search:
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Search Site Diary by Date or ID !");
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
                            Intent intent = new Intent(AllSiteDiary.this, AllSiteDiary.class);
                            intent.putExtra("search", "yes");
                            intent.putExtra("searchText", input.getText().toString());

                            Log.d("SEARCH TEXT", input.getText().toString());

                            startActivity(intent);
                        }
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(AllSiteDiary.this, "Search cancelled .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
            }
            break;
        }
    }

    private void callJsonArrayRequest() {
        // TODO Auto-generated method stub

        pDialog = new ProgressDialog(AllSiteDiary.this);
        pDialog.setMessage("Getting server data");
        pDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {
//                            dataObject = response.getJSONObject(0);
                            dataArray = response.getJSONArray("data");
                            datesSiteDiary = new String[dataArray.length()];

                            for(int i=0; i<dataArray.length();i++)
                            {
                                dataObject = dataArray.getJSONObject(i);

                                id = dataObject.getString("id");
                                date = dataObject.getString("date");
                                createdBy = dataObject.getString("createdBy");

                                Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
                                date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                if (getIntent().hasExtra("search"))
                                {
                                    if (getIntent().getStringExtra("search").equals("yes")) {

                                        if (id.toLowerCase().contains(searchText.toLowerCase()) || date.toLowerCase().contains(searchText.toLowerCase())) {

                                            items = new AllSiteDiaryList(String.valueOf(i+1) , date, id, currentProjectNo, createdBy);
                                            siteList.add(items);

                                            datesSiteDiary[i] = date;

                                            siteDiaryAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                                else
                                {

                                    items = new AllSiteDiaryList(String.valueOf(i+1) , date, id, currentProjectNo, createdBy);
                                    siteList.add(items);

                                    datesSiteDiary[i] = date;

                                    siteDiaryAdapter.notifyDataSetChanged();
                                }

                                pDialog.dismiss();
                            }
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
        if(pDialog!=null)
            pDialog.dismiss();
    }

    public void saveSiteDiary(String date)
    {
        JSONObject object = new JSONObject();

        try {
            object.put("projectId",currentProjectNo);
            object.put("createdBy", currentUserId);

            Date tradeDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(date);
            date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(tradeDate);

            object.put("date", date);


            Log.d("REQUEST SENT OF JSON :" , object.toString());

        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(AllSiteDiary.this);

        String url = AllSiteDiary.this.pm.getString("SERVER_URL") + "/postSiteDiary";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if(response.getString("msg").equals("success"))
                            {
                                Toast.makeText(AllSiteDiary.this, "Site Diary Created. ID - "+ response.getString("data"), Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                                Intent intent = new Intent(AllSiteDiary.this, AllSiteDiary.class);
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
        if (!isInternetPresent)
        {
            pDialog.dismiss();
            // Internet connection is not present

            Communicator communicator = new Communicator();
            Log.d("object", object.toString());

            Boolean createSiteDiaryPending = pm.getBoolean("createSiteDiaryPending");

            if(createSiteDiaryPending)
            {
                Toast.makeText(AllSiteDiary.this, "Already an Site Diary creation is in progress. Please try after sometime.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(AllSiteDiary.this, "Internet not currently available. Site Diary will automatically get created on internet connection.", Toast.LENGTH_SHORT).show();

                pm.putString("objectSiteDiary", object.toString());
                pm.putString("urlSiteDiary", url);
                pm.putString("toastMessageSiteDiary", "Site Diary Created");
                pm.putBoolean("createSiteDiaryPending", true);

                pDialog.dismiss();
                Intent intent = new Intent(AllSiteDiary.this, AllSiteDiary.class);
                startActivity(intent);

            }
        }
        else
        {
            requestQueue.add(jor);
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        String[] MONTHS = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};

        String date;

        if(dayOfMonth<10)
        {
            String newDayOfMonth = "0"+dayOfMonth;
            date = newDayOfMonth + "-" + (MONTHS[monthOfYear]) + "-" + year;

        }
        else
        {
            date = dayOfMonth + "-" + (MONTHS[monthOfYear]) + "-" + year;
        }

        text_date.setText(date);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AllSiteDiary.this, SiteProjectDelivery.class);
        startActivity(intent);
    }
}
