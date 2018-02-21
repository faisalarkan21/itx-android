package com.itx.android.android_itx.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itx.android.android_itx.R;
import com.itx.android.android_itx.ViewHolder.AssetsViewHolder;

/**
 * Created by aladhims on 21/02/18.
 */

public class AssetsAdapter extends RecyclerView.Adapter<AssetsViewHolder> {

    private Context mContext;

    public AssetsAdapter(Context context){
        this.mContext = context;

    }
    @Override
    public AssetsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_users, parent, false);


        return new AssetsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AssetsViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
