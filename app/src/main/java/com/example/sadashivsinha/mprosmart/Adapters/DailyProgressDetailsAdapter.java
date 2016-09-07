package com.example.sadashivsinha.mprosmart.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
        HelveticaRegular target_date, completed, percent_completed, res_worked, weather_title;
        ImageView weather_icon;

        public MyViewHolder(final View view) {
            super(view);
            wbs_name = (HelveticaBold) view.findViewById(R.id.wbs_name);
            activity = (HelveticaBold) view.findViewById(R.id.activity);

            target_date = (HelveticaRegular) view.findViewById(R.id.target_date);
            completed = (HelveticaRegular) view.findViewById(R.id.completed);
            percent_completed = (HelveticaRegular) view.findViewById(R.id.percent_completed);
            res_worked = (HelveticaRegular) view.findViewById(R.id.res_worked);
            weather_title = (HelveticaRegular) view.findViewById(R.id.weather_title);

            weather_icon = (ImageView) view.findViewById(R.id.weather_icon);
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
        holder.target_date.setText(String.valueOf(items.getTarget_date()));
        holder.completed.setText(String.valueOf(items.getCompleted()));
        holder.percent_completed.setText(String.valueOf(items.getPercent_completed()));
        holder.res_worked.setText(String.valueOf(items.getRes_worked()));
        holder.weather_title.setText(String.valueOf(items.getWeather_title()));

        switch (items.getWeather_title()) {
            case "Cloudy weather with high speed wind":
                holder.weather_icon.setBackgroundResource(R.drawable.weather_sunny_cloudy);
                break;
            case "Sunny weather":
                holder.weather_icon.setBackgroundResource(R.drawable.weather_sunny);
                break;
            case "Raining weather with wind":
                holder.weather_icon.setBackgroundResource(R.drawable.weather_raining);
                break;
            case "Thunderstorms":
                holder.weather_icon.setBackgroundResource(R.drawable.weather_thunder);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return daily_list.size();
    }
}