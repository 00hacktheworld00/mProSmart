package com.example.sadashivsinha.mprosmart.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.ModelLists.MaterialIssueList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.font.HelveticaBold;

import java.util.List;

/**
 * Created by saDashiv sinha on 01-Jul-16.
 */
public class MaterialIssueAdapter extends RecyclerView.Adapter<MaterialIssueAdapter.MyViewHolder> {

    private List<MaterialIssueList> materialList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView text_line_no, text_item_id, text_item_desc, text_quantity_issued, text_uom;
        public HelveticaBold text_desc_label;

        public MyViewHolder(final View view) {
            super(view);
            text_line_no = (TextView) view.findViewById(R.id.text_line_no);
            text_item_id = (TextView) view.findViewById(R.id.text_item_id);
            text_item_desc = (TextView) view.findViewById(R.id.text_item_desc);
            text_quantity_issued = (TextView) view.findViewById(R.id.text_quantity_issued);
            text_uom = (TextView) view.findViewById(R.id.text_uom);

            text_desc_label = (HelveticaBold) view.findViewById(R.id.text_desc_label);

        }
    }

    public MaterialIssueAdapter(List<MaterialIssueList> materialList) {
        this.materialList = materialList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_material_issue, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MaterialIssueList items = materialList.get(position);
        holder.text_line_no.setText(items.getText_line_no());
        holder.text_item_id.setText(items.getText_item_id());
        holder.text_item_desc.setText(items.getText_item_desc());
        holder.text_quantity_issued.setText(items.getText_quantity_issued());
        holder.text_uom.setText(items.getText_uom());

        if(items.getText_item_desc().equals(""))
        {
            holder.text_item_desc.setVisibility(View.GONE);
            holder.text_desc_label.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return materialList.size();
    }
}



