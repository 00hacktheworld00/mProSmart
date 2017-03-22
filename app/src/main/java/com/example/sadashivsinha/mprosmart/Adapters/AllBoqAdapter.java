package com.example.sadashivsinha.mprosmart.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.Activities.BoqActivity;
import com.example.sadashivsinha.mprosmart.ModelLists.AllBoqList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;

import java.util.List;

/**
 * Created by saDashiv sinha on 01-Jul-16.
 */
public class AllBoqAdapter extends RecyclerView.Adapter<AllBoqAdapter.MyViewHolder> {

private List<AllBoqList> boq_list;

public class MyViewHolder extends RecyclerView.ViewHolder {
    private TextView boq_sl_no, text_boq_no, text_project_id, text_project_name, text_unit, text_uom,
            text_created_by, text_date_created, text_boq_name;


    public MyViewHolder(final View view) {
        super(view);
        boq_sl_no = (TextView) view.findViewById(R.id.boq_sl_no);
        text_boq_no = (TextView) view.findViewById(R.id.text_boq_no);
        text_boq_name = (TextView) view.findViewById(R.id.text_boq_name);
        text_project_id = (TextView) view.findViewById(R.id.text_project_id);
        text_project_name = (TextView) view.findViewById(R.id.text_project_name);
        text_unit = (TextView) view.findViewById(R.id.text_unit);
        text_uom = (TextView) view.findViewById(R.id.text_uom);
        text_created_by = (TextView) view.findViewById(R.id.text_created_by);
        text_date_created = (TextView) view.findViewById(R.id.text_date_created);


        final PreferenceManager pm = new PreferenceManager(view.getContext());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), BoqActivity.class);

                pm.putString("currentBoq", text_boq_no.getText().toString());
                pm.putString("currentBoqName", text_boq_name.getText().toString());
                pm.putString("currentBoqProjectId", text_project_id.getText().toString());
                pm.putString("currentBoqProjectName", text_project_name.getText().toString());
                pm.putString("currentBoqUnit", text_unit.getText().toString());
                pm.putString("currentBoqUom", text_uom.getText().toString());
                pm.putString("currentBoqCreatedBy", text_created_by.getText().toString());
                pm.putString("currentBoqDate", text_date_created.getText().toString());

                view.getContext().startActivity(intent);
            }
        });
    }

}
    public AllBoqAdapter(List<AllBoqList> boq_list) {
        this.boq_list = boq_list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_all_boq, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AllBoqList items = boq_list.get(position);
        holder.boq_sl_no.setText(String.valueOf(items.getBoq_sl_no()));
        holder.text_boq_no.setText(String.valueOf(items.getText_boq_no()));
        holder.text_project_id.setText(String.valueOf(items.getText_project_id()));
        holder.text_project_name.setText(String.valueOf(items.getText_project_name()));
        holder.text_unit.setText(String.valueOf(items.getText_unit()));
        holder.text_uom.setText(String.valueOf(items.getText_uom()));
        holder.text_created_by.setText(String.valueOf(items.getText_created_by()));
        holder.text_date_created.setText(String.valueOf(items.getText_date_created()));
        holder.text_boq_name.setText(items.getItemName());
    }

    @Override
    public int getItemCount() {
        return boq_list.size();
    }
}