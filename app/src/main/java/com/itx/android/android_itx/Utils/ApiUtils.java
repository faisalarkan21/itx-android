package com.itx.android.android_itx.Utils;

import com.itx.android.android_itx.Service.APIService;
import com.itx.android.android_itx.Service.AuthService;
import com.itx.android.android_itx.Service.AssetService;
import com.itx.android.android_itx.Service.InventoryService;
import com.itx.android.android_itx.Service.UsersService;

/**
 * Created by faisal on 2/21/18.
 */

public class ApiUtils {

    private ApiUtils() {
    }

    public static final String BASE_URL_USERS_IMAGE = "https://s3-ap-southeast-1.amazonaws.com/itx-storage/userdata/image/";
    public static final String BASE_URL = "http://itx-backend.apps.playcourt.id/";

    public static AuthService getAuthAPIService() {

        return RetrofitClient.getClientLogin(BASE_URL).create(AuthService.class);
    }

    public static UsersService getListUsersService(String token) {

        return RetrofitClient.getClientPrivate(BASE_URL, token).create(UsersService.class);
    }

    public static AssetService getListAssetsService(String token) {

        return RetrofitClient.getClientPrivate(BASE_URL, token).create(AssetService.class);
    }

    public static InventoryService getListInventoryService(String token) {

        return RetrofitClient.getClientPrivate(BASE_URL, token).create(InventoryService.class);
    }

    public static APIService getAPIService(String token){
        return  RetrofitClient.getClientPrivate(BASE_URL, token).create(APIService.class);
    }


}
