package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 30-Jun-16.
 */
public class QualityChecklistList {
    String text_subject, text_status, text_comments, id;
    public QualityChecklistList(String id, String text_subject, String text_status, String text_comments) {
        this.id = id;
        this.text_subject = text_subject;
        this.text_status = text_status;
        this.text_comments = text_comments;
    }


    public String getId() {
        return id;
    }

    public String getText_subject() {

        return text_subject;
    }

    public String getText_status() {
        return text_status;
    }

    public String getText_comments() {
        return text_comments;
    }
}
