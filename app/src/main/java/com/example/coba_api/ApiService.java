package com.example.coba_api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface ApiService {

    @GET("expedition/shipping-cost")
    Call<ResponseBody> getShippingCost(
            @Header("x-api-co-id") String apiKey,
            @Query("origin_village_code") String origin,
            @Query("destination_village_code") String destination,
            @Query("weight") int weight,
            @Query("expedition") String expedition
    );
}
