package com.itx.android.android_itx.Service;

import com.google.gson.JsonObject;
import com.itx.android.android_itx.Entity.Users;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by faisal on 2/21/18.
 */

public interface APIService {

    @Multipart
    @POST("/api/upload")
    Call<ResponseBody> uploadPhoto(@Part MultipartBody.Part photo);

    @Multipart
    @POST("/api/upload")
    Call<ResponseBody> uploadPhotos(@Part MultipartBody.Part[] photos);

    @POST("https://maps.googleapis.com/maps/api/geocode/json")
    Call<JsonObject> reverseGeocode(@Query("latlng") String langlat,
                                    @Query("key") String key);


    // https://maps.googleapis.com/maps/api/staticmap?center=60.714728,-73.998672&zoom=14&size=600x300
    @POST("https://maps.googleapis.com/maps/api/staticmap")
    Call<ResponseBody> getStaticMap(@Query("center") String staticMap,
                                    @Query("zoom") String zoom,
                                    @Query("size") String size,
                                    @Query("markers") String marker,
                                    @Query("key") String key);


}
