package com.itx.android.android_itx;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    @BindView(R.id.select_image)
    Button mBtnAddImages;

    private static final int REQUEST_GALLERY_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_asset);

        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);




        ButterKnife.bind(this);


        mBtnAddImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
                openGalleryIntent.setType("image/*");
                openGalleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                openGalleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(openGalleryIntent,"Select Picture"), 1);

            }
        });

        mBtnAddAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: implement for adding new asset to the server
            }
        });
    }
}
