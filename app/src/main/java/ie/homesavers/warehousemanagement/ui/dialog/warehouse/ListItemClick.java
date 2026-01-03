package ie.homesavers.warehousemanagement.ui.dialog.warehouse;

import ie.homesavers.warehousemanagement.webservices.warehouse.ToWarehouse;
import ie.homesavers.warehousemanagement.webservices.warehouse.WarehouseModel;

public interface ListItemClick {
    void onItemClicked(WarehouseModel warehouseModel);
    void onItemClickedBusinessLocation(WarehouseModel warehouseModel);
    void onItemClickedFromLocation(WarehouseModel warehouseModel);
    void onItemClickedToLocation(ToWarehouse toWarehouse);

}
