package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 01-Jul-16.
 */
public class BoqList {

    String text_item, text_quantity, text_uom, itemName;

    public BoqList(String text_item, String text_quantity, String text_uom, String itemName) {
        this.text_item = text_item;
        this.text_quantity = text_quantity;
        this.text_uom = text_uom;
        this.itemName = itemName;
    }

    public String getItemName() {
        return itemName;
    }

    public String getText_item() {

        return text_item;
    }

    public String getText_quantity() {
        return text_quantity;
    }

    public String getText_uom() {
        return text_uom;
    }
}
