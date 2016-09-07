package com.example.sadashivsinha.mprosmart.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.Activities.AddVendorsActivity;
import com.example.sadashivsinha.mprosmart.Activities.VendorActivity;
import com.example.sadashivsinha.mprosmart.ModelLists.AllVendorList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;

import java.util.List;

/**
 * Created by saDashiv sinha on 22-Jul-16.
 */
public class AllVendorAdapter extends RecyclerView.Adapter<AllVendorAdapter.MyViewHolder> {

    private List<AllVendorList> vendor_list;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView sl_no, vendor_id, vendor_name, vendor_type, discipline;
        private ImageButton editBtn;


        public MyViewHolder(final View view) {
            super(view);
            sl_no = (TextView) view.findViewById(R.id.sl_no);
            vendor_id = (TextView) view.findViewById(R.id.vendor_id);
            vendor_name = (TextView) view.findViewById(R.id.vendor_name);
            vendor_type = (TextView) view.findViewById(R.id.vendor_type);
            discipline = (TextView) view.findViewById(R.id.discipline);


            final PreferenceManager pm = new PreferenceManager(view.getContext());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(view.getContext(), VendorActivity.class);

                    pm.putString("vendor_id", vendor_id.getText().toString());
                    pm.putString("vendor_name", vendor_name.getText().toString());
                    pm.putString("vendor_type", vendor_type.getText().toString());
                    pm.putString("discipline", discipline.getText().toString());

                    view.getContext().startActivity(intent);
                }
            });

            editBtn = (ImageButton) view.findViewById(R.id.editBtn);

            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(view.getContext(), AddVendorsActivity.class);
                    intent.putExtra("edit", "yes");
                    intent.putExtra("vendorId", vendor_id.getText().toString());
                    view.getContext().startActivity(intent);
                }
            });
        }

    }
    public AllVendorAdapter(List<AllVendorList> vendor_list) {
        this.vendor_list = vendor_list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_all_vendor, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AllVendorList items = vendor_list.get(position);
        holder.sl_no.setText(String.valueOf(items.getSl_no()));
        holder.vendor_id.setText(String.valueOf(items.getVendor_id()));
        holder.vendor_name.setText(String.valueOf(items.getVendor_name()));
        holder.vendor_type.setText(String.valueOf(items.getVendor_type()));
        holder.discipline.setText(String.valueOf(items.getDiscipline()));
    }

    @Override
    public int getItemCount() {
        return vendor_list.size();
    }
}