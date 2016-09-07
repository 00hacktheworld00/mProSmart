package com.example.sadashivsinha.mprosmart.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.ModelLists.VendorList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;

import java.util.List;

/**
 * Created by saDashiv sinha on 22-Jul-16.
 */
public class VendorAdapter extends RecyclerView.Adapter<VendorAdapter.MyViewHolder> {

    private List<VendorList> vendor_list;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView text_first_name, text_last_name, text_phone, text_email, text_building_no, text_street_name, text_locality,
                text_state, text_country, text_zipcode;


        public MyViewHolder(final View view) {
            super(view);
            text_first_name = (TextView) view.findViewById(R.id.text_first_name);
            text_last_name = (TextView) view.findViewById(R.id.text_last_name);
            text_phone = (TextView) view.findViewById(R.id.text_phone);
            text_email = (TextView) view.findViewById(R.id.text_email);
            text_building_no = (TextView) view.findViewById(R.id.text_building_no);
            text_street_name = (TextView) view.findViewById(R.id.text_street_name);
            text_locality = (TextView) view.findViewById(R.id.text_locality);
            text_state = (TextView) view.findViewById(R.id.text_state);
            text_country = (TextView) view.findViewById(R.id.text_country);
            text_zipcode = (TextView) view.findViewById(R.id.text_zipcode);

            final PreferenceManager pm = new PreferenceManager(view.getContext());

        }

    }
    public VendorAdapter(List<VendorList> vendor_list) {
        this.vendor_list = vendor_list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_vendor, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        VendorList items = vendor_list.get(position);
        holder.text_first_name.setText(String.valueOf(items.getText_first_name()));
        holder.text_last_name.setText(String.valueOf(items.getText_last_name()));
        holder.text_phone.setText(String.valueOf(items.getText_phone()));
        holder.text_email.setText(String.valueOf(items.getText_email()));
        holder.text_building_no.setText(String.valueOf(items.getText_building_no()));
        holder.text_street_name.setText(String.valueOf(items.getText_street_name()));
        holder.text_locality.setText(String.valueOf(items.getText_locality()));
        holder.text_state.setText(String.valueOf(items.getText_state()));
        holder.text_country.setText(String.valueOf(items.getText_country()));
        holder.text_zipcode.setText(String.valueOf(items.getText_zipcode()));
    }

    @Override
    public int getItemCount() {
        return vendor_list.size();
    }
}