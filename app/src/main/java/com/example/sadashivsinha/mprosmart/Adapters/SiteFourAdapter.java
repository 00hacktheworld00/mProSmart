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
public class SiteFourAdapter extends RecyclerView.Adapter<SiteFourAdapter.MyViewHolder> {
    private List<SiteTwoList> itemList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView text_line_no, text_wbs, text_activity, text_item_code, text_quan_issued, text_rec_by;

        public MyViewHolder(final View view) {
            super(view);
            text_line_no = (TextView) view.findViewById(R.id.text_line_no);
            text_wbs = (TextView) view.findViewById(R.id.text_wbs);
            text_activity = (TextView) view.findViewById(R.id.text_activity);
            text_item_code = (TextView) view.findViewById(R.id.text_item_code);
            text_quan_issued = (TextView) view.findViewById(R.id.text_quan_issued);
            text_rec_by = (TextView) view.findViewById(R.id.text_rec_by);
        }
    }


    public SiteFourAdapter(List<SiteTwoList> itemList) {
        this.itemList = itemList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_site_four, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        SiteTwoList items = itemList.get(position);
        holder.text_line_no.setText(String.valueOf(items.getText_line_no()));
        holder.text_wbs.setText(String.valueOf(items.getText_wbs()));
        holder.text_activity.setText(String.valueOf(items.getText_activity()));
        holder.text_item_code.setText(String.valueOf(items.getText_item_code()));
        holder.text_quan_issued.setText(String.valueOf(items.getText_quan_issued()));
        holder.text_rec_by.setText(String.valueOf(items.getText_rec_by()));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}