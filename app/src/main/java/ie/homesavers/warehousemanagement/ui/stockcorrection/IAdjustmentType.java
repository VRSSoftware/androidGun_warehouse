package ie.homesavers.warehousemanagement.ui.stockcorrection;

import ie.homesavers.warehousemanagement.webservices.stock_correction.AdjustmentTypeModel;

public interface IAdjustmentType {
    void onAdjustmentTypeClicked(AdjustmentTypeModel adjustmentTypeModel, int position);
}
