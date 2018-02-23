package com.itx.android.android_itx;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.DividerItemDecoration;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itx.android.android_itx.Adapter.AssetsAdapter;
import com.itx.android.android_itx.Adapter.InventoryAdapter;
import com.itx.android.android_itx.Entity.Inventory;
import com.itx.android.android_itx.Service.ListAssetService;
import com.itx.android.android_itx.Service.ListInventoryService;
import com.itx.android.android_itx.Utils.ApiUtils;
import com.itx.android.android_itx.Utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;

public class ListInventory extends AppCompatActivity {

    private List<Inventory> mListInventory = new ArrayList<>();

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.tv_asset_name)
    TextView mAssetName;

    @BindView(R.id.tv_asset_category)
    TextView mAssetCategory;

    @BindView(R.id.tv_asset_address)
    TextView mAssetAddress;

    @BindView(R.id.tv_asset_no_telp)
    TextView mAssetPhone;

    @BindView(R.id.rating_bar_assets)
    RatingBar mAssetRating;

    @BindView(R.id.btn_add_inventory)
    Button mBtnAddInvent;

    ListInventoryService mInventoryAPIService;
    SessionManager session;


    private InventoryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_inventory);

        ButterKnife.bind(this);

        mAdapter = new InventoryAdapter(mListInventory, this);

        session = new SessionManager(this);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mRecyclerView.setAdapter(mAdapter);

        mBtnAddInvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ListInventory.this, CreateNewInventory.class));
            }
        });

        prepareUserData();


    }

    private void prepareUserData() {

        String idAsset = getIntent().getStringExtra("idAsset");
        String assetAdress = getIntent().getStringExtra("address");
        String assetName = getIntent().getStringExtra("assetName");
        String categoryName = getIntent().getStringExtra("categoryName");
        String phone = getIntent().getStringExtra("phone");
        float rating = getIntent().getFloatExtra("rating", 0);

        mAssetName.setText(assetName);
        mAssetAddress.setText(assetAdress);
        mAssetCategory.setText(categoryName);
        mAssetPhone.setText(phone);
        mAssetRating.setRating(rating);
        Log.d("ini line 61", assetName);

        mInventoryAPIService = ApiUtils.getListInventoryService(session.getToken());

        Call<JsonObject> response = mInventoryAPIService.getUserInventory(idAsset);

        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> rawResponse) {
                if (rawResponse.isSuccessful()) {
                    try {

                        JsonElement json = rawResponse.body().get("data").getAsJsonObject().get("inventoryCategories").getAsJsonArray();
                        Log.d("lnes 102", Boolean.toString(json.isJsonArray()));

                        JsonArray jsonArray = json.getAsJsonArray();

                        for (int i = 0; i < jsonArray.size(); i++) {
                            JsonObject Data = jsonArray.get(i).getAsJsonObject();


                            Inventory invent = new Inventory();
                            invent.setName(Data.get("name").getAsString());

                            JsonArray facilitiesLoop = Data.get("facilities").getAsJsonArray();
                            String tempFacilities = "";

                            for (int a = 0; a < facilitiesLoop.size(); a++) {
                                JsonObject DataFacilities = facilitiesLoop.get(a).getAsJsonObject();
                                tempFacilities += DataFacilities.get("name").getAsString() + ", ";
                                invent.setFacilities(tempFacilities.substring(0,tempFacilities.length() - 2));
                            }

                            invent.setStock(Data.get("stock").getAsString());
                            invent.setSpace(Data.get("space").getAsString());
                            invent.setPrice(Data.get("price").getAsDouble());

                            mListInventory.add(invent);
                            mAdapter.notifyDataSetChanged();
                        }

                        Toast.makeText(ListInventory.this, "Terdapat : " + Integer.toString(jsonArray.size()) + " data",
                                Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(ListInventory.this, "Gagal",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {

                Toast.makeText(ListInventory.this, throwable.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });

//
//        Inventory invent = new Inventory("Superior", "3", "2");
//        mListInventory.add(invent);


    }


}
