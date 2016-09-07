package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 20-Jul-16.
 */
public class AllChangeOrdersList {
    String sl_no, text_orders_no, text_project_id, text_project_name, text_date_created, text_due_date, text_title;

    public AllChangeOrdersList(String sl_no, String text_orders_no, String text_title,
                               String text_project_id, String text_project_name, String text_date_created, String text_due_date) {
        this.sl_no = sl_no;
        this.text_orders_no = text_orders_no;
        this.text_project_id = text_project_id;
        this.text_project_name = text_project_name;
        this.text_date_created = text_date_created;
        this.text_due_date = text_due_date;
        this.text_title = text_title;
    }

    public String getText_title() {
        return text_title;
    }

    public String getSl_no() {

        return sl_no;
    }

    public String getText_orders_no() {
        return text_orders_no;
    }

    public String getText_project_id() {
        return text_project_id;
    }

    public String getText_project_name() {
        return text_project_name;
    }

    public String getText_date_created() {
        return text_date_created;
    }

    public String getText_due_date() {
        return text_due_date;
    }
}
