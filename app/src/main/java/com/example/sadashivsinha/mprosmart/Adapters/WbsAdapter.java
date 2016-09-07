package com.example.sadashivsinha.mprosmart.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.Activities.ActivityCreate;
import com.example.sadashivsinha.mprosmart.ModelLists.ActivitiesInWbsList;
import com.example.sadashivsinha.mprosmart.ModelLists.WbsList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.CustomLinearLayoutManager;
import com.example.sadashivsinha.mprosmart.font.HelveticaBold;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saDashiv sinha on 02-Jul-16.
 */
public class WbsAdapter extends RecyclerView.Adapter<WbsAdapter.MyViewHolder> {

    private List<WbsList> wbsList;
    PreferenceManager pm;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView text_wbs_name, text_progress, progress_text_title, wbs_id, text_currency, text_budget;
        private RecyclerView recycler_activities;
        private Button add_activity;
        private HelveticaBold btn_view_attachment;

        public MyViewHolder(final View view) {
            super(view);
            text_wbs_name = (TextView) view.findViewById(R.id.text_wbs_name);
            text_progress = (TextView) view.findViewById(R.id.text_progress);
            progress_text_title = (TextView) view.findViewById(R.id.progress_text_title);
            wbs_id = (TextView) view.findViewById(R.id.wbs_id);
            text_currency = (TextView) view.findViewById(R.id.text_currency);
            text_budget = (TextView) view.findViewById(R.id.text_budget);

            add_activity = (Button) view.findViewById(R.id.add_activity);

            btn_view_attachment = (HelveticaBold) view.findViewById(R.id.btn_attachment);

            btn_view_attachment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProgressDialog progressDialog = new ProgressDialog(view.getContext());
                    progressDialog.setMessage("Getting image from server");
                    progressDialog.show();
                    viewAttachment(view.getContext(), wbs_id.getText().toString(), progressDialog);
                }
            });

            recycler_activities = (RecyclerView) view.findViewById(R.id.recycler_activities);

            pm = new PreferenceManager(view.getContext());

        }
    }
    public WbsAdapter(List<WbsList> wbsList) {
        this.wbsList = wbsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_wbs, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        WbsList items = wbsList.get(position);
        holder.text_wbs_name.setText(items.getText_wbs_name());
        holder.text_progress.setText(items.getText_progress());
        holder.wbs_id.setText(items.getWbs_id());
        holder.text_budget.setText(items.getTotal_budget());
        holder.text_currency.setText(items.getCurrency_code());

        if(Integer.parseInt(holder.text_progress.getText().toString())>=80 &&
                Integer.parseInt(holder.text_progress.getText().toString())<100)
        {
            holder.text_progress.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.material_light_yellow_800));
            holder.progress_text_title.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.material_light_yellow_800));
        }

        else if(Integer.parseInt(holder.text_progress.getText().toString())==100)
        {
            holder.text_progress.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.success_green));
            holder.progress_text_title.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.success_green));
        }


