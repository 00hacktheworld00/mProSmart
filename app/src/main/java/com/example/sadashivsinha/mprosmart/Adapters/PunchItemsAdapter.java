package com.example.sadashivsinha.mprosmart.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.ModelLists.MomList;
import com.example.sadashivsinha.mprosmart.Activities.PunchListItems;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;

import java.util.List;

/**
 * Created by saDashiv sinha on 29-Mar-16.
 */
public class PunchItemsAdapter extends RecyclerView.Adapter<PunchItemsAdapter.MyViewHolder> {

    private List<MomList> momList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView text_line_no, text_description, original_line_no;

        public MyViewHolder(final View view) {
            super(view);
            text_line_no = (TextView) view.findViewById(R.id.text_line_no);
            text_description = (TextView) view.findViewById(R.id.text_description);
            original_line_no = (TextView) view.findViewById(R.id.original_line_no);

            final PreferenceManager pm = new PreferenceManager(view.getContext());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(view.getContext(), PunchListItems.class);
                    String originalLineNo = original_line_no.getText().toString();
                    String lineNo = text_line_no.getText().toString();
                    pm.putString("originalLineNo",originalLineNo);
                    pm.putString("lineNo",lineNo);
                    view.getContext().startActivity(intent);
                }
            });
        }

    }
    public PunchItemsAdapter(List<MomList> momList) {
        this.momList = momList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_punch_items, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MomList items = momList.get(position);
        holder.text_line_no.setText(items.getText_line_no());
        holder.text_description.setText(items.getDescription());
        holder.original_line_no.setText(String.valueOf(items.getOriginal_line_no()));
    }

    @Override
    public int getItemCount() {
        return momList.size();
    }
}
