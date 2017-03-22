package com.example.sadashivsinha.mprosmart.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.ModelLists.ApproveRejectList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.font.HelveticaMedium;
import com.example.sadashivsinha.mprosmart.font.HelveticaRegular;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by saDashiv sinha on 25-Aug-16.
 */
public class ApproveRejectAdapter extends RecyclerView.Adapter<ApproveRejectAdapter.ViewHolder> {

    private List<ApproveRejectList> approveRejectList;
    ProgressDialog pDialog;
    PreferenceManager pm;

    String id, department;

    String submittalRegistersId, priority, startDate, EndDate, Status;

    String purchaseOrderId, vendorId, totalAmount,acceptedQuantity, itemAndReceipt, approved;

    String projectId, projectName, projectDescription, currencyCode, addressLine1, addressLine2,
            city, state, country, createdBy, createddate;

    public ApproveRejectAdapter(List<ApproveRejectList> approveRejectList) {
        this.approveRejectList = approveRejectList;
    }

    @Override
    public ApproveRejectAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_approve_reject, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        ApproveRejectList items = approveRejectList.get(i);
        viewHolder.text_message.setText(String.valueOf(items.getText_message()));
        viewHolder.text_id.setText(String.valueOf(items.getText_id()));
        viewHolder.text_created_by.setText(String.valueOf(items.getText_created_by()));
        viewHolder.text_created_on.setText(String.valueOf(items.getText_created_on()));
        viewHolder.entityName.setText(String.valueOf(items.getEntityName()));
        viewHolder.entityTName.setText(String.valueOf(items.getEntityTName()));

    }

    @Override
    public int getItemCount() {
        return approveRejectList.size();
    }
