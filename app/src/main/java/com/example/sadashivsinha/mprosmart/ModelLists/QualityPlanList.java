package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 30-Jun-16.
 */
public class QualityPlanList {

    String sl_no, id, process_desc, activity, procedure, accept_criteria, supplier, subcontractor, third_party, customer_client;

    public QualityPlanList(String sl_no, String id, String process_desc, String activity, String procedure, String accept_criteria, String supplier, String subcontractor, String third_party, String customer_client) {
        this.sl_no = sl_no;
        this.process_desc = process_desc;
        this.activity = activity;
        this.procedure = procedure;
        this.accept_criteria = accept_criteria;
        this.supplier = supplier;
        this.subcontractor = subcontractor;
        this.third_party = third_party;
        this.customer_client = customer_client;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getSl_no() {

        return sl_no;
    }

    public String getProcess_desc() {
        return process_desc;
    }

    public String getActivity() {
        return activity;
    }

    public String getProcedure() {
        return procedure;
    }

    public String getAccept_criteria() {
        return accept_criteria;
    }

    public String getSupplier() {
        return supplier;
    }

    public String getSubcontractor() {
        return subcontractor;
    }

    public String getThird_party() {
        return third_party;
    }

    public String getCustomer_client() {
        return customer_client;
    }
}
