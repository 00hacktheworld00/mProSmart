package com.example.sadashivsinha.mprosmart.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.sadashivsinha.mprosmart.Activities.AddResourceActivity;
import com.example.sadashivsinha.mprosmart.ModelLists.AllAddResourcesList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.font.HelveticaBold;
import com.example.sadashivsinha.mprosmart.font.HelveticaRegular;

import java.util.List;

/**
 * Created by saDashiv sinha on 25-Jul-16.
 */
public class AllAddResourcesAdapter extends RecyclerView.Adapter<AllAddResourcesAdapter.MyViewHolder> {

    private List<AllAddResourcesList> resources_list;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private HelveticaRegular sl_no,res_type, designation, email, phone, rate_per_hour, currency, res_subcontractor;
        private HelveticaBold  res_id, res_name;
        private ImageButton editBtn;
        LinearLayout sub_layout;


        public MyViewHolder(final View view) {
            super(view);
            sl_no = (HelveticaRegular) view.findViewById(R.id.sl_no);
            res_id = (HelveticaBold) view.findViewById(R.id.res_id);
            res_name = (HelveticaBold) view.findViewById(R.id.res_name);
            res_type = (HelveticaRegular) view.findViewById(R.id.res_type);
            designation = (HelveticaRegular) view.findViewById(R.id.designation);
            email = (HelveticaRegular) view.findViewById(R.id.email);
            phone = (HelveticaRegular) view.findViewById(R.id.phone);
            rate_per_hour = (HelveticaRegular) view.findViewById(R.id.rate_per_hour);
            currency = (HelveticaRegular) view.findViewById(R.id.currency);
            res_subcontractor = (HelveticaRegular) view.findViewById(R.id.res_subcontractor);

            sub_layout = (LinearLayout) view.findViewById(R.id.sub_layout);

            editBtn = (ImageButton) view.findViewById(R.id.editBtn);

            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(view.getContext(), AddResourceActivity.class);
                    intent.putExtra("edit", "yes");
                    intent.putExtra("resId", res_id.getText().toString());
                    view.getContext().startActivity(intent);
                }
            });

        }

    }
    public AllAddResourcesAdapter(List<AllAddResourcesList> resources_list) {
        this.resources_list = resources_list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_all_add_resources, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AllAddResourcesList items = resources_list.get(position);
        holder.sl_no.setText(String.valueOf(items.getSl_no()));
        holder.res_id.setText(String.valueOf(items.getRes_id()));
        holder.res_name.setText(String.valueOf(items.getRes_name()));
        holder.email.setText(String.valueOf(items.getEmail()));
        holder.phone.setText(String.valueOf(items.getPhone()));
        holder.rate_per_hour.setText(String.valueOf(items.getRate_per_hour()));
        holder.currency.setText(String.valueOf(items.getCurrency()));

        if(items.getDesignation().equals("1"))
        {
            holder.designation.setText("Design Consultant");
        }
        else if(items.getDesignation().equals("2"))
        {
            holder.designation.setText("Engineer");
        }
        else
        {
            holder.designation.setText("Architect");
        }

        if(items.getRes_type().equals("1"))
        {
            holder.res_type.setText("EMPLOYEE");
            holder.sub_layout.setVisibility(View.GONE);
        }
        else
        {
            holder.res_type.setText("CONTRACT");
            holder.res_subcontractor.setText(items.getSubContractor());
            holder.sub_layout.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return resources_list.size();
    }
}