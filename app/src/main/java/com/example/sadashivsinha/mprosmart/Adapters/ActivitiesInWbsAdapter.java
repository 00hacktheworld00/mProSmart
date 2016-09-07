package com.example.sadashivsinha.mprosmart.Adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.example.sadashivsinha.mprosmart.ModelLists.ActivitiesInWbsList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.Utils.DatePickerFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

/**
 * Created by saDashiv sinha on 02-Jul-16.
 */
public class ActivitiesInWbsAdapter extends RecyclerView.Adapter<ActivitiesInWbsAdapter.MyViewHolder> {

    private List<ActivitiesInWbsList> activitiesList;
    TextView textViewDate;
    Button date_status;
    String whichDate, currentDate;
    int dateCurrentStart, dateCurrentEnd, monthCurrentStart, monthCurrentEnd, yearCurrentStart, yearCurrentEnd;
    Context context;
    String currentActivityId;
    String[] dateArray;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView text_activities, text_status, activity_progress, start_date, end_date, activity_id, text_resource, text_boq;
        private ImageButton success_icon, btn_complete, btn_in_progress, btn_cancel,  edit_btn, btn_yet_to_start, edit_dates_btn;
        private CircleImageView dot_color;
        public LinearLayout hidden_layout, event_layout, progress_layout, hidden_layout_date;
        EditText text_progress;
        Button save_btn, close_btn, close_dates_layout, start_date_change, end_date_change;

