package com.example.sadashivsinha.mprosmart.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.Adapters.WbsAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.WbsList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.AppController;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.example.sadashivsinha.mprosmart.Utils.FilePath;
import com.example.sadashivsinha.mprosmart.font.HelveticaBold;
import com.github.clans.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class WbsActivity extends AppCompatActivity implements View.OnClickListener {

    private List<WbsList> wbsList = new ArrayList<>();
    String image_url_link;
    private RecyclerView recyclerView;
    private WbsAdapter wbsAdapter;
    String currentProjectNo, currentProjectName, current_user_id;
    String wbsId, wbsName, projectId, progress, createdDate, totalBudget, currencyCode;
    View dialogView;
    AlertDialog show;
    int totalProjectBudget = 0;
    private static final int PICK_FILE_REQUEST = 1;
    private String selectedFilePath;
    ProgressDialog dialog;
    HelveticaBold btn_upload;
    String url;
    String searchText;
    String SERVER_URL;

    WbsList items;

    private ProgressDialog pDialog, pDialog1;
    JSONArray dataArray;
    JSONObject dataObject;
    ConnectionDetector cd;
    public static final String TAG = WbsActivity.class.getSimpleName();
    Boolean isInternetPresent = false;
    PreferenceManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wbs);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        pm = new PreferenceManager(getApplicationContext());
        SERVER_URL = pm.getString("SERVER_UPLOAD_URL") + "/upload/file-upload";

        currentProjectNo = pm.getString("projectId");
        currentProjectName = pm.getString("projectName");
        current_user_id = pm.getString("userId");

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        wbsAdapter = new WbsAdapter(wbsList);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(WbsActivity.this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(wbsAdapter);

        url = pm.getString("SERVER_URL") + "/getWbs?projectId=\"" + currentProjectNo + "\"";

        if (getIntent().hasExtra("search")) {
            if (getIntent().getStringExtra("search").equals("yes")) {

                searchText = getIntent().getStringExtra("searchText");

                getSupportActionBar().setTitle("WBS Search Results : " + searchText);
            }
        }


        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(WbsActivity.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(WbsActivity.this);
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


                        Boolean createWbsPending = pm.getBoolean("createWbsPending");

                        if(createWbsPending)
                        {
                            String jsonObjectVal = pm.getString("objectWbs");
                            Log.d("JSON WBS PENDING :", jsonObjectVal);

                            JSONObject jsonObjectPending = new JSONObject(jsonObjectVal);
                            Log.d("JSONObj WBS PENDING :", jsonObjectPending.toString());

                            wbsName = jsonObjectPending.getString("wbsName");
                            progress = jsonObjectPending.getString("progress");
                            totalBudget = jsonObjectPending.getString("totalBudget");
                            currencyCode = jsonObjectPending.getString("currencyCode");

                            items = new WbsList("-1", wbsName, progress, currencyCode, totalBudget);
                            wbsList.add(items);
                            wbsAdapter.notifyDataSetChanged();
                        }

                        for (int i = 0; i < dataArray.length(); i++) {
                            dataObject = dataArray.getJSONObject(i);
                            wbsId = dataObject.getString("wbsId");
                            wbsName = dataObject.getString("wbsName");
                            progress = dataObject.getString("progress");
                            totalBudget = dataObject.getString("totalBudget");
                            currencyCode = dataObject.getString("currencyCode");

                            if (getIntent().hasExtra("search"))
                            {
                                if (getIntent().getStringExtra("search").equals("yes")) {

                                    if (wbsName.toLowerCase().contains(searchText.toLowerCase()) || wbsId.toLowerCase().contains(searchText.toLowerCase())) {
                                        items = new WbsList(wbsId, wbsName, progress, currencyCode, totalBudget);
                                        wbsList.add(items);
                                        wbsAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                            else
                            {
                                items = new WbsList(wbsId, wbsName, progress, currencyCode, totalBudget);
                                wbsList.add(items);
                                wbsAdapter.notifyDataSetChanged();
                            }

                            totalProjectBudget = totalProjectBudget + Integer.parseInt(totalBudget);
                            pm.putInt("totalProjectBudget", totalProjectBudget);
                            Log.d("TOTAL WBS BUDGET ID " + wbsId, String.valueOf(totalProjectBudget));
                            pDialog.dismiss();
                        }

                    } catch (JSONException e) {
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
                Toast.makeText(WbsActivity.this, "Offline Data Not available for WBS", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }

        else
        {
            // Cache data not exist.
            callJsonArrayRequest();
        }

        FloatingActionButton fab_add, fab_search, exportBtn;

        fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        fab_search = (FloatingActionButton) findViewById(R.id.fab_search);
        exportBtn = (FloatingActionButton) findViewById(R.id.exportBtn);

        fab_add.setOnClickListener(this);
        fab_search.setOnClickListener(this);
        exportBtn.setOnClickListener(this);

        if (getIntent().hasExtra("create")) {
            if (getIntent().getStringExtra("create").equals("yes")) {
                Intent intent = new Intent(WbsActivity.this, WbsCreateActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add: {
//                createWbs();

                Intent intent = new Intent(WbsActivity.this, WbsCreateActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.fab_search: {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Search WBS by WBS Name or ID !");
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
                            Intent intent = new Intent(WbsActivity.this, WbsActivity.class);
                            intent.putExtra("search", "yes");
                            intent.putExtra("searchText", input.getText().toString());

                            Log.d("SEARCH TEXT", input.getText().toString());

                            startActivity(intent);
                        }
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(WbsActivity.this, "Search cancelled .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
            }
            break;
            case R.id.exportBtn: {
                // to do export
                Intent intent = new Intent(WbsActivity.this, WebViewWbsGantt.class);
                startActivity(intent);
            }
            break;
        }
    }

    private void callJsonArrayRequest() {
        // TODO Auto-generated method stub

        pDialog = new ProgressDialog(WbsActivity.this);
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
                                wbsId = dataObject.getString("wbsId");
                                wbsName = dataObject.getString("wbsName");
                                progress = dataObject.getString("progress");
                                totalBudget = dataObject.getString("totalBudget");
                                currencyCode = dataObject.getString("currencyCode");

                                if (getIntent().hasExtra("search"))
                                {
                                    if (getIntent().getStringExtra("search").equals("yes")) {

                                        if (wbsName.toLowerCase().contains(searchText.toLowerCase()) || wbsId.toLowerCase().contains(searchText.toLowerCase())) {
                                            items = new WbsList(wbsId, wbsName, progress, currencyCode, totalBudget);
                                            wbsList.add(items);
                                            wbsAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                                else
                                {
                                    items = new WbsList(wbsId, wbsName, progress, currencyCode, totalBudget);
                                    wbsList.add(items);
                                    wbsAdapter.notifyDataSetChanged();
                                }

                                totalProjectBudget = totalProjectBudget + Integer.parseInt(totalBudget);
                                pm.putInt("totalProjectBudget", totalProjectBudget);
                                Log.d("TOTAL WBS BUDGET ID " + wbsId, String.valueOf(totalProjectBudget));
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

    public void prepareSearchedValues(final String searchedText) {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = pm.getString("SERVER_URL") + "/getWbs?projectId=\"" + currentProjectNo + "\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            String type = response.getString("type");

                            if (type.equals("ERROR")) {
                                pDialog.dismiss();
                                Toast.makeText(WbsActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if (type.equals("INFO")) {
                                dataArray = response.getJSONArray("data");
                                for (int i = 0; i < dataArray.length(); i++) {
                                    dataObject = dataArray.getJSONObject(i);

                                    wbsId = dataObject.getString("wbsId");
                                    wbsName = dataObject.getString("wbsName");
                                    progress = dataObject.getString("progress");
                                    totalBudget = dataObject.getString("totalBudget");
                                    currencyCode = dataObject.getString("currencyCode");

                                    if (wbsName.toLowerCase().contains(searchedText.toLowerCase()) || wbsId.toLowerCase().contains(searchedText.toLowerCase())) {
                                        items = new WbsList(wbsId, wbsName, progress, currencyCode, totalBudget);
                                        wbsList.add(items);
                                        wbsAdapter.notifyDataSetChanged();
                                    }
                                }

                                if (wbsList.size() == 0) {
                                    Toast.makeText(WbsActivity.this, "Search didn't match any data", Toast.LENGTH_SHORT).show();
                                }
                            }
                            pDialog.dismiss();
                        } catch (JSONException e) {
                            pDialog.dismiss();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        Log.e("Volley", "Error");
                    }
                }
        );
        requestQueue.add(jor);
        wbsAdapter.notifyDataSetChanged();

    }

    public void saveData(String wbs_name, String wbs_desc, String currency_code, final String total_budget) {
        JSONObject object = new JSONObject();

        try {
            object.put("wbsName", wbs_name);
            object.put("projectId", currentProjectNo);
            object.put("wbsTittle", wbs_desc);
            object.put("createdBy", current_user_id);
            object.put("currencyCode", currency_code);
            object.put("totalBudget", total_budget);
            object.put("progress", "0");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(WbsActivity.this);

        String url = WbsActivity.this.pm.getString("SERVER_URL") + "/postWbs";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("msg").equals("success")) {
                                Toast.makeText(WbsActivity.this, "WBS Created. ID - '" + response.getString("data"), Toast.LENGTH_SHORT).show();
                                PreferenceManager pm = new PreferenceManager(getApplicationContext());
                                pm.putString("wbsId", response.getString("data"));

                                uploadImageToServer(response.getString("data"), image_url_link, total_budget);
                            } else {
                                Toast.makeText(WbsActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //response success message display
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                    }
                }
        );
        requestQueue.add(jor);
    }

    public void uploadImageToServer(String wbsId, String imageURL, final String total_budget) {

        JSONObject object = new JSONObject();

        try {
            object.put("wbsId", wbsId);
            object.put("url", imageURL);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(WbsActivity.this);

        String url = WbsActivity.this.pm.getString("SERVER_URL") + "/postWbsFiles";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("msg").equals("success")) {
                                updateProjectBudget(Float.parseFloat(total_budget));
                            } else {
                                Toast.makeText(WbsActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //response success message display
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                    }
                }
        );
        requestQueue.add(jor);
    }


    public void updateProjectBudget(float wbsBudget)
    {
        float projectBudget = Float.parseFloat(pm.getString("budget"));

        final float newBudget = projectBudget+wbsBudget;

        JSONObject object = new JSONObject();

        try {
            object.put("totalBudget", String.valueOf(newBudget));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(WbsActivity.this);

        String url = WbsActivity.this.pm.getString("SERVER_URL") + "/putProjectTotalBudget?projectId=\"" + currentProjectNo + "\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.PUT, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("msg").equals("success"))
                            {
                                pDialog.dismiss();
                                Intent intent = new Intent(WbsActivity.this, ActivityCreate.class);
                                pm.putString("budget", String.valueOf(newBudget));
                                startActivity(intent);
                            }
                            else
                            {
                                pDialog.dismiss();
                                Toast.makeText(WbsActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            pDialog.dismiss();
                            e.printStackTrace();
                        }
                        //response success message display
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                    }
                }
        );
        requestQueue.add(jor);

        if(pDialog!=null)
            pDialog.dismiss();
    }

    public void createWbs()
    {

        final AlertDialog.Builder alert = new AlertDialog.Builder(WbsActivity.this,android.R.style.Theme_Translucent_NoTitleBar);
        // Set an EditText view to get user input

        dialogView = LayoutInflater.from(WbsActivity.this).inflate(R.layout.dialog_new_wbs, null);
        alert.setView(dialogView);

        show = alert.show();

        final EditText wbs_name, wbs_desc, text_budget;
        final TextView text_currency;

        wbs_name = (EditText) dialogView.findViewById(R.id.wbs_name);
        wbs_desc = (EditText) dialogView.findViewById(R.id.wbs_desc);
        text_budget = (EditText) dialogView.findViewById(R.id.text_budget);
        text_currency = (TextView) dialogView.findViewById(R.id.text_currency);

        text_currency.setText(pm.getString("currency"));

        Button createBtn = (Button) dialogView.findViewById(R.id.createBtn);

        btn_upload = (HelveticaBold) dialogView.findViewById(R.id.btn_file_upload);

//        btn_upload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //image upload
//
//                chooseAndUploadImage();
//            }
//        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wbs_name.getText().toString().isEmpty())
                {
                    wbs_name.setError("Field cannot be empty");
                }
                else if(wbs_desc.getText().toString().isEmpty())
                {
                    wbs_desc.setError("Field cannot be empty");
                }
                else if(text_budget.getText().toString().isEmpty())
                {
                    text_budget.setError("Field cannot be empty");
                }

                else
                {
                    final String wbsName = wbs_name.getText().toString();
                    final String wbsDesc = wbs_desc.getText().toString();
                    final String currencyCode = text_currency.getText().toString();
                    final String totalBudget = text_budget.getText().toString();

                    pDialog1 = new ProgressDialog(WbsActivity.this);
                    pDialog1.setMessage("Sending Data ...");
                    pDialog1.setIndeterminate(false);
                    pDialog1.setCancelable(true);
                    pDialog1.show();

                    class MyTask extends AsyncTask<Void, Void, Void> {

                        @Override
                        protected Void doInBackground(Void... params) {
                            saveData(wbsName, wbsDesc,currencyCode, totalBudget );
                            return null;
                        }
                    }

                    new MyTask().execute();
                }
            }
        });
    }

    public void chooseAndUploadImage()
    {
        int currentapiVersion = Build.VERSION.SDK_INT;

        if (currentapiVersion > Build.VERSION_CODES.LOLLIPOP) {
            if (ContextCompat.checkSelfPermission(WbsActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(WbsActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 8);
            }
            else
                showFileChooser();
        }
        else
            showFileChooser();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 8:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    int currentapiVersion = Build.VERSION.SDK_INT;

                    if (currentapiVersion > Build.VERSION_CODES.LOLLIPOP) {
                        if (ContextCompat.checkSelfPermission(WbsActivity.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(WbsActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 9);
                        }
                    }

                }
                else
                {
                    int currentapiVersion = Build.VERSION.SDK_INT;

                    if (currentapiVersion > Build.VERSION_CODES.LOLLIPOP) {
                        if (ContextCompat.checkSelfPermission(WbsActivity.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(WbsActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 8);
                        }
                    }
                }
            case 9:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    showFileChooser();
                }

                else
                {
                    int currentapiVersion = Build.VERSION.SDK_INT;
                    if (currentapiVersion > Build.VERSION_CODES.LOLLIPOP) {
                        if (ContextCompat.checkSelfPermission(WbsActivity.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(WbsActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                        }
                    }
                }
        }
    }

    public void showFileChooser()
    {
        Intent intent = new Intent();
        //sets the select file to all types of files
        intent.setType("*/*");
        //allows to select data and return it
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //starts new activity to select file and return data
        startActivityForResult(Intent.createChooser(intent,"Choose File to Upload.."),PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == PICK_FILE_REQUEST){
                if(data == null){
                    //no data present
                    return;
                }

                Uri selectedFileUri = data.getData();
                selectedFilePath = FilePath.getPath(this,selectedFileUri);
                Log.i(TAG,"Selected File Path:" + selectedFilePath);

                if(selectedFilePath != null && !selectedFilePath.equals("")){
//                    tvFileName.setText(selectedFilePath);

                    //on upload button Click
                    if(selectedFilePath != null){

                        btn_upload.setText("UPLOAD in progress...");

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                //creating new thread to handle Http Operations
                                uploadFile(selectedFilePath);
                            }
                        }).start();
                    }else{
                        Toast.makeText(dialogView.getContext(),"Please choose a File First",Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(dialogView.getContext(),"Cannot upload file to server",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public int uploadFile(final String selectedFilePath) {

        int serverResponseCode = 0;

        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";


        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File selectedFile = new File(selectedFilePath);


        String[] parts = selectedFilePath.split("/");
        final String fileName = parts[parts.length - 1];

        if (!selectedFile.isFile()) {
            btn_upload.setText("UPLOAD failed!");
            return 0;
        } else {
            try {
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                URL url = new URL(SERVER_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);//Allow Inputs
                connection.setDoOutput(true);//Allow Outputs
                connection.setUseCaches(false);//Don't use a cached Copy
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("uploaded_file", selectedFilePath);

                //creating new dataoutputstream
                dataOutputStream = new DataOutputStream(connection.getOutputStream());

                //writing bytes to data outputstream
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + selectedFilePath + "\"" + lineEnd);

                dataOutputStream.writeBytes(lineEnd);

                //returns no. of bytes present in fileInputStream
                bytesAvailable = fileInputStream.available();
                //selecting the buffer size as minimum of available bytes or 1 MB
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                //setting the buffer as byte array of size of bufferSize
                buffer = new byte[bufferSize];

                //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                while (bytesRead > 0) {
                    //write the bytes read from inputstream
                    dataOutputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                final String response = connection.getResponseMessage();

                serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();

                Log.i(TAG, "Server Response is: " + serverResponseMessage + ": " + response);

                //response code of 200 indicates the server status OK

                btn_upload.setText("FILE Uploaded");

                image_url_link = response;

                //closing the input and output streams
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
                        Toast.makeText(dialogView.getContext(), "File Not Found", Toast.LENGTH_SHORT).show();

            } catch (MalformedURLException e) {
                e.printStackTrace();
                Toast.makeText(dialogView.getContext(), "URL error!", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(dialogView.getContext(), "Cannot Read/Write File!", Toast.LENGTH_SHORT).show();
            }
            return serverResponseCode;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(WbsActivity.this, ProjectPlanningSchedulingActivity.class);
        startActivity(intent);
    }
}