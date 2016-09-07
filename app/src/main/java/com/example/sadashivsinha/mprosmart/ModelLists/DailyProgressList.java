package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 22-Jul-16.
 */
public class DailyProgressList {
    String sl_no, created_by, weather, date;

    public DailyProgressList(String sl_no, String created_by, String weather, String date) {
        this.sl_no = sl_no;
        this.created_by = created_by;
        this.weather = weather;
        this.date = date;
    }

    public String getSl_no() {

        return sl_no;
    }

    public String getCreated_by() {
        return created_by;
    }

    public String getWeather() {
        return weather;
    }

    public String getDate() {
        return date;
    }
}
