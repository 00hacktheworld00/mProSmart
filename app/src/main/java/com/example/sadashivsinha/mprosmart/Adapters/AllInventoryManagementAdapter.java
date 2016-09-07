package com.example.sadashivsinha.mprosmart.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sadashivsinha.mprosmart.Activities.InventoryManagementActivity;
import com.example.sadashivsinha.mprosmart.ModelLists.AllInventoryList;
import com.example.sadashivsinha.mprosmart.R;

import java.util.List;

/**
 * Created by saDashiv sinha on 01-Jul-16.
 */
public class AllInventoryManagementAdapter extends RecyclerView.Adapter<AllInventoryManagementAdapter.MyViewHolder> {

    private List<AllInventoryList> inventoryList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView text_inventory_no, text_project_id, text_project_desc, text_item_id, text_item_desc, text_date,
                text_show_transact, text_to_date, text_from_date, inventory_sl_no;

        LinearLayout to_layout, from_layout;

        String inventoryNo, projectId, projectDesc, itemId, itemDesc, date, showTransact, toDate, fromDate;

        public MyViewHolder(final View view) {
            super(view);
            inventory_sl_no = (TextView) view.findViewById(R.id.inventory_sl_no);
            text_inventory_no = (TextView) view.findViewById(R.id.text_inventory_no);
            text_project_id = (TextView) view.findViewById(R.id.text_project_id);
            text_project_desc = (TextView) view.findViewById(R.id.text_project_desc);
            text_item_id = (TextView) view.findViewById(R.id.text_item_id);
            text_item_desc = (TextView) view.findViewById(R.id.text_item_desc);
            text_date = (TextView) view.findViewById(R.id.text_date);
            text_show_transact = (TextView) view.findViewById(R.id.text_show_transact);
            text_to_date = (TextView) view.findViewById(R.id.text_to_date);
            text_from_date = (TextView) view.findViewById(R.id.text_from_date);

            to_layout = (LinearLayout) view.findViewById(R.id.to_layout);
            from_layout = (LinearLayout) view.findViewById(R.id.from_layout);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(view.getContext(), InventoryManagementActivity.class);

                    inventoryNo = text_inventory_no.getText().toString();
                    projectId = text_project_id.getText().toString();
                    projectDesc = text_project_desc.getText().toString();
                    itemId = text_item_id.getText().toString();
                    itemDesc = text_item_desc.getText().toString();
                    date = text_date.getText().toString();
                    showTransact = text_show_transact.getText().toString();
                    toDate = text_to_date.getText().toString();
                    fromDate = text_from_date.getText().toString();

                    //passing the values for header on next page

                    intent.putExtra("inventoryNo", inventoryNo);
                    intent.putExtra("projectId", projectId);
                    intent.putExtra("projectDesc", projectDesc);
                    intent.putExtra("itemId", itemId);
                    intent.putExtra("itemDesc", itemDesc);
                    intent.putExtra("date", date);
                    intent.putExtra("toDate", toDate);
                    intent.putExtra("fromDate", fromDate);

                    if(showTransact.equals("YES"))
                    {
                        view.getContext().startActivity(intent);
                    }

                    else
                    {
                        Toast.makeText(view.getContext(), "Transactions cannot be shown for this inventory.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }
    public AllInventoryManagementAdapter(List<AllInventoryList> inventoryList) {
        this.inventoryList = inventoryList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_all_inventory, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AllInventoryList items = inventoryList.get(position);
        holder.inventory_sl_no.setText(String.valueOf(items.getInventory_sl_no()));
        holder.text_inventory_no.setText(String.valueOf(items.getText_inventory_no()));
        holder.text_project_id.setText(String.valueOf(items.getText_project_id()));
        holder.text_project_desc.setText(String.valueOf(items.getText_project_desc()));
        holder.text_item_id.setText(String.valueOf(items.getText_item_id()));
        holder.text_item_desc.setText(String.valueOf(items.getText_item_desc()));
        holder.text_date.setText(String.valueOf(items.getText_date()));
        holder.text_show_transact.setText(String.valueOf(items.getText_show_transact()));
        holder.text_to_date.setText(String.valueOf(items.getText_to_date()));
        holder.text_from_date.setText(String.valueOf(items.getText_from_date()));

        if(holder.text_show_transact.getText().toString().equals("NO"))
        {
            holder.to_layout.setVisibility(View.GONE);
            holder.from_layout.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return inventoryList.size();
    }
}