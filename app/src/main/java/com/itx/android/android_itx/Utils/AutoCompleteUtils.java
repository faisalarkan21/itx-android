package com.itx.android.android_itx.Utils;

import android.content.Context;
import android.net.Uri;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.itx.android.android_itx.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by faisal on 2/27/18.
 */

public class AutoCompleteUtils {
    private Context mContext;


    public AutoCompleteUtils(Context mContext) {
        this.mContext = mContext;

    }

    public String loadJSONFromAssetProvince() {
        String json = null;
        try {
            InputStream is = mContext.getResources().openRawResource(R.raw.provinces);
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
            InputStream is = mContext.getResources().openRawResource(R.raw.cities);
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

    public ArrayList<String> getArrayProvicesJson() {

        ArrayList<String> arrListProvince = new ArrayList<String>();
        String dataJsonProvince = loadJSONFromAssetProvince();
        JsonArray gsonProvince = new JsonParser().parse(dataJsonProvince).getAsJsonArray();

        for (int i = 0; i < gsonProvince.size(); i++) {
            String nameProvice = gsonProvince.get(i).getAsJsonObject().get("province_name").getAsString();
            arrListProvince.add(nameProvice);
        }
        return arrListProvince;
    }

    public ArrayList<String> getArrayCityJson(String byProvinceName) {
        ArrayList<String> arrListCity = new ArrayList<String>();

        String byProvinceId = null;

        if (byProvinceName != null) {

            String dataJsonCity = loadJSONFromAssetCity();
            JsonArray gsonCity = new JsonParser().parse(dataJsonCity).getAsJsonArray();

            String dataJsonProvince = loadJSONFromAssetProvince();
            JsonArray gsonProvince = new JsonParser().parse(dataJsonProvince).getAsJsonArray();

            for (int i = 0; i < gsonProvince.size(); i++) {

                if (gsonProvince.get(i).getAsJsonObject().get("province_name").getAsString().equals(byProvinceName)) {

                    byProvinceId = gsonProvince.get(i).getAsJsonObject().get("province_id").getAsString();
                }
            }


            for (int i = 0; i < gsonCity.size(); i++) {

                if (gsonCity.get(i).getAsJsonObject().get("province_id").getAsString().equals(byProvinceId)) {
                    String nameCity = gsonCity.get(i).getAsJsonObject().get("city_name").getAsString();
                    arrListCity.add(nameCity);
                }
            }


        }
        return arrListCity;
    }

}
