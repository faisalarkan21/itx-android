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

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.itx.android.android_itx.Entity.Asset;
import com.itx.android.android_itx.Entity.InventoryCategory;
import com.itx.android.android_itx.Entity.Response;
import com.itx.android.android_itx.InventoryDetail;
import com.itx.android.android_itx.R;
import com.itx.android.android_itx.Service.InventoryService;
import com.itx.android.android_itx.UpdateInventory;
import com.itx.android.android_itx.Utils.ApiUtils;
import com.itx.android.android_itx.Utils.RupiahCurrency;
import com.itx.android.android_itx.Utils.SessionManager;
import com.itx.android.android_itx.ViewHolder.InventoryViewHolder;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by aladhims on 21/02/18.
 */


public class InventoryAdapter extends RecyclerView.Adapter<InventoryViewHolder> {

    private List<InventoryCategory> inventoryList;
    private Context mContext;

    private SessionManager session;
    InventoryService mListInventAPIService;
    ProgressDialog progressDialog;
    private Asset mAsset;


    public InventoryAdapter(List<InventoryCategory> inventoryList, Context activity, Asset asset) {
        this.mContext = activity;
        this.inventoryList = inventoryList;
        this.mAsset = asset;
    }

    @Override
    public InventoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_inventory, parent, false);

        return new InventoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final InventoryViewHolder holder, int position) {
        Gson gson = new Gson();
        final InventoryCategory invent = inventoryList.get(position);
        final String data = gson.toJson(invent, InventoryCategory.class);
        final String dataAsset = gson.toJson(mAsset, Asset.class);

        holder.inventoryName.setText(invent.getName());


        if (invent.getImages().get(0).getmMedium() != null) {
            Glide.with(mContext)
                    .load(ApiUtils.BASE_URL_USERS_IMAGE + invent.getImages().get(0).getmMedium())
                    .into(holder.inventoryImage);
        }


        if (invent.getFacilities() != null) {
            for (int i = 0; i < invent.getFacilities().size(); i++) {
                if(i == 0){
                    holder.inventoryFacilities.setText("Fasilitas : " + invent.getFacilities().get(i).getName());
                }else{
                    holder.inventoryFacilities.append(", " + invent.getFacilities().get(i).getName());
                }
            }
        } else {

            holder.inventoryFacilities.setText("Facilities : Tidak ada fasilitas");
        }

        holder.inventoryStock.setText("Persediaan : " + invent.getStock());
        holder.inventorySpace.setText("Kapasitas : " + invent.getSpace());
        holder.inventoryPrice.setText(RupiahCurrency.toRupiahFormat(invent.getPrice()));


        holder.ivInventoptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(mContext, holder.ivInventoptions);
                popup.inflate(R.menu.list_menu);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_edit:
                                Intent updateInvent = new Intent(mContext, UpdateInventory.class);
                                updateInvent.putExtra("DATA", data);
                                updateInvent.putExtra("ASSET", dataAsset);
                                mContext.startActivity(updateInvent);
                                break;
                            case R.id.menu_delete:
                                deleteUser(invent);
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
            public void onClick(View view) {
                Intent updateInvent = new Intent(mContext, InventoryDetail.class);
                updateInvent.putExtra("DATA", data);
                mContext.startActivity(updateInvent);
            }
        });
    }


    public void deleteUser(InventoryCategory invent) {

        session = new SessionManager(mContext);
        mListInventAPIService = ApiUtils.getListInventoryService(session.getToken());

        final Call<Response.DeleteInventory> response = mListInventAPIService.deleteInventoryCategory(invent.getId());

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
                response.enqueue(new Callback<Response.DeleteInventory>() {
                    @Override
                    public void onResponse(Call<Response.DeleteInventory> call, retrofit2.Response<Response.DeleteInventory> rawResponse) {
                        if (rawResponse.isSuccessful()) {
                            progressDialog.dismiss();

                            Toast.makeText(mContext, "Berhasil Mengapus",
                                    Toast.LENGTH_LONG).show();

                            ((Activity) mContext).finish();
                            mContext.startActivity(((Activity) mContext).getIntent());
                            progressDialog.dismiss();

                        } else {
                            Toast.makeText(mContext, "Gagal",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Response.DeleteInventory> call, Throwable throwable) {

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
        return inventoryList.size();
    }
}
