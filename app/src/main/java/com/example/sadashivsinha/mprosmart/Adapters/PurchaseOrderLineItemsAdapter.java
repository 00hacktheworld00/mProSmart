package com.example.sadashivsinha.mprosmart.Adapters;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.Activities.PurchaseOrderLineItems;
import com.example.sadashivsinha.mprosmart.ModelLists.PurchaseOrderLineItemList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.DatePickerFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by saDashiv sinha on 17-May-16.
 */
public class PurchaseOrderLineItemsAdapter extends RecyclerView.Adapter<PurchaseOrderLineItemsAdapter.MyViewHolder> {

    private List<PurchaseOrderLineItemList> purchaseOrdersLineItemList;
    public String currentPoNo, currentProjectNo;
    JSONArray dataArray;
    JSONObject dataObject;
    Context mContext;
    TextView textViewDate;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView text_item_id, lineItemOldTotal, text_need_by_date;
        public EditText text_item_name, text_item_desc, text_uom, text_quantity, text_total_amount, text_unit_cost;
        public ImageButton editBtn, deleteBtn, editDone, editCancel;
        public RelativeLayout editOnLayout, editOffLayout;
        public ProgressDialog pDialog;

        public MyViewHolder(final View view) {
            super(view);
            text_item_id = (TextView) view.findViewById(R.id.text_item_id);
            lineItemOldTotal = (TextView) view.findViewById(R.id.lineItemOldTotal);

            text_item_name = (EditText) view.findViewById(R.id.text_item_name);
            text_item_desc = (EditText) view.findViewById(R.id.text_item_desc);
            text_uom = (EditText) view.findViewById(R.id.text_uom);
            text_quantity = (EditText) view.findViewById(R.id.text_quantity);
            text_total_amount = (EditText) view.findViewById(R.id.text_total_amount);
            text_unit_cost = (EditText) view.findViewById(R.id.text_unit_cost);
            text_need_by_date = (TextView) view.findViewById(R.id.text_need_by_date);

            editBtn = (ImageButton) view.findViewById(R.id.editBtn);
            deleteBtn = (ImageButton) view.findViewById(R.id.deleteBtn);
            editDone = (ImageButton) view.findViewById(R.id.editDone);
            editCancel = (ImageButton) view.findViewById(R.id.editCancel);

            editOnLayout = (RelativeLayout) view.findViewById(R.id.editOnLayout);
            editOffLayout = (RelativeLayout) view.findViewById(R.id.editOffLayout);

            editOnLayout.setVisibility(View.GONE);

            final PreferenceManager pm = new PreferenceManager(view.getContext());
            currentPoNo = pm.getString("poNumber");
            currentProjectNo = pm.getString("projectId");



            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        text_quantity.setEnabled(true);
                        text_need_by_date.setEnabled(true);

                        text_quantity.requestFocus();

                    TextWatcher watch = new TextWatcher(){

                        @Override
                        public void afterTextChanged(Editable arg0) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                                      int arg3) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int a, int b, int c) {
                            // TODO Auto-generated method stub
                            int quantityVal;
                            int unitCostVal;

                            if(!text_quantity.getText().toString().equals(""))
                            {
                                quantityVal = Integer.parseInt(text_quantity.getText().toString());
                                unitCostVal = Integer.parseInt(text_unit_cost.getText().toString());

                                text_total_amount.setText(String.valueOf(quantityVal*unitCostVal));
                            }
                        }};


                    text_quantity.addTextChangedListener(watch);




                        editOnLayout.setVisibility(View.VISIBLE);
                        editOffLayout.setVisibility(View.GONE);
                }
            });

            editDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(editOnLayout.getVisibility()==View.VISIBLE) {
                        text_uom.setEnabled(false);
                        text_quantity.setEnabled(false);
                        text_total_amount.setEnabled(false);
                        text_need_by_date.setEnabled(false);

                        if(text_uom.getText().toString().isEmpty())
                        {
                            text_uom.setError("Field cannot be empty");
                        }
                        else if(text_quantity.getText().toString().isEmpty())
                        {
                            text_quantity.setError("Field cannot be empty");
                        }
                        else if(text_total_amount.getText().toString().isEmpty())
                        {
                            text_total_amount.setError("Field cannot be empty");
                        }
                        else if(text_need_by_date.getText().toString().isEmpty())
                        {
                            text_need_by_date.setError("Field cannot be empty");
                        }
                        else
                        {
                            final Context context = itemView.getContext();
                            final String itemId = text_item_id.getText().toString();
                            final String uom = text_uom.getText().toString();
                            final String needByDate = text_need_by_date.getText().toString();
                            final String quantity = text_quantity.getText().toString();
                            final String amount = text_total_amount.getText().toString();
                            final String lineItemOldTotalText = lineItemOldTotal.getText().toString();

                            pDialog = new ProgressDialog(itemView.getContext());
                            pDialog.setMessage("Updating Data...");
                            pDialog.setIndeterminate(false);
                            pDialog.setCancelable(true);
                            pDialog.show();

                            class MyTask extends AsyncTask<Void, Void, Void>
                            {
                                @Override
                                protected Void doInBackground(Void... params)
                                {
                                    updatePoItemEditing(context, itemId, uom, quantity, lineItemOldTotalText, amount, needByDate, pDialog);
                                    return null;
                                }
                            }
                            new MyTask().execute();

                            editOnLayout.setVisibility(View.GONE);
                            editOffLayout.setVisibility(View.VISIBLE);
                        }


                    }
                }
            });

            editCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    text_uom.setEnabled(false);
                    text_quantity.setEnabled(false);

                    Snackbar snackbar = Snackbar.make(view, "EDIT Cancelled", Snackbar.LENGTH_SHORT);
                    snackbar.show();

                    editOnLayout.setVisibility(View.GONE);
                    editOffLayout.setVisibility(View.VISIBLE);
                }
            });

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final Context context =  itemView.getContext();
                    final String itemIdCurrent = text_item_id.getText().toString();

                    pDialog = new ProgressDialog(context);
                    confirmToDelete(context, itemIdCurrent, pDialog);
                }
            });
        }
    }

    public PurchaseOrderLineItemsAdapter(List<PurchaseOrderLineItemList> purchaseOrdersLineItemList) {
        this.purchaseOrdersLineItemList = purchaseOrdersLineItemList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_purchase_order_items, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        PurchaseOrderLineItemList items = purchaseOrdersLineItemList.get(position);

        holder.text_item_id.setText(String.valueOf(items.getItemId()));
        holder.text_item_name.setText(items.getItemName());
        holder.text_item_desc.setText(items.getItemDesc());
        holder.text_uom.setText(items.getItemUom());
        holder.text_quantity.setText(items.getItemQuantity());
        holder.text_total_amount.setText(items.getTotalAmount());
        holder.lineItemOldTotal.setText(items.getTotalAmount());
        holder.text_unit_cost.setText(items.getUnitCost());

        Date tradeDate = null;
        try {
            tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(items.getNeedByDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.text_need_by_date.setText(new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate));

        holder.text_need_by_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textViewDate = holder.text_need_by_date;
                mContext = holder.itemView.getContext();
                showDatePicker(mContext);

            }
        });

    }

    @Override
    public int getItemCount() {
        return purchaseOrdersLineItemList.size();
    }


    public void updatePoItemEditing(final Context context, String lineNo, String uom, final String quantity, final String lineItemOldTotalText, final String totalAmount, String needByDate,
                                    final ProgressDialog pDialog)
    {
        JSONObject object = new JSONObject();


        Date tradeDate = null;
        try {
            tradeDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(needByDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        needByDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(tradeDate);

        try {
            object.put("poQuantity", quantity);
            object.put("totalAmount", totalAmount);
            object.put("needByDate", needByDate);

            Log.d("totalQuantity - ", totalAmount);

            Log.d("tag", String.valueOf(object));

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        String url = context.getResources().getString(R.string.server_url) + "/putPurchaseOrderLine?purchaseLineItemsId=\""+ lineNo + "\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.PUT, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("updatePoItemEditing : ", response.toString());
                            if(response.getString("msg").equals("success"))
                            {
                                Toast.makeText(context, "Values Saved successfully", Toast.LENGTH_SHORT).show();
                                getPoTotal(context, lineItemOldTotalText, totalAmount, pDialog);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                        Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();

                    }
                }
        );
        requestQueue.add(jor);
    }

    public void getPoTotal(final Context context, final String lineItemOldTotalText, final String newTotal, final ProgressDialog pDialog)
    {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        String url = context.getString(R.string.server_url) + "/getPurchaseOrderTotal?purchaseOrderId='"+currentPoNo+"'";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("getPoTotal : ", response.toString());
                        try{
                            String oldAmount = null;

                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    oldAmount = dataObject.getString("totalAmount");

                                    Log.d("responseTotal : d",dataObject.getString("totalAmount"));

                                }

                                updateTotalQuantityOfPo(false, context, pDialog, newTotal, lineItemOldTotalText, Integer.parseInt(oldAmount));
                            }
                        }catch(JSONException e){e.printStackTrace();}
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

    public void updateTotalQuantityOfPo(Boolean whetherDelete, final Context context, final ProgressDialog pDialog, String newTotal, String lineItemOldTotalText, int oldAmount)
    {
        if(whetherDelete)
        {
            Log.d("oldAmount=", String.valueOf(oldAmount));
            Log.d("lineItemOldTotalText=", String.valueOf(lineItemOldTotalText));

            oldAmount = oldAmount - Integer.parseInt(lineItemOldTotalText);
            Log.d("ChangedoldAmount=", String.valueOf(oldAmount));
        }
        else
        {
            Log.d("oldAmount=", String.valueOf(oldAmount));
            Log.d("quantityOfItem=", String.valueOf(newTotal));
            Log.d("lineItemOldTotalText=", String.valueOf(lineItemOldTotalText));

            oldAmount = oldAmount + Integer.parseInt(newTotal) - Integer.parseInt(lineItemOldTotalText);
            Log.d("ChangedoldAmount=", String.valueOf(oldAmount));
        }

        JSONObject object = new JSONObject();

        try {
            object.put("totalAmount", oldAmount);

            Log.d("tag", String.valueOf(object));

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        String url = context.getResources().getString(R.string.server_url) + "/putPurchaseOrderTotal?purchaseOrderId=\""+ currentPoNo + "\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.PUT, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("getPoTotal", response.toString());
                            if(response.getString("msg").equals("success"))
                            {
                                pDialog.dismiss();
                                Intent intent = new Intent(context, PurchaseOrderLineItems.class);
                                context.startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                        pDialog.dismiss();
                        Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();

                    }
                }
        );
        requestQueue.add(jor);
        if(pDialog!=null)
            pDialog.dismiss();
    }

    public void deleteItem(final Context context, String itemId, final ProgressDialog pDialog)
    {
        pDialog.setMessage("Deleting Item...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();


        RequestQueue requestQueue = Volley.newRequestQueue(context);

        String url = context.getResources().getString(R.string.server_url) + "/deletePurchaseOrderLineItem?purchaseLineItemsId=\""+ itemId + "\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.DELETE, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("getPoTotal", response.toString());
                            if(response.getString("msg").equals("success"))
                            {
                                Toast.makeText(context, "Item Deleted", Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                                Intent intent = new Intent(context, PurchaseOrderLineItems.class);
                                context.startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                        pDialog.dismiss();
                        Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();

                    }
                }
        );
        requestQueue.add(jor);
        if(pDialog!=null)
            pDialog.dismiss();
    }

    public void confirmToDelete(final Context context, final String itemIdCurrent, final ProgressDialog pDialog)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Confirm Delete ITEM ? \n");
        alertDialogBuilder
                .setMessage("ITEM ID - "+itemIdCurrent)
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteItem(context, itemIdCurrent, pDialog);
                            }
                        })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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

            String newDate = String.valueOf(dayOfMonth) + "-" + String.valueOf(monthOfYear+1)
                    + "-" + String.valueOf(year);

            textViewDate.setText(newDate);
        }
    };

}
