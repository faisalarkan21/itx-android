package com.itx.android.android_itx.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.itx.android.android_itx.Entity.Request;
import com.itx.android.android_itx.Entity.Response;

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

public interface AssetService {


    @GET("/api/assets")
    Call<Response.GetAsset> getUserAssets(@Query("user") String postfix);

    @GET("/api/asset/{id}")
    Call<JsonObject> getUserAsset(@Path("id") String postfix);

    @GET("/api/asset/{id}/images")
    Call<JsonObject> getAssetImages(@Path("id") String id);

    @POST("/api/asset/create")
    Call<Response.CreateAsset> createAsset(@Body Request.CreateAsset asset);

    @POST("/api/asset/update/{id}")
    Call<Response.UpdateAsset> updateAsset(@Path("id") String postfix, @Body Request.UpdateAsset asset);

    @GET("/api/asset-categories")
    Call<Response.GetAssetCategory> getAssetCategories();

    @POST("/api/asset/delete/{id}")
    Call<Response.DeleteAsset> deleteAssets(@Path("id") String postfix);

}
