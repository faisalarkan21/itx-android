package com.itx.android.android_itx;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.itx.android.android_itx.Adapter.AssetsAdapter;
import com.itx.android.android_itx.Entity.Assets;
import com.itx.android.android_itx.Entity.Users;
import com.itx.android.android_itx.Service.ListAssetService;
import com.itx.android.android_itx.Utils.ApiUtils;
import com.itx.android.android_itx.Utils.SessionManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class ListAssets extends AppCompatActivity {

    private List<Assets> mListAsset = new ArrayList<>();
    @BindView(R.id.btn_add_asset)
    Button mBtnAddAsset;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private AssetsAdapter mAdapter;
    ListAssetService mAssetAPIService;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_assets);

        ButterKnife.bind(this);

        session = new SessionManager(this);

        mAdapter = new AssetsAdapter(mListAsset, this);

        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mRecyclerView.setAdapter(mAdapter);

        mBtnAddAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ListAssets.this, CreateNewAsset.class));
            }
        });

        prepareUserData();

    }


    private void prepareUserData() {


        String idUser = getIntent().getStringExtra("id");


        mAssetAPIService = ApiUtils.getListAssetsService(session.getToken());

        Call<JsonObject> response = mAssetAPIService.getUserAssets(idUser);

        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> rawResponse) {
                if (rawResponse.isSuccessful()) {
                    try {

                        JsonElement json = rawResponse.body().get("data");
                        Log.d("lnes 102", Boolean.toString(json.isJsonArray()));

                        JsonArray jsonArray = json.getAsJsonArray();

                        for (int i=0; i < jsonArray.size() ; i++ ){
                            JsonObject Data = jsonArray.get(i).getAsJsonObject();

                            Assets assets = new Assets();
                            assets.setId(Data.get("_id").getAsString());
                            assets.setName(Data.get("name").getAsString());
                            assets.setPhone(Data.get("phone").getAsString());
                            assets.setAssetCategory(Data.get("assetCategory").getAsJsonObject().get("name").getAsString());
                            assets.setRating(Data.get("rating").getAsFloat());
                            mListAsset.add(assets);
                            mAdapter.notifyDataSetChanged();
                        }

                        Toast.makeText(ListAssets.this, "Terdapat : " + Integer.toString(jsonArray.size()) + " data",
                                Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(ListAssets.this, "Gagal",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {

                Toast.makeText(ListAssets.this, throwable.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });


//        Assets assets = new Assets("Rumah Zakat", "Jalan Haji Mawi", 4.5f);
//        mListAsset.add(assets);

    }


}
