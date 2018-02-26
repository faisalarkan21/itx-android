package com.itx.android.android_itx.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.itx.android.android_itx.Entity.Inventory;
import com.itx.android.android_itx.ListInventory;
import com.itx.android.android_itx.R;
import com.itx.android.android_itx.Service.ListInventoryService;
import com.itx.android.android_itx.Service.ListUsersService;
import com.itx.android.android_itx.Utils.ApiUtils;
import com.itx.android.android_itx.Utils.SessionManager;
import com.itx.android.android_itx.ViewHolder.InventoryViewHolder;
import com.itx.android.android_itx.Utils.RupiahCurrency;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by aladhims on 21/02/18.
 */


public class InventoryAdapter extends RecyclerView.Adapter<InventoryViewHolder> {

    private List<Inventory> inventoryList;
    private Context mContext;

    private SessionManager session;
    ListInventoryService mListInventAPIService;
    ProgressDialog progressDialog;


    public InventoryAdapter(List<Inventory> inventoryList, Context activity) {
        this.mContext = activity;
        this.inventoryList = inventoryList;
    }

    @Override
    public InventoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_inventory, parent, false);

        return new InventoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(InventoryViewHolder holder, int position) {
        final Inventory invent = inventoryList.get(position);
        holder.inventoryName.setText(invent.getName());


        if (invent.getImage() != null) {
            Glide.with(mContext)
                    .load(ApiUtils.BASE_URL_USERS_IMAGE + invent.getImage())
                    .into(holder.inventoryImage);
        }

        holder.btnImgDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                session = new SessionManager(mContext);
                mListInventAPIService = ApiUtils.getListInventoryService(session.getToken());

                final Call<ResponseBody> response = mListInventAPIService.deleteInventoryCategory(invent.getIdAsset());

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
        });


        if (invent.getFacilities() != null) {
            holder.inventoryFacilities.setText("Facilities : " + invent.getFacilities());
        } else{

            holder.inventoryFacilities.setText("Facilities : Tidak ada fasilitas" );

        }
        holder.inventoryStock.setText("Stock : " + invent.getStock());
        holder.inventorySpace.setText("Space : " + invent.getSpace());
        holder.inventoryPrice.setText("@ " + new RupiahCurrency().toRupiahFormat(invent.getPrice()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });

    }

    @Override
    public int getItemCount() {
        return inventoryList.size();
    }
}
