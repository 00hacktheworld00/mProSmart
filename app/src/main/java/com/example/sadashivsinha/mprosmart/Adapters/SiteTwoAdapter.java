package com.example.sadashivsinha.mprosmart.Adapters;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.ModelLists.SiteTwoList;
import com.example.sadashivsinha.mprosmart.R;

import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by saDashiv sinha on 14-Mar-16.
 */
public class SiteTwoAdapter extends RecyclerView.Adapter<SiteTwoAdapter.MyViewHolder> {
    private List<SiteTwoList> itemList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView text_line_no, text_wbs, text_activities, text_res_name, text_total_hours, saveBtn;
        public FancyButton editBtn;
        public LinearLayout hiddenTextboxLayout;

        public MyViewHolder(final View view) {
            super(view);
            text_line_no = (TextView) view.findViewById(R.id.text_line_no);
            text_wbs = (TextView) view.findViewById(R.id.text_wbs);
            text_activities = (TextView) view.findViewById(R.id.text_activities);
            text_res_name = (TextView) view.findViewById(R.id.text_res_name);
            text_total_hours = (TextView) view.findViewById(R.id.text_total_hours);

            hiddenTextboxLayout = (LinearLayout) view.findViewById(R.id.hiddenTextboxLayout);

            editBtn = (FancyButton) view.findViewById(R.id.editBtn);
            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(hiddenTextboxLayout.getVisibility()==View.GONE)
                    {
                        hiddenTextboxLayout.setVisibility(View.VISIBLE);
                        hiddenTextboxLayout.startAnimation(AnimationUtils.loadAnimation( view.getContext(), R.anim.view_show));
                    }
                    else
                    {
                        hiddenTextboxLayout.startAnimation(AnimationUtils.loadAnimation( view.getContext(), R.anim.view_hide));
                        hiddenTextboxLayout.setVisibility(View.GONE);
                    }
                }
            });

            saveBtn = (TextView) itemView.findViewById(R.id.saveBtn);
            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar snackbar = Snackbar.make(itemView, "Saved", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            });

        }
    }


    public SiteTwoAdapter(List<SiteTwoList> itemList) {
        this.itemList = itemList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_site_two, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        SiteTwoList items = itemList.get(position);
        holder.text_line_no.setText(String.valueOf(items.getText_line_no()));
        holder.text_wbs.setText(String.valueOf(items.getText_wbs()));
        holder.text_activities.setText(String.valueOf(items.getText_activities()));
        holder.text_res_name.setText(String.valueOf(items.getText_res_name()));
        holder.text_total_hours.setText(String.valueOf(items.getText_total_hours()));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}