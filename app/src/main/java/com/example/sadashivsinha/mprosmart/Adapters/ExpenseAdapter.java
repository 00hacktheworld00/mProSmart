package com.example.sadashivsinha.mprosmart.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.ModelLists.BudgetList;
import com.example.sadashivsinha.mprosmart.R;

import java.util.List;

/**
 * Created by saDashiv sinha on 31-Aug-16.
 */
public class ExpenseAdapter  extends RecyclerView.Adapter<ExpenseAdapter.MyViewHolder> {

    private List<BudgetList> budgetList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView line_no, text_wbs, text_activity, item_name, item_desc, quantity, uom, amount, sl_no, title_wbs, title_activity, line_no_to_show;

        public MyViewHolder(final View view) {
            super(view);
            line_no = (TextView) view.findViewById(R.id.line_no);
            text_wbs = (TextView) view.findViewById(R.id.text_wbs);
            text_activity = (TextView) view.findViewById(R.id.text_activity);
            item_name = (TextView) view.findViewById(R.id.item_name);
            item_desc = (TextView) view.findViewById(R.id.item_desc);
            quantity = (TextView) view.findViewById(R.id.quantity);
            uom = (TextView) view.findViewById(R.id.uom);
            amount = (TextView) view.findViewById(R.id.amount);
            sl_no = (TextView) view.findViewById(R.id.sl_no);
            line_no_to_show = (TextView) view.findViewById(R.id.line_no_to_show);

            title_wbs = (TextView) view.findViewById(R.id.title_wbs);
            title_activity = (TextView) view.findViewById(R.id.title_activity);
        }

    }
    public ExpenseAdapter(List<BudgetList> budgetList) {
        this.budgetList = budgetList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_budget_new, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        BudgetList items = budgetList.get(position);
        holder.sl_no.setText(String.valueOf(items.getSl_no()));
        holder.line_no.setText(String.valueOf(items.getLine_no()));
        holder.item_name.setText(String.valueOf(items.getItem_name()));
        holder.item_desc.setText(String.valueOf(items.getItem_desc()));
        holder.quantity.setText(String.valueOf(items.getQuantity()));
        holder.uom.setText(String.valueOf(items.getUom()));
        holder.amount.setText(String.valueOf(items.getAmount()));
        holder.line_no_to_show.setText(String.valueOf(items.getSl_no()));

        if(!items.getExpenseType().isEmpty())
        {
            if(items.getExpenseType().equals("Personal"))
            {
                holder.text_wbs.setVisibility(View.GONE);
                holder.text_activity.setVisibility(View.GONE);
                holder.title_wbs.setVisibility(View.GONE);
                holder.title_activity.setVisibility(View.GONE);
            }
            else
            {
                holder.text_wbs.setText(String.valueOf(items.getText_wbs()));
                holder.text_activity.setText(String.valueOf(items.getText_activity()));
            }
        }

    }

    @Override
    public int getItemCount() {
        return budgetList.size();
    }
}
