package com.example.sadashivsinha.mprosmart.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.ModelLists.MomList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.Activities.SubcontractorActivity;

import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by saDashiv sinha on 23-Mar-16.
 */
public class SubcontractorFragmentAdapter  extends RecyclerView.Adapter<SubcontractorFragmentAdapter.MyViewHolder> {

    private List<MomList> momList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView text_mom_rec, text_project_id, text_project_name, text_date, text_created_by, title;
        public FancyButton editBtn, attachBtn;
        public LinearLayout hiddenTextboxLayout;


        public MyViewHolder(final View view) {
            super(view);
            text_mom_rec = (TextView) view.findViewById(R.id.text_mom_rec);
            text_project_id = (TextView) view.findViewById(R.id.text_project_id);
            text_project_name = (TextView) view.findViewById(R.id.text_project_name);
            text_date = (TextView) view.findViewById(R.id.text_date);
            text_created_by = (TextView) view.findViewById(R.id.text_created_by);
            title = (TextView) view.findViewById(R.id.title);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), SubcontractorActivity.class);
                    itemView.getContext().startActivity(intent);
                }
            });

        }

    }
    public SubcontractorFragmentAdapter(List<MomList> momList) {
        this.momList = momList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_fragment, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MomList items = momList.get(position);
        holder.text_mom_rec.setText(String.valueOf(items.getText_mom_rec()));
        holder.text_project_id.setText(String.valueOf(items.getText_project_id()));
        holder.text_project_name.setText(items.getText_project_name());
        holder.text_date.setText(items.getText_date());
        holder.text_created_by.setText(String.valueOf(items.getText_created_by()));
        holder.title.setText("Subcontractor # : ");
    }

    @Override
    public int getItemCount() {
        return momList.size();
    }
}
