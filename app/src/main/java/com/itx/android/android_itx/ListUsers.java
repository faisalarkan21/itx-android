package com.itx.android.android_itx;

import android.app.ProgressDialog;
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
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.itx.android.android_itx.Entity.Users;
import com.itx.android.android_itx.Service.ListUsersService;
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

    ListUsersService mListUsersAPIService;

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

        uAdapter = new UsersAdapter(userList,this);

        recyclerView.setHasFixedSize(true);

        showLoading();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(uAdapter);

        prepareUserData();

    }

    private void showLoading(){
        mPbListUser.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    private void hideLoading(){
        mPbListUser.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Prepares sample data to provide data set to adapter
     */
    private void prepareUserData() {

        mListUsersAPIService = ApiUtils.getListUsersService(session.getToken());
        Call<JsonObject> response = mListUsersAPIService.getAUsers();

        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> rawResponse) {
                if (rawResponse.isSuccessful()) {
                    try {
                        JsonArray jsonArray = rawResponse.body().get("data").getAsJsonArray();

                        for (int i=0; i < jsonArray.size() ; i++ ){

                            JsonObject Data = jsonArray.get(i).getAsJsonObject();
                            Log.d("TEST", Data.toString());
                            Users user = new Users();
                            user.setIdUser(Data.get("_id").getAsString());//
                            user.setFullName(Data.get("fullName").getAsString());
                            user.setAssets(Data.get("totalAssets").getAsString());

                            if(Data.get("photo") != null){
                                user.setPhoto(Data.get("photo").getAsJsonObject().get("thumbnail").getAsString());
                            }


                            userList.add(user);

                            new CountDownTimer(1000, 1000) {

                                public void onTick(long millisUntilFinished) {
                                    // You don't need anything here
                                }

                                public void onFinish() {
                                    uAdapter.notifyDataSetChanged();
                                    hideLoading();
                                }
                            }.start();


                        }
                        Toast.makeText(ListUsers.this, "Terdapat : " + Integer.toString(jsonArray.size()) + " data",
                                Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(ListUsers.this, "Gagal",
                            Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {

                Toast.makeText(ListUsers.this, throwable.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });


    }


}
