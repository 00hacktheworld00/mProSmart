package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 14-Mar-16.
 */
public class SiteTwoList
{
        private String text_line_no, text_wbs, text_activities, text_res_name,
                text_receipt_no, text_po_number, text_vendor_id, text_item_code, text_status,
                text_activity, resourceTimesheetId, text_material_no,
                text_item_id, text_item_desc, text_quan_issued,text_uom;

    private float text_total_hours;


        public SiteTwoList(String text_line_no, String text_receipt_no, String text_po_number,
                       String text_vendor_id, String text_item_code, String text_status)
            {
                this.text_line_no = text_line_no;
                this.text_receipt_no = text_receipt_no;
                this.text_po_number = text_po_number;
                this.text_vendor_id = text_vendor_id;
                this.text_item_code = text_item_code;
                this.text_status = text_status;
            }


    //for 4th fragment
    public SiteTwoList(String text_material_no, String text_item_id, String text_item_desc, String text_quan_issued,
                       String text_uom )
    {
        this.text_material_no = text_material_no;
        this.text_item_id = text_item_id;
        this.text_item_desc = text_item_desc;
        this.text_quan_issued = text_quan_issued;
        this.text_uom = text_uom;
    }


    public SiteTwoList(String resourceTimesheetId, String text_wbs, String text_activity,
                       String text_res_name, Float text_total_hours)
            {
                this.text_wbs = text_wbs;
                this.text_activity = text_activity;
                this.text_res_name = text_res_name;
                this.text_total_hours = text_total_hours;
                this.resourceTimesheetId = resourceTimesheetId;
            }

    public String getText_material_no() {
        return text_material_no;
    }

    public String getText_item_id() {
        return text_item_id;
    }

    public String getText_item_desc() {
        return text_item_desc;
    }

    public String getText_uom() {
        return text_uom;
    }

    public String getResourceTimesheetId() {
        return resourceTimesheetId;
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

        public float getText_total_hours() {
            return text_total_hours;
        }

        public String getText_receipt_no() {
            return text_receipt_no;
        }

        public String getText_po_number() {
            return text_po_number;
        }

        public String getText_vendor_id() {
            return text_vendor_id;
        }

        public String getText_item_code() {
            return text_item_code;
        }

        public String getText_status() {
            return text_status;
        }

        public String getText_activity()
        {
            return text_activity;
        }

        public String getText_quan_issued()
        {
            return text_quan_issued;
        }

}