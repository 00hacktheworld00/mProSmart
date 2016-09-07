package com.example.sadashivsinha.mprosmart.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.Activities.QualityCheckListActivity;
import com.example.sadashivsinha.mprosmart.ModelLists.AllQualityChecklistList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;

import java.util.List;

/**
 * Created by saDashiv sinha on 30-Jun-16.
 */
public class AllQualityChecklistAdapter extends RecyclerView.Adapter<AllQualityChecklistAdapter.MyViewHolder> {

    private List<AllQualityChecklistList> qualityList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView quality_sl_no, checklist_no, project_id, project_name, created_on, created_by;
        public String checklistNo, projectId, projectName, createdOn, createdBy;
        public PreferenceManager pm;

        public MyViewHolder(final View view) {
            super(view);
            quality_sl_no = (TextView) view.findViewById(R.id.quality_sl_no);
            checklist_no = (TextView) view.findViewById(R.id.checklist_no);
            project_id = (TextView) view.findViewById(R.id.project_id);
            project_name = (TextView) view.findViewById(R.id.project_name);
            created_on = (TextView) view.findViewById(R.id.created_on);
            created_by = (TextView) view.findViewById(R.id.created_by);

            pm = new PreferenceManager(view.getContext());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(view.getContext(), QualityCheckListActivity.class);

                    checklistNo = checklist_no.getText().toString();
                    projectId = project_id.getText().toString();
                    projectName = project_name.getText().toString();
                    createdOn = created_on.getText().toString();
                    createdBy = created_by.getText().toString();

                    //passing the values for header on next page
                    pm.putString("currentQualityChecklist", checklist_no.getText().toString());

                    view.getContext().startActivity(intent);
                }
            });

        }
    }

    public AllQualityChecklistAdapter(List<AllQualityChecklistList> qualityList) {
        this.qualityList = qualityList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_all_quality_checklist, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AllQualityChecklistList items = qualityList.get(position);
        holder.quality_sl_no.setText(items.getQuality_sl_no());
        holder.checklist_no.setText(items.getChecklist_no());
        holder.project_id.setText(items.getProject_id());
        holder.project_name.setText(String.valueOf(items.getProject_name()));
        holder.created_on.setText(items.getCreated_on());
        holder.created_by.setText(items.getCreated_by());
    }

    @Override
    public int getItemCount() {
        return qualityList.size();
    }
}

