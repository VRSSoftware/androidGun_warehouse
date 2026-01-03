package ie.homesavers.warehousemanagement.webservices.save_stock_trans;


import ie.homesavers.warehousemanagement.ui.acceptstocktransferList.AcceptStockTransferListModel;
import ie.homesavers.warehousemanagement.ui.acceptstocktransferList.RequestAcceptStockTransferList;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IAcceptStockTransferList {
    @POST("api/User/AcceptStockTransferList")
    Call<ArrayList<AcceptStockTransferListModel>> getAcceptStockTransferListModel(@Body RequestAcceptStockTransferList requestAcceptStockTransferList);
}