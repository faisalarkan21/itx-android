package com.itx.android.android_itx;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.itx.android.android_itx.Adapter.PreviewAdapter;
import com.itx.android.android_itx.Entity.Address;
import com.itx.android.android_itx.Entity.Asset;
import com.itx.android.android_itx.Entity.AssetCategory;
import com.itx.android.android_itx.Entity.Image;
import com.itx.android.android_itx.Entity.ImageHolder;
import com.itx.android.android_itx.Entity.Request;
import com.itx.android.android_itx.Entity.Response;
import com.itx.android.android_itx.Entity.User;
import com.itx.android.android_itx.Service.APIService;
import com.itx.android.android_itx.Service.AssetService;
import com.itx.android.android_itx.Utils.ApiUtils;
import com.itx.android.android_itx.Utils.AutoCompleteUtils;
import com.itx.android.android_itx.Utils.Helper;
import com.itx.android.android_itx.Utils.ImageUtils;
import com.itx.android.android_itx.Utils.SessionManager;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

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
    Validator validator;
    private LatLng assetLocation = new LatLng(-0.8784862, 113.6934566);

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

    @BindView(R.id.sv_new_asset)
    ScrollView scrollView;

    SessionManager session;
    ProgressDialog progressDialog;

    private Asset mAsset;
    private List<Image> mImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_asset);

        getSupportActionBar().setTitle("Ubah Asset");

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

        Gson gson = new Gson();
        mAsset = gson.fromJson(getIntent().getStringExtra("DATA"), Asset.class);

        if (fileImages.size() > 0 || imagePreviews.size() > 0) {
            fileImages.clear();
            imagePreviews.clear();
        }
        mBtnAddImages.setOnClickListener(this);
        mBtnAddAsset.setText("SIMPAN");
        mBtnAddAsset.setOnClickListener(this);
        staticMap.setOnClickListener(this);

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

                try {
                    mImages.remove(position);
                }catch (Exception e){
                    e.printStackTrace();
                }
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
        Call<com.itx.android.android_itx.Entity.Response.GetAssetCategory> categoriesRequest = mAssetService.getAssetCategories();
        categoriesRequest.enqueue(new Callback<com.itx.android.android_itx.Entity.Response.GetAssetCategory>() {
            @Override
            public void onResponse(Call<com.itx.android.android_itx.Entity.Response.GetAssetCategory> call, retrofit2.Response<com.itx.android.android_itx.Entity.Response.GetAssetCategory> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().getCode() == 200) {
                        for (int i = 0; i < response.body().getData().getAssetCategory().size(); i++) {
                            AssetCategory category = response.body().getData().getAssetCategory().get(i);
                            spAdapter.add(category.getName());
                            categories.add(category.getId());
                        }
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
                        prepareAsset();
                    } else {
                        Toast.makeText(UpdateAsset.this, response.body().getStatus().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UpdateAsset.this, "Gagal unduh category", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.itx.android.android_itx.Entity.Response.GetAssetCategory> call, Throwable t) {
                Toast.makeText(UpdateAsset.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void prepareAsset() {
        mEtAssetName.setText(mAsset.getName());
        mEtAssetBrand.setText(mAsset.getBrand());
        mEtAssetNPWP.setText(mAsset.getNpwp());
        mEtAssetPhone.setText(mAsset.getPhone());
        mEtAssetPostal.setText(mAsset.getAddress().getPostalCode());
        mAcAssetCountry.setText(mAsset.getAddress().getCountry());
        mImages.addAll(mAsset.getImages());

        for (int i = 0; i < mAsset.getImages().size(); i++) {
            Image image = mAsset.getImages().get(i);
            imagePreviews.add(new ImageHolder(image, null));
        }
        mPreviewAdapter.notifyDataSetChanged();

        if(mAsset.getImages().size() > 0){
            mBtnAddImages.setText("TAMBAH FOTO");
        }

        mRbAsset.setRating((float) mAsset.getRating());

        String selectedValueProv = mAsset.getAddress().getProvince();
        setProvince(selectedValueProv);

        int selectionPositionProv = spAdapterProvince.getPosition(selectedValueProv);
        mSpProvince.setSelection(selectionPositionProv);
        setCitybyProvince(selectedValueProv);

        int selectionPositionCity = spAdapterCity.getPosition(mAsset.getAddress().getCity());
        mSpCity.setSelection(selectionPositionCity);

        String assetCategory = mAsset.getAssetCategory().getName();
        int selectionPosition = spAdapter.getPosition(assetCategory);
        categoryIdSelected = categories.get(selectionPosition);
        mSpCategories.setSelection(selectionPosition);
        assetLocation = new LatLng(mAsset.getAddress().getCoordinates().get(0), mAsset.getAddress().getCoordinates().get(1));
        mEtAssetAddress.setText(mAsset.getAddress().getAddress());
        setStaticMap();
    }

    private void setStaticMap() {
        // check if address editText was null make it zoom level 4
        // for init map get full indonesia map
        if (mEtAssetAddress.getText().toString().isEmpty()) {
            Glide.with(UpdateAsset.this)
                    .load(Helper.getStaticMapWithoutMarker(UpdateAsset.this, assetLocation.latitude, assetLocation.longitude, 4))
                    .into(staticMap);
        } else {
            Glide.with(UpdateAsset.this)
                    .load(Helper.getStaticMapWithMarker(UpdateAsset.this, assetLocation.latitude, assetLocation.longitude, 18))
                    .into(staticMap);
        }

        progressDialog.dismiss();
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

        final Asset newAsset = new Asset();
        newAsset.setName(name);
        newAsset.setBrand(brand);
        newAsset.setNpwp(npwp);
        newAsset.setPhone(phone);

        Address newAddress = new Address();
        newAddress.setAddress(address);
        newAddress.setPostalCode(postal);
        newAddress.setCountry("Indonesia");
        newAddress.setCity(mSpCity.getSelectedItem().toString());
        newAddress.setProvince(mSpProvince.getSelectedItem().toString());

        List<Double> mListCoordinates = new ArrayList<>();
        mListCoordinates.add(assetLocation.latitude);
        mListCoordinates.add(assetLocation.longitude);
        newAddress.setCoordinates(mListCoordinates);
        newAsset.setAddress(newAddress);

        AssetCategory category = new AssetCategory();
        category.setId("5a8acddd0478dc160241c358");
        category.setName("Homestay");
        newAsset.setAssetCategory(category);

        newAsset.setUser(mAsset.getUser());
        newAsset.setRating(1);

        if (fileImages.size() < 1) {
            //INI JIKA USER TDK MENAMBAHKAN FOTO BARU DARI CAMERA/GALLERY
            newAsset.setImages(mImages);
            callUpdateAsset(newAsset);
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
            Call<Response.UploadResponse> uploadPhotoReq = mApiSevice.uploadPhotos(parts);
            uploadPhotoReq.enqueue(new Callback<Response.UploadResponse>() {
                @Override
                public void onResponse(Call<Response.UploadResponse> call, retrofit2.Response<Response.UploadResponse> response) {
                    if(response.isSuccessful()){
                        if(response.body().getStatus().getCode() == 200){
                            mImages.addAll(response.body().getData().getImage());
                            newAsset.setImages(mImages);
                            callUpdateAsset(newAsset);
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(UpdateAsset.this, response.body().getStatus().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(UpdateAsset.this, "Gagal unggah foto", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Response.UploadResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(UpdateAsset.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void callUpdateAsset(Asset newAsset){
        Request.UpdateAsset requestAsset = new Request(). new UpdateAsset();
        requestAsset.setAsset(newAsset);

        Call<Response.UpdateAsset> call = mAssetService.updateAsset(mAsset.getId(), requestAsset);
        call.enqueue(new Callback<Response.UpdateAsset>() {
            @Override
            public void onResponse(Call<Response.UpdateAsset> call, retrofit2.Response<Response.UpdateAsset> response) {
                progressDialog.dismiss();
                if(response.isSuccessful()){
                    if(response.body().getStatus().getCode() == 200){
                        Toast.makeText(UpdateAsset.this, "Asset berhasil dirubah", Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        Toast.makeText(UpdateAsset.this, response.body().getStatus().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(UpdateAsset.this, "Gagal merubah asset", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response.UpdateAsset> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(UpdateAsset.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
            case R.id.static_map:
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

//        if (mRbAsset.getRating() == 0) {
//            Toast.makeText(this, "Isi Rating terlebih dahulu", Toast.LENGTH_SHORT).show();
//            return;
//        } else
            if (imagePreviews.size() == 0) {
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

            if(fileImages.size() > 0){
                mBtnAddImages.setText("TAMBAH FOTO");
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

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
