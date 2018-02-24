package com.itx.android.android_itx;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.itx.android.android_itx.Service.ListAssetService;
import com.itx.android.android_itx.Utils.ApiUtils;
import com.itx.android.android_itx.Utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateNewAsset extends AppCompatActivity {

    private final static int GALLERY_RC = 299;

    private String idUser;

    //URI List for the images that will be on the server
    ArrayList<Uri> uriImages = new ArrayList<>();
    ArrayList<String> categories = new ArrayList<>();

    private ListAssetService mListAssetService;
    private String categoryIdSelected;

    ArrayAdapter<String> spAdapter;

    @BindView(R.id.et_add_asset_name)
    EditText mEtAssetName;
    @BindView(R.id.et_add_asset_brand) EditText mEtAssetBrand;
    @BindView(R.id.et_add_asset_npwp) EditText mEtAssetNPWP;
    @BindView(R.id.et_add_asset_phone) EditText mEtAssetPhone;
    @BindView(R.id.et_add_asset_address) EditText mEtAssetAddress;
    @BindView(R.id.et_add_asset_province) EditText mEtAssetProvince;
    @BindView(R.id.et_add_asset_city) EditText mEtAssetCity;
    @BindView(R.id.et_add_asset_postal) EditText mEtAssetPostal;
    @BindView(R.id.et_add_asset_country) EditText mEtAssetCountry;
    @BindView(R.id.sp_add_asset_categories)
    Spinner mSpCategories;
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

        idUser = getIntent().getStringExtra("idUser");
        mSpCategories.setVisibility(View.GONE);
        spAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item);
        mSpCategories.setAdapter(spAdapter);
        mListAssetService =  ApiUtils.getListAssetsService(new SessionManager(this).getToken());


        prepareAssetCategories();
        mBtnAddImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
                openGalleryIntent.setType("image/*");
                openGalleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                openGalleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(openGalleryIntent,"Select Picture"), GALLERY_RC);

            }
        });

        mBtnAddAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: implement for adding new asset to the server
                createAsset();
            }
        });


    }

    private void prepareAssetCategories(){
        Log.d("TEST", idUser);
        Toast.makeText(this, idUser, Toast.LENGTH_SHORT).show();
        Call<JsonObject> categoriesRequest = mListAssetService.getAssetCategories();
        categoriesRequest.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    JsonArray res = response.body().get("data").getAsJsonArray();
                    for(int i=0; i < res.size(); i++){
                        JsonObject category = res.get(i).getAsJsonObject();
                        spAdapter.add(category.get("name").getAsString());
                        categories.add(category.get("_id").getAsString());
                    }
                    mSpCategories.setVisibility(View.VISIBLE);
                    mSpCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            categoryIdSelected = categories.get(i);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                            categoryIdSelected = null;
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private void createAsset(){

        if (categoryIdSelected == null){
            Toast.makeText(this, "Pilih kategorinya", Toast.LENGTH_SHORT).show();
            return;
        }


        String name = mEtAssetName.getText().toString().trim();
        String brand = mEtAssetBrand.getText().toString().trim();
        String npwp = mEtAssetNPWP.getText().toString().trim();
        String phone = mEtAssetPhone.getText().toString().trim();
        String address = mEtAssetAddress.getText().toString().trim();
        String province = mEtAssetProvince.getText().toString().trim();
        String city = mEtAssetCity.getText().toString().trim();
        String postal = mEtAssetPostal.getText().toString().trim();
        String country = mEtAssetCountry.getText().toString().trim();


        try {
            JSONObject data = new JSONObject();
            data.put("assetCategory", categoryIdSelected);
            data.put("name", name);
            data.put("brand", brand);
            data.put("npwp", npwp);
            data.put("phone", phone);
            data.put("user", idUser);

            JSONObject location = new JSONObject();
            location.put("address", address);
            location.put("province", province);
            location.put("city", city);
            location.put("postalCode", postal);
            location.put("country", country);

            JSONObject request = new JSONObject();
            request.put("data", data);
            request.put("location", location);

            Log.d("TEST123", request.toString());
            RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),request.toString());

            Call<ResponseBody> response = mListAssetService.createAsset(requestBody);
            response.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()){
                        startActivity(new Intent(CreateNewAsset.this, ListAssets.class ));
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==  GALLERY_RC && resultCode == Activity.RESULT_OK){

        }
    }
}
