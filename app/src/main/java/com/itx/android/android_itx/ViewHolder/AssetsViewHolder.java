package com.itx.android.android_itx.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.itx.android.android_itx.R;

/**
 * Created by aladhims on 21/02/18.
 */

public class AssetsViewHolder extends RecyclerView.ViewHolder {
    public TextView mTvAssetName, mTvAssetCategory, iv_assets_options;
    public ImageView mIvGambarAsset;
    public RatingBar mRatingBar;

    public AssetsViewHolder(View itemView) {
        super(itemView);

        mTvAssetName = (TextView) itemView.findViewById(R.id.tv_asset_name);
        mTvAssetCategory = (TextView) itemView.findViewById(R.id.tv_asset_category);
        mRatingBar = (RatingBar) itemView.findViewById(R.id.rating_bar_assets);
        mIvGambarAsset = (ImageView) itemView.findViewById(R.id.iv_asset_images);
        iv_assets_options = (TextView) itemView.findViewById(R.id.iv_assets_options);


    }
}
