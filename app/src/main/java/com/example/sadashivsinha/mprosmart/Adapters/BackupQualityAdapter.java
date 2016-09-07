package com.example.sadashivsinha.mprosmart.Adapters;

import android.content.Intent;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.Activities.AttachmentActivity;
import com.example.sadashivsinha.mprosmart.ModelLists.QualityList;
import com.example.sadashivsinha.mprosmart.R;

import java.util.List;

/**
 * Created by saDashiv sinha on 01-Mar-16.
 */
public class BackupQualityAdapter extends RecyclerView.Adapter<BackupQualityAdapter.MyViewHolder> {

    private List<QualityList> qualityList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView item_id, attachments;
        public ImageButton attachBtn;
        public EditText received_quantity, quantity_accept,quantity_reject, item_desc;
        public Button editBtn;

        public MyViewHolder(final View view) {
            super(view);
            item_id = (TextView) view.findViewById(R.id.item_id);
            item_desc = (EditText) view.findViewById(R.id.item_desc);;
            received_quantity = (EditText) view.findViewById(R.id.received_quantity);
            quantity_accept = (EditText) view.findViewById(R.id.quantity_accept);
            quantity_reject = (EditText) view.findViewById(R.id.quantity_reject);
            attachments = (TextView) view.findViewById(R.id.no_of_attachments);


            attachBtn = (ImageButton) itemView.findViewById(R.id.attachBtn);
            editBtn = (Button) itemView.findViewById(R.id.editBtn);

            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(received_quantity.isEnabled() && received_quantity.isEnabled() && received_quantity.isEnabled() &&
                            received_quantity.isEnabled())
                    {
                        received_quantity.setEnabled(false);
                        quantity_accept.setEnabled(false);
                        quantity_reject.setEnabled(false);
                        item_desc.setEnabled(false);

                        editBtn.setText("EDIT");

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            editBtn.setBackgroundTintList(view.getContext().getResources().getColorStateList(R.color.colorPrimary));
                        }

                        Snackbar snackbar = Snackbar.make(view,"Values Saved.",Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }

                    else
                    {
                        received_quantity.setEnabled(true);
                        quantity_accept.setEnabled(true);
                        quantity_reject.setEnabled(true);
                        item_desc.setEnabled(true);

                        item_desc.requestFocus();
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
    public BackupQualityAdapter(List<QualityList> qualityList) {
        this.qualityList = qualityList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_quality, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        QualityList items = qualityList.get(position);
        holder.item_id.setText(String.valueOf(items.getItemId()));
        holder.item_desc.setText(items.getItemDesc());
        holder.received_quantity.setText(items.getReceivedQuantity());
        holder.quantity_accept.setText(String.valueOf(items.getQuantityAccept()));
        holder.quantity_reject.setText(items.getQuantityReject());
        holder.attachments.setText(items.getAttachments());
    }

    @Override
    public int getItemCount() {
        return qualityList.size();
    }
}
