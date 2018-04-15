package com.itx.android.android_itx.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.gson.Gson;
import com.itx.android.android_itx.Entity.Asset;
import com.itx.android.android_itx.Entity.Response;
import com.itx.android.android_itx.ListInventory;
import com.itx.android.android_itx.R;
import com.itx.android.android_itx.Service.AssetService;
import com.itx.android.android_itx.UpdateAsset;
import com.itx.android.android_itx.Utils.ApiUtils;
import com.itx.android.android_itx.Utils.SessionManager;
import com.itx.android.android_itx.ViewHolder.AssetsViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by aladhims on 21/02/18.
 */

public class AssetsAdapter extends RecyclerView.Adapter<AssetsViewHolder> {

    private Context mContext;
    private List<Asset> mListAssets;
    AssetService mListAssetsAPIService;
    private SessionManager session;
    ProgressDialog progressDialog;

    public AssetsAdapter(List<Asset> assets, Context context) {
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
        Gson gson = new Gson();
        final Asset currentAsset = mListAssets.get(position);
        final String data = gson.toJson(currentAsset, Asset.class);

        holder.mTvAssetName.setText(currentAsset.getName());
        holder.mTvAssetCategory.setText(currentAsset.getAssetCategory().getName());
        holder.mRatingBar.setRating(Math.round(currentAsset.getRating()));

        if (currentAsset.getImages() != null) {
            Picasso.with(mContext)
                    .load(ApiUtils.BASE_URL_USERS_IMAGE + currentAsset.getImages().get(0).getmMedium())
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
                                updateAsset.putExtra("DATA", data);
                                mContext.startActivity(updateAsset);
                                break;
                            case R.id.menu_delete:
                                deleteAsset(currentAsset);
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
                listInventory.putExtra("DATA", data);
                mContext.startActivity(listInventory);

            }
        });
    }


    public void deleteAsset(Asset asset) {
        session = new SessionManager(mContext);
        mListAssetsAPIService = ApiUtils.getListAssetsService(session.getToken());

        final Call<Response.DeleteAsset> response = mListAssetsAPIService.deleteAssets(asset.getId());

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
                response.enqueue(new Callback<Response.DeleteAsset>() {
                    @Override
                    public void onResponse(Call<Response.DeleteAsset> call, retrofit2.Response<Response.DeleteAsset> response) {
                        if(response.isSuccessful()){
                            progressDialog.dismiss();
                            if(response.body().getStatus().getCode() == 200){
                                Toast.makeText(mContext, "Berhasil Mengapus",
                                        Toast.LENGTH_LONG).show();

                                ((Activity) mContext).finish();
                                mContext.startActivity(((Activity) mContext).getIntent());
                                progressDialog.dismiss();
                            }else{
                                Toast.makeText(mContext, response.body().getStatus().getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(mContext, "Gagal",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Response.DeleteAsset> call, Throwable t) {
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
        return mListAssets.size();
    }
}
