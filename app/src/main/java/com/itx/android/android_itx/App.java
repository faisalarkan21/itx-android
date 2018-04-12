package com.itx.android.android_itx;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by legion on 12/04/2018.
 */

public class App extends Application {

    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        CalligraphyConfig.initDefault(
                new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/segoeui.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
    }

    public static App getInstance() {
        return instance;
    }
}
