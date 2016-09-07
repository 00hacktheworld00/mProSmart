package com.example.sadashivsinha.mprosmart.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.Activities.AllPurchaseRequisition;
import com.example.sadashivsinha.mprosmart.Activities.PurchaseRequisition;
import com.example.sadashivsinha.mprosmart.Activities.SelectPurchaseRequisitionItems;
import com.example.sadashivsinha.mprosmart.ModelLists.AllPurchaseRequisitionList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by saDashiv sinha on 01-Jul-16.
 */
public class AllPurchaseRequisitionAdapter extends RecyclerView.Adapter<AllPurchaseRequisitionAdapter.MyViewHolder> {

    private List<AllPurchaseRequisitionList> purchaseList;
    View dialogView;
    AlertDialog show;
    JSONArray dataArray;
    JSONObject dataObject;
    String[] vendorIdArray;
    String vendorId;
    PreferenceManager pm;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView pr_sl_no, text_pr_no,  text_department, text_created_on, text_created_by;
        Button convert_po_btn;

        String prSlNo, prNo, department, createdOn, createdBy;

        public MyViewHolder(final View view) {
            super(view);
            pr_sl_no = (TextView) view.findViewById(R.id.pr_sl_no);
            text_pr_no = (TextView) view.findViewById(R.id.text_pr_no);
            text_department = (TextView) view.findViewById(R.id.text_department);
            text_created_on = (TextView) view.findViewById(R.id.text_created_on);
            text_created_by = (TextView) view.findViewById(R.id.text_created_by);

            convert_po_btn = (Button) view.findViewById(R.id.convert_po_btn);

            convert_po_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(convert_po_btn.getText().toString().equals("Waiting for approval"))
                    {
                        Toast.makeText(view.getContext(), "Purchase Requisition is not approved at this moment.", Toast.LENGTH_SHORT).show();
                    }
                    else if(convert_po_btn.getText().toString().equals("Rejected"))
                    {
                        Toast.makeText(view.getContext(), "Purchase Requisition is rejected", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        //convert to PO
                        dialogVendor(view.getContext(), text_pr_no.getText().toString());
                    }
                }
            });

            pm = new PreferenceManager(view.getContext());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(view.getContext(), PurchaseRequisition.class);

                    prSlNo = pr_sl_no.getText().toString();
                    prNo = text_pr_no.getText().toString();
                    department = text_department.getText().toString();
                    createdOn = text_created_on.getText().toString();
                    createdBy = text_created_by.getText().toString();

                    //passing the values for header on next page
                    pm.putString("currentPr", prNo);
                    pm.putString("departmentPr", department);
                    pm.putString("createdOnPr", createdOn);
                    pm.putString("createdByPr", createdBy);

                    view.getContext().startActivity(intent);
                }
            });
        }
    }
    public AllPurchaseRequisitionAdapter(List<AllPurchaseRequisitionList> purchaseList) {
        this.purchaseList = purchaseList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_all_purchase_requisition, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        AllPurchaseRequisitionList items = purchaseList.get(position);
        holder.pr_sl_no.setText(String.valueOf(items.getPr_sl_no()));
        holder.text_pr_no.setText(String.valueOf(items.getText_pr_no()));
        holder.text_department.setText(String.valueOf(items.getText_department()));
        holder.text_created_on.setText(String.valueOf(items.getText_created_on()));
        holder.text_created_by.setText(String.valueOf(items.getText_created_by()));

        if(items.getApproved().equals("1"))
        {
            holder.convert_po_btn.setBackgroundResource(R.color.success_green);
            holder.convert_po_btn.setText("Convert to PO");
        }
        else if(items.getApproved().equals("2"))
        {
            holder.convert_po_btn.setBackgroundResource(R.color.fancy_red);
            holder.convert_po_btn.setText("Rejected");
        }

        else
        {
            holder.convert_po_btn.setBackgroundResource(R.color.new_grey);
            holder.convert_po_btn.setText("Waiting for approval");
        }

        if(items.getIsPo().equals("1"))
        {
            holder.convert_po_btn.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return purchaseList.size();
    }

    public void dialogVendor(final Context context, final String prNo)
    {
        final AlertDialog.Builder alert = new AlertDialog.Builder(context,android.R.style.Theme_Translucent_NoTitleBar);
        // Set an EditText view to get user input

        dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_select_vendor, null);
        alert.setView(dialogView);

        show = alert.show();

        TextView title = (TextView) dialogView.findViewById(R.id.title);
        title.setText("Select Vendor for Purchase Requisition - "+ prNo);

        Button proceedBtn = (Button) dialogView.findViewById(R.id.proceedBtn);

        final Spinner spinner_vendor = (Spinner) dialogView.findViewById(R.id.spinner_vendor);

        final ProgressDialog pDialog = new ProgressDialog(dialogView.getContext());
        pDialog.setMessage("Getting Items...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        class MyTask extends AsyncTask<Void, Void, Void>
        {
            @Override
            protected Void doInBackground(Void... params)
            {
                prepareVendors(pDialog, context, spinner_vendor);
                return null;
            }
        }

        new MyTask().execute();

        proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spinner_vendor.getSelectedItem().toString().equals("Select Vendor"))
                {
                    Toast.makeText(context, "Select Vendor First", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    final ProgressDialog pDialog = new ProgressDialog(dialogView.getContext());
                    pDialog.setMessage("Getting Vendors...");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(true);
                    pDialog.show();

                    final String currentProject = pm.getString("projectId");
                    final String currentUser = pm.getString("userId");

                    final String vendorId = spinner_vendor.getSelectedItem().toString();

                    class MyTask extends AsyncTask<Void, Void, Void>
                    {
                        @Override
                        protected Void doInBackground(Void... params)
                        {
                            convertPrToPo(context, vendorId, prNo, pDialog, currentProject, currentUser);
                            return null;
                        }
                    }

                    new MyTask().execute();
                }
            }
        });

    }

    public void convertPrToPo(final Context context, final String vendorId, final String prNo, final ProgressDialog pDialog,
                              String currentProject, final String currentUser)
    {
        JSONObject object = new JSONObject();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String strDate = sdf.format(c.getTime());

        try {
            object.put("projectId", currentProject);
            object.put("vendorId",vendorId);
            object.put("createdBy",currentUser);
            object.put("createdDate",strDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        String url =context.getResources().getString(R.string.server_url) + "/postPurchaseOrder";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("msg").equals("success"))
                            {
                                pm.putString("currentPr", prNo);
                                pm.putString("currentPo", response.getString("data"));
                                pm.putString("vendorId", vendorId);

//                                updatePRisPO(prNo, response.getString("data"), pDialog, context);

                                pDialog.dismiss();
                                Intent intent = new Intent(dialogView.getContext(), SelectPurchaseRequisitionItems.class);
                                intent.putExtra("currentPo", response.getString("data"));
                                pm.putString("currentPr",prNo);

                                dialogView.getContext().startActivity(intent);
                            }
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
                        Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                    }
                }
        );
        requestQueue.add(jor);

    }

