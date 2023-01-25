package com.maffin.recipes.network;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ReceiptService {

    @GET("/recipes/list")
    Call<ResponseSuccess<Receipt>> fetchReceiptList();
}
