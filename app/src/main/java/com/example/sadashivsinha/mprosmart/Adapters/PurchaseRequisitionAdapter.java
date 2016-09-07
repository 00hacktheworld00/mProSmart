package com.example.sadashivsinha.mprosmart.Adapters;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sadashivsinha.mprosmart.ModelLists.PurchaseRequisitionList;
import com.example.sadashivsinha.mprosmart.R;

import java.util.List;

/**
 * Created by saDashiv sinha on 01-Jul-16.
 */
public class PurchaseRequisitionAdapter extends RecyclerView.Adapter<PurchaseRequisitionAdapter.MyViewHolder> {

    private List<PurchaseRequisitionList> purchaseList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public EditText text_line_no, text_item_id, text_item_desc, text_quantity, text_uom, text_needed_by;

        Button editBtn;

        public MyViewHolder(final View view) {
            super(view);
            text_line_no = (EditText) view.findViewById(R.id.text_line_no);
            text_item_id = (EditText) view.findViewById(R.id.text_item_id);
            text_item_desc = (EditText) view.findViewById(R.id.text_item_desc);
            text_quantity = (EditText) view.findViewById(R.id.text_quantity);
            text_uom = (EditText) view.findViewById(R.id.text_uom);
            text_needed_by = (EditText) view.findViewById(R.id.text_needed_by);

            editBtn = (Button) view.findViewById(R.id.editBtn);

            editBtn.setOnClickListener(new View.OnClickListener() {
                int count=0;

                @Override
                public void onClick(View v) {

                    if(count==0)
                    {
                        text_line_no.setEnabled(true);
                        text_item_id.setEnabled(true);
                        text_item_desc.setEnabled(true);
                        text_quantity.setEnabled(true);
                        text_uom.setEnabled(true);

                        editBtn.setText("SAVE");

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            editBtn.setBackgroundTintList(view.getContext().getResources().getColorStateList(R.color.success_green));
                        }

                        count++;
                    }
                    else
                    {
                        Toast.makeText(view.getContext(), "Values Saved", Toast.LENGTH_SHORT).show();

                        text_line_no.setEnabled(false);
                        text_item_id.setEnabled(false);
                        text_item_desc.setEnabled(false);
                        text_quantity.setEnabled(false);
                        text_uom.setEnabled(false);

                        editBtn.setText("EDIT");

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            editBtn.setBackgroundTintList(view.getContext().getResources().getColorStateList(R.color.colorPrimary));
                        }

                        count--;
                    }
                }
            });

        }
    }

    public PurchaseRequisitionAdapter(List<PurchaseRequisitionList> purchaseList) {
        this.purchaseList = purchaseList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_purchase_requisition, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PurchaseRequisitionList items = purchaseList.get(position);
        holder.text_line_no.setText(items.getText_line_no());
        holder.text_item_id.setText(items.getText_item_id());
        holder.text_item_desc.setText(items.getText_item_desc());
        holder.text_quantity.setText(items.getText_quantity());
        holder.text_uom.setText(items.getText_uom());
        holder.text_needed_by.setText(items.getNeededBy());
    }

    @Override
    public int getItemCount() {
        return purchaseList.size();
    }
}



