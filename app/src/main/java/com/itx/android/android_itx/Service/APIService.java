package com.itx.android.android_itx.Service;

import com.google.gson.JsonObject;
import com.itx.android.android_itx.Entity.Response;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by faisal on 2/21/18.
 */

public interface APIService {

    @Multipart
    @POST("/api/upload")
    Call<Response.UploadResponse> uploadPhoto(@Part MultipartBody.Part photo);

    @Multipart
    @POST("/api/upload")
    Call<Response.UploadResponse> uploadPhotos(@Part MultipartBody.Part[] photos);

    @Multipart
    @POST("/api/upload")
    Call<ResponseBody> uploadPhotos2(@Part MultipartBody.Part[] photos);

    @POST("https://maps.googleapis.com/maps/api/geocode/json")
    Call<JsonObject> reverseGeocode(@Query("latlng") String langlat,
                                    @Query("key") String key);

}
