package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 15-Jul-16.
 */
public class PurchaseReceiptCreateNewList {
    String item_id, new_quantity, purchaseLineItemsId, purchaseReceiptItemsId, quantity, poQuantity, unit_cost,
            acceptedQuantity, rejectedQuantity, item_name, item_desc, uom_id, needByDate, purId;

    public PurchaseReceiptCreateNewList(int val, String item_id, String quantity, String unit_cost) {
        this.item_id = item_id;
        this.quantity = quantity;
        this.unit_cost = unit_cost;
    }
    public PurchaseReceiptCreateNewList(String purchaseReceiptItemsId, String quantity, String poQuantity, String unit_cost, String acceptedQuantity, String rejectedQuantity) {
        this.purchaseReceiptItemsId = purchaseReceiptItemsId;
        this.quantity = quantity;
        this.poQuantity = poQuantity;
        this.unit_cost = unit_cost;
        this.acceptedQuantity = acceptedQuantity;
        this.rejectedQuantity = rejectedQuantity;
    }

    public PurchaseReceiptCreateNewList(int val, String item_id, String quantity, String new_quantity, String unit_cost) {
        this.item_id = item_id;
        this.quantity = quantity;
        this.new_quantity = new_quantity;
        this.unit_cost = unit_cost;
    }

    public PurchaseReceiptCreateNewList(String item_id, String item_name, String item_desc, String uom_id, String quantity,
                                        String needByDate, int a) {
        this.item_id = item_id;
        this.item_name = item_name;
        this.item_desc = item_desc;
        this.uom_id = uom_id;
        this.quantity = quantity;
        this.needByDate = needByDate;
    }

    public PurchaseReceiptCreateNewList(String purId, String purchaseReceiptItemsId, String quantity, String poQuantity, String unit_cost, String acceptedQuantity, String rejectedQuantity) {
        this.purchaseReceiptItemsId = purchaseReceiptItemsId;
        this.quantity = quantity;
        this.poQuantity = poQuantity;
        this.unit_cost = unit_cost;
        this.acceptedQuantity = acceptedQuantity;
        this.rejectedQuantity = rejectedQuantity;
        this.purId = purId;
    }

    public String getPurId() {
        return purId;
    }

    public String getNeedByDate() {
        return needByDate;
    }

    public String getItem_name() {
        return item_name;
    }

    public String getItem_desc() {
        return item_desc;
    }

    public String getUom_id() {
        return uom_id;
    }

    public String getAcceptedQuantity() {
        return acceptedQuantity;
    }

    public String getRejectedQuantity() {
        return rejectedQuantity;
    }

    public String getUnit_cost() {
        return unit_cost;
    }

    public String getPurchaseReceiptItemsId() {
        return purchaseReceiptItemsId;
    }

    public String getPoQuantity() {
        return poQuantity;
    }

    public String getNew_quantity() {
        return new_quantity;
    }

    public String getItem_id() {

        return item_id;
    }

    public String getQuantity() {
        return quantity;
    }

}
