package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 02-Mar-16.
 */
public class InspectionList{

    private String item_id, line_no, item_desc, uom;

        public InspectionList()
                {
                }

        public InspectionList(String item_id, String line_no, String item_desc, String uom)
        {
            this.item_id = item_id;
            this.line_no = line_no;
            this.item_desc = item_desc;
            this.uom = uom;
        }

        public String getItemId()
        {
                return item_id;
        }

        public void setItemId(String item_id)
        {
                this.item_id = item_id;
        }

        public String getLine_no()
        {
                return line_no;
        }

        public void setLine_no(String line_no)
        {
                this.line_no = line_no;
        }

        public String getItem_desc()
        {
                return item_desc;
        }

        public void setItem_desc(String item_desc)
        {
                this.item_desc = item_desc;
        }

        public String getUom()
        {
                return uom;
        }

        public void setUom(String uom)
        {
                this.uom = uom;
        }
}