package com.itx.android.android_itx.Utils;

import android.content.Context;

import com.itx.android.android_itx.R;

/**
 * Created by faisal on 2/23/18.
 */

public class Helper {

    public static String getStaticMapWithMarker (Context context, Double lat, Double lon, int zoom){

        String url = "http://maps.googleapis.com/maps/api/staticmap?" + "center=" + lat
                + "," + lon + "&zoom=" + zoom
                + "&size=600x600&maptype=roadmap&markers=color:red%7Clabel:%7C"
                + lat + "," + lon + "&key="
                + context.getString(R.string.google_maps_key);

        return url;
    }

    public static String getStaticMapWithoutMarker (Context context, Double lat, Double lon, int zoom){

        String url = "http://maps.googleapis.com/maps/api/staticmap?" + "center=" + lat
                + "," + lon + "&zoom=" + zoom
                + "&size=600x600&maptype=roadmap&key="
                + context.getString(R.string.google_maps_key);

        return url;
    }
}
