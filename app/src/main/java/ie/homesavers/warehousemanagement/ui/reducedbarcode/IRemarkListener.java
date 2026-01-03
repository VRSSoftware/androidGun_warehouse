package ie.homesavers.warehousemanagement.ui.reducedbarcode;

import ie.homesavers.warehousemanagement.webservices.reducedbarcode.ReducedBarcodeRemarkList;

public interface IRemarkListener {
    void onRemarkNameClicked(ReducedBarcodeRemarkList reducedBarcodeRemarkList, int position);
}
