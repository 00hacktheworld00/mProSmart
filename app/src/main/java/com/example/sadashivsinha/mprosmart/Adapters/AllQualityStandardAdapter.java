package com.example.sadashivsinha.mprosmart.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.Activities.QualityStandardActivity;
import com.example.sadashivsinha.mprosmart.ModelLists.AllQualityStandardList;
import com.example.sadashivsinha.mprosmart.R;

import java.util.List;

/**
 * Created by saDashiv sinha on 30-Jun-16.
 */
public class AllQualityStandardAdapter extends RecyclerView.Adapter<AllQualityStandardAdapter.MyViewHolder> {

    private List<AllQualityStandardList> qualityList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView quality_sl_no, standard_no, item_id, item_desc, date_created, created_by, project_id;
        public String projectId, dateCreated, createdBy, standardNo, itemId, itemDesc;

        public MyViewHolder(final View view) {
            super(view);
            quality_sl_no = (TextView) view.findViewById(R.id.quality_sl_no);
            standard_no = (TextView) view.findViewById(R.id.standard_no);
            item_id = (TextView) view.findViewById(R.id.item_id);
            item_desc = (TextView) view.findViewById(R.id.item_desc);
            date_created = (TextView) view.findViewById(R.id.date_created);
            created_by = (TextView) view.findViewById(R.id.created_by);
            project_id = (TextView) view.findViewById(R.id.project_id);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(view.getContext(), QualityStandardActivity.class);

                    standardNo = standard_no.getText().toString();
                    projectId = project_id.getText().toString();
                    itemId = item_id.getText().toString();
                    itemDesc = item_desc.getText().toString();
                    dateCreated = date_created.getText().toString();
                    createdBy = created_by.getText().toString();

                    //passing the values for header on next page

                    intent.putExtra("standardNo", standardNo);
                    intent.putExtra("projectId", projectId);
                    intent.putExtra("itemId", itemId);
                    intent.putExtra("itemDesc", itemDesc);
                    intent.putExtra("dateCreated", dateCreated);
                    intent.putExtra("createdBy", createdBy);


                    view.getContext().startActivity(intent);
                }
            });

        }
    }

    public AllQualityStandardAdapter(List<AllQualityStandardList> qualityList) {
        this.qualityList = qualityList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_all_quality_standard, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AllQualityStandardList items = qualityList.get(position);
        holder.quality_sl_no.setText(String.valueOf(items.getQuality_sl_no()));
        holder.standard_no.setText(items.getStandard_no());
        holder.project_id.setText(items.getProject_id());
        holder.item_id.setText(String.valueOf(items.getItem_id()));
        holder.item_desc.setText(items.getItem_desc());
        holder.date_created.setText(items.getDate_created());
        holder.created_by.setText(items.getCreated_by());
    }

    @Override
    public int getItemCount() {
        return qualityList.size();
    }
}


