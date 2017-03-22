package com.example.sadashivsinha.mprosmart.Adapters;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.example.sadashivsinha.mprosmart.ModelLists.SubcontractorList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.AppController;
import com.example.sadashivsinha.mprosmart.Utils.ConnectionDetector;
import com.example.sadashivsinha.mprosmart.Utils.DatePickerFragment;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by saDashiv sinha on 16-Mar-16.
 */
public class SubcontractorAdapter extends RecyclerView.Adapter<SubcontractorAdapter.MyViewHolder> {

    public List<SubcontractorList> subcontractorList;
    TextView textViewDate;
    Context mContext;
    String[] array_resources_id, array_resources_name;
    String resource;
    JSONArray jsonArray;
    JSONObject jsonObject;
    String currentSelectedRes;
    String currentDate;
    PreferenceManager pm;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView text_line_no, original_line_no;
        public Button editBtn;
        public EditText text_wbs, text_activities, text_total_hours, text_res_name;
        BetterSpinner spinner_res_name;
        ProgressDialog pDialog;
        TextView text_date;
        String lineNo;
        Snackbar snackbar;

        public MyViewHolder(final View view) {
            super(view);

            pm = new PreferenceManager(view.getContext());
            mContext = view.getContext();

            original_line_no = (TextView) view.findViewById(R.id.original_line_no);

            text_line_no = (TextView) view.findViewById(R.id.text_line_no);
            text_wbs = (EditText) view.findViewById(R.id.text_wbs);
            text_activities = (EditText) view.findViewById(R.id.text_activities);
            text_date = (TextView) view.findViewById(R.id.text_date);
            text_total_hours = (EditText) view.findViewById(R.id.text_total_hours);
            text_res_name = (EditText) view.findViewById(R.id.text_res_name);

            editBtn = (Button) view.findViewById(R.id.editBtn);

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            currentDate = dateFormat.format(cal.getTime());

            spinner_res_name = (BetterSpinner) view.findViewById(R.id.spinner_res_name);
            spinner_res_name.setVisibility(View.GONE);

            spinner_res_name.setText("Select Resource");

            spinner_res_name.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(position!=0)
                    {
                        currentSelectedRes = array_resources_name[position];
                    }
                }
            });

            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(text_res_name.getVisibility()==View.GONE
                            && text_total_hours.isEnabled() && text_date.isEnabled())
                    {
                        if(spinner_res_name.getText().toString().equals("Select Resource"))
                        {
                            Toast.makeText(mContext, "Select a Resource first", Toast.LENGTH_SHORT).show();
                        }
                        else if(Integer.parseInt(text_total_hours.getText().toString())>24)
                        {
                            Toast.makeText(mContext, "TOTAL HOURS cannot be more than 24", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            pDialog = new ProgressDialog(view.getContext());
                            pDialog.setMessage("Saving Data ...");
                            pDialog.setIndeterminate(false);
                            pDialog.setCancelable(true);
                            pDialog.show();

                            class MyTask extends AsyncTask<Void, Void, Void> {

                                @Override
                                protected Void doInBackground(Void... params) {
                                    sendDataToServer(editBtn, spinner_res_name, text_date, text_total_hours, text_res_name);
                                    return null;
                                }
                            }

                            new MyTask().execute();
                        }
                    }

                    else
                    {
                        text_res_name.setVisibility(View.GONE);
                        spinner_res_name.setVisibility(View.VISIBLE);
                        text_date.setEnabled(true);
                        text_total_hours.setEnabled(true);

                        editBtn.setText("SAVE");

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            editBtn.setBackgroundTintList(view.getContext().getResources().getColorStateList(R.color.success_green));
                        }
                        pDialog = new ProgressDialog(view.getContext());
                        pDialog.setMessage("Saving Data ...");
                        pDialog.setIndeterminate(false);
                        pDialog.setCancelable(true);
                        pDialog.show();

                        class MyTask extends AsyncTask<Void, Void, Void> {

                            @Override
                            protected Void doInBackground(Void... params) {
                                prepareResources(mContext, pDialog, spinner_res_name, text_res_name);
                                return null;
                            }
                        }

                        new MyTask().execute();
                    }
                }

        public void sendDataToServer(final Button editBtn, final BetterSpinner spinner_res_name, final TextView text_date, final EditText text_total_hours,
                                 final TextView text_res_name)
        {
            JSONObject object = new JSONObject();

            try {

                object.put("resourceName",currentSelectedRes);
                object.put("totalHours",text_total_hours.getText().toString());

                Date tradeDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(text_date.getText().toString());
                String formattedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(tradeDate);

                object.put("date",formattedDate);

                Log.d("SENT JSON : " , object.toString());

            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }

            RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());

            lineNo = original_line_no.getText().toString();
            String url = pm.getString("SERVER_URL")  + "/updateSubContractorLineItems?subContractorLineItems="+lineNo;

            JsonObjectRequest jor = new JsonObjectRequest(Request.Method.PUT, url, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.d("RESPONSE JSON : " , response.toString());

                                if(response.getString("msg").equals("success"))
                                {
                                    Toast.makeText(view.getContext(), "Saved", Toast.LENGTH_SHORT).show();
                                    text_date.setEnabled(false);
                                    text_total_hours.setEnabled(false);

                                    text_res_name.setText(spinner_res_name.getText().toString());

                                    text_res_name.setVisibility(View.VISIBLE);
                                    spinner_res_name.setVisibility(View.GONE);

                                    editBtn.setText("EDIT");

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        editBtn.setBackgroundTintList(view.getContext().getResources().getColorStateList(R.color.colorPrimary));
                                    }

                                    pDialog.dismiss();
                                }
                                else
                                {
                                    Toast.makeText(view.getContext(), "Cannot saved due to some error", Toast.LENGTH_SHORT).show();
                                    pDialog.dismiss();
                                }

                                snackbar = Snackbar.make(view,"Values Saved.",Snackbar.LENGTH_SHORT);
                                snackbar.show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //response success message display
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Volley","Error");
                            pDialog.dismiss();
                        }
                    }
            );
            requestQueue.add(jor);
            }
        });
    }
}
    public SubcontractorAdapter(List<SubcontractorList> subcontractorList) {
        this.subcontractorList = subcontractorList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_subcontractor, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        SubcontractorList items = subcontractorList.get(position);

        holder.original_line_no.setText(items.getOriginalLineNos());

        holder.text_line_no.setText(String.valueOf(items.getText_line_no()));
        holder.text_wbs.setText(items.getText_wbs());
        holder.text_activities.setText(items.getText_activities());
        holder.text_total_hours.setText(items.getText_total_hours());
        holder.text_res_name.setText(String.valueOf(items.getText_res_name()));
        holder.text_date.setText(items.getText_date());

        holder.text_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewDate = holder.text_date;
                mContext = holder.itemView.getContext();
                showDatePicker(holder.itemView.getContext());
            }
        });
    }

    private void prepareResources(final Context mContext, ProgressDialog pDialog, final BetterSpinner spinnerResource,
                                  final EditText text_res_name)
    {

        String resource_url = pm.getString("SERVER_URL")  + "/getResource";
        ConnectionDetector cd = new ConnectionDetector(mContext);
        Boolean isInternetPresent = cd.isConnectingToInternet();

        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet

            Cache cache = AppController.getInstance().getRequestQueue().getCache();
            Cache.Entry entry = cache.get(resource_url);
            if (entry != null) {
                int currentPos = 0;
                //Cache data available.
                try {
                    String data = new String(entry.data, "UTF-8");
                    Log.d("CACHE DATA", data);
                    JSONObject jsonObject = new JSONObject(data);
                    try {
                        jsonArray = jsonObject.getJSONArray("data");
                        array_resources_id = new String[jsonArray.length()+1];
                        array_resources_name = new String[jsonArray.length()+1];

                        for(int i=0; i<jsonArray.length();i++)
                        {
                            jsonObject = jsonArray.getJSONObject(i);

                            array_resources_id[i]=jsonObject.getString("id");
                            array_resources_name[i] = jsonObject.getString("firstName") + " " +jsonObject.getString("lastName");

                            if(array_resources_name[i].equals(text_res_name.getText().toString()))
                                currentPos = i;
                        }

                        ArrayAdapter<String> adapter;

                        if (array_resources_id == null) {
                            adapter = new ArrayAdapter<String>(mContext,
                                    android.R.layout.simple_dropdown_item_1line, new String[]{"No Resource Found"});
                        } else {
                            adapter = new ArrayAdapter<String>(mContext,
                                    android.R.layout.simple_dropdown_item_1line, array_resources_name);
                        }
                        spinnerResource.setAdapter(adapter);
                        spinnerResource.setSelection(currentPos);
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
                Toast.makeText(mContext, "Offline Data Not available for Resources", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }

        else
        {
            // Cache data not exist.
            // TODO Auto-generated method stub

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, resource_url, null,
                    new Response.Listener<JSONObject>() {
                        int currentPos = 0;
                        @Override
                        public void onResponse(JSONObject response) {
                            try
                            {
                                Log.d("RESPONSE RESOURCE :", response.toString());

                                jsonArray = response.getJSONArray("data");
                                array_resources_id = new String[jsonArray.length()+1];
                                array_resources_name = new String[jsonArray.length()+1];
                                array_resources_id[0]= "Select Resource";
                                array_resources_name[0] = "Select Resource";

                                for(int i=0; i<jsonArray.length();i++)
                                {
                                    jsonObject = jsonArray.getJSONObject(i);

                                    array_resources_id[i]=jsonObject.getString("id");
                                    array_resources_name[i] = jsonObject.getString("firstName") + " " +jsonObject.getString("lastName");

                                    if(array_resources_name[i].equals(text_res_name.getText().toString()))
                                        currentPos = i;
                                }

                                ArrayAdapter<String> adapter;

                                if (array_resources_id == null) {
                                    adapter = new ArrayAdapter<String>(mContext,
                                            android.R.layout.simple_dropdown_item_1line, new String[]{"No Resource Found"});
                                } else {
                                    adapter = new ArrayAdapter<String>(mContext,
                                            android.R.layout.simple_dropdown_item_1line, array_resources_name);
                                }
                                spinnerResource.setAdapter(adapter);
                                spinnerResource.setSelection(currentPos);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
//                        setData(response,false);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("SUB ERR ", "Error: " + error.getMessage());
                    Toast.makeText(mContext,
                            error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjectRequest);
            if(pDialog!=null)
                pDialog.dismiss();
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

            String formattedMonth = null;

            if ((monthOfYear+1)<10){
                formattedMonth = "0" + String.valueOf(monthOfYear+1);
            };

            String newDate = String.valueOf(dayOfMonth) + "-" + String.valueOf(formattedMonth)
                    + "-" + String.valueOf(year);

            String newSelectedDateYYMMDDD = String.valueOf(year) + "-" + String.valueOf(formattedMonth) + "-" +
                    String.valueOf(dayOfMonth);

            Log.d("Selected date :", newSelectedDateYYMMDDD);
            Log.d("Current date :", currentDate);

            if(newSelectedDateYYMMDDD.compareTo(currentDate)>0)
            {
                Toast.makeText(mContext, "Maximum selected Date should be TODAY'S DATE", Toast.LENGTH_SHORT).show();
            }
            else
            {
                textViewDate.setText(newDate);
            }

        }
    };


    @Override
    public int getItemCount()
    {
        return subcontractorList.size();
    }
}
