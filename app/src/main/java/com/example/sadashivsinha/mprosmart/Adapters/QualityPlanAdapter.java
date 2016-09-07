package com.example.sadashivsinha.mprosmart.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.ModelLists.QualityPlanList;
import com.example.sadashivsinha.mprosmart.R;

import java.util.List;

/**
 * Created by saDashiv sinha on 30-Jun-16.
 */
public class QualityPlanAdapter extends RecyclerView.Adapter<QualityPlanAdapter.MyViewHolder> {

    private List<QualityPlanList> qualityList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView sl_no, process_desc, activity, procedure, accept_criteria, supplier, subcontractor, third_party,
                customer_client, id;

        public MyViewHolder(final View view) {
            super(view);
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
    }

    @Override
    public int getItemCount() {
        return qualityList.size();
    }
}

