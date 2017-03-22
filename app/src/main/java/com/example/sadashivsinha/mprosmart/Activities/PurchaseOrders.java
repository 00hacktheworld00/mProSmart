package com.example.sadashivsinha.mprosmart.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.Adapters.PurchaseOrdersAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.AllPurchaseRequisitionList;
import com.example.sadashivsinha.mprosmart.ModelLists.PurchaseOrdersList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.AppController;
import com.example.sadashivsinha.mprosmart.Utils.Communicator;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.example.sadashivsinha.mprosmart.Utils.CsvCreateUtility;
import com.example.sadashivsinha.mprosmart.font.HelveticaRegular;
import com.github.clans.fab.FloatingActionButton;
import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;

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

public class PurchaseOrders extends NewActivity implements View.OnClickListener  {

    private List<PurchaseOrdersList> purchaseOrdersList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PurchaseOrdersAdapter purchaseOrdersAdapter;
    PurchaseOrdersList items;
    JSONArray dataArray;
    JSONObject dataObject;
    String poId, vendorCode, createdOn, createdBy, totalAmount, projectId;
    ConnectionDetector cd;
    public static final String TAG = PurchaseOrders.class.getSimpleName();
    Boolean isInternetPresent = false;
    private ProgressDialog pDialog;
    String currentProjectNo, currentUserId;
    Spinner spinner_vendor;
    String vendorId;
    String[] vendorIdArray, vendorNameArray;
    View dialogView;
    AlertDialog show;
    String email_send_to, email_send_from, text, email_username, email_password;
    String url, vendor_url, approved, searchText;
    PreferenceManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_orders);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getIntent().hasExtra("search")) {
            if (getIntent().getStringExtra("search").equals("yes")) {

                searchText = getIntent().getStringExtra("searchText");

                getSupportActionBar().setTitle("PO Search Results : " + searchText);
            }
        }

        pm = new PreferenceManager(getApplicationContext());
        currentProjectNo = pm.getString("projectId");
        currentUserId = pm.getString("userId");

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        url = pm.getString("SERVER_URL") + "/getPurchaseOrders?projectId='"+currentProjectNo+"'";
        vendor_url = pm.getString("SERVER_URL") + "/getVendors";
        // check for Internet status

        recyclerView.setLayoutManager(new LinearLayoutManager(PurchaseOrders.this));
        recyclerView.setHasFixedSize(true);
        purchaseOrdersAdapter = new PurchaseOrdersAdapter(purchaseOrdersList);
        recyclerView.setAdapter(purchaseOrdersAdapter);

        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(PurchaseOrders.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(PurchaseOrders.this);
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
                    try
                    {
                        dataArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < dataArray.length(); i++)
                        {
                            dataObject = dataArray.getJSONObject(i);
                            poId = dataObject.getString("purchaseOrderId");
                            vendorCode = dataObject.getString("vendorId");
                            createdOn = dataObject.getString("createdDate");
                            createdBy = dataObject.getString("createdBy");
                            totalAmount = dataObject.getString("totalAmount");
                            approved = dataObject.getString("approved");

                            Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(createdOn);
                            createdOn = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);


                            if (getIntent().hasExtra("search"))
                            {
                                if (getIntent().getStringExtra("search").equals("yes")) {

                                    if (poId.toLowerCase().contains(searchText.toLowerCase())){
                                        items = new PurchaseOrdersList(String.valueOf(i+1), poId,vendorCode,createdOn, createdBy, totalAmount, approved);
                                        purchaseOrdersList.add(items);

                                        purchaseOrdersAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                            else
                            {
                                items = new PurchaseOrdersList(String.valueOf(i+1), poId,vendorCode,createdOn, createdBy, totalAmount, approved);
                                purchaseOrdersList.add(items);

                                purchaseOrdersAdapter.notifyDataSetChanged();
                            }

                            pDialog.dismiss();
                        }


                        //check if is offline mode and data creation is pending

//                        Boolean createPoPending = pm.getBoolean("createPoPending");
//                        Log.d("create Po Pending :", createPoPending.toString());
//
//                        if(createPoPending)
//                        {
//                            //if is in offline mode and data creation is pending, show the data in the list
//
//                            String jsonObjectVal = pm.getString("objectPO");
//                            Log.d("JSON PO PENDING :", jsonObjectVal);
//
//                            JSONObject jsonObjectPending = new JSONObject(jsonObjectVal);
//                            Log.d("JSONObj PO PENDING :", jsonObjectPending.toString());
//
//                            projectId = jsonObjectPending.getString("projectId");
//
//                            if(projectId.equals(currentProjectNo))
//                            {
//                                vendorCode = jsonObjectPending.getString("vendorId");
//                                createdOn = jsonObjectPending.getString("createdDate");
//                                createdBy = jsonObjectPending.getString("createdBy");
//
//                                Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(createdOn);
//                                createdOn = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);
//
//                                Log.d("JSONObj vendorCode :", vendorCode);
//                                Log.d("JSONObj createdOn :", createdOn);
//                                Log.d("JSONObj createdBy :", createdBy);
//                                Log.d("JSONObj createdOn :", createdOn);
//
//                                items = new PurchaseOrdersList(String.valueOf(dataArray.length() + 1), getResources().getString(R.string.waiting_to_connect) ,vendorCode,createdOn, createdBy, "0", "0");
//                                purchaseOrdersList.add(items);
//
//                                purchaseOrdersAdapter.notifyDataSetChanged();
//                            }
//                        }

                        int noOfPendingPo = pm.getInt("poPending");

                        if(noOfPendingPo!=0)
                        {
                            for(int i=1; i<noOfPendingPo+1; i++)
                            {
                                Log.d("PO Pend Que", String.valueOf(noOfPendingPo));

                                String jsonObjectVal = pm.getString("objectPO" + String.valueOf(i));
                                Log.d("JSON PO PENDING :", jsonObjectVal);

                                JSONObject jsonObjectPending = new JSONObject(jsonObjectVal);
                                Log.d("JSONObj PO PENDING :", jsonObjectPending.toString());

                                projectId = jsonObjectPending.getString("projectId");

                                if(projectId.equals(currentProjectNo))
                                {
                                    vendorCode = jsonObjectPending.getString("vendorId");
                                    createdOn = jsonObjectPending.getString("createdDate");
                                    createdBy = jsonObjectPending.getString("createdBy");

                                    Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(createdOn);
                                    createdOn = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                    Log.d("JSONObj vendorCode :", vendorCode);
                                    Log.d("JSONObj createdOn :", createdOn);
                                    Log.d("JSONObj createdBy :", createdBy);
                                    Log.d("JSONObj createdOn :", createdOn);

                                    items = new PurchaseOrdersList(String.valueOf(dataArray.length() + i), getResources().getString(R.string.waiting_to_connect)+ " " + String.valueOf(i) ,vendorCode,createdOn, createdBy, "0", "0");
                                    purchaseOrdersList.add(items);

                                    purchaseOrdersAdapter.notifyDataSetChanged();
                                }
                            }
                        }

                    }
                    catch (ParseException | JSONException e)
                    {
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
                Toast.makeText(PurchaseOrders.this, "Offline Data Not available for this Purchase Order", Toast.LENGTH_SHORT).show();
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

        if(getIntent().hasExtra("create"))
        {
            if(getIntent().getStringExtra("create").equals("yes"))
            {
                createPurchaseOrder();
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.exportBtn:
            {
                //csv export
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP) {
                    if (ContextCompat.checkSelfPermission(PurchaseOrders.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(PurchaseOrders.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                    }
                    Environment.getExternalStorageState();

                    String po_number = null, vendor_code = null, created_on = null, created_by = null;
                    int listSize = purchaseOrdersList.size();
                    String cvsValues = "PO No." + ","+ "Vendor Code" + ","+ "Created On" + ","+ "Created By" + "\n";

                    for(int i=0; i<listSize;i++)
                    {
                        PurchaseOrdersList items = purchaseOrdersList.get(i);
                        po_number = items.getPo_number();
                        vendor_code = items.getVendor_code();
                        created_on = items.getCreated_on();
                        created_by = items.getCreated_by();

                        cvsValues = cvsValues +  po_number + ","+ vendor_code + ","+ created_on + ","+ created_by + "\n";
                    }

                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "PO-data.csv", cvsValues);
                }

                else

                {
                    Environment.getExternalStorageState();

                    String po_number = null, vendor_code = null, created_on = null, created_by = null;
                    int listSize = purchaseOrdersList.size();
                    String cvsValues = "PO No." + ","+ "Vendor Code" + ","+ "Created On" + ","+ "Created By" + "\n";

                    for(int i=0; i<listSize;i++)
                    {
                        PurchaseOrdersList items = purchaseOrdersList.get(i);
                        po_number = items.getPo_number();
                        vendor_code = items.getVendor_code();
                        created_on = items.getCreated_on();
                        created_by = items.getCreated_by();

                        cvsValues = cvsValues +  po_number + ","+ vendor_code + ","+ created_on + ","+ created_by + "\n";
                    }

                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "PO-data.csv", cvsValues);

                }

            }
            break;
            case R.id.fab_add:
            {
//                Intent intent = new Intent(PurchaseOrders.this, PurchaseOrders.class);
//                startActivity(intent);
                createPurchaseOrder();
            }
            break;
            case R.id.fab_search:
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Search Purchase Orders by ID !");
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
                            Intent intent = new Intent(PurchaseOrders.this, PurchaseOrders.class);
                            intent.putExtra("search", "yes");
                            intent.putExtra("searchText", input.getText().toString());

                            Log.d("SEARCH TEXT", input.getText().toString());

                            startActivity(intent);
                        }
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(PurchaseOrders.this, "Search cancelled .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
            }
            break;
        }
    }
    private void callJsonArrayRequest() {
        // TODO Auto-generated method stub

        pDialog = new ProgressDialog(PurchaseOrders.this);
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
                                poId = dataObject.getString("purchaseOrderId");
                                vendorCode = dataObject.getString("vendorId");
                                createdOn = dataObject.getString("createdDate");
                                createdBy = dataObject.getString("createdBy");
                                totalAmount = dataObject.getString("totalAmount");
                                approved = dataObject.getString("approved");

                                Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(createdOn);
                                createdOn = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                if (getIntent().hasExtra("search"))
                                {
                                    if (getIntent().getStringExtra("search").equals("yes")) {

                                        if (poId.toLowerCase().contains(searchText.toLowerCase())){
                                            items = new PurchaseOrdersList(String.valueOf(i+1), poId,vendorCode,createdOn, createdBy, totalAmount, approved);
                                            purchaseOrdersList.add(items);

                                            purchaseOrdersAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                                else
                                {
                                    items = new PurchaseOrdersList(String.valueOf(i+1), poId,vendorCode,createdOn, createdBy, totalAmount, approved);
                                    purchaseOrdersList.add(items);

                                    purchaseOrdersAdapter.notifyDataSetChanged();
                                }

                            }
                        }
                        catch (ParseException | JSONException e)
                        {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_purchase_order, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void callVendorRequest() {
        // TODO Auto-generated method stub

        pDialog = new ProgressDialog(PurchaseOrders.this);
        pDialog.setMessage("Getting server data");
        pDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, vendor_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {
                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(PurchaseOrders.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                vendorIdArray = new String[dataArray.length()+1];
                                vendorNameArray = new String[dataArray.length()+1];

                                vendorIdArray[0]="Select Vendor";
                                vendorNameArray[0]= "Select Vendor";
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    vendorId = dataObject.getString("vendorId");
                                    vendorIdArray[i+1]=vendorId;
                                    vendorNameArray[i+1]= dataObject.getString("vendorName");
                                }
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(PurchaseOrders.this,
                                        android.R.layout.simple_dropdown_item_1line,vendorIdArray);
                                spinner_vendor.setAdapter(adapter);
                                pDialog.dismiss();

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

    public void createPo(String vendorId)
    {
        JSONObject object = new JSONObject();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String strDate = sdf.format(c.getTime());

        try {
            object.put("projectId", currentProjectNo);
            object.put("vendorId",vendorId);
            object.put("createdBy",currentUserId);
            object.put("createdDate",strDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(PurchaseOrders.this);

        String url = PurchaseOrders.this.pm.getString("SERVER_URL") + "/postPurchaseOrder";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
//                            Toast.makeText(PurchaseOrders.this, response.getString("msg").toString(), Toast.LENGTH_SHORT).show();
                            String successMsg = "Purchase Order Created. ID - "+response.getString("data").toString();
                            Toast.makeText(PurchaseOrders.this, successMsg, Toast.LENGTH_SHORT).show();

                            text = "New Purchase Order creation\n\n\n"+
                                    "\n"+"Project ID - "+currentProjectNo+
                                    "\n"+"Vendor ID - "+spinner_vendor.getSelectedItem().toString()+
                                    "\n"+"Created By - "+currentUserId+
                                    "\n"+"Created On - "+strDate+
                                    "\n\n\n"+"Response from Server - "+response.getString("msg").toString();

                            email_username = PurchaseOrders.this.getResources().getString(R.string.SENDGRID_USERNAME);
                            email_password = PurchaseOrders.this.getResources().getString(R.string.SENDGRID_PASSWORD);
                            email_send_to = PurchaseOrders.this.getResources().getString(R.string.SENDGRID_EMAIL_SEND_TO);
                            email_send_from = PurchaseOrders.this.getResources().getString(R.string.SENDGRID_EMAIL_SEND_FROM);
                            SendEmailASyncTask  task = new SendEmailASyncTask(getApplicationContext(), email_send_to, email_send_from, "New Purchase Order Creation", text, email_username, email_password);
                            task.execute();

                            Intent intent = new Intent(PurchaseOrders.this, PurchaseOrders.class);
                            startActivity(intent);

                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        //response success message display
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley","Error");
                        Toast.makeText(PurchaseOrders.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        if (!isInternetPresent)
        {
            // Internet connection is not present

            Communicator communicator = new Communicator();
            Log.d("object", object.toString());

            Toast.makeText(PurchaseOrders.this, "Internet not currently available. PO will automatically get created on internet connection.", Toast.LENGTH_SHORT).show();

            int noOfPendingPo = pm.getInt("poPending");
            Log.d("PO Pending", String.valueOf(noOfPendingPo));

            noOfPendingPo++;

            pm.putString("objectPO" + String.valueOf(noOfPendingPo), object.toString());
            pm.putString("urlPO" + String.valueOf(noOfPendingPo), url);
            pm.putString("toastMessagePO" + String.valueOf(noOfPendingPo), "Purchase Order Created");
            pm.putBoolean("createPoPending"+ String.valueOf(noOfPendingPo), true);

            pm.putInt("poPending", noOfPendingPo);

            Intent intent = new Intent(PurchaseOrders.this, PurchaseOrders.class);
            startActivity(intent);
        }
        else
        {
            requestQueue.add(jor);
        }
        if(pDialog!=null)
            pDialog.dismiss();
    }



    private static class SendEmailASyncTask extends AsyncTask<Void, Void, Void> {

        private Context mAppContext;
        private String mMsgResponse;

        private String mTo;
        private String mFrom;
        private String mSubject;
        private String mText;
        private String username;
        private String password;

        public SendEmailASyncTask(Context context, String mTo, String mFrom, String mSubject,
                                  String mText, String username, String password) {
            this.mAppContext = context.getApplicationContext();
            this.mTo = mTo;
            this.mFrom = mFrom;
            this.mSubject = mSubject;
            this.mText = mText;
            this.username = username;
            this.password = password;
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                SendGrid sendgrid = new SendGrid(username, password);

                SendGrid.Email email = new SendGrid.Email();

                // Get values from edit text to compose email
                // TODO: Validate edit texts
                email.addTo(mTo);
                email.setFrom(mFrom);
                email.setSubject(mSubject);
                email.setText(mText);

                // Send email, execute http request
                SendGrid.Response response = sendgrid.send(email);
                mMsgResponse = response.getMessage();

                Log.d("SendAppExample", mMsgResponse);

            } catch (SendGridException e) {
                Log.e("SendAppExample", e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }

    public void createPurchaseOrder()
    {
        final AlertDialog.Builder alert = new AlertDialog.Builder(PurchaseOrders.this,android.R.style.Theme_Translucent_NoTitleBar);

        dialogView = LayoutInflater.from(PurchaseOrders.this).inflate(R.layout.dialog_new_po, null);
        alert.setView(dialogView);

        show = alert.show();

        final HelveticaRegular text_vendor = (HelveticaRegular) dialogView.findViewById(R.id.text_vendor);

        final LinearLayout hiddenLayout = (LinearLayout) dialogView.findViewById(R.id.hiddenLayout);
        hiddenLayout.setVisibility(View.INVISIBLE);

        spinner_vendor = (Spinner) dialogView.findViewById(R.id.spinner_vendor);

        Button createBtn = (Button) dialogView.findViewById(R.id.createBtn);

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        if (!isInternetPresent)
        {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(PurchaseOrders.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(PurchaseOrders.this);
            pDialog.setMessage("Getting cache data");
            pDialog.show();

            Cache cache = AppController.getInstance().getRequestQueue().getCache();
            Cache.Entry entry = cache.get(vendor_url);
            if (entry != null) {
                //Cache data available.
                try {
                    String data = new String(entry.data, "UTF-8");
                    Log.d("CACHE DATA", data);
                    JSONObject jsonObject = new JSONObject(data);
                    try {
                        dataArray = jsonObject.getJSONArray("data");
                        vendorIdArray = new String[dataArray.length()+1];
                        vendorNameArray = new String[dataArray.length()+1];

                        vendorIdArray[0]="Select Vendor";
                        vendorNameArray[0]= "Select Vendor";
                        for(int i=0; i<dataArray.length();i++)
                        {
                            dataObject = dataArray.getJSONObject(i);
                            vendorId = dataObject.getString("vendorId");
                            vendorIdArray[i+1]=vendorId;
                            vendorNameArray[i+1]= dataObject.getString("vendorName");
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(PurchaseOrders.this,
                                android.R.layout.simple_dropdown_item_1line,vendorIdArray);
                        spinner_vendor.setAdapter(adapter);
                        pDialog.dismiss();

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
                Toast.makeText(PurchaseOrders.this, "Offline Data Not available for Vendor List", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }

        else
        {
            // Cache data not exist.
            callVendorRequest();
        }


        spinner_vendor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0)
                {
                    hiddenLayout.setVisibility(View.INVISIBLE);
                }
                else
                {
                    hiddenLayout.setVisibility(View.VISIBLE);
                    text_vendor.setText(vendorNameArray[position]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(spinner_vendor.getSelectedItem().toString().equals("Select Vendor"))
                {
                    Toast.makeText(PurchaseOrders.this, "Select Vendor First", Toast.LENGTH_SHORT).show();
                }

                else {
                    createPo(spinner_vendor.getSelectedItem().toString());
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PurchaseOrders.this, SiteProcurementActivity.class);
        startActivity(intent);
    }

}
