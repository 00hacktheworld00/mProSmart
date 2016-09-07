package com.example.sadashivsinha.mprosmart.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.Activities.QualityControlNew;
import com.example.sadashivsinha.mprosmart.ModelLists.MomList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;

import java.util.List;

/**
 * Created by saDashiv sinha on 30-Mar-16.
 */
public class AllQualityAdapter extends RecyclerView.Adapter<AllQualityAdapter.MyViewHolder> {

    private List<MomList> momList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView quality_index, qir_no, vendor_id, receipt_no, project_id, created_by, purchase_order;

        public MyViewHolder(final View view) {
            super(view);
            quality_index = (TextView) view.findViewById(R.id.quality_index);
            qir_no = (TextView) view.findViewById(R.id.qir_no);
            vendor_id = (TextView) view.findViewById(R.id.vendor_id);
            receipt_no = (TextView) view.findViewById(R.id.receipt_no);
            project_id = (TextView) view.findViewById(R.id.project_id);
            created_by = (TextView) view.findViewById(R.id.created_by);
            purchase_order = (TextView) view.findViewById(R.id.purchase_order);


            final PreferenceManager pm = new PreferenceManager(view.getContext());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(view.getContext(), QualityControl.class);
                    Intent intent = new Intent(view.getContext(), QualityControlNew.class);
                    String qirNo = qir_no.getText().toString();
                    pm.putString("qirNo",qirNo);
                    String receiptNo = receipt_no.getText().toString();
                    pm.putString("receiptNo", receiptNo);
                    String currentQualityPoNo = purchase_order.getText().toString();
                    pm.putString("currentQualityPoNo", currentQualityPoNo);
                    pm.putString("vendorId", vendor_id.getText().toString());
                    pm.putString("createdBy", created_by.getText().toString());
                    view.getContext().startActivity(intent);
                }
            });
        }

    }
    public AllQualityAdapter(List<MomList> momList) {
        this.momList = momList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_all_quality, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MomList items = momList.get(position);
        holder.quality_index.setText(String.valueOf(items.getQuality_index()));
        holder.qir_no.setText(String.valueOf(items.getQir_no()));
        holder.vendor_id.setText(String.valueOf(items.getVendor_id()));
        holder.receipt_no.setText(String.valueOf(items.getReceipt_no()));
        holder.project_id.setText(String.valueOf(items.getProject_id()));
        holder.created_by.setText(String.valueOf(items.getCreated_by()));
        holder.purchase_order.setText(String.valueOf(items.getPurchase_order()));
    }
    @Override
    public int getItemCount() {
        return momList.size();
    }
}