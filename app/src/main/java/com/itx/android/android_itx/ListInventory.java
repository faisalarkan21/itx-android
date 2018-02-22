package com.itx.android.android_itx;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.DividerItemDecoration;
import com.itx.android.android_itx.Adapter.InventoryAdapter;
import com.itx.android.android_itx.Entity.Inventory;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListInventory extends AppCompatActivity {

    private List<Inventory> mListInventory = new ArrayList<>();

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private InventoryAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_inventory);

        ButterKnife.bind(this);

        mAdapter = new InventoryAdapter(mListInventory, this);

        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mRecyclerView.setAdapter(mAdapter);

        prepareUserData();


    }

    private void prepareUserData() {


//        String token =  session.getToken();
//        mListUsersAPIService = ApiUtils.getListUsersService(token);
//
//        Call<ResponseBody> response = mListUsersAPIService.getAUsers();
//
//        response.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> rawResponse) {
//                if (rawResponse.isSuccessful()) {
//                    try {
//
//                        JSONObject jsonObject = new JSONObject(rawResponse.body().string());
//
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    Toast.makeText(ListUsers.this, "Gagal",
//                            Toast.LENGTH_LONG).show();
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
//
//                Toast.makeText(ListUsers.this, throwable.getMessage(),
//                        Toast.LENGTH_LONG).show();
//            }
//        });


        Inventory invent = new Inventory("Superior", "3", "2");
        mListInventory.add(invent);

        invent = new Inventory("Superior", "3", "2");
        mListInventory.add(invent);

        invent = new Inventory("Superior", "3", "2");
        mListInventory.add(invent);

        invent = new Inventory("Superior", "3", "2");
        mListInventory.add(invent);

        invent = new Inventory("Superior", "3", "2");
        mListInventory.add(invent);

        invent = new Inventory("Superior", "3", "2");
        mListInventory.add(invent);


        // notify adapter about data set changes
        // so that it will render the list with new data
        mAdapter.notifyDataSetChanged();
    }
}
