package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 22-Jul-16.
 */
public class AllVendorList {
    String sl_no, vendor_id, vendor_name, vendor_type, discipline, text_tax_id, text_licence_no, text_company_name;

    public AllVendorList(String sl_no, String vendor_id, String vendor_name, String vendor_type, String discipline,
                         String text_tax_id, String text_licence_no, String text_company_name) {
        this.sl_no = sl_no;
        this.vendor_id = vendor_id;
        this.vendor_name = vendor_name;
        this.vendor_type = vendor_type;
        this.discipline = discipline;
        this.text_tax_id = text_tax_id;
        this.text_licence_no = text_licence_no;
        this.text_company_name = text_company_name;
    }

    public String getText_company_name() {
        return text_company_name;
    }

    public String getText_tax_id() {

        return text_tax_id;
    }

    public String getText_licence_no() {
        return text_licence_no;
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
