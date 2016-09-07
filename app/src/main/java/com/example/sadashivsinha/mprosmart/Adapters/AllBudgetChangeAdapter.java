package com.example.sadashivsinha.mprosmart.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.Activities.BudgetChanges;
import com.example.sadashivsinha.mprosmart.ModelLists.AllBudgetChangeList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;

import java.util.List;

/**
 * Created by saDashiv sinha on 21-Jul-16.
 */
public class AllBudgetChangeAdapter extends RecyclerView.Adapter<AllBudgetChangeAdapter.MyViewHolder> {

    private List<AllBudgetChangeList> budget_list;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView sl_no, change_no, project_id, project_name, created_by, created_on, original_amount, current_budget,
                total_budget, text_desc, contract_ref;


        public MyViewHolder(final View view) {
            super(view);
            sl_no = (TextView) view.findViewById(R.id.sl_no);
            change_no = (TextView) view.findViewById(R.id.change_no);
            created_on = (TextView) view.findViewById(R.id.created_on);
            created_by = (TextView) view.findViewById(R.id.created_by);
            project_id = (TextView) view.findViewById(R.id.project_id);
            project_name = (TextView) view.findViewById(R.id.project_name);
            original_amount = (TextView) view.findViewById(R.id.original_amount);
            current_budget = (TextView) view.findViewById(R.id.current_budget);
            total_budget = (TextView) view.findViewById(R.id.total_budget);
            text_desc = (TextView) view.findViewById(R.id.text_desc);
            contract_ref = (TextView) view.findViewById(R.id.contract_ref);


            final PreferenceManager pm = new PreferenceManager(view.getContext());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;

                    intent = new Intent(view.getContext(), BudgetChanges.class);
                    pm.putString("currentChange", change_no.getText().toString());
                    pm.putString("original_amount", original_amount.getText().toString());
                    pm.putString("current_budget", current_budget.getText().toString());
                    pm.putString("total_budget", total_budget.getText().toString());
                    pm.putString("budgetChangeId", change_no.getText().toString());
                    pm.putString("description", text_desc.getText().toString());
                    pm.putString("contractRef", contract_ref.getText().toString());

                    view.getContext().startActivity(intent);
                }
            });
        }

    }
    public AllBudgetChangeAdapter(List<AllBudgetChangeList> budget_list) {
        this.budget_list = budget_list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_all_budget_changes, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AllBudgetChangeList items = budget_list.get(position);
        holder.sl_no.setText(String.valueOf(items.getSl_no()));
        holder.change_no.setText(String.valueOf(items.getChange_no()));
        holder.project_id.setText(String.valueOf(items.getProject_id()));
        holder.project_name.setText(String.valueOf(items.getProject_name()));
        holder.created_by.setText(String.valueOf(items.getCreated_by()));
        holder.created_on.setText(String.valueOf(items.getCreated_on()));
        holder.original_amount.setText(String.valueOf(items.getOriginal_budget()));
        holder.current_budget.setText(String.valueOf(items.getCurrent_budget()));
        holder.total_budget.setText(String.valueOf(items.getTotal_budget()));
        holder.text_desc.setText(items.getDescription());
        holder.contract_ref.setText(items.getContractRefNo());

    }

    @Override
    public int getItemCount() {
        return budget_list.size();
    }
}