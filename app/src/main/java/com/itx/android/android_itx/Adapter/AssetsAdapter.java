package com.itx.android.android_itx.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.itx.android.android_itx.Entity.Assets;
import com.itx.android.android_itx.Entity.Users;
import com.itx.android.android_itx.ListInventory;
import com.itx.android.android_itx.R;
import com.itx.android.android_itx.Service.AssetService;
import com.itx.android.android_itx.UpdateAsset;
import com.itx.android.android_itx.UpdateUser;
import com.itx.android.android_itx.Utils.ApiUtils;
import com.itx.android.android_itx.Utils.SessionManager;
import com.itx.android.android_itx.ViewHolder.AssetsViewHolder;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by aladhims on 21/02/18.
 */

public class AssetsAdapter extends RecyclerView.Adapter<AssetsViewHolder> {

    private Context mContext;
    private List<Assets> mListAssets;
    AssetService mListAssetsAPIService;
    private SessionManager session;
    ProgressDialog progressDialog;


    public AssetsAdapter(List<Assets> assets, Context context) {
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
    public void onBindViewHolder(final AssetsViewHolder holder, int position) {
        final Assets currentAsset = mListAssets.get(position);
        holder.mTvAssetName.setText(currentAsset.getName());
        holder.mTvAssetCategory.setText(currentAsset.getAssetCategory());
        holder.mRatingBar.setRating(Math.round(currentAsset.getRating()));

        if (currentAsset.getImages() != null) {
            Glide.with(mContext)
                    .load(ApiUtils.BASE_URL_USERS_IMAGE + currentAsset.getImages())
                    .into(holder.mIvGambarAsset);
        }

        holder.iv_assets_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(mContext, holder.iv_assets_options);
                popup.inflate(R.menu.list_menu);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_edit:
                                Intent updateAsset = new Intent(mContext, UpdateAsset.class);
                                updateAsset.putExtra("id", currentAsset.getId());
                                mContext.startActivity(updateAsset);
                                break;
                            case R.id.menu_delete:
                                deleteUser(currentAsset);
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent listInventory = new Intent(mContext, ListInventory.class);
                listInventory.putExtra("idAsset", currentAsset.getId());
                listInventory.putExtra("address", currentAsset.getAddress());
                listInventory.putExtra("images", currentAsset.getImages());
                listInventory.putExtra("assetName", currentAsset.getName());
                listInventory.putExtra("categoryName", currentAsset.getAssetCategory());
                listInventory.putExtra("phone", currentAsset.getPhone());
                listInventory.putExtra("rating", currentAsset.getRating());
                mContext.startActivity(listInventory);

            }
        });
    }



    public void deleteUser(Assets asset) {
        session = new SessionManager(mContext);
        mListAssetsAPIService = ApiUtils.getListAssetsService(session.getToken());

        final Call<ResponseBody> response = mListAssetsAPIService.deleteAssets(asset.getId());

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
        return mListAssets.size();
    }
}
