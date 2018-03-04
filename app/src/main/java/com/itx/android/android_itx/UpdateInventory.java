package com.itx.android.android_itx;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.itx.android.android_itx.Adapter.PreviewAdapter;
import com.itx.android.android_itx.Entity.Image;
import com.itx.android.android_itx.Entity.ImageHolder;
import com.itx.android.android_itx.Entity.Inventory;
import com.itx.android.android_itx.Service.APIService;
import com.itx.android.android_itx.Service.InventoryService;
import com.itx.android.android_itx.Utils.ApiUtils;
import com.itx.android.android_itx.Utils.NumberTextWatcher;
import com.itx.android.android_itx.Utils.RupiahCurrency;
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
import java.util.Arrays;
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
 * Created by faisal on 3/2/18.
 */

public class UpdateInventory extends AppCompatActivity implements View.OnClickListener, Validator.ValidationListener, EasyPermissions.PermissionCallbacks {

    private static final int RC_CAMERA = 1000;
    private static final int RC_GALLERY = 1001;
    private static final int CAMERA_REQUEST = 201;


    ArrayList<ImageHolder> imagePreviews = new ArrayList<>();
    ArrayList<File> fileImages = new ArrayList<>();

    String idAsset, userAdress, userName, phone, imagesDetail, role;
    private APIService mApiSevice;
    private PreviewAdapter mPreviewAdapter;
    private List<String> mListFacilitiesChecked = new ArrayList<>();

    SessionManager session;
    InventoryService mInventoryAPIService;
    ProgressDialog progressDialog;
    Validator validator;


    @BindView(R.id.btn_add_new_inventory)
    Button mBtnAddInvent;

    @BindView(R.id.select_image_inventory)
    Button mBtnAddImage;

    @BindView(R.id.layoutFacilities)
    LinearLayout layoutFacilities;

    @BindView(R.id.rv_preview_img_new_invent)
    RecyclerView mRvPreviewImageInvent;

    @NotEmpty
    @BindView(R.id.et_add_inventory_name)
    EditText mEtInventName;

    @NotEmpty
    @BindView(R.id.et_add_inventory_deskripsi)
    EditText mEtInventDeskripsi;

    @NotEmpty
    @BindView(R.id.et_add_inventory_space)
    EditText mEtInventSpace;

    @NotEmpty
    @BindView(R.id.et_add_inventory_stock)
    EditText mEtInventStock;

    @NotEmpty
    @BindView(R.id.et_add_price)
    EditText mEtAddPrice;

