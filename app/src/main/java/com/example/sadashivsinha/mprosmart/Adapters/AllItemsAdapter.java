package com.example.sadashivsinha.mprosmart.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.Activities.AddItemsActivity;
import com.example.sadashivsinha.mprosmart.ModelLists.AllItemsList;
import com.example.sadashivsinha.mprosmart.R;

import java.util.List;

/**
 * Created by saDashiv sinha on 23-Aug-16.
 */
public class AllItemsAdapter extends RecyclerView.Adapter<AllItemsAdapter.MyViewHolder> {

    private List<AllItemsList> all_items_list;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView sl_no, item_id, item_name, item_desc, uom;
        private ImageButton editBtn;

        public MyViewHolder(final View view) {
            super(view);
            sl_no = (TextView) view.findViewById(R.id.sl_no);
            item_id = (TextView) view.findViewById(R.id.item_id);
            item_name = (TextView) view.findViewById(R.id.item_name);
            item_desc = (TextView) view.findViewById(R.id.item_desc);
            uom = (TextView) view.findViewById(R.id.uom);

            editBtn = (ImageButton) view.findViewById(R.id.editBtn);
        }

    }
    public AllItemsAdapter(List<AllItemsList> all_items_list) {
        this.all_items_list = all_items_list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_all_items, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        AllItemsList items = all_items_list.get(position);
        holder.sl_no.setText(String.valueOf(items.getSl_no()));
        holder.item_id.setText(String.valueOf(items.getItem_id()));
        holder.item_name.setText(String.valueOf(items.getItem_name()));
        holder.item_desc.setText(String.valueOf(items.getItem_desc()));
        holder.uom.setText(String.valueOf(items.getUom()));

        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), AddItemsActivity.class);
                intent.putExtra("editItem", true);
                intent.putExtra("itemId", holder.item_id.getText().toString());
                intent.putExtra("itemName", holder.item_name.getText().toString());
                intent.putExtra("itemDesc", holder.item_desc.getText().toString());
                intent.putExtra("itemUom", holder.uom.getText().toString());
                holder.itemView.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return all_items_list.size();
    }
}