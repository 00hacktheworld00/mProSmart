package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 13-Jul-16.
 */
public class SiteList {
    String notes, createdBy, createdOn, slNo;

    public SiteList(String notes, String createdBy, String createdOn, String slNo) {
        this.notes = notes;
        this.createdBy = createdBy;
        this.createdOn = createdOn;
        this.slNo = slNo;
    }

    public String getNotes() {

        return notes;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public String getSlNo() {
        return slNo;
    }
}
