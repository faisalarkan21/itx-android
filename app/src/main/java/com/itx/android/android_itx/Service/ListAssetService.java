package com.itx.android.android_itx.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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

public interface ListAssetService {


    @GET("/api/user/assets/{id}")
    Call<JsonObject> getUserAssets(@Path("id") String postfix);

    @GET("/api/asset/{id}/images")
    Call<JsonObject> getAssetImages(@Path("id") String id);

    @POST("/api/asset/create")
    Call<ResponseBody> createAsset(@Body RequestBody params);

    @GET("/api/asset-categories")
    Call<JsonObject> getAssetCategories();

    @POST("/api/asset/delete/{id}")
    Call<ResponseBody> deleteAssets(@Path("id") String postfix);

}
