package com.itx.android.android_itx.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.itx.android.android_itx.R;

/**
 * Created by aladhims on 21/02/18.
 */

public class UsersViewHolder extends RecyclerView.ViewHolder {
    public TextView userName, assets , iv_users_options;
    public ImageView imageUsers;
    public UsersViewHolder(View itemView) {
        super(itemView);
        userName = (TextView) itemView.findViewById(R.id.userName);
        assets = (TextView) itemView.findViewById(R.id.asset);
        iv_users_options = (TextView) itemView.findViewById(R.id.iv_users_options);
        imageUsers = (ImageView) itemView.findViewById(R.id.list_image_users);





    }
}
