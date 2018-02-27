package com.itx.android.android_itx.Utils;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by faisal on 2/27/18.
 */

public class AutoCompleteUtils {
    private Context mContext;



    public AutoCompleteUtils(Context mContext){
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

    public ArrayList<String> getArrayProvicesJson(){

        ArrayList<String> arrListProvince = new ArrayList<String>();
        String dataJsonProvince = loadJSONFromAssetProvince();
        JsonArray gsonProvince = new JsonParser().parse(dataJsonProvince).getAsJsonObject().get("features").getAsJsonArray();

        for (int i = 0; i < gsonProvince.size(); i++) {
            String nameProvice = gsonProvince.get(i).getAsJsonObject().get("properties").getAsJsonObject().get("name").getAsString();
            arrListProvince.add(nameProvice);
        }
        return arrListProvince;
    }

    public ArrayList<String> getArrayCityJson(){
        ArrayList<String> arrListCity = new ArrayList<String>();

        String dataJsonCity = loadJSONFromAssetCity();
        JsonArray gsonCity = new JsonParser().parse(dataJsonCity).getAsJsonObject().get("features").getAsJsonArray();

        for (int i = 0; i < gsonCity.size(); i++) {
            String nameCity = gsonCity.get(i).getAsJsonObject().get("properties").getAsJsonObject().get("NAME").getAsString();
            arrListCity.add(nameCity);
        }


        return arrListCity;
    }

}
