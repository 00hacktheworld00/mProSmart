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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.Adapters.SiteAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.SiteList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_site_five, container, false);

//        TextView text_title;
        Button saveBtn;
//        text_title = (TextView) view.findViewById(R.id.text_title);
        text_notes = (EditText) view.findViewById(R.id.text_notes);

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

                    progressDialog = new ProgressDialog(view.getContext(),R.style.MyTheme);
                    progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
                    progressDialog.setCancelable(false);
                    progressDialog.show();

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

        pDialog = new ProgressDialog(view.getContext());
        pDialog.setMessage("Getting Data ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        final Context mContext = view.getContext();

        class MyTask extends AsyncTask<Void, Void, Void>
        {

            @Override
            protected Void doInBackground(Void... params)
            {
                prepareItems(mContext, pDialog);
                return null;
            }

        }

        new MyTask().execute();

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

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        String url = getResources().getString(R.string.server_url) + "/postVariations";

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
                                        prepareItems(context, progressDialog);
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
        requestQueue.add(jor);
    }



    public void prepareItems(final Context context, final ProgressDialog pDialog)
    {
        siteList.clear();

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        String url = getResources().getString(R.string.server_url) + "/getVariations?siteDairyId='"+currentSiteDiary+"'";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
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

                                    pDialog.dismiss();
                                }
                            }
                            pDialog.dismiss();
                        }catch(JSONException e){e.printStackTrace();} catch (ParseException e) {
                            e.printStackTrace();
                        }
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
    }
}
