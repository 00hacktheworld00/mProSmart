package com.example.sadashivsinha.mprosmart.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.Activities.SubmittalActivity;
import com.example.sadashivsinha.mprosmart.ModelLists.MomList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;

import java.util.List;

/**
 * Created by saDashiv sinha on 30-Mar-16.
 */
public class AllSubmittalAdapter extends RecyclerView.Adapter<AllSubmittalAdapter.MyViewHolder> {

    private List<MomList> momList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView sl_no, submittal_no, project_id, project_name, date, created_by, sub_reg_id, submittals_type, due_date, status, description;

        public MyViewHolder(final View view) {
            super(view);
            sl_no = (TextView) view.findViewById(R.id.sl_no);
            submittal_no = (TextView) view.findViewById(R.id.submittal_no);
            project_id = (TextView) view.findViewById(R.id.project_id);
            project_name = (TextView) view.findViewById(R.id.project_name);
            date = (TextView) view.findViewById(R.id.date);
            created_by = (TextView) view.findViewById(R.id.created_by);
            sub_reg_id = (TextView) view.findViewById(R.id.sub_reg_id);
            submittals_type = (TextView) view.findViewById(R.id.submittals_type);
            due_date = (TextView) view.findViewById(R.id.due_date);
            status = (TextView) view.findViewById(R.id.status);
            description = (TextView) view.findViewById(R.id.description);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(view.getContext(), SubmittalActivity.class);
                    PreferenceManager pm = new PreferenceManager(view.getContext());
                    pm.putString("submittalNo", submittal_no.getText().toString());
                    pm.putString("submittalProjectId", project_id.getText().toString());
                    pm.putString("submittalProjectName", project_name.getText().toString());
                    pm.putString("submittalDate", date.getText().toString());
                    pm.putString("submittalCreatedBy", created_by.getText().toString());
                    pm.putString("submittalSubRegId", sub_reg_id.getText().toString());
                    pm.putString("submittalType", submittals_type.getText().toString());
                    pm.putString("submittalDueDate", due_date.getText().toString());
                    pm.putString("submittalStatus", status.getText().toString());
                    pm.putString("submittalDesc", description.getText().toString());
                    view.getContext().startActivity(intent);
                }
            });
        }

    }
    public AllSubmittalAdapter(List<MomList> momList) {
        this.momList = momList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_all_submittal, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MomList items = momList.get(position);
        holder.sl_no.setText(items.getSlNo());
        holder.submittal_no.setText(items.getSubmittalId());
        holder.project_id.setText(items.getCurrentProjectNo());
        holder.project_name.setText(items.getProjectName());
        holder.date.setText(items.getCreatedDate());
        holder.created_by.setText(items.getCreatedBy());
        holder.sub_reg_id.setText(items.getSubmittalRegisterId());
        holder.submittals_type.setText(items.getSubmittalsType());
        holder.due_date.setText(items.getDueDate());
        holder.status.setText(items.getStatus());
        holder.description.setText(items.getDescription());
    }

    @Override
    public int getItemCount() {
        return momList.size();
    }
}
