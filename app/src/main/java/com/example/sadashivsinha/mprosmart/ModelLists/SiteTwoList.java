package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 14-Mar-16.
 */
public class SiteTwoList
{
        private String text_line_no, text_wbs, text_activities, text_res_name, text_total_hours,
                text_receipt_no, text_po_number, text_vendor_id, text_item_code, text_status,
                text_activity, text_quan_issued, text_rec_by;


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
    public SiteTwoList(int siteFour, String text_line_no, String text_activity, String text_item_code,
                       String text_quan_issued, String text_rec_by, String text_wbs)
    {
        this.text_line_no = text_line_no;
        this.text_activity = text_activity;
        this.text_item_code = text_item_code;
        this.text_quan_issued = text_quan_issued;
        this.text_rec_by = text_rec_by;
        this.text_wbs = text_wbs;
    }


    public SiteTwoList(String text_line_no, String text_wbs, String text_activities,
                       String text_res_name, String text_total_hours)
            {
                this.text_line_no = text_line_no;
                this.text_wbs = text_wbs;
                this.text_activities = text_activities;
                this.text_res_name = text_res_name;
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

        public String getText_total_hours() {
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

        public String getText_rec_by()
        {
            return text_rec_by;
        }

}