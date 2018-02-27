package com.itx.android.android_itx.Utils;

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by faisal on 2/27/18.
 */

public class SuggestionAdapter extends ArrayAdapter<String> {

    protected static final String TAG = "SuggestionAdapter";
    private List<String> suggestions;
    public SuggestionAdapter(Activity context, String nameFilter) {
        super(context, android.R.layout.simple_dropdown_item_1line);
        suggestions = new ArrayList<String>();
    }

    @Override
    public int getCount() {
        return suggestions.size();
    }

    @Override
    public String getItem(int index) {
        return suggestions.get(index);
    }

    @Override
    public Filter getFilter() {
        Filter myFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();

                if (constraint != null) {

                    JsonObject jsonResponse = null;

                    String dataJson = new AutoCompleteUtils(getContext()).loadJSONFromAssetProvince();


                    JsonArray gson = new JsonParser().parse(dataJson).getAsJsonObject().get("features").getAsJsonArray();




                    ArrayList<String> arrList = new ArrayList<String>();
                    for (int i = 0; i < gson.size(); i++) {


                        String nameProvice =gson.get(i).getAsJsonObject().get("properties").getAsJsonObject().get("name").getAsString();
                        arrList.add(nameProvice);
                    }

                    // Now assign the values and count to the FilterResults
                    // object
                    filterResults.values = arrList;
                    filterResults.count = arrList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence contraint,
                                          FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return myFilter;
    }

}
