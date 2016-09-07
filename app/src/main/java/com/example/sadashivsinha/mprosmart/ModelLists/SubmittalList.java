package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 15-Mar-16.
 */
public class SubmittalList {
    private String line_no, text_sub_title, text_sub_type, text_status, text_contract_id, attachments;
    private String text_doc_type, text_short_desc, text_variation, text_variation_desc, text_attachment;

    //for Submittal

    public SubmittalList(String line_no, String text_doc_type, String text_short_desc, String text_variation,
                         String text_variation_desc, String text_status, String text_attachment)
    {
        this.line_no = line_no;
        this.text_doc_type = text_doc_type;
        this.text_short_desc = text_short_desc;
        this.text_variation = text_variation;
        this.text_variation_desc = text_variation_desc;
        this.text_status = text_status;
        this.text_attachment = text_attachment;
    }


    //for Submittal Register
    public SubmittalList(String line_no, String text_sub_title, String text_sub_type,
                         String text_status, String text_contract_id, String attachments)
    {
        this.line_no = line_no;
        this.text_sub_title = text_sub_title;
        this.text_sub_type = text_sub_type;
        this.text_status = text_status;
        this.text_contract_id = text_contract_id;
        this.attachments = attachments;

    }

    public String getAttachments() {
        return attachments;
    }

    public String getLine_no() {
        return line_no;
    }

    public String getText_sub_title() {
        return text_sub_title;
    }

    public String getText_sub_type() {
        return text_sub_type;
    }

    public String getText_status() {
        return text_status;
    }

    public String getText_contract_id() {
        return text_contract_id;
    }



    //for submittal

    public String getText_doc_type() {
        return text_doc_type;
    }

    public String getText_short_desc() {
        return text_short_desc;
    }

    public String getText_variation() {
        return text_variation;
    }

    public String getText_variation_desc() {
        return text_variation_desc;
    }

    public String getText_attachment() {
        return text_attachment;
    }
}