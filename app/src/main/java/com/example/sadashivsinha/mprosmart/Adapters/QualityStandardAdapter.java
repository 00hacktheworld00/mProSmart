package com.example.sadashivsinha.mprosmart.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.ModelLists.QualityStandardList;
import com.example.sadashivsinha.mprosmart.R;

import java.util.List;

/**
 * Created by saDashiv sinha on 30-Jun-16.
 */
public class QualityStandardAdapter extends RecyclerView.Adapter<QualityStandardAdapter.MyViewHolder> {

    private List<QualityStandardList> qualityList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView text_criteria, text_uom, text_result, text_status, text_comments;

        public MyViewHolder(final View view) {
            super(view);
            text_criteria = (TextView) view.findViewById(R.id.text_criteria);
            text_uom = (TextView) view.findViewById(R.id.text_uom);
            text_result = (TextView) view.findViewById(R.id.text_result);
            text_status = (TextView) view.findViewById(R.id.text_status);
            text_comments = (TextView) view.findViewById(R.id.text_comments);

        }
    }

    public QualityStandardAdapter(List<QualityStandardList> qualityList) {
        this.qualityList = qualityList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_quality_standard, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        QualityStandardList items = qualityList.get(position);
        holder.text_criteria.setText(String.valueOf(items.getText_criteria()));
        holder.text_uom.setText(items.getText_uom());
        holder.text_result.setText(items.getText_result());
        holder.text_status.setText(String.valueOf(items.getText_status()));
        holder.text_comments.setText(items.getText_comments());
    }

    @Override
    public int getItemCount() {
        return qualityList.size();
    }
}


