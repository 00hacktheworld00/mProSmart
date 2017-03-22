package com.example.sadashivsinha.mprosmart.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.Activities.AttachmentActivity;
import com.example.sadashivsinha.mprosmart.Activities.ExpenseManagement;
import com.example.sadashivsinha.mprosmart.ModelLists.BudgetList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by saDashiv sinha on 31-Aug-16.
 */
public class ExpenseAdapter  extends RecyclerView.Adapter<ExpenseAdapter.MyViewHolder> {

    private List<BudgetList> budgetList;
    PreferenceManager pm;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView line_no, text_wbs, text_activity, item_name, item_desc, quantity, uom, amount, sl_no, title_wbs,
                title_activity, line_no_to_show, text_expense_type_desc, text_amount_personal;
        private TextView btn_update_quantity, no_of_attachments;
        public ImageButton attachBtn;
        private EditText text_new_quantity;
        Context context;
        LinearLayout layout_project, layout_personal;

        String newAmount, newQuantity, lineNo, oldQuantity;
        ProgressDialog pDialog;
        int oldAmount;

        public MyViewHolder(final View view) {
            super(view);

            context = view.getContext();
            pm = new PreferenceManager(context);

            no_of_attachments = (TextView) view.findViewById(R.id.no_of_attachments);
            attachBtn = (ImageButton) view.findViewById(R.id.attachBtn);

            line_no = (TextView) view.findViewById(R.id.line_no);
            text_wbs = (TextView) view.findViewById(R.id.text_wbs);
            text_activity = (TextView) view.findViewById(R.id.text_activity);
            item_name = (TextView) view.findViewById(R.id.item_name);
            item_desc = (TextView) view.findViewById(R.id.item_desc);
            quantity = (TextView) view.findViewById(R.id.quantity);
            uom = (TextView) view.findViewById(R.id.uom);
            amount = (TextView) view.findViewById(R.id.amount);
            sl_no = (TextView) view.findViewById(R.id.sl_no);
            line_no_to_show = (TextView) view.findViewById(R.id.line_no_to_show);
            text_expense_type_desc = (TextView) view.findViewById(R.id.text_expense_type_desc);
            text_amount_personal = (TextView) view.findViewById(R.id.text_amount_personal);

            layout_project = (LinearLayout) view.findViewById(R.id.layout_project);
            layout_personal = (LinearLayout) view.findViewById(R.id.layout_personal);

            btn_update_quantity = (TextView) view.findViewById(R.id.btn_update_quantity);
            text_new_quantity = (EditText) view.findViewById(R.id.text_new_quantity);

            text_new_quantity.setVisibility(View.GONE);

            title_wbs = (TextView) view.findViewById(R.id.title_wbs);
            title_activity = (TextView) view.findViewById(R.id.title_activity);

            attachBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), AttachmentActivity.class);
                    String url = pm.getString("SERVER_URL") + "/getExpenseManagementLineFiles?expenseManagementLineId=\"" + line_no.getText().toString() + "\"";
                    Log.d("VIEW IMG URL : ", url);
                    intent.putExtra("viewURL", url);
                    intent.putExtra("viewOnly", true);
                    itemView.getContext().startActivity(intent);

                }
            });

            btn_update_quantity.setOnClickListener(new View.OnClickListener() {
                int count = 0;

                @Override
                public void onClick(View v) {

                    if(count==0)
                    {
                        text_new_quantity.setVisibility(View.VISIBLE);
                        btn_update_quantity.setText("Save New Quantity");
                        btn_update_quantity.setBackgroundColor(view.getResources().getColor(R.color.success_green));
                        count++;
                    }
                    else
                    {
                        if(text_new_quantity.getText().toString().isEmpty())
                        {
                            text_new_quantity.setError("Enter Quantity");
                        }
                        else if(Float.parseFloat(text_new_quantity.getText().toString())<=Float.parseFloat(quantity.getText().toString()))
                        {
                            text_new_quantity.setError("Quantity cannot be less than or equal to current quantity");
                        }
                        else
                        {
                            pDialog = new ProgressDialog(context);
                            pDialog.setMessage("Saving Data ...");
                            pDialog.setIndeterminate(false);
                            pDialog.setCancelable(true);
                            pDialog.show();

                            newAmount = amount.getText().toString();
                            oldQuantity = quantity.getText().toString();
                            newQuantity = text_new_quantity.getText().toString();
                            lineNo = line_no.getText().toString();
                            oldAmount = Integer.parseInt(newAmount);

                            class MyTask extends AsyncTask<Void, Void, Void> {

                                @Override
                                protected Void doInBackground(Void... params) {
                                    int totalNewAmount = (oldAmount * Integer.parseInt(newQuantity))/ Integer.parseInt(oldQuantity);
                                    updateQuantity(context, Integer.parseInt(newQuantity), totalNewAmount, oldAmount , lineNo, pDialog);
                                    return null;
                                }


                                @Override
                                protected void onPostExecute(Void result) {
                                    text_new_quantity.setVisibility(View.GONE);
                                    btn_update_quantity.setText("Update Quantity");
                                    btn_update_quantity.setBackgroundColor(view.getResources().getColor(R.color.baby_blue));
                                    count--;

                                }
                            }

                            new MyTask().execute();
                        }
                    }
                }
            });
        }

    }
    public ExpenseAdapter(List<BudgetList> budgetList) {
        this.budgetList = budgetList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_expense_new, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        BudgetList items = budgetList.get(position);
        holder.sl_no.setText(String.valueOf(items.getSl_no()));
        holder.line_no.setText(String.valueOf(items.getLine_no()));
        holder.item_name.setText(String.valueOf(items.getItem_name()));
        holder.item_desc.setText(String.valueOf(items.getItem_desc()));
        holder.quantity.setText(String.valueOf(items.getQuantity()));
        holder.uom.setText(String.valueOf(items.getUom()));
        holder.amount.setText(String.valueOf(items.getAmount()));
        holder.line_no_to_show.setText(String.valueOf(items.getSl_no()));
        holder.text_wbs.setText(String.valueOf(items.getText_wbs()));
        holder.text_activity.setText(String.valueOf(items.getText_activity()));
        holder.text_expense_type_desc.setText(String.valueOf(items.getItem_desc()));
        holder.text_amount_personal.setText(String.valueOf(items.getAmount()));
        holder.no_of_attachments.setText(String.valueOf(items.getNoOfAttachments()));

        if(!items.getExpenseType().isEmpty())
        {
            if(items.getExpenseType().equals("Personal"))
            {
                holder.layout_project.setVisibility(View.GONE);
            }
            else
            {
                holder.layout_personal.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public int getItemCount() {
        return budgetList.size();
    }

    public void updateQuantity(final Context context, final int newQuantity, final int totalNewAmount ,final int oldAmount, String currentLineId, final ProgressDialog pDialog)
    {

        JSONObject object = new JSONObject();

        try {
            object.put("quantity",String.valueOf(newQuantity));
            object.put("amount",String.valueOf(totalNewAmount));

            Log.d("REQUEST SENT OF JSON :" , object.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        String url = pm.getString("SERVER_URL")  + "/putExpenseManagementLineDetails?expenseManagementLineId=\"" +
                currentLineId +"\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.PUT, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            Log.d("SERVER RESPONSE :" , response.toString());

                            if(response.getString("msg").equals("success"))
                            {
                                Log.d("Updated New Quan", String.valueOf(newQuantity));
                                updateTotalExpense(totalNewAmount, oldAmount, pDialog, context);
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
                        pDialog.dismiss();
                    }
                }
        );
        requestQueue.add(jor);
    }

    public void updateTotalExpense(int totalAmount, int oldAmount, final ProgressDialog pDialog, final Context context)
    {
        int currentExpense = Integer.parseInt(pm.getString("expenseTotalExpense"));
        Log.d("TOTAL CURR EXPENSE", String.valueOf(currentExpense));

        totalAmount = totalAmount + currentExpense - oldAmount;

        final int totalNewAmount = totalAmount;

        Log.d("TOTAL NEW EXPENSE", String.valueOf(totalAmount));

        JSONObject object = new JSONObject();

        try {
            object.put("totalExpense",String.valueOf(totalAmount));

            Log.d("REQUEST SENT OF JSON :" , object.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        String currentExpenseId = pm.getString("currentExpense");

        String url = pm.getString("SERVER_URL")  + "/putExpenseManagwmentTotal?expenseManagementId=\"" +
                currentExpenseId +"\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.PUT, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            Log.d("SERVER RESPONSE :" , response.toString());

                            if(response.getString("msg").equals("success"))
                            {
                                Toast.makeText(context, "New Quantity has been Updated", Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();

                                pm.putString("expenseTotalExpense", String.valueOf(totalNewAmount));

                                Intent intent = new Intent(context, ExpenseManagement.class);
                                context.startActivity(intent);
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
