package com.example.sadashivsinha.mprosmart.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.sadashivsinha.mprosmart.Activities.AllPurchaseReceipts;
import com.example.sadashivsinha.mprosmart.Adapters.PurchaseReceiptsNewAdapter;
import com.example.sadashivsinha.mprosmart.Adapters.SiteThreeAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.PurchaseOrdersList;
import com.example.sadashivsinha.mprosmart.ModelLists.SiteTwoList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.AppController;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by saDashiv sinha on 14-Mar-16.
 */
public class FragmentSiteThree extends Fragment {

    private List<SiteTwoList> itemList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PurchaseReceiptsNewAdapter mainAdapter;

    PreferenceManager pm;
    String currentSiteDate, currentProjectNo, url;
    PurchaseOrdersList items;
    private List<com.example.sadashivsinha.mprosmart.ModelLists.PurchaseOrdersList> itemsOne = new ArrayList<>();
    ConnectionDetector cd;
    ProgressDialog pDialog;
    Boolean isInternetPresent = false;
    JSONArray dataArray;
    JSONObject dataObject;
    String purchaseOrderId, purchaseReceiptId,date;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_site_two, container, false);
        pm = new PreferenceManager(view.getContext());

        mainAdapter = new PurchaseReceiptsNewAdapter(itemsOne);
        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mainAdapter);

        cd = new ConnectionDetector(view.getContext());
        isInternetPresent = cd.isConnectingToInternet();

        currentSiteDate = pm.getString("currentSiteDate");
        currentProjectNo = pm.getString("projectId");

        url = pm.getString("SERVER_URL") + "/getPurchaseReceipts?projectId=\""+currentProjectNo+"\"";

//        SiteTwoList items = new SiteTwoList("1","001","41454.","V-001","I-101", "Processing");
//        itemList.add(items);
//
//        items = new SiteTwoList("2","002","41774.","V-002","I-102", "Processing");
//        itemList.add(items);
//
//        items = new SiteTwoList("3","003","41575.","V-003","I-103", "Delivered");
//        itemList.add(items);
//
//        items = new SiteTwoList("4","004","25874.","V-004","I-104", "Processing");
//        itemList.add(items);
//
//        items = new SiteTwoList("5","005","11424.","V-005","I-105", "Processing");
//        itemList.add(items);
//
//        mainAdapter.notifyDataSetChanged();

        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet

            pDialog = new ProgressDialog(view.getContext());
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
                            purchaseOrderId = dataObject.getString("purchaseOrderId");

                            purchaseReceiptId = dataObject.getString("purchaseReceiptId");
                            date = dataObject.getString("date");

                            Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
                            date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                            if(date.equals(currentSiteDate))
                            {
                                items = new PurchaseOrdersList(purchaseReceiptId, purchaseOrderId,
                                        currentProjectNo, date, String.valueOf(i+1),0);
                                itemsOne.add(items);

                                mainAdapter.notifyDataSetChanged();
                            }
                        }
                        pDialog.dismiss();
                    } catch (ParseException | JSONException e) {
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
                Toast.makeText(view.getContext(), "Offline Data Not available for these Material Receipts", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }

        else
        {
            // Cache data not exist.
            callJsonArrayRequest(view.getContext());
        }

        return view;

    }

    private void callJsonArrayRequest(final Context context) {
        // TODO Auto-generated method stub

        pDialog = new ProgressDialog(context);
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
                            for(int i=0; i<dataArray.length();i++) {
                                dataObject = dataArray.getJSONObject(i);
                                purchaseOrderId = dataObject.getString("purchaseOrderId");

                                purchaseReceiptId = dataObject.getString("purchaseReceiptId");
                                date = dataObject.getString("date");

                                Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
                                date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                if(date.equals(currentSiteDate))
                                {
                                    items = new PurchaseOrdersList(purchaseReceiptId, purchaseOrderId,
                                            currentProjectNo, date, String.valueOf(i+1),0);
                                    itemsOne.add(items);

                                    mainAdapter.notifyDataSetChanged();
                                }
                            }
                        } catch (ParseException | JSONException e) {
                            e.printStackTrace();
                        }
//                        setData(response,false);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("", "Error: " + error.getMessage());
                Toast.makeText(context,
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
