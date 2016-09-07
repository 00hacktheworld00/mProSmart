package com.example.sadashivsinha.mprosmart.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.Activities.SubcontractorActivity;
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
public class AllSubcontractorAdapter extends RecyclerView.Adapter<AllSubcontractorAdapter.MyViewHolder> {

    private List<MomList> momList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView sub_index, sub_id, sub_name, date, created_by;

        public MyViewHolder(final View view) {
            super(view);
            sub_index = (TextView) view.findViewById(R.id.sub_index);
            sub_id = (TextView) view.findViewById(R.id.sub_id);
            sub_name = (TextView) view.findViewById(R.id.sub_name);
            date = (TextView) view.findViewById(R.id.date);
            created_by = (TextView) view.findViewById(R.id.created_by);

            final PreferenceManager pm = new PreferenceManager(view.getContext());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(view.getContext(), SubcontractorActivity.class);
                    String subcontractorId = sub_id.getText().toString();
                    pm.putString("subcontractorId",subcontractorId);
                    pm.putString("subcontractorName",sub_name.getText().toString());
                    pm.putString("subcontractorDate",date.getText().toString());
                    pm.putString("subcontractorCreatedBy",created_by.getText().toString());
                    view.getContext().startActivity(intent);
                }
            });
        }

    }
    public AllSubcontractorAdapter(List<MomList> momList) {
        this.momList = momList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_all_subcontractor, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MomList items = momList.get(position);
        holder.sub_index.setText(String.valueOf(items.getText_line_no()));
        holder.sub_id.setText(String.valueOf(items.getText_matter()));
        holder.sub_name.setText(String.valueOf(items.getText_responsible()));
        holder.created_by.setText(String.valueOf(items.getText_date()));


        Date tradeDate = null;
        try {
            tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(items.getText_attachments());

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
