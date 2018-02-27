package com.itx.android.android_itx;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.itx.android.android_itx.Adapter.PreviewAdapter;
import com.itx.android.android_itx.Service.APIService;
import com.itx.android.android_itx.Service.ListAssetService;
import com.itx.android.android_itx.Utils.ApiUtils;
import com.itx.android.android_itx.Utils.GetDataJson;
import com.itx.android.android_itx.Utils.SessionManager;
import com.itx.android.android_itx.Utils.SuggestionAdapter;

import org.json.JSONArray;
import org.json.JSONException;
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

public class CreateNewAsset extends AppCompatActivity {

    private final static int GALLERY_RC = 299;

    private String idUser;

    //URI List for the images that will be on the server
    ArrayList<Uri> uriImages = new ArrayList<>();
    ArrayList<File> fileImages = new ArrayList<>();
    ArrayList<String> categories = new ArrayList<>();

    private ListAssetService mListAssetService;
    private String categoryIdSelected;
    private APIService mApiSevice;

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
    EditText mEtAssetCity;
    @BindView(R.id.et_add_asset_postal)
    EditText mEtAssetPostal;
    @BindView(R.id.et_add_asset_country)
    EditText mEtAssetCountry;
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

    ArrayAdapter<String> adapter;


    private static final int CAMERA_REQUEST = 201;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_asset);

        ButterKnife.bind(this);

        idUser = getIntent().getStringExtra("idUser");
        mSpCategories.setVisibility(View.GONE);
        spAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        mSpCategories.setAdapter(spAdapter);
        mListAssetService = ApiUtils.getListAssetsService(new SessionManager(this).getToken());

        mApiSevice = ApiUtils.getAPIService(new SessionManager(this).getToken());
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

//        String[] fruits = {"Apple", "Banana", "Cherry", "Date", "Grape", "Kiwi", "Mango", "Pear"};


        ArrayList<String> arrList = new ArrayList<String>();

        String dataJson = new GetDataJson(this).loadJSONFromAsset();


        JsonArray gson = new JsonParser().parse(dataJson).getAsJsonObject().get("features").getAsJsonArray();





        for (int i = 0; i < gson.size(); i++) {
            String nameProvice =gson.get(i).getAsJsonObject().get("properties").getAsJsonObject().get("name").getAsString();
            arrList.add(nameProvice);
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, arrList);

            mAcAssetProvince.setAdapter(adapter);
            mAcAssetProvince.setThreshold(1);



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
//        final String province = mEtAssetProvince.getText().toString().trim();
        final String city = mEtAssetCity.getText().toString().trim();
        final String postal = mEtAssetPostal.getText().toString().trim();
        final String country = mEtAssetCountry.getText().toString().trim();
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
                    JSONArray images = new JSONArray(response.body().string());
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
//                    location.put("province", province);
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
}