//
//    public void addItem(String country) {
//        approveRejectList.add(country);
//
//
//
//
//        notifyItemInserted(approveRejectList.size());
//    }

    public String getCurrentId(int position)
    {
        ApproveRejectList items = approveRejectList.get(position);
        return items.getText_id();
    }

    public String getEntityName(int position)
    {
        ApproveRejectList items = approveRejectList.get(position);
        return items.getEntityName();
    }

    public String getEntityTable(int position)
    {
        ApproveRejectList items = approveRejectList.get(position);
        return items.getEntityTName();
    }

    public void removeItem(int position) {
        approveRejectList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, approveRejectList.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        HelveticaMedium text_message;
        HelveticaRegular text_id, text_created_by, text_created_on, entityName, entityTName;

        public ViewHolder(final View view) {
            super(view);

            pm = new PreferenceManager(view.getContext());

            text_message = (HelveticaMedium)view.findViewById(R.id.text_message);

            text_id = (HelveticaRegular)view.findViewById(R.id.text_id);
            text_created_by = (HelveticaRegular)view.findViewById(R.id.text_created_by);
            text_created_on = (HelveticaRegular)view.findViewById(R.id.text_created_on);
            entityName = (HelveticaRegular)view.findViewById(R.id.entityName);
            entityTName = (HelveticaRegular)view.findViewById(R.id.entityTName);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    pDialog = new ProgressDialog(view.getContext());
                    pDialog.setMessage("Getting Details ...");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(true);
                    pDialog.show();

                    final Context context = view.getContext();

                    final String entityId = text_id.getText().toString();
                    final String entityNameText = entityName.getText().toString();
                    final String entityTableText = entityTName.getText().toString();

                    Log.d("entity id : " , entityId);
                    Log.d("entity name : " , entityNameText);
                    Log.d("entity table : " , entityTableText);

                    class MyTask extends AsyncTask<Void, Void, Void> {

                        @Override
                        protected Void doInBackground(Void... params) {
                            getDetailsOfNotification(context, entityId, entityNameText, entityTableText);
                            return null;
                        }

                    }
                    new MyTask().execute();
                }
            });
        }
    }


    public void getDetailsOfNotification(final Context context, String entityId, String entityNameText, final String entityTableText)
    {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        String url = pm.getString("SERVER_URL")  +
                "/notifyDetails?id="+ entityId +"&entityName="+ entityNameText +"&entityTName="+ entityTableText;

        if(entityNameText.equals("purchaseOrderId"))
        {
            JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try{

                                Log.d("entity response : " , response.toString());

                                String msg = response.getString("msg");

                                if(msg.equals("success"))
                                {
                                    JSONArray dataArray = response.getJSONArray("data");
                                    Log.d("response approval : ", response.toString());

                                    for(int i=0; i<dataArray.length();i++)
                                    {
                                        JSONObject dataObject = dataArray.getJSONObject(i);
                                        purchaseOrderId = dataObject.getString("purchaseOrderId");
                                        projectId = dataObject.getString("projectId");
                                        vendorId = dataObject.getString("vendorId");
                                        totalAmount = dataObject.getString("totalAmount");
                                        acceptedQuantity = dataObject.getString("acceptedQuantity");
                                        itemAndReceipt = dataObject.getString("itemAndReceipt");
                                        approved = dataObject.getString("approved");
                                        createdBy = dataObject.getString("createdBy");
                                        createddate = dataObject.getString("createdDate");
                                    }


                                    Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(createddate);
                                    createddate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                    final MaterialDialog mMaterialDialog = new MaterialDialog(context);

                                    mMaterialDialog.setPositiveButton("OK", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mMaterialDialog.dismiss();
                                        }
                                    });
                                    mMaterialDialog.setMessage("PO ID : " + purchaseOrderId+ " \nPROJECT ID : " + projectId+ "\nVENDOR ID : " + vendorId+
                                            "\nTOTAL AMOUNT : " + totalAmount+
                                             "\nAPPROVAL : " + "PENDING" + "\nCREATED BY : " + createdBy+
                                            "\nCREATED DATE : " + createddate);


                                    mMaterialDialog.show();


                                    pDialog.dismiss();
                                }
                                pDialog.dismiss();
                            }catch(JSONException e){e.printStackTrace();
                                pDialog.dismiss();} catch (ParseException e) {
                                e.printStackTrace();
                            }
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

        else if(entityNameText.equals("submittalRegistersId"))
        {
            JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try{

                                Log.d("entity response : " , response.toString());

                                String msg = response.getString("msg");

                                if(msg.equals("success"))
                                {
                                    JSONArray dataArray = response.getJSONArray("data");
                                    Log.d("response approval : ", response.toString());

                                    for(int i=0; i<dataArray.length();i++)
                                    {
                                        JSONObject dataObject = dataArray.getJSONObject(i);
                                        submittalRegistersId = dataObject.getString("submittalRegistersId");
                                        projectId = dataObject.getString("projectId");
                                        projectDescription = dataObject.getString("Description");
                                        priority = dataObject.getString("priority");
                                        startDate = dataObject.getString("startDate");
                                        EndDate = dataObject.getString("EndDate");
                                        Status = dataObject.getString("Status");
                                        createdBy = dataObject.getString("createdBy");
                                        createddate = dataObject.getString("createdDate");
                                    }
                                    Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(createddate);
                                    createddate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                    tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(startDate);
                                    startDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                    tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(EndDate);
                                    EndDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                    final MaterialDialog mMaterialDialog = new MaterialDialog(context);

                                    mMaterialDialog.setPositiveButton("OK", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mMaterialDialog.dismiss();
                                        }
                                    });
                                    mMaterialDialog.setMessage("SUBMITTAL REGISTER ID : " + submittalRegistersId+ " \nPROJECT ID : " + projectId+ "\nPROJECT DESCRIPTION : " + projectDescription+
                                            "\nPRIORITY : " + priority+  "\nSTART DATE : " + startDate +
                                            "\nEND DATE : " + EndDate+  "\nSTATUS : " + Status + "\nCREATED BY : " + createdBy+
                                            "\nCREATED DATE : " + createddate);


                                    mMaterialDialog.show();


                                    pDialog.dismiss();
                                }
                                pDialog.dismiss();
                            }catch(JSONException e){e.printStackTrace();
                                pDialog.dismiss();} catch (ParseException e) {
                                e.printStackTrace();
                            }
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
        else if(entityTableText.equals("purchaseRequisition"))
        {
            JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try{

                                Log.d("entity response : " , response.toString());

                                String msg = response.getString("msg");

                                if(msg.equals("success"))
                                {
                                    JSONArray dataArray = response.getJSONArray("data");
                                    Log.d("response approval : ", response.toString());

                                    for(int i=0; i<dataArray.length();i++)
                                    {
                                        JSONObject dataObject = dataArray.getJSONObject(i);
                                        id = dataObject.getString("id");
                                        projectId = dataObject.getString("projectId");
                                        department = dataObject.getString("department");
                                        createdBy = dataObject.getString("createdBy");
                                        createddate = dataObject.getString("createdDate");
                                    }

                                    Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(createddate);
                                    createddate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                    final MaterialDialog mMaterialDialog = new MaterialDialog(context);

                                    mMaterialDialog.setPositiveButton("OK", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mMaterialDialog.dismiss();
                                        }
                                    });
                                    mMaterialDialog.setMessage("PURCHASE REQUISITION ID : " + id+ " \nPROJECT ID : " + projectId+ "\nDEPARTMENT : " + department+
                                            "\nCREATED BY : " + createdBy+
                                            "\nCREATED DATE : " + createddate);


                                    mMaterialDialog.show();


                                    pDialog.dismiss();
                                }
                                pDialog.dismiss();
                            }catch(JSONException e){e.printStackTrace();
                                pDialog.dismiss();} catch (ParseException e) {
                                e.printStackTrace();
                            }
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

        else {
            JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try{

                                Log.d("entity response : " , response.toString());

                                String msg = response.getString("msg");

                                if(msg.equals("success"))
                                {
                                    JSONArray dataArray = response.getJSONArray("data");
                                    Log.d("response approval : ", response.toString());

                                    for(int i=0; i<dataArray.length();i++)
                                    {
                                        JSONObject dataObject = dataArray.getJSONObject(i);
                                        projectId = dataObject.getString("projectId");
                                        projectName = dataObject.getString("projectName");
                                        projectDescription = dataObject.getString("projectDescription");
                                        currencyCode = dataObject.getString("currencyCode");
                                        addressLine1 = dataObject.getString("addressLine1");
                                        addressLine2 = dataObject.getString("addressLine2");
                                        city = dataObject.getString("city");
                                        state = dataObject.getString("state");
                                        country = dataObject.getString("country");
                                        createdBy = dataObject.getString("createdBy");
                                        createddate = dataObject.getString("createddate");
                                    }

                                    Date tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(createddate);
                                    createddate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

                                    final MaterialDialog mMaterialDialog = new MaterialDialog(context);

                                    mMaterialDialog.setPositiveButton("OK", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mMaterialDialog.dismiss();
                                        }
                                    });
                                    mMaterialDialog.setMessage("PROJECT ID : " + projectId+ " \nPROJECT NAME : " + projectName+ "\nPROJECT DESCRIPTION : " + projectDescription+
                                            "\nCURRENCY CODE : " + currencyCode+  "\nADDRESS #1 : " + addressLine1 +
                                            "\nADDRESS #2 : " + addressLine2+  "\nCITY #1 : " + city + "\nSTATE #2 : " + state+  "\nCOUNTRY : " + country +  "\nCREATED BY : " + createdBy +
                                            "\nCREATED DATE : " + createddate);


                                    mMaterialDialog.show();


                                    pDialog.dismiss();
                                }
                                pDialog.dismiss();
                            }catch(JSONException e){e.printStackTrace();
                                pDialog.dismiss();} catch (ParseException e) {
                                e.printStackTrace();
                            }
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
    }
}
