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
import android.util.Log;
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
import com.itx.android.android_itx.Entity.Image;
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

/**
 * Created by faisal on 3/2/18.
 */

public class UpdateInventory extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, Validator.ValidationListener, EasyPermissions.PermissionCallbacks {

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
    private Asset mAsset;
    private InventoryCategory mInventory;
    private List<Facility> facilities = new ArrayList<>();
    private List<Image> mImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_inventory);

        getSupportActionBar().setTitle("Ubah Inventory");

        Gson gson = new Gson();
        mAsset = gson.fromJson(getIntent().getStringExtra("ASSET"), Asset.class);
        mInventory =  gson.fromJson(getIntent().getStringExtra("DATA"), InventoryCategory.class);

        ButterKnife.bind(this);
        validator = new Validator(this);
        validator.setValidationListener(this);
        mBtnAddInvent.setOnClickListener(this);
        mBtnAddImage.setOnClickListener(this);
        mBtnAddInvent.setText("SIMPAN");

        if(fileImages.size() > 0 || imagePreviews.size() > 0){
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

                try {
                    mImages.remove(position);
                }catch (Exception e){
                    e.printStackTrace();
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
        progressDialog = new ProgressDialog(UpdateInventory.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Menyiapkan Data");
        progressDialog.show();

        prepareFacilitiesData();
    }


    public void prepareFacilitiesData() {
        Call<com.itx.android.android_itx.Entity.Response.GetFacillity> response = mInventoryAPIService.getAllFacilities();
        response.enqueue(new Callback<com.itx.android.android_itx.Entity.Response.GetFacillity>() {
            @Override
            public void onResponse(Call<com.itx.android.android_itx.Entity.Response.GetFacillity> call, retrofit2.Response<com.itx.android.android_itx.Entity.Response.GetFacillity> response) {
                if(response.isSuccessful()){
                    if(response.body().getStatus().getCode() == 200){
                        facilities.addAll(response.body().getData().getFacility());
                        checkFacilities = new CheckBox[facilities.size()];
                        for (int i = 0; i < facilities.size(); i++) {

                            final Facility facility = facilities.get(i);
                            checkFacilities[i] = new CheckBox(UpdateInventory.this);
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
                        prepareInventData();
                    }else{
                        Toast.makeText(UpdateInventory.this, response.body().getStatus().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(UpdateInventory.this, "Gagal mengambil data",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<com.itx.android.android_itx.Entity.Response.GetFacillity> call, Throwable t) {
                Toast.makeText(UpdateInventory.this, t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
    public void prepareInventData() {

        mEtInventName.setText(mInventory.getName());
        mEtInventDeskripsi.setText(mInventory.getDescription());
        mEtInventSpace.setText(mInventory.getSpace().toString());
        mEtInventStock.setText(mInventory.getStock().toString());
        mEtAddPrice.setText(mInventory.getPrice().toString());
        mImages.addAll(mInventory.getImages());

        for (int i = 0; i < mInventory.getImages().size(); i++) {
            Image image = mInventory.getImages().get(i);
            imagePreviews.add(new ImageHolder(image, null));
        }
        if(imagePreviews.size() > 0 ){
            mBtnAddImage.setText("TAMBAH FOTO");
        }
        mPreviewAdapter.notifyDataSetChanged();

        int hasChecked = 0;

        if (checkFacilities.length >= 1) {
            for (int z = 0; z < mInventory.getFacilities().size(); z++) {
                Facility facility = mInventory.getFacilities().get(z);
                for (int i = 0; i < checkFacilities.length; i++) {
                    if (checkFacilities[i].getText().toString().equals(facility.getName())) {
                        checkFacilities[i].setChecked(true);
                        hasChecked += 1;
                    }
                }
            }
        }
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

        final InventoryCategory newInventory = new InventoryCategory();
        newInventory.setAsset(mAsset.getId());
        newInventory.setName(inventoryName);
        newInventory.setDescription(inventoryDescription);
        newInventory.setStock(Integer.parseInt(inventoryStock));
        newInventory.setSpace(Integer.parseInt(inventorySpace));
        newInventory.setPrice(Integer.parseInt(RupiahCurrency.unformatRupiah(inventoryPrice)));
        newInventory.setFacilities(mListFacilitiesChecked);

        if (fileImages.size() < 1) {
            newInventory.setImages(mImages);
            updateInventoryToServer(newInventory);
        } else {
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
                            mImages.addAll(response.body().getData().getImage());
                            newInventory.setImages(mImages);
                            updateInventoryToServer(newInventory);
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(UpdateInventory.this, response.body().getStatus().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(UpdateInventory
                                .this, "Gagal unggah foto", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Response.UploadResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(UpdateInventory.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    public void updateInventoryToServer(InventoryCategory inventoryCategory) {
        Request.UpdateInventory inventoryRequest = new Request().new UpdateInventory();
        inventoryRequest.setInventoryCategory(inventoryCategory);

        Call<Response.UpdateInventory> addInventoryRequest = mInventoryAPIService.updateInventoryCategory(mInventory.getId(), inventoryRequest);
        addInventoryRequest.enqueue(new Callback<Response.UpdateInventory>() {
            @Override
            public void onResponse(Call<Response.UpdateInventory> call, retrofit2.Response<Response.UpdateInventory> response) {
                progressDialog.dismiss();
                if(response.isSuccessful()){
                    if(response.body().getStatus().getCode() == 200){
                        Toast.makeText(UpdateInventory.this, "Inventory telah dirubah", Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        Toast.makeText(UpdateInventory.this, response.body().getStatus().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(UpdateInventory.this, "Gagal menyimpan inventory", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response.UpdateInventory> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(UpdateInventory.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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

            if(fileImages.size() > 0 ){
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
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode == RC_PERMS_CAMERA) {
            ImageUtils.takeMultiplePhotosFromCamera(this,CAMERA_REQUEST);
        } else if (requestCode == RC_PERMS_GALLERY) {
            ImageUtils.takeMultiplePhotosFromGallery(this, RC_PERMS_GALLERY);
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
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int checkedID = buttonView.getId();

        for (int i = 0; i < facilities.size(); i++) {
            Facility facility = facilities.get(i);

            if (isChecked) {

                if(i == checkedID) {
                    mListFacilitiesChecked.add(facility);
                }
            } else {

                if(i == checkedID) {
                    mListFacilitiesChecked.remove(facility);
                }
            }
        }

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
