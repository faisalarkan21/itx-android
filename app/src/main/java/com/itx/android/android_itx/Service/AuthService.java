package com.itx.android.android_itx.Service;

import com.itx.android.android_itx.Entity.Users;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by faisal on 2/21/18.
 */

public interface AuthService {

    @POST("/api/{login}")
    Call<ResponseBody> loginPost(@Path("login") String postfix, @Body RequestBody params);

}
