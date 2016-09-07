package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 27-Jul-16.
 */
public class InventoryViewList {
    String pr_no, inventory_total, text_date, title, item_added_issued;

    public InventoryViewList(String pr_no, String inventory_total, String text_date, String title, String item_added_issued) {
        this.pr_no = pr_no;
        this.inventory_total = inventory_total;
        this.text_date = text_date;
        this.title = title;
        this.item_added_issued = item_added_issued;
    }

    public String getItem_added_issued() {
        return item_added_issued;
    }

    public String getPr_no() {
        return pr_no;
    }

    public String getInventory_total() {
        return inventory_total;
    }

    public String getText_date() {
        return text_date;
    }

    public String getTitle() {
        return title;
    }
}
