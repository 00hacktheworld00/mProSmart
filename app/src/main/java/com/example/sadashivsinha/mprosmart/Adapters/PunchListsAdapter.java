package com.example.sadashivsinha.mprosmart.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.Activities.PunchListActivity;
import com.example.sadashivsinha.mprosmart.ModelLists.MomList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by saDashiv sinha on 29-Mar-16.
 */
public class PunchListsAdapter extends RecyclerView.Adapter<PunchListsAdapter.MyViewHolder> {

    private List<MomList> momList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView punch_index, punch_list_no, project_id, project_name, date, vendor_id, vendor_name, created_by;

        public MyViewHolder(final View view) {
            super(view);
            punch_index = (TextView) view.findViewById(R.id.punch_index);
            punch_list_no = (TextView) view.findViewById(R.id.punch_list_no);
            project_id = (TextView) view.findViewById(R.id.project_id);
            project_name = (TextView) view.findViewById(R.id.project_name);
            date = (TextView) view.findViewById(R.id.date);
            created_by = (TextView) view.findViewById(R.id.created_by);
            vendor_id = (TextView) view.findViewById(R.id.vendor_id);
            vendor_name = (TextView) view.findViewById(R.id.vendor_name);

            final PreferenceManager pm = new PreferenceManager(view.getContext());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(view.getContext(), PunchListActivity.class);
                    String punchListNo = punch_list_no.getText().toString();
                    pm.putString("punchListNo",punchListNo);
                    view.getContext().startActivity(intent);
                }
            });
        }

    }
    public PunchListsAdapter(List<MomList> momList) {
        this.momList = momList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_punch_lists, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MomList items = momList.get(position);
        holder.punch_index.setText(String.valueOf(items.getPunch_index()));
        holder.punch_list_no.setText(String.valueOf(items.getPunch_list_no()));
        holder.project_id.setText(String.valueOf(items.getProject_id()));
        holder.project_name.setText(String.valueOf(items.getProject_name()));
        holder.created_by.setText(String.valueOf(items.getCreated_by()));
        holder.vendor_id.setText(String.valueOf(items.getVendor_id()));
        holder.vendor_name.setText(String.valueOf(items.getVendor_name()));

        Date tradeDate = null;

        try {
            tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(items.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String formattedDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate);
        holder.date.setText(String.valueOf(formattedDate));
    }

    @Override
    public int getItemCount() {
        return momList.size();
    }
}
