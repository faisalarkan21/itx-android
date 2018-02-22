package com.itx.android.android_itx.Service;

import com.itx.android.android_itx.Entity.Users;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

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





}
