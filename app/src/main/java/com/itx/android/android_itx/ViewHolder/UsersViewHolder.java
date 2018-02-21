package com.itx.android.android_itx.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.itx.android.android_itx.R;

/**
 * Created by aladhims on 21/02/18.
 */

public class UsersViewHolder extends RecyclerView.ViewHolder {
    public TextView userName, assets, genre;

    public UsersViewHolder(View view) {
        super(view);
        userName = (TextView) view.findViewById(R.id.userName);
        assets = (TextView) view.findViewById(R.id.assets);

    }
}
