package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 04-Aug-16.
 */
public class AllSiteDiaryList {
    String sl_no, text_site_date, text_site_id, project_id, created_by;

    public AllSiteDiaryList(String sl_no, String text_site_date, String text_site_id, String project_id, String created_by) {
        this.sl_no = sl_no;
        this.text_site_date = text_site_date;
        this.text_site_id = text_site_id;
        this.project_id = project_id;
        this.created_by = created_by;
    }

    public String getSl_no() {
        return sl_no;
    }

    public String getText_site_date() {
        return text_site_date;
    }

    public String getText_site_id() {
        return text_site_id;
    }

    public String getProject_id() {
        return project_id;
    }

    public String getCreated_by() {
        return created_by;
    }
}
