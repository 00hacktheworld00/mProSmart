package com.example.sadashivsinha.mprosmart.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.Activities.ResourceTimesheetActivity;
import com.example.sadashivsinha.mprosmart.ModelLists.MomList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by saDashiv sinha on 30-Mar-16.
 */
public class AllResourceAdapter extends RecyclerView.Adapter<AllResourceAdapter.MyViewHolder> {

    private List<MomList> momList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView res_index, res_id, res_name, date, created_by;

        public MyViewHolder(final View view) {
            super(view);
            res_index = (TextView) view.findViewById(R.id.res_index);
            res_id = (TextView) view.findViewById(R.id.res_id);
            res_name = (TextView) view.findViewById(R.id.res_name);
            date = (TextView) view.findViewById(R.id.date);
            created_by = (TextView) view.findViewById(R.id.created_by);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(view.getContext(), ResourceTimesheetActivity.class);
                    PreferenceManager pm = new PreferenceManager(view.getContext());
                    pm.putString("resourceId", res_id.getText().toString());
                    pm.putString("resourceName", res_name.getText().toString());
                    pm.putString("resourceDate", date.getText().toString());
                    pm.putString("resourceCreatedBy", created_by.getText().toString());
                    view.getContext().startActivity(intent);
                }
            });
        }

    }
    public AllResourceAdapter(List<MomList> momList) {
        this.momList = momList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_all_resource, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MomList items = momList.get(position);
        holder.res_index.setText(String.valueOf(items.getMom_index()));
        holder.res_id.setText(String.valueOf(items.getMom_rec_no()));
        holder.res_name.setText(String.valueOf(items.getProject_id()));
        holder.created_by.setText(String.valueOf(items.getCreated_by()));

        Date tradeDate = null;
        try {
            tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(items.getDate());

            String formattedDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);
            holder.date.setText(String.valueOf(formattedDate));

        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return momList.size();
    }
}
