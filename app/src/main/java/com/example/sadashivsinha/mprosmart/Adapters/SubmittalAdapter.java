package com.example.sadashivsinha.mprosmart.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import com.example.sadashivsinha.mprosmart.ModelLists.SubmittalList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by saDashiv sinha on 15-Mar-16.
 */
public class SubmittalAdapter extends RecyclerView.Adapter<SubmittalAdapter.MyViewHolder> {

    public List<SubmittalList> submittalList;
    PreferenceManager pm;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView line_no, original_line_no, no_of_attachments;
        public EditText text_doc_type, text_short_desc, text_variation, text_variation_desc;
        public BetterSpinner spinner_status, spinner_type;
        public Button editBtn;
        public ImageButton attachBtn;


        public MyViewHolder(final View view) {
            super(view);

            pm = new PreferenceManager(view.getContext());

            original_line_no = (TextView) view.findViewById(R.id.original_line_no);
            line_no = (TextView) view.findViewById(R.id.text_line_no);
            no_of_attachments = (TextView) view.findViewById(R.id.no_of_attachments);
            text_doc_type = (EditText) view.findViewById(R.id.text_doc_type);;
            text_short_desc = (EditText) view.findViewById(R.id.text_short_desc);
            text_variation = (EditText) view.findViewById(R.id.text_variation);
            text_variation_desc = (EditText) view.findViewById(R.id.text_variation_desc);

            spinner_status = (BetterSpinner) view.findViewById(R.id.spinner_status);
            spinner_type = (BetterSpinner) view.findViewById(R.id.spinner_type);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(),
                    android.R.layout.simple_dropdown_item_1line,new String[] {"ACTIVE", "INACTIVE"});
            spinner_status.setAdapter(adapter);

            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(view.getContext(),
                    android.R.layout.simple_dropdown_item_1line,new String[] {"INCOMING", "OUTGOING"});
            spinner_type.setAdapter(adapter2);

            spinner_status.setEnabled(false);
            spinner_type.setEnabled(false);

            editBtn = (Button) view.findViewById(R.id.editBtn);
            attachBtn = (ImageButton) itemView.findViewById(R.id.attachBtn);

            attachBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), AttachmentActivity.class);
                    String url = pm.getString("SERVER_URL") + "/getSubmittalLineFiles?lineNo=\"" + original_line_no.getText().toString() + "\"";
                    intent.putExtra("viewURL", url);
                    intent.putExtra("viewOnly", true);
                    itemView.getContext().startActivity(intent);
                }
            });

            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(spinner_status.isEnabled() && text_doc_type.isEnabled() && text_short_desc.isEnabled()
                            && text_variation_desc.isEnabled() && text_variation.isEnabled() && spinner_type.isEnabled())
                    {
                        spinner_status.setEnabled(false);
                        spinner_type.setEnabled(false);
                        text_doc_type.setEnabled(false);
                        text_short_desc.setEnabled(false);
                        text_variation.setEnabled(false);
                        text_variation_desc.setEnabled(false);

                        editBtn.setText("EDIT");

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            editBtn.setBackgroundTintList(view.getContext().getResources().getColorStateList(R.color.colorPrimary));
                        }

                        ProgressDialog pDialog = new ProgressDialog(view.getContext());
                        pDialog.setMessage("Getting cache data");
                        pDialog.show();

                        updateSubmittalLine(view.getContext(), text_doc_type.getText().toString(), text_short_desc.getText().toString(),
                                text_variation.getText().toString(), text_variation_desc.getText().toString(),
                                spinner_status.getText().toString(), spinner_type.getText().toString(), original_line_no.getText().toString(),
                                pDialog);
                    }

                    else
                    {
                        spinner_status.setEnabled(true);
                        spinner_type.setEnabled(true);
                        text_doc_type.setEnabled(true);
                        text_short_desc.setEnabled(true);
                        text_variation.setEnabled(true);
                        text_variation_desc.setEnabled(true);

                        text_doc_type.requestFocus();
                        editBtn.setText("SAVE");

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            editBtn.setBackgroundTintList(view.getContext().getResources().getColorStateList(R.color.success_green));
                        }
                    }
                }
            });
        }
    }
    public SubmittalAdapter(List<SubmittalList> submittalList) {
        this.submittalList = submittalList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_submittals, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        SubmittalList items = submittalList.get(position);
        holder.line_no.setText(String.valueOf(items.getLine_no()));
        holder.text_doc_type.setText(items.getText_doc_type());
        holder.text_short_desc.setText(items.getText_short_desc());
        holder.spinner_status.setText(String.valueOf(items.getText_status()));
        holder.text_variation.setText(items.getText_variation());
        holder.text_variation_desc.setText(items.getText_variation_desc());
        holder.original_line_no.setText(items.getOriginal_line_no());
        holder.no_of_attachments.setText(items.getText_attachment());

        holder.spinner_type.setText(String.valueOf(items.getSub_reg_type()));

//        if(items.getSub_reg_type().equals("INCOMING"))
//        {
//            holder.spinner_type.setSelection(0);
//        }
//        else
//        {
//            holder.spinner_type.setSelection(1);
//        }
    }

    public void updateSubmittalLine(final Context context, final String docType, final String description,
                                    final String variationFromContract, final String variationFromContractDocDsc,
                                    final String status, final String submittalRegisterType, final String lineNo,
                                    final ProgressDialog pDialog)
    {
        JSONObject object = new JSONObject();

        try {

            object.put("docType",docType);
            object.put("description",description);
            object.put("variationFromContract",variationFromContract);
            object.put("variationFromContractDocDsc",variationFromContractDocDsc);
            object.put("status",status);
            object.put("submittalRegisterType",submittalRegisterType);

            Log.d("OBJECT SENT JSON", object.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        String url = pm.getString("SERVER_URL")  + "/putSubmittalLineItems?lineNo=\"" + lineNo + "\"";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.PUT, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("msg").equals("success"))
                                Toast.makeText(context, "Values Saved" , Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();

                            pDialog.dismiss();

                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            pDialog.dismiss();
                        }

                        Log.d("Submittal Line", response.toString());

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
    public int getItemCount()
    {
        return submittalList.size();
    }
}
