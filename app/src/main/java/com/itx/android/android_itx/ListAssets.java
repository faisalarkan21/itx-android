package com.itx.android.android_itx;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itx.android.android_itx.Adapter.AssetsAdapter;
import com.itx.android.android_itx.Entity.Asset;
import com.itx.android.android_itx.Entity.Response;
import com.itx.android.android_itx.Entity.User;
import com.itx.android.android_itx.Service.AssetService;
import com.itx.android.android_itx.Service.UsersService;
import com.itx.android.android_itx.Utils.ApiUtils;
import com.itx.android.android_itx.Utils.GridSpacingItemDecoration;
import com.itx.android.android_itx.Utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ListAssets extends AppCompatActivity {

    private List<Asset> mListAsset = new ArrayList<>();
    @BindView(R.id.btn_add_asset)
    FloatingActionButton mBtnAddAsset;

    @BindView(R.id.userName)
    TextView mUserName;

    @BindView(R.id.tv_user_role)
    TextView mUserRole;

    @BindView(R.id.tv_user_address)
    TextView mUserAddress;

    @BindView(R.id.tv_user_no_telp)
    TextView mUserTelp;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.iv_user_images)
    CircleImageView mIvUserImages;

    @BindView(R.id.pb_list_asset)
    ProgressBar mPbListAsset;

    @BindView(R.id.swipeRefreshLayout)
    PullRefreshLayout mPullRefreshLayout;

    private AssetsAdapter mAdapter;
    private AssetService mAssetAPIService;
    private UsersService mListUsersAPIService;
    private SessionManager session;
    private User mUser;
    private String data;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_assets);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle("Pemilik");

        session = new SessionManager(this);
        mAdapter = new AssetsAdapter(mListAsset, this);

        mRecyclerView.setHasFixedSize(true);

        showLoading();

        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2);

        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2
                , 10, true));

        mRecyclerView.setAdapter(mAdapter);
        Gson gson = new Gson();
        data = getIntent().getStringExtra("DATA");
        mUser = gson.fromJson( data, User.class);

        mUserName.setText(mUser.getFullName());
        mUserRole.setText(mUser.getRole().getName());
        mUserAddress.setText(mUser.getAddress().getAddress());
        mUserTelp.setText(mUser.getPhone());

        if (mUser.getPhoto().getAlt() != null){
            Glide.with(ListAssets.this)
                    .load(ApiUtils.BASE_URL_USERS_IMAGE + mUser.getPhoto().getLarge())
                    .into(mIvUserImages);
        }

        mBtnAddAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addAssetIntent = new Intent(ListAssets.this, CreateNewAsset.class);
                Gson gson = new Gson();
                String userData = gson.toJson(mUser, User.class);
                addAssetIntent.putExtra("DATA", userData);
                startActivity(addAssetIntent);
            }
        });

        mPullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                prepareUserData();
            }
        });
    }

    private void showLoading() {
        mPbListAsset.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void hideLoading() {
        mPbListAsset.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }


    private void prepareUserData() {

        showLoading();
        if(mListAsset.size() >= 1){
            mListAsset.clear();
            mAdapter.notifyDataSetChanged();
        }

        mAssetAPIService = ApiUtils.getListAssetsService(session.getToken());

        Call<Response.GetAsset> response = mAssetAPIService.getUserAssets(mUser.get_id());
        response.enqueue(new Callback<Response.GetAsset>() {
            @Override
            public void onResponse(Call<Response.GetAsset> call, retrofit2.Response<Response.GetAsset> response) {
                mPullRefreshLayout.setRefreshing(false);
                hideLoading();
                if(response.isSuccessful()){
                    if(response.body().getStatus().getCode() == 200){
                        if(response.body().getData().getTotal() == 0){
                            Toast.makeText(ListAssets.this, "Tidak ada data.",
                                    Toast.LENGTH_LONG).show();
                        }else{
                            mListAsset.addAll(response.body().getData().getAsset());
                            mAdapter.notifyDataSetChanged();
                        }
                    }else{
                        Toast.makeText(ListAssets.this, response.body().getStatus().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(ListAssets.this, "Gagal Mengambil Data",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Response.GetAsset> call, Throwable t) {
                mPullRefreshLayout.setRefreshing(false);
                hideLoading();
                Toast.makeText(ListAssets.this, t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

//    public void deleteUser(User user) {
//        session = new SessionManager(ListAssets.this);
//        mListUsersAPIService = ApiUtils.getListUsersService(session.getToken());
//
//        final Call<Response.DeleteUser> response = mListUsersAPIService.deleteUser(user.get_id());
//
//        android.app.AlertDialog.Builder alertBuilder = new android.app.AlertDialog.Builder(ListAssets.this);
//        alertBuilder.setTitle("Konfirmasi");
//        alertBuilder.setMessage("Anda yakin ingin menghapus ?");
//        alertBuilder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                progressDialog = new ProgressDialog(ListAssets.this);
//                progressDialog.setMessage("Menghapus Data");
//                progressDialog.show();
//
//                dialog.dismiss();
//                response.enqueue(new Callback<Response.DeleteUser>() {
//                    @Override
//                    public void onResponse(Call<Response.DeleteUser> call, retrofit2.Response<Response.DeleteUser> response) {
//                        progressDialog.dismiss();
//                        if(response.isSuccessful()){
//                            if(response.body().getStatus().getCode() == 200){
//                                Toast.makeText(ListAssets.this, "Data telah dihapus",
//                                        Toast.LENGTH_LONG).show();
//
//                                finish();
//                            }else{
//                                Toast.makeText(ListAssets.this, response.body().getStatus().getMessage(),
//                                        Toast.LENGTH_LONG).show();
//                            }
//                        }else {
//                            Toast.makeText(ListAssets.this, "Data gagal dihapus",
//                                    Toast.LENGTH_LONG).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<Response.DeleteUser> call, Throwable t) {
//                        Toast.makeText(ListAssets.this, t.getMessage(),
//                                Toast.LENGTH_LONG).show();
//                    }
//                });
//            }
//        });
//        alertBuilder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        android.app.AlertDialog alertDialog = alertBuilder.create();
//        alertDialog.show();
//
//    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareUserData();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.list_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.menu_edit:
//                Intent updateAsset = new Intent(ListAssets.this, UpdateUser.class);
//                updateAsset.putExtra("DATA", data);
//                startActivity(updateAsset);
//                return true;
//            case R.id.menu_delete:
//                deleteUser(mUser);
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//
//    }
}
