package com.example.sadashivsinha.mprosmart.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.sadashivsinha.mprosmart.ModelLists.QualityList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by saDashiv sinha on 01-Mar-16.
 */
public class QualityAdapter extends RecyclerView.Adapter<QualityAdapter.MyViewHolder> {

    private List<QualityList> qualityList;
    Context context;
    ProgressDialog pDialog;
    String acceptedQuantity;
    String line_id;
    View currentView;
    PreferenceManager pm;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView item_id, attachments;
        public ImageButton attachBtn;
        public EditText received_quantity, quantity_accept,quantity_reject, item_desc;
        public Button editBtn;

        public MyViewHolder(final View view) {
            super(view);

            pm = new PreferenceManager(view.getContext());

            currentView = view;
            item_id = (TextView) view.findViewById(R.id.item_id);
            item_desc = (EditText) view.findViewById(R.id.item_desc);;
            received_quantity = (EditText) view.findViewById(R.id.received_quantity);
            quantity_accept = (EditText) view.findViewById(R.id.quantity_accept);
            quantity_reject = (EditText) view.findViewById(R.id.quantity_reject);
            attachments = (TextView) view.findViewById(R.id.no_of_attachments);
            quantity_reject.setEnabled(false);

            acceptedQuantity = quantity_accept.getText().toString();

            context = view.getContext();

            attachBtn = (ImageButton) itemView.findViewById(R.id.attachBtn);
            editBtn = (Button) itemView.findViewById(R.id.editBtn);

            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    line_id = item_id.getText().toString();
                    if(quantity_accept.isEnabled())
                    {
                        int quantityTotalVal = Integer.parseInt(String.valueOf(received_quantity.getText().toString()));
                        int quantityAcceptVal = Integer.parseInt(String.valueOf(quantity_accept.getText().toString()));
                        int quantityRejectVal = Integer.parseInt(String.valueOf(quantity_reject.getText().toString()));

                        if(quantityAcceptVal>quantityTotalVal)
                        {
                            quantity_accept.setError("Value greater than received quantity.");
                        }

                        else if(quantityRejectVal>quantityTotalVal)
                        {
                            quantity_reject.setError("Value greater than received quantity.");
                        }

                        else
                        {
                            quantityRejectVal = quantityTotalVal - quantityAcceptVal;
                            quantity_reject.setText(String.valueOf(quantityRejectVal));

                            editBtn.setText("EDIT");

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                editBtn.setBackgroundTintList(context.getResources().getColorStateList(R.color.colorPrimary));
                            }
                            acceptedQuantity = quantity_accept.getText().toString();

                            quantity_accept.setEnabled(false);
                            pDialog = new ProgressDialog(context);
                            pDialog.setMessage("Saving Data ...");
                            pDialog.setIndeterminate(false);
                            pDialog.setCancelable(true);
                            pDialog.show();

                            class MyTask extends AsyncTask<Void, Void, Void> {

                                @Override
                                protected Void doInBackground(Void... params) {
                                    prepareItems();
                                    return null;
                                }
                            }

                            new MyTask().execute();
                        }
                    }
                    else
                    {
                        quantity_accept.setEnabled(true);
                        quantity_accept.requestFocus();
                        editBtn.setText("SAVE");

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            editBtn.setBackgroundTintList(context.getResources().getColorStateList(R.color.success_green));
                        }
                    }
                }
            });


            attachBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AttachmentActivity.class);
                    context.startActivity(intent);
                }
            });
        }
    }
    public void prepareItems()
    {
        JSONObject object = new JSONObject();

        try {
            object.put("quantityAccepted",acceptedQuantity);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        String url = pm.getString("SERVER_URL")  + "/updateQirItem?itemId=\""+line_id+"\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.PUT, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(context, response.getString("msg").toString(), Toast.LENGTH_SHORT).show();
                            Snackbar snackbar = Snackbar.make(currentView,"Values Saved.",Snackbar.LENGTH_SHORT);
                            snackbar.show();
                            pDialog.dismiss();

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
    public QualityAdapter(List<QualityList> qualityList) {
        this.qualityList = qualityList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_quality, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        QualityList items = qualityList.get(position);
        holder.item_id.setText(String.valueOf(items.getItemId()));
        holder.item_desc.setText(items.getItemDesc());
        holder.received_quantity.setText(items.getReceivedQuantity());
        holder.quantity_accept.setText(String.valueOf(items.getQuantityAccept()));
        holder.quantity_reject.setText(items.getQuantityReject());
        holder.attachments.setText(items.getAttachments());
    }

    @Override
    public int getItemCount() {
        return qualityList.size();
    }
}
