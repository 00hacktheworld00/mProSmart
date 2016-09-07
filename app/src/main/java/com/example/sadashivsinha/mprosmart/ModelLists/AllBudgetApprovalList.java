package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 21-Jul-16.
 */
public class AllBudgetApprovalList {
    String sl_no, appoval_no, created_by, created_on, wbs, startDate, endDate, contractRef, budget;

    public AllBudgetApprovalList(String sl_no, String appoval_no, String created_by, String created_on,
                                 String wbs, String startDate, String endDate, String contractRef, String budget) {
        this.appoval_no = appoval_no;
        this.created_by = created_by;
        this.created_on = created_on;
        this.sl_no = sl_no;
        this.wbs = wbs;
        this.startDate = startDate;
        this.endDate = endDate;
        this.contractRef = contractRef;
        this.budget = budget;
    }

    public String getWbs() {
        return wbs;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getContractRef() {
        return contractRef;
    }

    public String getBudget() {
        return budget;
    }

    public String getSl_no() {
        return sl_no;
    }

    public String getAppoval_no() {

        return appoval_no;
    }

    public String getCreated_by() {
        return created_by;
    }

    public String getCreated_on() {
        return created_on;
    }
}
