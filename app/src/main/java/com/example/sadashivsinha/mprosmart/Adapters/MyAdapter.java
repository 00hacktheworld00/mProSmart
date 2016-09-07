package com.example.sadashivsinha.mprosmart.Adapters;

/**
 * Created by saDashiv sinha on 18-Feb-16.
 */
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.sadashivsinha.mprosmart.Activities.ChangePasswordActivity;
import com.example.sadashivsinha.mprosmart.Activities.CustomerSupport;
import com.example.sadashivsinha.mprosmart.Activities.NewAllProjects;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.font.RobotoTextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {


    private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
    // IF the view under inflation and population is header or Item
    private static final int TYPE_ITEM = 1;

    private String mNavTitles[]; // String Array to store the passed titles Value from MainActivity.java
    private int mIcons[];       // Int Array to store the passed icons resource value from MainActivity.java

    private String name;        //String Resource for header View Name
    private int profile;        //int Resource for header view profile picture
    private String email;       //String Resource for header view email


    // Creating a ViewHolder which extends the RecyclerView View Holder
    // ViewHolder are used to to store the inflated views in order to recycle them

    public static class ViewHolder extends RecyclerView.ViewHolder {
        int Holderid;

        RobotoTextView textView;
//        LinearLayout hiddenLayoutSite, hiddenLayoutReport, hiddenLayoutSetting;
        ImageButton button;
        CircleImageView profile;
        RobotoTextView Name;
//        RobotoTextView list_text1, list_text2, list_text3, list_text03;
        RobotoTextView email;
        LinearLayout all_projects, change_password, dashboard, settings, my_profile, cus_support;

        RecyclerView recyclerview;

        public ViewHolder(final View itemView, int ViewType) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);

            // Here we set the appropriate view in accordance with the the view type as passed when the holder object is created

            if(ViewType == TYPE_ITEM) {
                textView = (RobotoTextView) itemView.findViewById(R.id.rowText);

                all_projects = (LinearLayout) itemView.findViewById(R.id.all_projects);
                change_password = (LinearLayout) itemView.findViewById(R.id.change_password);
                dashboard = (LinearLayout) itemView.findViewById(R.id.dashboard);
                settings = (LinearLayout) itemView.findViewById(R.id.settings);
                my_profile = (LinearLayout) itemView.findViewById(R.id.my_profile);
                cus_support = (LinearLayout) itemView.findViewById(R.id.cus_support);


//                hiddenLayoutSite = (LinearLayout) itemView.findViewById(R.id.hiddenLayoutSite);
//                hiddenLayoutReport = (LinearLayout) itemView.findViewById(R.id.hiddenLayoutReport);
//                hiddenLayoutSetting = (LinearLayout) itemView.findViewById(R.id.hiddenLayoutSetting);
//
//                button = (ImageButton) itemView.findViewById(R.id.button);
//                list_text1 = (RobotoTextView) itemView.findViewById(R.id.list_text1);
//                list_text2 = (RobotoTextView) itemView.findViewById(R.id.list_text2);
//                list_text3 = (RobotoTextView) itemView.findViewById(R.id.list_text3);
//                list_text03 = (RobotoTextView) itemView.findViewById(R.id.list_text03);

                // Creating TextView object with the id of textView from item_row.xml
                Holderid = 1;                                               // setting holder id as 1 as the object being populated are of type item row
            }
            else{

                Name = (RobotoTextView) itemView.findViewById(R.id.name);         // Creating Text View object from header.xml for name
                email = (RobotoTextView) itemView.findViewById(R.id.email);       // Creating Text View object from header.xml for email
                profile = (CircleImageView) itemView.findViewById(R.id.circleView);// Creating Image view object from header.xml for profile pic

                Holderid = 0;// Setting holder id = 0 as the object being populated are of type header view
            }
        }


    }

    public MyAdapter(String Titles[], int Icons[], String Name, String Email, int Profile){ // MyAdapter Constructor with titles and icons parameter
        // titles, icons, name, email, profile pic are passed from the main activity as we
        mNavTitles = Titles;                //have seen earlier
        mIcons = Icons;
        name = Name;
        email = Email;
        profile = Profile;                     //here we assign those passed values to the values we declared here
        //in adapter



    }



    //Below first we ovverride the method onCreateViewHolder which is called when the ViewHolder is
    //Created, In this method we inflate the item_row.xml layout if the viewType is Type_ITEM or else we inflate header.xml
    // if the viewType is TYPE_HEADER
    // and pass it to the view holder

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row,parent,false); //Inflating the layout

            ViewHolder vhItem = new ViewHolder(v,viewType); //Creating ViewHolder and passing the object of type view

            return vhItem; // Returning the created object

            //inflate your layout and pass it to view holder

        } else if (viewType == TYPE_HEADER) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.header,parent,false); //Inflating the layout

            ViewHolder vhHeader = new ViewHolder(v,viewType); //Creating ViewHolder and passing the object of type view

            return vhHeader; //returning the object created


        }
        return null;

    }

    //Next we override a method which is called when the item in a row is needed to be displayed, here the int position
    // Tells us item at which position is being constructed to be displayed and the holder id of the holder object tell us
    // which view type is being created 1 for item row
    @Override
    public void onBindViewHolder(final MyAdapter.ViewHolder holder, int position) {
        if (holder.Holderid == 1) {                              // as the list view is going to be called after the header view so we decrement the
            // position by 1 and pass it to the holder while setting the text and image
//            holder.textView.setText(mNavTitles[position - 1]);


            holder.all_projects.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(holder.itemView.getContext(), NewAllProjects.class);
                    holder.itemView.getContext().startActivity(intent);
                }
            });


            holder.change_password.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(holder.itemView.getContext(), ChangePasswordActivity.class);
                    holder.itemView.getContext().startActivity(intent);
                }
            });

