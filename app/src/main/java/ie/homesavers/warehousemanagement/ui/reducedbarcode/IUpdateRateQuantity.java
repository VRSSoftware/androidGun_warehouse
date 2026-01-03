package ie.homesavers.warehousemanagement.ui.reducedbarcode;


import ie.homesavers.warehousemanagement.webservices.check_stock.PriceListModel;
import ie.homesavers.warehousemanagement.webservices.reducedbarcode.RequestSaveReducedBarcodeDetail;

public interface IUpdateRateQuantity {
    void onUpdateRateChange(PriceListModel checkStockModel, int position);
    void onRemarkClicked(int position);
    void onChangeNewRate(RequestSaveReducedBarcodeDetail requestSaveReducedBarcodeDetail, int position);
}
