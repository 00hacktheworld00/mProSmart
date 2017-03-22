package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.sadashivsinha.mprosmart.ModelLists.AllBoqList;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class ResourcesViewAddRemoveActivity extends AppCompatActivity {

    ConnectionDetector cd;
    public static final String TAG = ResourcesViewAddRemoveActivity.class.getSimpleName();
    Boolean isInternetPresent = false;
    PreferenceManager pm;
    String url, wbsActivityId;
    JSONObject dataObject;
    JSONArray dataArray;
    ProgressDialog pDialog;
    String firstName, lastName, wbsResourceId;

    String[] listviewTitle, listviewId;
    List<HashMap<String, String>> aList;
    ListView list_view;
    SimpleAdapter simpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resources_view_add_remove);

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        pm = new PreferenceManager(getApplicationContext());
        wbsActivityId = pm.getString("wbsActivityId");

        url = pm.getString("SERVER_URL") + "/getWbsResources?wbsActivityId=\""+wbsActivityId+"\"";

        pDialog = new ProgressDialog(getApplicationContext());
        pDialog.setMessage("Getting data...");
        pDialog.show();

        aList = new ArrayList<HashMap<String, String>>();

        list_view = (ListView) findViewById(R.id.list_view);

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                list_view.removeViewAt(position);
//                simpleAdapter.
            }
        });


        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_content);
            Crouton.cancelAllCroutons();
            Crouton.makeText(ResourcesViewAddRemoveActivity.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

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
                        listviewTitle = new String[dataArray.length()];
                        listviewId = new String[dataArray.length()];

                        for(int i=0; i<dataArray.length();i++)
                        {
                            dataObject = dataArray.getJSONObject(i);
                            firstName = dataObject.getString("firstName");
                            lastName = dataObject.getString("lastName");
                            wbsResourceId = dataObject.getString("wbsResourceId");

                            listviewTitle[i] = firstName + " " + lastName;
                            listviewId[i] = wbsResourceId;
                        }

                        for (int i = 0; i < 8; i++) {
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("listview_title", listviewTitle[i]);
                            hm.put("wbs_res_id", listviewId[i]);
                            aList.add(hm);
                        }

                        String[] from = {"listview_title", "wbs_res_id"};
                        int[] to = {R.id.listview_item_title, R.id.wbs_res_id};

                        simpleAdapter = new SimpleAdapter(getBaseContext(), aList, R.layout.listview_activity, from, to);
                        list_view.setAdapter(simpleAdapter);

                        if (pDialog != null)
                            pDialog.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    Toast.makeText(getApplicationContext(), "Loading from cache.", Toast.LENGTH_SHORT).show();
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }
            }

            else
            {
                Toast.makeText(ResourcesViewAddRemoveActivity.this, "Offline Data Not available for Resources", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }

        else
        {
            // Cache data not exist.
            prepareResources();
        }
    }

    public void prepareResources()
    {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {
//                            dataObject = response.getJSONObject(0);
                            dataArray = response.getJSONArray("data");
                            listviewTitle = new String[dataArray.length()];
                            listviewId = new String[dataArray.length()];

                            for(int i=0; i<dataArray.length();i++)
                            {
                                dataObject = dataArray.getJSONObject(i);
                                firstName = dataObject.getString("firstName");
                                lastName = dataObject.getString("lastName");
                                wbsResourceId = dataObject.getString("wbsResourceId");

                                listviewTitle[i] = firstName + " " + lastName;
                                listviewId[i] = wbsResourceId;
                            }

                            String[] from = {"listview_title", "wbs_res_id"};
                            int[] to = {R.id.listview_item_title, R.id.wbs_res_id};

                            simpleAdapter = new SimpleAdapter(getBaseContext(), aList, R.layout.listview_activity, from, to);
                            list_view.setAdapter(simpleAdapter);

                            pDialog.dismiss();

                        } catch (JSONException e) {
                            pDialog.dismiss();
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
    }
}
