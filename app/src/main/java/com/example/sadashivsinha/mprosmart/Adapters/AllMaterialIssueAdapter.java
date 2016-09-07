package com.example.sadashivsinha.mprosmart.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.Activities.MaterialIssueActivity;
import com.example.sadashivsinha.mprosmart.ModelLists.AllMaterialIssueList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;

import java.util.List;

/**
 * Created by saDashiv sinha on 01-Jul-16.
 */
public class AllMaterialIssueAdapter extends RecyclerView.Adapter<AllMaterialIssueAdapter.MyViewHolder> {

    private List<AllMaterialIssueList> materialList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView material_sl_no, text_project_id,  text_material_no, text_issue_as_per_bq, text_boq_item, text_quantity,
                text_issued_to, text_issued_on, text_issued_by;
        private LinearLayout hiddenLayout;
        PreferenceManager pm;

        public MyViewHolder(final View view) {
            super(view);
            material_sl_no = (TextView) view.findViewById(R.id.material_sl_no);
            text_material_no = (TextView) view.findViewById(R.id.text_material_no);
            text_project_id = (TextView) view.findViewById(R.id.text_project_id);
            text_issue_as_per_bq = (TextView) view.findViewById(R.id.text_issue_as_per_bq);
            text_boq_item = (TextView) view.findViewById(R.id.text_boq_item);
            text_quantity = (TextView) view.findViewById(R.id.text_quantity);
            text_issued_to = (TextView) view.findViewById(R.id.text_issued_to);
            text_issued_on = (TextView) view.findViewById(R.id.text_issued_on);
            text_issued_by = (TextView) view.findViewById(R.id.text_issued_by);

            hiddenLayout = (LinearLayout) view.findViewById(R.id.hiddenLayout);

            pm = new PreferenceManager(view.getContext());

        }

    }
    public AllMaterialIssueAdapter(List<AllMaterialIssueList> materialList) {
        this.materialList = materialList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_all_material_issue, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        AllMaterialIssueList items = materialList.get(position);
        holder.material_sl_no.setText(String.valueOf(items.getMaterial_sl_no()));
        holder.text_project_id.setText(String.valueOf(items.getText_project_id()));
        holder.text_material_no.setText(String.valueOf(items.getText_material_no()));
        holder.text_issue_as_per_bq.setText(String.valueOf(items.getText_issue_as_per_bq()));
        holder.text_boq_item.setText(String.valueOf(items.getText_boq_item()));
        holder.text_quantity.setText(String.valueOf(items.getText_quantity()));
        holder.text_issued_to.setText(String.valueOf(items.getText_issued_to()));
        holder.text_issued_on.setText(String.valueOf(items.getText_issued_on()));
        holder.text_issued_by.setText(String.valueOf(items.getText_issued_by()));

//        if(holder.text_issue_as_per_bq.getText().toString().equals("No"))
//        {
//            holder.hiddenLayout.setVisibility(View.GONE);
//        }
//        else
//        {
//            holder.hiddenLayout.setVisibility(View.VISIBLE);
//        }

        holder.hiddenLayout.setVisibility(View.GONE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), MaterialIssueActivity.class);

                String issueAsPerBoq = holder.text_issue_as_per_bq.getText().toString();
                if(issueAsPerBoq.equals("Yes"))
                {
                    holder.pm.putString("issueAsPerBoq","Yes");
                    holder.pm.putString("itemIdBoq", holder.text_boq_item.getText().toString());
                    holder.pm.putString("itemQuantityBoq", holder.text_quantity.getText().toString());
                }
                else
                {
                    holder.pm.putString("issueAsPerBoq","No");
                }


                holder.pm.putString("currentMaterialIssueId",holder.text_material_no.getText().toString());

                //passing the values for header on next page

                holder.itemView.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return materialList.size();
    }
}