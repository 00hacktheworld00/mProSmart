package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 23-Aug-16.
 */
public class DailyProgressDetailsList {
    String wbs_name, activity;
    String target_date, completed, percent_completed, res_worked, weather_title;

    public DailyProgressDetailsList(String wbs_name, String activity, String target_date, String completed,
                                    String percent_completed, String res_worked, String weather_title)
    {
        this.wbs_name = wbs_name;
        this.activity = activity;
        this.target_date = target_date;
        this.completed = completed;
        this.percent_completed = percent_completed;
        this.res_worked = res_worked;
        this.weather_title = weather_title;
    }

    public String getWbs_name() {
        return wbs_name;
    }

    public String getActivity() {
        return activity;
    }

    public String getTarget_date() {
        return target_date;
    }

    public String getCompleted() {
        return completed;
    }

    public String getPercent_completed() {
        return percent_completed;
    }

    public String getRes_worked() {
        return res_worked;
    }

    public String getWeather_title() {
        return weather_title;
    }
}
