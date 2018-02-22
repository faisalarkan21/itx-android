package com.itx.android.android_itx.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itx.android.android_itx.Entity.Assets;
import com.itx.android.android_itx.ListInventory;
import com.itx.android.android_itx.R;
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
        Assets currentAsset = mListAssets.get(position);
        holder.mTvAssetName.setText(currentAsset.getName());
        holder.mTvAssetAddress.setText(currentAsset.getAddress());
        holder.mTvAssetPhone.setText(currentAsset.getPhone());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent ieventreport = new Intent(mContext, ListInventory.class);
                mContext.startActivity(ieventreport);


            }
        });
    }

    @Override
    public int getItemCount() {
        return mListAssets.size();
    }
}
