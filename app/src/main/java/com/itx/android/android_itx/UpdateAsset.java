package com.itx.android.android_itx;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Rect;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

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
import com.itx.android.android_itx.Utils.SessionManager;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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

public class UpdateAsset extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, Validator.ValidationListener, TextWatcher {

    private final static int GALLERY_RC = 299;
    private final static int LOCATION_SETTING_RC = 122;
    final AutoCompleteUtils completeUtils = new AutoCompleteUtils(this);

    //URI List for the images that will be on the server
    ArrayList<ImageHolder> imagePreviews = new ArrayList<>();
    ArrayList<File> fileImages = new ArrayList<>();
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

    @NotEmpty
    @BindView(R.id.et_add_asset_province)
    AutoCompleteTextView mAcAssetProvince;

    @NotEmpty
    @BindView(R.id.et_add_asset_city)
    AutoCompleteTextView mAcAssetCity;

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

    private AssetsAdapter mAdapter;
    String idUser, userAdress, userName, phone, imagesDetail, role;
    AssetService mAssetAPIService;
    SessionManager session;
    ProgressDialog progressDialog;


    private static final int CAMERA_REQUEST = 201;
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
        progressDialog.setMessage("Sedang Menyiapkan Data");
        progressDialog.show();

        idUser = getIntent().getStringExtra("idUser");
        userAdress = getIntent().getStringExtra("address");
        userName = getIntent().getStringExtra("name");
        phone = getIntent().getStringExtra("phone");
        imagesDetail = getIntent().getStringExtra("photo");
        role = getIntent().getStringExtra("role");


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.asset_map);
        mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        // ask user to on the GPS when the activity is created
        if (EasyPermissions.hasPermissions(this, locationPerm)) {
            askUserToTurnOnGPS();
        } else {
            EasyPermissions.requestPermissions(this, "Izinkan aplikasi untuk mengakses lokasi anda", LOCATION_REQUEST, locationPerm);
        }
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
                for (int i = 0; i < fileImages.size(); i++){
                    String fileName = Uri.fromFile(fileImages.get(i)).getLastPathSegment();
                    String currentUriName = currentImage.getmUri().getLastPathSegment();
                    if(currentImage.getmUri() != null && fileName.equals(currentUriName)){
                        fileImages.remove(i);
                    }
                }
                imagePreviews.remove(position);
                Log.d("DATA IMAGE", "uri ada : " + imagePreviews.size() + " and files :" + fileImages.size());
                mPreviewAdapter.notifyDataSetChanged();
            }
        });
        mRvPreviewImageAsset.setAdapter(mPreviewAdapter);

        prepareAssetCategories();
        setAutoComplete();
    }

    private void askUserToTurnOnGPS() {
        try {
            GoogleApiClient
                    googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .build();

            googleApiClient.connect();
            LocationRequest request = new LocationRequest();
            request.setInterval(1000000)
                    .setFastestInterval(500000)
                    .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(request);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can initialize location
                            // requests here.
                            if (state.isGpsUsable() && state.isLocationUsable() && state.isNetworkLocationUsable()) {
                                return;
                            }
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(
                                        UpdateAsset.this, LOCATION_SETTING_RC);
                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        } catch (Exception exception) {
            // Log exception
        }
    }

    public void setAutoComplete() {

        ArrayAdapter<String> adapterProvince = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, completeUtils.getArrayProvicesJson());

        String country[] = {"Indonesia"};

        ArrayAdapter<String> adapterCountry = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, country);

        mAcAssetProvince.setAdapter(adapterProvince);
        mAcAssetProvince.setThreshold(1);
        mAcAssetCity.setThreshold(1);

        mAcAssetCity.addTextChangedListener(this);


        mBtnAddAsset.setOnClickListener(this);
        mBtnAddImages.setOnClickListener(this);

        mAcAssetCountry.setAdapter(adapterCountry);
        mAcAssetCountry.setThreshold(1);
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
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {


            }
        });

        prepareAsset();
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
                        mAcAssetCity.setText(jsonObject.get("address").getAsJsonObject().get("city").getAsString());
                        mAcAssetCountry.setText(jsonObject.get("address").getAsJsonObject().get("country").getAsString());
                        mAcAssetProvince.setText(jsonObject.get("address").getAsJsonObject().get("province").getAsString());
                        JsonArray images = jsonObject.get("images").getAsJsonArray();
                        for(int i = 0; i < images.size(); i++){
                            Gson gson = new Gson();
                            Image image = gson.fromJson(images.get(i), Image.class);
                            imagePreviews.add(new ImageHolder(image,null));
                        }

                        mRbAsset.setRating(jsonObject.get("rating").getAsFloat());
                        String assetCategory = jsonObject.get("assetCategory").getAsJsonObject().get("name").getAsString();


                        int selectionPosition = spAdapter.getPosition(assetCategory);
                        categoryIdSelected = categories.get(selectionPosition);
                        mSpCategories.setSelection(selectionPosition);
                        JsonArray AssetCoordinates = jsonObject.get("address").getAsJsonObject().get("coordinates").getAsJsonArray();

                        assetLocation = new LatLng(AssetCoordinates.get(0).getAsDouble(), AssetCoordinates.get(1).getAsDouble());

                        getAddressByLocation(assetLocation.latitude, assetLocation.longitude);
                        updateMapUI();

                        new CountDownTimer(1000, 1000) {

                            public void onTick(long millisUntilFinished) {
                                // You don't need anything here
                            }
                            public void onFinish() {
                                progressDialog.dismiss();


                            }
                        }.start();


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


    private void updateAsset() {

        final String name = mEtAssetName.getText().toString().trim();
        final String brand = mEtAssetBrand.getText().toString().trim();
        final String npwp = mEtAssetNPWP.getText().toString().trim();
        final String phone = mEtAssetPhone.getText().toString().trim();
        final String address = mEtAssetAddress.getText().toString().trim();
        final String province = mAcAssetProvince.getText().toString().trim();
        final String city = mAcAssetCity.getText().toString().trim();
        final String postal = mEtAssetPostal.getText().toString().trim();
        final String country = mAcAssetCountry.getText().toString().trim();
        final int rating = Math.round(mRbAsset.getRating());

<<<<<<< HEAD
        MultipartBody.Part[] parts = new MultipartBody.Part[fileImages.size()];
        for (int i = 0; i < fileImages.size(); i++) {
            File file = fileImages.get(i);
            RequestBody uploadBody = RequestBody.create(MediaType.parse(getContentResolver().getType(uriImages.get(i))), file);
            parts[i] = MultipartBody.Part.createFormData("photos", file.getName(), uploadBody);
        }
//        Call<ResponseBody> uploadPhotoReq = mApiSevice.uploadPhotos(parts);
//        uploadPhotoReq.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {

                    List<Double> mListCoordinates = new ArrayList<>();
                    mListCoordinates.add(assetLocation.latitude);
                    mListCoordinates.add(assetLocation.longitude);

                    Log.d("checkcoordinates", mListCoordinates.toString());

//                    final JSONArray images = new JSONArray(response.body().string());
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
                    location.put("province", province);
                    location.put("city", city);
                    location.put("postalCode", postal);
                    location.put("country", country);

                    JSONArray jsonArrayCoordinates = new JSONArray(mListCoordinates);
                    location.put("coordinates", jsonArrayCoordinates);

                    JSONObject request = new JSONObject();
                    request.put("data", data);
                    request.put("location", location);
//                    request.put("images", images);

                    RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), request.toString());

                    Call<ResponseBody> res = mAssetService.updateAsset(idUser, requestBody);
                    res.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {


                                Intent listUser = new Intent(UpdateAsset.this, ListUsers.class);
                                startActivity(listUser);
                                finish();
=======
        if (fileImages.size() < 1){
            try {

                List<Double> mListCoordinates = new ArrayList<>();
                mListCoordinates.add(assetLocation.latitude);
                mListCoordinates.add(assetLocation.longitude);


                final JSONArray images = new JSONArray();
                for (int i = 0; i < imagePreviews.size(); i++){
                    ImageHolder img = imagePreviews.get(i);
                    if(img.getmImage() != null){
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
                location.put("province", province);
                location.put("city", city);
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
                        if (response.isSuccessful()) {


                            Intent listAsset = new Intent(UpdateAsset.this, ListAssets.class);
                            startActivity(listAsset);
                            finish();
>>>>>>> 51f95ba2e07b13ee855efab6414dd5762c45473f


                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
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
                for(int j = 0; j < imagePreviews.size(); j++){
                    ImageHolder currImg = imagePreviews.get(j);
                    String lastpath = Uri.fromFile(file).getLastPathSegment();
                    if(currImg.getmUri() != null && currImg.getmUri().getLastPathSegment().equals(lastpath)){
                        RequestBody uploadBody = RequestBody.create(MediaType.parse(getContentResolver().getType(currImg.getmUri())), file);
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
                        for (int i = 0; i < imagePreviews.size(); i++){
                            ImageHolder img = imagePreviews.get(i);
                            if(img.getmImage() != null){
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
                        location.put("province", province);
                        location.put("city", city);
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
                                if (response.isSuccessful()) {


                                    Intent listAsset = new Intent(UpdateAsset.this, ListAssets.class);
                                    startActivity(listAsset);
                                    finish();


                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Toast.makeText(UpdateAsset.this, "Gagal buat asset", Toast.LENGTH_SHORT).show();
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


    private void updateMapUI() {
        if (mMap != null && mAssetMarker != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(assetLocation, 14.0f));
            mAssetMarker.setPosition(assetLocation);
            mEtAssetAddress.setText(mAddress);
        }
    }

    private void takePhotoFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            try {
                fileImages.add(createImageFile());
            } catch (IOException ex) {
                // Error occurred while creating the File
                return;
            }
            // Continue only if the File was successfully created
            if (fileImages.get(fileImages.size() - 1) != null) {
                Uri uri = FileProvider.getUriForFile(UpdateAsset.this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        fileImages.get(fileImages.size() - 1));
                imagePreviews.add(new ImageHolder(null, uri));
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imagePreviews.get(imagePreviews.size() - 1).getmUri());
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    @AfterPermissionGranted(13)
    private void takePhotoWithPermission() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            takePhotoFromCamera();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "Izinkan aplikasi untuk akses kamera dan storage",
                    13, perms);
        }
    }

    private void getCurrentLocation() {

        try {
            Task<Location> result = mFusedLocationClient.getLastLocation();
            result.addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        Log.d("currlocation", "berhasil :" + location.getLongitude());
                        assetLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        getAddressByLocation(location.getLatitude(), location.getLongitude());
                    }
                }
            });
        } catch (SecurityException e) {

            e.printStackTrace();
        }
    }

    private void getAddressByLocation(double lat, double lng) {
        String formattedLangLat = Double.toString(lat) + "," + Double.toString(lng);
        Call<JsonObject> geoRequest = mApiSevice.reverseGeocode(formattedLangLat, getString(R.string.geocode_key));
        geoRequest.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonArray results = response.body().getAsJsonArray("results");
                JsonObject firstAddress = results.get(0).getAsJsonObject();
                mAddress = firstAddress.get("formatted_address").getAsString();
                updateMapUI();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }


    @AfterPermissionGranted(12)
    private void getCurrentLocationWithPermission() {

        String[] locPerms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (EasyPermissions.hasPermissions(this, locPerms)) {
            getCurrentLocation();

        } else {
            EasyPermissions.requestPermissions(this, "Izinkan aplikasi untuk mengakses lokasi anda", 12, locPerms);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 13) {
            takePhotoFromCamera();
        } else if (requestCode == 12) {
            getCurrentLocation();
        } else if (requestCode == LOCATION_REQUEST) {
            mMap.setMyLocationEnabled(true);
        }

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        ArrayAdapter<String> adapterCity = new ArrayAdapter<String>
                (UpdateAsset.this, android.R.layout.select_dialog_item, completeUtils.getArrayCityJson(mAcAssetProvince.getText().toString()));
        mAcAssetCity.setAdapter(adapterCity);
        mAcAssetCity.setThreshold(1);

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_add_new_asset:
                validator.validate();
                break;
            case R.id.select_image:
                takePhotoWithPermission();
                break;
            default:
                break;
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setIndoorEnabled(true);


        if (EasyPermissions.hasPermissions(this, locationPerm)) {
            mMap.setMyLocationEnabled(true);
        } else {
            EasyPermissions.requestPermissions(this, "Izinkan aplikasi untuk mengakses lokasi anda", LOCATION_REQUEST, locationPerm);
        }


        mAssetMarkerOptions = new MarkerOptions();
        mAssetMarkerOptions.position(assetLocation);
        mAssetMarkerOptions.draggable(true);
        mAssetMarkerOptions.title("Lokasi Asset");
        mAssetMarker = mMap.addMarker(mAssetMarkerOptions);
        mAssetMarker.showInfoWindow();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(assetLocation, 14.0f));


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {


                mMap.getUiSettings().setMapToolbarEnabled(true);
                // return true will prevent any further map action from happening
                return false;

            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                Log.d("new loc click", point.toString());
                assetLocation = point;
                Log.d("new loc asset", assetLocation.toString());
                Toast.makeText(UpdateAsset.this,assetLocation.toString(), Toast.LENGTH_SHORT).show();
                getAddressByLocation(assetLocation.latitude, assetLocation.longitude);
            }
        });

    }

    @Override
    public void onValidationSucceeded() {

        if (mRbAsset.getRating() == 0) {
            Toast.makeText(this, "Isi Rating terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        } else if (imagePreviews.size() == 0) {
            Toast.makeText(this, "Pilih foto terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Sedang Membuat Asset", Toast.LENGTH_SHORT).show();
        updateAsset();

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
}
