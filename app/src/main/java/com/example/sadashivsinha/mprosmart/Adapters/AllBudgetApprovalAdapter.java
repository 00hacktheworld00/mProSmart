package com.example.sadashivsinha.mprosmart.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.Activities.BudgetApproval;
import com.example.sadashivsinha.mprosmart.Activities.BudgetTransfer;
import com.example.sadashivsinha.mprosmart.ModelLists.AllBudgetApprovalList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;

import java.util.List;

/**
 * Created by saDashiv sinha on 21-Jul-16.
 */
public class AllBudgetApprovalAdapter extends RecyclerView.Adapter<AllBudgetApprovalAdapter.MyViewHolder> {

    private List<AllBudgetApprovalList> budget_list;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView sl_no, appoval_no, created_by, created_on, title, text_wbs, text_start_date, text_end_date, text_budget, text_contract_ref;


        public MyViewHolder(final View view) {
            super(view);
            sl_no = (TextView) view.findViewById(R.id.sl_no);
            appoval_no = (TextView) view.findViewById(R.id.appoval_no);
            created_on = (TextView) view.findViewById(R.id.created_on);
            created_by = (TextView) view.findViewById(R.id.created_by);
            text_wbs = (TextView) view.findViewById(R.id.text_wbs);
            text_start_date = (TextView) view.findViewById(R.id.text_start_date);
            text_end_date = (TextView) view.findViewById(R.id.text_end_date);
            text_budget = (TextView) view.findViewById(R.id.text_budget);
            text_contract_ref = (TextView) view.findViewById(R.id.text_contract_ref);

            title = (TextView) view.findViewById(R.id.title);

            title.setText("Budget Approval# ");

            final PreferenceManager pm = new PreferenceManager(view.getContext());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;

                    if(pm.getString("currentBudget").equals("approval"))
                    {
                        intent = new Intent(view.getContext(), BudgetApproval.class);
                    }
                    else
                    {
                        intent = new Intent(view.getContext(), BudgetTransfer.class);
                    }

                    pm.putString("currentApproval", appoval_no.getText().toString());
                    pm.putString("created_on", created_on.getText().toString());
                    pm.putString("created_by", created_by.getText().toString());

                    view.getContext().startActivity(intent);
                }
            });
        }

    }
    public AllBudgetApprovalAdapter(List<AllBudgetApprovalList> budget_list) {
        this.budget_list = budget_list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_all_budget_approval, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AllBudgetApprovalList items = budget_list.get(position);
        holder.sl_no.setText(String.valueOf(items.getSl_no()));
        holder.appoval_no.setText(String.valueOf(items.getAppoval_no()));
        holder.created_by.setText(String.valueOf(items.getCreated_by()));
        holder.created_on.setText(String.valueOf(items.getCreated_on()));
        holder.text_wbs.setText(String.valueOf(items.getWbs()));
        holder.text_start_date.setText(String.valueOf(items.getStartDate()));
        holder.text_end_date.setText(String.valueOf(items.getEndDate()));
        holder.text_contract_ref.setText(String.valueOf(items.getContractRef()));
        holder.text_budget.setText(String.valueOf(items.getBudget()));
    }

    @Override
    public int getItemCount() {
        return budget_list.size();
    }
}