package ie.homesavers.warehousemanagement.webservices.save_stock_trans;


import ie.homesavers.warehousemanagement.webservices.stock_notification.StockTransferNotificationResponse;
import ie.homesavers.warehousemanagement.ui.stocktransfernotification.RequestStockTransferNotification;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IPickHeldNotification {
    @POST("api/User/StockTransferNotification")
    Call<ArrayList<StockTransferNotificationResponse>> getStockTransferNotificationViewModel(@Body RequestStockTransferNotification requestStockTransferNotification);
}
