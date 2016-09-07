package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 01-Jul-16.
 */
public class AllBoqList {

    String boq_sl_no, text_boq_no, text_project_id, text_project_name, text_unit, itemName,  text_uom, text_created_by,
            text_date_created;

    public AllBoqList(String boq_sl_no, String text_boq_no, String text_project_id, String text_project_name,
                 String text_unit, String text_uom, String itemName, String text_created_by, String text_date_created) {
        this.boq_sl_no = boq_sl_no;
        this.text_boq_no = text_boq_no;
        this.text_project_id = text_project_id;
        this.text_project_name = text_project_name;
        this.text_unit = text_unit;
        this.text_uom = text_uom;
        this.text_created_by = text_created_by;
        this.text_date_created = text_date_created;
        this.itemName = itemName;
    }

    public String getItemName() {
        return itemName;
    }

    public String getBoq_sl_no() {
        return boq_sl_no;
    }

    public String getText_project_id() {

        return text_project_id;
    }

    public String getText_boq_no() {
        return text_boq_no;
    }

    public String getText_project_name() {
        return text_project_name;
    }

    public String getText_unit() {
        return text_unit;
    }

    public String getText_uom() {
        return text_uom;
    }

    public String getText_created_by() {
        return text_created_by;
    }

    public String getText_date_created() {
        return text_date_created;
    }
}
