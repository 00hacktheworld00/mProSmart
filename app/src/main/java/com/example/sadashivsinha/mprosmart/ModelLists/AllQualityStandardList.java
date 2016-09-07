package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 30-Jun-16.
 */
public class AllQualityStandardList {

    String quality_sl_no, standard_no, item_id, item_desc, date_created, created_by, project_id;

    public AllQualityStandardList(String quality_sl_no, String standard_no, String item_id, String item_desc, String date_created, String created_by, String project_id) {
        this.quality_sl_no = quality_sl_no;
        this.standard_no = standard_no;
        this.item_id = item_id;
        this.item_desc = item_desc;
        this.date_created = date_created;
        this.created_by = created_by;
        this.project_id = project_id;
    }

    public String getQuality_sl_no() {

        return quality_sl_no;
    }

    public String getStandard_no() {
        return standard_no;
    }

    public String getItem_id() {
        return item_id;
    }

    public String getItem_desc() {
        return item_desc;
    }

    public String getDate_created() {
        return date_created;
    }

    public String getCreated_by() {
        return created_by;
    }

    public String getProject_id() {
        return project_id;
    }
}
