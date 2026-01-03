package ie.homesavers.warehousemanagement.webservices.company;



import ie.homesavers.warehousemanagement.ui.company.ChangeCompanyModel;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CompanyService {

    @GET("api/User/CompanyList")
    Call<ArrayList<ChangeCompanyModel>> getCompanyList();
}