//    public void updatePRisPO(final String purReqId, final String poId, final ProgressDialog pDialog, Context context)
//    {
//        JSONObject object = new JSONObject();
//
//        Calendar c = Calendar.getInstance();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        String currentDate = sdf.format(c.getTime());
//
//        try {
//            object.put("isPo", "1");
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        RequestQueue requestQueue = Volley.newRequestQueue(context);
//
//        String url = context.getResources().getString(R.string.server_url) + "/putIsPo?id="+ purReqId;
//
//        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.PUT, url, object,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            if(response.getString("msg").equals("success"))
//                            {
//                                pDialog.dismiss();
//                                Intent intent = new Intent(dialogView.getContext(), SelectPurchaseRequisitionItems.class);
//                                intent.putExtra("currentPo", poId);
//                                pm.putString("currentPr",purReqId);
//                                dialogView.getContext().startActivity(intent);
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            pDialog.dismiss();
//                        }
//                        //response success message display
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.e("Volley", "Error");
//                        pDialog.dismiss();
//                    }
//                }
//        );
//        requestQueue.add(jor);
//        if(pDialog!=null)
//            pDialog.dismiss();
//    }

    public void prepareVendors(final ProgressDialog pDialog, final Context context, final Spinner vendorsSpinner)
    {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        String url = context.getResources().getString(R.string.server_url) + "/getVendors";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            String type = response.getString("type");
                            String msg = response.getString("msg");
                            Log.d("response->", String.valueOf(response));

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                            }

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                vendorIdArray = new String[dataArray.length()+1];
                                vendorIdArray[0]="Select Vendor";
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    vendorId = dataObject.getString("vendorId");

                                    vendorIdArray[i+1]=vendorId;
                                }
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                                        android.R.layout.simple_dropdown_item_1line,vendorIdArray);
                                vendorsSpinner.setAdapter(adapter);
                                pDialog.dismiss();
                            }

                            if(msg.equals("No data"))
                            {
                                Toast.makeText(context, "No Vendors Found.", Toast.LENGTH_LONG).show();
                                pDialog.dismiss();

                                Intent intent = new Intent(context, AllPurchaseRequisition.class);
                                context.startActivity(intent);
                            }
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

        if(pDialog!=null)
            pDialog.dismiss();
    }
}
