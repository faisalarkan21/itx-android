package com.itx.android.android_itx;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.itx.android.android_itx.Entity.InventoryCategory;
import com.itx.android.android_itx.Utils.ApiUtils;
import com.itx.android.android_itx.Utils.RupiahCurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by legion on 15/04/2018.
 */

public class InventoryDetail extends AppCompatActivity {

    @BindView(R.id.vp_inventory_details)
    ViewPager mVpInventoryImages;

    @BindView(R.id.pb_inventory_details)
    ProgressBar mPbInventoryImages;

    @BindView(R.id.layoutDots_inventory_details)
    LinearLayout mLlDotsInventoryImages;

    @BindView(R.id.tv_inventory_description)
    TextView mInventoryDescription;

    @BindView(R.id.tv_inventory_facilities)
    TextView mInventoryFacilities;

    @BindView(R.id.tv_inventory_stock)
    TextView mInventoryStock;

    @BindView(R.id.tv_inventory_space)
    TextView mInventorySpace;

    @BindView(R.id.tv_inventory_price)
    TextView mInventoryPrice;

    private TextView[] dots;
    private int currentPage = 0;
    private InventoryCategory mInventory;
    private List<String> mInventoryImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_inventory);
        ButterKnife.bind(this);

        Gson gson = new Gson();
        mInventory = gson.fromJson(getIntent().getStringExtra("DATA"), InventoryCategory.class);
        getSupportActionBar().setTitle(mInventory.getName());

        prepareImage();
        mInventoryDescription.setText(mInventory.getDescription());
        for (int i = 0; i < mInventory.getFacilities().size(); i++) {
            if(i == 0) {
                mInventoryFacilities.append("Fasilitas\n+ " + mInventory.getFacilities().get(i).getName());
            }else{
                mInventoryFacilities.append("\n+ " + mInventory.getFacilities().get(i).getName());
            }
        }
        mInventoryStock.setText("Persediaan " + mInventory.getStock());
        mInventorySpace.setText("Kapasitas " + mInventory.getSpace() + " orang");
        mInventoryPrice.setText(RupiahCurrency.toRupiahFormat(mInventory.getPrice()));
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

    public void prepareImage(){
        mPbInventoryImages.setVisibility(View.INVISIBLE);
        for (int i = 0; i < mInventory.getImages().size(); i++) {
            mInventoryImages.add(mInventory.getImages().get(i).getmMedium());
        }
        if (mInventoryImages.size() != 0) {
            addBottomDots(0);
            mVpInventoryImages.addOnPageChangeListener(Listener);
            mVpInventoryImages.setAdapter(new InventoryDetail.ImageSlider(mInventoryImages, this));
            final Handler handler = new Handler();

            final Runnable update = new Runnable() {
                public void run() {

                    if (currentPage == mInventoryImages.size()) {
                        currentPage = 0;
                    }
                    mVpInventoryImages.setCurrentItem(currentPage++, true);
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

    private void addBottomDots(int currentPage) {
        dots = new TextView[mInventoryImages.size()];

        int colosActive = ContextCompat.getColor(this, R.color.dot_active);
        int colorInactive = ContextCompat.getColor(this, R.color.dot_inactive);

        mLlDotsInventoryImages.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorInactive);
            mLlDotsInventoryImages.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[currentPage].setTextColor(colosActive);
        }
    }
}
