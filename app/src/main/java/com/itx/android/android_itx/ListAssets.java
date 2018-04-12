package com.itx.android.android_itx;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itx.android.android_itx.Adapter.AssetsAdapter;
import com.itx.android.android_itx.Entity.Asset;
import com.itx.android.android_itx.Service.AssetService;
import com.itx.android.android_itx.Utils.ApiUtils;
import com.itx.android.android_itx.Utils.GridSpacingItemDecoration;
import com.itx.android.android_itx.Utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ListAssets extends AppCompatActivity {

    private List<Asset> mListAsset = new ArrayList<>();
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
    CircleImageView mIvUserImages;

    @BindView(R.id.pb_list_asset)
    ProgressBar mPbListAsset;

    private AssetsAdapter mAdapter;
    String idUser,userAdress,userName,phone,images,role ;
    AssetService mAssetAPIService;
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

        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2);

        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2
                , 10, true));

        mRecyclerView.setAdapter(mAdapter);
        idUser = getIntent().getStringExtra("id");
        userAdress = getIntent().getStringExtra("address");
        userName = getIntent().getStringExtra("name");
        phone = getIntent().getStringExtra("phone");
        images = getIntent().getStringExtra("photo");
        role = getIntent().getStringExtra("role");

        getSupportActionBar().setTitle("Pengguna");
        mUserName.setText(userName);
        mUserRole.setText(role);
        mUserAddress.setText(userAdress);
        mUserTelp.setText(phone);

        if (images != null){
            Glide.with(ListAssets.this)
                    .load(ApiUtils.BASE_URL_USERS_IMAGE + images)
                    .into(mIvUserImages);
        }

        mBtnAddAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addAssetIntent = new Intent(ListAssets.this, CreateNewAsset.class);
                addAssetIntent.putExtra("idUser", idUser);
                addAssetIntent.putExtra("name", userName);
                addAssetIntent.putExtra("address", userAdress);
                addAssetIntent.putExtra("phone", phone);
                addAssetIntent.putExtra("photo", images);
                addAssetIntent.putExtra("role", role);
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

        showLoading();
        if(mListAsset.size() >= 1){
            mListAsset.clear();
            mAdapter.notifyDataSetChanged();
        }

        mAssetAPIService = ApiUtils.getListAssetsService(session.getToken());

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

                            Asset asset = new Asset();
                            asset.setId(Data.get("_id").getAsString());
                            asset.setAddress(Data.get("address").getAsJsonObject().get("address").getAsString());
                            asset.setName(Data.get("name").getAsString());

                            if (Data.get("images").getAsJsonArray().size() != 0) {
                                JsonArray imagesLoop = Data.get("images").getAsJsonArray();
                                JsonObject DataImageAseets = imagesLoop.get(0).getAsJsonObject();
                                asset.setImages(DataImageAseets.get("thumbnail").getAsString());
                            }

                            asset.setPhone(Data.get("phone").getAsString());
                            asset.setAssetCategory(Data.get("assetCategory").getAsJsonObject().get("name").getAsString());
                            if (Data.get("rating") != null) {
                                asset.setRating(Data.get("rating").getAsFloat());
                            }

                            mListAsset.add(asset);

                        }

                        mAdapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(ListAssets.this, "Gagal Mengambil Data",
                            Toast.LENGTH_LONG).show();
                }
                hideLoading();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                hideLoading();
                Toast.makeText(ListAssets.this, throwable.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareUserData();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
