package ie.homesavers.warehousemanagement.ui.printpricetag;

import ie.homesavers.warehousemanagement.webservices.check_stock.CheckStockModel;

public interface PrintTagPriceListener {
    void onPrintTagClicked(CheckStockModel checkStockModel);
}
