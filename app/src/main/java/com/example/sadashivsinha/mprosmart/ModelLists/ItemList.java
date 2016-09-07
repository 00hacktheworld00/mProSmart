package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 06-Feb-16.
 */
public class ItemList{
    private String item_id, item_quantity_received, item_total_quantity, item_balance, item_last_received, receiptNo,
            item_date, item_quantity, vendor_code, percentage_received;

    public ItemList() {
    }

    public ItemList(String receiptNo, String item_last_received)
    {
        this.receiptNo = receiptNo;
        this.item_last_received = item_last_received;
    }



    //for new purchase orders
    public ItemList(int no_val, String receiptNo, String item_date, String item_quantity,
                    String vendor_code, String percentage_received, String item_last_received)
    {
        this.receiptNo = receiptNo;
        this.item_date = item_date;
        this.item_quantity = item_quantity;
        this.vendor_code = vendor_code;
        this.percentage_received = percentage_received;
        this.item_last_received = item_last_received;
    }

    //----------------------------------------------------------------------




        public ItemList(String item_id, String item_quantity_received, String item_total_quantity, String item_balance, String item_last_received) {
        this.item_id = item_id;
        this.item_quantity_received = item_quantity_received;
        this.item_total_quantity = item_total_quantity;
        this.item_balance = item_balance;
        this.item_last_received = item_last_received;
    }

    public String getItemId() {
        return item_id;
    }

    public String getQuantityReceived() {
        return item_quantity_received;
    }

    public String getTotalQuantity() {
        return item_total_quantity;
    }

    public String getItemBalance() {
        return item_balance;
    }

    public String getLastReceived() {
        return item_last_received;
    }

    public String getReceiptNo()
    {
        return receiptNo;
    }

    public String getItem_date()
    {
        return item_date;
    }

    public String getItem_quantity()
    {
        return item_quantity;
    }

    public String getVendor_code()
    {
        return vendor_code;
    }

    public String getPercentage_received()
    {
        return percentage_received;
    }

}