        public MyViewHolder(final View view) {
            super(view);
            text_activities = (TextView) view.findViewById(R.id.text_activities);
            text_status = (TextView) view.findViewById(R.id.text_status);
            activity_progress = (TextView) view.findViewById(R.id.activity_progress);
            start_date = (TextView) view.findViewById(R.id.start_date);
            end_date = (TextView) view.findViewById(R.id.end_date);
            activity_id = (TextView) view.findViewById(R.id.activity_id);
            text_resource = (TextView) view.findViewById(R.id.text_resource);
            text_boq = (TextView) view.findViewById(R.id.text_boq);

            text_progress = (EditText) view.findViewById(R.id.text_progress);

            save_btn = (Button) view.findViewById(R.id.save_btn);
            close_btn = (Button) view.findViewById(R.id.close_btn);
            close_dates_layout = (Button) view.findViewById(R.id.close_dates_layout);

            start_date_change = (Button) view.findViewById(R.id.start_date_change);
            end_date_change = (Button) view.findViewById(R.id.end_date_change);

            dot_color = (CircleImageView) view.findViewById(R.id.dot_color);

            success_icon = (ImageButton) view.findViewById(R.id.success_icon);
            btn_complete = (ImageButton) view.findViewById(R.id.btn_complete);
            btn_yet_to_start = (ImageButton) view.findViewById(R.id.btn_yet_to_start);
            btn_in_progress = (ImageButton) view.findViewById(R.id.btn_in_progress);
            btn_cancel = (ImageButton) view.findViewById(R.id.btn_cancel);
            edit_btn = (ImageButton) view.findViewById(R.id.edit_btn);
            edit_dates_btn = (ImageButton) view.findViewById(R.id.edit_dates_btn);


            hidden_layout = (LinearLayout) view.findViewById(R.id.hidden_layout);
            event_layout = (LinearLayout) view.findViewById(R.id.event_layout);

            progress_layout = (LinearLayout) view.findViewById(R.id.progress_layout);
            progress_layout.setVisibility(View.INVISIBLE);

            hidden_layout_date = (LinearLayout) view.findViewById(R.id.hidden_layout_date);

            hidden_layout_date.setVisibility(View.GONE);

            hidden_layout.setVisibility(View.GONE);

            edit_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    circularRevealEffect(hidden_layout);
                    event_layout.setVisibility(View.GONE);
                }
            });

            edit_dates_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    circularRevealEffect(hidden_layout_date);
                    event_layout.setVisibility(View.GONE);
                }
            });
        }
    }
    public ActivitiesInWbsAdapter(List<ActivitiesInWbsList> activitiesList) {
        this.activitiesList = activitiesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_activities_in_wbs, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        ActivitiesInWbsList items = activitiesList.get(position);
        holder.activity_id.setText(items.getId());
        holder.text_activities.setText(items.getActivityName());
        holder.activity_progress.setText(items.getProgress());
        holder.text_resource.setText(items.getResourceAllocated());
        holder.text_boq.setText(items.getBoq());

        currentActivityId = holder.activity_id.getText().toString();

        String startDate = null, endDate = null;

        Date tradeDate = null;
        try
        {
            tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(items.getStartDate());
            startDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);

            tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(items.getEndDate());
            endDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);
        } catch (ParseException e)

        {
            e.printStackTrace();
        }


        holder.start_date.setText(startDate);
        holder.end_date.setText(endDate);



        currentDate = holder.start_date.getText().toString();

        dateArray = currentDate.split("-");

        dateCurrentStart = Integer.parseInt(dateArray[0]);
        monthCurrentStart = Integer.parseInt(dateArray[1]);
        yearCurrentStart = Integer.parseInt(dateArray[2]);


        currentDate = holder.end_date.getText().toString();

        dateArray = currentDate.split("-");

        dateCurrentEnd = Integer.parseInt(dateArray[0]);
        monthCurrentEnd = Integer.parseInt(dateArray[1]);
        yearCurrentEnd = Integer.parseInt(dateArray[2]);






        switch (items.getStatus()) {
            case "Yet to start":
                holder.text_status.setText("Yet to start");
                holder.dot_color.setImageResource(R.color.material_light_yellow_500);
                holder.success_icon.setBackgroundResource(R.drawable.ic_restore);

                break;

            case "In-Progress":
                holder.dot_color.setImageResource(R.color.navy_blue);
                holder.success_icon.setBackgroundResource(R.drawable.ic_hold);
                holder.text_status.setText("In-Progress");

                break;

            case "Completed":
                holder.dot_color.setImageResource(R.color.success_green);
                holder.success_icon.setBackgroundResource(R.drawable.ic_success);
                holder.text_status.setText("Completed");

                break;

            case "Cancelled":
                holder.dot_color.setImageResource(R.color.fancy_red);
                holder.success_icon.setBackgroundResource(R.drawable.ic_cancel);
                holder.text_status.setText("Cancelled");

                break;
        }

        final ProgressDialog pDialog = new ProgressDialog(holder.itemView.getContext());

        holder.btn_yet_to_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pDialog.setMessage("Updating Status ...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();

                String status = "1";

                updateProgressAndStatus(holder.itemView, pDialog, holder.activity_progress,  holder.itemView.getContext(), holder.activity_id.getText().toString(),
                        "0", status,
                        holder.progress_layout, holder.hidden_layout, holder.event_layout, holder.text_progress, holder, "Yet to start");

            }
        });



        holder.btn_in_progress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.progress_layout.setVisibility(View.VISIBLE);


                holder.save_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(holder.text_progress.getText().toString().isEmpty())
                        {
                            holder.text_progress.setError("Enter Progress");
                        }

                        else if (Integer.parseInt(holder.text_progress.getText().toString())<0 || Integer.parseInt(holder.text_progress.getText().toString())>100)
                        {
                            holder.text_progress.setError("Value should be between 0 and 100 only");
                        }
                        else if(Integer.parseInt(holder.text_progress.getText().toString())<Integer.parseInt(holder.activity_progress.getText().toString()))
                        {
                            holder.text_progress.setError("Progress update cannot be less that current progress - " + holder.activity_progress.getText().toString());
                        }
                        else
                        {
                            setCorrectStatusAndColors(holder, "In-Progress");
                            String valueWithNoZerosStart = holder.text_progress.getText().toString().replaceFirst("^0+(?!$)", "");


                            pDialog.setMessage("Updating Status ...");
                            pDialog.setIndeterminate(false);
                            pDialog.setCancelable(true);
                            pDialog.show();

                            String status = "2";

                            updateProgressAndStatus(holder.itemView, pDialog, holder.activity_progress, holder.itemView.getContext(), holder.activity_id.getText().toString(),
                                    valueWithNoZerosStart, status,
                                    holder.progress_layout, holder.hidden_layout, holder.event_layout, holder.text_progress ,
                                    holder, "In-Progress");
                        }
                    }
                });
            }
        });

        holder.btn_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pDialog.setMessage("Updating Status ...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();

                String status = "3";

                updateProgressAndStatus(holder.itemView, pDialog, holder.activity_progress, holder.itemView.getContext(), holder.activity_id.getText().toString(),
                        "100",status,
                        holder.progress_layout, holder.hidden_layout, holder.event_layout, holder.text_progress ,
                        holder, "Completed");
            }
        });



        holder.btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                pDialog.setMessage("Updating Status ...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();

                String status = "4";

                updateProgressAndStatus(holder.itemView, pDialog, holder.activity_progress, holder.itemView.getContext(), holder.activity_id.getText().toString(),
                        "0",status,
                        holder.progress_layout, holder.hidden_layout, holder.event_layout, holder.text_progress ,
                        holder, "Cancelled");
            }
        });



        holder.close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.progress_layout.setVisibility(View.INVISIBLE);
                holder.hidden_layout.setVisibility(View.GONE);
                holder.event_layout.setVisibility(View.VISIBLE);
            }
        });

        holder.close_dates_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.hidden_layout_date.setVisibility(View.GONE);
                holder.event_layout.setVisibility(View.VISIBLE);
                holder.start_date_change.setText("CHANGE START DATE");
                holder.end_date_change.setText("CHANGE END DATE");
            }
        });

        holder.start_date_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (holder.text_status.getText().toString()) {
                    case "Yet to start":

                        textViewDate = holder.start_date;
                        date_status = holder.start_date_change;
                        whichDate = "start";

                        currentDate = holder.start_date.getText().toString();

                        dateArray = currentDate.split("-");

                        dateCurrentStart = Integer.parseInt(dateArray[0]);
                        monthCurrentStart = Integer.parseInt(dateArray[1]);
                        yearCurrentStart = Integer.parseInt(dateArray[2]);

                        context = holder.itemView.getContext();
                        showDatePicker(context);

                        break;

                    case "In-Progress":

                        Toast.makeText(holder.itemView.getContext(), "Start date cannot be changed because Activity is In-Progress", Toast.LENGTH_SHORT).show();

                        break;

                    case "Completed":

                        Toast.makeText(holder.itemView.getContext(), "Start date cannot be changed because Activity is Completed", Toast.LENGTH_SHORT).show();

                        break;

                    case "Cancelled":

                        Toast.makeText(holder.itemView.getContext(), "Start date cannot be changed because Activity is Cancelled", Toast.LENGTH_SHORT).show();

                        break;
                }
            }
        });

        holder.end_date_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (holder.text_status.getText().toString()) {
                    case "Yet to start":

                        textViewDate = holder.end_date;
                        date_status = holder.end_date_change;
                        whichDate = "end";

                        currentDate = holder.end_date.getText().toString();

                        dateArray = currentDate.split("-");

                        dateCurrentEnd = Integer.parseInt(dateArray[0]);
                        monthCurrentEnd = Integer.parseInt(dateArray[1]);
                        yearCurrentEnd = Integer.parseInt(dateArray[2]);

                        context = holder.itemView.getContext();
                        showDatePicker(context);

                        break;

                    case "In-Progress":

                        textViewDate = holder.end_date;
                        date_status = holder.end_date_change;
                        whichDate = "end";

                        currentDate = holder.end_date.getText().toString();

                        dateArray = currentDate.split("-");

                        dateCurrentEnd = Integer.parseInt(dateArray[0]);
                        monthCurrentEnd = Integer.parseInt(dateArray[1]);
                        yearCurrentEnd = Integer.parseInt(dateArray[2]);

                        context = holder.itemView.getContext();
                        showDatePicker(context);

                        break;

                    case "Completed":

                        Toast.makeText(holder.itemView.getContext(), "End date cannot be changed because Activity is Completed", Toast.LENGTH_SHORT).show();

                        break;

                    case "Cancelled":

                        Toast.makeText(holder.itemView.getContext(), "End date cannot be changed because Activity is Cancelled", Toast.LENGTH_SHORT).show();

                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return activitiesList.size();
    }

    public void setCorrectStatusAndColors(MyViewHolder holder, String statusText)
    {

        if(statusText.equals("Yet to start"))
        {
            holder.dot_color.setImageResource(R.color.material_yellow_500);
            holder.success_icon.setBackgroundResource(R.drawable.ic_restore);
            holder.text_status.setText("Yet to start");
        }

        else if(statusText.equals("Completed"))
        {
            holder.dot_color.setImageResource(R.color.success_green);
            holder.success_icon.setBackgroundResource(R.drawable.ic_success);
            holder.text_status.setText("Completed");
        }

        else if(statusText.equals("In-Progress"))
        {
            holder.dot_color.setImageResource(R.color.navy_blue);
            holder.success_icon.setBackgroundResource(R.drawable.ic_hold);
            holder.text_status.setText("In-Progress");
        }

        else if(statusText.equals("Cancelled"))
        {
            holder.dot_color.setImageResource(R.color.fancy_red);
            holder.success_icon.setBackgroundResource(R.drawable.ic_cancel);
            holder.text_status.setText("Cancelled");

        }
    }

    public void circularRevealEffect(final LinearLayout mRevealView)
    {

        int radius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());

        int cx = (mRevealView.getLeft() + mRevealView.getRight());
        //                int cy = (mRevealView.getTop() + mRevealView.getBottom())/2;
        int cy = mRevealView.getTop();


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {


            SupportAnimator animator =
                    ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, radius);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(800);

            SupportAnimator animator_reverse = animator.reverse();

            if (mRevealView.getVisibility()==View.GONE) {
                mRevealView.setVisibility(View.VISIBLE);
                animator.start();
            } else {
                animator_reverse.addListener(new SupportAnimator.AnimatorListener() {
                    @Override
                    public void onAnimationStart() {

                    }

                    @Override
                    public void onAnimationEnd() {
                        mRevealView.setVisibility(View.GONE);

                    }

                    @Override
                    public void onAnimationCancel() {

                    }

                    @Override
                    public void onAnimationRepeat() {

                    }
                });
                animator_reverse.start();

            }
        } else {
            if (mRevealView.getVisibility() == View.GONE) {
                Animator anim = android.view.ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, radius);
                mRevealView.setVisibility(View.VISIBLE);
                anim.start();

            } else {
                Animator anim = android.view.ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, radius, 0);
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mRevealView.setVisibility(View.GONE);
                    }
                });
                anim.start();
            }
        }
    }

    public void updateProgressAndStatus(final View view, final ProgressDialog pDialog, final TextView activity_progress, final Context context, String currentActivityId, final String progress, String status,
                                        final LinearLayout progress_layout, final LinearLayout hidden_layout,
                                        final LinearLayout event_layout, final EditText text_progress, final MyViewHolder holder, final String statusString )
    {

        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);


        JSONObject object = new JSONObject();

        try {

            if(statusString.equals("In_Progress"))
                object.put("progress",text_progress );
            else
                object.put("progress",progress);


            object.put("status",status);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        String url = context.getResources().getString(R.string.server_url) + "/putWbsActivity?id=\""+currentActivityId +"\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.PUT, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(context, "Values Saved" , Toast.LENGTH_SHORT).show();

                        progress_layout.setVisibility(View.INVISIBLE);
                        setCorrectStatusAndColors(holder, statusString);
                        hidden_layout.setVisibility(View.GONE);
                        event_layout.setVisibility(View.VISIBLE);
                        activity_progress.setText(progress);
                        holder.text_progress.setText("");

                        pDialog.dismiss();

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

            final String newDate = String.valueOf(dayOfMonth) + "-" + String.valueOf(monthOfYear+1)
                    + "-" + String.valueOf(year);

            if(whichDate.equals("start"))
            {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try
                {
                    Date endDate = sdf.parse(yearCurrentEnd+"-"+monthCurrentEnd+"-"+dateCurrentEnd);
                    Date startDate = sdf.parse(year+"-"+ (monthOfYear+1) +"-"+dayOfMonth);

                    Log.d("startDate : ", startDate.toString());
                    Log.d("endDate : ", endDate.toString());

                    if(startDate.compareTo(endDate)>0)
                    {
                        Toast.makeText(context, "Start Date cannot be greater than End date \nCurrent End Date : " + String.valueOf(yearCurrentEnd+"-"+monthCurrentEnd+"-"+dateCurrentEnd), Toast.LENGTH_SHORT).show();
                    }
                    else
                    {

                    final ProgressDialog pDialog = new ProgressDialog(context);
                    pDialog.setMessage("Getting Data ...");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(true);
                    pDialog.show();

                    class MyTask extends AsyncTask<Void, Void, Void> {

                        @Override
                        protected Void doInBackground(Void... params) {

                            updateStartDate(textViewDate, date_status, newDate, currentActivityId, pDialog);
                            return null;
                        }

                    }
                    new MyTask().execute();

                        currentDate = newDate;

                        String[] dateArray = currentDate.split("-");

                        dateCurrentStart = Integer.parseInt(dateArray[0]);
                        monthCurrentStart = Integer.parseInt(dateArray[1]);
                        yearCurrentStart = Integer.parseInt(dateArray[2]);
                    }
                }

                catch (ParseException e)
                {
                    e.printStackTrace();
                }
            }
            else if(whichDate.equals("end"))
            {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try
                {
                    Date startDate = sdf.parse(yearCurrentStart+"-"+monthCurrentStart+"-"+dateCurrentStart);
                    Date endDate = sdf.parse(year+"-"+ (monthOfYear+1) +"-"+dayOfMonth);

                    Log.d("startDate : ", startDate.toString());
                    Log.d("endDate : ", endDate.toString());

                    if(startDate.compareTo(endDate)>0)
                    {
                        Toast.makeText(context, "End Date cannot be smaller than Start date \nCurrent Start Date : " + String.valueOf(yearCurrentStart+"-"+monthCurrentStart+"-"+dateCurrentStart), Toast.LENGTH_SHORT).show();
                    }
                    else
                    {

                    final ProgressDialog pDialog = new ProgressDialog(context);
                    pDialog.setMessage("Getting Data ...");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(true);
                    pDialog.show();

                    class MyTask extends AsyncTask<Void, Void, Void> {

                        @Override
                        protected Void doInBackground(Void... params) {

                            updateEndDate(textViewDate, date_status, newDate, currentActivityId, pDialog);
                            return null;
                        }

                    }
                    new MyTask().execute();

                    currentDate = newDate;

                        String[] dateArray = currentDate.split("-");

                        dateCurrentEnd = Integer.parseInt(dateArray[0]);
                        monthCurrentEnd = Integer.parseInt(dateArray[1]);
                        yearCurrentEnd = Integer.parseInt(dateArray[2]);
                    }
                }

                catch (ParseException e)
                {
                    e.printStackTrace();
                }
            }

        }
    };

    public void updateStartDate(final TextView startDate, final TextView statusDate, final String updatedDate, String currentActivityId, final ProgressDialog pDialog)
    {
        JSONObject object = new JSONObject();

        try {

            Date tradeDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(updatedDate);
            String updatedDateFormatted = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(tradeDate);


            object.put("startDate",updatedDateFormatted);

            Log.d("REQUEST SENT OF JSON :" , object.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        String url = context.getResources().getString(R.string.server_url) + "/putWbsStartDate?id="+ currentActivityId;

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.PUT, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if(response.getString("msg").equals("success"))
                            {
                                Toast.makeText(context, "Start Date Updated", Toast.LENGTH_SHORT).show();
                                startDate.setText(updatedDate);
                                statusDate.setText("New Start Date : "+updatedDate);
                                pDialog.dismiss();
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

    public void updateEndDate(final TextView startDate, final TextView statusDate, final String updatedDate, String currentActivityId, final ProgressDialog pDialog)
    {
        JSONObject object = new JSONObject();

        try {

            Date tradeDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(updatedDate);
            String updatedDateFormatted = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(tradeDate);

            object.put("endDate",updatedDateFormatted);

            Log.d("REQUEST SENT OF JSON :" , object.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        String url = context.getResources().getString(R.string.server_url) + "/putWbsEndDate?id="+ currentActivityId;

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.PUT, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if(response.getString("msg").equals("success"))
                            {
                                Toast.makeText(context, "End Date Updated", Toast.LENGTH_SHORT).show();

                                startDate.setText(updatedDate);
                                statusDate.setText("New End Date : "+updatedDate);
                                pDialog.dismiss();
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