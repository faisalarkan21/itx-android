package com.itx.android.android_itx.Service;

import com.google.gson.JsonObject;
import com.itx.android.android_itx.Entity.Request;
import com.itx.android.android_itx.Entity.Response;

import org.json.JSONObject;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by faisal on 2/22/18.
 */

public interface InventoryService {

    @GET("/api/inventory-categories?page=1&limit=1000")
    Call<Response.GetInventory> getUserInventories(@Query("asset") String postfix);

    @GET("/api/facilities?page=1&limit=1000")
    Call<Response.GetFacillity> getAllFacilities();

    @POST("/api/inventory-category/create")
    Call<Response.CreateInventory> createInventoryCategory (@Body Request.CreateInventory inventory);

    @POST("/api/inventory-category/delete/{id}")
    Call<Response.DeleteInventory> deleteInventoryCategory (@Path("id") String params);

    @GET("/api/inventory-category/{id}")
    Call<JsonObject> getUserInventory(@Path("id") String postfix);

    @POST("/api/inventory-category/update/{id}")
    Call<Response.UpdateInventory> updateInventoryCategory (@Path("id") String postfix, @Body Request.UpdateInventory inventory);


}
