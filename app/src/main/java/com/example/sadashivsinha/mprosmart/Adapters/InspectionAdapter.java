package com.example.sadashivsinha.mprosmart.Adapters;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.ModelLists.InspectionList;
import com.example.sadashivsinha.mprosmart.R;

import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by saDashiv sinha on 02-Mar-16.
 */
public class InspectionAdapter extends RecyclerView.Adapter<InspectionAdapter.MyViewHolder> {

    private List<InspectionList> inspectionList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView item_id, line_no, item_desc, item_uom;
        public FancyButton editBtn;
        public LinearLayout hiddenTextboxLayout;
        public EditText desc_edit, action_taken;
        public TextView saveBtn;
        public ImageView plus_minus_btn;

        public MyViewHolder(final View view) {
            super(view);
            item_id = (TextView) view.findViewById(R.id.item_id);
            line_no = (TextView) view.findViewById(R.id.line_no);;
            item_desc = (TextView) view.findViewById(R.id.item_desc);
            item_uom = (TextView) view.findViewById(R.id.item_uom);
            editBtn = (FancyButton) view.findViewById(R.id.editBtn);

            desc_edit = (EditText) view.findViewById(R.id.desc_of_noncomformance);
            action_taken = (EditText) view.findViewById(R.id.action_taken);
            saveBtn = (TextView) view.findViewById(R.id.saveBtn);

            hiddenTextboxLayout = (LinearLayout) itemView.findViewById(R.id.hiddenTextboxLayout);

//            ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_dropdown_item_1line,
//                    new String[] {
//                            "KGs", "GMs", "MGs"});
//            uom_new.setAdapter(adapter);

            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar snackbar = Snackbar.make(view, "Saved successfully", Snackbar.LENGTH_LONG);
                                snackbar.show();
                    hiddenTextboxLayout.setVisibility(View.GONE);
                    plus_minus_btn.setBackgroundResource(R.drawable.plus_new_btn);
                }
            });

//
//            uom_new.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//
//                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                    if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))|| (actionId == EditorInfo.IME_ACTION_DONE))
//                    {
//                        if(uom_new.getText().toString().equals(""))
//                        {
//
//                        }
//                        else
//                        {
//                            Snackbar snackbar = Snackbar
//                                        .make(view, "UOM updated.", Snackbar.LENGTH_LONG);
//
//                                snackbar.show();
//                            item_uom.setText(uom_new.getText().toString());
//                            }
//                    }
//                    return false;
//                }
//            });


            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(hiddenTextboxLayout.getVisibility()==View.GONE)
                    {
                        hiddenTextboxLayout.setVisibility(View.VISIBLE);
                        hiddenTextboxLayout.startAnimation(AnimationUtils.loadAnimation( itemView.getContext(), R.anim.view_show));
                    }
                    else
                    {
                        hiddenTextboxLayout.startAnimation(AnimationUtils.loadAnimation( itemView.getContext(), R.anim.view_hide));
                        hiddenTextboxLayout.setVisibility(View.GONE);
                    }
                }
            });
        }
    }
    public InspectionAdapter(List<InspectionList> inspectionList) {
        this.inspectionList = inspectionList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_inspection, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        InspectionList items = inspectionList.get(position);

        holder.item_id.setText(String.valueOf(items.getItemId()));
        holder.line_no.setText(items.getLine_no());
        holder.item_desc.setText(items.getItem_desc());
        holder.item_uom.setText(String.valueOf(items.getUom()));
    }

    @Override
    public int getItemCount() {
        return inspectionList.size();
    }
}
