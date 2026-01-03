package ie.homesavers.warehousemanagement.webservices.stock_correction;



import ie.homesavers.warehousemanagement.ui.stockcorrection.HeldStockCorrectionModel;
import ie.homesavers.warehousemanagement.ui.stockcorrection.RequestHeldStockCorrection;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IStockCorrection {
    @POST("api/User/HeldStockCorrection")
    Call<ArrayList<HeldStockCorrectionModel>> getHeldStockCorrectionModel(@Body  RequestHeldStockCorrection requestHeldStockCorrection);

    @POST("api/User/StockCorrection")
    Call<StockCorrectionRequest> setStockCorrectionModel(@Body JsonObject stockCorrectionRequest);

    @POST("api/User/StockCorrectionReasonList")
    Call<ArrayList<DamageReasonModels>> setDamageReasonModels(@Body JsonObject damageReasonModels);
}