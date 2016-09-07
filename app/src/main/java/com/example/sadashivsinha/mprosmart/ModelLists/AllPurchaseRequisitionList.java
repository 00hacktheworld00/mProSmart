package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 01-Jul-16.
 */
public class AllPurchaseRequisitionList {

    String pr_sl_no, text_pr_no,  text_department, text_created_on, text_created_by, isPo;
    String approved;

    public AllPurchaseRequisitionList(String pr_sl_no, String text_pr_no, String text_department, String text_created_on, String text_created_by,
                                      String approved, String isPo) {
        this.pr_sl_no = pr_sl_no;
        this.text_pr_no = text_pr_no;
        this.text_department = text_department;
        this.text_created_on = text_created_on;
        this.text_created_by = text_created_by;
        this.approved = approved;
        this.isPo = isPo;
    }

    public String getPr_sl_no() {

        return pr_sl_no;
    }

    public String getIsPo() {
        return isPo;
    }

    public String getApproved() {
        return approved;
    }

    public String getText_pr_no() {
        return text_pr_no;
    }

    public String getText_department() {
        return text_department;
    }

    public String getText_created_on() {
        return text_created_on;
    }

    public String getText_created_by() {
        return text_created_by;
    }
}
