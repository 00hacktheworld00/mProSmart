package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 01-Jul-16.
 */
public class AllInventoryList {

    String inventory_sl_no, text_inventory_no, text_project_id, text_project_desc, text_item_id, text_item_desc, text_date,
            text_show_transact, text_to_date, text_from_date;

    public AllInventoryList(String inventory_sl_no, String text_inventory_no, String text_project_id, String text_project_desc, String text_item_id,
                            String text_item_desc, String text_date, String text_show_transact, String text_to_date,
                            String text_from_date)
    {
        this.inventory_sl_no = inventory_sl_no;

        this.text_inventory_no = text_inventory_no;
        this.text_project_id = text_project_id;
        this.text_project_desc = text_project_desc;
        this.text_item_id = text_item_id;
        this.text_item_desc = text_item_desc;
        this.text_date = text_date;
        this.text_show_transact = text_show_transact;
        this.text_to_date = text_to_date;
        this.text_from_date = text_from_date;
    }

    public String getInventory_sl_no() {
        return inventory_sl_no;
    }

    public String getText_inventory_no() {

        return text_inventory_no;
    }

    public String getText_project_id() {
        return text_project_id;
    }

    public String getText_project_desc() {
        return text_project_desc;
    }

    public String getText_item_id() {
        return text_item_id;
    }

    public String getText_item_desc() {
        return text_item_desc;
    }

    public String getText_date() {
        return text_date;
    }

    public String getText_show_transact() {
        return text_show_transact;
    }

    public String getText_to_date() {
        return text_to_date;
    }

    public String getText_from_date() {
        return text_from_date;
    }
}
