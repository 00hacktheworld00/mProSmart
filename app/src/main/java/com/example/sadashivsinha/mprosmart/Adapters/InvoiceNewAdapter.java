package com.example.sadashivsinha.mprosmart.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sadashivsinha.mprosmart.ModelLists.InvoiceNewList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.font.HelveticaMedium;
import com.example.sadashivsinha.mprosmart.font.HelveticaRegular;

import java.util.List;

/**
 * Created by saDashiv sinha on 10-Aug-16.
 */
public class InvoiceNewAdapter extends RecyclerView.Adapter<InvoiceNewAdapter.MyViewHolder> {

    private List<InvoiceNewList> invoiceList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private HelveticaRegular text_item_id, text_quantity, text_quan_accepted, text_unit_cost;
        private HelveticaMedium text_total_item_cost;

        public MyViewHolder(final View view) {
            super(view);
            text_item_id = (HelveticaRegular) view.findViewById(R.id.text_item_id);
            text_quantity = (HelveticaRegular) view.findViewById(R.id.text_quantity);
            text_quan_accepted = (HelveticaRegular) view.findViewById(R.id.text_quan_accepted);
            text_unit_cost = (HelveticaRegular) view.findViewById(R.id.text_unit_cost);
            text_total_item_cost = (HelveticaMedium) view.findViewById(R.id.text_total_item_cost);
        }

    }
    public InvoiceNewAdapter(List<InvoiceNewList> invoiceList) {
        this.invoiceList = invoiceList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_invoice_new, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        InvoiceNewList items = invoiceList.get(position);
        holder.text_item_id.setText(String.valueOf(items.getText_item_id()));
        holder.text_quantity.setText(String.valueOf(items.getText_quantity()));
        holder.text_quan_accepted.setText(String.valueOf(items.getText_quan_accepted()));
        holder.text_unit_cost.setText(String.valueOf(items.getText_unit_cost()));
        holder.text_total_item_cost.setText(String.valueOf(items.getTotal_cost()));

    }

    @Override
    public int getItemCount() {
        return invoiceList.size();
    }
}
