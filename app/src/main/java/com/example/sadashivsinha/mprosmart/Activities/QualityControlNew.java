package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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
import com.example.sadashivsinha.mprosmart.Adapters.MyAdapter;
import com.example.sadashivsinha.mprosmart.ModelLists.MomList;
import com.example.sadashivsinha.mprosmart.ModelLists.PurchaseReceiptCreateNewList;
import com.example.sadashivsinha.mprosmart.ModelLists.QualityList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.AppController;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.example.sadashivsinha.mprosmart.font.HelveticaBold;
import com.example.sadashivsinha.mprosmart.font.HelveticaRegular;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class QualityControlNew extends NewActivity {

    private TextView qirNo, purchaseOrder, receiptNo, vendorId, createdBy;
    MomList items;
    PurchaseReceiptCreateNewList LineItems;

    QualityList qualityItem;
    JSONArray dataArray;
    JSONObject dataObject;
    String qir_no, vendor_id, receipt_no, project_id, created_by, purchase_order_no;
    String itemId, itemDescription, quantityReceived, quantityAccepted, quantityRejected;
    ConnectionDetector cd;
    public static final String TAG = PurchaseOrders.class.getSimpleName();
    Boolean isInternetPresent = false;
    private ProgressDialog pDialog;
    String currentQualityPoNo, itemName, purchaseLineItemsId, purchaseReceiptItemsId, quantity, poQuantity, unitCost, rejectedQuantityString, acceptedQuantityString;
    String currentQirNo,currentProjectNo, currentReceiptNo;
    int totalQuantity, rejectedQuantity, acceptedQuantity;
    private List<PurchaseReceiptCreateNewList> purchaseList = new ArrayList<>();
    private RecyclerView recyclerView;
    private QualityControlNewItemAdapter qualityAdapter;
    PreferenceManager pm;
    String url, po_url;
    String[] itemIdArray, poItemIdArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quality_control_new);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        pm = new PreferenceManager(getApplicationContext());


        qirNo = (TextView) findViewById(R.id.qir_no);
        purchaseOrder = (TextView) findViewById(R.id.purchase_order);
        receiptNo = (TextView) findViewById(R.id.receipt_no);
        vendorId = (TextView) findViewById(R.id.vendor_id);
        createdBy = (TextView) findViewById(R.id.created_by);

        currentQirNo = pm.getString("qirNo");
        currentProjectNo = pm.getString("projectId");
        currentQualityPoNo  = pm.getString("currentQualityPoNo");
        currentReceiptNo = pm.getString("receiptNo");


        url = pm.getString("SERVER_URL") + "/getPurchaseReceiptItems?purchaseReceiptId=\""+currentReceiptNo+"\"";
        po_url = pm.getString("SERVER_URL") + "/getPurchaseLineItems?purchaseOrderId=\"" + currentQualityPoNo + "\"";


        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        qualityAdapter = new QualityControlNewItemAdapter(purchaseList);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(QualityControlNew.this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(qualityAdapter);

        // check for Internet status
        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            RelativeLayout main_layout = (RelativeLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(QualityControlNew.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(QualityControlNew.this);
            pDialog.setMessage("Getting cache data");
            pDialog.show();

            Cache cache = AppController.getInstance().getRequestQueue().getCache();
            Cache.Entry entry = cache.get(po_url);
            if (entry != null) {
                //Cache data available.
                try {
                    String data = new String(entry.data, "UTF-8");
                    Log.d("CACHE DATA", data);
                    JSONObject jsonObject = new JSONObject(data);
                    try {
                        dataArray = jsonObject.getJSONArray("data");

                        itemIdArray = new String[dataArray.length()];
                        poItemIdArray = new String[dataArray.length()];

                        for (int i = 0; i < dataArray.length(); i++) {
                            dataObject = dataArray.getJSONObject(i);
                            purchaseLineItemsId =  dataObject.getString("purchaseLineItemsId");
                            itemName =  dataObject.getString("itemName");

                            poItemIdArray[i] = purchaseLineItemsId;
                            itemIdArray[i] = itemName;
                        }

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
                Toast.makeText(QualityControlNew.this, "Offline Data Not available for this PO Items in this QIR", Toast.LENGTH_SHORT).show();
            }


            entry = cache.get(url);
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
                            purchaseReceiptItemsId =  dataObject.getString("purchaseReceiptItemsId");
                            quantity =  dataObject.getString("quantityReceived");
                            poQuantity =  dataObject.getString("poQuantity");
                            unitCost =  dataObject.getString("unitCost");
                            acceptedQuantityString = dataObject.getString("acceptedQuantity");
                            rejectedQuantityString = dataObject.getString("rejectedQuantity");
                            itemId = dataObject.getString("itemId");

                            for(int j=0; j<itemIdArray.length; j++)
                            {
                                if(itemId.equals(poItemIdArray[j]))
                                    itemId = itemIdArray[j];
                            }

                            LineItems = new PurchaseReceiptCreateNewList(itemId, quantity, poQuantity,
                                    unitCost, acceptedQuantityString, rejectedQuantityString);
                            purchaseList.add(LineItems);

                            qualityAdapter.notifyDataSetChanged();
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
                Toast.makeText(QualityControlNew.this, "Offline Data Not available for this QIR", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }

        else
        {
            // Cache data not exist.
            callGetItemsIdFromPo();
        }

        prepareHeader();

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
    }

    public void callGetItemsIdFromPo()
    {
        pDialog = new ProgressDialog(QualityControlNew.this);
        pDialog.setMessage("Getting server data");
        pDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, po_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {
//                            dataObject = response.getJSONObject(0);
                            dataArray = response.getJSONArray("data");

                            poItemIdArray = new String[dataArray.length()];
                            itemIdArray = new String[dataArray.length()];

                            for(int i=0; i<dataArray.length();i++)
                            {
                                dataObject = dataArray.getJSONObject(i);
                                purchaseLineItemsId =  dataObject.getString("purchaseLineItemsId");
                                itemName =  dataObject.getString("itemName");

                                poItemIdArray[i] = purchaseLineItemsId;
                                itemIdArray[i] = itemName;
                            }

                            callJsonArrayRequest();

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
        qirNo.setText(pm.getString("qirNo"));
        purchaseOrder.setText(pm.getString("currentQualityPoNo"));
        receiptNo.setText(pm.getString("receiptNo"));
        vendorId.setText(pm.getString("vendorId"));
        createdBy.setText(pm.getString("createdBy"));
    }

    public void callJsonArrayRequest()
    {
        pDialog = new ProgressDialog(QualityControlNew.this);
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
                                purchaseReceiptItemsId =  dataObject.getString("purchaseReceiptItemsId");
                                quantity =  dataObject.getString("quantityReceived");
                                poQuantity =  dataObject.getString("poQuantity");
                                unitCost =  dataObject.getString("unitCost");
                                acceptedQuantityString = dataObject.getString("acceptedQuantity");
                                rejectedQuantityString = dataObject.getString("rejectedQuantity");
                                itemId = dataObject.getString("itemId");

                                for(int j=0; j<itemIdArray.length; j++)
                                {
                                    if(itemId.equals(poItemIdArray[j]))
                                        itemId = itemIdArray[j];
                                }

                                LineItems = new PurchaseReceiptCreateNewList(purchaseReceiptItemsId, itemId, quantity, poQuantity, unitCost, acceptedQuantityString, rejectedQuantityString);
                                purchaseList.add(LineItems);

                                qualityAdapter.notifyDataSetChanged();

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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(QualityControlNew.this, AllQualityControl.class);
        startActivity(intent);
    }




    //adapter of class






    public class QualityControlNewItemAdapter extends RecyclerView.Adapter<QualityControlNewItemAdapter.MyViewHolder> {

        private List<PurchaseReceiptCreateNewList> list;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public HelveticaRegular item_id, text_rejected, new_quantity, unit_cost, po_quantity, text_accepted;
            CardView cardview;
            HelveticaBold btn_update_quantity;
            TextView pur_id;

            public MyViewHolder(final View view) {
                super(view);
                item_id = (HelveticaRegular) view.findViewById(R.id.item_id);
                text_accepted = (HelveticaRegular) view.findViewById(R.id.text_accepted);

                pur_id = (TextView) view.findViewById(R.id.pur_id);

                unit_cost = (HelveticaRegular) view.findViewById(R.id.unit_cost);
                po_quantity = (HelveticaRegular) view.findViewById(R.id.po_quantity);

                new_quantity = (HelveticaRegular) view.findViewById(R.id.new_quantity);
                text_rejected = (HelveticaRegular) view.findViewById(R.id.text_rejected);

                btn_update_quantity = (HelveticaBold) view.findViewById(R.id.btn_update_quantity);

                btn_update_quantity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                        alert.setTitle("Enter Accepted Quantity for ITEM ID : " + item_id.getText().toString());
                        // Set an EditText view to get user input
                        final EditText input = new EditText(view.getContext());
                        input.setMaxLines(1);
                        input.setInputType(InputType.TYPE_CLASS_NUMBER);
                        input.setImeOptions(EditorInfo.IME_ACTION_DONE);
                        input.setHint(text_accepted.getText().toString());
                        alert.setView(input);
                        alert.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                if(input.getText().toString().isEmpty())
                                {
                                    input.setError("Enter Accepted Quantity for ITEM ID : " + item_id.getText().toString());
                                }
                                else if(Integer.parseInt(input.getText().toString()) > Integer.parseInt(new_quantity.getText().toString()))
                                {
                                    Toast.makeText(view.getContext(), "Accepted Quantity cannot be more than Received Quantity", Toast.LENGTH_SHORT).show();
                                }
                                else if(Integer.parseInt(input.getText().toString()) < Integer.parseInt(text_accepted.getText().toString()))
                                {
                                    Toast.makeText(view.getContext(), "Accepted Quantity cannot be less than previously received quantity", Toast.LENGTH_SHORT).show();
                                }
                                else if(Integer.parseInt(input.getText().toString()) <1)
                                {
                                    Toast.makeText(view.getContext(), "Minimum quantity accepted should be 1", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    int acceptedQuantityVal = Integer.parseInt(input.getText().toString());
                                    int newQuantityVal = Integer.parseInt(new_quantity.getText().toString());

                                    int rejectedQuantityVal = newQuantityVal - acceptedQuantityVal;

                                    updateQuantity(view.getContext(), pur_id.getText().toString(), input.getText().toString(), String.valueOf(rejectedQuantityVal), newQuantityVal, text_accepted, text_rejected, btn_update_quantity);

                                }
                            }
                        });
                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Toast.makeText(view.getContext(), "Cancelled Updation .", Toast.LENGTH_SHORT).show();
                            }
                        });
                        alert.show();
                    }
                });


                cardview = (CardView) view.findViewById(R.id.cardview);
            }
        }

        public QualityControlNewItemAdapter(List<PurchaseReceiptCreateNewList> list) {
            this.list = list;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.quality_control_new_item, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            PurchaseReceiptCreateNewList items = list.get(position);

            holder.item_id.setText(String.valueOf(items.getPurchaseReceiptItemsId()));
            holder.pur_id.setText(String.valueOf(items.getPurId()));
            holder.new_quantity.setText(items.getQuantity());
            holder.unit_cost.setText(String.valueOf(items.getUnit_cost()));
            holder.po_quantity.setText(items.getPoQuantity());
            holder.text_accepted.setText(String.valueOf(items.getAcceptedQuantity()));
            holder.text_rejected.setText(items.getRejectedQuantity());

            if(Integer.parseInt(holder.new_quantity.getText().toString()) == Integer.parseInt(holder.text_accepted.getText().toString()))
            {
                holder.btn_update_quantity.setVisibility(View.GONE);
            }
            else
            {
                holder.btn_update_quantity.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    public void updateQuantity(final Context context, final String itemId, final String acceptedQuantity, final String rejectedQuantity, final int newQuantityVal,
                               final HelveticaRegular accepted_quantity_view, final HelveticaRegular rejected_quantity_view, final HelveticaBold updateButton)
    {
        JSONObject object = new JSONObject();

        try {
            object.put("acceptedQuantity",acceptedQuantity);
            object.put("rejectedQuantity", rejectedQuantity);

            Log.d("REQUEST SENT OF JSON :" , object.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        String url = pm.getString("SERVER_URL") + "/putPurchaseReceiptItemsAcc?purchaseReceiptItemsId=\"" + itemId + "\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.PUT, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            Log.d("SERVER RESPONSE :" , response.toString());

                            if(response.getString("msg").equals("success"))
                            {
                                Toast.makeText(context, "Quantity Accepted/Rejected Updated for Item ID - " + itemId, Toast.LENGTH_SHORT).show();

                                //set to view new values to textViews
                                accepted_quantity_view.setText(acceptedQuantity);
                                rejected_quantity_view.setText(rejectedQuantity);

                                if(Integer.parseInt(acceptedQuantity) == newQuantityVal)
                                {
                                    updateButton.setVisibility(View.GONE);
                                }

                                pDialog.dismiss();
                            }
                            else
                            {
                                Toast.makeText(QualityControlNew.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
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

}

