package com.itx.android.android_itx.Service;

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

/**
 * Created by faisal on 2/22/18.
 */

public interface UsersService {

    @GET("/api/users?page=1&limit=1000")
    Call<Response.GetAllUser> getAUsers();

    @POST("/api/user/create")
    Call<Response.CreateUser> createUser(@Body Request.CreateUser user);

    @GET("/api/user/{id}")
    Call<JsonObject> getUser(@Path("id") String postfix);

    @POST("/api/user/update/{id}")
    Call<Response.UpdateUser> updateUser(@Path("id") String postfix, @Body Request.UpdateUser user);

    @POST("/api/user/delete/{id}")
    Call<Response.DeleteUser> deleteUser(@Path("id") String postfix);

}
