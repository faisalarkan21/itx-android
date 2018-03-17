package com.itx.android.android_itx;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.database.Cursor;
import android.graphics.Rect;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.itx.android.android_itx.Adapter.AssetsAdapter;
import com.itx.android.android_itx.Adapter.PreviewAdapter;
import com.itx.android.android_itx.Entity.Image;
import com.itx.android.android_itx.Entity.ImageHolder;
import com.itx.android.android_itx.Service.APIService;
import com.itx.android.android_itx.Service.AssetService;
import com.itx.android.android_itx.Utils.ApiUtils;
import com.itx.android.android_itx.Utils.AutoCompleteUtils;
import com.itx.android.android_itx.Utils.ImageUtils;
import com.itx.android.android_itx.Utils.SessionManager;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by faisal on 3/1/18.
 */

public class UpdateAsset extends AppCompatActivity implements
        View.OnClickListener,
        Validator.ValidationListener,
        EasyPermissions.PermissionCallbacks {
    private static final int CAMERA_REQUEST = 1000;
    private static final int RC_PERMS_CAMERA = 13;
    private static final int RC_PERMS_GALLERY = 14;
    private static final int GALLERY_REQUEST = 1001;

    private final static int LOCATION_SETTING_RC = 122;
    final AutoCompleteUtils completeUtils = new AutoCompleteUtils(this);

    //URI List for the images that will be on the server
    public static ArrayList<ImageHolder> imagePreviews = new ArrayList<>();
    public static ArrayList<File> fileImages = new ArrayList<>();
    ArrayList<String> categories = new ArrayList<>();

    private AssetService mAssetService;
    private String categoryIdSelected;
    private APIService mApiSevice;

    private String mAddress = "";

    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleMap mMap;
    private Marker mAssetMarker;
    private MarkerOptions mAssetMarkerOptions;
    Validator validator;
    private LatLng assetLocation = new LatLng(-7.348868, 108.535240);
    private String[] locationPerm = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    ArrayAdapter spAdapterCity;
    ArrayAdapter spAdapterProvince;
    String chosenCity, chosenProvince;

    UpdateAsset.ReceiverBroadcastMap recieve;
    IntentFilter filter;
    String action;


    private PreviewAdapter mPreviewAdapter;

    ArrayAdapter<String> spAdapter;

    @BindView(R.id.rv_preview_img_new_asset)
    RecyclerView mRvPreviewImageAsset;

    @NotEmpty
    @BindView(R.id.et_add_asset_name)
    EditText mEtAssetName;

    @NotEmpty
    @BindView(R.id.et_add_asset_brand)
    EditText mEtAssetBrand;

    @NotEmpty
    @BindView(R.id.et_add_asset_npwp)
    EditText mEtAssetNPWP;

    @NotEmpty
    @BindView(R.id.et_add_asset_phone)
    EditText mEtAssetPhone;

    @NotEmpty
    @BindView(R.id.et_add_asset_address)
    EditText mEtAssetAddress;

    @BindView(R.id.sp_add_asset_province)
    Spinner mSpProvince;

    @BindView(R.id.sp_add_asset_city)
    Spinner mSpCity;

    @NotEmpty
    @BindView(R.id.et_add_asset_postal)
    EditText mEtAssetPostal;

    @NotEmpty
    @BindView(R.id.et_add_asset_country)
    AutoCompleteTextView mAcAssetCountry;

    @BindView(R.id.sp_add_asset_categories)
    Spinner mSpCategories;

    @BindView(R.id.rb_new_asset)
    RatingBar mRbAsset;

    @BindView(R.id.btn_add_new_asset)
    Button mBtnAddAsset;

    @BindView(R.id.select_image)
    Button mBtnAddImages;

    @BindView(R.id.static_map)
    ImageView staticMap;

    @BindView(R.id.btn_add_location)
    Button mBtnAddLocaion;

    String idUser, userAdress, userName, phone, imagesDetail, role;
    AssetService mAssetAPIService;
    SessionManager session;
    ProgressDialog progressDialog;


    private static final int LOCATION_REQUEST = 201;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_asset);

        ButterKnife.bind(this);
        mApiSevice = ApiUtils.getAPIService(new SessionManager(this).getToken());
        validator = new Validator(this);
        validator.setValidationListener(this);

        session = new SessionManager(this);
        progressDialog = new ProgressDialog(UpdateAsset.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Sedang Menyiapkan Data");
        progressDialog.show();

        recieve = new ReceiverBroadcastMap();
        filter = new IntentFilter("sendMapCoordinates");
        registerReceiver(recieve, filter);

        idUser = getIntent().getStringExtra("idUser");

        if (fileImages.size() > 0 || imagePreviews.size() > 0) {
            fileImages.clear();
            imagePreviews.clear();
        }
        mBtnAddImages.setOnClickListener(this);
        mBtnAddAsset.setOnClickListener(this);
        mBtnAddLocaion.setOnClickListener(this);
        mBtnAddLocaion.setText("Update / Detail Map");

        mSpCategories.setVisibility(View.GONE);
        spAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        mSpCategories.setAdapter(spAdapter);
        mAssetService = ApiUtils.getListAssetsService(new SessionManager(this).getToken());

        mRvPreviewImageAsset.setLayoutManager(new GridLayoutManager(this, 2));
        mRvPreviewImageAsset.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int space = 16;
                outRect.left = space;
                outRect.right = space;
                outRect.top = space;
            }
        });
        mPreviewAdapter = new PreviewAdapter(imagePreviews, this, new PreviewAdapter.previewInterface() {
            @Override
            public void deleteCurrentPreviewImage(int position) {
                ImageHolder currentImage = imagePreviews.get(position);
                for (int i = 0; i < fileImages.size(); i++) {
                    if (currentImage.getmUri() == null) continue;
                    String fileName = Uri.fromFile(fileImages.get(i)).getLastPathSegment();
                    String currentUriName = currentImage.getmUri().getLastPathSegment();
                    Log.d("filename : ", fileName);
                    Log.d("Uriname : ", currentUriName);
                    if (currentImage.getmUri() != null && fileName.equals(currentUriName)) {
                        fileImages.remove(i);
                    }
                }

                Log.d("SIZE ", "The size image : " + imagePreviews.size() + " and files size : " + fileImages.size());
                imagePreviews.remove(position);
                mPreviewAdapter.notifyDataSetChanged();
            }
        });
        mRvPreviewImageAsset.setAdapter(mPreviewAdapter);

        prepareAssetCategories();
    }

    public void setProvince(final String initProvince) {

        ArrayAdapter<String> adapterProvince = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, completeUtils.getArrayProvicesJson());

        String country[] = {"Indonesia"};

        ArrayAdapter<String> adapterCountry = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, country);

        mAcAssetCountry.setAdapter(adapterCountry);
        mAcAssetCountry.setThreshold(1);

        spAdapterProvince = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                completeUtils.getArrayProvicesJson());
        mSpProvince.setAdapter(spAdapterProvince);

        mSpProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                chosenProvince = adapterView.getItemAtPosition(i).toString();

                if (!adapterView.getItemAtPosition(i).toString().equals(initProvince)) {
                    setCitybyProvince(chosenProvince);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                chosenCity = null;
            }
        });
    }

    public void setCitybyProvince(String provinceName) {


        spAdapterCity = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                completeUtils.getArrayCityJson(provinceName));
        mSpCity.setAdapter(spAdapterCity);


        mSpCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                chosenCity = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                chosenCity = null;
            }
        });

    }


    private void prepareAssetCategories() {
        Call<JsonObject> categoriesRequest = mAssetService.getAssetCategories();
        categoriesRequest.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonArray res = response.body().get("data").getAsJsonArray();
                    for (int i = 0; i < res.size(); i++) {
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

                    // make it only if response isSuccessful true we can prepare assets
                    // bug resolved sometimes categories asset not loaded
                    prepareAsset();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {


            }
        });
    }


    private void prepareAsset() {

        idUser = getIntent().getStringExtra("id");
        mAssetAPIService = ApiUtils.getListAssetsService(session.getToken());

        Call<JsonObject> response = mAssetAPIService.getUserAsset(idUser);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> rawResponse) {
                if (rawResponse.isSuccessful()) {
                    try {
                        JsonObject jsonObject = rawResponse.body().get("data").getAsJsonObject();
                        mEtAssetName.setText(jsonObject.get("name").getAsString());

                        mEtAssetBrand.setText(jsonObject.get("brand").getAsString());
                        mEtAssetNPWP.setText(jsonObject.get("npwp").getAsString());
                        mEtAssetPhone.setText(jsonObject.get("phone").getAsString());
                        mEtAssetPostal.setText(jsonObject.get("address").getAsJsonObject().get("postalCode").getAsString());

                        mAcAssetCountry.setText(jsonObject.get("address").getAsJsonObject().get("country").getAsString());

                        JsonArray images = jsonObject.get("images").getAsJsonArray();
                        for (int i = 0; i < images.size(); i++) {
                            Gson gson = new Gson();
                            Image image = gson.fromJson(images.get(i), Image.class);
                            imagePreviews.add(new ImageHolder(image, null));
                        }

                        mRbAsset.setRating(jsonObject.get("rating").getAsFloat());

                        String selectedValueProv = jsonObject.get("address").getAsJsonObject().get("province").getAsString();
                        setProvince(selectedValueProv);

                        int selectionPositionProv = spAdapterProvince.getPosition(selectedValueProv);
                        mSpProvince.setSelection(selectionPositionProv);
                        setCitybyProvince(selectedValueProv);

                        int selectionPositionCity = spAdapterCity.getPosition(jsonObject.get("address").getAsJsonObject().get("city").getAsString());
                        mSpCity.setSelection(selectionPositionCity);

                        String assetCategory = jsonObject.get("assetCategory").getAsJsonObject().get("name").getAsString();
                        int selectionPosition = spAdapter.getPosition(assetCategory);
                        categoryIdSelected = categories.get(selectionPosition);
                        mSpCategories.setSelection(selectionPosition);

                        JsonArray AssetCoordinates = jsonObject.get("address").getAsJsonObject().get("coordinates").getAsJsonArray();

                        if (AssetCoordinates != null) {
                            assetLocation = new LatLng(AssetCoordinates.get(0).getAsDouble(), AssetCoordinates.get(1).getAsDouble());
                        }

                        String fullAddress = jsonObject.get("address").getAsJsonObject().get("address").getAsString();
                        mEtAssetAddress.setText(fullAddress);

                        setStaticMap();

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                progressDialog.dismiss();
                            }
                        }, 3800);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(UpdateAsset.this, "Gagal",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {

                Toast.makeText(UpdateAsset.this, throwable.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });


    }

    private void setStaticMap() {

        String formattedLangLat = assetLocation.latitude + "," + assetLocation.longitude;
        Call<ResponseBody> staticMapRequest = null;
        try {
            staticMapRequest = mApiSevice.getStaticMap(formattedLangLat, "18", "600x600", "color:red" + java.net.URLDecoder.decode("%7C", "UTF-8") + formattedLangLat, getString(R.string.google_maps_key));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        staticMapRequest.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> rawResponse) {
//
                if (rawResponse.isSuccessful()) {

                    Glide.with(UpdateAsset.this)
                            .load(rawResponse.raw().request().url().toString())
                            .into(staticMap);
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }


    private void updateAsset() {

        progressDialog = new ProgressDialog(UpdateAsset.this);
        progressDialog.setMessage("Menyimpan Data");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        final String name = mEtAssetName.getText().toString().trim();
        final String brand = mEtAssetBrand.getText().toString().trim();
        final String npwp = mEtAssetNPWP.getText().toString().trim();
        final String phone = mEtAssetPhone.getText().toString().trim();
        final String address = mEtAssetAddress.getText().toString().trim();
        final String postal = mEtAssetPostal.getText().toString().trim();
        final String country = mAcAssetCountry.getText().toString().trim();
        final int rating = Math.round(mRbAsset.getRating());


        if (fileImages.size() < 1) {
            //INI JIKA USER TDK MENAMBAHKAN FOTO BARU DARI CAMERA/GALLERY
            try {

                List<Double> mListCoordinates = new ArrayList<>();
                mListCoordinates.add(assetLocation.latitude);
                mListCoordinates.add(assetLocation.longitude);


                final JSONArray images = new JSONArray();
                for (int i = 0; i < imagePreviews.size(); i++) {
                    ImageHolder img = imagePreviews.get(i);
                    if (img.getmImage() != null) {
                        Gson gson = new Gson();
                        String jsonImg = gson.toJson(img.getmImage());
                        images.put(new JSONObject(jsonImg));
                    }
                }
                JSONObject data = new JSONObject();
                data.put("assetCategory", categoryIdSelected);
                data.put("name", name);
                data.put("brand", brand);
                data.put("npwp", npwp);
                data.put("phone", phone);
                data.put("user", idUser);
                data.put("rating", rating);


                JSONObject location = new JSONObject();
                location.put("address", address);
                location.put("province", chosenProvince);
                location.put("city", chosenCity);
                location.put("postalCode", postal);
                location.put("country", country);

                JSONArray jsonArrayCoordinates = new JSONArray(mListCoordinates);
                location.put("coordinates", jsonArrayCoordinates);

                JSONObject request = new JSONObject();
                request.put("data", data);
                request.put("location", location);
                request.put("images", images);

                RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), request.toString());

                Call<ResponseBody> res = mAssetService.updateAsset(idUser, requestBody);


                res.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        progressDialog.dismiss();
                        if (response.isSuccessful()) {
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        progressDialog.dismiss();
                        Toast.makeText(UpdateAsset.this, "Gagal buat asset", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //DONE: file belom ke handle jika ada penghapusan preview
            MultipartBody.Part[] parts = new MultipartBody.Part[fileImages.size()];
            for (int i = 0; i < fileImages.size(); i++) {
                File file = fileImages.get(i);
                for (int j = 0; j < imagePreviews.size(); j++) {
                    ImageHolder currImg = imagePreviews.get(j);
                    Uri imageUri = currImg.getmUri();
                    String lastpath = Uri.fromFile(file).getLastPathSegment();
                    if (imageUri != null && currImg.getmUri().getLastPathSegment().equals(lastpath)) {

                        File compressedImageFile = null;
                        try {
                            compressedImageFile = new Compressor(this).compressToFile(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        RequestBody uploadBody = RequestBody.create(MediaType.parse("image/jpeg"), compressedImageFile);
                        parts[i] = MultipartBody.Part.createFormData("photos", file.getName(), uploadBody);
                    }
                }
            }
            Call<ResponseBody> uploadPhotoReq = mApiSevice.uploadPhotos(parts);

            uploadPhotoReq.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {

                        List<Double> mListCoordinates = new ArrayList<>();
                        mListCoordinates.add(assetLocation.latitude);
                        mListCoordinates.add(assetLocation.longitude);


                        final JSONArray images = new JSONArray(response.body().string());
                        for (int i = 0; i < imagePreviews.size(); i++) {
                            ImageHolder img = imagePreviews.get(i);
                            if (img.getmImage() != null) {
                                Gson gson = new Gson();
                                String jsonImg = gson.toJson(img.getmImage());
                                images.put(new JSONObject(jsonImg));
                            }
                        }
                        JSONObject data = new JSONObject();
                        data.put("assetCategory", categoryIdSelected);
                        data.put("name", name);
                        data.put("brand", brand);
                        data.put("npwp", npwp);
                        data.put("phone", phone);
                        data.put("user", idUser);
                        data.put("rating", rating);


                        JSONObject location = new JSONObject();
                        location.put("address", address);
                        location.put("province", chosenProvince);
                        location.put("city", chosenCity);
                        location.put("postalCode", postal);
                        location.put("country", country);

                        JSONArray jsonArrayCoordinates = new JSONArray(mListCoordinates);
                        location.put("coordinates", jsonArrayCoordinates);

                        JSONObject request = new JSONObject();
                        request.put("data", data);
                        request.put("location", location);
                        request.put("images", images);

                        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), request.toString());


                        Call<ResponseBody> res = mAssetService.updateAsset(idUser, requestBody);


                        res.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                progressDialog.dismiss();
                                if (response.isSuccessful()) {
                                    finish();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                progressDialog.dismiss();
                                Toast.makeText(UpdateAsset.this, "Gagal Membuat asset", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(UpdateAsset.this, "Gagal upload foto : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_add_new_asset:
                validator.validate();
                break;
            case R.id.select_image:
                ImageUtils.takeMultiplePhotos(this,
                        CAMERA_REQUEST,
                        GALLERY_REQUEST,
                        RC_PERMS_GALLERY,
                        RC_PERMS_CAMERA);
                break;
            case R.id.btn_add_location:
                Intent mapAsset = new Intent(UpdateAsset.this, ActivityMapAsset.class);
                mapAsset.putExtra("lang", assetLocation.longitude);
                mapAsset.putExtra("lat", assetLocation.latitude);
                mapAsset.putExtra("address", mEtAssetAddress.getText().toString());
                startActivity(mapAsset);
            default:
                break;
        }

    }

    @Override
    public void onValidationSucceeded() {

        if (mRbAsset.getRating() == 0) {
            Toast.makeText(this, "Isi Rating terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        } else if (imagePreviews.size() == 0) {
            Toast.makeText(this, "Pilih foto terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        } else if (mSpProvince.getSelectedItem() == null) {
            Toast.makeText(this, "Pilih Provinsi terlebih dahulu", Toast.LENGTH_SHORT).show();
        } else if (mSpCity.getSelectedItem() == null) {
            Toast.makeText(this, "Pilih Kota terlebih dahulu", Toast.LENGTH_SHORT).show();
        }

        updateAsset();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                for (int i = 0; i < mClipData.getItemCount(); i++) {

                    ClipData.Item item = mClipData.getItemAt(i);
                    Uri uri = item.getUri();
                    File file = new File(ImageUtils.getPath(uri, this));
                    imagePreviews.add(new ImageHolder(null, Uri.fromFile(file)));
                    fileImages.add(file);

                }


            } else {
                Uri imageUri = data.getData();
                File file = new File(ImageUtils.getPath(imageUri, this));
                imagePreviews.add(new ImageHolder(null, Uri.fromFile(file)));
                fileImages.add(file);
            }


            mPreviewAdapter.notifyDataSetChanged();

        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            mPreviewAdapter.notifyDataSetChanged();
        } else if (requestCode == LOCATION_SETTING_RC && resultCode == RESULT_OK) {
            LocationSettingsStates settingsStates = LocationSettingsStates.fromIntent(data);
            if (settingsStates.isNetworkLocationUsable() && settingsStates.isGpsUsable() && settingsStates.isLocationUsable()) {
                return;
            }
        }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode == RC_PERMS_CAMERA) {
            ImageUtils.takeMultiplePhotosFromCamera(this, CAMERA_REQUEST);
        } else if (requestCode == RC_PERMS_GALLERY) {
            ImageUtils.takeMultiplePhotosFromGallery(this, GALLERY_REQUEST);
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        setStaticMap();
    }

    @Override
    protected void onDestroy() {
        if (recieve != null) {
            unregisterReceiver(recieve);
        }
        super.onDestroy();
    }

    private class ReceiverBroadcastMap extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();
            if (action != null && action.equals("sendMapCoordinates")) {
                //do something

                double lang = intent.getDoubleExtra("lang", 0);
                double lat = intent.getDoubleExtra("lat", 0);
                String address = intent.getStringExtra("address");

                mEtAssetAddress.setText(address);
                assetLocation = new LatLng(lat, lang);

//                Toast.makeText(UpdateAsset.this, "Lokasi " + intent.getDoubleExtra("lang", 0), Toast.LENGTH_LONG).show();
            }
        }
    }


}
