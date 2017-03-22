package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.Adapters.QualityChecklistAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.QualityChecklistList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class QualityCheckListActivity extends AppCompatActivity {

    TextView quality_checklist_no, project_id, project_name, date_created, created_by;
    private List<QualityChecklistList> qualityList = new ArrayList<>();
    private RecyclerView recyclerView;
    private QualityChecklistAdapter qualityAdapter;
    QualityChecklistList qualityItem;
    PreferenceManager pm;

    JSONArray dataArray;
    JSONObject dataObject;
    Boolean isInternetPresent = false;

    ProgressDialog pDialog, pDialog1;
    String subject, comments, id, status, createdBy, createdDate;
    String currentQualityChecklist, currentProjectNo, currentProjectName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quality_check_list);

        quality_checklist_no = (TextView) findViewById(R.id.quality_checklist_no);
        project_id = (TextView) findViewById(R.id.project_id);
        project_name = (TextView) findViewById(R.id.project_name);
        date_created = (TextView) findViewById(R.id.date_created);
        created_by = (TextView) findViewById(R.id.created_by);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentProjectName = pm.getString("projectName");
        currentQualityChecklist = pm.getString("currentQualityChecklist");

        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_content = (RelativeLayout) findViewById(R.id.main_content);
            Snackbar snackbar = Snackbar.make(main_content, getResources().getString(R.string.no_internet_error), Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        pDialog1 = new ProgressDialog(QualityCheckListActivity.this);
        pDialog1.setMessage("Preparing Header ...");
        pDialog1.setIndeterminate(false);
        pDialog1.setCancelable(true);
        pDialog1.show();

        prepareHeader();

        pDialog = new ProgressDialog(QualityCheckListActivity.this);
        pDialog.setMessage("Getting Data ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();


        class MyTask extends AsyncTask<Void, Void, Void> {

            @Override protected void onPreExecute() {
                qualityAdapter = new QualityChecklistAdapter(qualityList);
                recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                recyclerView.setLayoutManager(new LinearLayoutManager(QualityCheckListActivity.this));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(qualityAdapter);
            }

            @Override
            protected Void doInBackground(Void... params) {
                prepareItems();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                qualityAdapter.notifyDataSetChanged();
            }

        }

        new MyTask().execute();



    }


    public void prepareItems()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = pm.getString("SERVER_URL") + "/getQualityChecklistStatus?qualityChecklistStatus='"+currentQualityChecklist+"'";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(QualityCheckListActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    id = dataObject.getString("id");
                                    subject = dataObject.getString("subject");
                                    status = dataObject.getString("status");
                                    comments = dataObject.getString("comments");

                                    qualityItem = new QualityChecklistList(id, subject, status, comments);
                                    qualityList.add(qualityItem);

                                    qualityAdapter.notifyDataSetChanged();
                                }
                            }

                            pDialog.dismiss();

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

    public void prepareHeader()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = pm.getString("SERVER_URL") + "/getQualityCheckList?projectId='"+currentProjectNo+"'";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            String type = response.getString("type");

                            if (type.equals("ERROR")) {
                                Toast.makeText(QualityCheckListActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if (type.equals("INFO")) {
                                dataArray = response.getJSONArray("data");
                                for (int i = 0; i < dataArray.length(); i++) {
                                    dataObject = dataArray.getJSONObject(i);
                                    id = dataObject.getString("id");

                                    if(id.equals(currentQualityChecklist))
                                    {
                                        createdBy = dataObject.getString("createdBy");
                                        createdDate = dataObject.getString("createdDate");

                                        Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(createdDate);
                                        createdDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                        quality_checklist_no.setText(id);
                                        project_id.setText(currentProjectNo);
                                        project_name.setText(currentProjectName);
                                        date_created.setText(createdDate);
                                        created_by.setText(createdBy);
                                    }

                                }
                                pDialog1.dismiss();
                            }
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(QualityCheckListActivity.this, AllQualityChecklist.class);
        startActivity(intent);
    }

}
