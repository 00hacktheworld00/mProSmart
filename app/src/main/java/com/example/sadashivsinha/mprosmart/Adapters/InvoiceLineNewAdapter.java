package com.example.sadashivsinha.mprosmart.Adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sadashivsinha.mprosmart.ModelLists.PurchaseReceiptCreateNewList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.font.HelveticaRegular;

import java.util.List;

/**
 * Created by saDashiv sinha on 20-Jul-16.
 */
public class InvoiceLineNewAdapter extends RecyclerView.Adapter<InvoiceLineNewAdapter.MyViewHolder> {

    private List<PurchaseReceiptCreateNewList> list;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public HelveticaRegular item_id, new_quantity;
        CardView cardview;

        public MyViewHolder(final View view) {
            super(view);
            item_id = (HelveticaRegular) view.findViewById(R.id.item_id);
            new_quantity = (HelveticaRegular) view.findViewById(R.id.new_quantity);

            cardview = (CardView) view.findViewById(R.id.cardview);

            cardview.setBackgroundColor(view.getContext().getResources().getColor(R.color.transparent_color));
        }
    }

    public InvoiceLineNewAdapter(List<PurchaseReceiptCreateNewList> list) {
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_invoice_line_new, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        PurchaseReceiptCreateNewList items = list.get(position);

        holder.item_id.setText(String.valueOf(items.getItem_id()));
        holder.new_quantity.setText(items.getQuantity());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
