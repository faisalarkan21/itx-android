package com.itx.android.android_itx.Service;

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

}
