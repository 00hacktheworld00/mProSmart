package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 22-Jul-16.
 */
public class AllVendorList {
    String sl_no, vendor_id, vendor_name, vendor_type, discipline;

    public AllVendorList(String sl_no, String vendor_id, String vendor_name, String vendor_type, String discipline) {
        this.sl_no = sl_no;
        this.vendor_id = vendor_id;
        this.vendor_name = vendor_name;
        this.vendor_type = vendor_type;
        this.discipline = discipline;
    }

    public String getSl_no() {

        return sl_no;
    }

    public String getVendor_id() {
        return vendor_id;
    }

    public String getVendor_name() {
        return vendor_name;
    }

    public String getVendor_type() {
        return vendor_type;
    }

    public String getDiscipline() {
        return discipline;
    }
}
