package com.example.sadashivsinha.mprosmart.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.Activities.ChangeOrdersNew;
import com.example.sadashivsinha.mprosmart.ModelLists.AllChangeOrdersList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;

import java.util.List;

/**
 * Created by saDashiv sinha on 20-Jul-16.
 */
public class AllChangeOrdersAdapter extends RecyclerView.Adapter<AllChangeOrdersAdapter.MyViewHolder> {

    private List<AllChangeOrdersList> list;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView sl_no, text_orders_no, text_project_id, text_project_name, text_date_created, text_due_date, text_title;


        public MyViewHolder(final View view) {
            super(view);
            sl_no = (TextView) view.findViewById(R.id.sl_no);
            text_orders_no = (TextView) view.findViewById(R.id.text_orders_no);
            text_project_id = (TextView) view.findViewById(R.id.text_project_id);
            text_project_name = (TextView) view.findViewById(R.id.text_project_name);
            text_date_created = (TextView) view.findViewById(R.id.text_date_created);
            text_due_date = (TextView) view.findViewById(R.id.text_due_date);
            text_title = (TextView) view.findViewById(R.id.text_title);


            final PreferenceManager pm = new PreferenceManager(view.getContext());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(view.getContext(), ChangeOrdersNew.class);

                    pm.putString("currentOrderNo", text_orders_no.getText().toString());
                    pm.putString("text_project_id", text_project_id.getText().toString());
                    pm.putString("text_project_name", text_project_name.getText().toString());
                    pm.putString("text_date_created", text_date_created.getText().toString());
                    pm.putString("text_due_date", text_due_date.getText().toString());
                    pm.putString("text_title", text_title.getText().toString());

                    view.getContext().startActivity(intent);
                }
            });
        }

    }
    public AllChangeOrdersAdapter(List<AllChangeOrdersList> list) {
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_all_change_orders, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AllChangeOrdersList items = list.get(position);
        holder.sl_no.setText(String.valueOf(items.getSl_no()));
        holder.text_orders_no.setText(String.valueOf(items.getText_orders_no()));
        holder.text_project_id.setText(String.valueOf(items.getText_project_id()));
        holder.text_project_name.setText(String.valueOf(items.getText_project_name()));
        holder.text_date_created.setText(String.valueOf(items.getText_date_created()));
        holder.text_due_date.setText(String.valueOf(items.getText_due_date()));
        holder.text_title.setText(String.valueOf(items.getText_title()));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
