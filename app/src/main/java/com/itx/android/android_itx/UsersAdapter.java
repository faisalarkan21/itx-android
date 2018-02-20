package com.itx.android.android_itx;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itx.android.android_itx.Entity.Users;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {

    private List<Users> userList;

    public class UsersViewHolder extends RecyclerView.ViewHolder {
        public TextView userName, assets, genre;

        public UsersViewHolder(View view) {
            super(view);
            userName = (TextView) view.findViewById(R.id.userName);
            assets = (TextView) view.findViewById(R.id.assets);

        }
    }


    public UsersAdapter(List<Users> userList) {
        this.userList = userList;
    }

    @Override
    public UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_users, parent, false);

        return new UsersViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UsersViewHolder holder, int position) {
        Users user = userList.get(position);
        holder.userName.setText(user.getFirstName()  + " " + user.getLastName());
        holder.assets.setText(user.getAssets());

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
