package ie.homesavers.warehousemanagement.ui.grn;


import ie.homesavers.warehousemanagement.webservices.grn.GrnWithPurOrderModel;

public interface IOnSaveGrnQty {
    void onStockItemChange(GrnWithPurOrderModel grnWithPurOrderModel, int position);
}
