package com.example.sadashivsinha.mprosmart.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.Activities.ExpenseManagement;
import com.example.sadashivsinha.mprosmart.ModelLists.AllExpensesList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;

import java.util.List;

/**
 * Created by saDashiv sinha on 13-Jul-16.
 */
public class AllExpensesAdapter extends RecyclerView.Adapter<AllExpensesAdapter.MyViewHolder> {

    private List<AllExpensesList> expense_list;
    String currentCurrency;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView sl_no, expense_no, date_created, created_by, expense_type, total_expense, expense_desc, text_currency;
        PreferenceManager pm;


        public MyViewHolder(final View view) {
            super(view);
            sl_no = (TextView) view.findViewById(R.id.sl_no);
            expense_no = (TextView) view.findViewById(R.id.expense_no);
            date_created = (TextView) view.findViewById(R.id.date_created);
            created_by = (TextView) view.findViewById(R.id.created_by);
            expense_type = (TextView) view.findViewById(R.id.expense_type);
            total_expense = (TextView) view.findViewById(R.id.total_expense);
            expense_desc = (TextView) view.findViewById(R.id.expense_desc);
            text_currency = (TextView) view.findViewById(R.id.text_currency);


            pm = new PreferenceManager(view.getContext());

            currentCurrency = pm.getString("currency");

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(view.getContext(), ExpenseManagement.class);

                    pm.putString("currentExpense", expense_no.getText().toString());
                    pm.putString("expenseType", expense_type.getText().toString());
                    pm.putString("expenseDate", date_created.getText().toString());
                    pm.putString("expenseCreatedBy", created_by.getText().toString());
                    pm.putString("expenseTotalExpense", total_expense.getText().toString());
                    pm.putString("expenseDesc", expense_desc.getText().toString());

                    view.getContext().startActivity(intent);
                }
            });
        }

    }
    public AllExpensesAdapter(List<AllExpensesList> expense_list) {
        this.expense_list = expense_list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_all_expenses, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AllExpensesList items = expense_list.get(position);
        holder.sl_no.setText(String.valueOf(items.getSl_no()));
        holder.expense_no.setText(String.valueOf(items.getExpense_no()));
        holder.date_created.setText(String.valueOf(items.getDate_created()));
        holder.created_by.setText(String.valueOf(items.getCreated_by()));
        holder.expense_type.setText(String.valueOf(items.getExpense_type()));
        holder.total_expense.setText(String.valueOf(items.getTotal_expense()));
        holder.expense_desc.setText(String.valueOf(items.getExpense_desc()));
        holder.text_currency.setText(currentCurrency);
    }

    @Override
    public int getItemCount() {
        return expense_list.size();
    }
}