package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 10-Mar-16.
 */
public class PurchaseReceiptList{

    private String item_id, item_received_quantity, item_total_quantity, item_balance, item_last_received;

    public PurchaseReceiptList(String item_id, String item_received_quantity, String item_total_quantity, String item_balance, String item_last_received)
    {
        this.item_id = item_id;
        this.item_received_quantity = item_received_quantity;
        this.item_total_quantity = item_total_quantity;
        this.item_balance = item_balance;
        this.item_last_received = item_last_received;
    }

    public String getItemId()
    {
        return item_id;
    }

    public void setItemId(String item_id)
    {
        this.item_id = item_id;
    }

    public String getReceivedQuantity()
    {
        return item_received_quantity;
    }

    public void setReceivedQuantity(String item_received_quantity)
    {
        this.item_received_quantity = item_received_quantity;
    }

    public String getTotalQuantity()
    {
        return item_total_quantity;
    }

    public void setTotalQuantity(String item_total_quantity)
    {
        this.item_total_quantity = item_total_quantity;
    }

    public String getBalance()
    {
        return item_balance;
    }

    public void setBalance(String item_balance)
    {
        this.item_balance = item_balance;
    }


    public String getLastReceived()
    {
        return item_last_received;
    }

    public void setLastReceived(String item_last_received)
    {
        this.item_last_received = item_last_received;
    }
}