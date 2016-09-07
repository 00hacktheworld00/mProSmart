package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 13-Jul-16.
 */
public class AllExpensesList {
    String sl_no, expense_no, date_created, created_by, expense_type, total_expense, expense_desc;

    public AllExpensesList(String sl_no, String expense_no,
                           String date_created, String created_by, String expense_type, String total_expense, String expense_desc) {
        this.sl_no = sl_no;
        this.expense_no = expense_no;
        this.date_created = date_created;
        this.created_by = created_by;
        this.expense_type = expense_type;
        this.total_expense = total_expense;
        this.expense_desc = expense_desc;
    }

    public String getSl_no() {

        return sl_no;
    }

    public String getExpense_no() {
        return expense_no;
    }

    public String getDate_created() {
        return date_created;
    }

    public String getCreated_by() {
        return created_by;
    }

    public String getExpense_type() {
        return expense_type;
    }

    public String getTotal_expense() {
        return total_expense;
    }

    public String getExpense_desc() {
        return expense_desc;
    }
}
