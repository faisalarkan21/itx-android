package com.itx.android.android_itx.Service;

import com.google.gson.JsonObject;
import com.itx.android.android_itx.Entity.Users;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by faisal on 2/21/18.
 */

public interface APIService {

    @POST("/posts")
    @FormUrlEncoded
    Call<Users> savePost(@Field("idUser") int idUser,
                         @Field("firstName") String firstName,
                         @Field("lastName") String lastName,
                         @Field("ktp") String ktp,
                         @Field("email") String email,
                         @Field("password") String password,
                         @Field("phone") String phone,
                         @Field("socials") String socials,
                         @Field("address") String address,
                         @Field("assets") String assets,
                         @Field("photo") String photo,
                         @Field("isVerified") String isVerified,
                         @Field("updatedAt") String updatedAt,
                         @Field("updatedAt") String createdAt,
                         @Field("updatedAt") String deletedAt );


    @Multipart
    @POST("/api/upload")
    Call<ResponseBody> uploadPhoto(@Part MultipartBody.Part photo);

    @Multipart
    @POST("/api/upload")
    Call<ResponseBody> uploadPhotos(@Part MultipartBody.Part[] photos);

    @FormUrlEncoded
    @POST("https://maps.googleapis.com/maps/api/geocode/json?latlng={lat},{lng}&key={key}")
    Call<JsonObject> reverseGeocode(@Path("lat") double lat,
                                    @Path("lng") double lng,
                                    @Path("key") String key);


}
