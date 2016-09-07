package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 30-Jun-16.
 */
public class QualityStandardList {

    String text_criteria, text_uom, text_result, text_status, text_comments;

    public QualityStandardList(String text_criteria, String text_uom, String text_result, String text_status, String text_comments) {
        this.text_criteria = text_criteria;
        this.text_uom = text_uom;
        this.text_result = text_result;
        this.text_status = text_status;
        this.text_comments = text_comments;
    }

    public String getText_criteria() {

        return text_criteria;
    }

    public String getText_uom() {
        return text_uom;
    }

    public String getText_result() {
        return text_result;
    }

    public String getText_status() {
        return text_status;
    }

    public String getText_comments() {
        return text_comments;
    }
}
