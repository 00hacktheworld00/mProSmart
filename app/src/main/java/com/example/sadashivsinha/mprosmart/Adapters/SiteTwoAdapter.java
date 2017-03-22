package com.example.sadashivsinha.mprosmart.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sadashivsinha.mprosmart.ModelLists.SiteTwoList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.font.HelveticaRegular;

import java.util.List;

/**
 * Created by saDashiv sinha on 14-Mar-16.
 */
public class SiteTwoAdapter extends RecyclerView.Adapter<SiteTwoAdapter.MyViewHolder> {
    private List<SiteTwoList> itemList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public HelveticaRegular text_wbs, text_activities, text_res_name, text_total_hours, text_res_timesheet_id;

        public MyViewHolder(final View view) {
            super(view);
            text_res_timesheet_id = (HelveticaRegular) view.findViewById(R.id.text_res_timesheet_id);
            text_wbs = (HelveticaRegular) view.findViewById(R.id.text_wbs);
            text_activities = (HelveticaRegular) view.findViewById(R.id.text_activities);
            text_res_name = (HelveticaRegular) view.findViewById(R.id.text_res_name);
            text_total_hours = (HelveticaRegular) view.findViewById(R.id.text_total_hours);

        }
    }


    public SiteTwoAdapter(List<SiteTwoList> itemList) {
        this.itemList = itemList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_site_two, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        SiteTwoList items = itemList.get(position);
        holder.text_res_timesheet_id.setText(String.valueOf(items.getResourceTimesheetId()));
        holder.text_wbs.setText(String.valueOf(items.getText_wbs()));
        holder.text_activities.setText(String.valueOf(items.getText_activities()));
        holder.text_res_name.setText(String.valueOf(items.getText_res_name()));
        holder.text_total_hours.setText(String.valueOf(items.getText_total_hours()));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}