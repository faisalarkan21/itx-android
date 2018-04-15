package com.itx.android.android_itx;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.itx.android.android_itx.Adapter.PreviewAdapter;
import com.itx.android.android_itx.Entity.Asset;
import com.itx.android.android_itx.Entity.Facility;
import com.itx.android.android_itx.Entity.ImageHolder;
import com.itx.android.android_itx.Entity.InventoryCategory;
import com.itx.android.android_itx.Entity.Request;
import com.itx.android.android_itx.Entity.Response;
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

public class CreateNewInventory extends AppCompatActivity implements View.OnClickListener, Validator.ValidationListener, EasyPermissions.PermissionCallbacks {

    private static final int CAMERA_REQUEST = 1000;
    private static final int RC_PERMS_CAMERA = 13;
    private static final int RC_PERMS_GALLERY = 14;
    private static final int GALLERY_REQUEST = 1001;


    public static ArrayList<ImageHolder> imagePreviews = new ArrayList<>();
    public static ArrayList<File> fileImages = new ArrayList<>();

    private APIService mApiSevice;
    private PreviewAdapter mPreviewAdapter;
    private List<Facility> mListFacilitiesChecked = new ArrayList<>();

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
    private List<Facility> facilities = new ArrayList<>();
    private Asset mAsset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_inventory);

        getSupportActionBar().setTitle("Tambah Inventory");

        Gson gson = new Gson();
        mAsset = gson.fromJson(getIntent().getStringExtra("DATA"), Asset.class);

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
                    if (currentImage.getmUri() != null && fileName.equals(currentUriName)) {
                        fileImages.remove(i);
                    }
                }
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
        Call<Response.GetFacillity> response = mInventoryAPIService.getAllFacilities();
        response.enqueue(new Callback<Response.GetFacillity>() {
            @Override
            public void onResponse(Call<Response.GetFacillity> call, retrofit2.Response<Response.GetFacillity> response) {
                if(response.isSuccessful()){
                    if(response.body().getStatus().getCode() == 200){
                        facilities.addAll(response.body().getData().getFacility());
                        checkFacilities = new CheckBox[facilities.size()];
                        for (int i = 0; i < facilities.size(); i++) {

                            final Facility facility = facilities.get(i);
                            checkFacilities[i] = new CheckBox(CreateNewInventory.this);
                            checkFacilities[i].setText(facility.getName());
                            checkFacilities[i].setTextSize(12);
                            checkFacilities[i].setId(i);
                            checkFacilities[i].setTextColor(Color.BLACK);
                            layoutFacilities.addView(checkFacilities[i]);

                            progressDialog.dismiss();

                            checkFacilities[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked == true) {
                                        mListFacilitiesChecked.add(facility);
                                    } else {
                                        mListFacilitiesChecked.remove(facility);
                                    }
                                }
                            });

                        }
                    }else{
                        Toast.makeText(CreateNewInventory.this, response.body().getStatus().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(CreateNewInventory.this, "Gagal mengambil data",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Response.GetFacillity> call, Throwable t) {
                Toast.makeText(CreateNewInventory.this, t.getMessage(),
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

        final InventoryCategory newInventory = new InventoryCategory();
        newInventory.setAsset(mAsset.getId());
        newInventory.setName(inventoryName);
        newInventory.setDescription(inventoryDescription);
        newInventory.setStock(Integer.parseInt(inventoryStock));
        newInventory.setSpace(Integer.parseInt(inventorySpace));
        newInventory.setPrice(Integer.parseInt(RupiahCurrency.unformatRupiah(inventoryPrice)));
        newInventory.setFacilities(mListFacilitiesChecked);

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
        Call<Response.UploadResponse> uploadPhotoReq = mApiSevice.uploadPhotos(parts);
        uploadPhotoReq.enqueue(new Callback<Response.UploadResponse>() {
            @Override
            public void onResponse(Call<Response.UploadResponse> call, retrofit2.Response<Response.UploadResponse> response) {
                if(response.isSuccessful()){
                    if(response.body().getStatus().getCode() == 200){
                        newInventory.setImages(response.body().getData().getImage());
                        callAddInventory(newInventory);
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(CreateNewInventory.this, response.body().getStatus().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(CreateNewInventory
                            .this, "Gagal unggah foto", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response.UploadResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(CreateNewInventory.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void callAddInventory(InventoryCategory inventory) {
        Request.CreateInventory inventoryRequest = new Request().new CreateInventory();
        inventoryRequest.setInventoryCategory(inventory);

        Call<Response.CreateInventory> addInventoryRequest = mInventoryAPIService.createInventoryCategory(inventoryRequest);
        addInventoryRequest.enqueue(new Callback<Response.CreateInventory>() {
            @Override
            public void onResponse(Call<Response.CreateInventory> call, retrofit2.Response<Response.CreateInventory> response) {
                progressDialog.dismiss();
                if(response.isSuccessful()){
                    if(response.body().getStatus().getCode() == 200){
                        Toast.makeText(CreateNewInventory.this, "Inventory telah dibuat", Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        Toast.makeText(CreateNewInventory.this, response.body().getStatus().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(CreateNewInventory.this, "Gagal membuat inventory", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response.CreateInventory> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(CreateNewInventory.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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

            if(fileImages.size()>0){
                mBtnAddImage.setText("TAMBAH FOTO");
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
