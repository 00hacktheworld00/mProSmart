package com.example.sadashivsinha.mprosmart.Fragments;

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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.Adapters.SiteTwoAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.SiteTwoList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.font.HelveticaRegular;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by saDashiv sinha on 14-Mar-16.
 */
public class FragmentSiteTwo extends Fragment {

    private List<SiteTwoList> itemList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SiteTwoAdapter mainAdapter;
    private Context context;
    private SiteTwoList items;
    private JSONObject dataObject;
    private JSONArray dataArray;
    String[] allResId;
    String wbs,activities, resourceName, totalHours, date, lineId;
    String currentProjectNo, currentSelectedDate;
    HelveticaRegular text_back_msg;
    PreferenceManager pm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_site_two, container, false);

        context = view.getContext();

        pm = new PreferenceManager(context);

        text_back_msg = (HelveticaRegular) view.findViewById(R.id.text_back_msg);
        text_back_msg.setText("LOADING DATA...");

        PreferenceManager pm  =new PreferenceManager(context);
        currentProjectNo = pm.getString("projectId");
        currentSelectedDate = pm.getString("currentSiteDate");

        mainAdapter = new SiteTwoAdapter(itemList);
        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        class MyTask extends AsyncTask<Void, Void, Void>
        {
            @Override protected void onPreExecute()
            {
                recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(mainAdapter);

            }

            @Override
            protected Void doInBackground(Void... params)
            {
                setRetainInstance(true);
                getAllResources();
                return null;
            }

            @Override protected void onPostExecute(Void result)
            {
                mainAdapter.notifyDataSetChanged();
            }

        }

        new MyTask().execute();


        return view;

    }

    private void getAllResources()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        String url = pm.getString("SERVER_URL") + "/getResourceTimesheets?projectId='"+currentProjectNo+"'";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            Log.d("Site Hours Res", response.toString());

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                allResId = new String[dataArray.length()];

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    allResId[i] = dataObject.getString("resourceTimesheetsId");
                                }

                                for(int j=0; j<allResId.length; j++)
                                {
                                    Log.d("Site Hours CurRes", allResId[j]);
                                    prepareItems(allResId[j]);
                                }

                            }

                        }catch(JSONException e){e.printStackTrace();}
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

    private void prepareItems(final String currentResourceId)
    {

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        String url = pm.getString("SERVER_URL") + "/getResourceLineItems?resourceTimesheetsId='"+currentResourceId+"'";

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
                                Log.d("Site Hours Line Res", response.toString());

                                dataArray = response.getJSONArray("data");
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    date = dataObject.getString("date");

                                    Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
                                    date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                    if(date.equals(currentSelectedDate))
                                    {
                                        lineId = dataObject.getString("resourceLineItemsId");
                                        wbs = dataObject.getString("wbs");
                                        activities = dataObject.getString("activities");
                                        resourceName = dataObject.getString("name");
                                        totalHours = dataObject.getString("totalHours");

                                        items = new SiteTwoList(currentResourceId,wbs, activities, resourceName, Float.parseFloat(totalHours));
                                        itemList.add(items);

                                        mainAdapter.notifyDataSetChanged();
                                    }

                                    if(itemList.isEmpty())
                                    {
                                        text_back_msg.setVisibility(View.VISIBLE);
                                    }
                                    else
                                    {
                                        text_back_msg.setVisibility(View.GONE);
                                    }
                                }

                                if(itemList.isEmpty())
                                {
                                    text_back_msg.setText("NO RESOURCES WORKED FOR THIS DATE");
                                }
                                else
                                {
                                    text_back_msg.setVisibility(View.GONE);
                                }
                            }

                        }
                        catch(JSONException | ParseException e){e.printStackTrace();}
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
