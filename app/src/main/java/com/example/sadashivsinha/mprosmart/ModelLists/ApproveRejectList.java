package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 25-Aug-16.
 */
public class ApproveRejectList {
    String text_message, text_id, text_created_by, text_created_on, entityName, entityTName;

    public ApproveRejectList(String text_message, String text_id, String text_created_by, String text_created_on, String entityName,
                             String entityTName) {
        this.text_message = text_message;
        this.text_id = text_id;
        this.text_created_by = text_created_by;
        this.text_created_on = text_created_on;
        this.entityName = entityName;
        this.entityTName = entityTName;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getEntityTName() {
        return entityTName;
    }

    public String getText_message() {
        return text_message;
    }

    public String getText_id() {
        return text_id;
    }

    public String getText_created_by() {
        return text_created_by;
    }

    public String getText_created_on() {
        return text_created_on;
    }
}
