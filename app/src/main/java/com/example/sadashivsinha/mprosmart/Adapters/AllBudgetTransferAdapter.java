package com.example.sadashivsinha.mprosmart.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.Activities.BudgetTransfer;
import com.example.sadashivsinha.mprosmart.ModelLists.AllBudgetTransferList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;

import java.util.List;

/**
 * Created by saDashiv sinha on 29-Aug-16.
 */
public class AllBudgetTransferAdapter extends RecyclerView.Adapter<AllBudgetTransferAdapter.MyViewHolder> {

    private List<AllBudgetTransferList> budget_list;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView transfer_no, text_wbs_from, text_wbs_to, budget_amount, transfer_by, text_date, sl_no;

        public MyViewHolder(final View view) {
            super(view);
            sl_no = (TextView) view.findViewById(R.id.sl_no);
            transfer_no = (TextView) view.findViewById(R.id.transfer_no);
            text_wbs_from = (TextView) view.findViewById(R.id.text_wbs_from);
            text_wbs_to = (TextView) view.findViewById(R.id.text_wbs_to);
            budget_amount = (TextView) view.findViewById(R.id.budget_amount);
            transfer_by = (TextView) view.findViewById(R.id.transfer_by);
            text_date = (TextView) view.findViewById(R.id.text_date);


            final PreferenceManager pm = new PreferenceManager(view.getContext());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(view.getContext(), BudgetTransfer.class);
                    pm.putString("currentTransfer", transfer_no.getText().toString());

                    view.getContext().startActivity(intent);
                }
            });
        }

    }
    public AllBudgetTransferAdapter(List<AllBudgetTransferList> budget_list) {
        this.budget_list = budget_list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_all_budget_transfer, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AllBudgetTransferList items = budget_list.get(position);
        holder.sl_no.setText(String.valueOf(items.getSl_no()));
        holder.transfer_no.setText(String.valueOf(items.getTransfer_no()));
        holder.text_wbs_from.setText(String.valueOf(items.getText_wbs_from()));
        holder.text_wbs_to.setText(String.valueOf(items.getText_wbs_to()));
        holder.budget_amount.setText(String.valueOf(items.getBudget_amount()));
        holder.transfer_by.setText(String.valueOf(items.getTransfer_by()));
        holder.text_date.setText(String.valueOf(items.getText_date()));
    }

    @Override
    public int getItemCount() {
        return budget_list.size();
    }
}