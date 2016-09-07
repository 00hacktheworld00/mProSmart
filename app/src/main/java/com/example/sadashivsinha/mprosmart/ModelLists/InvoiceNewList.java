package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 10-Aug-16.
 */
public class InvoiceNewList {

    String text_item_id, text_quantity, text_quan_accepted, text_unit_cost;
    float total_cost;

    public InvoiceNewList(String text_item_id, String text_quantity,
                          String text_quan_accepted, String text_unit_cost, float total_cost) {
        this.text_item_id = text_item_id;
        this.text_quantity = text_quantity;
        this.text_quan_accepted = text_quan_accepted;
        this.text_unit_cost = text_unit_cost;
        this.total_cost = total_cost;
    }

    public String getText_item_id() {
        return text_item_id;
    }

    public String getText_quantity() {
        return text_quantity;
    }

    public String getText_quan_accepted() {
        return text_quan_accepted;
    }

    public String getText_unit_cost() {
        return text_unit_cost;
    }

    public float getTotal_cost() {
        return total_cost;
    }

}