//        final ProgressDialog pDialog = new ProgressDialog(holder.itemView.getContext());
//        pDialog.setMessage("Getting all activities ...");
//        pDialog.setIndeterminate(false);
//        pDialog.setCancelable(true);
//        pDialog.show();

        ProgressDialog progressDialog;

        progressDialog = createProgressDialog(holder.itemView.getContext());
        progressDialog.show();


        List<ActivitiesInWbsList> activitiesInWbsList = new ArrayList<>();
        ActivitiesInWbsAdapter activitiesInWbsAdapter;
        Context context = holder.itemView.getContext();
        String currentWbsId = holder.wbs_id.getText().toString();

        activitiesInWbsAdapter = new ActivitiesInWbsAdapter(activitiesInWbsList);
        holder.recycler_activities.setLayoutManager(new CustomLinearLayoutManager(holder.itemView.getContext()));
        holder.recycler_activities.setAdapter(activitiesInWbsAdapter);

        prepareActivities(context, currentWbsId, progressDialog,  activitiesInWbsAdapter, activitiesInWbsList, holder.text_progress);


        holder.add_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(holder.itemView.getContext(), ActivityCreate.class);

                pm.putString("wbsId", holder.wbs_id.getText().toString());

                holder.itemView.getContext().startActivity(intent);

                 }
        });
    }

    @Override
    public int getItemCount() {
        return wbsList.size();
    }

    public void prepareItems(final Context context, String wbsId, final String progress, final LinearLayout hiddenLayout, final TextView text_progress)
    {

        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Updating Progress ...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.show();

        JSONObject object = new JSONObject();

        try {
            object.put("progress", progress);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        String url = context.getResources().getString(R.string.server_url) + "/putWbs?wbsId='"+ wbsId +"'";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.PUT, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();

                        hiddenLayout.setVisibility(View.GONE);
                        text_progress.setText(progress);
                        Toast.makeText(context, "Progress Saved", Toast.LENGTH_SHORT).show();

                        //response success message display
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                        Toast.makeText(context, "Error updating progress", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
        );
        requestQueue.add(jor);
    }

    public void prepareActivities(final Context context, final String currentWbsId, final ProgressDialog pDialog, final ActivitiesInWbsAdapter wbsActivityAdapter,
                                  final List<ActivitiesInWbsList> wbsActivityList , final TextView progressWbs)
    {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = context.getString(R.string.server_url) + "/getWbsActivity?wbsId=\""+currentWbsId+"\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{
                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                pDialog.dismiss();
                                Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            if(type.equals("INFO"))
                            {

                                JSONArray dataArray = response.getJSONArray("data");
                                JSONObject dataObject;
                                String id ,activityName ,progress ,startDate, endDate, resourceAllocated ,boq, status;

                                ActivitiesInWbsList items;

                                float averageWbsProgress = 0;

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);

                                    id = dataObject.getString("id");
                                    activityName = dataObject.getString("activityName");
                                    progress = dataObject.getString("progress");
                                    startDate = dataObject.getString("startDate");
                                    endDate = dataObject.getString("endDate");
                                    resourceAllocated = dataObject.getString("resourceAllocated");
                                    boq = dataObject.getString("boq");
                                    status = dataObject.getString("status");

                                    switch (status) {
                                        case "1":
                                            status = "Yet to start";
                                            break;
                                        case "2":
                                            status = "In-Progress";
                                            break;
                                        case "3":
                                            status = "Completed";
                                            break;
                                        case "4":
                                            status = "Cancelled";
                                            break;
                                    }
                                    items = new ActivitiesInWbsList(id, activityName, progress, startDate, endDate,
                                        resourceAllocated, boq, status);
                                    wbsActivityList.add(items);
                                    wbsActivityAdapter.notifyDataSetChanged();

                                    averageWbsProgress = averageWbsProgress + Integer.parseInt(progress);
                                }

                                averageWbsProgress = averageWbsProgress/dataArray.length();
                                progressWbs.setText(String.valueOf(averageWbsProgress));
                            }
                            pDialog.dismiss();
                        }
                        catch(JSONException e){
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
        wbsActivityAdapter.notifyDataSetChanged();

    }

    public void viewAttachment(final Context context, String wbsId, final ProgressDialog pDialog)
    {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        final String url = context.getString(R.string.server_url) + "/getWbsFiles?wbsId=\""+wbsId+"\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{
                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                pDialog.dismiss();
                                Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            String imageUrl;

                            if(type.equals("INFO"))
                            {

                                Log.d("RESPONSE OF JSON :" , response.toString());

                                JSONArray dataArray = response.getJSONArray("data");
                                for(int i=0; i<dataArray.length();i++)
                                {
                                    JSONObject dataObject = dataArray.getJSONObject(i);

                                    imageUrl = dataObject.getString("url");
                                    if(!imageUrl.isEmpty())
                                    {
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(imageUrl));
                                        context.startActivity(browserIntent);
                                    }
                                    else
                                    {
                                        Toast.makeText(context, "No Attachment", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }
                            pDialog.dismiss();
                        }
                        catch(JSONException e){
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

    public static ProgressDialog createProgressDialog(Context mContext) {
        ProgressDialog dialog = new ProgressDialog(mContext);
        try {
            dialog.show();
        } catch (WindowManager.BadTokenException e) {

        }
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.progress_dialog);
        // dialog.setMessage(Message);
        return dialog;
    }
}
