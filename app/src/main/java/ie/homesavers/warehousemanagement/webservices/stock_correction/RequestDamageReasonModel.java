package ie.homesavers.warehousemanagement.webservices.stock_correction;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RequestDamageReasonModel {

    @SerializedName("cobr_id")
    @Expose
    private String cobrId;

    public String getCobrId() {
        return cobrId;
    }

    public void setCobrId(String cobrId) {
        this.cobrId = cobrId;
    }

}