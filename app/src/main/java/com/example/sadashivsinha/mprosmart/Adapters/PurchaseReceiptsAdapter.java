package com.example.sadashivsinha.mprosmart.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.ModelLists.PurchaseReceiptList;
import com.example.sadashivsinha.mprosmart.R;

import java.util.List;

/**
 * Created by saDashiv sinha on 10-Mar-16.
 */
public class PurchaseReceiptsAdapter extends RecyclerView.Adapter<PurchaseReceiptsAdapter.MyViewHolder> {

private List<PurchaseReceiptList> purchaseReceiptList;

public class MyViewHolder extends RecyclerView.ViewHolder {
    public TextView item_id, item_received_quantity, item_total_quantity, item_balance, item_last_received;

    public MyViewHolder(final View view) {
        super(view);
        item_id = (TextView) view.findViewById(R.id.item_id);
        item_received_quantity = (TextView) view.findViewById(R.id.item_received_quantity);
        item_total_quantity = (TextView) view.findViewById(R.id.item_total_quantity);
        item_balance = (TextView) view.findViewById(R.id.item_balance);
        item_last_received = (TextView) view.findViewById(R.id.item_last_received);

    }
}

    public PurchaseReceiptsAdapter(List<PurchaseReceiptList> purchaseReceiptList) {
        this.purchaseReceiptList = purchaseReceiptList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_purchase_receipts, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PurchaseReceiptList items = purchaseReceiptList.get(position);

        holder.item_id.setText(String.valueOf(items.getItemId()));
        holder.item_received_quantity.setText(items.getReceivedQuantity());
        holder.item_total_quantity.setText(items.getTotalQuantity());
        holder.item_balance.setText(String.valueOf(items.getBalance()));
        holder.item_last_received.setText(String.valueOf(items.getLastReceived()));
    }

    @Override
    public int getItemCount() {
        return purchaseReceiptList.size();
    }
}
