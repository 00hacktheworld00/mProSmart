package com.example.sadashivsinha.mprosmart.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.ModelLists.PurchaseItemsList;
import com.example.sadashivsinha.mprosmart.R;

import java.util.List;

/**
 * Created by saDashiv sinha on 04-Apr-16.
 */
public class PurchaseItemsAdapter extends RecyclerView.Adapter<PurchaseItemsAdapter.MyViewHolder> {

    private List<PurchaseItemsList> purchaseItemsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView item_index, item_no, desc, po_quantity, uom, need_by_date, unit_cost, currency, amount;

        public MyViewHolder(final View view) {
            super(view);
            item_index = (TextView) view.findViewById(R.id.item_index);
            item_no = (TextView) view.findViewById(R.id.item_no);
            desc = (TextView) view.findViewById(R.id.desc);
            po_quantity = (TextView) view.findViewById(R.id.po_quantity);
            uom = (TextView) view.findViewById(R.id.uom);
            need_by_date = (TextView) view.findViewById(R.id.need_by_date);
            unit_cost = (TextView) view.findViewById(R.id.unit_cost);
            currency = (TextView) view.findViewById(R.id.currency);
            amount = (TextView) view.findViewById(R.id.amount);

        }
    }

    public PurchaseItemsAdapter(List<PurchaseItemsList> purchaseItemsList) {
        this.purchaseItemsList = purchaseItemsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_purchase_items, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PurchaseItemsList items = purchaseItemsList.get(position);

        holder.item_index.setText(String.valueOf(items.getItem_index()));
        holder.item_no.setText(items.getItem_no());
        holder.desc.setText(items.getDesc());
        holder.po_quantity.setText(items.getPo_quantity());
        holder.uom.setText(items.getUom());
        holder.need_by_date.setText(items.getNeed_by_date());
        holder.unit_cost.setText(items.getUnit_cost());
        holder.currency.setText(items.getCurrency());
        holder.amount.setText(items.getAmount());
    }

    @Override
    public int getItemCount() {
        return purchaseItemsList.size();
    }
}
