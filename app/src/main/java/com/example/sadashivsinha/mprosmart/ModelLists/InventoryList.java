package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 01-Jul-16.
 */
public class InventoryList {

    String text_date, text_received, text_issued, text_closing_bal;

    public InventoryList(String text_date, String text_received, String text_issued, String text_closing_bal) {
        this.text_date = text_date;
        this.text_received = text_received;
        this.text_issued = text_issued;
        this.text_closing_bal = text_closing_bal;
    }

    public String getText_date() {

        return text_date;
    }

    public String getText_received() {
        return text_received;
    }

    public String getText_issued() {
        return text_issued;
    }

    public String getText_closing_bal() {
        return text_closing_bal;
    }
}
