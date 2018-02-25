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

public interface ListUsersService {

    @GET("/api/users")
    Call<JsonObject> getAUsers();

    @POST("/api/user/create")
    Call<ResponseBody> createUser(@Body RequestBody params);

    @POST("/api/user/delete/{id}")
    Call<ResponseBody> deleteUser(@Path("id") String postfix);

}
