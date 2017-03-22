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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.Activities.AllSiteDiary;
import com.example.sadashivsinha.mprosmart.Adapters.SiteAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.SiteList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.AppController;
import com.example.sadashivsinha.mprosmart.Utils.Communicator;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;

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

/**
 * Created by saDashiv sinha on 14-Mar-16.
 */
public class FragmentSiteOne extends Fragment {

    private List<SiteList> siteList = new ArrayList<>();
    private RecyclerView recycler_view;
    private SiteAdapter siteAdapter;
    EditText text_notes;
    JSONArray dataArray;
    JSONObject dataObject;
    ConnectionDetector cd;
    PreferenceManager pm;
    Boolean isInternetPresent = false;
    String currentUser, currentDate, currentSiteDiary, currentSiteDate, newDate;
    ProgressDialog pDialog, progressDialog;
    SiteList items;
    String id, description, date, createdBy, url;
    public static final String TAG = AllSiteDiary.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_site_five, container, false);

//        TextView text_title;
        Button saveBtn;
//        text_title = (TextView) view.findViewById(R.id.text_title);
        text_notes = (EditText) view.findViewById(R.id.text_notes);

        saveBtn = (Button) view.findViewById(R.id.saveBtn);
        pm = new PreferenceManager(view.getContext());
        currentUser = pm.getString("userId");
        currentSiteDiary = pm.getString("currentSiteDiary");
        currentSiteDate = pm.getString("currentSiteDate");

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        currentDate = sdf.format(c.getTime());

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(text_notes.getText().toString().isEmpty())
                {
                    text_notes.setError("Field cannot be empty");
                }
                else
                {
                    progressDialog = new ProgressDialog(view.getContext(),R.style.MyTheme);
                    progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
                    progressDialog.setCancelable(true);
                    progressDialog.show();

                    try {
                        saveItems(view.getContext());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    recycler_view.setVisibility(View.VISIBLE);
                    text_notes.setText("");
                }
            }
        });

        recycler_view = (RecyclerView) view.findViewById(R.id.recycler_view);

        siteAdapter = new SiteAdapter(siteList);
        recycler_view.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recycler_view.setHasFixedSize(true);
        recycler_view.setAdapter(siteAdapter);

        cd = new ConnectionDetector(getContext());
        isInternetPresent = cd.isConnectingToInternet();

        url = pm.getString("SERVER_URL") + "/getVisitorsOnSite?siteDairyId='"+currentSiteDiary+"'";

        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) view.findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText((Activity) getContext(), R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(getContext());
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
                            id = dataObject.getString("id");
                            description = dataObject.getString("description");
                            date = dataObject.getString("date");
                            createdBy = dataObject.getString("createdBy");

                            Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
                            date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                            items = new SiteList(description, createdBy, date, String.valueOf(i+1));
                            siteList.add(items);
                            siteAdapter.notifyDataSetChanged();
                            pDialog.dismiss();
                        }

                        Boolean createVisitorsSiteDiaryPending = pm.getBoolean("createVisitorsSiteDiaryPending");

                        if (createVisitorsSiteDiaryPending) {

                            String jsonObjectVal = pm.getString("objectVisitorsSiteDiary");
                            Log.d("JSON QIR PENDING :", jsonObjectVal);

                            JSONObject jsonObjectPending = new JSONObject(jsonObjectVal);
                            Log.d("JSONObj QIR PENDING :", jsonObjectPending.toString());

                            description = dataObject.getString("description");
                            createdBy = dataObject.getString("createdBy");

                            items = new SiteList(description, createdBy, currentSiteDate, String.valueOf(dataArray.length()+1));
                            siteList.add(items);
                            siteAdapter.notifyDataSetChanged();
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
                Toast.makeText(getContext(), "Offline Data Not available for Visitors on Site", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }

        else
        {
            // Cache data not exist.
            callJsonArrayRequest(getContext());
        }

//        text_title.setText("Visitors on site");
        text_notes.setHint("Add notes for visitors on site.");
        text_notes.clearFocus();
        return view;

    }

    public void saveItems(final Context context) throws JSONException {
        JSONObject object = new JSONObject();

        try {
            object.put("description",text_notes.getText().toString());
            object.put("siteDairyId", currentSiteDiary);
            object.put("createdBy", currentUser);

            Date tradeDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(currentDate);
            newDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(tradeDate);

            object.put("date", newDate);


            Log.d("REQUEST SENT OF JSON :" , object.toString());

        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        String url = pm.getString("SERVER_URL") + "/postVisitorsOnSite";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if(response.getString("msg").equals("success"))
                            {
                                Toast.makeText(context, "Visitors added " , Toast.LENGTH_SHORT).show();
                                callJsonArrayRequest(context);

                            }

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
                        Log.e("Volley", "Error");
                        progressDialog.dismiss();
                    }
                }
        );
        if (!isInternetPresent)
        {
            progressDialog.dismiss();
            // Internet connection is not present

            Communicator communicator = new Communicator();
            Log.d("object", object.toString());

            Boolean createVisitorsSiteDiaryPending = pm.getBoolean("createVisitorsSiteDiaryPending");
            if(createVisitorsSiteDiaryPending)
            {
                Toast.makeText(getContext(), "Already an Visitors creation is in progress. Please try after sometime.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(getContext(), "Internet not currently available. Visitors will automatically get created on internet connection.", Toast.LENGTH_SHORT).show();

                pm.putString("objectVisitorsSiteDiary", object.toString());
                pm.putString("urlVisitorsSiteDiary", url);
                pm.putString("toastMessageVisitorsSiteDiary", "Visitors Added on Site Diary");
                pm.putBoolean("createVisitorsSiteDiaryPending", true);


                description = object.getString("description");
                createdBy = object.getString("createdBy");

                items = new SiteList(description, createdBy, currentSiteDate, String.valueOf(dataArray.length()+1));
                siteList.add(items);
                siteAdapter.notifyDataSetChanged();
            }
        }
        else
        {
            requestQueue.add(jor);
        }
    }
    private void callJsonArrayRequest(final Context context) {
        // TODO Auto-generated method stub

        siteList.clear();

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

                                id = dataObject.getString("id");
                                description = dataObject.getString("description");
                                date = dataObject.getString("date");
                                createdBy = dataObject.getString("createdBy");

                                Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
                                date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                items = new SiteList(description, createdBy, date, String.valueOf(i+1));
                                siteList.add(items);
                                siteAdapter.notifyDataSetChanged();

                                if(progressDialog!=null)
                                    progressDialog.dismiss();
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
                Toast.makeText(context,
                        error.getMessage(), Toast.LENGTH_SHORT).show();

                if(progressDialog!=null)
                    progressDialog.dismiss();
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }
}
