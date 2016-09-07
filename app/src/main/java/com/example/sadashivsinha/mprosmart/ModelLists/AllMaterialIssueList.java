package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 01-Jul-16.
 */
public class AllMaterialIssueList {

    String material_sl_no, text_project_id,  text_material_no, text_issue_as_per_bq, text_boq_item, text_quantity,
            text_issued_to, text_issued_on, text_issued_by;

    public AllMaterialIssueList(String material_sl_no, String text_project_id, String text_material_no, String text_issue_as_per_bq,
            String text_boq_item, String text_quantity, String text_issued_to, String text_issued_on, String text_issued_by)
    {
        this.material_sl_no = material_sl_no;
        this.text_project_id = text_project_id;
        this.text_material_no = text_material_no;
        this.text_issue_as_per_bq = text_issue_as_per_bq;
        this.text_boq_item = text_boq_item;
        this.text_quantity = text_quantity;
        this.text_issued_to = text_issued_to;
        this.text_issued_on = text_issued_on;
        this.text_issued_by = text_issued_by;
    }

    public String getMaterial_sl_no() {

        return material_sl_no;
    }

    public String getText_project_id() {
        return text_project_id;
    }

    public String getText_material_no() {
        return text_material_no;
    }

    public String getText_issue_as_per_bq() {
        return text_issue_as_per_bq;
    }

    public String getText_boq_item() {
        return text_boq_item;
    }

    public String getText_quantity() {
        return text_quantity;
    }

    public String getText_issued_to() {
        return text_issued_to;
    }

    public String getText_issued_on() {
        return text_issued_on;
    }

    public String getText_issued_by() {
        return text_issued_by;
    }
}
