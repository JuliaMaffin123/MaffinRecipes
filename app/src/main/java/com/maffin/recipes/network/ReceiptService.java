package com.maffin.recipes.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ReceiptService {

    @GET("/recipes/list")
    Call<ResponseSuccess<Receipt>> fetchReceiptList();

    @GET("/recipes/{id}")
    Call<ResponseSuccess<Receipt>> fetchReceipt(@Path("id") long id);

    @GET("/components/{id}")
    Call<ResponseSuccess<Component>> fetchComponents(@Path("id") long id);

    @GET("/steps/{id}")
    Call<ResponseSuccess<Step>> fetchSteps(@Path("id") long id);
}
