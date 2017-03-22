package com.example.sadashivsinha.mprosmart.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.Activities.AttachmentActivity;
import com.example.sadashivsinha.mprosmart.ModelLists.QualityPlanList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;

import java.util.List;

/**
 * Created by saDashiv sinha on 30-Jun-16.
 */
public class QualityPlanAdapter extends RecyclerView.Adapter<QualityPlanAdapter.MyViewHolder> {

    private List<QualityPlanList> qualityList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView sl_no, process_desc, activity, procedure, accept_criteria, supplier, subcontractor, third_party,
                customer_client, id;

        public ImageButton attachBtn;
        public TextView no_of_attachments;
        PreferenceManager pm;

        public MyViewHolder(final View view) {
            super(view);
            pm = new PreferenceManager(view.getContext());

            sl_no = (TextView) view.findViewById(R.id.sl_no);
            process_desc = (TextView) view.findViewById(R.id.process_desc);
            activity = (TextView) view.findViewById(R.id.activity);
            procedure = (TextView) view.findViewById(R.id.procedure);
            accept_criteria = (TextView) view.findViewById(R.id.accept_criteria);
            supplier = (TextView) view.findViewById(R.id.supplier);
            subcontractor = (TextView) view.findViewById(R.id.subcontractor);
            third_party = (TextView) view.findViewById(R.id.third_party);
            customer_client = (TextView) view.findViewById(R.id.customer_client);
            id = (TextView) view.findViewById(R.id.id);
            no_of_attachments = (TextView) view.findViewById(R.id.no_of_attachments);

            attachBtn = (ImageButton) view.findViewById(R.id.attachBtn);
            attachBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), AttachmentActivity.class);
                    String url = pm.getString("SERVER_URL") + "/getQualityPlanStatusFiles?qualityPlanStatusId=\"" + id.getText().toString() + "\"";
                    Log.d("VIEW IMG URL : ", url);
                    intent.putExtra("viewURL", url);
                    intent.putExtra("viewOnly", true);
                    itemView.getContext().startActivity(intent);

                }
            });
           }
    }

    public QualityPlanAdapter(List<QualityPlanList> qualityList) {
        this.qualityList = qualityList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_quality_plans, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        QualityPlanList items = qualityList.get(position);
        holder.sl_no.setText(String.valueOf(items.getSl_no()));
        holder.process_desc.setText(items.getProcess_desc());
        holder.activity.setText(items.getActivity());
        holder.procedure.setText(String.valueOf(items.getProcedure()));
        holder.accept_criteria.setText(items.getAccept_criteria());
        holder.supplier.setText(items.getSupplier());
        holder.subcontractor.setText(String.valueOf(items.getSubcontractor()));
        holder.third_party.setText(items.getThird_party());
        holder.customer_client.setText(items.getCustomer_client());
        holder.id.setText(items.getId());
        holder.no_of_attachments.setText(items.getNo_of_attachments());
    }

    @Override
    public int getItemCount() {
        return qualityList.size();
    }
}

