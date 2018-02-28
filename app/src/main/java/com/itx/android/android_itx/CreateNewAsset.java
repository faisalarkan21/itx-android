package com.itx.android.android_itx;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Rect;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itx.android.android_itx.Adapter.PreviewAdapter;
import com.itx.android.android_itx.Service.APIService;
import com.itx.android.android_itx.Service.ListAssetService;
import com.itx.android.android_itx.Utils.ApiUtils;
import com.itx.android.android_itx.Utils.AutoCompleteUtils;
import com.itx.android.android_itx.Utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

public class CreateNewAsset extends AppCompatActivity implements OnMapReadyCallback {

    private final static int GALLERY_RC = 299;

    String idUser,userAdress,userName,phone,role,imagesDetail ;


    //URI List for the images that will be on the server
    ArrayList<Uri> uriImages = new ArrayList<>();
    ArrayList<File> fileImages = new ArrayList<>();
    ArrayList<String> categories = new ArrayList<>();

    private ListAssetService mListAssetService;
    private String categoryIdSelected;
    private APIService mApiSevice;

    private String mAddress = "";

    private FusedLocationProviderClient mFusedLocationClient;

    private GoogleMap mMap;
    private Marker mAssetMarker;
    private MarkerOptions mAssetMarkerOptions;

    private LatLng assetLocation = new LatLng(-7.348868,108.535240);

    private PreviewAdapter mPreviewAdapter;

    ArrayAdapter<String> spAdapter;

    @BindView(R.id.et_add_asset_name)
    EditText mEtAssetName;
    @BindView(R.id.et_add_asset_brand)
    EditText mEtAssetBrand;
    @BindView(R.id.et_add_asset_npwp)
    EditText mEtAssetNPWP;
    @BindView(R.id.et_add_asset_phone)
    EditText mEtAssetPhone;
    @BindView(R.id.et_add_asset_address)
    EditText mEtAssetAddress;
    @BindView(R.id.et_add_asset_province)
    AutoCompleteTextView mAcAssetProvince;
    @BindView(R.id.et_add_asset_city)
    AutoCompleteTextView mAcAssetCity;
    @BindView(R.id.et_add_asset_postal)
    EditText mEtAssetPostal;
    @BindView(R.id.et_add_asset_country)
    AutoCompleteTextView mAcAssetCountry;
    @BindView(R.id.rv_preview_img_new_asset)
    RecyclerView mRvPreviewImageAsset;
    @BindView(R.id.sp_add_asset_categories)
    Spinner mSpCategories;
    @BindView(R.id.rb_new_asset)
    RatingBar mRbAsset;
    @BindView(R.id.btn_add_new_asset)
    Button mBtnAddAsset;

    @BindView(R.id.select_image)
    Button mBtnAddImages;



