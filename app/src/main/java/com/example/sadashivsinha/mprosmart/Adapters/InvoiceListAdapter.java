package com.example.sadashivsinha.mprosmart.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.Activities.InvoiceNew;
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
public class InvoiceListAdapter extends RecyclerView.Adapter<InvoiceListAdapter.MyViewHolder> {

    private List<MomList> momList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView invoice_index, invoice_no, ven_invoice_no, vendor_code, pr_number, date, created_by;

        public MyViewHolder(final View view) {
            super(view);
            invoice_index = (TextView) view.findViewById(R.id.invoice_index);
            invoice_no = (TextView) view.findViewById(R.id.invoice_no);
            ven_invoice_no = (TextView) view.findViewById(R.id.ven_invoice_no);
            vendor_code = (TextView) view.findViewById(R.id.vendor_code);
            pr_number = (TextView) view.findViewById(R.id.pr_number);
            date = (TextView) view.findViewById(R.id.date);
            created_by = (TextView) view.findViewById(R.id.created_by);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(view.getContext(), InvoiceNew.class);
                    PreferenceManager pm = new PreferenceManager(view.getContext());
                    pm.putString("currentVendorInvoice", ven_invoice_no.getText().toString());
                    pm.putString("prNo", pr_number.getText().toString());
                    pm.putString("vendor", vendor_code.getText().toString());
                    view.getContext().startActivity(intent);
                }
            });
        }

    }
    public InvoiceListAdapter(List<MomList> momList) {
        this.momList = momList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_invoices, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MomList items = momList.get(position);
        holder.invoice_index.setText(String.valueOf(items.getInvoice_index()));
        holder.invoice_no.setText(String.valueOf(items.getInvoice_no()));
        holder.ven_invoice_no.setText(String.valueOf(items.getVen_invoice_no()));
        holder.vendor_code.setText(String.valueOf(items.getVendor_code()));
        holder.pr_number.setText(String.valueOf(items.getPo_number()));
        holder.created_by.setText(String.valueOf(items.getText_created_by()));
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
