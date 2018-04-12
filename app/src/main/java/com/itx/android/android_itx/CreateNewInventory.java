package com.itx.android.android_itx;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.itx.android.android_itx.Adapter.PreviewAdapter;
import com.itx.android.android_itx.Entity.ImageHolder;
import com.itx.android.android_itx.Entity.Inventory;
import com.itx.android.android_itx.Service.APIService;
import com.itx.android.android_itx.Service.InventoryService;
import com.itx.android.android_itx.Utils.ApiUtils;
import com.itx.android.android_itx.Utils.ImageUtils;
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
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CreateNewInventory extends AppCompatActivity implements View.OnClickListener, Validator.ValidationListener, EasyPermissions.PermissionCallbacks {

    private static final int CAMERA_REQUEST = 1000;
    private static final int RC_PERMS_CAMERA = 13;
    private static final int RC_PERMS_GALLERY = 14;
    private static final int GALLERY_REQUEST = 1001;


    public static ArrayList<ImageHolder> imagePreviews = new ArrayList<>();
    public static ArrayList<File> fileImages = new ArrayList<>();

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

        getSupportActionBar().setTitle("Tambah Inventory");

        ButterKnife.bind(this);
        validator = new Validator(this);
        validator.setValidationListener(this);
        mBtnAddInvent.setOnClickListener(this);
        mBtnAddImage.setOnClickListener(this);

        if (fileImages.size() > 0 || imagePreviews.size() > 0) {
            fileImages.clear();
            imagePreviews.clear();
        }

        session = new SessionManager(this);
        mInventoryAPIService = ApiUtils.getListInventoryService(session.getToken());

        mEtAddPrice.addTextChangedListener(new NumberTextWatcher(mEtAddPrice, "##,###"));

        mRvPreviewImageInvent.setLayoutManager(new GridLayoutManager(this, 2));
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

        progressDialog = new ProgressDialog(CreateNewInventory.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Menyiapkan Data");
        progressDialog.show();

        prepareFacilitiesData();


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
                            Inventory invent = new Inventory();

                            checkFacilities[i] = new CheckBox(CreateNewInventory.this);
                            checkFacilities[i].setText(Data.get("name").getAsString());
                            checkFacilities[i].setTextSize(12);
                            checkFacilities[i].setId(i);
                            checkFacilities[i].setTextColor(Color.BLACK);
                            layoutFacilities.addView(checkFacilities[i]);


                            progressDialog.dismiss();


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
//                        Toast.makeText(CreateNewInventory.this, "Terdapat : " + Integer.toString(jsonArray.size()) + " data",
//                                Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(CreateNewInventory.this, "Gagal",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {

                Toast.makeText(CreateNewInventory.this, throwable.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public void uploadImagesToServer() {

        progressDialog = new ProgressDialog(CreateNewInventory.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Menyimpan Data");
        progressDialog.show();

        final String inventoryName = mEtInventName.getText().toString().trim();
        final String inventoryDescription = mEtInventDeskripsi.getText().toString().trim();
        final String inventoryStock = mEtInventStock.getText().toString().trim();
        final String inventorySpace = mEtInventSpace.getText().toString().trim();
        final String inventoryPrice = mEtAddPrice.getText().toString().trim();

        Log.d("getAllChecked", mListFacilitiesChecked.toString());

        MultipartBody.Part[] parts = new MultipartBody.Part[fileImages.size()];
        for (int i = 0; i < fileImages.size(); i++) {
            File file = fileImages.get(i);
            for (int j = 0; j < imagePreviews.size(); j++) {
                ImageHolder currImg = imagePreviews.get(j);
                String lastpath = Uri.fromFile(file).getLastPathSegment();
                if (currImg.getmUri() != null && currImg.getmUri().getLastPathSegment().equals(lastpath)) {

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
                if (response.isSuccessful()) {
                    try {

                        JSONArray images = new JSONArray(response.body().string());
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
                        saveInventoryToServer(baseObject);

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

    public void saveInventoryToServer(JSONObject jsonParams) {

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                (jsonParams).toString());

        Call<ResponseBody> addInventoryRequest = mInventoryAPIService.createInventoryCategory(body);

        addInventoryRequest.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    Log.d("Post", response.body().toString());
                    //success then send back the user to the list user and destroy this activity
//                    startActivity(new Intent(CreateNewInventory.this, ListUsers.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(CreateNewInventory.this, "Gagal Membuat Inventory", Toast.LENGTH_SHORT).show();
            }
        });
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
                    File file = new File(ImageUtils.getPath(uri,this));
                    imagePreviews.add(new ImageHolder(null, Uri.fromFile(file)));
                    fileImages.add(file);

                }
            } else {
                Uri imageUri = data.getData();
                File file = new File(ImageUtils.getPath(imageUri,this));
                imagePreviews.add(new ImageHolder(null, Uri.fromFile(file)));
                fileImages.add(file);
            }


            mPreviewAdapter.notifyDataSetChanged();

        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            mPreviewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onValidationSucceeded() {


        if (mListFacilitiesChecked.size() == 0) {
            Toast.makeText(this, "Pilih Fasilitas terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        } else if (imagePreviews.size() == 0) {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_new_inventory:
                validator.validate();
                break;
            case R.id.select_image_inventory:
                ImageUtils.takeMultiplePhotos(this,
                        CAMERA_REQUEST,
                        GALLERY_REQUEST,
                        RC_PERMS_GALLERY,
                        RC_PERMS_CAMERA);
                break;
            default:
                break;
        }

    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode == RC_PERMS_CAMERA) {
            ImageUtils.takeMultiplePhotosFromCamera(this,CAMERA_REQUEST);
        } else if (requestCode == RC_PERMS_GALLERY) {
            ImageUtils.takeMultiplePhotosFromGallery(this, GALLERY_REQUEST);
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
