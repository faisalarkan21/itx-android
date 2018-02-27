package com.itx.android.android_itx;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.itx.android.android_itx.Adapter.AssetsAdapter;
import com.itx.android.android_itx.Entity.Address;
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
    FloatingActionButton mBtnAddAsset;

    @BindView(R.id.userName)
    TextView mUserName;

    @BindView(R.id.tv_user_role)
    TextView mUserRole;

    @BindView(R.id.tv_user_address)
    TextView mUserAddress;

    @BindView(R.id.tv_user_no_telp)
    TextView mUserTelp;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.iv_user_images)
    ImageView mIvUserImages;

    @BindView(R.id.pb_list_asset)
    ProgressBar mPbListAsset;

    private AssetsAdapter mAdapter;
    String idUser;
    ListAssetService mAssetAPIService;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_assets);




        ButterKnife.bind(this);

        session = new SessionManager(this);
        Log.d("TOKEN", session.getToken());

        mAdapter = new AssetsAdapter(mListAsset, this);

        mRecyclerView.setHasFixedSize(true);

        showLoading();

        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mRecyclerView.setAdapter(mAdapter);
        prepareUserData();
        mBtnAddAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addAssetIntent = new Intent(ListAssets.this, CreateNewAsset.class);
                addAssetIntent.putExtra("idUser", idUser);
                startActivity(addAssetIntent);
            }
        });
    }

    private void showLoading() {
        mPbListAsset.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void hideLoading() {
        mPbListAsset.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }


    private void prepareUserData() {



        idUser = getIntent().getStringExtra("id");
        String userAdress = getIntent().getStringExtra("address");
        String userName = getIntent().getStringExtra("name");
        String phone = getIntent().getStringExtra("phone");
        String images = getIntent().getStringExtra("photo");
        String role = getIntent().getStringExtra("role");





        mUserName.setText(userName);
        mUserRole.setText(role);
        mUserAddress.setText(userAdress);
        mUserTelp.setText(phone);
        if (images != null){
            Glide.with(ListAssets.this)
                    .load(ApiUtils.BASE_URL_USERS_IMAGE + images)
                    .into(mIvUserImages);
        }

        mAssetAPIService = ApiUtils.getListAssetsService(session.getToken());/**/

        Call<JsonObject> response = mAssetAPIService.getUserAssets(idUser);

        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> rawResponse) {
                if (rawResponse.isSuccessful()) {
                    try {
                        JsonElement json = rawResponse.body().get("data").getAsJsonObject().get("assets");
                        final JsonArray jsonArray = json.getAsJsonArray();

                        if (jsonArray.size() == 0) {
                            hideLoading();
                            Toast.makeText(ListAssets.this, "Tidak ada data.",
                                    Toast.LENGTH_LONG).show();
                        }

                        for (int i = 0; i < jsonArray.size(); i++) {
                            JsonObject Data = jsonArray.get(i).getAsJsonObject();

                            Assets assets = new Assets();
                            assets.setId(Data.get("_id").getAsString());
                            assets.setAddress(Data.get("address").getAsJsonObject().get("address").getAsString());
                            assets.setName(Data.get("name").getAsString());

                            if (Data.get("images").getAsJsonArray().size() != 0) {
                                JsonArray imagesLoop = Data.get("images").getAsJsonArray();
                                JsonObject DataImageAseets = imagesLoop.get(0).getAsJsonObject();
                                assets.setImages(DataImageAseets.get("thumbnail").getAsString());
                            }


                            assets.setPhone(Data.get("phone").getAsString());
                            assets.setAssetCategory(Data.get("assetCategory").getAsJsonObject().get("name").getAsString());
                            if (Data.get("rating") != null) {
                                assets.setRating(Data.get("rating").getAsFloat());
                            }

                            mListAsset.add(assets);

                        }

                        mAdapter.notifyDataSetChanged();
                        hideLoading();
                        Toast.makeText(ListAssets.this, "Terdapat : " + Integer.toString(jsonArray.size()) + " Assets",
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

    }

}
