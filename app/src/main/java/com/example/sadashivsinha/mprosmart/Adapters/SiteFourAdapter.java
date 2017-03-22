package com.example.sadashivsinha.mprosmart.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sadashivsinha.mprosmart.ModelLists.SiteTwoList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.font.HelveticaRegular;

import java.util.List;

/**
 * Created by saDashiv sinha on 14-Mar-16.
 */
public class SiteFourAdapter extends RecyclerView.Adapter<SiteFourAdapter.MyViewHolder> {
    private List<SiteTwoList> itemList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public HelveticaRegular text_material_no, text_item_id, text_item_desc, text_quan_issued,text_uom;

        public MyViewHolder(final View view) {
            super(view);
            text_material_no = (HelveticaRegular) view.findViewById(R.id.text_material_no);
            text_item_id = (HelveticaRegular) view.findViewById(R.id.text_item_id);
            text_item_desc = (HelveticaRegular) view.findViewById(R.id.text_item_desc);
            text_uom = (HelveticaRegular) view.findViewById(R.id.text_uom);
            text_quan_issued = (HelveticaRegular) view.findViewById(R.id.text_quan_issued);
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
        holder.text_material_no.setText(String.valueOf(items.getText_material_no()));
        holder.text_item_id.setText(String.valueOf(items.getText_item_id()));
        holder.text_item_desc.setText(String.valueOf(items.getText_item_desc()));
        holder.text_quan_issued.setText(String.valueOf(items.getText_quan_issued()));
        holder.text_uom.setText(String.valueOf(items.getText_uom()));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}