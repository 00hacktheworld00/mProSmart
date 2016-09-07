package com.example.sadashivsinha.mprosmart.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.Activities.QualityPlanActivity;
import com.example.sadashivsinha.mprosmart.ModelLists.AllQualityPlansList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by saDashiv sinha on 30-Jun-16.
 */
public class AllQualityPlansAdapter extends RecyclerView.Adapter<AllQualityPlansAdapter.MyViewHolder> {

    private List<AllQualityPlansList> qualityList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView quality_sl_no, plan_no, project_id, project_name, created_on, created_by;

        public MyViewHolder(final View view) {
            super(view);
            quality_sl_no = (TextView) view.findViewById(R.id.quality_sl_no);
            plan_no = (TextView) view.findViewById(R.id.plan_no);
            project_id = (TextView) view.findViewById(R.id.project_id);
            project_name = (TextView) view.findViewById(R.id.project_name);
            created_on = (TextView) view.findViewById(R.id.created_on);
            created_by = (TextView) view.findViewById(R.id.created_by);

            final PreferenceManager pm = new PreferenceManager(view.getContext());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(view.getContext(), QualityPlanActivity.class);
                    pm.putString("currentQualityPlan", plan_no.getText().toString());

                    view.getContext().startActivity(intent);
                }
            });

        }
    }

    public AllQualityPlansAdapter(List<AllQualityPlansList> qualityList) {
        this.qualityList = qualityList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_all_quality_plan, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AllQualityPlansList items = qualityList.get(position);
        holder.quality_sl_no.setText(String.valueOf(items.getQuality_sl_no()));
        holder.plan_no.setText(items.getPlan_no());
        holder.project_id.setText(items.getProject_id());
        holder.project_name.setText(String.valueOf(items.getProject_name()));
        holder.created_by.setText(items.getCreated_by());


        Date tradeDate = null;
        try {
            tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(items.getCreated_on());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.created_on.setText(new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate));
    }

    @Override
    public int getItemCount() {
        return qualityList.size();
    }
}

