package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 01-Jul-16.
 */
public class BoqList {

    String text_item, text_quantity, text_uom, text_cost, text_currency, text_total_cost;;

    public BoqList(String text_item, String text_quantity, String text_uom, String text_cost, String text_currency, String text_total_cost) {
        this.text_item = text_item;
        this.text_quantity = text_quantity;
        this.text_uom = text_uom;
        this.text_cost = text_cost;
        this.text_currency = text_currency;
        this.text_total_cost = text_total_cost;
    }

    public String getText_item() {

        return text_item;
    }

    public String getText_quantity() {
        return text_quantity;
    }

    public String getText_uom() {
        return text_uom;
    }

    public String getText_cost() {
        return text_cost;
    }

    public String getText_currency() {
        return text_currency;
    }

    public String getText_total_cost() {
        return text_total_cost;
    }
}
