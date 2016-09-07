package com.example.sadashivsinha.mprosmart.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.Adapters.MyAdapter;
import com.example.sadashivsinha.mprosmart.Adapters.QualityAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.MomList;
import com.example.sadashivsinha.mprosmart.ModelLists.QualityList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.example.sadashivsinha.mprosmart.Utils.CsvCreateUtility;
import com.github.clans.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class QualityControl extends NewActivity implements View.OnClickListener {

    private List<QualityList> qualityList = new ArrayList<>();
    private RecyclerView recyclerView;
    private QualityAdapter qualityAdapter;
    private TextView qirNo, purchaseOrder, receiptNo, vendorId, createdBy;
    MomList items;
    QualityList qualityItem;
    JSONArray dataArray;
    JSONObject dataObject;
    String qir_no, vendor_id, receipt_no, project_id, created_by, purchase_order_no;
    String itemId, itemDescription, quantityReceived, quantityAccepted, quantityRejected;
    ConnectionDetector cd;
    public static final String TAG = PurchaseOrders.class.getSimpleName();
    Boolean isInternetPresent = false;
    private ProgressDialog pDialog1, pDialog2;
    String currentQirNo,currentProjectNo;
    int totalQuantity, rejectedQuantity, acceptedQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quality_control);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        PreferenceManager pm = new PreferenceManager(getApplicationContext());

        qirNo = (TextView) findViewById(R.id.qir_no);
        purchaseOrder = (TextView) findViewById(R.id.purchase_order);
        receiptNo = (TextView) findViewById(R.id.receipt_no);
        vendorId = (TextView) findViewById(R.id.vendor_id);
        createdBy = (TextView) findViewById(R.id.created_by);

        currentQirNo = pm.getString("qirNo");
        currentProjectNo = pm.getString("projectId");

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_content = (RelativeLayout) findViewById(R.id.main_content);
            Snackbar snackbar = Snackbar.make(main_content,getResources().getString(R.string.no_internet_error), Snackbar.LENGTH_LONG);
            snackbar.show();
        }
        pDialog1 = new ProgressDialog(QualityControl.this);
        pDialog1.setMessage("Preparing Header ...");
        pDialog1.setIndeterminate(false);
        pDialog1.setCancelable(true);
        pDialog1.show();

        prepareHeader();

        qualityAdapter = new QualityAdapter(qualityList);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(qualityAdapter);


        pDialog2 = new ProgressDialog(QualityControl.this);
        pDialog2.setMessage("Getting Data ...");
        pDialog2.setIndeterminate(false);
        pDialog2.setCancelable(true);
        pDialog2.show();

        class MyTask extends AsyncTask<Void, Void, Void>
        {

            @Override
            protected Void doInBackground(Void... params)
            {
                prepareItems();
                return null;
            }

        }

        new MyTask().execute();

        final CardView cardView = (CardView) findViewById(R.id.cardview);

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

        fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        fab_search = (FloatingActionButton) findViewById(R.id.fab_search);
        exportBtn = (FloatingActionButton) findViewById(R.id.exportBtn);

        fab_add.setLabelText("Add Quality control item");

        fab_add.setOnClickListener(this);
        fab_search.setOnClickListener(this);
        exportBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add:
            {
                Intent intent = new Intent(QualityControl.this, QualityControlItemCreate.class);
                startActivity(intent);
            }
            break;
            case R.id.fab_search:
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Search Quality Control !");
                // Set an EditText view to get user input
                final EditText input = new EditText(this);
                alert.setView(input);
                alert.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(QualityControl.this, "Search for it .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(QualityControl.this, "Search cancelled .", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
            }
            break;
            case R.id.exportBtn:
            { int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP)
                {
                    if (ContextCompat.checkSelfPermission(QualityControl.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(QualityControl.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }

                    Environment.getExternalStorageState();

                    String itemId=null, received_quantity=null, accepted=null, rejected=null, attachments=null;
                    int listSize = qualityList.size();
                    String cvsValues = "QIR No." + ","+"Item ID" + ","+ "Quantity Received" + ","+ "Accepted" + ","+ "Rejected" + ","+ "Attachments" + "\n";

                    for(int i=0; i<listSize;i++)
                    {
                        QualityList items = qualityList.get(i);
                        itemId = items.getItemId();
                        received_quantity = items.getReceivedQuantity();
                        accepted = items.getQuantityAccept();
                        rejected = items.getQuantityReject();
                        attachments = items.getAttachments();

                        cvsValues = cvsValues + currentQirNo + ","+  itemId + ","+  received_quantity + ","+ accepted + ","+ rejected + ","+ attachments + "\n";
                    }
                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "PO-No-"+currentQirNo+".csv", cvsValues);
                }

                else

                {
                    Environment.getExternalStorageState();

                    String itemId=null, received_quantity=null, accepted=null, rejected=null, attachments=null;
                    int listSize = qualityList.size();
                    String cvsValues = "QIR No." + ","+"Item ID" + ","+ "Quantity Received" + ","+ "Accepted" + ","+ "Rejected" + ","+ "Attachments" + "\n";

                    for(int i=0; i<listSize;i++)
                    {
                        QualityList items = qualityList.get(i);
                        itemId = items.getItemId();
                        received_quantity = items.getReceivedQuantity();
                        accepted = items.getQuantityAccept();
                        rejected = items.getQuantityReject();
                        attachments = items.getAttachments();

                        cvsValues = cvsValues + currentQirNo + ","+  itemId + ","+  received_quantity + ","+ accepted + ","+ rejected + ","+ attachments + "\n";
                    }
                    CsvCreateUtility.generateNoteOnSD(getApplicationContext(), "QIR-No-"+currentQirNo+".csv", cvsValues);
                }
            }
            break;
        }
    }

    public void prepareItems()
    {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = getResources().getString(R.string.server_url) + "/getQualityInspection?qualityInspectionId="+"'"+currentQirNo+"'";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(QualityControl.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    itemId = dataObject.getString("itemId");
                                    itemDescription = dataObject.getString("itemDescription");
                                    quantityReceived = dataObject.getString("quantityReceived");
                                    quantityAccepted = dataObject.getString("quantityAccepted");
                                    quantityRejected = dataObject.getString("quantityRejected");

                                    totalQuantity = Integer.parseInt(quantityReceived);
                                    acceptedQuantity = Integer.parseInt(quantityAccepted);
                                    rejectedQuantity = totalQuantity - acceptedQuantity;

                                    quantityRejected = String.valueOf(rejectedQuantity);

                                    qualityItem = new QualityList(itemId, itemDescription,quantityReceived, quantityAccepted, quantityRejected, "0");
                                    qualityList.add(qualityItem);
                                    qualityAdapter.notifyDataSetChanged();
                                }
                            }
                            pDialog2.dismiss();
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

        String url = getResources().getString(R.string.server_url) + "/getAllQualityControls?projectId='"+currentProjectNo+"'";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(QualityControl.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    qir_no = dataObject.getString("qualityInspectionReportId");
                                    if(qir_no.equals(currentQirNo))
                                    {
                                        vendor_id = dataObject.getString("vendorId");
                                        receipt_no = dataObject.getString("purchaseReceiptId");
                                        project_id = dataObject.getString("projectId");
                                        created_by = dataObject.getString("createdBy");
                                        purchase_order_no = dataObject.getString("purchaseOrderId");

                                        qirNo.setText(currentQirNo);
                                        purchaseOrder.setText(purchase_order_no);
                                        receiptNo.setText(receipt_no);
                                        vendorId.setText(vendor_id);
                                        createdBy.setText(created_by);
                                    }
                                }
                            }
                            pDialog1.dismiss();
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(QualityControl.this, AllQualityControl.class);
        startActivity(intent);
    }
}
