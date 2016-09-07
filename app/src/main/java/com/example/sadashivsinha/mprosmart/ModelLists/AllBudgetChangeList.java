package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 21-Jul-16.
 */
public class AllBudgetChangeList {
    String sl_no, change_no, project_id, project_name, created_by, created_on, original_budget, current_budget,
            total_budget, description, contractRefNo;

    public AllBudgetChangeList(String sl_no, String change_no, String project_id, String project_name,
                               String created_by, String created_on, String original_budget, String current_budget,
                               String total_budget, String description, String contractRefNo) {
        this.sl_no = sl_no;
        this.change_no = change_no;
        this.project_id = project_id;
        this.project_name = project_name;
        this.created_by = created_by;
        this.created_on = created_on;
        this.original_budget = original_budget;
        this.current_budget = current_budget;
        this.total_budget = total_budget;
        this.description = description;
        this.contractRefNo = contractRefNo;
    }

    public String getContractRefNo() {
        return contractRefNo;
    }

    public String getDescription() {
        return description;
    }

    public String getSl_no() {

        return sl_no;
    }

    public String getChange_no() {
        return change_no;
    }

    public String getProject_id() {
        return project_id;
    }

    public String getProject_name() {
        return project_name;
    }

    public String getCreated_by() {
        return created_by;
    }

    public String getCreated_on() {
        return created_on;
    }

    public String getOriginal_budget() {
        return original_budget;
    }

    public String getCurrent_budget() {
        return current_budget;
    }

    public String getTotal_budget() {
        return total_budget;
    }
}
