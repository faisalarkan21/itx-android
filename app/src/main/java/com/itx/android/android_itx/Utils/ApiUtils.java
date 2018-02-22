package com.itx.android.android_itx.Utils;

import com.itx.android.android_itx.ListUsers;
import com.itx.android.android_itx.Service.APIService;
import com.itx.android.android_itx.Service.AuthService;
import com.itx.android.android_itx.Service.ListUsersService;

/**
 * Created by faisal on 2/21/18.
 */

public class ApiUtils {

    private ApiUtils() {
    }

    public static final String BASE_URL = "http://139.99.105.54:3001/";

    public static AuthService getAuthAPIService() {

        return RetrofitClient.getClientLogin(BASE_URL).create(AuthService.class);
    }


    public static ListUsersService getListUsersService(String token) {

        return RetrofitClient.getClientPrivate(BASE_URL, token).create(ListUsersService.class);
    }


}