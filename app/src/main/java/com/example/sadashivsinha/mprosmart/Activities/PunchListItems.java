package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.sadashivsinha.mprosmart.Adapters.MyAdapter;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.AppController;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.example.sadashivsinha.mprosmart.font.HelveticaRegular;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class PunchListItems extends NewActivity {

    RelativeLayout main_content;
    private ProgressDialog pDialog;
    JSONArray dataArray;
    JSONObject dataObject;
    String puncListLinesId, location, description, itemType, notes, responsible, priority, status, scheduleToComplete, dateComplted, architectAccepted;
    ConnectionDetector cd;
    public static final String TAG = PunchListItems.class.getSimpleName();
    Boolean isInternetPresent = false;
    String current_line_no, current_punch_list_no, current_punch_list_original_no;

    HelveticaRegular text_line_no, text_location, text_item_desc, text_item_type, text_notes, text_responsible, text_priority, text_status,
            text_schedule_complete, text_date, text_architect;
    String url;
    PreferenceManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_punch_list_items);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Punch List Item");

        pm = new PreferenceManager(PunchListItems.this);
        current_punch_list_original_no = pm.getString("originalLineNo");
        current_line_no = pm.getString("lineNo");
        current_punch_list_no = pm.getString("punchListNo");

        url = pm.getString("SERVER_URL") + "/getPunchListLines?punchListId=\""+current_punch_list_no+"\"";

        text_line_no = (HelveticaRegular) findViewById(R.id.text_line_no);
        text_location = (HelveticaRegular) findViewById(R.id.text_location);
        text_item_desc = (HelveticaRegular) findViewById(R.id.text_item_desc);
        text_item_type = (HelveticaRegular) findViewById(R.id.text_punch_item_type);
        text_notes = (HelveticaRegular) findViewById(R.id.text_notes);
        text_responsible = (HelveticaRegular) findViewById(R.id.text_resp_party);
        text_priority = (HelveticaRegular) findViewById(R.id.text_priority);
        text_status = (HelveticaRegular) findViewById(R.id.text_status);
        text_schedule_complete = (HelveticaRegular) findViewById(R.id.text_schedule_date);
        text_date = (HelveticaRegular) findViewById(R.id.text_date);
        text_architect = (HelveticaRegular) findViewById(R.id.text_architect);


        main_content = (RelativeLayout) findViewById(R.id.main_content);


        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            LinearLayout main_layout = (LinearLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(PunchListItems.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(PunchListItems.this);
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
                            puncListLinesId = dataObject.getString("puncListLinesId");

                            if (puncListLinesId.equals(current_punch_list_original_no)) {
                                location = dataObject.getString("location");
                                description = dataObject.getString("description");
                                itemType = dataObject.getString("itemType");
                                notes = dataObject.getString("notes");
                                responsible = dataObject.getString("responsible");
                                priority = dataObject.getString("priority");
                                status = dataObject.getString("status");
                                scheduleToComplete = dataObject.getString("scheduleToComplete");
                                dateComplted = dataObject.getString("dateComplted");
                                architectAccepted = dataObject.getString("architectAccepted");


                                Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(dateComplted);

                                String formattedDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);
                                text_date.setText(String.valueOf(formattedDate));


                                tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(scheduleToComplete);

                                String formattedDate2 = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);
                                text_schedule_complete.setText(String.valueOf(formattedDate2));


                                text_line_no.setText(current_line_no);
                                text_location.setText(location);
                                text_item_desc.setText(description);
                                text_item_type.setText(itemType);
                                text_notes.setText(notes);
                                text_responsible.setText(responsible);
                                text_priority.setText(priority);
                                text_status.setText(status);
                                text_architect.setText(architectAccepted);

                            }
                        }
//                    Toast.makeText(getApplicationContext(), "Loading from cache.", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (pDialog != null)
                        pDialog.dismiss();
                }
                catch ( JSONException | UnsupportedEncodingException | ParseException e) {
                    e.printStackTrace();
                }
                }

            else
            {
                Toast.makeText(PunchListItems.this, "Offline Data Not available for this Punch List", Toast.LENGTH_SHORT).show();
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
    }


    private void callJsonArrayRequest() {
        // TODO Auto-generated method stub

        pDialog = new ProgressDialog(PunchListItems.this);
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
                            for(int i=0; i<dataArray.length();i++)
                            {
                                dataObject = dataArray.getJSONObject(i);

                                puncListLinesId = dataObject.getString("puncListLinesId");

                                if(puncListLinesId.equals(current_punch_list_original_no)) {
                                    location = dataObject.getString("location");
                                    description = dataObject.getString("description");
                                    itemType = dataObject.getString("itemType");
                                    notes = dataObject.getString("notes");
                                    responsible = dataObject.getString("responsible");
                                    priority = dataObject.getString("priority");
                                    status = dataObject.getString("status");
                                    scheduleToComplete = dataObject.getString("scheduleToComplete");
                                    dateComplted = dataObject.getString("dateComplted");
                                    architectAccepted = dataObject.getString("architectAccepted");


                                    Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(dateComplted);

                                    String formattedDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);
                                    text_date.setText(String.valueOf(formattedDate));


                                    tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(scheduleToComplete);

                                    String formattedDate2 = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);
                                    text_schedule_complete.setText(String.valueOf(formattedDate2));


                                    text_line_no.setText(current_line_no);
                                    text_location.setText(location);
                                    text_item_desc.setText(description);
                                    text_item_type.setText(itemType);
                                    text_notes.setText(notes);
                                    text_responsible.setText(responsible);
                                    text_priority.setText(priority);
                                    text_status.setText(status);
                                    text_architect.setText(architectAccepted);
                                }
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
}