    CheckBox[] checkFacilities;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_inventory);

        ButterKnife.bind(this);
        validator = new Validator(this);
        validator.setValidationListener(this);
        mBtnAddInvent.setOnClickListener(this);
        mBtnAddImage.setOnClickListener(this);

        session = new SessionManager(this);
        mInventoryAPIService = ApiUtils.getListInventoryService(session.getToken());

        mEtAddPrice.addTextChangedListener(new NumberTextWatcher(mEtAddPrice, "##,###"));

        mRvPreviewImageInvent.setLayoutManager(new GridLayoutManager(this, 2));
        mPreviewAdapter = new PreviewAdapter(imagePreviews, this, new PreviewAdapter.previewInterface() {
            @Override
            public void deleteCurrentPreviewImage(int position) {
                ImageHolder currentImage = imagePreviews.get(position);
                for (int i = 0; i < fileImages.size(); i++){
                    if(currentImage.getmUri() == null) continue;
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
        mRvPreviewImageInvent.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int space = 16;
                outRect.left = space;
                outRect.right = space;
                outRect.top = space;
            }
        });

        mRvPreviewImageInvent.setAdapter(mPreviewAdapter);
        mApiSevice = ApiUtils.getAPIService(new SessionManager(this).getToken());
        progressDialog = new ProgressDialog(UpdateInventory.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Menyiapkan Data");
        progressDialog.show();

        prepareFacilitiesData();
        prepareInventData();


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
                Uri uri = FileProvider.getUriForFile(UpdateInventory.this,
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


    public void prepareFacilitiesData() {

        Call<JsonObject> response = mInventoryAPIService.getAllFacilities();

        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> rawResponse) {
                if (rawResponse.isSuccessful()) {
                    try {
                        final JsonArray jsonArray = rawResponse.body().get("data").getAsJsonArray();
                        checkFacilities = new CheckBox[jsonArray.size()];
                        for (int i = 0; i < jsonArray.size(); i++) {

                            final JsonObject Data = jsonArray.get(i).getAsJsonObject();

                            checkFacilities[i] = new CheckBox(UpdateInventory.this);
                            checkFacilities[i].setText(Data.get("name").getAsString());
                            checkFacilities[i].setTextSize(12);
                            checkFacilities[i].setId(i);
                            checkFacilities[i].setTextColor(Color.BLACK);
                            layoutFacilities.addView(checkFacilities[i]);

                            checkFacilities[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked == true) {
                                        int getChecked = buttonView.getId();

                                        final JsonObject DataChecked = jsonArray.get(getChecked).getAsJsonObject();

                                        Log.i("checkbox", DataChecked.get("_id").getAsString());
                                        mListFacilitiesChecked.add(DataChecked.get("_id").getAsString());
                                        Log.i("checkboxList", mListFacilitiesChecked.toString());
                                    } else {
                                        int getChecked = buttonView.getId();

                                        final JsonObject DataChecked = jsonArray.get(getChecked).getAsJsonObject();

                                        Log.i("checkbox", DataChecked.get("_id").getAsString());
                                        mListFacilitiesChecked.remove(DataChecked.get("_id").getAsString());
                                        Log.i("checkboxList", mListFacilitiesChecked.toString());
                                    }
                                }
                            });

                        }
                        Toast.makeText(UpdateInventory.this, "Terdapat : " + Integer.toString(jsonArray.size()) + " data",
                                Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(UpdateInventory.this, "Gagal",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {

                Toast.makeText(UpdateInventory.this, throwable.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public void prepareInventData() {

        idAsset = getIntent().getStringExtra("id");
        mInventoryAPIService = ApiUtils.getListInventoryService(session.getToken());

        Call<JsonObject> response = mInventoryAPIService.getUserInventory(idAsset);

        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> rawResponse) {
                if (rawResponse.isSuccessful()) {
                    try {
                        JsonObject jsonObject = rawResponse.body().get("data").getAsJsonObject();
                        mEtInventName.setText(jsonObject.get("name").getAsString());
                        mEtInventDeskripsi.setText(jsonObject.get("description").getAsString());
                        mEtInventSpace.setText(jsonObject.get("space").getAsString());
                        mEtInventStock.setText(jsonObject.get("stock").getAsString());
                        mEtAddPrice.setText(jsonObject.get("price").getAsString());

                        JsonArray images = jsonObject.get("images").getAsJsonArray();
                        for(int i = 0; i < images.size(); i++){
                            Gson gson = new Gson();
                            Image image = gson.fromJson(images.get(i), Image.class);
                            imagePreviews.add(new ImageHolder(image,null));
                        }

                        final JsonArray jsonArray = jsonObject.get("facilities").getAsJsonArray();

                        if (checkFacilities != null) {
                            for (int z = 0; z < jsonArray.size(); z++) {
                                final JsonObject Data = jsonArray.get(z).getAsJsonObject();
                                Log.d("KenaBerepa", Data.get("name").getAsString());

                                for (int i = 0; i < checkFacilities.length; i++) {

                                    if (checkFacilities[i].getText().toString().equals(jsonArray.get(z).getAsJsonObject().get("name").getAsString())) {
                                        checkFacilities[i].setChecked(true);

                                    }
                                }
                            }
                        }


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
                    Toast.makeText(UpdateInventory.this, "Gagal",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {

                Toast.makeText(UpdateInventory.this, throwable.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });





    }


    public void uploadImagesToServer() {

        progressDialog = new ProgressDialog(UpdateInventory.this);
        progressDialog.setMessage("Menyimpan Data");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        final String inventoryName = mEtInventName.getText().toString().trim();
        final String inventoryDescription = mEtInventDeskripsi.getText().toString().trim();
        final String inventoryStock = mEtInventStock.getText().toString().trim();
        final String inventorySpace = mEtInventSpace.getText().toString().trim();
        final String inventoryPrice = mEtAddPrice.getText().toString().trim();

        Log.d("getAllChecked", mListFacilitiesChecked.toString());


        if(fileImages.size() < 1){
            try {
                final JSONArray images = new JSONArray();
                for (int i = 0; i < imagePreviews.size(); i++){
                    ImageHolder img = imagePreviews.get(i);
                    if(img.getmImage() != null){
                        Gson gson = new Gson();
                        String jsonImg = gson.toJson(img.getmImage());
                        images.put(new JSONObject(jsonImg));
                    }
                }
                String idAsset = getIntent().getStringExtra("idAsset");
                JSONObject objectData = new JSONObject();
                objectData.put("asset", idAsset);
                objectData.put("name", inventoryName);
                objectData.put("description", inventoryDescription);
                objectData.put("space", inventorySpace);
                objectData.put("price", RupiahCurrency.unformatRupiah(inventoryPrice));
                objectData.put("stock", inventoryStock);

                JSONArray jsonArrayChecked = new JSONArray(mListFacilitiesChecked);
                objectData.put("facilities", jsonArrayChecked);

                JSONObject baseObject = new JSONObject();
                baseObject.put("data", objectData);
                baseObject.put("images", images);
                updateInventoryToServer(baseObject);
            } catch (Exception e){
                e.printStackTrace();
            }
        } else {
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
                    if (response.isSuccessful()) {

                        try {

                            final JSONArray images = new JSONArray(response.body().string());
                            for (int i = 0; i < imagePreviews.size(); i++){
                                ImageHolder img = imagePreviews.get(i);
                                if(img.getmImage() != null){
                                    Gson gson = new Gson();
                                    String jsonImg = gson.toJson(img.getmImage());
                                    images.put(new JSONObject(jsonImg));
                                }
                            }
                            String idAsset = getIntent().getStringExtra("idAsset");
                            JSONObject objectData = new JSONObject();
                            objectData.put("asset", idAsset);
                            objectData.put("name", inventoryName);
                            objectData.put("description", inventoryDescription);
                            objectData.put("space", inventorySpace);
                            objectData.put("price", RupiahCurrency.unformatRupiah(inventoryPrice));
                            objectData.put("stock", inventoryStock);

                            JSONArray jsonArrayChecked = new JSONArray(mListFacilitiesChecked);
                            objectData.put("facilities", jsonArrayChecked);

                            JSONObject baseObject = new JSONObject();
                            baseObject.put("data", objectData);
                            baseObject.put("images", images);
                            updateInventoryToServer(baseObject);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        }
    }


    public void updateInventoryToServer(JSONObject jsonParams) {

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                (jsonParams).toString());

        Log.d("testJson", body.toString());
        Call<ResponseBody> addInventoryRequest = mInventoryAPIService.updateInventoryCategory(idAsset ,body);

        addInventoryRequest.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    Log.d("Post", response.body().toString());
                    //success then send back the user to the list user and destroy this activity
//                    Intent i = new Intent(UpdateInventory.this, ListInventory.class);
//                    i.putExtra("idAsset",idAsset);
//                    startActivity(i);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode == 13) {
            takePhotoFromCamera();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_add_new_inventory:
                validator.validate();
                break;
            case R.id.select_image_inventory:
                takePhotoWithPermission();
                break;
            default:
                break;
        }

    }

    @Override
    public void onValidationSucceeded() {

        if (mListFacilitiesChecked.size() == 0) {
            Toast.makeText(this, "Pilih Fasilitas terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (imagePreviews.size() == 0) {
            Toast.makeText(this, "Isi Foto terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Sedang Membuat Inventory", Toast.LENGTH_SHORT).show();
        uploadImagesToServer();

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
