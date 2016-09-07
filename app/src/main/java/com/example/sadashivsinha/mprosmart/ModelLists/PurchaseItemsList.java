package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 04-Apr-16.
 */
public class PurchaseItemsList
{

        private String  item_index, item_no, desc, po_quantity, uom, need_by_date, unit_cost, currency, amount;


        //for new purchase receipts

        public PurchaseItemsList(String item_index,String item_no, String desc, String po_quantity, String uom,
                                  String need_by_date,String unit_cost, String currency, String amount)
        {
            this.item_index = item_index;
            this.item_no = item_no;
            this.desc = desc;
            this.po_quantity = po_quantity;
            this.uom = uom;
            this.need_by_date = need_by_date;
            this.unit_cost = unit_cost;
            this.currency = currency;
            this.amount = amount;
        }

        public String getItem_index()
        {
            return item_index;
        }

        public String getItem_no()
        {
            return item_no;
        }

        public String getDesc()
        {
            return desc;
        }

        public String getPo_quantity()
        {
            return po_quantity;
        }

        public String getUom()
        {
            return uom;
        }

        public String getNeed_by_date()
        {
            return need_by_date;
        }

        public String getUnit_cost()
        {
            return unit_cost;
        }

        public String getCurrency()
        {
            return currency;
        }

        public String getAmount()
        {
            return amount;
        }
}