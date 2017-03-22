package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
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

import java.util.ArrayList;

public class ChartRejection extends AppCompatActivity {

//    float rejectPercentage, acceptPercentage , aRejectPercent, bRejectPercent, cRejectPercent, dRejectPercent, eRejectPercent, fRejectPercent,
//            aAcceptPercent, bAcceptPercent, cAcceptPercent, dAcceptPercent, eAcceptPercent, fAcceptPercent;
    String poId;
    JSONObject dataObject;
    JSONArray dataArray;
    String[] poIdArray, purchaseReceiptsIdArray;
    String po_pr_url, pr_items_url, currentProjectNo, purchaseReceiptId, item_id_name_url, item_id, item_name,
            poQuantity, itemId, acceptedQuantity, rejectedQuantity;
    PreferenceManager pm;
    ConnectionDetector cd;
    public static final String TAG = PurchaseOrders.class.getSimpleName();
    Boolean isInternetPresent = false;
    BarChart chart;
    Float acceptPercentage, rejectPercentage, aAcceptPercent, aRejectPercent;
    ProgressDialog pDialog;
    ArrayList itemIdArrayList, itemNameArrayList, poQuantityList, acceptedQuantityList, rejectedQuantityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_rejection);

        pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");

        chart  = (BarChart) findViewById(R.id.chart);

        itemIdArrayList = new ArrayList();
        itemNameArrayList = new ArrayList();
        poQuantityList = new ArrayList();
        acceptedQuantityList = new ArrayList();
        rejectedQuantityList = new ArrayList();

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        po_pr_url = pm.getString("SERVER_URL") + "/getPurchaseReceipts?projectId=\""+ currentProjectNo +"\"";
//
//        // check for Internet status
//        if (!isInternetPresent) {
//            // Internet connection is not present
//            // Ask user to connect to Internet
//            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_layout);
//            Crouton.cancelAllCroutons();
//            Crouton.makeText(ChartRejection.this, R.string.no_internet_error, Style.ALERT, main_layout).show();
//
//            pDialog = new ProgressDialog(ChartRejection.this);
//            pDialog.setMessage("Getting cache data");
//            pDialog.show();
//
//            Cache cache = AppController.getInstance().getRequestQueue().getCache();
//            Cache.Entry entry = cache.get(po_url);
//            if (entry != null) {
//                //Cache data available.
//                try {
//                    String data = new String(entry.data, "UTF-8");
//                    Log.d("CACHE DATA", data);
//                    JSONObject jsonObject = new JSONObject(data);
//                    try {
//                        dataArray = jsonObject.getJSONArray("data");
//
//                        itemIdArray = new String[dataArray.length()];
//                        poItemIdArray = new String[dataArray.length()];
//
//                        for (int i = 0; i < dataArray.length(); i++) {
//                            dataObject = dataArray.getJSONObject(i);
//                            purchaseLineItemsId =  dataObject.getString("purchaseLineItemsId");
//                            itemName =  dataObject.getString("itemName");
//
//                            poItemIdArray[i] = purchaseLineItemsId;
//                            itemIdArray[i] = itemName;
//                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
////                    Toast.makeText(getApplicationContext(), "Loading from cache.", Toast.LENGTH_SHORT).show();
//                } catch (UnsupportedEncodingException | JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            else
//            {
//                Toast.makeText(ChartRejection.this, "Offline Data Not available for these PO Items", Toast.LENGTH_SHORT).show();
//            }
//
//
//            entry = cache.get(url);
//            if (entry != null) {
//                //Cache data available.
//                try {
//                    String data = new String(entry.data, "UTF-8");
//                    Log.d("CACHE DATA", data);
//                    JSONObject jsonObject = new JSONObject(data);
//                    try {
//                        dataArray = jsonObject.getJSONArray("data");
//                        for (int i = 0; i < dataArray.length(); i++) {
//                            dataObject = dataArray.getJSONObject(i);
//                            purchaseReceiptItemsId =  dataObject.getString("purchaseReceiptItemsId");
//                            quantity =  dataObject.getString("quantityReceived");
//                            poQuantity =  dataObject.getString("poQuantity");
//                            unitCost =  dataObject.getString("unitCost");
//                            acceptedQuantityString = dataObject.getString("acceptedQuantity");
//                            rejectedQuantityString = dataObject.getString("rejectedQuantity");
//                            itemId = dataObject.getString("itemId");
//
//                            for(int j=0; j<itemIdArray.length; j++)
//                            {
//                                if(itemId.equals(poItemIdArray[j]))
//                                    itemId = itemIdArray[j];
//                            }
//
//                            LineItems = new PurchaseReceiptCreateNewList(itemId, quantity, poQuantity,
//                                    unitCost, acceptedQuantityString, rejectedQuantityString);
//                            purchaseList.add(LineItems);
//
//                            qualityAdapter.notifyDataSetChanged();
//                            pDialog.dismiss();
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
////                    Toast.makeText(getApplicationContext(), "Loading from cache.", Toast.LENGTH_SHORT).show();
//                } catch (UnsupportedEncodingException | JSONException e) {
//                    e.printStackTrace();
//                }
//                if (pDialog != null)
//                    pDialog.dismiss();
//            }
//
//            else
//            {
//                Toast.makeText(ChartRejection.this, "Offline Data Not available for these Items", Toast.LENGTH_SHORT).show();
//                pDialog.dismiss();
//            }
//        }

