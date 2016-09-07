package com.example.sadashivsinha.mprosmart.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.ModelLists.BoqList;
import com.example.sadashivsinha.mprosmart.R;

import java.util.List;

/**
 * Created by saDashiv sinha on 01-Jul-16.
 */
public class BoqAdapter extends RecyclerView.Adapter<BoqAdapter.MyViewHolder> {

private List<BoqList> boqList;

public class MyViewHolder extends RecyclerView.ViewHolder {

    public TextView text_item, text_quantity, text_uom, text_cost, text_currency, text_total_cost;

    public MyViewHolder(final View view) {
        super(view);
        text_item = (TextView) view.findViewById(R.id.text_item);
        text_quantity = (TextView) view.findViewById(R.id.text_quantity);
        text_uom = (TextView) view.findViewById(R.id.text_uom);
        text_cost = (TextView) view.findViewById(R.id.text_cost);
        text_currency = (TextView) view.findViewById(R.id.text_currency);
        text_total_cost = (TextView) view.findViewById(R.id.text_total_cost);

    }
}

    public BoqAdapter(List<BoqList> boqList) {
        this.boqList = boqList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_boq, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        BoqList items = boqList.get(position);
        holder.text_item.setText(items.getText_item());
        holder.text_quantity.setText(items.getText_quantity());
        holder.text_uom.setText(items.getText_uom());
        holder.text_cost.setText(items.getText_cost());
        holder.text_currency.setText(items.getText_currency());
        holder.text_total_cost.setText(items.getText_total_cost());
    }

    @Override
    public int getItemCount() {
        return boqList.size();
    }
}



