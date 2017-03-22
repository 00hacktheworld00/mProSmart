package com.example.sadashivsinha.mprosmart.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.sadashivsinha.mprosmart.Activities.AddVendorsActivity;
import com.example.sadashivsinha.mprosmart.Activities.VendorActivity;
import com.example.sadashivsinha.mprosmart.ModelLists.AllVendorList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.font.HelveticaBold;
import com.example.sadashivsinha.mprosmart.font.HelveticaRegular;

import java.util.List;

/**
 * Created by saDashiv sinha on 22-Jul-16.
 */
public class AllVendorAdapter extends RecyclerView.Adapter<AllVendorAdapter.MyViewHolder> {

    private List<AllVendorList> vendor_list;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private HelveticaRegular sl_no, vendor_type, discipline, text_tax_id, text_licence_no, text_company_name;
        HelveticaBold  vendor_id, vendor_name;
        private ImageButton editBtn;


        public MyViewHolder(final View view) {
            super(view);
            sl_no = (HelveticaRegular) view.findViewById(R.id.sl_no);
            vendor_id = (HelveticaBold) view.findViewById(R.id.vendor_id);
            vendor_name = (HelveticaBold) view.findViewById(R.id.vendor_name);
            vendor_type = (HelveticaRegular) view.findViewById(R.id.vendor_type);
            discipline = (HelveticaRegular) view.findViewById(R.id.discipline);
            text_tax_id = (HelveticaRegular) view.findViewById(R.id.text_tax_id);
            text_licence_no = (HelveticaRegular) view.findViewById(R.id.text_licence_no);
            text_company_name = (HelveticaRegular) view.findViewById(R.id.text_company_name);


            final PreferenceManager pm = new PreferenceManager(view.getContext());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(view.getContext(), VendorActivity.class);

                    pm.putString("vendorId", vendor_id.getText().toString());
                    pm.putString("vendorName", vendor_name.getText().toString());
                    pm.putString("vendorType", vendor_type.getText().toString());
                    pm.putString("vendorDiscipline", discipline.getText().toString());
                    pm.putString("vendorTaxId", text_tax_id.getText().toString());
                    pm.putString("vendorLicenceNo", text_licence_no.getText().toString());
                    pm.putString("vendorCompanyName", text_company_name.getText().toString());

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

        holder.text_tax_id.setText(String.valueOf(items.getText_tax_id()));
        holder.text_licence_no.setText(String.valueOf(items.getText_licence_no()));
        holder.text_company_name.setText(String.valueOf(items.getText_company_name()));
    }

    @Override
    public int getItemCount() {
        return vendor_list.size();
    }
}