//            holder.dashboard.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(holder.itemView.getContext(), DashboardActivity.class);
//                    holder.itemView.getContext().startActivity(intent);
//                }
//            });

            holder.cus_support.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(holder.itemView.getContext(), CustomerSupport.class);
                    holder.itemView.getContext().startActivity(intent);
                }
            });

//            holder.list_text1.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
//                    holder.itemView.getContext().startActivity(intent);
//                }
//            });
//
//
//            holder.list_text2.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(holder.itemView.getContext(), QualityControl.class);
//                    holder.itemView.getContext().startActivity(intent);
//                }
//            });
//
//
//            holder.list_text3.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(holder.itemView.getContext(), WorklistActivity.class);
//                    holder.itemView.getContext().startActivity(intent);
//                }
//            });
//
//
//            holder.list_text03.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(holder.itemView.getContext(), InspectionReportActivity.class);
//                    holder.itemView.getContext().startActivity(intent);
//                }
//            });
//
//            holder.button.setOnClickListener(new View.OnClickListener() {
//                int count=0;
//                @Override
//                public void onClick(View v) {
//                    if (count == 0)
//                    {
//                        if (holder.textView.getText().toString().equals("SITE MANAGEMENT"))
//                        {
//                            holder.hiddenLayoutSite.setVisibility(View.VISIBLE);
//                        }
//                        else if (holder.textView.getText().toString().equals("REPORTS"))
//                        {
//                            holder.hiddenLayoutReport.setVisibility(View.VISIBLE);
//                        }
//                        else if (holder.textView.getText().toString().equals("SETTINGS"))
//                        {
//                            holder.hiddenLayoutSetting.setVisibility(View.VISIBLE);
//                        }
//
//                        holder.button.setBackgroundResource(R.drawable.minus_button);
//                        count++;
//                    }
//                    else {
//                        if (holder.textView.getText().toString().equals("SITE MANAGEMENT"))
//                        {
//                            holder.hiddenLayoutSite.setVisibility(View.GONE);
//                        }
//                        else if (holder.textView.getText().toString().equals("REPORTS"))
//                        {
//                            holder.hiddenLayoutReport.setVisibility(View.GONE);
//                        }
//                        else if (holder.textView.getText().toString().equals("SETTINGS"))
//                        {
//                            holder.hiddenLayoutSetting.setVisibility(View.GONE);
//                        }
//
//                        holder.button.setBackgroundResource(R.drawable.add_button);
//                        count--;
//                        }
//                }
//            });

        }
        else{

            holder.profile.setImageResource(profile);           // Similarly we set the resources for header view
            holder.Name.setText(name);
            holder.email.setText(email);
        }
    }

    // This method returns the number of items present in the list
    @Override
    public int getItemCount() {
        return 1+1; // the number of items in the list will be +1 the titles including the header view.
    }


    // Witht the following method we check what type of view is being passed
    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

}