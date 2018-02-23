package com.itx.android.android_itx.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.itx.android.android_itx.Entity.Inventory;
import com.itx.android.android_itx.R;
import com.itx.android.android_itx.Utils.ApiUtils;
import com.itx.android.android_itx.ViewHolder.InventoryViewHolder;
import com.itx.android.android_itx.Utils.RupiahCurrency;

import java.util.List;

/**
 * Created by aladhims on 21/02/18.
 */


public class InventoryAdapter extends RecyclerView.Adapter<InventoryViewHolder> {

    private List<Inventory> inventoryList;
    private Context mContext;



    public InventoryAdapter(List<Inventory> inventoryList, Context activity) {
        this.mContext = activity;
        this.inventoryList = inventoryList;
    }

    @Override
    public InventoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_inventory, parent, false);

        return new InventoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(InventoryViewHolder holder, int position) {
        final Inventory invent = inventoryList.get(position);
        holder.inventoryName.setText(invent.getName());

        Glide.with(mContext)
                .load(ApiUtils.BASE_URL_USERS_IMAGE + invent.getImage())
                .into(holder.inventoryImage);

        holder.inventoryFacilities.setText("Facilities : " + invent.getFacilities());
        holder.inventoryStock.setText("Stock : " + invent.getStock());
        holder.inventorySpace.setText("Space : " + invent.getSpace());
        holder.inventoryPrice.setText("@ " + new RupiahCurrency().toRupiahFormat(invent.getPrice()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });

    }

    @Override
    public int getItemCount() {
        return inventoryList.size();
    }
}
