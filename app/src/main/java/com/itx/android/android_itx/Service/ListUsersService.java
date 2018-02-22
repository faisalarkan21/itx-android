package com.itx.android.android_itx.Service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;

/**
 * Created by faisal on 2/22/18.
 */

public interface ListUsersService {

    @GET("/api/users")
    Call<ResponseBody> getAUsers();

}
