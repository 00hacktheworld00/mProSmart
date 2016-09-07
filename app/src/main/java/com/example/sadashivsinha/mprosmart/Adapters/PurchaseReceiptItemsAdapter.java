package com.example.sadashivsinha.mprosmart.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.ModelLists.PurchaseReceiptCreateNewList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.font.HelveticaRegular;

import java.util.List;

/**
 * Created by saDashiv sinha on 27-Jul-16.
 */
public class PurchaseReceiptItemsAdapter extends RecyclerView.Adapter<PurchaseReceiptItemsAdapter.MyViewHolder> {

    private List<PurchaseReceiptCreateNewList> list;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView item_id;
        HelveticaRegular po_quantity, new_quantity, unit_cost;

        public MyViewHolder(final View view) {
            super(view);
            item_id = (TextView) view.findViewById(R.id.item_id);
            po_quantity = (HelveticaRegular) view.findViewById(R.id.po_quantity);
            new_quantity = (HelveticaRegular) view.findViewById(R.id.new_quantity);
            unit_cost = (HelveticaRegular) view.findViewById(R.id.unit_cost);
        }
    }

    public PurchaseReceiptItemsAdapter(List<PurchaseReceiptCreateNewList> list) {
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_items_inside_receipt_view, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        PurchaseReceiptCreateNewList items = list.get(position);

        holder.item_id.setText(String.valueOf(items.getItem_id()));
        holder.po_quantity.setText(items.getQuantity());
        holder.new_quantity.setText(items.getNew_quantity());
        holder.unit_cost.setText(items.getUnit_cost());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}