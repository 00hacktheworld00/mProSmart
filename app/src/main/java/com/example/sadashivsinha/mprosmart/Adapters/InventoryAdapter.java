package com.example.sadashivsinha.mprosmart.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.ModelLists.InventoryList;
import com.example.sadashivsinha.mprosmart.R;

import java.util.List;

/**
 * Created by saDashiv sinha on 01-Jul-16.
 */
public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.MyViewHolder> {

    private List<InventoryList> inventoryList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView text_date, text_received, text_issued, text_closing_bal;

        public MyViewHolder(final View view) {
            super(view);
            text_date = (TextView) view.findViewById(R.id.text_date);
            text_received = (TextView) view.findViewById(R.id.text_received);
            text_issued = (TextView) view.findViewById(R.id.text_issued);
            text_closing_bal = (TextView) view.findViewById(R.id.text_closing_bal);

        }
    }

    public InventoryAdapter(List<InventoryList> inventoryList) {
        this.inventoryList = inventoryList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_inventory, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        InventoryList items = inventoryList.get(position);
        holder.text_date.setText(items.getText_date());
        holder.text_received.setText(items.getText_received());
        holder.text_issued.setText(items.getText_issued());
        holder.text_closing_bal.setText(items.getText_closing_bal());

    }
    @Override
    public int getItemCount() {
        return inventoryList.size();
    }
}



