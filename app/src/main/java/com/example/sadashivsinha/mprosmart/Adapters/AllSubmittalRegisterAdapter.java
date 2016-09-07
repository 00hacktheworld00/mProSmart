package com.example.sadashivsinha.mprosmart.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.Activities.SubmittalRegisterActivity;
import com.example.sadashivsinha.mprosmart.ModelLists.MomList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;

import java.util.List;

/**
 * Created by saDashiv sinha on 30-Mar-16.
 */
public class AllSubmittalRegisterAdapter extends RecyclerView.Adapter<AllSubmittalRegisterAdapter.MyViewHolder> {

private List<MomList> momList;

public class MyViewHolder extends RecyclerView.ViewHolder {
    private TextView submittal_no, sl_no, project_id, project_name, date, created_by, title, start_date, end_date, priority, status;

    public MyViewHolder(final View view) {
        super(view);
        submittal_no = (TextView) view.findViewById(R.id.submittal_no);
        sl_no = (TextView) view.findViewById(R.id.sl_no);
        project_id = (TextView) view.findViewById(R.id.project_id);
        project_name = (TextView) view.findViewById(R.id.project_name);
        date = (TextView) view.findViewById(R.id.date);
        created_by = (TextView) view.findViewById(R.id.created_by);
        title = (TextView) view.findViewById(R.id.title);
        start_date = (TextView) view.findViewById(R.id.created_by);
        end_date = (TextView) view.findViewById(R.id.title);
        priority = (TextView) view.findViewById(R.id.priority);
        status = (TextView) view.findViewById(R.id.status);

        title.setText("#Submittal Register : ");

        final PreferenceManager pm = new PreferenceManager(view.getContext());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), SubmittalRegisterActivity.class);
                pm.putString("currentProjectName",project_name.getText().toString());
                pm.putString("submittalRegistersId",submittal_no.getText().toString());
                pm.putString("projectId",project_id.getText().toString());
                pm.putString("startDate",start_date.getText().toString());
                pm.putString("EndDate",end_date.getText().toString());
                pm.putString("createdDate",date.getText().toString());
                pm.putString("Status",status.getText().toString());
                pm.putString("priority",priority.getText().toString());
                pm.putString("createdBy",created_by.getText().toString());
                view.getContext().startActivity(intent);
            }
        });
    }

}
    public AllSubmittalRegisterAdapter(List<MomList> momList) {
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
        holder.sl_no.setText(String.valueOf(items.getSl_no()));
        holder.submittal_no.setText(String.valueOf(items.getSubmittal_no()));
        holder.project_id.setText(String.valueOf(items.getProject_id()));
        holder.project_name.setText(String.valueOf(items.getProject_name()));
        holder.date.setText(String.valueOf(items.getDate()));
        holder.created_by.setText(String.valueOf(items.getCreated_by()));
        holder.start_date.setText(String.valueOf(items.getStart_date()));
        holder.end_date.setText(String.valueOf(items.getEnd_date()));
        holder.priority.setText(String.valueOf(items.getStart_date()));
        holder.status.setText(String.valueOf(items.getEnd_date()));
    }

    @Override
    public int getItemCount() {
        return momList.size();
    }
}
