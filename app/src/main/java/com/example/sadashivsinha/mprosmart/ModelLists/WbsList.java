package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 02-Jul-16.
 */
public class WbsList {

    String text_wbs_name;
    String text_progress;
    String wbs_id;
    String currency_code;
    String total_budget;

    public WbsList(String wbs_id, String text_wbs_name, String text_progress, String currency_code, String total_budget) {
        this.text_wbs_name = text_wbs_name;
        this.text_progress = text_progress;
        this.wbs_id = wbs_id;
        this.currency_code = currency_code;
        this.total_budget = total_budget;
    }

    public String getCurrency_code() {
        return currency_code;
    }

    public String getTotal_budget() {
        return total_budget;
    }

    public String getWbs_id() {
        return wbs_id;
    }

    public String getText_wbs_name() {

        return text_wbs_name;
    }

    public String getText_progress() {
        return text_progress;
    }
}
