package ie.homesavers.warehousemanagement.ui.stocktransfer;

public interface IOnSaveStock {
    void onStockItemChange(int position);
    void onStockItemRemove(int position);
    void onStockItemDamageReason(int position);
}