//        else
//        {
//            // Cache data not exist.
////            getAllPoPrId();
//        }

        pDialog = new ProgressDialog(ChartRejection.this);
        pDialog.setMessage("Checking Login ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
        class MyTask extends AsyncTask<Void, Void, String[]> {

            @Override
            protected String[] doInBackground(Void... params) {
                getAllPoPrId();
                return poIdArray;
            }

//            @Override
//            protected void onPostExecute(String[] poIdArray) {
//                for(int i=0; i<poIdArray.length; i++)
//                {
//                    getItemsIdNameFromPo(poIdArray[i]);
//                }
//            }
        }
        new MyTask().execute();





//
//        aRejectPercent = calculateRejectPercent(50,50,25);
//        bRejectPercent = calculateRejectPercent(100,50,20);
//        cRejectPercent = calculateRejectPercent(150,100,20);
//        dRejectPercent = calculateRejectPercent(75,25,10);
//        eRejectPercent = calculateRejectPercent(10,8,2);
//        fRejectPercent = calculateRejectPercent(100,20,10);
//
//        aAcceptPercent = calculateAcceptPercent(50,50,25);
//        bAcceptPercent = calculateAcceptPercent(100,50,30);
//        cAcceptPercent = calculateAcceptPercent(150,100,80);
//        dAcceptPercent = calculateAcceptPercent(75,25,15);
//        eAcceptPercent = calculateAcceptPercent(10,8,6);
//        fAcceptPercent = calculateAcceptPercent(100,20,10);



    }



