package com.itx.android.android_itx;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    Button btnAddUser;

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


                String token =  session.getToken();
                Toast.makeText(ListUsers.this, token,
                        Toast.LENGTH_LONG).show();
            }
        });


        uAdapter = new UsersAdapter(userList,this);

        recyclerView.setHasFixedSize(true);

        // vertical RecyclerView
        // keep user_list_row.xml width to `match_parent`
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        // horizontal RecyclerView
        // keep user_list_row.xml width to `wrap_content`
        // RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(mLayoutManager);

        // adding inbuilt divider line
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        // adding custom divider line with padding 16dp
        // recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.HORIZONTAL, 16));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(uAdapter);

        prepareUserData();

    }

    /**
     * Prepares sample data to provide data set to adapter
     */
    private void prepareUserData() {


        String token =  session.getToken();
        mListUsersAPIService = ApiUtils.getListUsersService(token);

        Call<JsonObject> response = mListUsersAPIService.getAUsers();

        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> rawResponse) {
                if (rawResponse.isSuccessful()) {
                    try {
                        JsonArray jsonArray = rawResponse.body().get("data").getAsJsonArray();

                        for (int i=0; i < jsonArray.size() ; i++ ){
                            JsonObject Data = jsonArray.get(i).getAsJsonObject();
                            Users user = new Users();
                            user.setFullName(Data.get("fullName").getAsString());
                            user.setAssets(Data.get("totalAssets").getAsString());
                            userList.add(user);
                            uAdapter.notifyDataSetChanged();
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
