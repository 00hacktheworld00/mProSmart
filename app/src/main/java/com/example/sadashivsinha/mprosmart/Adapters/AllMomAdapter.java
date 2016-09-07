package com.example.sadashivsinha.mprosmart.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.Activities.MomActivity;
import com.example.sadashivsinha.mprosmart.ModelLists.MomList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;

import java.util.List;

/**
 * Created by saDashiv sinha on 30-Mar-16.
 */
public class AllMomAdapter extends RecyclerView.Adapter<AllMomAdapter.MyViewHolder> {

    private List<MomList> momList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mom_index, mom_rec_no, project_id, project_name, date, created_by;

        public MyViewHolder(final View view) {
            super(view);
            mom_index = (TextView) view.findViewById(R.id.mom_index);
            mom_rec_no = (TextView) view.findViewById(R.id.mom_rec_no);
            project_id = (TextView) view.findViewById(R.id.project_id);
            project_name = (TextView) view.findViewById(R.id.project_name);
            date = (TextView) view.findViewById(R.id.date);
            created_by = (TextView) view.findViewById(R.id.created_by);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(view.getContext(), MomActivity.class);
                    String momNo = mom_rec_no.getText().toString();
                    PreferenceManager pm = new PreferenceManager(view.getContext());
                    pm.putString("momId", momNo);
                    pm.putString("projectId", project_id.getText().toString());
                    pm.putString("projectName", project_name.getText().toString());
                    pm.putString("date", date.getText().toString());
                    pm.putString("createdBy", created_by.getText().toString());
                    view.getContext().startActivity(intent);
                }
            });
        }

    }
    public AllMomAdapter(List<MomList> momList) {
        this.momList = momList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_all_mom, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MomList items = momList.get(position);
        holder.mom_index.setText(String.valueOf(items.getMom_index()));
        holder.mom_rec_no.setText(String.valueOf(items.getMom_rec_no()));
        holder.project_id.setText(String.valueOf(items.getProject_id()));
        holder.project_name.setText(String.valueOf(items.getProject_name()));
        holder.date.setText(String.valueOf(items.getDate()));
        holder.created_by.setText(String.valueOf(items.getCreated_by()));
    }

    @Override
    public int getItemCount() {
        return momList.size();
    }
}