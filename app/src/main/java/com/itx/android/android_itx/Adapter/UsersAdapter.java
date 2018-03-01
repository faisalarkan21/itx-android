package com.itx.android.android_itx.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.itx.android.android_itx.ListAssets;
import com.itx.android.android_itx.R;
import com.itx.android.android_itx.Entity.Users;
import com.itx.android.android_itx.Service.UsersService;
import com.itx.android.android_itx.UpdateUser;
import com.itx.android.android_itx.Utils.ApiUtils;
import com.itx.android.android_itx.Utils.SessionManager;
import com.itx.android.android_itx.ViewHolder.UsersViewHolder;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;


public class UsersAdapter extends RecyclerView.Adapter<UsersViewHolder> {

    private List<Users> userList;
    private Context mContext;
    private SessionManager session;
    UsersService mListUsersAPIService;
    ProgressDialog progressDialog;
    private PopupMenu popupMenu;


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
    public void onBindViewHolder(final UsersViewHolder holder, final int position) {
        final Users user = userList.get(position);

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
                                updateUser.putExtra("id", user.getIdUser());
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
        holder.assets.setText("Assets : " + user.getAssets());

        if (user.getPhoto() != null) {
            Glide.with(mContext)
                    .load(ApiUtils.BASE_URL_USERS_IMAGE + user.getPhoto())
                    .into(holder.imageUsers);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("inidia", Integer.toString(position));
                Log.d("ini stringnya", user.getIdUser());
                Intent listAsset = new Intent(mContext, ListAssets.class);
                listAsset.putExtra("id", user.getIdUser());
                listAsset.putExtra("name", user.getFullName());
                listAsset.putExtra("address", user.getAddress());
                listAsset.putExtra("phone", user.getPhone());
                listAsset.putExtra("photo", user.getPhoto());
                listAsset.putExtra("role", user.getRole());

                mContext.startActivity(listAsset);


            }
        });


    }

    public void deleteUser(Users user) {
        session = new SessionManager(mContext);
        mListUsersAPIService = ApiUtils.getListUsersService(session.getToken());

        final Call<ResponseBody> response = mListUsersAPIService.deleteUser(user.getIdUser());

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
                response.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> rawResponse) {
                        if (rawResponse.isSuccessful()) {

                            new CountDownTimer(800, 800) {

                                public void onTick(long millisUntilFinished) {
                                    // You don't need anything here
                                }

                                public void onFinish() {
                                    progressDialog.dismiss();

                                    Toast.makeText(mContext, "Berhasil Mengapus",
                                            Toast.LENGTH_LONG).show();

                                    ((Activity) mContext).finish();
                                    mContext.startActivity(((Activity) mContext).getIntent());
                                    progressDialog.dismiss();
                                }
                            }.start();
                        } else {
                            Toast.makeText(mContext, "Gagal",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable throwable) {

                        Toast.makeText(mContext, throwable.getMessage(),
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
