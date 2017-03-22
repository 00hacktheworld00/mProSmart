package com.example.sadashivsinha.mprosmart.Adapters;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sadashivsinha.mprosmart.Activities.MainCategories;
import com.example.sadashivsinha.mprosmart.Activities.MapsActivity;
import com.example.sadashivsinha.mprosmart.ModelLists.NewAllProjectList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.font.HelveticaBold;
import com.example.sadashivsinha.mprosmart.font.HelveticaRegular;

import java.net.URL;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by saDashiv sinha on 04-Apr-16.
 */
public class NewAllProjectsAdapter extends RecyclerView.Adapter<NewAllProjectsAdapter.MyViewHolder> {

    public List<NewAllProjectList> modelList;
    URL url = null;
    PreferenceManager pm;
    public Drawable mDrawable;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public HelveticaRegular project_id, project_desc, created_by, date, address, text_budget, text_currency, text_approved;
        public CircleImageView company_logo;
        public ImageButton map_btn;
        public HelveticaBold map_text_btn, project_name;

        String  address_one, address_two, city, state, pincode, country, fullAddress;

        public MyViewHolder(final View view) {
            super(view);
            text_approved = (HelveticaRegular) view.findViewById(R.id.text_approved);
            project_name = (HelveticaBold) view.findViewById(R.id.project_name);
            project_id = (HelveticaRegular) view.findViewById(R.id.project_id);
            project_desc = (HelveticaRegular) view.findViewById(R.id.project_desc);
            created_by = (HelveticaRegular) view.findViewById(R.id.created_by);
            date = (HelveticaRegular) view.findViewById(R.id.date);
            address = (HelveticaRegular) view.findViewById(R.id.address);
            text_budget = (HelveticaRegular) view.findViewById(R.id.text_budget);
            text_currency = (HelveticaRegular) view.findViewById(R.id.text_currency);
            company_logo = (CircleImageView) view.findViewById(R.id.company_logo);
            map_btn = (ImageButton) view.findViewById(R.id.map_btn);

            map_text_btn = (HelveticaBold) view.findViewById(R.id.map_text_btn);

            pm = new PreferenceManager(view.getContext());

        }
    }

    public NewAllProjectsAdapter(List<NewAllProjectList> modelList) {
        this.modelList = modelList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_new_all_projects, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final NewAllProjectList items = modelList.get(position);
        holder.project_name.setText(String.valueOf(items.getProject_name()));
        holder.project_id.setText(items.getProject_id());
        holder.project_desc.setText(String.valueOf(items.getProject_desc()));
        holder.created_by.setText(items.getCreated_by());
        holder.date.setText(String.valueOf(items.getDate()));
        holder.text_currency.setText(items.getText_currency());
        holder.text_budget.setText(String.valueOf(items.getText_budget()));


        switch (items.getApproved()) {
            case "0":
                holder.text_approved.setText("APPROVAL PENDING");
                holder.text_approved.setVisibility(View.VISIBLE);
                break;
            case "1":
                holder.text_approved.setVisibility(View.GONE);
                break;
            case "2":
                holder.text_approved.setText("APPROVAL REJECTED");
                holder.text_approved.setVisibility(View.VISIBLE);
                break;
        }

        holder.fullAddress = items.getAddress_one()+ ", " + items.getAddress_two() + ", " + items.getCity()
                + ", " + items.getState() + "-" + items.getPincode() + ", " + items.getPincode();

        holder.address.setText(String.valueOf(holder.fullAddress));

//        Picasso.with(holder.itemView.getContext()).setLoggingEnabled(true);

        if(!items.getCompanyLogo().isEmpty())
        {
            Glide.with(holder.itemView.getContext()).load(items.getCompanyLogo()).crossFade().into(holder.company_logo);

//            Picasso.with(holder.itemView.getContext())
//                    .load(items.getCompanyLogo())
//                    .into(holder.company_logo);
        }

        else
        {
            mDrawable = holder.itemView.getContext().getResources().getDrawable(R.drawable.no_logo);
            holder.company_logo.setImageDrawable(mDrawable);
        }



        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(holder.text_approved.getVisibility()!=View.GONE)
                {
                    if(holder.text_approved.getText().toString().equals("APPROVAL PENDING"))
                    {
                        Toast.makeText(holder.itemView.getContext(), "Project has not been APPROVED yet.", Toast.LENGTH_SHORT).show();
                    }
                    else if(holder.text_approved.getText().toString().equals("APPROVAL REJECTED"))
                    {
                        Toast.makeText(holder.itemView.getContext(), "Project has been REJECTED.", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Intent intent = new Intent(holder.itemView.getContext(), MainCategories.class);
                    String projectNo = holder.project_id.getText().toString();
                    pm.putString("projectId",projectNo);
                    pm.putString("projectName",holder.project_name.getText().toString());
                    pm.putString("projectDesc",holder.project_desc.getText().toString());
                    pm.putString("currency",holder.text_currency.getText().toString());
                    pm.putString("budget",holder.text_budget.getText().toString());
                    pm.putString("createdBy",holder.created_by.getText().toString());
                    pm.putString("createdDate",holder.date.getText().toString());
                    pm.putString("imageUrl",items.getCompanyLogo());
                    holder.itemView.getContext().startActivity(intent);
                }
            }
        });


        holder.map_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), MapsActivity.class);
                intent.putExtra("fullAddress", holder.address.getText().toString());
                intent.putExtra("projectId", holder.project_id.getText().toString());
                intent.putExtra("projectName", holder.project_name.getText().toString());
                holder.itemView.getContext().startActivity(intent);
            }
        });

        holder.map_text_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), MapsActivity.class);
                intent.putExtra("fullAddress", holder.address.getText().toString());
                intent.putExtra("projectId", holder.project_id.getText().toString());
                intent.putExtra("projectName", holder.project_name.getText().toString());
                holder.itemView.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }
}