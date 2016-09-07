package com.example.sadashivsinha.mprosmart.Activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.ModelLists.PurchaseReceiptCreateNewList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.example.sadashivsinha.mprosmart.Utils.DatePickerFragment;
import com.example.sadashivsinha.mprosmart.font.HelveticaBold;
import com.example.sadashivsinha.mprosmart.font.HelveticaRegular;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SelectPurchaseRequisitionItems extends AppCompatActivity {

    String currentPoNo, purchaseLineItemsId, quantity, currentProjectId, itemId;
    ProgressDialog pDialog;
    String[] itemIdArray, quantityArray;
    JSONArray dataArray;
    JSONObject dataObject, poJsonObject, itemsJsonObject;
    Boolean isInternetPresent = false;
    JSONArray jsonArray;
    Button createBtn;
    String currentPr, itemDescription, itemName, uomId;
    HelveticaBold textViewDate;
    String newDate, currentVendorId;
    PreferenceManager pm;

    private List<PurchaseReceiptCreateNewList> purchaseList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SelectRequisitionAdapter purchaseAdapter;

    PurchaseReceiptCreateNewList items;
    String currentPo, currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_purchase_requisition_items);

        createBtn = (Button) findViewById(R.id.createBtn);

        pm = new PreferenceManager(this);
        currentPr = pm.getString("currentPr");

        currentVendorId = pm.getString("vendorId");

        currentPo = getIntent().getStringExtra("currentPo");

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        currentDate = dateFormat.format(cal.getTime());

        jsonArray = new JSONArray();
        poJsonObject = new JSONObject();
        itemsJsonObject = new JSONObject();

        currentPoNo = pm.getString("currentPo");
        currentProjectId = pm.getString("projectId");

        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            CoordinatorLayout main_content = (CoordinatorLayout) findViewById(R.id.main_content);
            Snackbar snackbar = Snackbar.make(main_content, getResources().getString(R.string.no_internet_error), Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        pDialog = new ProgressDialog(SelectPurchaseRequisitionItems.this);
        pDialog.setMessage("Getting Data ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();


        class MyTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected void onPreExecute() {
                purchaseAdapter = new SelectRequisitionAdapter(purchaseList);
                recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                recyclerView.setLayoutManager(new LinearLayoutManager(SelectPurchaseRequisitionItems.this));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(purchaseAdapter);
            }

            @Override
            protected Void doInBackground(Void... params) {
                prepareItemsInRequisition();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                purchaseAdapter.notifyDataSetChanged();
            }

        }

        new MyTask().execute();

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(jsonArray.isNull(0))
                {
                    Toast.makeText(SelectPurchaseRequisitionItems.this, "Select at least one Item", Toast.LENGTH_SHORT).show();
                }
                else
                {

                    Log.d("JSON ARRAY OF ITEMS", jsonArray.toString());

                        sendJsonObject(jsonArray);


                }
            }
        });
    }

    public void sendJsonObject(JSONArray jsonArray)
    {
        RequestQueue requestQueue = Volley.newRequestQueue(SelectPurchaseRequisitionItems.this);

        String url = getResources().getString(R.string.server_url) + "/postPurchaseLineItems";

        JSONObject tempJsonObj = new JSONObject();
        Boolean moveToNextActivity = false;

        for(int i=0; i<jsonArray.length(); i++)
        {
            try {
                tempJsonObj = jsonArray.getJSONObject(i);
                Log.d("temp JSON Obj : ", tempJsonObj.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if((i+1)==jsonArray.length()) {
                moveToNextActivity = true;
            }

            sendRequestToServer(requestQueue, url, tempJsonObj , moveToNextActivity);
        }


    }

    public void sendRequestToServer(RequestQueue requestQueue, String url, JSONObject jSONObject, final Boolean moveToNext)
    {
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, jSONObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if(response.getString("msg").equals("success"))
                            {
                                Toast.makeText(SelectPurchaseRequisitionItems.this, "Purchase Order Line Items have been added ", Toast.LENGTH_SHORT).show();
                                if(moveToNext)
                                {

                                    Intent intent = new Intent(SelectPurchaseRequisitionItems.this, PurchaseOrderLineItems.class);
                                    pm.putString("poNumber",currentPoNo);
                                    pm.putString("createdOn",currentDate);
                                    pm.putString("vendorCode",currentVendorId);
                                    startActivity(intent);


                                    //update that pr is converted to po with line items


                                    updatePRisPO(currentPr,currentPo, pDialog, getApplicationContext());


                                }
                            }
                            else
                            {
                                Toast.makeText(SelectPurchaseRequisitionItems.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
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

    public void updatePRisPO(final String purReqId, final String poId, final ProgressDialog pDialog, Context context)
    {
        JSONObject object = new JSONObject();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(c.getTime());

        try {
            object.put("isPo", "1");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        String url = context.getResources().getString(R.string.server_url) + "/putIsPo?id="+ purReqId;

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.PUT, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("msg").equals("success"))
                            {

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



    public void prepareLineItems()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = getResources().getString(R.string.server_url) + "/getItems?projectId='"+currentProjectId+"'";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(SelectPurchaseRequisitionItems.this, response.getString("msg"), Toast.LENGTH_SHORT).show();

                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);

                                    if(itemIdArray!=null)
                                    {
                                        for(int j=0; j<itemIdArray.length; j++)
                                        {
                                            itemId =  dataObject.getString("itemId");
                                            itemName =  dataObject.getString("itemName");
                                            itemDescription =  dataObject.getString("itemDescription");
                                            uomId =  dataObject.getString("uomId");

                                            if(itemId.equals(itemIdArray[j]))
                                            {
                                                items = new PurchaseReceiptCreateNewList(itemId, itemName, itemDescription,
                                                        uomId, quantityArray[j]);
                                                purchaseList.add(items);

                                                purchaseAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                    else
                                    {
                                        Toast.makeText(SelectPurchaseRequisitionItems.this, "No Items found in this Purchase Requisition", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SelectPurchaseRequisitionItems.this, AllPurchaseRequisition.class);
                                        startActivity(intent);
                                    }
                                }
                            }

                            pDialog.dismiss();
                        }catch(JSONException e){
                            pDialog.dismiss();
                            e.printStackTrace();}
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        Log.e("Volley","Error");

                    }
                }
        );
        requestQueue.add(jor);
        if(pDialog!=null)
            pDialog.dismiss();
    }


    public class SelectRequisitionAdapter extends RecyclerView.Adapter<SelectRequisitionAdapter.MyViewHolder> {

        private List<PurchaseReceiptCreateNewList> list;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public HelveticaRegular item_id, po_quantity, text_total_cost, item_name, uom_id, text_unit_cost;
            CheckBox checkBox;
            CardView cardview;
            JSONObject jsonObject;
            HelveticaBold btn_update_unit_cost, need_by_date;

            TextView item_desc;

            public MyViewHolder(final View view) {
                super(view);
                newDate = "";
                item_id = (HelveticaRegular) view.findViewById(R.id.item_id);
                po_quantity = (HelveticaRegular) view.findViewById(R.id.po_quantity);
                text_total_cost = (HelveticaRegular) view.findViewById(R.id.text_total_cost);
                item_name = (HelveticaRegular) view.findViewById(R.id.item_name);
                uom_id = (HelveticaRegular) view.findViewById(R.id.uom_id);
                text_unit_cost = (HelveticaRegular) view.findViewById(R.id.text_unit_cost);

                item_desc = (TextView) view.findViewById(R.id.item_desc);

                btn_update_unit_cost = (HelveticaBold) view.findViewById(R.id.btn_update_unit_cost);
                need_by_date = (HelveticaBold) view.findViewById(R.id.need_by_date);

                need_by_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        textViewDate = need_by_date;
                        showDatePicker(view.getContext());
                    }
                });

                btn_update_unit_cost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                        alert.setTitle("Enter Unit Cost of Item : " + item_id.getText().toString());
                        // Set an EditText view to get user input
                        final EditText input = new EditText(view.getContext());
                        input.setMaxLines(1);
                        input.setInputType(InputType.TYPE_CLASS_NUMBER);
                        input.setImeOptions(EditorInfo.IME_ACTION_DONE);
                        input.setHint(text_unit_cost.getText().toString());
                        alert.setView(input);
                        alert.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                if(input.getText().toString().isEmpty())
                                {
                                    input.setError("Enter Accepted Quantity for ITEM ID : " + item_id.getText().toString());
                                }
                                else if(Integer.parseInt(input.getText().toString()) <1)
                                {
                                    Toast.makeText(view.getContext(), "Minimum quantity accepted should be 1", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    //calculate total cost of item
                                    text_unit_cost.setText(input.getText().toString());
                                    int totalCostVal = Integer.parseInt(po_quantity.getText().toString()) * Integer.parseInt(input.getText().toString());
                                    text_total_cost.setText(String.valueOf(totalCostVal));
                                }
                            }
                        });
                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //do on cancellation
                            }
                        });
                        alert.show();
                    }
                });

                jsonObject = new JSONObject();

                checkBox = (CheckBox) view.findViewById(R.id.checkBox);

                cardview = (CardView) view.findViewById(R.id.cardview);

                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {

                            if (checkBox.isChecked())
                            {
                                jsonObject.put("itemName", item_name.getText().toString());
                                jsonObject.put("poQuantity", po_quantity.getText().toString().isEmpty()? "0" : po_quantity.getText().toString());
                                jsonObject.put("itemDescription", item_desc.getText().toString());
                                jsonObject.put("uomId", uom_id.getText().toString());
                                jsonObject.put("unitCost", text_unit_cost.getText().toString().isEmpty()? "0" : text_unit_cost.getText().toString());
                                jsonObject.put("totalAmount", text_total_cost.getText().toString().isEmpty()? "0" : text_total_cost.getText().toString());
                                jsonObject.put("needByDate", need_by_date.getText().toString());
                                jsonObject.put("purchaseOrderId", currentPoNo);

                                jsonArray.put(jsonObject);

                                cardview.setBackgroundColor(view.getContext().getResources().getColor(R.color.simple_blue));
                                btn_update_unit_cost.setEnabled(false);
                                btn_update_unit_cost.setTextColor(getResources().getColor(R.color.new_grey));

                                need_by_date.setEnabled(false);
                                need_by_date.setTextColor(getResources().getColor(R.color.new_grey));

                            }

                            else
                            {
                                JSONObject checkForRemovalObject = new JSONObject();

                                for(int i=0; i < jsonArray.length(); i++)
                                {

                                    checkForRemovalObject = jsonArray.getJSONObject(i);

                                    if(item_name.getText().toString().equals(checkForRemovalObject.getString("itemName")))
                                    {
                                        jsonArray.remove(i);
                                        break;
                                    }
                                }

                                cardview.setBackgroundColor(view.getContext().getResources().getColor(R.color.white));
                                btn_update_unit_cost.setEnabled(true);
                                btn_update_unit_cost.setTextColor(getResources().getColor(R.color.colorPrimary));

                                need_by_date.setEnabled(true);
                                need_by_date.setTextColor(getResources().getColor(R.color.colorPrimary));
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

        public SelectRequisitionAdapter(List<PurchaseReceiptCreateNewList> list) {
            this.list = list;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_items_inside_pr_to_po, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            PurchaseReceiptCreateNewList items = list.get(position);

            holder.item_id.setText(String.valueOf(items.getItem_id()));
            holder.po_quantity.setText(items.getQuantity());
            holder.item_name.setText(items.getItem_name());
            holder.uom_id.setText(String.valueOf(items.getUom_id()));
            holder.text_unit_cost.setText("0");
            holder.item_desc.setText(items.getItem_desc());

            int newQuantityVal = 0, unitCostVal = 0;

            //calculate total cost of item
            if(holder.po_quantity.getText().toString().isEmpty())
                newQuantityVal = 0;

            if(holder.text_unit_cost.getText().toString().isEmpty())
                unitCostVal = 0;

            int totalCostVal = newQuantityVal * unitCostVal;
            holder.text_total_cost.setText(String.valueOf(totalCostVal));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    private void showDatePicker(Context context) {
        DatePickerFragment date = new DatePickerFragment();
        /**
         * Set Up Current Date Into dialog
         */
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);
        /**
         * Set Call back to capture selected date
         */
        date.setCallBack(ondate);
        date.show(((Activity) context).getFragmentManager(), "Date Picker");

    }

    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            newDate = String.valueOf(year) + "-" + String.valueOf(monthOfYear+1)
                    + "-" + String.valueOf(dayOfMonth);

            textViewDate.setText(newDate);
        }
    };

    public void prepareItemsInRequisition()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = getResources().getString(R.string.server_url) + "/getPurchaseRequisitionItem?purchaseRequisitionId=\""+currentPr+"\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                itemIdArray = new String[dataArray.length()];
                                quantityArray = new String[dataArray.length()];

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    itemId = dataObject.getString("itemId");
                                    quantity = dataObject.getString("quantity");

                                    itemIdArray[i] = itemId;
                                    quantityArray[i] = quantity;
                                }

                                prepareLineItems();

                            }

                            if(response.getString("msg").equals("No data"))
                            {
                                final LinearLayout main_layout = (LinearLayout) findViewById(R.id.main_layout);

                                Snackbar snackbar = Snackbar
                                        .make(main_layout, "No items inside this Purchase Requisition.", Snackbar.LENGTH_INDEFINITE)
                                        .setAction("CREATE LINE ITEM", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent intent = new Intent(SelectPurchaseRequisitionItems.this, PurchaseRequisition.class);
                                                intent.putExtra("create", "yes");
                                                startActivity(intent);
                                            }
                                        });

                                snackbar.show();
                            }
                            pDialog.dismiss();


                        }catch(JSONException e){e.printStackTrace();;}
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
        if(pDialog!=null)
            pDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SelectPurchaseRequisitionItems.this, PurchaseOrders.class);
        startActivity(intent);
    }
}
