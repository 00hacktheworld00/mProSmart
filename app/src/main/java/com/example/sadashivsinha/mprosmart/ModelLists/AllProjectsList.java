package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 25-Feb-16.
 */
public class AllProjectsList {
    private String startedDate, finishByDate, purchaseReceipts, itemsReceived, descOne, descTwo, last_updated_time, title, wbsId, notes;
    private int profilePic, companyLogo;
    private int projectNo;

    public AllProjectsList() {
    }

    public AllProjectsList(int projectNo, String startedDate, String finishByDate, String purchaseReceipts, String itemsReceived,
                           String descOne, String descTwo, int profilePic, int companyLogo , String last_updated_time, String title, String wbsId, String notes) {

        this.projectNo = projectNo;
        this.startedDate = startedDate;
        this.finishByDate = finishByDate;
        this.purchaseReceipts = purchaseReceipts;
        this.itemsReceived = itemsReceived;
        this.descOne = descOne;
        this.descTwo = descTwo;
        this.profilePic = profilePic;
        this.companyLogo = companyLogo;
        this.last_updated_time = last_updated_time;
        this.title = title;
        this.wbsId = wbsId;
        this.notes = notes;
    }

    public int getProfilePic() {
        return profilePic;
    }

    public int getCompanyLogo() {
        return companyLogo;
    }

    public String getStartedDate() {
        return startedDate;
    }

    public String getFinishByDate() {
        return finishByDate;
    }

    public String getPurchaseReceipts() {
        return purchaseReceipts;
    }

    public String getItemsReceived() {
        return itemsReceived;
    }

    public String getDescOne() {
        return descOne;
    }

    public String getDescTwo() {
        return descTwo;
    }

    public String getLast_updated_time() {
        return last_updated_time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getProjectNo() {
        return projectNo;
    }

    public String getWbsId() {
        return wbsId;
    }

    public String getNotes() {
        return notes;
    }
}