//    private float calculateRejectPercent(float quantityOrdered, float quantityReceived, float quantityRejected)
//    {
//
//        //perform calculation
//
//        rejectPercentage = (quantityRejected/quantityReceived)*100;
//
//        return rejectPercentage;
//    }
//
//    private float calculateAcceptPercent(float quantityOrdered, float quantityReceived, float quantityAccepted)
//    {
//
//        //perform calculation
//
//        acceptPercentage = (quantityAccepted/quantityReceived)*100;
//
//        return acceptPercentage;
//    }

    public void getAllPoPrId()
    {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, po_pr_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {
//                            dataObject = response.getJSONObject(0);
                            dataArray = response.getJSONArray("data");
                            poIdArray = new String[dataArray.length()];
                            purchaseReceiptsIdArray = new String[dataArray.length()];

                            for(int i=0; i<dataArray.length();i++)
                            {
                                dataObject = dataArray.getJSONObject(i);

                                poId = dataObject.getString("purchaseOrderId");
                                purchaseReceiptId = dataObject.getString("purchaseReceiptId");

                                poIdArray[i] = poId;
                                purchaseReceiptsIdArray[i] = purchaseReceiptId;

                            }
                            for(int i=0; i<poIdArray.length; i++)
                            {
                                getItemsIdNameFromPo(poIdArray[i]);
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
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public void getItemsIdNameFromPo(String currentPoNo)
    {
        item_id_name_url = pm.getString("SERVER_URL") + "/getPurchaseLineItems?purchaseOrderId=\"" + currentPoNo + "\"";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, item_id_name_url, null,
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
                                item_id =  dataObject.getString("purchaseLineItemsId");
                                item_name =  dataObject.getString("itemName");

                                itemNameArrayList.add(item_name);
                                itemIdArrayList.add(item_id);

                            }

                            class MyTask extends AsyncTask<Void, Void, Void> {

                                @Override
                                protected Void doInBackground(Void... params) {
                                    for(int i=0; i<purchaseReceiptsIdArray.length;i++)
                                        getAcceptRejectFromServer(purchaseReceiptsIdArray[i]);
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Void params) {
                                    pDialog.dismiss();
                                }
                            }
                            new MyTask().execute();


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
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }


    public void getAcceptRejectFromServer(String currentReceiptNo)
    {
        pr_items_url = pm.getString("SERVER_URL") + "/getPurchaseReceiptItems?purchaseReceiptId=\""+currentReceiptNo+"\"";


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, pr_items_url, null,
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
//                                purchaseReceiptItemsId =  dataObject.getString("purchaseReceiptItemsId");
//                                quantity =  dataObject.getString("quantityReceived");
//                                unitCost =  dataObject.getString("unitCost");

                                itemId = dataObject.getString("itemId");

                                poQuantity =  dataObject.getString("poQuantity");
                                acceptedQuantity = dataObject.getString("acceptedQuantity");
                                rejectedQuantity = dataObject.getString("rejectedQuantity");

                                for(int j=0; j<itemIdArrayList.size(); j++)
                                {
                                    if(itemId.equals(itemIdArrayList.get(j)))
                                        itemId = String.valueOf(itemNameArrayList.get(j));
                                }

                                poQuantityList.add(poQuantity);
                                acceptedQuantityList.add(acceptedQuantity);
                                rejectedQuantityList.add(rejectedQuantity);

                            }
                            prepareGraphs();
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
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public void prepareGraphs() {

        BarData data = new BarData(getXAxisValues(), getDataSet());
        chart.setData(data);
        chart.setDescription("Rejection Chart");
        chart.animateXY(2000, 2000);
        chart.invalidate();
    }

        private ArrayList<BarDataSet> getDataSet() {
        ArrayList<BarDataSet> dataSets = null;
            ArrayList<BarEntry> valueSet1 = new ArrayList<>();
            ArrayList<BarEntry> valueSet2 = new ArrayList<>();

            for(int i=0; i<poQuantityList.size();i++)
            {
                aAcceptPercent = calculateAcceptPercent(Float.parseFloat(String.valueOf(poQuantityList.get(i))), Float.parseFloat(String.valueOf(acceptedQuantityList.get(i))));
                BarEntry v1e1 = new BarEntry(aAcceptPercent, i); // Jan
                valueSet1.add(v1e1);

                aRejectPercent = calculateRejectPercent(Float.parseFloat(poQuantity), Float.parseFloat(rejectedQuantity));
                BarEntry v1e2 = new BarEntry(aRejectPercent, i); // Jan
                valueSet2.add(v1e2);
            }

            for(int i=0; i<poQuantityList.size();i++)
            {
                aRejectPercent = calculateRejectPercent(Float.parseFloat(String.valueOf(poQuantityList.get(i))), Float.parseFloat(String.valueOf(rejectedQuantityList.get(i))));
                BarEntry v1e1 = new BarEntry(aRejectPercent, i); // Jan
                valueSet2.add(v1e1);
            }

        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Accepted");
        barDataSet1.setColor(Color.rgb(113,186,81));
        BarDataSet barDataSet2 = new BarDataSet(valueSet2, "Rejected");
        barDataSet2.setColor(Color.rgb(238,84,58));


        //to set multi-color graphs
//        barDataSet2.setColors(ColorTemplate.COLORFUL_COLORS);

        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);
        dataSets.add(barDataSet2);
        return dataSets;
    }

        private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();

        for(int i=0; i<poQuantityList.size();i++)
            {
                xAxis.add(String.valueOf(itemNameArrayList.get(i)));
            }

        return xAxis;
        }

    private float calculateRejectPercent( float quantityReceived, float quantityRejected)
    {

        //perform calculation

        rejectPercentage = (quantityRejected/quantityReceived)*100;

        return rejectPercentage;
    }

    private float calculateAcceptPercent(float quantityReceived, float quantityAccepted)
    {

        //perform calculation

        acceptPercentage = (quantityAccepted/quantityReceived)*100;

        return acceptPercentage;
    }

}