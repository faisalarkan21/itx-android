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
    public TextView mTvNamaAsset, mTvDeskripsiAsset;
    public ImageView mIvGambarAsset;

    public AssetsViewHolder(View itemView) {
        super(itemView);

        mTvNamaAsset = (TextView) itemView.findViewById(R.id.tv_asset_name);
        mTvDeskripsiAsset = (TextView) itemView.findViewById(R.id.tv_assets_deskripsi);
        mIvGambarAsset = (ImageView) itemView.findViewById(R.id.iv_asset);
    }
}
