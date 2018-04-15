package com.itx.android.android_itx.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.itx.android.android_itx.Entity.Response;
import com.itx.android.android_itx.ListAssets;
import com.itx.android.android_itx.R;
import com.itx.android.android_itx.Entity.User;
import com.itx.android.android_itx.Service.UsersService;
import com.itx.android.android_itx.UpdateUser;
import com.itx.android.android_itx.Utils.ApiUtils;
import com.itx.android.android_itx.Utils.SessionManager;
import com.itx.android.android_itx.ViewHolder.UsersViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;


public class UsersAdapter extends RecyclerView.Adapter<UsersViewHolder> {

    private List<User> userList;
    private Context mContext;
    private SessionManager session;
    UsersService mListUsersAPIService;
    ProgressDialog progressDialog;


    public UsersAdapter(List<User> userList, Context activity) {
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
    public void onBindViewHolder(final UsersViewHolder holder, final int position) {
        Gson gson = new Gson();
        final User user = userList.get(position);
        final String data = gson.toJson(user, User.class);

        holder.iv_users_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(mContext, holder.iv_users_options);
                popup.inflate(R.menu.list_menu);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_edit:
                                Intent updateUser = new Intent(mContext, UpdateUser.class);
                                updateUser.putExtra("DATA", data);
                                mContext.startActivity(updateUser);
                                break;
                            case R.id.menu_delete:
                                deleteUser(user);
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });

        holder.userName.setText(user.getFullName());
        holder.assets.setText(user.getAssets().size() + " ASSET");

        if (user.getPhoto() != null) {
            Picasso.with(mContext)
                    .load(ApiUtils.BASE_URL_USERS_IMAGE + user.getPhoto().getMedium())
                    .into(holder.imageUsers);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent listAsset = new Intent(mContext, ListAssets.class);
                listAsset.putExtra("DATA", data);
                mContext.startActivity(listAsset);
            }
        });


    }

    public void deleteUser(User user) {
        session = new SessionManager(mContext);
        mListUsersAPIService = ApiUtils.getListUsersService(session.getToken());

        final Call<Response.DeleteUser> response = mListUsersAPIService.deleteUser(user.get_id());

        android.app.AlertDialog.Builder alertBuilder = new android.app.AlertDialog.Builder(mContext);
        alertBuilder.setTitle("Konfirmasi");
        alertBuilder.setMessage("Anda yakin ingin menghapus ?");
        alertBuilder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                progressDialog = new ProgressDialog(mContext);
                progressDialog.setMessage("Menghapus Data");
                progressDialog.show();

                dialog.dismiss();
                response.enqueue(new Callback<Response.DeleteUser>() {
                    @Override
                    public void onResponse(Call<Response.DeleteUser> call, retrofit2.Response<Response.DeleteUser> response) {
                        progressDialog.dismiss();
                        if(response.isSuccessful()){
                            if(response.body().getStatus().getCode() == 200){
                                Toast.makeText(mContext, "Data telah dihapus",
                                        Toast.LENGTH_LONG).show();

                                ((Activity) mContext).finish();
                                mContext.startActivity(((Activity) mContext).getIntent());
                                progressDialog.dismiss();
                            }else{
                                Toast.makeText(mContext, response.body().getStatus().getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }else {
                            Toast.makeText(mContext, "Data gagal dihapus",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Response.DeleteUser> call, Throwable t) {
                        Toast.makeText(mContext, t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        alertBuilder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        android.app.AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();

    }


    @Override
    public int getItemCount() {
        return userList.size();
    }


}
