package com.example.sadashivsinha.mprosmart.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.sadashivsinha.mprosmart.ModelLists.DailyProgressDetailsList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.font.HelveticaBold;
import com.example.sadashivsinha.mprosmart.font.HelveticaRegular;

import java.util.List;

/**
 * Created by saDashiv sinha on 23-Aug-16.
 */
public class DailyProgressDetailsAdapter extends RecyclerView.Adapter<DailyProgressDetailsAdapter.MyViewHolder> {

    private List<DailyProgressDetailsList> daily_list;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        HelveticaBold wbs_name, activity;
        HelveticaRegular completed, res_worked, percent_completed_today, percent_completed_total;
        ProgressBar progress_bar_total, progress_bar_today;

        public MyViewHolder(final View view) {
            super(view);
            wbs_name = (HelveticaBold) view.findViewById(R.id.wbs_name);
            activity = (HelveticaBold) view.findViewById(R.id.activity);

            completed = (HelveticaRegular) view.findViewById(R.id.completed);
            res_worked = (HelveticaRegular) view.findViewById(R.id.res_worked);
            percent_completed_today = (HelveticaRegular) view.findViewById(R.id.percent_completed_today);
            percent_completed_total = (HelveticaRegular) view.findViewById(R.id.percent_completed_total);

            progress_bar_total = (ProgressBar) view.findViewById(R.id.progress_bar_total);
            progress_bar_today = (ProgressBar) view.findViewById(R.id.progress_bar_today);
        }

    }
    public DailyProgressDetailsAdapter(List<DailyProgressDetailsList> daily_list) {
        this.daily_list = daily_list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_daily_progress_details, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DailyProgressDetailsList items = daily_list.get(position);
        holder.wbs_name.setText(String.valueOf(items.getWbs_name()));
        holder.activity.setText(String.valueOf(items.getActivity()));
        holder.completed.setText(String.valueOf(items.getCompleted()));
        holder.res_worked.setText(String.valueOf(items.getRes_worked()));
        holder.percent_completed_today.setText(String.valueOf(items.getPercent_completed_today()));
        holder.percent_completed_total.setText(String.valueOf(items.getPercent_completed_total()));

        holder.progress_bar_total.setProgress(Integer.parseInt(items.getPercent_completed_total()));

        int progressToShow = Integer.parseInt(items.getPercent_completed_total()) - Integer.parseInt(items.getPercent_completed_today());

        holder.progress_bar_today.setProgress(progressToShow);
    }

    @Override
    public int getItemCount() {
        return daily_list.size();
    }
}