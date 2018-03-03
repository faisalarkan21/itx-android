package com.itx.android.android_itx.Service;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by faisal on 2/22/18.
 */

public interface InventoryService {

    @GET("/api/asset/inventory-categories/{id}")
    Call<JsonObject> getUserInventories(@Path("id") String postfix);

    @GET("/api/facilities")
    Call<JsonObject> getAllFacilities();

    @POST("/api/inventory-category/create")
    Call<ResponseBody> createInventoryCategory (@Body RequestBody params);

    @POST("/api/inventory-category/delete/{id}")
    Call<ResponseBody> deleteInventoryCategory (@Path("id") String params);

    @GET("/api/inventory-category/{id}")
    Call<JsonObject> getUserInventory(@Path("id") String postfix);

    @POST("/api/inventory-category/update/{id}")
    Call<ResponseBody> updateInventoryCategory (@Path("id") String postfix, @Body RequestBody params);


}
