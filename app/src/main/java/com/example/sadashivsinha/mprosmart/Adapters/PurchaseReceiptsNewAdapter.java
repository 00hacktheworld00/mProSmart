package com.example.sadashivsinha.mprosmart.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.Activities.PurchaseReceiptItems;
import com.example.sadashivsinha.mprosmart.ModelLists.PurchaseOrdersList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;

import java.util.List;

/**
 * Created by saDashiv sinha on 23-Mar-16.
 */
public class PurchaseReceiptsNewAdapter extends RecyclerView.Adapter<PurchaseReceiptsNewAdapter.MyViewHolder> {

    private List<PurchaseOrdersList> purchaseOrdersList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView project_no, po_number, date, text_receipt_no, receipt_index;

        public MyViewHolder(final View view) {
            super(view);
            project_no = (TextView) view.findViewById(R.id.text_project_no);
            po_number = (TextView) view.findViewById(R.id.text_po_number);
            date = (TextView) view.findViewById(R.id.text_date);
            text_receipt_no = (TextView) view.findViewById(R.id.text_receipt_no);
            receipt_index = (TextView) view.findViewById(R.id.receipt_index);


            final PreferenceManager pm = new PreferenceManager(view.getContext());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), PurchaseReceiptItems.class);
                    pm.putString("currentReceiptNo", text_receipt_no.getText().toString());
                    pm.putString("poNo", po_number.getText().toString());
                    pm.putString("date", date.getText().toString());
                    itemView.getContext().startActivity(intent);
                }
            });

        }
    }

    public PurchaseReceiptsNewAdapter(List<PurchaseOrdersList> purchaseOrdersList) {
        this.purchaseOrdersList = purchaseOrdersList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_purchase_receipts_new, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PurchaseOrdersList items = purchaseOrdersList.get(position);

        holder.po_number.setText(String.valueOf(items.getPo_number()));
        holder.project_no.setText(items.getProject_number());
        holder.text_receipt_no.setText(items.getReceipt_no());
        holder.receipt_index.setText(items.getReceipt_index());
        holder.date.setText(items.getDate());
    }

    @Override
    public int getItemCount() {
        return purchaseOrdersList.size();
    }
}
