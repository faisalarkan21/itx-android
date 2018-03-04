package com.itx.android.android_itx;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.DividerItemDecoration;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itx.android.android_itx.Adapter.InventoryAdapter;
import com.itx.android.android_itx.Entity.Inventory;
import com.itx.android.android_itx.Service.AssetService;
import com.itx.android.android_itx.Service.InventoryService;
import com.itx.android.android_itx.Utils.ApiUtils;
import com.itx.android.android_itx.Utils.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListInventory extends AppCompatActivity implements Callback<JsonObject> {

    private List<Inventory> mListInventory = new ArrayList<>();
    private List<String> mAssetImages = new ArrayList<>();

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.tv_asset_name)
    TextView mAssetName;

    @BindView(R.id.pb_list_invent)
    ProgressBar mPbListInvent;

    @BindView(R.id.tv_asset_category)
    TextView mAssetCategory;

    @BindView(R.id.tv_asset_address)
    TextView mAssetAddress;

    @BindView(R.id.tv_asset_no_telp)
    TextView mAssetPhone;

    @BindView(R.id.rating_bar_assets)
    RatingBar mAssetRating;

    @BindView(R.id.btn_add_inventory)
    FloatingActionButton mBtnAddInvent;

    @BindView(R.id.iv_asset_images)
    ImageView mImagesAssets;

    @BindView(R.id.vp_asset_details)
    ViewPager mVpAssetImages;

    @BindView(R.id.layoutDots_asset_details)
    LinearLayout mLlDotsAssetImages;

    @BindView(R.id.pb_asset_details)
    ProgressBar mPbAssetImages;

    InventoryService mInventoryAPIService;
    SessionManager session;
    private TextView[] dots;
    private int currentPage = 0;

    private String idAsset;


    private InventoryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_inventory);


        ButterKnife.bind(this);

        mAdapter = new InventoryAdapter(mListInventory, this);

        showLoading();

        idAsset = getIntent().getStringExtra("idAsset");

        session = new SessionManager(this);

        mInventoryAPIService = ApiUtils.getListInventoryService(session.getToken());
        mRecyclerView.setHasFixedSize(true);

        AssetService assetService = ApiUtils.getListAssetsService(session.getToken());
        Call<JsonObject> imagesResponse = assetService.getAssetImages(idAsset);
        imagesResponse.enqueue(this);

        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mRecyclerView.setAdapter(mAdapter);

        mBtnAddInvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent createInventory = new Intent(ListInventory.this, CreateNewInventory.class);
                createInventory.putExtra("idAsset", idAsset);
                startActivity(createInventory);
            }
        });

        String idAsset = getIntent().getStringExtra("idAsset");
        String assetAdress = getIntent().getStringExtra("address");
        String assetName = getIntent().getStringExtra("assetName");
        String categoryName = getIntent().getStringExtra("categoryName");
        String phone = getIntent().getStringExtra("phone");
        String images = getIntent().getStringExtra("images");
        float rating = getIntent().getFloatExtra("rating", 0);

        mAssetName.setText(assetName);
        mAssetAddress.setText(assetAdress);
        mAssetCategory.setText(categoryName);
        mAssetPhone.setText(phone);
        mAssetRating.setRating(rating);

        if (images != null) {
            Glide.with(ListInventory.this)
                    .load(ApiUtils.BASE_URL_USERS_IMAGE + images)
                    .into(mImagesAssets);
        }

        prepareUserData();


    }

    private void showLoading() {
        mPbListInvent.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void hideLoading() {
        mPbListInvent.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[mAssetImages.size()];

        int colosActive = ContextCompat.getColor(this, R.color.dot_active);
        int colorInactive = ContextCompat.getColor(this, R.color.dot_inactive);

        mLlDotsAssetImages.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorInactive);
            mLlDotsAssetImages.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[currentPage].setTextColor(colosActive);
        }
    }

    private void prepareUserData() {

        showLoading();
        if (mListInventory.size() > 1){
            mListInventory.clear();
            mAdapter.notifyDataSetChanged();
        }



        Call<JsonObject> response = mInventoryAPIService.getUserInventories(idAsset);

        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> rawResponse) {
                hideLoading();
                if (rawResponse.isSuccessful()) {
                    try {

                        JsonElement json = rawResponse.body().get("data").getAsJsonObject().get("inventoryCategories").getAsJsonArray();
                        Log.d("lnes 102", Boolean.toString(json.isJsonArray()));

                        JsonArray jsonArray = json.getAsJsonArray();

                        if (jsonArray.size() == 0) {
                            hideLoading();
                            Toast.makeText(ListInventory.this, "Tidak ada data.",
                                    Toast.LENGTH_LONG).show();
                        }

                        for (int i = 0; i < jsonArray.size(); i++) {
                            JsonObject Data = jsonArray.get(i).getAsJsonObject();

                            Inventory invent = new Inventory();
                            invent.setIdAsset(Data.get("_id").getAsString());
                            invent.setName(Data.get("name").getAsString());

                            if (Data.get("images").getAsJsonArray().size() != 0) {
                                JsonArray imagesLoop = Data.get("images").getAsJsonArray();
                                JsonObject DataImageInvent = imagesLoop.get(0).getAsJsonObject();
                                invent.setImage(DataImageInvent.get("thumbnail").getAsString());

                            }

                            JsonArray facilitiesLoop = Data.get("facilities").getAsJsonArray();
                            String tempFacilities = "";

                            for (int a = 0; a < facilitiesLoop.size(); a++) {
                                JsonObject DataFacilities = facilitiesLoop.get(a).getAsJsonObject();
                                tempFacilities += DataFacilities.get("name").getAsString() + ", ";
                                invent.setFacilities(tempFacilities.substring(0, tempFacilities.length() - 2));
                            }

                            invent.setStock(Data.get("stock").getAsString());
                            invent.setSpace(Data.get("space").getAsString());
                            invent.setPrice(Data.get("price").getAsDouble());

                            mListInventory.add(invent);

                        }

                        mAdapter.notifyDataSetChanged();

                        Toast.makeText(ListInventory.this, "Terdapat : " + Integer.toString(jsonArray.size()) + " Inventory",
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
                hideLoading();
                Toast.makeText(ListInventory.this, throwable.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    ViewPager.OnPageChangeListener Listener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        prepareUserData();
    }

    @Override
    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
        mPbAssetImages.setVisibility(View.INVISIBLE);
        if (response.isSuccessful()) {
            JsonArray imagesResponse = response.body().getAsJsonArray("images");
            for (int i = 0; i < imagesResponse.size(); i++) {
                JsonObject image = imagesResponse.get(i).getAsJsonObject();
                mAssetImages.add(image.get("medium").getAsString());
            }
            if (mAssetImages.size() != 0) {
                addBottomDots(0);
                mVpAssetImages.addOnPageChangeListener(Listener);
                mVpAssetImages.setAdapter(new ImageSlider(mAssetImages, this));
                final Handler handler = new Handler();

                final Runnable update = new Runnable() {
                    public void run() {

                        if (currentPage == mAssetImages.size()) {
                            currentPage = 0;
                        }
                        mVpAssetImages.setCurrentItem(currentPage++, true);
                    }
                };


                new Timer().schedule(new TimerTask() {

                    @Override
                    public void run() {
                        handler.post(update);
                    }
                }, 500, 3000);
            }
        } else {
            mAssetImages.add("http://baak.gunadarma.ac.id/public/images/ugcoba24.jpg");
            mAssetImages.add("http://mherman.org/assets/img/blog/on-demand-environments/on-demand-envs.png");
            if (mAssetImages.size() != 0) {
                addBottomDots(0);
                mVpAssetImages.addOnPageChangeListener(Listener);
                mVpAssetImages.setAdapter(new ImageSlider(mAssetImages, this));
                final Handler handler = new Handler();

                final Runnable update = new Runnable() {
                    public void run() {

                        if (currentPage == mAssetImages.size()) {
                            currentPage = 0;
                        }
                        mVpAssetImages.setCurrentItem(currentPage++, true);
                    }
                };


                new Timer().schedule(new TimerTask() {

                    @Override
                    public void run() {
                        handler.post(update);
                    }
                }, 500, 3000);
            }
        }
    }

    @Override
    public void onFailure(Call<JsonObject> call, Throwable t) {

    }


    class ImageSlider extends PagerAdapter {

        List<String> images;
        Context mContext;

        public ImageSlider(List<String> img, Context context) {
            images = img;
            mContext = context;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View rootView = LayoutInflater.from(mContext).inflate(R.layout.layout_images_slider, null);
            ImageView mImageView = (ImageView) rootView.findViewById(R.id.iv_bg_banner);
            Glide.with(mContext)
                    .load(ApiUtils.BASE_URL_USERS_IMAGE + images.get(position))
                    .into(mImageView);
            container.addView(rootView);
            return rootView;
        }


        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
