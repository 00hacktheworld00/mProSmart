package com.example.sadashivsinha.mprosmart.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.sadashivsinha.mprosmart.Adapters.MyAdapter;
import com.example.sadashivsinha.mprosmart.Adapters.SubmittalAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.SubmittalList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.AppController;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.example.sadashivsinha.mprosmart.Utils.CsvCreateUtility;
import com.github.clans.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class SubmittalActivity extends NewActivity implements View.OnClickListener {

    private List<SubmittalList> submittalList = new ArrayList<>();
    private SubmittalAdapter submittalAdapter;

    Button view_details_btn;
    JSONArray dataArray;
    JSONObject dataObject;
    String submittalId, submittalRegisterId, submittalsType, createdDate, dueDate, createdBy, projectName;
    String lineNo, docType, description, variationFromContract, variationFromContractDocDsc, status,submittalNo;
    ConnectionDetector cd;
    public static final String TAG = NewAllProjects.class.getSimpleName();
    Boolean isInternetPresent = false;
    SubmittalList items;
    private ProgressDialog pDialog, pDialog1;
    String currentSubmittalNo, currentProjectNo, currentSubmittalId;
    TextView text_submittal_no, text_project_id, text_project_name, text_desc, text_type, text_created_by, text_due_date, text_date_created,
            text_status, text_sub_register;
    PreferenceManager pm;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_submittals);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pm = new PreferenceManager(getApplicationContext());
        currentSubmittalNo = pm.getString("submittalNo");
        currentProjectNo = pm.getString("projectId");
        currentSubmittalId = pm.getString("submittalNo");

        text_submittal_no = (TextView) findViewById(R.id.text_submittal_no);
        text_project_id = (TextView) findViewById(R.id.text_project_id);
        text_project_name = (TextView) findViewById(R.id.text_project_name);
        text_desc = (TextView) findViewById(R.id.text_desc);
        text_type = (TextView) findViewById(R.id.text_type);
        text_created_by = (TextView) findViewById(R.id.text_created_by);
        text_due_date = (TextView) findViewById(R.id.text_due_date);
        text_date_created = (TextView) findViewById(R.id.text_date_created);
        text_status = (TextView) findViewById(R.id.text_status);
        text_sub_register = (TextView) findViewById(R.id.text_sub_register);

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        prepareHeader();

        url = getResources().getString(R.string.server_url) + "/submittalLineItems?submittalsId='"+ currentSubmittalId + "'";

        submittalAdapter = new SubmittalAdapter(submittalList);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(submittalAdapter);

        // check for Internet status
        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(SubmittalActivity.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(SubmittalActivity.this);
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
                            lineNo = dataObject.getString("lineNo");
                            docType = dataObject.getString("docType");
                            description = dataObject.getString("description");
                            variationFromContract = dataObject.getString("variationFromContract");
                            variationFromContractDocDsc = dataObject.getString("variationFromContractDocDsc");
                            status = dataObject.getString("status");

                            items = new SubmittalList(lineNo, docType, description, variationFromContract, variationFromContractDocDsc,
                                    status, "");
                            submittalList.add(items);

                            submittalAdapter.notifyDataSetChanged();
                            pDialog.dismiss();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    Toast.makeText(getApplicationContext(), "Loading from cache.", Toast.LENGTH_SHORT).show();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (pDialog != null)
                    pDialog.dismiss();
            }

            else
            {
                Toast.makeText(SubmittalActivity.this, "Offline Data Not available for this Submittals", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }

        else
        {
            // Cache data not exist.
            callJsonArrayRequest();
        }





        view_details_btn = (Button) findViewById(R.id.view_details_btn);

        final LinearLayout hiddenLayout = (LinearLayout) findViewById(R.id.hiddenLayout);

        view_details_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hiddenLayout.getVisibility()==View.GONE)
                {
                    hiddenLayout.setVisibility(View.VISIBLE);
                    view_details_btn.setText("Hide Details");
                    hiddenLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.view_show));
                }
                else
                {
                    hiddenLayout.setVisibility(View.GONE);
                    view_details_btn.setText("View Details");
                }
            }
        });


        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View
        mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size
        mAdapter = new MyAdapter(TITLES,ICONS,NAME,EMAIL,PROFILE);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,header view name, header view email,
        // and header view profile picture
        mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView
        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager
        mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager
        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);        // Drawer object Assigned to the view
        mDrawerToggle = new ActionBarDrawerToggle(this,Drawer,toolbar,R.string.openDrawer,R.string.closeDrawer){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
                // open I am not going to put anything here)
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
            }



        }; // Drawer Toggle Object Made
        Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State

        FloatingActionButton fab_add, fab_search, exportBtn;

        fab_add = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_add);
        fab_search = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_search);
        exportBtn = (FloatingActionButton) findViewById(R.id.exportBtn);

        fab_add.setLabelText("Add new Submittal item");

        fab_add.setOnClickListener(this);
        fab_search.setOnClickListener(this);
        exportBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add:
            {
                Intent intent = new Intent(SubmittalActivity.this, SubmittalItemCreate.class);
                startActivity(intent);
            }
            break;

            case R.id.exportBtn:
            { int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP)
                {
                    if (ContextCompat.checkSelfPermission(SubmittalActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(SubmittalActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }

                    Environment.getExternalStorageState();

                    String line_no=null, doc_type=null, short_desc=null, variation=null, variation_desc=null, status=null, attachments=null;
                    int listSize = submittalList.size();
                    String cvsValues = "Sub No." + ","+"Line No." + ","+ "Document Type" + ","+ "Short Description" + ","+ "Variation from contract" + ","+ "Variation from Contract Doc Description" + ","+ "Line Item Status"+ ","+ "Attachments" + "\n";

                    for(int i=0; i<listSize;i++)
                    {
                        SubmittalList items = submittalList.get(i);
                        line_no = items.getLine_no();
                        doc_type = items.getText_doc_type();
                        short_desc = items.getText_short_desc();
                        variation = items.getText_variation();
                        variation_desc = items.getText_variation_desc();
                        status = items.getText_status();
                        attachments = items.getAttachments();

                        cvsValues = cvsValues + currentSubmittalNo + ","+  line_no + ","+  doc_type + ","+ short_desc + ","+ variation + ","+ variation_desc + ","+ status+ ","+ attachments + "\n";
                    }
                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "Submittal-No-"+currentSubmittalNo+".csv", cvsValues);
                }

                else

                {
                    Environment.getExternalStorageState();

                    String line_no=null, doc_type=null, short_desc=null, variation=null, variation_desc=null, status=null, attachments=null;
                    int listSize = submittalList.size();
                    String cvsValues = "Sub No." + ","+"Line No." + ","+ "Document Type" + ","+ "Short Description" + ","+ "Variation from contract" + ","+ "Variation from Contract Doc Description" + ","+ "Line Item Status"+ ","+ "Attachments" + "\n";

                    for(int i=0; i<listSize;i++)
                    {
                        SubmittalList items = submittalList.get(i);
                        line_no = items.getLine_no();
                        doc_type = items.getText_doc_type();
                        short_desc = items.getText_short_desc();
                        variation = items.getText_variation();
                        variation_desc = items.getText_variation_desc();
                        status = items.getText_status();
                        attachments = items.getAttachments();

                        cvsValues = cvsValues + currentSubmittalNo + ","+  line_no + ","+  doc_type + ","+ short_desc + ","+ variation + ","+ variation_desc + ","+ status+ ","+ attachments + "\n";
                    }
                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "Submittal-No-"+currentSubmittalNo+".csv", cvsValues);
                }
            }
            break;
            case R.id.fab_search:
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Search Submittals !");
                // Set an EditText view to get user input
                final EditText input = new EditText(this);
                alert.setView(input);
                alert.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(SubmittalActivity.this, "Search for it .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(SubmittalActivity.this, "Search cancelled .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
            }
            break;
        }
    }

    private void callJsonArrayRequest() {
        // TODO Auto-generated method stub

        pDialog = new ProgressDialog(SubmittalActivity.this);
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
                            for(int i=0; i<dataArray.length();i++)
                            {
                                dataObject = dataArray.getJSONObject(i);

                                lineNo = dataObject.getString("lineNo");
                                docType = dataObject.getString("docType");
                                description = dataObject.getString("description");
                                variationFromContract = dataObject.getString("variationFromContract");
                                variationFromContractDocDsc = dataObject.getString("variationFromContractDocDsc");
                                status = dataObject.getString("status");

                                items = new SubmittalList(lineNo, docType, description, variationFromContract, variationFromContractDocDsc,
                                        status, "");
                                submittalList.add(items);

                                submittalAdapter.notifyDataSetChanged();
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

    public void prepareHeader()
    {
        text_submittal_no.setText(pm.getString("submittalNo"));
        text_project_id.setText(pm.getString("submittalProjectId"));
        text_project_name.setText(pm.getString("submittalProjectName"));
        text_desc.setText(pm.getString("submittalDesc"));
        text_type.setText(pm.getString("submittalType"));
        text_created_by.setText(pm.getString("submittalCreatedBy"));
        text_due_date.setText(pm.getString("submittalDueDate"));
        text_date_created.setText(pm.getString("submittalDate"));
        text_status.setText(pm.getString("submittalStatus"));
        text_sub_register.setText(pm.getString("submittalSubRegId"));
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SubmittalActivity.this, AllSubmittals.class);
        startActivity(intent);
    }
}