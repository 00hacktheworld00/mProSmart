package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.sadashivsinha.mprosmart.Adapters.DailyProgressDetailsAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.DailyProgressDetailsList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.AppController;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.example.sadashivsinha.mprosmart.font.HelveticaRegular;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class DailyProgressDetails extends AppCompatActivity {

    private List<DailyProgressDetailsList> dailyList = new ArrayList<>();
    private RecyclerView recyclerView;
    private DailyProgressDetailsAdapter dailyListAdapter;
    DailyProgressDetailsList items;
    PreferenceManager pm;
    String currentProgressDate, currentProjectNo;
    ProgressDialog pDialog;
    public static final String TAG = DailyProgressDetails.class.getSimpleName();
    String wbs_name, activity, completed, res_worked, percent_completed_today, percent_completed_total;
    JSONObject dataObject;
    JSONArray dataArray;
    HelveticaRegular text_no_progress;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_progress_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Daily Field Progress Report");

        pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentProgressDate = pm.getString("currentProgressDate");

        url = pm.getString("SERVER_URL") + "/getDailyProgressWbs?projectId=\""+ currentProjectNo
                +"\"&date=\"" + currentProgressDate + "\"";

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        HelveticaRegular from_date, to_date, item_no;

        from_date = (HelveticaRegular) findViewById(R.id.from_date);
        to_date = (HelveticaRegular) findViewById(R.id.to_date);

        item_no = (HelveticaRegular) findViewById(R.id.item_no);

        text_no_progress = (HelveticaRegular) findViewById(R.id.text_no_progress);
        text_no_progress.setVisibility(View.GONE);

        dailyListAdapter = new DailyProgressDetailsAdapter(dailyList);

        recyclerView.setLayoutManager(new LinearLayoutManager(DailyProgressDetails.this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(dailyListAdapter);

        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(DailyProgressDetails.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(DailyProgressDetails.this);
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

                            wbs_name = dataObject.getString("wbsName");
                            activity = dataObject.getString("activityName");
                            completed = dataObject.getString("status");
                            res_worked = dataObject.getString("firstName");
                            percent_completed_today = dataObject.getString("progress");
                            percent_completed_total = dataObject.getString("totalProgress");

                            items = new DailyProgressDetailsList(wbs_name, activity, completed, res_worked,
                                    percent_completed_today, percent_completed_total);
                            dailyList.add(items);

                            dailyListAdapter.notifyDataSetChanged();
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
            else
            {
                Toast.makeText(DailyProgressDetails.this, "Offline Daily Progress Report Not available for this Date", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }

        else
        {
            // Cache data not exist.
            callJsonArrayRequest();
        }
    }

    private void callJsonArrayRequest() {
        // TODO Auto-generated method stub

        pDialog = new ProgressDialog(DailyProgressDetails.this);
        pDialog.setMessage("Getting server data");
        pDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {
//                            dataObject = response.getJSONObject(0);
                           if(response!=null)
                           {
                               dataArray = response.getJSONArray("data");
                               for(int i=0; i<dataArray.length();i++)
                               {
                                   dataObject = dataArray.getJSONObject(i);
                                   wbs_name = dataObject.getString("wbsName");
                                   activity = dataObject.getString("activityName");
                                   completed = dataObject.getString("status");
                                   res_worked = dataObject.getString("firstName");
                                   percent_completed_today = dataObject.getString("progress");
                                   percent_completed_total = dataObject.getString("totalProgress");

                                   items = new DailyProgressDetailsList(wbs_name, activity, completed, res_worked,
                                           percent_completed_today, percent_completed_total);
                                   dailyList.add(items);

                                   dailyListAdapter.notifyDataSetChanged();
                               }
                           }

                           else
                           {
                               Toast.makeText(DailyProgressDetails.this, "No Data available", Toast.LENGTH_SHORT).show();
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
        if(pDialog!=null)
            pDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DailyProgressDetails.this, SiteProjectDelivery.class);
        startActivity(intent);
    }
}