    private static final int CAMERA_REQUEST = 201;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_asset);

        ButterKnife.bind(this);

        mApiSevice = ApiUtils.getAPIService(new SessionManager(this).getToken());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.asset_map);
        mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        idUser = getIntent().getStringExtra("idUser");
        userAdress = getIntent().getStringExtra("address");
        userName = getIntent().getStringExtra("name");
        phone = getIntent().getStringExtra("phone");
        imagesDetail = getIntent().getStringExtra("photo");
        role = getIntent().getStringExtra("role");
        mSpCategories.setVisibility(View.GONE);
        spAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        mSpCategories.setAdapter(spAdapter);
        mListAssetService = ApiUtils.getListAssetsService(new SessionManager(this).getToken());


        prepareAssetCategories();
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
        mPreviewAdapter = new PreviewAdapter(uriImages, this);
        mRvPreviewImageAsset.setAdapter(mPreviewAdapter);
        mBtnAddImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhotoWithPermission();
            }
        });

        final AutoCompleteUtils completeUtils = new AutoCompleteUtils(this);

        ArrayAdapter<String> adapterProvince = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, completeUtils.getArrayProvicesJson());

        String country[] = {"Indonesia"};

        ArrayAdapter<String> adapterCountry = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, country);


        mAcAssetProvince.setAdapter(adapterProvince);
        mAcAssetProvince.setThreshold(1);
        mAcAssetCity.setThreshold(1);

        mAcAssetCity.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                ArrayAdapter<String> adapterCity = new ArrayAdapter<String>
                        (CreateNewAsset.this, android.R.layout.select_dialog_item, completeUtils.getArrayCityJson(mAcAssetProvince.getText().toString()));
                mAcAssetCity.setAdapter(adapterCity);
                mAcAssetCity.setThreshold(1);

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mAcAssetCountry.setAdapter(adapterCountry);
        mAcAssetCountry.setThreshold(1);

        mBtnAddAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAsset();
            }
        });

    }

    private void takePhoto() {
        //show dialog for user to choose between camera or gallery
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Pilih Foto");
        alertBuilder.setMessage("Ambil foto dari ?");
        alertBuilder.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                takePhotoWithPermission();
            }
        });
        alertBuilder.setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                pickImagesFromGallery();
            }
        });
        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    private void pickImagesFromGallery() {
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
        openGalleryIntent.setType("image/*");
        openGalleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        openGalleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        openGalleryIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(Intent.createChooser(openGalleryIntent, "Select Picture"), GALLERY_RC);
    }

    private void prepareAssetCategories() {
//        Toast.makeText(this, idUser, Toast.LENGTH_SHORT).show();
        Call<JsonObject> categoriesRequest = mListAssetService.getAssetCategories();
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
                uriImages.add(FileProvider.getUriForFile(CreateNewAsset.this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        fileImages.get(fileImages.size() - 1)));
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriImages.get(uriImages.size() - 1));
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        }
    }

    private void getCurrentLocation(){

        try {
            Task<Location> result = mFusedLocationClient.getLastLocation();
            result.addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        Log.d("currlocation", "berhasil :" + location.getLongitude());
                        assetLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        getAddressByLocation(location.getLatitude(),location.getLongitude());
                        updateMapUI();
                    }
                }
            });
        }catch (SecurityException e){

            e.printStackTrace();
        }
    }

    private void getAddressByLocation(double lat, double lng){
         Call<JsonObject> geoRequest = mApiSevice.reverseGeocode(lat,lng, getString(R.string.geocode_key));
         geoRequest.enqueue(new Callback<JsonObject>() {
             @Override
             public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                 JsonArray results = response.body().getAsJsonArray("results");
                 JsonObject firstAddress = results.get(0).getAsJsonObject();
                 mAddress = firstAddress.get("formatted_address").getAsString();
             }

             @Override
             public void onFailure(Call<JsonObject> call, Throwable t) {

             }
         });
    }

    private void updateMapUI(){
        if(mMap != null && mAssetMarker != null){
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(assetLocation,12.0f));
            mAssetMarker.setPosition(assetLocation);
            mEtAssetAddress.setText(mAddress);
        }
    }

    @AfterPermissionGranted(12)
    private void getCurrentLocationWithPermission(){

        String[] locPerms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if(EasyPermissions.hasPermissions(this,locPerms)){
            getCurrentLocation();

        } else {
            EasyPermissions.requestPermissions(this,"Izinkan aplikasi untuk mengakses lokasi anda",12,locPerms);
        }
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

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null,
                null, null);
        if (cursor == null)
            return null;
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        if (cursor.moveToFirst()) {
            String s = cursor.getString(column_index);
            // cursor.close();
            return s;
        }
        // cursor.close();
        return null;
    }

    private void createAsset() {

        if (categoryIdSelected == null) {
            Toast.makeText(this, "Pilih kategorinya", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Sedang membuat asset", Toast.LENGTH_SHORT).show();


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

        MultipartBody.Part[] parts = new MultipartBody.Part[fileImages.size()];
        for (int i = 0; i < fileImages.size(); i++) {
            File file = fileImages.get(i);
            RequestBody uploadBody = RequestBody.create(MediaType.parse(getContentResolver().getType(uriImages.get(i))), file);
            parts[i] = MultipartBody.Part.createFormData("photos", file.getName(), uploadBody);
        }
        Call<ResponseBody> uploadPhotoReq = mApiSevice.uploadPhotos(parts);
        uploadPhotoReq.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    final JSONArray images = new JSONArray(response.body().string());
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

                    JSONObject request = new JSONObject();
                    request.put("data", data);
                    request.put("location", location);
                    request.put("images", images);

                    RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), request.toString());

                    Call<ResponseBody> res = mListAssetService.createAsset(requestBody);
                    res.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                Intent i = new Intent(CreateNewAsset.this, ListAssets.class);
                                i.putExtra("id", idUser);
                                i.putExtra("idUser", idUser);
                                i.putExtra("name", userName);
                                i.putExtra("address", userAdress);
                                i.putExtra("phone", phone);
                                i.putExtra("photo", imagesDetail);
                                i.putExtra("role", role);
                                startActivity(i);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(CreateNewAsset.this, "Gagal buat asset", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(CreateNewAsset.this, "Gagal upload foto : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 13) {
            takePhotoFromCamera();
        } else if (requestCode == 12){
            getCurrentLocation();
        }

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_RC && resultCode == Activity.RESULT_OK) {
            if (data.getData() != null) {

                Uri imageUri = data.getData();
                uriImages.add(imageUri);
                fileImages.add(new File(imageUri.getEncodedPath()));


            } else {
                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    Log.d("TEST!23", mClipData.getItemCount() + " jumlah");
                    for (int i = 0; i < mClipData.getItemCount(); i++) {

                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri uri = item.getUri();
                        uriImages.add(uri);
                        fileImages.add(new File(getPath(uri)));

                    }
                }
            }

            mPreviewAdapter.notifyDataSetChanged();

            Toast.makeText(this, "images : " + fileImages.size(), Toast.LENGTH_SHORT).show();
        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            mPreviewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setIndoorEnabled(true);

        mAssetMarkerOptions = new MarkerOptions();
        mAssetMarkerOptions.position(assetLocation);
        mAssetMarkerOptions.draggable(true);
        mAssetMarkerOptions.title("Lokasi Asset");
        mAssetMarker = mMap.addMarker(mAssetMarkerOptions);

        mAssetMarker.showInfoWindow();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(assetLocation,12.0f));

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                assetLocation = marker.getPosition();
                updateMapUI();
                Toast.makeText(CreateNewAsset.this, "new Location :" + assetLocation, Toast.LENGTH_SHORT).show();
            }
        });

        getCurrentLocationWithPermission();
    }
}
