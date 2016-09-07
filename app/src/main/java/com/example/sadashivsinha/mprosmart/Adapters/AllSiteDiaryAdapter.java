package com.example.sadashivsinha.mprosmart.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.Activities.SiteDiaryActivity;
import com.example.sadashivsinha.mprosmart.ModelLists.AllSiteDiaryList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;

import java.util.List;

/**
 * Created by saDashiv sinha on 04-Aug-16.
 */
public class AllSiteDiaryAdapter extends RecyclerView.Adapter<AllSiteDiaryAdapter.MyViewHolder> {

    private List<AllSiteDiaryList> site_list;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView sl_no, text_site_date, text_site_id, project_id, created_by;


        public MyViewHolder(final View view) {
            super(view);
            sl_no = (TextView) view.findViewById(R.id.sl_no);
            text_site_date = (TextView) view.findViewById(R.id.text_site_date);
            text_site_id = (TextView) view.findViewById(R.id.text_site_id);
            project_id = (TextView) view.findViewById(R.id.project_id);
            created_by = (TextView) view.findViewById(R.id.created_by);

            final PreferenceManager pm = new PreferenceManager(view.getContext());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(view.getContext(), SiteDiaryActivity.class);
                    pm.putString("currentSiteDiary", text_site_id.getText().toString());
                    pm.putString("currentSiteDate",  text_site_date.getText().toString());
                    view.getContext().startActivity(intent);
                }
            });

        }

    }
    public AllSiteDiaryAdapter(List<AllSiteDiaryList> site_list) {
        this.site_list = site_list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_all_site_diary, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AllSiteDiaryList items = site_list.get(position);
        holder.sl_no.setText(String.valueOf(items.getSl_no()));
        holder.text_site_date.setText(String.valueOf(items.getText_site_date()));
        holder.text_site_id.setText(String.valueOf(items.getText_site_id()));
        holder.project_id.setText(String.valueOf(items.getProject_id()));
        holder.created_by.setText(String.valueOf(items.getCreated_by()));
    }

    @Override
    public int getItemCount() {
        return site_list.size();
    }
}