package com.itx.android.android_itx.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by faisal on 2/21/18.
 */

public class SessionManager {

    private SharedPreferences prefs;
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";


    public SessionManager(Context cntx) {
        // TODO Auto-generated constructor stub
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void setSession(String token) {
        prefs.edit().putString("token", token).apply();
    }

    public String getToken() {
        String token = prefs.getString("token","");
        return token;
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        prefs.edit().putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime).commit();
    }

    public void removeToken(){
        prefs.edit().remove("token").apply();
    }
}



