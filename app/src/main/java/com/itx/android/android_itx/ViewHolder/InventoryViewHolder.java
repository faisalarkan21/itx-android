package com.itx.android.android_itx.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;

import com.itx.android.android_itx.R;

/**
 * Created by faisal on 2/22/18.
 */

public class InventoryViewHolder extends RecyclerView.ViewHolder  {

    public TextView inventoryName, inventoryStock, inventorySpace, inventoryPrice, inventoryFacilities;
    public ImageView inventoryImage, btnImgDelete;;

    public InventoryViewHolder(View itemView) {
        super(itemView);
        inventoryName = (TextView) itemView.findViewById(R.id.tv_inventory_name);
        inventoryFacilities = (TextView) itemView.findViewById(R.id.tv_inventory_facilities);
        inventoryStock = (TextView) itemView.findViewById(R.id.tv_inventory_stock);
        inventorySpace = (TextView) itemView.findViewById(R.id.tv_inventory_space);
        inventoryPrice = (TextView) itemView.findViewById(R.id.tv_inventory_price);
        inventoryImage = (ImageView) itemView.findViewById(R.id.iv_asset_inventory);
        btnImgDelete = (ImageView) itemView.findViewById(R.id.iv_delete);

    }

}
