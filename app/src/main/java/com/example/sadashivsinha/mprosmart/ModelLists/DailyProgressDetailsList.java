package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 23-Aug-16.
 */
public class DailyProgressDetailsList {
    String wbs_name, activity;
    String completed, res_worked, percent_completed_today, percent_completed_total;

    public DailyProgressDetailsList(String wbs_name, String activity, String completed, String res_worked,
                                    String percent_completed_today, String percent_completed_total)
    {
        this.wbs_name = wbs_name;
        this.activity = activity;
        this.completed = completed;
        this.res_worked = res_worked;
        this.percent_completed_today = percent_completed_today;
        this.percent_completed_total = percent_completed_total;
    }

    public String getPercent_completed_today() {
        return percent_completed_today;
    }

    public String getPercent_completed_total() {
        return percent_completed_total;
    }

    public String getWbs_name() {
        return wbs_name;
    }

    public String getActivity() {
        return activity;
    }
    public String getCompleted() {
        return completed;
    }

    public String getRes_worked() {
        return res_worked;
    }
}
