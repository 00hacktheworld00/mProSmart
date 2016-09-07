package com.example.sadashivsinha.mprosmart.Adapters;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.Activities.AttachmentActivity;
import com.example.sadashivsinha.mprosmart.ModelLists.MomList;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.Utils.DatePickerFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by saDashiv sinha on 14-Mar-16.
 */
public class MomAdapter extends RecyclerView.Adapter<MomAdapter.MyViewHolder> {

    private List<MomList> momList;
    TextView textViewDate;
    Context mContext;

public class MyViewHolder extends RecyclerView.ViewHolder {
    public TextView text_line_no, btn_date;
    public Button editBtn;
    public ImageButton attachBtn;
    public EditText text_matter, text_responsible;

    public MyViewHolder(final View view) {
        super(view);
        text_matter = (EditText) view.findViewById(R.id.text_matter);
        text_responsible = (EditText) view.findViewById(R.id.text_responsible);
        text_line_no = (TextView) view.findViewById(R.id.text_line_no);

        btn_date = (TextView) view.findViewById(R.id.btn_date);

        editBtn = (Button) view.findViewById(R.id.editBtn);
        attachBtn = (ImageButton) itemView.findViewById(R.id.attachBtn);

        btn_date.setEnabled(false);

        editBtn.setOnClickListener(new View.OnClickListener() {
            int count=0;
            @Override
            public void onClick(View v) {
                if (text_matter.isEnabled() && text_responsible.isEnabled() && btn_date.isEnabled())
                {
                    text_matter.setEnabled(false);
                    text_responsible.setEnabled(false);
                    btn_date.setEnabled(false);

                    editBtn.setText("EDIT");

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        editBtn.setBackgroundTintList(view.getContext().getResources().getColorStateList(R.color.colorPrimary));
                    }

                    Snackbar snackbar = Snackbar.make(view,"Values Saved.",Snackbar.LENGTH_SHORT);
                    snackbar.show();
                } else {
                    text_matter.setEnabled(true);
                    text_responsible.setEnabled(true);
                    btn_date.setEnabled(true);

                    text_matter.requestFocus();
                    editBtn.setText("SAVE");

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        editBtn.setBackgroundTintList(view.getContext().getResources().getColorStateList(R.color.success_green));
                    }
                }
            }
        });

        attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(itemView.getContext(), AttachmentActivity.class);
                itemView.getContext().startActivity(intent);
            }
        });

    }

}
    public MomAdapter(List<MomList> momList) {
        this.momList = momList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_mom, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        MomList items = momList.get(position);
        holder.text_line_no.setText(String.valueOf(items.getText_line_no()));
        holder.text_matter.setText(String.valueOf(items.getText_matter()));
        holder.text_responsible.setText(items.getText_responsible());

        Date tradeDate = null;
        try {
            tradeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(items.getText_date());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.btn_date.setText(new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(tradeDate));

        holder.btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textViewDate = holder.btn_date;
                mContext = holder.itemView.getContext();
                showDatePicker(mContext);

            }
        });
    }
    private void showDatePicker(Context context) {
        DatePickerFragment date = new DatePickerFragment();
        /**
         * Set Up Current Date Into dialog
         */
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);
        /**
         * Set Call back to capture selected date
         */
        date.setCallBack(ondate);
        date.show(((Activity) context).getFragmentManager(), "Date Picker");

    }

    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            String newDate = String.valueOf(dayOfMonth) + "-" + String.valueOf(monthOfYear+1)
                    + "-" + String.valueOf(year);

            textViewDate.setText(newDate);
        }
    };

    @Override
    public int getItemCount() {
        return momList.size();
    }

}
