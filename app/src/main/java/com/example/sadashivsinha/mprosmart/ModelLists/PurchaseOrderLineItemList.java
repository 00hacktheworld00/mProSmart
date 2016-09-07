package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 17-May-16.
 */
public class PurchaseOrderLineItemList {

    String itemId, itemName, itemDesc, itemUom, itemQuantity, totalAmount, needByDate, unitCost;

    public PurchaseOrderLineItemList(String itemId, String itemName, String itemDesc, String itemUom, String itemQuantity,
                                     String totalAmount, String needByDate, String unitCost) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemDesc = itemDesc;
        this.itemUom = itemUom;
        this.itemQuantity = itemQuantity;
        this.totalAmount = totalAmount;
        this.needByDate = needByDate;
        this.unitCost = unitCost;
    }

    public String getUnitCost() {
        return unitCost;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public String getNeedByDate() {
        return needByDate;
    }

    public String getItemId() {

        return itemId;
    }

    public String getItemQuantity() {

        return itemQuantity;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public String getItemUom() {
        return itemUom;
    }
}
