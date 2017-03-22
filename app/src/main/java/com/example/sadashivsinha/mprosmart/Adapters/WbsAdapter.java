package com.example.sadashivsinha.mprosmart.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.Activities.ActivityCreate;
import com.example.sadashivsinha.mprosmart.Activities.AttachmentActivity;
import com.example.sadashivsinha.mprosmart.Activities.WbsActivity;
import com.example.sadashivsinha.mprosmart.ModelLists.ActivitiesInWbsList;
import com.example.sadashivsinha.mprosmart.ModelLists.WbsList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.AppController;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.example.sadashivsinha.mprosmart.Utils.CustomLinearLayoutManager;
import com.example.sadashivsinha.mprosmart.font.HelveticaBold;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by saDashiv sinha on 02-Jul-16.
 */
public class WbsAdapter extends RecyclerView.Adapter<WbsAdapter.MyViewHolder> {

    private List<WbsList> wbsList;
    PreferenceManager pm;
    ConnectionDetector cd;
    public static final String TAG = WbsActivity.class.getSimpleName();
    Boolean isInternetPresent = false;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView text_wbs_name, text_progress, progress_text_title, wbs_id, text_currency, text_budget;
        private RecyclerView recycler_activities;
        private Button add_activity;
        private HelveticaBold btn_view_attachment;

