package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 10-Mar-16.
 */
public class PurchaseOrdersList{

    private String project_number, date, receipt_no,created_by, created_on, total_amount;
    private String  po_index, po_number, vendor_code, no_of_items, percent_received, last_updated, receipt_index;

    public PurchaseOrdersList(String po_index, String po_number, String vendor_code, String created_on,
                              String created_by, String total_amount)
    {
        this.po_index = po_index;
        this.po_number = po_number;
        this.vendor_code = vendor_code;
        this.created_on = created_on;
        this.created_by = created_by;
        this.total_amount = total_amount;
    }


    //for new purchase receipts

    public PurchaseOrdersList(String receipt_no,String po_number, String project_number, String date,
                              String receipt_index, int val)
    {
        this.po_number = po_number;
        this.project_number = project_number;
        this.date = date;
        this.receipt_no = receipt_no;
        this.receipt_index = receipt_index;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public String getPo_number()
        {
            return po_number;
        }

        public void setPo_number(String po_number)
        {
            this.po_number = po_number;
        }

        public String getProject_number()
        {
            return project_number;
        }

        public void setProject_number(String project_number)
        {
            this.project_number = project_number;
        }

        public String getVendor_code()
        {
            return vendor_code;
        }

        public void setVendor_code(String vendor_code)
        {
            this.vendor_code = vendor_code;
        }

    public String getDate()
    {
        return date;
    }

    public String getReceipt_no()
    {
        return receipt_no;
    }



    public String getPo_index()
    {
        return po_index;
    }


    public String getNo_of_items()
    {
        return no_of_items;
    }


    public String getPercent_received()
    {
        return percent_received;
    }


    public String getLast_updated()
    {
        return last_updated;
    }

    public String getReceipt_index()
    {
        return receipt_index;
    }


    public String getCreated_by()
    {
        return created_by;
    }

    public String getCreated_on()
    {
        return created_on;
    }
}