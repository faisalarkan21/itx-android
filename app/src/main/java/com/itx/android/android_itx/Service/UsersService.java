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

public interface UsersService {

    @GET("/api/users")
    Call<JsonObject> getAUsers();

    @POST("/api/user/create")
    Call<ResponseBody> createUser(@Body RequestBody params);

    @GET("/api/user/{id}")
    Call<JsonObject> getUser(@Path("id") String postfix);

    @POST("/api/user/update/{id}")
    Call<ResponseBody> updateUser(@Path("id") String postfix, @Body RequestBody params);

    @POST("/api/user/delete/{id}")
    Call<ResponseBody> deleteUser(@Path("id") String postfix);

}
