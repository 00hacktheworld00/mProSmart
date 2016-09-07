package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 01-Mar-16.
 */
public class QualityList{
        private String item_id, item_desc, received_quantity, quantity_accept, quantity_reject, attachments;

        public QualityList(String item_id, String item_desc,
                        String received_quantity, String quantity_accept, String quantity_reject, String attachments) {
            this.item_id = item_id;
            this.item_desc = item_desc;
            this.received_quantity = received_quantity;
            this.quantity_accept = quantity_accept;
            this.quantity_reject = quantity_reject;
            this.attachments = attachments;
        }

        public String getItemId() {
            return item_id;
        }

        public String getItemDesc() {
            return item_desc;
        }

        public String getReceivedQuantity() {
            return received_quantity;
        }

        public String getQuantityAccept() {
            return quantity_accept;
        }

        public String getQuantityReject() {
        return quantity_reject;
    }

        public String getAttachments() {
            return attachments;
        }
}