package com.example.sadashivsinha.mprosmart.Adapters;

import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.ModelLists.SubmittalList;
import com.example.sadashivsinha.mprosmart.R;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import java.util.List;

/**
 * Created by saDashiv sinha on 15-Mar-16.
 */
public class SubmittalAdapter extends RecyclerView.Adapter<SubmittalAdapter.MyViewHolder> {

    public List<SubmittalList> submittalList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView line_no;
        public EditText text_doc_type, text_short_desc, text_variation, text_variation_desc;
        public BetterSpinner spinner_status;
        public Button editBtn;

        public MyViewHolder(final View view) {
            super(view);
            line_no = (TextView) view.findViewById(R.id.text_line_no);
            text_doc_type = (EditText) view.findViewById(R.id.text_doc_type);;
            text_short_desc = (EditText) view.findViewById(R.id.text_short_desc);
            text_variation = (EditText) view.findViewById(R.id.text_variation);
            text_variation_desc = (EditText) view.findViewById(R.id.text_variation_desc);

            spinner_status = (BetterSpinner) view.findViewById(R.id.spinner_status);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(),
                    android.R.layout.simple_dropdown_item_1line,new String[] {"ACTIVE", "INACTIVE"});
            spinner_status.setAdapter(adapter);

            editBtn = (Button) view.findViewById(R.id.editBtn);

            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(spinner_status.isEnabled() && text_doc_type.isEnabled() && text_short_desc.isEnabled()
                            && text_variation_desc.isEnabled() && text_variation.isEnabled())
                    {
                        spinner_status.setEnabled(false);
                        text_doc_type.setEnabled(false);
                        text_short_desc.setEnabled(false);
                        text_variation.setEnabled(false);
                        text_variation_desc.setEnabled(false);

                        editBtn.setText("EDIT");

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            editBtn.setBackgroundTintList(view.getContext().getResources().getColorStateList(R.color.colorPrimary));
                        }

                        Snackbar snackbar = Snackbar.make(view,"Values Saved.",Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }

                    else
                    {
                        spinner_status.setEnabled(true);
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
    }


    @Override
    public int getItemCount()
    {
        return submittalList.size();
    }
}
