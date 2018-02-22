package com.itx.android.android_itx.Service;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by faisal on 2/22/18.
 */

public interface ListInventoryService {

    @GET("/api/asset/inventory-categories/{id}")
    Call<JsonObject> getUserInventory(@Path("id") String postfix);

}
