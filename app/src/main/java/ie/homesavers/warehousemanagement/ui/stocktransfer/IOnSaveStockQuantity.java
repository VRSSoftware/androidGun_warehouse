package ie.homesavers.warehousemanagement.ui.stocktransfer;


import ie.homesavers.warehousemanagement.webservices.save_stock_trans.SaveStockTransferModel;

public interface  IOnSaveStockQuantity {
    void onStockItemChange(SaveStockTransferModel saveStockTransferModel, int position);
}
