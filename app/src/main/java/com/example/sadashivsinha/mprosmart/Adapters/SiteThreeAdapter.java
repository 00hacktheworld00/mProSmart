package com.example.sadashivsinha.mprosmart.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.ModelLists.SiteTwoList;
import com.example.sadashivsinha.mprosmart.R;

import java.util.List;

/**
 * Created by saDashiv sinha on 14-Mar-16.
 */
public class SiteThreeAdapter extends RecyclerView.Adapter<SiteThreeAdapter.MyViewHolder> {
    private List<SiteTwoList> itemList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView text_line_no, text_receipt_no, text_po_number, text_vendor_id, text_item_code, text_status;

        public MyViewHolder(final View view) {
            super(view);
            text_line_no = (TextView) view.findViewById(R.id.text_line_no);
            text_receipt_no = (TextView) view.findViewById(R.id.text_receipt_no);
            text_po_number = (TextView) view.findViewById(R.id.text_po_number);
            text_vendor_id = (TextView) view.findViewById(R.id.text_vendor_id);
            text_item_code = (TextView) view.findViewById(R.id.text_item_code);
            text_status = (TextView) view.findViewById(R.id.text_status);
        }
    }


    public SiteThreeAdapter(List<SiteTwoList> itemList) {
        this.itemList = itemList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_site_three, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        SiteTwoList items = itemList.get(position);
        holder.text_line_no.setText(String.valueOf(items.getText_line_no()));
        holder.text_receipt_no.setText(String.valueOf(items.getText_receipt_no()));
        holder.text_po_number.setText(String.valueOf(items.getText_po_number()));
        holder.text_vendor_id.setText(String.valueOf(items.getText_vendor_id()));
        holder.text_item_code.setText(String.valueOf(items.getText_item_code()));
        holder.text_status.setText(String.valueOf(items.getText_status()));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}