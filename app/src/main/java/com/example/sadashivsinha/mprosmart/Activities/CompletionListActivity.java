package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.example.sadashivsinha.mprosmart.Adapters.MyAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.MomList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.AppController;
import com.example.sadashivsinha.mprosmart.Utils.Communicator;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class CompletionListActivity extends NewActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    Button date_one, date_two,date_three, date_four, date_five, date_six, date_seven, date_eight, date_nine, date_ten, date_eleven,
            date_twelve, date_two_one, date_two_two, date_two_three, date_three_one, date_three_two,date_three_three,
            date_three_four, date_three_five, date_three_six;

    TextView project_id, project_name, date, created_by;

    String selectedBtn;

    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    String url;
    PreferenceManager pm;
    ProgressDialog pDialog;

    JSONObject dataObject;
    JSONArray dataArray;

    Button[] dateButtons;
    String[] allDateValues;

    int count=0;

    Button btn_submit;

    String currentProject, SHARED_PREF_NAME;

    String certificateOfOccupancy,warranteeCertificate,operationAndMaintainance,trainingAndMaintainance,spareParts,
            drawing,warrenteeAgents,keysreturned,claimsAndChangeOrderList,testandBalance,punchList,recordDrawings,
            submittalPayRequest, changeOrdersAndChangeSetteled, approvalOfTest, notifyMaintenance, copiesOfMaintenance,
            copiesOfWarrentee, forwardSpareParts,verifyAllChangesAndClaims, processFinalPayment, updatedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completion_list);

        pm = new PreferenceManager(CompletionListActivity.this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        btn_submit = (Button) findViewById(R.id.btn_submit);

        currentProject = pm.getString("projectId");

        SHARED_PREF_NAME = "completionListIsNull"+ currentProject;
        pm.putBoolean(SHARED_PREF_NAME, true);

        url = pm.getString("SERVER_URL") + "/getComplitionList?projectId=\""+currentProject+"\"";
        Log.d("URL ", url);


        date_one = (Button) findViewById(R.id.date_one);
        date_two = (Button) findViewById(R.id.date_two);
        date_three = (Button) findViewById(R.id.date_three);
        date_four = (Button) findViewById(R.id.date_four);
        date_five = (Button) findViewById(R.id.date_five);
        date_six = (Button) findViewById(R.id.date_six);
        date_seven = (Button) findViewById(R.id.date_seven);
        date_eight = (Button) findViewById(R.id.date_eight);
        date_nine = (Button) findViewById(R.id.date_nine);
        date_ten = (Button) findViewById(R.id.date_ten);
        date_eleven = (Button) findViewById(R.id.date_eleven);
        date_twelve = (Button) findViewById(R.id.date_twelve);
        date_two_one = (Button) findViewById(R.id.date_two_one);
        date_two_two = (Button) findViewById(R.id.date_two_two);
        date_two_three = (Button) findViewById(R.id.date_two_three);
        date_three_one = (Button) findViewById(R.id.date_three_one);
        date_three_two = (Button) findViewById(R.id.date_three_two);
        date_three_three = (Button) findViewById(R.id.date_three_three);
        date_three_four = (Button) findViewById(R.id.date_three_four);
        date_three_five = (Button) findViewById(R.id.date_three_five);
        date_three_six = (Button) findViewById(R.id.date_three_six);


        date_one.setOnClickListener(this);
        date_two.setOnClickListener(this);
        date_three.setOnClickListener(this);
        date_four.setOnClickListener(this);
        date_five.setOnClickListener(this);
        date_six.setOnClickListener(this);
        date_seven.setOnClickListener(this);
        date_eight.setOnClickListener(this);
        date_nine.setOnClickListener(this);
        date_ten.setOnClickListener(this);
        date_eleven.setOnClickListener(this);
        date_twelve.setOnClickListener(this);
        date_two_one.setOnClickListener(this);
        date_two_two.setOnClickListener(this);
        date_two_three.setOnClickListener(this);
        date_three_one.setOnClickListener(this);
        date_three_two.setOnClickListener(this);
        date_three_three.setOnClickListener(this);
        date_three_four.setOnClickListener(this);
        date_three_five.setOnClickListener(this);
        date_three_six.setOnClickListener(this);

        dateButtons = new Button[]{date_one, date_two,date_three, date_four, date_five, date_six, date_seven, date_eight, date_nine, date_ten, date_eleven,
                date_twelve, date_two_one, date_two_two, date_two_three, date_three_one, date_three_two,date_three_three,
                date_three_four, date_three_five, date_three_six};


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });

        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(CompletionListActivity.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(CompletionListActivity.this);
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
                            certificateOfOccupancy = dataObject.getString("certificateOfOccupancy");
                            warranteeCertificate = dataObject.getString("warranteeCertificate");
                            operationAndMaintainance = dataObject.getString("operationAndMaintainance");
                            trainingAndMaintainance = dataObject.getString("trainingAndMaintainance");
                            spareParts = dataObject.getString("spareParts");
                            drawing = dataObject.getString("drawing");
                            warrenteeAgents = dataObject.getString("warrenteeAgents");
                            keysreturned = dataObject.getString("keysreturned");
                            claimsAndChangeOrderList = dataObject.getString("claimsAndChangeOrderList");
                            testandBalance = dataObject.getString("testandBalance");
                            punchList = dataObject.getString("punchList");
                            submittalPayRequest = dataObject.getString("submittalPayRequest");
                            recordDrawings = dataObject.getString("recordDrawings");
                            changeOrdersAndChangeSetteled = dataObject.getString("changeOrdersAndChangeSetteled");
                            approvalOfTest = dataObject.getString("approvalOfTest");
                            notifyMaintenance = dataObject.getString("notifyMaintenance");
                            copiesOfMaintenance = dataObject.getString("copiesOfMaintenance");
                            copiesOfWarrentee = dataObject.getString("copiesOfWarrentee");
                            forwardSpareParts = dataObject.getString("forwardSpareParts");
                            verifyAllChangesAndClaims = dataObject.getString("verifyAllChangesAndClaims");
                            processFinalPayment = dataObject.getString("processFinalPayment");
                            updatedDate = dataObject.getString("updatedDate");

                            allDateValues = new String[] {certificateOfOccupancy,warranteeCertificate,operationAndMaintainance,trainingAndMaintainance,spareParts,
                                    drawing,warrenteeAgents,keysreturned,claimsAndChangeOrderList,testandBalance,punchList,recordDrawings,
                                    submittalPayRequest, changeOrdersAndChangeSetteled, approvalOfTest, notifyMaintenance, copiesOfMaintenance,
                                    copiesOfWarrentee, forwardSpareParts,verifyAllChangesAndClaims, processFinalPayment};

                            correctDateFormat(allDateValues);

                            pDialog.dismiss();
                        }
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
                Toast.makeText(CompletionListActivity.this, "Offline Data Not available for Completion List", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }

        else
        {
            // Cache data not exist.
            callJsonArrayRequest();
        }

        project_id = (TextView) findViewById(R.id.project_no);
        project_name = (TextView) findViewById(R.id.project_name);
        created_by = (TextView) findViewById(R.id.created_by);

        project_id.setText(pm.getString("projectId"));
        project_name.setText(pm.getString("projectName"));
        created_by.setText(pm.getString("userId"));

        date = (TextView) findViewById(R.id.date);
        created_by = (TextView) findViewById(R.id.created_by);


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

    }


    public void correctDateFormat(String[] dates)
    {
        Date tradeDate = null;
        for(int i=0; i<allDateValues.length; i++)
        {
            if(!dates[i].equals("0000-00-00"))
            {
                try {
                    tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(dates[i]);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                dateButtons[i].setText(new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate));
            }
        }
    }

    public void correctDateFormatForServer(Button[] dateButtons)
    {
        if( dateButtons!=null && dateButtons.length>0)
        {
            Date tradeDate = null;
            for(int i=0; i<dateButtons.length; i++)
            {
                if(!Objects.equals(dateButtons[i].getText().toString(), "") && !Objects.equals(dateButtons[i].getText().toString(), "Click to select date"))
                {
                    try {
                        tradeDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(dateButtons[i].getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    dateButtons[i].setText(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(tradeDate));
                }

            }
        }

    }

    public void saveData()
    {
        final ProgressDialog progressDialog = new ProgressDialog(CompletionListActivity.this);
        progressDialog.setMessage("Saving Data");
        progressDialog.setCancelable(false);
        progressDialog.show();

        correctDateFormatForServer(dateButtons);

        JSONObject object = new JSONObject();
        int requestMethod;

        try {
            int i =0;

            if(dateButtons[i].getText().toString().equals("Click to select date"))
                dateButtons[i].setText("");

            object.put("certificateOfOccupancy",dateButtons[i++].getText().toString());
            if(dateButtons[i].getText().toString().equals("Click to select date"))
                dateButtons[i].setText("");

            object.put("warranteeCertificate",dateButtons[i++].getText().toString());

            if(dateButtons[i].getText().toString().equals("Click to select date"))
                dateButtons[i].setText("");
            object.put("operationAndMaintainance",dateButtons[i++].getText().toString());

            if(dateButtons[i].getText().toString().equals("Click to select date"))
                dateButtons[i].setText("");
            object.put("trainingAndMaintainance",dateButtons[i++].getText().toString());

            if(dateButtons[i].getText().toString().equals("Click to select date"))
                dateButtons[i].setText("");
            object.put("spareParts",dateButtons[i++].getText().toString());

            if(dateButtons[i].getText().toString().equals("Click to select date"))
                dateButtons[i].setText("");
            object.put("drawing",dateButtons[i++].getText().toString());

            if(dateButtons[i].getText().toString().equals("Click to select date"))
                dateButtons[i].setText("");
            object.put("warrenteeAgents",dateButtons[i++].getText().toString());

            if(dateButtons[i].getText().toString().equals("Click to select date"))
                dateButtons[i].setText("");
            object.put("keysreturned", dateButtons[i++].getText().toString());

            if(dateButtons[i].getText().toString().equals("Click to select date"))
                dateButtons[i].setText("");
            object.put("claimsAndChangeOrderList",dateButtons[i++].getText().toString());

            if(dateButtons[i].getText().toString().equals("Click to select date"))
                dateButtons[i].setText("");
            object.put("testandBalance",dateButtons[i++].getText().toString());

            if(dateButtons[i].getText().toString().equals("Click to select date"))
                dateButtons[i].setText("");
            object.put("punchList",dateButtons[i++].getText().toString());

            if(dateButtons[i].getText().toString().equals("Click to select date"))
                dateButtons[i].setText("");
            object.put("recordDrawings",dateButtons[i++].getText().toString());

            if(dateButtons[i].getText().toString().equals("Click to select date"))
                dateButtons[i].setText("");
            object.put("submittalPayRequest",dateButtons[i++].getText().toString());

            if(dateButtons[i].getText().toString().equals("Click to select date"))
                dateButtons[i].setText("");
            object.put("changeOrdersAndChangeSetteled",dateButtons[i++].getText().toString());

            if(dateButtons[i].getText().toString().equals("Click to select date"))
                dateButtons[i].setText("");
            object.put("approvalOfTest",dateButtons[i++].getText().toString());

            if(dateButtons[i].getText().toString().equals("Click to select date"))
                dateButtons[i].setText("");
            object.put("notifyMaintenance",dateButtons[i++].getText().toString());

            if(dateButtons[i].getText().toString().equals("Click to select date"))
                dateButtons[i].setText("");
            object.put("copiesOfMaintenance", dateButtons[i++].getText().toString());

            if(dateButtons[i].getText().toString().equals("Click to select date"))
                dateButtons[i].setText("");
            object.put("copiesOfWarrentee",dateButtons[i++].getText().toString());

            if(dateButtons[i].getText().toString().equals("Click to select date"))
                dateButtons[i].setText("");
            object.put("forwardSpareParts",dateButtons[i++].getText().toString());

            if(dateButtons[i].getText().toString().equals("Click to select date"))
                dateButtons[i].setText("");
            object.put("verifyAllChangesAndClaims",dateButtons[i++].getText().toString());

            if(dateButtons[i].getText().toString().equals("Click to select date"))
                dateButtons[i].setText("");
            object.put("processFinalPayment",dateButtons[i++].getText().toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(CompletionListActivity.this);

        Log.d("PrefName", SHARED_PREF_NAME);

        if(pm.getBoolean(SHARED_PREF_NAME))
        {
            requestMethod = Request.Method.POST;
            Log.d("METHOD", "POST");

            url = CompletionListActivity.this.pm.getString("SERVER_URL") + "/postComplitionList";
            try {
                object.put("projectId",currentProject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else
        {
            requestMethod = Request.Method.PUT;
            Log.d("METHOD", "PUT");

            url = CompletionListActivity.this.pm.getString("SERVER_URL") + "/putComplitionList?projectId=\"" + currentProject + "\"" ;
        }

        Log.d("URL :", url);

        Log.d("JSON :", object.toString());

        JsonObjectRequest jor = new JsonObjectRequest(requestMethod, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("RES: ", response.toString());
                            if(response.getString("msg").equals("success"))
                            {
                                Toast.makeText(CompletionListActivity.this, "Completion List Dates Saved" , Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(CompletionListActivity.this, CompletionListActivity.class);
                                startActivity(intent);
                            }
                            else
                            {
                                Toast.makeText(CompletionListActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(CompletionListActivity.this, CompletionListActivity.class);
                                startActivity(intent);
                            }

                            progressDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }
                        //response success message display
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
        if(progressDialog!=null)
            progressDialog.dismiss();

    }


    private void callJsonArrayRequest() {
        // TODO Auto-generated method stub

        pDialog = new ProgressDialog(CompletionListActivity.this);
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
                            if(response.getString("msg").equals("success"))
                            {
                                pm.putBoolean(SHARED_PREF_NAME, false);
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);

                                    certificateOfOccupancy = dataObject.getString("certificateOfOccupancy");
                                    warranteeCertificate = dataObject.getString("warranteeCertificate");
                                    operationAndMaintainance = dataObject.getString("operationAndMaintainance");
                                    trainingAndMaintainance = dataObject.getString("trainingAndMaintainance");
                                    spareParts = dataObject.getString("spareParts");
                                    drawing = dataObject.getString("drawing");
                                    warrenteeAgents = dataObject.getString("warrenteeAgents");
                                    keysreturned = dataObject.getString("keysreturned");
                                    claimsAndChangeOrderList = dataObject.getString("claimsAndChangeOrderList");
                                    testandBalance = dataObject.getString("testandBalance");
                                    punchList = dataObject.getString("punchList");
                                    submittalPayRequest = dataObject.getString("submittalPayRequest");
                                    recordDrawings = dataObject.getString("recordDrawings");
                                    changeOrdersAndChangeSetteled = dataObject.getString("changeOrdersAndChangeSetteled");
                                    approvalOfTest = dataObject.getString("approvalOfTest");
                                    notifyMaintenance = dataObject.getString("notifyMaintenance");
                                    copiesOfMaintenance = dataObject.getString("copiesOfMaintenance");
                                    copiesOfWarrentee = dataObject.getString("copiesOfWarrentee");
                                    forwardSpareParts = dataObject.getString("forwardSpareParts");
                                    verifyAllChangesAndClaims = dataObject.getString("verifyAllChangesAndClaims");
                                    processFinalPayment = dataObject.getString("processFinalPayment");
                                    updatedDate = dataObject.getString("updatedDate");


                                    allDateValues = new String[] {certificateOfOccupancy,warranteeCertificate,operationAndMaintainance,trainingAndMaintainance,spareParts,
                                            drawing,warrenteeAgents,keysreturned,claimsAndChangeOrderList,testandBalance,punchList,recordDrawings,
                                            submittalPayRequest, changeOrdersAndChangeSetteled, approvalOfTest, notifyMaintenance, copiesOfMaintenance,
                                            copiesOfWarrentee, forwardSpareParts,verifyAllChangesAndClaims, processFinalPayment};

                                    correctDateFormat(allDateValues);

                                    pDialog.dismiss();
                                }
                            }
                            else
                                pm.putBoolean(SHARED_PREF_NAME, true);
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
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.date_one:
            {
                selectedBtn = "1";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CompletionListActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
            break;
            case R.id.date_two:
            {
                selectedBtn = "2";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CompletionListActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
            break;
            case R.id.date_three:
            {
                selectedBtn = "3";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CompletionListActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
            break;

            case R.id.date_four:
            {
                selectedBtn = "4";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CompletionListActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
            break;
            case R.id.date_five:
            {
                selectedBtn = "5";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CompletionListActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
            break;
            case R.id.date_six:
            {
                selectedBtn = "6";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CompletionListActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
            break;

            case R.id.date_seven:
            {
                selectedBtn = "7";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CompletionListActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
            break;
            case R.id.date_eight:
            {
                selectedBtn = "8";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CompletionListActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
            break;
            case R.id.date_nine:
            {
                selectedBtn = "9";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CompletionListActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
            break;

            case R.id.date_ten:
            {
                selectedBtn = "10";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CompletionListActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
            break;
            case R.id.date_eleven:
            {
                selectedBtn = "11";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CompletionListActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
            break;
            case R.id.date_twelve:
            {
                selectedBtn = "12";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CompletionListActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
            break;

            case R.id.date_two_one:
            {
                selectedBtn = "13";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CompletionListActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
            break;
            case R.id.date_two_two:
            {
                selectedBtn = "14";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CompletionListActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
            break;
            case R.id.date_two_three:
            {
                selectedBtn = "15";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CompletionListActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
            break;

            case R.id.date_three_one:
            {
                selectedBtn = "16";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CompletionListActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
            break;
            case R.id.date_three_two:
            {
                selectedBtn = "17";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CompletionListActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
            break;
            case R.id.date_three_three:
            {
                selectedBtn = "18";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CompletionListActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
            break;

            case R.id.date_three_four:
            {
                selectedBtn = "19";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CompletionListActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
            break;
            case R.id.date_three_five:
            {
                selectedBtn = "20";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CompletionListActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
            break;
            case R.id.date_three_six:
            {
                selectedBtn = "21";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CompletionListActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
            break;
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        String[] MONTHS = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        String stringDate = dayOfMonth+"-"+(MONTHS[monthOfYear])+"-"+year;


        switch (selectedBtn) {

            case "1":
            {
                date_one.setText(stringDate);
            }
            break;

            case "2":
            {
                date_two.setText(stringDate);
            }
            break;

            case "3":
            {
                date_three.setText(stringDate);
            }
            break;

            case "4":
            {
                date_four.setText(stringDate);
            }
            break;

            case "5":
            {
                date_five.setText(stringDate);
            }
            break;

            case "6":
            {
                date_six.setText(stringDate);
            }
            break;

            case "7":
            {
                date_seven.setText(stringDate);
            }
            break;

            case "8":
            {
                date_eight.setText(stringDate);
            }
            break;

            case "9":
            {
                date_nine.setText(stringDate);
            }
            break;

            case "10":
            {
                date_ten.setText(stringDate);
            }
            break;

            case "11":
            {
                date_eleven.setText(stringDate);
            }
            break;

            case "12":
            {
                date_twelve.setText(stringDate);
            }
            break;

            case "13":
            {
                date_two_one.setText(stringDate);
            }
            break;

            case "14":
            {
                date_two_two.setText(stringDate);
            }
            break;

            case "15":
            {
                date_two_three.setText(stringDate);
            }
            break;

            case "16":
            {
                date_three_one.setText(stringDate);
            }
            break;

            case "17":
            {
                date_three_two.setText(stringDate);
            }
            break;

            case "18":
            {
                date_three_three.setText(stringDate);
            }
            break;

            case "19":
            {
                date_three_four.setText(stringDate);
            }
            break;

            case "20":
            {
                date_three_five.setText(stringDate);
            }
            break;

            case "21":
            {
                date_three_six.setText(stringDate);
            }
            break;

        }

    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CompletionListActivity.this, QualityControlMain.class);
        startActivity(intent);
    }
}
