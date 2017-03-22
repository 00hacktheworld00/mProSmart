package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.AppController;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ChartComplete extends AppCompatActivity {

    ConnectionDetector cd;
    public static final String TAG = ChartComplete.class.getSimpleName();
    Boolean isInternetPresent = false;
    PreferenceManager pm;
    String currentProjectNo, url;
    ProgressDialog pDialog;
    JSONArray dataArray;
    JSONObject dataObject;
    String[] wbsIdArray, wbsNameArray, wbsProgressArray;
    String wbsId, wbsName, wbsProgress;
    BarEntry barEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_complete);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("WBS Completion %");

        pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");

        url = pm.getString("SERVER_URL") + "/getWbs?projectId=\"" + currentProjectNo + "\"";

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();


        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(ChartComplete.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(ChartComplete.this);
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

                        wbsIdArray = new String[dataArray.length()];
                        wbsNameArray = new String[dataArray.length()];
                        wbsProgressArray = new String[dataArray.length()];

                        for (int i = 0; i < dataArray.length(); i++) {
                            dataObject = dataArray.getJSONObject(i);
                            wbsId = dataObject.getString("wbsId");
                            wbsName = dataObject.getString("wbsName");
                            wbsProgress = dataObject.getString("progress");
//                            totalBudget = dataObject.getString("totalBudget");
//                            currencyCode = dataObject.getString("currencyCode");

                            wbsIdArray[i] = wbsId;
                            wbsNameArray[i] = wbsName;
                            wbsProgressArray[i] = wbsProgress;
                        }

                        prepareGraph();
                        pDialog.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    Toast.makeText(getApplicationContext(), "Loading from cache.", Toast.LENGTH_SHORT).show();
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }
                if (pDialog != null)
                    pDialog.dismiss();
            } else {
                Toast.makeText(ChartComplete.this, "Offline Data Not available for WBS Completion", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        } else {
            // Cache data not exist.
            getDataFromServer();
        }
    }

    private void getDataFromServer() {
        // TODO Auto-generated method stub

        pDialog = new ProgressDialog(ChartComplete.this);
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

                            wbsIdArray = new String[dataArray.length()];
                            wbsNameArray = new String[dataArray.length()];
                            wbsProgressArray = new String[dataArray.length()];

                            for(int i=0; i<dataArray.length();i++)
                            {
                                dataObject = dataArray.getJSONObject(i);
                                wbsId = dataObject.getString("wbsId");
                                wbsName = dataObject.getString("wbsName");
                                wbsProgress = dataObject.getString("progress");

                                wbsIdArray[i] = wbsId;
                                wbsNameArray[i] = wbsName;
                                wbsProgressArray[i] = wbsProgress;
                            }
                            prepareGraph();

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

    public void prepareGraph()
    {
        BarChart chart = (BarChart) findViewById(R.id.chart);

        chart.setDrawGridBackground(false);
//        chart.setPinchZoom(false);
//        chart.setDoubleTapToZoomEnabled(false);
//        chart.setScaleXEnabled(false);
//        chart.setScaleYEnabled(false);

        chart.setDrawBarShadow(false);
        chart.getAxisLeft().setTextColor(Color.rgb(255,255,255)); // left y-axis
        chart.getXAxis().setTextColor(Color.rgb(255,255,255));
        chart.getLegend().setTextColor(Color.rgb(255,255,255));
        chart.getAxisRight().setTextColor(Color.rgb(255,255,255));


        BarData data = new BarData(getXAxisValues(), getDataSet());
        chart.setData(data);
        chart.setDescription("Completion Chart");
        chart.animateXY(2000, 2000);
        chart.invalidate();
    }

    private ArrayList<BarDataSet> getDataSet() {
        ArrayList<BarDataSet> dataSets = null;

        ArrayList<BarEntry> valueSet = new ArrayList<>();

        for(int i=0; i<wbsIdArray.length; i++)
        {
            barEntry = new BarEntry(Float.parseFloat(wbsProgressArray[i]), i); // Jan
            valueSet.add(barEntry);
        }

        BarDataSet barDataSet = new BarDataSet(valueSet, "Completed");
        barDataSet.setColor(Color.rgb(88,143,193));

        dataSets = new ArrayList<>();
        dataSets.add(barDataSet);
        return dataSets;
    }

    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();

        for(int i=0; i<wbsIdArray.length; i++)
        {
            xAxis.add(wbsNameArray[i]);
        }
        return xAxis;
    }
}