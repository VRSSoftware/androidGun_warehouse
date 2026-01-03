package ie.homesavers.warehousemanagement.ui.stockcorrection;

import ie.homesavers.warehousemanagement.webservices.stock_correction.DamageReasonModel;

public interface IDamageReason {
    void onDamageReasonClicked(DamageReasonModel damageReasonModel, int position);
}
