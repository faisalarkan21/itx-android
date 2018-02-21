package com.itx.android.android_itx;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import com.itx.android.android_itx.Adapter.AssetsAdapter;
import com.itx.android.android_itx.Entity.Assets;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListAssets extends AppCompatActivity {

    private List<Assets> mListAsset = new ArrayList<>();
    private @BindView(R.id.btn_add_asset) Button mBtnAddAsset;
    private @BindView(R.id.recycler_view) RecyclerView mRecyclerView;
    private AssetsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_assets);

        ButterKnife.bind(this);

        mAdapter = new AssetsAdapter(this,mListAsset);

        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);;

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        mRecyclerView.setAdapter(mAdapter);

    }
}
