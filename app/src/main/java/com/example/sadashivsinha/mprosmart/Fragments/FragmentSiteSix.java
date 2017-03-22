package com.example.sadashivsinha.mprosmart.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
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
import com.example.sadashivsinha.mprosmart.Activities.SiteDiaryActivity;
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
 * Created by saDashiv sinha on 15-Mar-16.
 */
public class FragmentSiteSix extends Fragment {


    private List<SiteList> siteList = new ArrayList<>();
    private RecyclerView recycler_view;
    private SiteAdapter siteAdapter;
    private int count=0;
    EditText text_notes;
    String currentUser, currentDate, currentSiteDiary, currentSiteDate;
    ProgressDialog pDialog, progressDialog;
    SiteList items;
    JSONArray dataArray;
    JSONObject dataObject;
    String id, description, date, createdBy;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    String url;
    public static final String TAG = SiteDiaryActivity.class.getSimpleName();
    PreferenceManager pm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_site_five, container, false);

//        TextView text_title;
        Button saveBtn;
//        text_title = (TextView) view.findViewById(R.id.text_title);
        text_notes = (EditText) view.findViewById(R.id.text_notes);

        pm = new PreferenceManager(getContext());

        saveBtn = (Button) view.findViewById(R.id.saveBtn);
        PreferenceManager pm = new PreferenceManager(view.getContext());
        currentUser = pm.getString("userId");
        currentSiteDiary = pm.getString("currentSiteDiary");

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        currentDate = sdf.format(c.getTime());


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;

                if(text_notes.getText().toString().isEmpty())
                {
                    text_notes.setError("Field cannot be empty");
                }

                else
                {
                    saveItems(view.getContext());
                    text_notes.setText("");
                    recycler_view.setVisibility(View.VISIBLE);
                }
            }
        });

        recycler_view = (RecyclerView) view.findViewById(R.id.recycler_view);

        siteAdapter = new SiteAdapter(siteList);
        recycler_view.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recycler_view.setHasFixedSize(true);
        recycler_view.setAdapter(siteAdapter);

        cd = new ConnectionDetector(view.getContext());
        isInternetPresent = cd.isConnectingToInternet();

        url = pm.getString("SERVER_URL") + "/getVariations?siteDairyId='"+currentSiteDiary+"'";

        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) view.findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(getActivity(), R.string.no_internet_error, Style.ALERT, main_layout).show();

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
                        for (int i = 0; i < dataArray.length(); i++) {
                            dataObject = dataArray.getJSONObject(i);
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

                        Boolean createSiteVariationPending = pm.getBoolean("createSiteVariationPending");

                        if(createSiteVariationPending)
                        {

                            String jsonObjectVal = pm.getString("objectSiteVariation");
                            Log.d("JSON MOMLINE PENDING :", jsonObjectVal);

                            JSONObject jsonObjectPending = new JSONObject(jsonObjectVal);
                            Log.d("JSONObj MOMIT PENDING :", jsonObjectPending.toString());

                            description = dataObject.getString("description");
                            date = dataObject.getString("date");
                            createdBy = dataObject.getString("createdBy");

                            Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
                            date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                            items = new SiteList(description, createdBy, date, String.valueOf(dataArray.length()+1));
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
                Toast.makeText(getActivity(), "Offline Data Not available for Project Delays", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }

        else
        {
            // Cache data not exist.
            prepareItems(getContext());
        }

//        text_title.setText("Visitors on site");
        text_notes.setHint("Add notes for variations.");
        text_notes.clearFocus();
        return view;

    }

    public void saveItems(final Context context)
    {
        JSONObject object = new JSONObject();

        try {
            object.put("description",text_notes.getText().toString());
            object.put("siteDairyId", currentSiteDiary);
            object.put("createdBy", currentUser);

            Date tradeDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(currentDate);
            currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(tradeDate);

            object.put("date", currentDate);


            Log.d("REQUEST SENT OF JSON :" , object.toString());

        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        String url = pm.getString("SERVER_URL") + "/postVariations";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if(response.getString("msg").equals("success"))
                            {
                                Toast.makeText(context, "Variations added " , Toast.LENGTH_SHORT).show();

                                class MyTask extends AsyncTask<Void, Void, Void>
                                {
                                    @Override
                                    protected Void doInBackground(Void... params)
                                    {
                                        prepareItems(context);
                                        return null;
                                    }

                                }
                                new MyTask().execute();

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
            // Internet connection is not present

            Communicator communicator = new Communicator();
            Log.d("object", object.toString());

            Boolean createSiteVariationPending = pm.getBoolean("createSiteVariationPending");

            if(createSiteVariationPending)
            {
                Toast.makeText(getActivity(), "Already a Variation creation is in progress. Please try after sometime.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(getActivity(), "Internet not currently available. Variation will automatically get created on internet connection.", Toast.LENGTH_SHORT).show();

                pm.putString("objectSiteVariation", object.toString());
                pm.putString("urlSiteVariation", url);
                pm.putString("toastMessageSiteVariation", "Site Diary - Variation Created");
                pm.putBoolean("createSiteVariationPending", true);
            }

            prepareItems(context);
        }
        else
        {
            requestQueue.add(jor);
        }
    }



    public void prepareItems(final Context context)
    {
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
                Toast.makeText(getContext(),
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
