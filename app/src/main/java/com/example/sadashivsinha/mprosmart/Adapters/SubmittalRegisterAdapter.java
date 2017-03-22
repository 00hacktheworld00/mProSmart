package com.example.sadashivsinha.mprosmart.Adapters;

import android.content.Intent;
import android.os.Build;
import android.support.design.widget.Snackbar;
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

import com.example.sadashivsinha.mprosmart.Activities.AttachmentActivity;
import com.example.sadashivsinha.mprosmart.ModelLists.SubmittalList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import java.util.List;

/**
 * Created by saDashiv sinha on 15-Mar-16.
 */
public class SubmittalRegisterAdapter extends RecyclerView.Adapter<SubmittalRegisterAdapter.MyViewHolder> {

    public List<SubmittalList> submittalList;
    PreferenceManager pm;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView line_no, original_line_no, no_of_attachments;
        public EditText text_sub_title, text_sub_type, text_contract_id;
        public Button editBtn;
        public ImageButton attachBtn;
        public BetterSpinner spinner_status;

        public MyViewHolder(final View view) {
            super(view);

            pm = new PreferenceManager(view.getContext());

            line_no = (TextView) view.findViewById(R.id.text_line_no);
            text_sub_title = (EditText) view.findViewById(R.id.text_sub_title);;
            text_sub_type = (EditText) view.findViewById(R.id.text_sub_type);
            text_contract_id = (EditText) view.findViewById(R.id.text_contract_id);
            editBtn = (Button) view.findViewById(R.id.editBtn);
            attachBtn = (ImageButton) view.findViewById(R.id.attachBtn);
            original_line_no = (TextView) view.findViewById(R.id.original_line_no);
            no_of_attachments = (TextView) view.findViewById(R.id.no_of_attachments);

            spinner_status = (BetterSpinner) view.findViewById(R.id.spinner_status);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(),
                    android.R.layout.simple_dropdown_item_1line,new String[] {"ACTIVE", "INACTIVE", "PENDING"});
            spinner_status.setAdapter(adapter);


            attachBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), AttachmentActivity.class);
                    String url = pm.getString("SERVER_URL") + "/getSubmittalregisterLineItemsFiles?submittalregisterLineItemsId=\"" + original_line_no.getText().toString() + "\"";
                    Log.d("VIEW IMG URL : ", url);
                    intent.putExtra("viewURL", url);
                    intent.putExtra("viewOnly", true);
                    itemView.getContext().startActivity(intent);

                    }
                });

            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(text_sub_title.isEnabled() && text_sub_type.isEnabled() && spinner_status.isEnabled() &&
                            text_contract_id.isEnabled())
                    {
                        text_sub_title.setEnabled(false);
                        text_sub_type.setEnabled(false);
                        spinner_status.setEnabled(false);
                        text_contract_id.setEnabled(false);

                        editBtn.setText("EDIT");

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            editBtn.setBackgroundTintList(view.getContext().getResources().getColorStateList(R.color.colorPrimary));
                        }

                        Snackbar snackbar = Snackbar.make(view,"Values Saved.",Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }

                    else
                    {
                        text_sub_title.setEnabled(true);
                        text_sub_type.setEnabled(true);
                        spinner_status.setEnabled(true);
                        text_contract_id.setEnabled(true);

                        text_sub_title.requestFocus();
                        editBtn.setText("SAVE");

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            editBtn.setBackgroundTintList(view.getContext().getResources().getColorStateList(R.color.success_green));
                        }
                    }
                }
            });
        }
    }
    public SubmittalRegisterAdapter(List<SubmittalList> submittalList) {
        this.submittalList = submittalList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_submittals_register, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        SubmittalList items = submittalList.get(position);
        holder.line_no.setText(String.valueOf(items.getLine_no()));
        holder.text_sub_title.setText(items.getText_sub_title());
        holder.text_sub_type.setText(items.getText_sub_type());
        holder.spinner_status.setText(String.valueOf(items.getText_status()));
        holder.text_contract_id.setText(items.getText_contract_id());
        holder.original_line_no.setText(items.getOriginal_line_no());
        holder.no_of_attachments.setText(String.valueOf(items.getNoOfAttachments()));

    }

    @Override
    public int getItemCount()
    {
        return submittalList.size();
    }
}
