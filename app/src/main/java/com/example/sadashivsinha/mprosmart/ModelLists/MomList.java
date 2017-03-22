package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 14-Mar-16.
 */
public class MomList {
    private String text_matter, text_responsible, text_attachments, text_date, text_line_no;
    private String text_mom_rec, text_project_id, text_project_name, text_created_by;
    private String punch_index, punch_list_no, vendor_name;
    private String quality_index, qir_no, vendor_id, receipt_no, project_id, created_by, purchase_order;
    private String mom_index, mom_rec_no, project_name, date;
    private String slNo, submittalId, currentProjectNo, projectName, createdDate, createdBy, submittalRegisterId, submittalsType, dueDate,
            status, description;
    private String submittal_no, sl_no, title, start_date, end_date;
    private int original_line_no;
    int approved;


    //for invoice list
    private String invoice_index, invoice_no, ven_invoice_no, vendor_code, po_number, priority;

    //for punch list
    public MomList(String punch_index, String punch_list_no, String project_id, String project_name,
                   String date, String vendor_id, String created_by, String vendor_name) {

        this.punch_index = punch_index;
        this.punch_list_no = punch_list_no;
        this.project_id = project_id;
        this.project_name = project_name;
        this.date = date;
        this.created_by = created_by;
        this.vendor_id = vendor_id;
        this.vendor_name = vendor_name;
    }

    //for submittals
    public MomList(String slNo, String submittalId, String currentProjectNo, String projectName, String createdDate, String createdBy, String submittalRegisterId, String submittalsType,
                   String dueDate, String status, String description)
    {
        this.slNo = slNo;
        this.submittalId = submittalId;
        this.currentProjectNo = currentProjectNo;
        this.projectName = projectName;
        this.createdDate = createdDate;
        this.createdBy = createdBy;
        this.submittalRegisterId = submittalRegisterId;
        this.submittalsType = submittalsType;
        this.dueDate = dueDate;
        this.status = status;
        this.description = description;
    }

    //for submittal register
    public MomList(String submittal_no, String sl_no, String project_id, String project_name, String date, String created_by, String status, String priority, String start_date, String end_date,
                   int approved)
    {
        this.submittal_no = submittal_no;
        this.sl_no = sl_no;
        this.project_id = project_id;
        this.project_name = project_name;
        this.date = date;
        this.created_by = created_by;
        this.priority = priority;
        this.status = status;
        this.start_date = start_date;
        this.end_date = end_date;
        this.approved = approved;
    }


    //for invoice list
    public MomList(String invoice_index,String invoice_no,String ven_invoice_no,String vendor_code,
                   String po_number,String date, String text_created_by) {

        this.invoice_index = invoice_index;
        this.invoice_no = invoice_no;
        this.ven_invoice_no = ven_invoice_no;
        this.vendor_code = vendor_code;
        this.po_number = po_number;
        this.date = date;
        this.text_created_by = text_created_by;
    }

    //for header content
    public MomList(int val, String text_mom_rec, String text_project_id, String text_project_name, String text_date, String text_created_by) {
        this.text_mom_rec = text_mom_rec;
        this.text_project_id = text_project_id;
        this.text_project_name = text_project_name;
        this.text_created_by = text_created_by;
        this.text_date = text_date;
    }

    public MomList(String text_line_no, String description) {
        this.text_line_no = text_line_no;
        this.description = description;
    }

    public MomList(String text_line_no, int original_line_no, String description) {
        this.text_line_no = text_line_no;
        this.description = description;
        this.original_line_no = original_line_no;
    }

    //for all Quality Control
    public MomList(String quality_index, String qir_no, String vendor_id, String receipt_no, String project_id, String created_by
            , String purchase_order, int val) {

        this.quality_index = quality_index;
        this.qir_no = qir_no;
        this.vendor_id = vendor_id;
        this.receipt_no = receipt_no;
        this.project_id = project_id;
        this.created_by = created_by;
        this.purchase_order = purchase_order;
    }


    //for All MOM
    public MomList(String mom_index, String mom_rec_no, String project_id, String project_name, String date, String created_by) {
        this.mom_index = mom_index;
        this.mom_rec_no = mom_rec_no;
        this.project_id = project_id;
        this.project_name = project_name;
        this.date = date;
        this.created_by = created_by;
    }


    public MomList(String text_line_no, String text_matter, String text_responsible, String text_attachments, String text_date) {
        this.text_line_no = text_line_no;
        this.text_matter = text_matter;
        this.text_responsible = text_responsible;
        this.text_attachments = text_attachments;
        this.text_date = text_date;
    }

    //for MOM
    public MomList(String text_line_no, int original_line_no, String text_matter, String text_responsible, String text_attachments, String text_date) {
        this.text_line_no = text_line_no;
        this.text_matter = text_matter;
        this.text_responsible = text_responsible;
        this.text_attachments = text_attachments;
        this.text_date = text_date;
        this.original_line_no = original_line_no;
    }

    public int getApproved() {
        return approved;
    }

    public int getOriginal_line_no() {
        return original_line_no;
    }

    public String getSubmittal_no() {
        return submittal_no;
    }

    public String getSl_no() {
        return sl_no;
    }

    public String getTitle() {
        return title;
    }

    public String getStart_date() {
        return start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public String getSlNo() {
        return slNo;
    }

    public String getSubmittalId() {
        return submittalId;
    }

    public String getCurrentProjectNo() {
        return currentProjectNo;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getSubmittalRegisterId() {
        return submittalRegisterId;
    }

    public String getSubmittalsType() {
        return submittalsType;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public String getText_line_no() {
        return text_line_no;
    }

    public void setText_line_no(String text_line_no) {
        this.text_line_no = text_line_no;
    }

    public String getText_matter() {
        return text_matter;
    }

    public String getText_responsible() {
        return text_responsible;
    }

    public String getText_attachments() {
        return text_attachments;
    }

    public String getText_date() {
        return text_date;
    }

    public String getText_mom_rec() {
        return text_mom_rec;
    }

    public String getText_project_id() {
        return text_project_id;
    }

    public String getText_project_name() {
        return text_project_name;
    }

    public String getText_created_by() {
        return text_created_by;
    }

    public String getQir_no() {
        return qir_no;
    }

    public String getQuality_index() {
        return quality_index;
    }

    public String getVendor_id() {
        return vendor_id;
    }

    public String getReceipt_no() {
        return receipt_no;
    }

    public String getProject_id() {
        return project_id;
    }

    public String getCreated_by() {
        return created_by;
    }

    public String getMom_index() {
        return mom_index;
    }

    public String getMom_rec_no() {
        return mom_rec_no;
    }

    public String getProject_name() {
        return project_name;
    }

    public String getDate() {
        return date;
    }

    public String getPunch_index() {
        return punch_index;
    }

    public String getPunch_list_no() {
        return punch_list_no;
    }

    public String getVendor_name() {
        return vendor_name;
    }

    public String getInvoice_index() {
        return invoice_index;
    }

    public String getInvoice_no() {
        return invoice_no;
    }

    public String getVen_invoice_no() {
        return ven_invoice_no;
    }

    public String getVendor_code() {
        return vendor_code;
    }

    public String getPo_number() {
        return po_number;
    }

    public String getPurchase_order() {
        return purchase_order;
    }


}