        public MyViewHolder(final View view) {
            super(view);

            pm = new PreferenceManager(view.getContext());

            cd = new ConnectionDetector(view.getContext());
            isInternetPresent = cd.isConnectingToInternet();

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
                    if(wbs_id.getText().toString().equals("-1"))
                    {
                        Toast.makeText(view.getContext(), "No Attachments", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {

                        Intent intent = new Intent(itemView.getContext(), AttachmentActivity.class);
                        String url = pm.getString("SERVER_URL") + "/getWbsFiles?wbsId=\""+ wbs_id.getText().toString() +"\"";

                        intent.putExtra("viewURL", url);
                        intent.putExtra("viewOnly", true);
                        itemView.getContext().startActivity(intent);
//                        viewAttachment(view.getContext(), wbs_id.getText().toString());
                    }
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

//        final ProgressDialog progressDialog = new ProgressDialog(holder.itemView.getContext());
//        progressDialog.setMessage("Getting all activities ...");
//        progressDialog.setIndeterminate(false);
//        progressDialog.setCancelable(true);
//        progressDialog.show();

//        ProgressDialog progressDialog;
//
//        progressDialog = createProgressDialog(holder.itemView.getContext());
//        progressDialog.show();

        List<ActivitiesInWbsList> activitiesInWbsList = new ArrayList<>();
        ActivitiesInWbsAdapter activitiesInWbsAdapter;
        Context context = holder.itemView.getContext();
        String currentWbsId = holder.wbs_id.getText().toString();

        activitiesInWbsAdapter = new ActivitiesInWbsAdapter(activitiesInWbsList);
        holder.recycler_activities.setLayoutManager(new CustomLinearLayoutManager(holder.itemView.getContext()));
        holder.recycler_activities.setAdapter(activitiesInWbsAdapter);

        String res_url = pm.getString("SERVER_URL") + "/getResource";


        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet

            Cache cache = AppController.getInstance().getRequestQueue().getCache();
            Cache.Entry entry = cache.get(res_url);

            if (entry != null) {

                String[] resourceIdArray, resourceNameArray;
                JSONArray dataArray;
                JSONObject dataObject;

                //Cache data available.
                try {
                    String data = new String(entry.data, "UTF-8");
                    Log.d("CACHE DATA", data);
                    JSONObject jsonObject = new JSONObject(data);
                    try {
                        dataArray = jsonObject.getJSONArray("data");
                        resourceIdArray = new String[dataArray.length()+1];
                        resourceNameArray = new String[dataArray.length()+1];

                        resourceIdArray[0] = "Select Resource";
                        resourceNameArray[0] = "Select Resource";

                        for(int i=0; i<dataArray.length();i++)
                        {
                            dataObject = dataArray.getJSONObject(i);
                            resourceIdArray[i+1]= dataObject.getString("id");
                            resourceNameArray[i+1]= dataObject.getString("firstName") + " " + dataObject.getString("lastName");
                        }

                        prepareActivities(context, currentWbsId,  activitiesInWbsAdapter, activitiesInWbsList, holder.text_progress,
                                resourceIdArray, resourceNameArray);

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
                Toast.makeText(context, "Offline Data Not available for WBS Activities", Toast.LENGTH_SHORT).show();
            }
        }

        else
        {
            // Cache data not exist.
            getAllResources(context, currentWbsId,  activitiesInWbsAdapter, activitiesInWbsList, holder.text_progress, res_url);
        }


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


    public void getAllResources (final Context context, final String currentWbsId, final ActivitiesInWbsAdapter wbsActivityAdapter,
                                 final List<ActivitiesInWbsList> wbsActivityList , final TextView progressWbs, String res_url)
    {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, res_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{
                            String type = response.getString("type");

                            if(type.equals("ERROR"))
                            {
                                Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                            String[] resourceIdArray, resourceNameArray;
                            JSONArray dataArray;
                            JSONObject dataObject;

                            if(type.equals("INFO"))
                            {
                                dataArray = response.getJSONArray("data");
                                resourceIdArray = new String[dataArray.length()+1];
                                resourceNameArray = new String[dataArray.length()+1];

                                resourceIdArray[0] = "Select Resource";
                                resourceNameArray[0] = "Select Resource";

                                for(int i=0; i<dataArray.length();i++)
                                {
                                    dataObject = dataArray.getJSONObject(i);
                                    resourceIdArray[i+1]= dataObject.getString("id");
                                    resourceNameArray[i+1]= dataObject.getString("firstName") + " " + dataObject.getString("lastName");
                                }

                                prepareActivities(context, currentWbsId,  wbsActivityAdapter, wbsActivityList, progressWbs,
                                        resourceIdArray, resourceNameArray);

                            }

                        }
                        catch(JSONException e){
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

    public void prepareItems(final Context context, String wbsId, final String progress, final LinearLayout hiddenLayout, final TextView text_progress)
    {

        JSONObject object = new JSONObject();

        try {
            object.put("progress", progress);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        pm = new PreferenceManager(context);

        String url = pm.getString("SERVER_URL") + "/putWbs?wbsId='"+ wbsId +"'";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.PUT, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

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
                    }
                }
        );
        requestQueue.add(jor);
    }

    public void prepareActivities(final Context context, final String currentWbsId, final ActivitiesInWbsAdapter wbsActivityAdapter,
                                  final List<ActivitiesInWbsList> wbsActivityList , final TextView progressWbs,
                                  final String[] resourceIdArray, final String[] resourceNameArray)
    {
        pm = new PreferenceManager(context);

        String activity_url = pm.getString("SERVER_URL") + "/getWbsActivity?wbsId=\""+currentWbsId+"\"";

        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet

            Cache cache = AppController.getInstance().getRequestQueue().getCache();
            Cache.Entry entry = cache.get(activity_url);

            if (entry != null) {
                JSONArray dataArray;
                JSONObject dataObject;

                //Cache data available.
                try {
                    String data = new String(entry.data, "UTF-8");
                    Log.d("CACHE DATA", data);
                    JSONObject jsonObject = new JSONObject(data);
                    try {
                        dataArray = jsonObject.getJSONArray("data");
                        String id ,activityName ,progress ,startDate, endDate, resourceAllocated ,boq, status, extendedEndDate;

                        ActivitiesInWbsList items;

                        float averageWbsProgress = 0;
                        int count=0;

                        for (int i = 0; i < dataArray.length(); i++) {
                            dataObject = dataArray.getJSONObject(i);
                            id = dataObject.getString("id");
                            activityName = dataObject.getString("activityName");
                            progress = dataObject.getString("progress");
                            startDate = dataObject.getString("startDate");
                            endDate = dataObject.getString("endDate");
                            resourceAllocated = dataObject.getString("resourceAllocated");
                            boq = dataObject.getString("boq");
                            status = dataObject.getString("status");
                            extendedEndDate = dataObject.getString("extendedEndDate");

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

                            for (int j = 0; j < resourceIdArray.length; j++) {
                                if (resourceAllocated.equals(resourceIdArray[j]))
                                    resourceAllocated = resourceNameArray[j];
                            }

                            items = new ActivitiesInWbsList(id, activityName, progress, startDate, endDate, extendedEndDate ,
                                    resourceAllocated, boq, status);
                            wbsActivityList.add(items);
                            wbsActivityAdapter.notifyDataSetChanged();

                            if(!Objects.equals(status, "Cancelled"))
                            {
                                Float currProgress = Float.parseFloat(progress);
                                DecimalFormat df = new DecimalFormat("#.00");

                                averageWbsProgress = averageWbsProgress + Float.parseFloat(df.format(currProgress));

                                count++;
                            }
                        }

                        averageWbsProgress = averageWbsProgress / count;
                        progressWbs.setText(String.valueOf(averageWbsProgress));


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
                Toast.makeText(context, "Offline Data Not available for WBS Activities", Toast.LENGTH_SHORT).show();
            }
        }

        else
        {

            RequestQueue requestQueue = Volley.newRequestQueue(context);

            JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, activity_url, null,
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

                                    JSONArray dataArray = response.getJSONArray("data");
                                    JSONObject dataObject;
                                    String id ,activityName ,progress ,startDate, endDate, resourceAllocated ,boq, status = null, extendedEndDate;

                                    int cancelledItems=0;

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
                                        extendedEndDate = dataObject.getString("extendedEndDate");

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

                                        for(int j=0;j<resourceIdArray.length;j++){
                                            if(resourceAllocated.equals(resourceIdArray[j]))
                                                resourceAllocated = resourceNameArray[j];
                                        }

                                        items = new ActivitiesInWbsList(id, activityName, progress, startDate, endDate, extendedEndDate,
                                                resourceAllocated, boq, status);
                                        wbsActivityList.add(items);
                                        wbsActivityAdapter.notifyDataSetChanged();

                                        Float currProgress = Float.parseFloat(progress);
                                        DecimalFormat df = new DecimalFormat("#.00");

                                        if(status.equals("Cancelled"))
                                            cancelledItems++;

                                        averageWbsProgress = averageWbsProgress + Float.parseFloat(df.format(currProgress));
                                    }

                                    if(dataArray.length()==cancelledItems)
                                        averageWbsProgress = 0;
                                    else
                                        averageWbsProgress = averageWbsProgress/(dataArray.length()-cancelledItems);

                                    progressWbs.setText(String.valueOf(averageWbsProgress));
                                }
                            }
                            catch(JSONException e){
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
            wbsActivityAdapter.notifyDataSetChanged();
        }

    }

    public void viewAttachment(final Context context, String wbsId)
    {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        pm = new PreferenceManager(context);

        final String url = pm.getString("SERVER_URL") + "/getWbsFiles?wbsId=\""+wbsId+"\"";

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
                        }
                        catch(JSONException e){
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

//    public static ProgressDialog createProgressDialog(Context mContext) {
//        ProgressDialog dialog = new ProgressDialog(mContext);
//        try {
//            dialog.show();
//        } catch (WindowManager.BadTokenException e) {
//
//        }
//        dialog.setCancelable(false);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        dialog.setContentView(R.layout.progress_dialog);
//        // dialog.setMessage(Message);
//        return dialog;
//    }
}
