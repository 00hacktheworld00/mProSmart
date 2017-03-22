package com.example.sadashivsinha.mprosmart.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.ModelLists.PurchaseReceiptCreateNewList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.AppController;
import com.example.sadashivsinha.mprosmart.Utils.Communicator;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.example.sadashivsinha.mprosmart.font.HelveticaRegular;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class PurchaseReceiptCreateNew extends AppCompatActivity {

    String currentPoNo, purchaseLineItemsId, quantity, currentProjectId, unitCost;
    ProgressDialog pDialog;
    JSONArray dataArray;
    JSONObject dataObject, poJsonObject, itemsJsonObject, jSONObjectToBeSent;
    Boolean isInternetPresent = false;
    JSONArray jsonArray;
    Button createBtn;

    private List<PurchaseReceiptCreateNewList> purchaseList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PurchaseReceiptCreateNewAdapter purchaseAdapter;

    PurchaseReceiptCreateNewList items;
    PreferenceManager pm;
    ConnectionDetector cd;
    String url;
    String currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_receipt_create_new);

        createBtn = (Button) findViewById(R.id.createBtn);

        jsonArray = new JSONArray();
        jSONObjectToBeSent = new JSONObject();
        poJsonObject = new JSONObject();
        itemsJsonObject = new JSONObject();

        pm = new PreferenceManager(getApplicationContext());
        currentPoNo = pm.getString("poNumber");
        currentProjectId = pm.getString("projectId");

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        currentDate = dateFormat.format(cal.getTime());

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();


        purchaseAdapter = new PurchaseReceiptCreateNewAdapter(purchaseList);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(PurchaseReceiptCreateNew.this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(purchaseAdapter);


        url = pm.getString("SERVER_URL") + "/getPurchaseLineItems?purchaseOrderId='"+currentPoNo+"'";

        // check for Internet status
        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            LinearLayout main_layout = (LinearLayout) findViewById(R.id.main_layout);
            Crouton.cancelAllCroutons();
            Crouton.makeText(PurchaseReceiptCreateNew.this, R.string.no_internet_error, Style.ALERT, main_layout).show();

            pDialog = new ProgressDialog(PurchaseReceiptCreateNew.this);
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
                            purchaseLineItemsId =  dataObject.getString("purchaseLineItemsId");
                            quantity =  dataObject.getString("poQuantity");
                            unitCost =  dataObject.getString("unitCost");


                            items = new PurchaseReceiptCreateNewList(0, purchaseLineItemsId, quantity, unitCost);
                            purchaseList.add(items);
                            purchaseAdapter.notifyDataSetChanged();
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
                Toast.makeText(PurchaseReceiptCreateNew.this, "Offline Data Not available for Purchase Items", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }

        else
        {
            // Cache data not exist.
            callJsonArrayRequest();
        }



        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(jsonArray.isNull(0))
                {
                    Toast.makeText(PurchaseReceiptCreateNew.this, "Select at least one Item", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    try {
                        jSONObjectToBeSent.put("purchaseOrderId", currentPoNo);
                        jSONObjectToBeSent.put("date", currentDate);
                        jSONObjectToBeSent.put("projectId", currentProjectId);
                        jSONObjectToBeSent.put("items", jsonArray);

                        Log.d("JSON ARRAY OF ITEMS", jSONObjectToBeSent.toString());

                        sendJsonObject(jSONObjectToBeSent);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    public void callJsonArrayRequest()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(PurchaseReceiptCreateNew.this, response.getString("msg"), Toast.LENGTH_SHORT).show();

                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    purchaseLineItemsId =  dataObject.getString("purchaseLineItemsId");
                                    quantity =  dataObject.getString("poQuantity");
                                    unitCost =  dataObject.getString("unitCost");


                                    items = new PurchaseReceiptCreateNewList(0, purchaseLineItemsId, quantity, unitCost);
                                    purchaseList.add(items);
                                    purchaseAdapter.notifyDataSetChanged();
                                }
                            }

                        }catch(JSONException e){
                            e.printStackTrace();}
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


    public class PurchaseReceiptCreateNewAdapter extends RecyclerView.Adapter<PurchaseReceiptCreateNewAdapter.MyViewHolder> {

        private List<PurchaseReceiptCreateNewList> list;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public HelveticaRegular item_id, po_quantity, unit_cost;
            EditText new_quantity;
            CheckBox checkBox;
            CardView cardview;
            JSONObject jsonObject;

            public MyViewHolder(final View view) {
                super(view);
                item_id = (HelveticaRegular) view.findViewById(R.id.item_id);
                new_quantity = (EditText) view.findViewById(R.id.new_quantity);
                po_quantity = (HelveticaRegular) view.findViewById(R.id.po_quantity);
                unit_cost = (HelveticaRegular) view.findViewById(R.id.unit_cost);

                jsonObject = new JSONObject();

                checkBox = (CheckBox) view.findViewById(R.id.checkBox);

                checkBox.setVisibility(View.INVISIBLE);

                cardview = (CardView) view.findViewById(R.id.cardview);

                TextWatcher watch = new TextWatcher(){

                    @Override
                    public void afterTextChanged(Editable arg0) {
                        // TODO Auto-generated method stub
                        if(new_quantity.getText().toString().equals("0"))
                        {
                            checkBox.setVisibility(View.INVISIBLE);
                        }
                        else if (new_quantity.getText().toString().isEmpty())
                        {
                            checkBox.setVisibility(View.INVISIBLE);
                        }
                        else if(Integer.parseInt(po_quantity.getText().toString()) < Integer.parseInt(new_quantity.getText().toString()))
                        {
                            new_quantity.setError("Quantity cannot be more than PO Quantity");
                        }
                        else
                        {
                            checkBox.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                                  int arg3) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int a, int b, int c) {
                        // TODO Auto-generated method stub

                        if(new_quantity.getText().toString().equals("0"))
                        {
                            checkBox.setVisibility(View.INVISIBLE);
                        }
                        else if (new_quantity.getText().toString().isEmpty())
                        {
                            checkBox.setVisibility(View.INVISIBLE);
                        }
                        else if(Integer.parseInt(po_quantity.getText().toString()) < Integer.parseInt(new_quantity.getText().toString()))
                        {
                            new_quantity.setError("Quantity cannot be more than PO Quantity");
                        }
                        else
                        {
                            checkBox.setVisibility(View.VISIBLE);
                        }
                    }};

                new_quantity.addTextChangedListener(watch);

                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try
                        {
                            if (checkBox.isChecked())
                            {
                                if(Integer.parseInt(po_quantity.getText().toString()) < Integer.parseInt(new_quantity.getText().toString()))
                                {
                                    new_quantity.setError("Quantity cannot be more than PO Quantity");
                                }
                                else
                                {
                                    new_quantity.setEnabled(false);

                                    jsonObject.put("itemId", item_id.getText().toString().isEmpty()? "0" : item_id.getText().toString());
                                    jsonObject.put("quantityReceived", new_quantity.getText().toString().isEmpty()? "0" : new_quantity.getText().toString());
                                    jsonObject.put("poQuantity", po_quantity.getText().toString().isEmpty()? "0" : po_quantity.getText().toString());
                                    jsonObject.put("unitCost", unit_cost.getText().toString().isEmpty()? "0" : unit_cost.getText().toString());

                                    jsonArray.put(jsonObject);

                                    cardview.setBackgroundColor(view.getContext().getResources().getColor(R.color.simple_blue));
                                }
                            }

                            else
                            {
                                new_quantity.setEnabled(true);

                                JSONObject checkForRemovalObject = new JSONObject();

                                for(int i=0; i < jsonArray.length(); i++) {

                                    checkForRemovalObject = jsonArray.getJSONObject(i);

                                    if(item_id.getText().toString().equals(checkForRemovalObject.getString("itemId")))
                                    {
                                        jsonArray.remove(i);
                                        break;
                                    }
                                }

                                cardview.setBackgroundColor(view.getContext().getResources().getColor(R.color.white));
                            }
                        }

                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }

        public PurchaseReceiptCreateNewAdapter(List<PurchaseReceiptCreateNewList> list) {
            this.list = list;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_item_inside_receipt, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            PurchaseReceiptCreateNewList items = list.get(position);

            holder.item_id.setText(String.valueOf(items.getItem_id()));
            holder.po_quantity.setText(items.getQuantity());
            holder.unit_cost.setText(items.getUnit_cost());

        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }



    public void sendJsonObject(JSONObject jSONObject) {

        RequestQueue requestQueue = Volley.newRequestQueue(PurchaseReceiptCreateNew.this);

        String url = PurchaseReceiptCreateNew.this.pm.getString("SERVER_URL") + "/reciept";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, jSONObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if(response.getString("msg").equals("success"))
                            {
                                Toast.makeText(PurchaseReceiptCreateNew.this, "Purchase Receipt Created. ID - "+ response.getString("data"), Toast.LENGTH_SHORT).show();

                                if(pDialog!=null)
                                     pDialog.dismiss();
                                Intent intent = new Intent(PurchaseReceiptCreateNew.this, PurchaseReceiptsNew.class);
                                startActivity(intent);
                            }
                            else
                            {
                                Toast.makeText(PurchaseReceiptCreateNew.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
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

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        if (!isInternetPresent)
        {
            // Internet connection is not present

            Communicator communicator = new Communicator();
            Log.d("object", jSONObject.toString());

            Boolean createPrPending = pm.getBoolean("createPrPending");

            if(createPrPending)
            {
                Toast.makeText(PurchaseReceiptCreateNew.this, "Already a Receipt creation is in progress. Please try after sometime.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(PurchaseReceiptCreateNew.this, "Internet not currently available. Receipt will automatically get created on internet connection.", Toast.LENGTH_SHORT).show();
                pm.putString("objectPR", jSONObject.toString());
                pm.putString("urlPR", url);
                pm.putString("toastMessagePR", "Purchase Receipt Created");
                pm.putBoolean("createPrPending", true);

                if(pDialog!=null)
                    pDialog.dismiss();

                Intent intent = new Intent(PurchaseReceiptCreateNew.this, PurchaseReceiptsNew.class);
                startActivity(intent);
            }
        }
        else
        {
            requestQueue.add(jor);
        }
        if(pDialog!=null)
            pDialog.dismiss();
    }
}
