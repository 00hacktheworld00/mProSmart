package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 01-Jul-16.
 */
public class PurchaseRequisitionList {
    String text_line_no, text_item_id, text_item_desc, text_quantity, text_uom, neededBy;

    public PurchaseRequisitionList(String text_line_no, String text_item_id, String text_item_desc, String text_quantity,
                                   String text_uom, String neededBy)
    {
        this.text_line_no = text_line_no;
        this.text_item_id = text_item_id;
        this.text_item_desc = text_item_desc;
        this.text_quantity = text_quantity;
        this.text_uom = text_uom;
        this.neededBy = neededBy;
    }

    public String getNeededBy() {

        return neededBy;
    }

    public String getText_line_no() {

        return text_line_no;
    }

    public String getText_item_id() {
        return text_item_id;
    }

    public String getText_item_desc() {
        return text_item_desc;
    }

    public String getText_quantity() {
        return text_quantity;
    }

    public String getText_uom() {
        return text_uom;
    }
}
