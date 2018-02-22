package com.itx.android.android_itx;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import butterknife.BindView;

public class CreateNewAsset extends AppCompatActivity {

    @BindView(R.id.et_add_asset_name)
    EditText mEtAssetName;
    @BindView(R.id.et_add_asset_deskripsi) EditText mEtAssetDesc;
    @BindView(R.id.et_add_asset_address) EditText mEtAssetAddress;
    @BindView(R.id.et_add_asset_province) EditText mEtAssetProvince;
    @BindView(R.id.et_add_asset_city) EditText mEtAssetCity;
    @BindView(R.id.et_add_asset_postal) EditText mEtAssetPostal;
    @BindView(R.id.et_add_asset_country) EditText mEtAssetCountry;
    @BindView(R.id.rb_new_asset)
    RatingBar mRbAsset;
    @BindView(R.id.btn_add_new_asset)
    Button mBtnAddAsset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_asset);

        mBtnAddAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TOOD: implement for adding new asset to the server
            }
        });
    }
}
