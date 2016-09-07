package com.example.sadashivsinha.mprosmart.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.ModelLists.InventoryViewList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.font.HelveticaBold;
import com.example.sadashivsinha.mprosmart.font.HelveticaRegular;

import java.util.List;

/**
 * Created by saDashiv sinha on 27-Jul-16.
 */
public class InventoryViewAdapter extends RecyclerView.Adapter<InventoryViewAdapter.MyViewHolder> {

    private List<InventoryViewList> inventoryList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private HelveticaBold pr_no, title;
        private HelveticaRegular inventory_total, item_added_issued_title, item_added_issued;
        private TextView text_date;
        private ImageButton icon;

        public MyViewHolder(final View view) {
            super(view);
            pr_no = (HelveticaBold) view.findViewById(R.id.pr_no);
            title = (HelveticaBold) view.findViewById(R.id.title);

            inventory_total = (HelveticaRegular) view.findViewById(R.id.inventory_total);
            item_added_issued_title = (HelveticaRegular) view.findViewById(R.id.item_added_issued_title);
            item_added_issued = (HelveticaRegular) view.findViewById(R.id.item_added_issued);

            text_date = (TextView) view.findViewById(R.id.text_date);

            icon = (ImageButton) view.findViewById(R.id.icon);

        }

    }
    public InventoryViewAdapter(List<InventoryViewList> inventoryList) {
        this.inventoryList = inventoryList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_inventory_view, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        InventoryViewList items = inventoryList.get(position);
        holder.pr_no.setText(String.valueOf(items.getPr_no()));
        holder.inventory_total.setText(String.valueOf(items.getInventory_total()));
        holder.text_date.setText(String.valueOf(items.getText_date()));
        holder.item_added_issued.setText(items.getItem_added_issued());

        if(items.getTitle().equals("1"))
        {
            holder.title.setText("Material Issue No. ");
            holder.icon.setImageResource(R.drawable.ic_minus_square);
            holder.item_added_issued_title.setText("Item Issued - ");
        }

    }

    @Override
    public int getItemCount() {
        return inventoryList.size();
    }
}
