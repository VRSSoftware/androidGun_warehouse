package ie.homesavers.warehousemanagement.ui.purchaseorder;


import ie.homesavers.warehousemanagement.webservices.supplier.SupplierModel;

public interface CustomItemClickListener {
   void onItemClick(SupplierModel model, int position);
}
