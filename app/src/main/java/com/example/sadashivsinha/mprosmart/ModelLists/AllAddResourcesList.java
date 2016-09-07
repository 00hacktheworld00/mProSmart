package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 25-Jul-16.
 */
public class AllAddResourcesList {
    String sl_no, res_id, res_name, res_type, designation, email, phone, rate_per_hour, currency;;

    public AllAddResourcesList(String sl_no, String res_id, String res_name, String res_type,
                               String designation, String email, String phone, String rate_per_hour, String currency) {
        this.sl_no = sl_no;
        this.res_id = res_id;
        this.res_name = res_name;
        this.res_type = res_type;
        this.designation = designation;
        this.email = email;
        this.phone = phone;
        this.rate_per_hour = rate_per_hour;
        this.currency = currency;
    }

    public String getSl_no() {

        return sl_no;
    }

    public String getRes_id() {
        return res_id;
    }

    public String getRes_name() {
        return res_name;
    }

    public String getRes_type() {
        return res_type;
    }

    public String getDesignation() {
        return designation;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getRate_per_hour() {
        return rate_per_hour;
    }

    public String getCurrency() {
        return currency;
    }
}
