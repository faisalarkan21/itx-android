package com.itx.android.android_itx.Utils;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by faisal on 2/27/18.
 */

public class GetDataJson {
    private Context mContext;



    public GetDataJson(Context mContext){
        this.mContext = mContext;

    }

    public String loadJSONFromAssetProvince() {
        String json = null;
        try {
            InputStream is = mContext.getAssets().open("states_provinces.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public String loadJSONFromAssetCity() {
        String json = null;
        try {
            InputStream is = mContext.getAssets().open("places.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

}
