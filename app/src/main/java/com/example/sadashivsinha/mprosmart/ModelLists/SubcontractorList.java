package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 16-Mar-16.
 */
public class SubcontractorList {
    private String text_line_no, text_wbs, text_activities, text_res_name, text_date, text_total_hours;

    public SubcontractorList(String text_line_no, String text_wbs, String text_activities, String text_res_name,
                         String text_date, String text_total_hours)
    {
        this.text_line_no = text_line_no;
        this.text_wbs = text_wbs;
        this.text_activities = text_activities;
        this.text_res_name = text_res_name;
        this.text_date = text_date;
        this.text_total_hours = text_total_hours;
    }

    public String getText_line_no() {
        return text_line_no;
    }

    public String getText_wbs() {
        return text_wbs;
    }

    public String getText_activities() {
        return text_activities;
    }

    public String getText_res_name() {
        return text_res_name;
    }

    public String getText_date() {
        return text_date;
    }

    public String getText_total_hours() {
        return text_total_hours;
    }
}