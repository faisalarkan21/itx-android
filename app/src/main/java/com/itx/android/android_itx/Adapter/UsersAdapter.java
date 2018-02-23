package com.itx.android.android_itx.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.itx.android.android_itx.DashboardUtama;
import com.itx.android.android_itx.ListAssets;
import com.itx.android.android_itx.ListUsers;
import com.itx.android.android_itx.Login;
import com.itx.android.android_itx.R;
import com.itx.android.android_itx.Entity.Users;
import com.itx.android.android_itx.Utils.ApiUtils;
import com.itx.android.android_itx.Utils.RecyclerTouchListener;
import com.itx.android.android_itx.ViewHolder.UsersViewHolder;
import android.content.Intent;

import java.util.List;


public class UsersAdapter extends RecyclerView.Adapter<UsersViewHolder> {

    private List<Users> userList;
    private Context mContext;

    public UsersAdapter(List<Users> userList, Context activity) {
        this.mContext = activity;
        this.userList = userList;
    }

    @Override
    public UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_users, parent, false);

        return new UsersViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UsersViewHolder holder, final int position) {
        final Users user = userList.get(position);

        holder.userName.setText(user.getFullName());
        holder.assets.setText("Assets : " + user.getAssets());

        Glide.with(mContext)
                .load(ApiUtils.BASE_URL_USERS_IMAGE + user.getPhoto())
                .into(holder.imageUsers);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("inidia", Integer.toString(position));
                Log.d("ini stringnya",user.getIdUser());

                Intent listAsset = new Intent(mContext,ListAssets.class);
                listAsset.putExtra("id", user.getIdUser());

                mContext.startActivity(listAsset);


            }
        });



    }







    @Override
    public int getItemCount() {
        return userList.size();
    }


}
