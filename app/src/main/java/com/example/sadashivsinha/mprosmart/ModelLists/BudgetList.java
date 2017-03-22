package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 27-May-16.
 */
public class BudgetList {

    int noOfAttachments;
    String sl_no, line_no, text_wbs, text_activity, item_name, item_desc, quantity, uom, amount, expenseType;

    public BudgetList(String sl_no,String line_no, String text_wbs, String text_activity, String item_name,
                      String item_desc, String quantity, String uom, String amount)
    {
        this.line_no = line_no;
        this.text_wbs = text_wbs;
        this.text_activity = text_activity;
        this.item_name = item_name;
        this.item_desc = item_desc;
        this.quantity = quantity;
        this.uom = uom;
        this.amount = amount;
        this.sl_no = sl_no;
    }

    public BudgetList(String expenseType, String sl_no,String line_no, String text_wbs, String text_activity, String item_name,
                      String item_desc, String quantity, String uom, String amount, int noOfAttachments)
    {
        this.line_no = line_no;
        this.text_wbs = text_wbs;
        this.text_activity = text_activity;
        this.item_name = item_name;
        this.item_desc = item_desc;
        this.quantity = quantity;
        this.uom = uom;
        this.amount = amount;
        this.sl_no = sl_no;
        this.noOfAttachments = noOfAttachments;
        this.expenseType = expenseType;
    }

    public int getNoOfAttachments() {
        return noOfAttachments;
    }

    public String getExpenseType() {
        return expenseType;
    }

    public String getSl_no() {
        return sl_no;
    }

    public String getLine_no() {
        return line_no;
    }

    public String getText_wbs() {
        return text_wbs;
    }

    public String getText_activity() {
        return text_activity;
    }

    public String getItem_name() {
        return item_name;
    }

    public String getItem_desc() {
        return item_desc;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getUom() {
        return uom;
    }

    public String getAmount() {
        return amount;
    }
}
