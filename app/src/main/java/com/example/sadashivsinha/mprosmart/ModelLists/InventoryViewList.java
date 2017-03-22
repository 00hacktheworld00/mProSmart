package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 27-Jul-16.
 */
public class InventoryViewList {
    String text_date, title, item_added_issued;

    public InventoryViewList(String text_date, String title, String item_added_issued) {
        this.text_date = text_date;
        this.title = title;
        this.item_added_issued = item_added_issued;
    }

    public String getItem_added_issued() {
        return item_added_issued;
    }

    public String getText_date() {
        return text_date;
    }

    public String getTitle() {
        return title;
    }
}
