package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 02-Jul-16.
 */
public class ActivitiesInWbsList {
    String id ,activityName ,progress ,startDate, endDate, resourceAllocated ,boq, status;

    public ActivitiesInWbsList(String id, String activityName, String progress,
                               String startDate, String endDate, String resourceAllocated, String boq, String status) {
        this.id = id;
        this.activityName = activityName;
        this.progress = progress;
        this.startDate = startDate;
        this.endDate = endDate;
        this.resourceAllocated = resourceAllocated;
        this.boq = boq;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }

    public String getActivityName() {
        return activityName;
    }

    public String getProgress() {
        return progress;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getResourceAllocated() {
        return resourceAllocated;
    }

    public String getBoq() {
        return boq;
    }
}
