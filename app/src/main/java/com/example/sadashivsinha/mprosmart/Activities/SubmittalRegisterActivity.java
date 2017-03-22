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
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
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
import com.example.sadashivsinha.mprosmart.Adapters.SubmittalRegisterAdapter;
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

public class SubmittalRegisterActivity extends NewActivity implements View.OnClickListener  {

    private List<SubmittalList> submittalList = new ArrayList<>();
    private SubmittalRegisterAdapter submittalAdapter;
    private Button view_details_btn;
    SubmittalList items;
    JSONArray dataArray;
    JSONObject dataObject;
    String submittalregisterLineItemId, lineNo, submittaltittle, submittalType, status;
    String submittalRegistersId, projectId, Description, startDate, EndDate, Status, createdDate, createdBy, priority;
    TextView sub_register_id, project_id, start_date, end_date, date_created, status_text, priority_text, project_name, created_by;
    ConnectionDetector cd;
    public static final String TAG = SubmittalRegisterActivity.class.getSimpleName();
    Boolean isInternetPresent = false;
    ProgressDialog pDialog,pDialog1;
    String currentSubRegId, currentProjectNo, currentProjectName, contractId, totalAttachments;
    String url, searchText;
    PreferenceManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sub_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getIntent().hasExtra("search")) {
            if (getIntent().getStringExtra("search").equals("yes")) {

                searchText = getIntent().getStringExtra("searchText");

                getSupportActionBar().setTitle("Submittal Register Item Search Results : " + searchText);
            }
        }

        pm = new PreferenceManager(this);
        currentProjectNo =pm.getString("projectId");
        currentSubRegId =pm.getString("submittalRegistersId");
        currentProjectName =pm.getString("projectName");

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        sub_register_id = (TextView) findViewById(R.id.sub_register_id);
        project_id = (TextView) findViewById(R.id.project_id);
        start_date = (TextView) findViewById(R.id.start_date);
        end_date = (TextView) findViewById(R.id.end_date);
        date_created = (TextView) findViewById(R.id.date_created);
        status_text = (TextView) findViewById(R.id.status);
        priority_text = (TextView) findViewById(R.id.priority);
        project_name = (TextView) findViewById(R.id.project_name);
        created_by = (TextView) findViewById(R.id.created_by);

        prepareHeader();

        submittalAdapter = new SubmittalRegisterAdapter(submittalList);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(submittalAdapter);

        url = pm.getString("SERVER_URL") + "/getsubmittalRegistersIdById?submittalRegistersId='"+currentSubRegId+"'";


        // check for Internet status
        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(SubmittalRegisterActivity.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(SubmittalRegisterActivity.this);
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
                            submittalregisterLineItemId = dataObject.getString("submittalregisterLineItemId");
                            lineNo = dataObject.getString("lineNo");
                            submittaltittle = dataObject.getString("submittaltittle");
                            submittalType = dataObject.getString("submittalType");
                            status = dataObject.getString("status");
                            contractId = dataObject.getString("contractId");
                            totalAttachments = dataObject.getString("totalAttachments");

                            if (getIntent().hasExtra("search"))
                            {
                                if (getIntent().getStringExtra("search").equals("yes")) {

                                    if (lineNo.toLowerCase().contains(searchText.toLowerCase()) || submittaltittle.toLowerCase().contains(searchText.toLowerCase())) {

                                        items = new SubmittalList(String.valueOf(i + 1), submittalregisterLineItemId, submittaltittle, submittalType, status, contractId, Integer.parseInt(totalAttachments));
                                        submittalList.add(items);

                                        submittalAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                            else
                            {
                                items = new SubmittalList(String.valueOf(i + 1), submittalregisterLineItemId, submittaltittle, submittalType, status, contractId, Integer.parseInt(totalAttachments));
                                submittalList.add(items);

                                submittalAdapter.notifyDataSetChanged();
                            }

                            pDialog.dismiss();
                        }

                        Boolean createSubmittalRegItemPending = pm.getBoolean("createSubmittalRegItemPending");

                        if (createSubmittalRegItemPending) {

                            String jsonObjectVal = pm.getString("objectSubmittalRegItem");
                            Log.d("JSON SubReg PENDING :", jsonObjectVal);

                            JSONObject jsonObjectPending = new JSONObject(jsonObjectVal);
                            Log.d("JSONO SubReg PENDING :", jsonObjectPending.toString());

                            submittaltittle = dataObject.getString("submittaltittle");
                            submittalType = dataObject.getString("submittalType");
                            status = dataObject.getString("status");
                            contractId = dataObject.getString("contractId");

                            items = new SubmittalList(String.valueOf(dataArray.length()+1), getResources().getString(R.string.waiting_to_connect)
                                    , submittaltittle, submittalType, status, contractId, Integer.parseInt("0"));
                            submittalList.add(items);

                            submittalAdapter.notifyDataSetChanged();
                            pDialog.dismiss();

                        }
//                    Toast.makeText(getApplicationContext(), "Loading from cache.", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (pDialog != null)
                        pDialog.dismiss();
                }catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }
            }

                else
            {
                Toast.makeText(SubmittalRegisterActivity.this, "Offline Data Not available for this Submittal Register Line Items", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }
        else
        {
            // Cache data not exist.
            callJsonArrayRequest();
        }

        view_details_btn = (Button) findViewById(R.id.view_details_btn);

        final LinearLayout hiddenTextboxLayout = (LinearLayout) findViewById(R.id.hiddenLayout);

        view_details_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hiddenTextboxLayout.getVisibility()==View.GONE)
                {
                    hiddenTextboxLayout.setVisibility(View.VISIBLE);
                    view_details_btn.setText("Hide Details");
                    hiddenTextboxLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.view_show));
                }
                else
                {
                    hiddenTextboxLayout.setVisibility(View.GONE);
                    view_details_btn.setText("View Details");
                }
            }
        });

        FloatingActionButton fab_add, fab_search, exportBtn;

        fab_add = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_add);
        fab_search = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_search);
        exportBtn = (FloatingActionButton) findViewById(R.id.exportBtn);

        fab_add.setLabelText("Add new Submittal Register item");

        fab_add.setOnClickListener(this);
        fab_search.setOnClickListener(this);
        exportBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add:
            {
                Intent intent = new Intent(SubmittalRegisterActivity.this, SubRegisterItemCreate.class);
                startActivity(intent);
            }
            break;
            case R.id.exportBtn:
            { int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP)
                {
                    if (ContextCompat.checkSelfPermission(SubmittalRegisterActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(SubmittalRegisterActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }

                    Environment.getExternalStorageState();

                    String line_no=null, title=null, type=null, status=null, contract_id=null, attachments=null;
                    int listSize = submittalList.size();
                    String cvsValues = "Sub Reg No." + ","+"Line No." + ","+ "Submittal Title" + ","+ "Submittal Type" + ","+ "Submittal Status" + ","+ "Contract ID" + ","+ "Attachments" + "\n";

                    for(int i=0; i<listSize;i++)
                    {
                        SubmittalList items = submittalList.get(i);
                        line_no = items.getLine_no();
                        title = items.getText_sub_title();
                        type = items.getText_sub_type();
                        status = items.getText_status();
                        contract_id = items.getText_contract_id();
                        attachments = items.getAttachments();

                        cvsValues = cvsValues + currentSubRegId + ","+  line_no + ","+  title + ","+ type + ","+ status + ","+ contract_id+ ","+ attachments + "\n";
                    }
                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "SubReg-No-"+currentSubRegId+".csv", cvsValues);
                }

                else

                {

                    Environment.getExternalStorageState();

                    String line_no=null, title=null, type=null, status=null, contract_id=null, attachments=null;
                    int listSize = submittalList.size();
                    String cvsValues = "Sub Reg No." + ","+"Line No." + ","+ "Submittal Title" + ","+ "Submittal Type" + ","+ "Submittal Status" + ","+ "Contract ID" + ","+ "Attachments" + "\n";

                    for(int i=0; i<listSize;i++)
                    {
                        SubmittalList items = submittalList.get(i);
                        line_no = items.getLine_no();
                        title = items.getText_sub_title();
                        type = items.getText_sub_type();
                        status = items.getText_status();
                        contract_id = items.getText_contract_id();
                        attachments = items.getAttachments();

                        cvsValues = cvsValues + currentSubRegId + ","+  line_no + ","+  title + ","+ type + ","+ status + ","+ contract_id+ ","+ attachments + "\n";
                    }
                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "SubReg-No-"+currentSubRegId+".csv", cvsValues);
                }
            }
            break;
            case R.id.fab_search:
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Search Submittal Register by Line No or Submittal Title !");
                // Set an EditText view to get user input
                final EditText input = new EditText(this);
                input.setMaxLines(1);
                input.setImeOptions(EditorInfo.IME_ACTION_DONE);
                alert.setView(input);
                alert.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        if (input.getText().toString().isEmpty()) {
                            input.setError("Enter Search Field");
                        } else {
                            Intent intent = new Intent(SubmittalRegisterActivity.this, SubmittalRegisterActivity.class);
                            intent.putExtra("search", "yes");
                            intent.putExtra("searchText", input.getText().toString());

                            Log.d("SEARCH TEXT", input.getText().toString());

                            startActivity(intent);
                        }
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(SubmittalRegisterActivity.this, "Search cancelled .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
            }
            break;
        }
    }

    private void callJsonArrayRequest() {
        // TODO Auto-generated method stub

        pDialog = new ProgressDialog(SubmittalRegisterActivity.this);
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

                            Log.d("SubReg Res", response.toString());

                            for(int i=0; i<dataArray.length();i++)
                            {
                                dataObject = dataArray.getJSONObject(i);
                                submittalregisterLineItemId = dataObject.getString("submittalregisterLineItemId");
                                lineNo = dataObject.getString("lineNo");
                                submittaltittle = dataObject.getString("submittaltittle");
                                submittalType = dataObject.getString("submittalType");
                                status = dataObject.getString("status");
                                contractId = dataObject.getString("contractId");
                                totalAttachments = dataObject.getString("totalAttachments");

                                if (getIntent().hasExtra("search"))
                                {
                                    if (getIntent().getStringExtra("search").equals("yes")) {

                                        if (lineNo.toLowerCase().contains(searchText.toLowerCase()) || submittaltittle.toLowerCase().contains(searchText.toLowerCase())) {

                                            items = new SubmittalList(String.valueOf(i + 1), submittalregisterLineItemId, submittaltittle, submittalType, status, contractId, Integer.parseInt(totalAttachments));
                                            submittalList.add(items);

                                            submittalAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                                else
                                {
                                    items = new SubmittalList(String.valueOf(i + 1), submittalregisterLineItemId, submittaltittle, submittalType, status, contractId, Integer.parseInt(totalAttachments));
                                    submittalList.add(items);

                                    submittalAdapter.notifyDataSetChanged();
                                }
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
        project_name.setText(pm.getString("currentProjectName"));
        sub_register_id.setText(pm.getString("submittalRegistersId"));
        project_id.setText(pm.getString("projectId"));
        start_date.setText(pm.getString("startDate"));
        end_date.setText(pm.getString("EndDate"));
        date_created.setText(pm.getString("createdDate"));
        status_text.setText(pm.getString("Status"));
        priority_text.setText(pm.getString("priority"));
        created_by.setText(pm.getString("createdBy"));
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SubmittalRegisterActivity.this, AllSubmittalsRegister.class);
        startActivity(intent);
    }
}