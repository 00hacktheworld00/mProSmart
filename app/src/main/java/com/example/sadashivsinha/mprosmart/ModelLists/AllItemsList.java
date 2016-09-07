package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 23-Aug-16.
 */
public class AllItemsList {
    String sl_no, item_id, item_name, item_desc, uom;

    public AllItemsList(String sl_no, String item_id, String item_name, String item_desc, String uom) {
        this.sl_no = sl_no;
        this.item_id = item_id;
        this.item_name = item_name;
        this.item_desc = item_desc;
        this.uom = uom;
    }

    public String getSl_no() {
        return sl_no;
    }

    public String getItem_id() {
        return item_id;
    }

    public String getItem_name() {
        return item_name;
    }

    public String getItem_desc() {
        return item_desc;
    }

    public String getUom() {
        return uom;
    }
}
