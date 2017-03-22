package com.example.sadashivsinha.mprosmart.Adapters;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadashivsinha.mprosmart.ModelLists.QualityChecklistList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by saDashiv sinha on 30-Jun-16.
 */
public class QualityChecklistAdapter extends RecyclerView.Adapter<QualityChecklistAdapter.MyViewHolder> {

    private List<QualityChecklistList> qualityList;
    PreferenceManager pm;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView text_subject, text_comments, id;
        public Spinner spinner_status;
        public LinearLayout hiddenLayout;
        public Button editBtn;
        public String currentId;

        public MyViewHolder(final View view) {
            super(view);

            pm = new PreferenceManager(view.getContext());

            text_subject = (TextView) view.findViewById(R.id.text_subject);
            text_comments = (TextView) view.findViewById(R.id.text_comments);
            id = (TextView) view.findViewById(R.id.id);

            spinner_status = (Spinner) view.findViewById(R.id.spinner_status);

            spinner_status.setEnabled(false);
            spinner_status.setClickable(false);


            ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    new String[]{"PENDING", "ACCEPT", "REJECT"});

            spinner_status.setAdapter(adapter);

            hiddenLayout = (LinearLayout) view.findViewById(R.id.hiddenLayout);
            hiddenLayout.setVisibility(View.GONE);

            editBtn = (Button) view.findViewById(R.id.editBtn);
            editBtn.setOnClickListener(new View.OnClickListener() {
                int count=0;
                @Override
                public void onClick(View v) {
                    if(count==0)
                    {
                        hiddenLayout.setVisibility(View.VISIBLE);
                        editBtn.setText("SAVE");
                        spinner_status.setEnabled(true);
                        spinner_status.setClickable(true);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        {
                        editBtn.setBackgroundTintList(view.getContext().getResources().getColorStateList(R.color.success_green));
                        }

                        count++;
                    }

                    else
                    {
                        if(text_comments.getText().toString().isEmpty())
                        {
                            text_comments.setError("Please enter comments for changes made.");
                        }
                        else
                        {
                            currentId = id.getText().toString();
                            int statusVal = 0;
                            if(spinner_status.getSelectedItem().toString().equals("PENDING"))
                            {
                                statusVal=1;
                            }
                            else if(spinner_status.getSelectedItem().toString().equals("ACCEPT"))
                            {
                                statusVal=2;
                            }
                            else if(spinner_status.getSelectedItem().toString().equals("REJECT"))
                            {
                                statusVal=3;
                            }
                            prepareItems(view.getContext(), statusVal, text_comments.getText().toString(),
                                    spinner_status, editBtn, hiddenLayout, currentId);
                            count--;
                        }
                    }
                }
            });
        }
    }

    public QualityChecklistAdapter(List<QualityChecklistList> qualityList) {
        this.qualityList = qualityList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_quality_checklist, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        QualityChecklistList items = qualityList.get(position);
        holder.text_subject.setText(items.getText_subject());
        holder.text_comments.setText(items.getText_comments());
        holder.id.setText(items.getId());

        if(items.getText_status().equals("1"))
        {
            holder.spinner_status.setSelection(0);
        }
        else if(items.getText_status().equals("2"))
        {
            holder.spinner_status.setSelection(1);
        }
        else if(items.getText_status().equals("3"))
        {
            holder.spinner_status.setSelection(2);
        }
    }

    @Override
    public int getItemCount() {
        return qualityList.size();
    }



    public void prepareItems(final Context context, int statusVal, String comments , final Spinner spinner_status, final Button editBtn,
                             final LinearLayout hiddenLayout, String currentId)
    {
        JSONObject object = new JSONObject();

        try {

            object.put("status", statusVal);
            object.put("comments", comments);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        String url = pm.getString("SERVER_URL")  + "/updateQualityChecklistStatus?id='" + currentId + "'";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.PUT, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(context, response.getString("msg").toString(), Toast.LENGTH_SHORT).show();

                            spinner_status.setEnabled(false);
                            spinner_status.setClickable(false);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                            {
                                editBtn.setBackgroundTintList(context.getResources().getColorStateList(R.color.colorPrimary));
                            }
                            editBtn.setText("EDIT");
                            hiddenLayout.setVisibility(View.GONE);

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
                    }
                }
        );
        requestQueue.add(jor);
    }
}


