package com.example.sadashivsinha.mprosmart.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sadashivsinha.mprosmart.ModelLists.ItemList;
import com.example.sadashivsinha.mprosmart.Activities.PurchaseReceiptsNew;
import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;

import java.util.List;

/**
 * Created by saDashiv sinha on 06-Feb-16.
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> {
    private List<ItemList> itemList;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            public TextView receipt_no, item_last_received,
                    item_date, item_quantity, vendor_code, percentage_received;


            public MyViewHolder(final View view) {
                super(view);
                final PreferenceManager pm = new PreferenceManager(view.getContext());
                receipt_no = (TextView) view.findViewById(R.id.receipt_no);
                item_last_received = (TextView) view.findViewById(R.id.item_last_received);



                //for new one
                item_date = (TextView) view.findViewById(R.id.item_date);
                item_quantity = (TextView) view.findViewById(R.id.item_quantity);
                vendor_code = (TextView) view.findViewById(R.id.vendor_code);
                percentage_received = (TextView) view.findViewById(R.id.percentage_received);
                //----------------------------------------------------------------------------------

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(view.getContext(), PurchaseReceiptsNew.class);
                        pm.putString("purchaseOrderNo", receipt_no.getText().toString());
                        view.getContext().startActivity(intent);
                    }
                });
            }
        }


        public MainAdapter(List<ItemList> itemList) {
            this.itemList = itemList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_main_new, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            ItemList items = itemList.get(position);
            holder.receipt_no.setText(String.valueOf(items.getReceiptNo()));

//            holder.item_last_received.setText(String.valueOf(items.getLastReceived()));
            holder.item_date.setText(String.valueOf(items.getItem_date()));
            holder.item_quantity.setText(String.valueOf(items.getItem_quantity()));
            holder.vendor_code.setText(String.valueOf(items.getVendor_code()));
            holder.item_last_received.setText(String.valueOf(items.getLastReceived()));
            holder.percentage_received.setText(String.valueOf(items.getPercentage_received()));
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }
    }