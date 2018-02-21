package com.itx.android.android_itx.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by faisal on 2/21/18.
 */

public class SessionManager {

    private SharedPreferences prefs;

    String nm_pembeli, email_pembeli, id_pembeli;

    public SessionManager(Context cntx) {
        // TODO Auto-generated constructor stub
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void setSession(String email, String id_user) {
        prefs.edit().putString("email", email).apply();
        prefs.edit().putString("id_user", id_user).apply();

    }

    public String getEmail_pembeli() {
        String email_pembeli = prefs.getString("email","");
        return email_pembeli;
    }

    public String getId_pembeli() {
        String id_pembeli = prefs.getString("id_user","");
        return id_pembeli;
    }

}



