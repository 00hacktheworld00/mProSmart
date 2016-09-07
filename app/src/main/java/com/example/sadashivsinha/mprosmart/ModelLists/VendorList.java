package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 22-Jul-16.
 */
public class VendorList {
    String text_first_name, text_last_name, text_phone, text_email, text_building_no, text_street_name, text_locality,
            text_state, text_country, text_zipcode;

    public VendorList(String text_first_name, String text_last_name, String text_phone, String text_email,
                      String text_building_no, String text_street_name,
                      String text_locality, String text_state, String text_country, String text_zipcode) {
        this.text_first_name = text_first_name;
        this.text_last_name = text_last_name;
        this.text_phone = text_phone;
        this.text_email = text_email;
        this.text_building_no = text_building_no;
        this.text_street_name = text_street_name;
        this.text_locality = text_locality;
        this.text_state = text_state;
        this.text_country = text_country;
        this.text_zipcode = text_zipcode;
    }

    public String getText_first_name() {

        return text_first_name;
    }

    public String getText_last_name() {
        return text_last_name;
    }

    public String getText_phone() {
        return text_phone;
    }

    public String getText_email() {
        return text_email;
    }

    public String getText_building_no() {
        return text_building_no;
    }

    public String getText_street_name() {
        return text_street_name;
    }

    public String getText_locality() {
        return text_locality;
    }

    public String getText_state() {
        return text_state;
    }

    public String getText_country() {
        return text_country;
    }

    public String getText_zipcode() {
        return text_zipcode;
    }
}
