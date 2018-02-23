package com.itx.android.android_itx.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.itx.android.android_itx.Entity.Assets;
import com.itx.android.android_itx.ListInventory;
import com.itx.android.android_itx.R;
import com.itx.android.android_itx.Utils.ApiUtils;
import com.itx.android.android_itx.ViewHolder.AssetsViewHolder;

import java.util.List;

/**
 * Created by aladhims on 21/02/18.
 */

public class AssetsAdapter extends RecyclerView.Adapter<AssetsViewHolder> {

    private Context mContext;
    private List<Assets> mListAssets;

    public AssetsAdapter( List<Assets> assets,Context context){
        this.mContext = context;
        this.mListAssets = assets;

    }
    @Override
    public AssetsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_assets, parent, false);


        return new AssetsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AssetsViewHolder holder, int position) {
        final Assets currentAsset = mListAssets.get(position);
        holder.mTvAssetName.setText(currentAsset.getName());
        holder.mTvAssetCategory.setText(currentAsset.getAssetCategory());
        holder.mRatingBar.setRating(currentAsset.getRating());

        Glide.with(mContext)
                .load(ApiUtils.BASE_URL_USERS_IMAGE + currentAsset.getImages())
                .into(holder.mIvGambarAsset);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent listInventory = new Intent(mContext, ListInventory.class);
                listInventory.putExtra("idAsset", currentAsset.getId());
                listInventory.putExtra("address", currentAsset.getAddress());
                listInventory.putExtra("images", currentAsset.getImages());
                listInventory.putExtra("assetName", currentAsset.getName());
                listInventory.putExtra("categoryName", currentAsset.getAssetCategory());
                listInventory.putExtra("phone", currentAsset.getPhone());
                listInventory.putExtra("rating", currentAsset.getRating());
                mContext.startActivity(listInventory);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mListAssets.size();
    }
}
