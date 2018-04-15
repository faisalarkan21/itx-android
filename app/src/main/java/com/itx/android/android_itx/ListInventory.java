package com.itx.android.android_itx;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.itx.android.android_itx.Adapter.InventoryAdapter;
import com.itx.android.android_itx.Entity.Asset;
import com.itx.android.android_itx.Entity.InventoryCategory;
import com.itx.android.android_itx.Entity.Response;
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
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ListInventory extends AppCompatActivity {

    private List<InventoryCategory> mListInventory = new ArrayList<>();
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

    @BindView(R.id.swipeRefreshLayout)
    PullRefreshLayout mPullRefreshLayout;


    InventoryService mInventoryAPIService;
    SessionManager session;
    private TextView[] dots;
    private int currentPage = 0;

    private InventoryAdapter mAdapter;
    private Asset mAsset;
    private String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_inventory);

        ButterKnife.bind(this);

        Gson gson = new Gson();
        data = getIntent().getStringExtra("DATA");
        mAsset = gson.fromJson(data, Asset.class);
        prepareImage();

        mAdapter = new InventoryAdapter(mListInventory, this, mAsset);

        session = new SessionManager(this);

        mInventoryAPIService = ApiUtils.getListInventoryService(session.getToken());
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mRecyclerView.setAdapter(mAdapter);

        mBtnAddInvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createInventory = new Intent(ListInventory.this, CreateNewInventory.class);
                createInventory.putExtra("DATA", data);
                startActivity(createInventory);
            }
        });

        getSupportActionBar().setTitle(mAsset.getName());
        mAssetName.setText(mAsset.getName());
        mAssetAddress.setText(mAsset.getAddress().getAddress());
        mAssetCategory.setText(mAsset.getAssetCategory().getName());
        mAssetPhone.setText(mAsset.getPhone());
        mAssetRating.setRating(mAsset.getRating().floatValue());

        mPullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                prepareData();
            }
        });
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

    private void prepareData() {
        mListInventory.clear();
        showLoading();

        Call<Response.GetInventory> response = mInventoryAPIService.getUserInventories(mAsset.getId());
        response.enqueue(new Callback<Response.GetInventory>() {
            @Override
            public void onResponse(Call<Response.GetInventory> call, retrofit2.Response<Response.GetInventory> response) {
                mPullRefreshLayout.setRefreshing(false);
                hideLoading();
                if(response.isSuccessful()){
                    if(response.body().getStatus().getCode() == 200){
                        if(response.body().getData().getTotal() == 0){
                            Toast.makeText(ListInventory.this, "Tidak ada data.",
                                    Toast.LENGTH_LONG).show();
                        }else{
                            mListInventory.addAll(response.body().getData().getInventoryCategory());
                            mAdapter.notifyDataSetChanged();
                        }
                    }else{
                        Toast.makeText(ListInventory.this, response.body().getStatus().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(ListInventory.this, "Gagal Mengambil Data",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Response.GetInventory> call, Throwable t) {
                mPullRefreshLayout.setRefreshing(false);
                hideLoading();
                Toast.makeText(ListInventory.this, t.getMessage(),
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
        prepareData();
    }

    public void prepareImage(){
        mPbAssetImages.setVisibility(View.INVISIBLE);
        for (int i = 0; i < mAsset.getImages().size(); i++) {
            mAssetImages.add(mAsset.getImages().get(i).getmMedium());
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

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
