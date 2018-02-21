package com.itx.android.android_itx.Utils;

import com.itx.android.android_itx.Service.APIService;

/**
 * Created by faisal on 2/21/18.
 */

public class ApiUtils {

    private ApiUtils() {}

    public static final String BASE_URL = "http://139.99.105.54:3001/api/";

    public static APIService getAPIService() {

        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }


}
