package com.example.sadashivsinha.mprosmart.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.Activities.PurchaseOrderLineItems;
import com.example.sadashivsinha.mprosmart.Activities.PurchaseReceiptsNew;
import com.example.sadashivsinha.mprosmart.ModelLists.PurchaseOrdersList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by saDashiv sinha on 10-Mar-16.
 */
public class PurchaseOrdersAdapter extends RecyclerView.Adapter<PurchaseOrdersAdapter.MyViewHolder> {

    private List<PurchaseOrdersList> purchaseOrdersList;
    String currentProjectNo;
    ProgressDialog pDialog;
    JSONArray dataArray;
    JSONObject dataObject;
    String[] itemArray;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView po_index, po_number, vendor_code, created_on, created_by, total_amount;
        public FancyButton btn_items, btn_receipt;

        public MyViewHolder(final View view) {
            super(view);
            po_index = (TextView) view.findViewById(R.id.po_index);
            po_number = (TextView) view.findViewById(R.id.po_number);
            vendor_code = (TextView) view.findViewById(R.id.vendor_code);
            created_on = (TextView) view.findViewById(R.id.created_on);
            created_by = (TextView) view.findViewById(R.id.created_by);
            total_amount = (TextView) view.findViewById(R.id.total_amount);

            btn_items = (FancyButton) view.findViewById(R.id.btn_items);
            btn_receipt = (FancyButton) view.findViewById(R.id.btn_receipt);

            final PreferenceManager pm = new PreferenceManager(view.getContext());
            currentProjectNo = pm.getString("projectId");

            btn_receipt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), PurchaseReceiptsNew.class);
                    String poNumber = po_number.getText().toString();
                    pm.putString("poNumber",poNumber);
                    pm.putString("createdOn",created_on.getText().toString());
                    pm.putString("vendorCode",vendor_code.getText().toString());
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }

    public PurchaseOrdersAdapter(List<PurchaseOrdersList> purchaseOrdersList) {
        this.purchaseOrdersList = purchaseOrdersList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_purchase_order_new, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        PurchaseOrdersList items = purchaseOrdersList.get(position);

        holder.po_index.setText(String.valueOf(items.getPo_index()));
        holder.po_number.setText(items.getPo_number());
        holder.vendor_code.setText(items.getVendor_code());
        holder.created_on.setText(items.getCreated_on());
        holder.created_by.setText(items.getCreated_by());
        holder.total_amount.setText(items.getTotal_amount());

//
//        pDialog = new ProgressDialog(holder.itemView.getContext());
//        pDialog.setMessage("Getting Details...");
//        pDialog.setIndeterminate(false);
//        pDialog.setCancelable(true);
//        pDialog.show();
//
//        final Context context = holder.itemView.getContext();
//        final String currentPoNo = holder.po_number.getText().toString();
//
//        class MyTask extends AsyncTask<Void, Void, Void>
//        {
//
//            @Override
//            protected Void doInBackground(Void... params)
//            {
//                prepareLineItems(context, currentPoNo);
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void result) {
//
//                if(itemArray==null)
//                {
//                    holder.btn_items.setText("Create Item");
//                    holder.btn_receipt.setVisibility(View.GONE);
//                }
//
//            }
//        }
//
//        new MyTask().execute();


        final PreferenceManager pm = new PreferenceManager(holder.itemView.getContext());

        holder.btn_items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.btn_items.getText().toString().equals("Create Item"))
                {
                    Intent intent = new Intent(holder.itemView.getContext(), PurchaseOrderLineItems.class);
                    String poNumber = holder.po_number.getText().toString();
                    pm.putString("poNumber",poNumber);
                    pm.putString("vendorCode",holder.vendor_code.getText().toString());
                    pm.putString("createdBy",holder.created_by.getText().toString());
                    pm.putString("createdOn",holder.created_on.getText().toString());
                    pm.putString("totalAmount",holder.total_amount.getText().toString());

                    intent.putExtra("createItem", "yes");

                    holder.itemView.getContext().startActivity(intent);
                }

                else
                {
                    Intent intent = new Intent(holder.itemView.getContext(), PurchaseOrderLineItems.class);
                    String poNumber = holder.po_number.getText().toString();
                    pm.putString("poNumber",poNumber);
                    pm.putString("vendorCode",holder.vendor_code.getText().toString());
                    pm.putString("createdBy",holder.created_by.getText().toString());
                    pm.putString("createdOn",holder.created_on.getText().toString());
                    holder.itemView.getContext().startActivity(intent);
                    pm.putString("totalAmount",holder.total_amount.getText().toString());
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return purchaseOrdersList.size();
    }


    public void prepareLineItems(final Context context, final String currentPoNo)
    {

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        String url = context.getResources().getString(R.string.server_url) + "/getPurchaseLineItems?purchaseOrderId='"+currentPoNo+"'";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();

                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                itemArray = new String[dataArray.length()];

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    itemArray[i]=  dataObject.getString("purchaseLineItemsId");
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
    }
}