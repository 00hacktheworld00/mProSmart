package com.example.sadashivsinha.mprosmart.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.ModelLists.SiteList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.font.HelveticaMedium;
import com.example.sadashivsinha.mprosmart.font.HelveticaRegular;

import java.util.List;

/**
 * Created by saDashiv sinha on 13-Jul-16.
 */
public class SiteAdapter extends RecyclerView.Adapter<SiteAdapter.MyViewHolder> {

    private List<SiteList> siteList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView sl_no;
        private HelveticaMedium text_notes;
        private HelveticaRegular text_created_by, text_created_on;

        public MyViewHolder(final View view) {
            super(view);
            text_notes = (HelveticaMedium) view.findViewById(R.id.text_notes);
            text_created_by = (HelveticaRegular) view.findViewById(R.id.text_created_by);
            text_created_on = (HelveticaRegular) view.findViewById(R.id.text_created_on);
            sl_no = (TextView) view.findViewById(R.id.sl_no);
        }

    }
    public SiteAdapter(List<SiteList> siteList) {
        this.siteList = siteList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_fragment_site, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        SiteList items = siteList.get(position);
        holder.text_notes.setText(String.valueOf(items.getNotes()));
        holder.text_created_by.setText(String.valueOf(items.getCreatedBy()));
        holder.text_created_on.setText(String.valueOf(items.getCreatedOn()));
        holder.sl_no.setText(String.valueOf(items.getSlNo()));
    }

    @Override
    public int getItemCount() {
        return siteList.size();
    }
}
