package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 30-Jun-16.
 */
public class AllQualityChecklistList {

    String quality_sl_no, checklist_no, project_id, project_name, created_on, created_by;

    public AllQualityChecklistList(String quality_sl_no, String checklist_no, String project_id, String project_name, String created_on, String created_by) {
        this.quality_sl_no = quality_sl_no;
        this.checklist_no = checklist_no;
        this.project_id = project_id;
        this.project_name = project_name;
        this.created_on = created_on;
        this.created_by = created_by;
    }

    public String getQuality_sl_no() {

        return quality_sl_no;
    }

    public String getChecklist_no() {
        return checklist_no;
    }

    public String getProject_id() {
        return project_id;
    }

    public String getProject_name() {
        return project_name;
    }

    public String getCreated_on() {
        return created_on;
    }

    public String getCreated_by() {
        return created_by;
    }
}
