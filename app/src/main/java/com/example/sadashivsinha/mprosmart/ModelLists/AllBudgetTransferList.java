package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 29-Aug-16.
 */
public class AllBudgetTransferList {
    String transfer_no, text_wbs_from, text_wbs_to, budget_amount, transfer_by, text_date, sl_no;

    public AllBudgetTransferList(String sl_no, String text_date, String transfer_by,
                                 String budget_amount, String text_wbs_to, String text_wbs_from, String transfer_no) {
        this.sl_no = sl_no;
        this.text_date = text_date;
        this.transfer_by = transfer_by;
        this.budget_amount = budget_amount;
        this.text_wbs_to = text_wbs_to;
        this.text_wbs_from = text_wbs_from;
        this.transfer_no = transfer_no;
    }

    public String getTransfer_no() {
        return transfer_no;
    }

    public String getText_wbs_from() {
        return text_wbs_from;
    }

    public String getText_wbs_to() {
        return text_wbs_to;
    }

    public String getBudget_amount() {
        return budget_amount;
    }

    public String getTransfer_by() {
        return transfer_by;
    }

    public String getText_date() {
        return text_date;
    }

    public String getSl_no() {
        return sl_no;
    }
}
