package com.itx.android.android_itx.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.itx.android.android_itx.R;

/**
 * Created by aladhims on 21/02/18.
 */

public class AssetsViewHolder extends RecyclerView.ViewHolder {
    public TextView mTvAssetName, mTvAssetAddress, mTvAssetPhone;
    public ImageView mIvGambarAsset;

    public AssetsViewHolder(View itemView) {
        super(itemView);

        mTvAssetName = (TextView) itemView.findViewById(R.id.tv_asset_name);
        mTvAssetAddress = (TextView) itemView.findViewById(R.id.tv_assets_address);
        mTvAssetPhone = (TextView) itemView.findViewById(R.id.tv_asset_no_hp);


    }
}
