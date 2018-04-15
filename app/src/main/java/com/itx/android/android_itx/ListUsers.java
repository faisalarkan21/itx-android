package com.itx.android.android_itx;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.itx.android.android_itx.Entity.Response;
import com.itx.android.android_itx.Entity.User;
import com.itx.android.android_itx.Service.UsersService;
import com.itx.android.android_itx.Utils.ApiUtils;
import com.itx.android.android_itx.Adapter.UsersAdapter;
import com.itx.android.android_itx.Utils.GridSpacingItemDecoration;
import com.itx.android.android_itx.Utils.SessionManager;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ListUsers extends AppCompatActivity {

    private List<User> userList = new ArrayList<>();
    private UsersAdapter uAdapter;
    SessionManager session;

    UsersService mListUsersAPIService;

    @BindView(R.id.btn_add_user)
    FloatingActionButton btnAddUser;

    @BindView(R.id.pb_list_user)
    ProgressBar mPbListUser;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.swipeRefreshLayout)
    PullRefreshLayout mPullRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users);

        getSupportActionBar().setTitle("List Pemilik");

        ButterKnife.bind(this);
        session = new SessionManager(this);

        btnAddUser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(ListUsers.this, CreateNewUser.class));
            }
        });

        uAdapter = new UsersAdapter(userList, this);

        recyclerView.setHasFixedSize(true);


        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 3);

        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3
                , 10, true));

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(uAdapter);

        mPullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                prepareUserData();
            }
        });

    }

    private void showLoading() {
        mPbListUser.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    private void hideLoading() {
        mPbListUser.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Prepares sample data to provide data set to adapter
     */
    private void prepareUserData() {

        showLoading();
        if (userList.size() >= 1) {
            userList.clear();
            uAdapter.notifyDataSetChanged();
        }
        mListUsersAPIService = ApiUtils.getListUsersService(session.getToken());
        Call<Response.GetAllUser> response = mListUsersAPIService.getAUsers();
        response.enqueue(new Callback<Response.GetAllUser>() {
            @Override
            public void onResponse(Call<Response.GetAllUser> call, retrofit2.Response<Response.GetAllUser> response) {
                mPullRefreshLayout.setRefreshing(false);
                hideLoading();
                if(response.isSuccessful()){
                    if(response.body().getStatus().getCode() == 200){
                        if(response.body().getData().getTotal() == 0){
                            Toast.makeText(ListUsers.this, "Tidak ada data.",
                                    Toast.LENGTH_LONG).show();
                        }else{
                            userList.addAll(response.body().getData().getUser());
                            uAdapter.notifyDataSetChanged();
                        }
                    }else{
                        Toast.makeText(ListUsers.this, response.body().getStatus().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(ListUsers.this, "Gagal Mengambil Data",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Response.GetAllUser> call, Throwable t) {
                mPullRefreshLayout.setRefreshing(false);
                hideLoading();
                Toast.makeText(ListUsers.this, t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareUserData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.actionbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                session.removeToken();
                session.remoceUserData();
                startActivity(new Intent(ListUsers.this, Login.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
