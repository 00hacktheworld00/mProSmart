package com.example.sadashivsinha.mprosmart.Adapters;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.Activities.AttachmentActivity;
import com.example.sadashivsinha.mprosmart.ModelLists.MomList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.Utils.DatePickerFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by saDashiv sinha on 14-Mar-16.
 */
public class MomAdapter extends RecyclerView.Adapter<MomAdapter.MyViewHolder> {

    private List<MomList> momList;
    TextView textViewDate;
    Context mContext;
    String selectedDateToSend;
    PreferenceManager pm;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView text_line_no, btn_date,original_line_no, no_of_attachments;
        public Button editBtn;
        public ImageButton attachBtn;
        public EditText text_matter, text_responsible;
        public MyViewHolder(final View view) {
            super(view);

            pm = new PreferenceManager(view.getContext());

            text_matter = (EditText) view.findViewById(R.id.text_matter);
            text_responsible = (EditText) view.findViewById(R.id.text_responsible);
            text_line_no = (TextView) view.findViewById(R.id.text_line_no);
            no_of_attachments = (TextView) view.findViewById(R.id.no_of_attachments);

            original_line_no = (TextView) view.findViewById(R.id.original_line_no);

            btn_date = (TextView) view.findViewById(R.id.btn_date);

            editBtn = (Button) view.findViewById(R.id.editBtn);
            attachBtn = (ImageButton) itemView.findViewById(R.id.attachBtn);

            btn_date.setEnabled(false);

            editBtn.setOnClickListener(new View.OnClickListener() {
                int count=0;
                @Override
                public void onClick(View v) {
                    if (btn_date.isEnabled())
                    {
                        text_matter.setEnabled(false);
                        text_responsible.setEnabled(false);
                        btn_date.setEnabled(false);

                        editBtn.setText("EDIT");

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            editBtn.setBackgroundTintList(view.getContext().getResources().getColorStateList(R.color.colorPrimary));
                        }


                        ProgressDialog pDialog = new ProgressDialog(view.getContext());
                        pDialog.setMessage("Getting cache data");
                        pDialog.show();

                        updateMomLine(view.getContext(), original_line_no.getText().toString(),selectedDateToSend , pDialog);
                    } else {
//                    text_matter.setEnabled(true);
//                    text_responsible.setEnabled(true);
//                    btn_date.setEnabled(true);

//                    text_matter.requestFocus();

                        btn_date.setEnabled(true);

                        editBtn.setText("SAVE");

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            editBtn.setBackgroundTintList(view.getContext().getResources().getColorStateList(R.color.success_green));
                        }
                    }
                }
            });

            attachBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PreferenceManager pm = new PreferenceManager(itemView.getContext());
                    Intent intent = new Intent(itemView.getContext(), AttachmentActivity.class);
                    String url =  pm.getString("SERVER_URL") + "/getMomLineItemsFiles?momLineId=\"" + original_line_no.getText().toString() + "\"";
                    intent.putExtra("viewURL", url);
                    intent.putExtra("viewOnly", true);
                    itemView.getContext().startActivity(intent);
                }
            });

        }

    }
    public MomAdapter(List<MomList> momList) {
        this.momList = momList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_mom, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        MomList items = momList.get(position);
        holder.text_line_no.setText(String.valueOf(items.getText_line_no()));
        holder.text_matter.setText(String.valueOf(items.getText_matter()));
        holder.text_responsible.setText(items.getText_responsible());
        holder.original_line_no.setText(String.valueOf(items.getOriginal_line_no()));
        holder.no_of_attachments.setText(String.valueOf(items.getText_attachments()));

        Date tradeDate = null;
        try {
            tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(items.getText_date());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.btn_date.setText(new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate));

        holder.btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textViewDate = holder.btn_date;
                mContext = holder.itemView.getContext();
                showDatePicker(mContext);

            }
        });
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


            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dateFormatDDMMYYYY = new SimpleDateFormat("dd-MM-yyyy");

            Date date = new Date();
            String dateText = dateFormat.format(date);

            Date selectedDate = null;
            Date currentDate = null;

            try
            {
                selectedDate = dateFormat.parse(year+"-"+ (monthOfYear+1) +"-"+dayOfMonth);
                currentDate = dateFormat.parse(dateText);
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }

            if(currentDate.compareTo(selectedDate)>0)
            {
                Toast.makeText(view.getContext(), "Selected Date should be minimum current Date : " + dateFormatDDMMYYYY.format(date) , Toast.LENGTH_SHORT).show();
            }
            else
            {
                String newDate = String.valueOf(dayOfMonth) + "-" + String.valueOf(monthOfYear+1)
                        + "-" + String.valueOf(year);

                textViewDate.setText(newDate);
                selectedDateToSend = String.valueOf(year) + "-" + String.valueOf(monthOfYear+1)
                        + "-" + String.valueOf(dayOfMonth);
            }
        }
    };

    public void updateMomLine(final Context context, final String lineNo, final String date, final ProgressDialog pDialog)
    {
        JSONObject object = new JSONObject();

        try {

            object.put("dueDate",date);

            Log.d("OBJECT SENT JSON", object.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        String url = pm.getString("SERVER_URL")  + "/updateMomLineItems?lineId=\"" + lineNo + "\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.PUT, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("msg").equals("success"))
                            {
                                Toast.makeText(context, "Values Saved" , Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(context, response.getString("msg") , Toast.LENGTH_SHORT).show();
                            }
                            pDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            pDialog.dismiss();
                        }

                        Log.d("Daily Progress", response.toString());

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

    @Override
    public int getItemCount() {
        return momList.size();
    }

}
