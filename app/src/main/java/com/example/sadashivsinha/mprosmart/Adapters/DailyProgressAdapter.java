package com.example.sadashivsinha.mprosmart.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.Activities.DailyProgressDetails;
import com.example.sadashivsinha.mprosmart.ModelLists.DailyProgressList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;

import java.util.List;

/**
 * Created by saDashiv sinha on 22-Jul-16.
 */
public class DailyProgressAdapter extends RecyclerView.Adapter<DailyProgressAdapter.MyViewHolder> {

    private List<DailyProgressList> daily_list;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView sl_no, created_by, weather, date;


        public MyViewHolder(final View view) {
            super(view);
            sl_no = (TextView) view.findViewById(R.id.sl_no);
            date = (TextView) view.findViewById(R.id.date);
            weather = (TextView) view.findViewById(R.id.weather);
            created_by = (TextView) view.findViewById(R.id.created_by);


            final PreferenceManager pm = new PreferenceManager(view.getContext());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(view.getContext(), DailyProgressDetails.class);

                    pm.putString("date", date.getText().toString());
                    pm.putString("weather", weather.getText().toString());
                    pm.putString("created_by", created_by.getText().toString());

                    view.getContext().startActivity(intent);
                }
            });
        }

    }
    public DailyProgressAdapter(List<DailyProgressList> daily_list) {
        this.daily_list = daily_list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_daily_progress, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DailyProgressList items = daily_list.get(position);
        holder.sl_no.setText(String.valueOf(items.getSl_no()));
        holder.created_by.setText(String.valueOf(items.getCreated_by()));
        holder.weather.setText(String.valueOf(items.getWeather()));
        holder.date.setText(String.valueOf(items.getDate()));
    }

    @Override
    public int getItemCount() {
        return daily_list.size();
    }
}