package com.itx.android.android_itx;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.itx.android.android_itx.Entity.Users;
import com.itx.android.android_itx.Service.UsersService;
import com.itx.android.android_itx.Utils.ApiUtils;
import com.itx.android.android_itx.Adapter.UsersAdapter;
import com.itx.android.android_itx.Utils.SessionManager;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;

public class ListUsers extends AppCompatActivity {

    private List<Users> userList = new ArrayList<>();
    private UsersAdapter uAdapter;
    SessionManager session;

    GoogleApiClient mGoogleApiClient;
    GoogleMap mMap;

    private LatLng mPosMarker = new LatLng(-6.3660756, 106.8346144);

    private MarkerOptions markerOptions;
    private Marker marker;

    UsersService mListUsersAPIService;

    @BindView(R.id.btn_add_user)
    FloatingActionButton btnAddUser;

    @BindView(R.id.pb_list_user)
    ProgressBar mPbListUser;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users);

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


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(uAdapter);

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
        Call<JsonObject> response = mListUsersAPIService.getAUsers();

        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> rawResponse) {
                hideLoading();
                if (rawResponse.isSuccessful()) {
                    try {
                        JsonArray jsonArray = rawResponse.body().get("data").getAsJsonArray();

                        if (jsonArray.size() == 0) {
                            hideLoading();
                            Toast.makeText(ListUsers.this, "Tidak ada data.",
                                    Toast.LENGTH_LONG).show();
                        }

                        for (int i = 0; i < jsonArray.size(); i++) {

                            JsonObject Data = jsonArray.get(i).getAsJsonObject();
                            Log.d("TEST", Data.toString());
                            Users user = new Users();
                            user.setIdUser(Data.get("_id").getAsString());
                            user.setRole(Data.get("role").getAsJsonObject().get("name").getAsString());
                            user.setFullName(Data.get("fullName").getAsString());

                            user.setAssets(Data.get("totalAssets").getAsString());

                            if (Data.get("phone") != null) {
                                user.setPhone(Data.get("phone").getAsString());
                            }

                            if (Data.get("address").getAsJsonObject().get("address") != null) {
                                user.setAddress(Data.get("address").getAsJsonObject().get("address").getAsString());
                            }

                            if (Data.get("photo") != null) {
                                user.setPhoto(Data.get("photo").getAsJsonObject().get("thumbnail").getAsString());
                            }


                            userList.add(user);


                        }

                        uAdapter.notifyDataSetChanged();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(ListUsers.this, "Gagal Mengambil Data",
                            Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                hideLoading();
                Toast.makeText(ListUsers.this, throwable.getMessage(),
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
                startActivity(new Intent(ListUsers.this, Login.class));
                finish();
                return true;
            case R.id.menu_edit:
                Toast.makeText(ListUsers.this, "kena", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
