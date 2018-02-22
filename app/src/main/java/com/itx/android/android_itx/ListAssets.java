package com.itx.android.android_itx;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.Toast;

import com.itx.android.android_itx.Adapter.AssetsAdapter;
import com.itx.android.android_itx.Entity.Assets;
import com.itx.android.android_itx.Entity.Users;
import com.itx.android.android_itx.Utils.ApiUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class ListAssets extends AppCompatActivity {

    private List<Assets> mListAsset = new ArrayList<>();
    @BindView(R.id.btn_add_asset)
    Button mBtnAddAsset;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private AssetsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_assets);

        ButterKnife.bind(this);

        mAdapter = new AssetsAdapter(mListAsset, this);

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


        Assets assets = new Assets("Rumah Zakat", "Jalan Haji Mawi", 4.5f);
        mListAsset.add(assets);

        assets = new Assets("Rumah Zakat", "Jalan Haji Mawi", 2.5f);
        mListAsset.add(assets);

        assets = new Assets("Rumah Zakat", "Jalan Haji Mawi", 3);
        mListAsset.add(assets);

        assets = new Assets("Rumah Zakat", "Jalan Haji Mawi", 4);
        mListAsset.add(assets);

        assets = new Assets("Rumah Zakat", "Jalan Haji Mawi", 3.1f);
        mListAsset.add(assets);

        assets = new Assets("Rumah Zakat", "Jalan Haji Mawi",4.5f);
        mListAsset.add(assets);


        // notify adapter about data set changes
        // so that it will render the list with new data
        mAdapter.notifyDataSetChanged();
    }


}
