package com.itx.android.android_itx;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.itx.android.android_itx.Adapter.PreviewAdapter;
import com.itx.android.android_itx.Entity.Inventory;
import com.itx.android.android_itx.Entity.Users;
import com.itx.android.android_itx.Service.APIService;
import com.itx.android.android_itx.Service.ListInventoryService;
import com.itx.android.android_itx.Service.ListUsersService;
import com.itx.android.android_itx.Utils.ApiUtils;
import com.itx.android.android_itx.Utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

public class CreateNewInventory extends AppCompatActivity {

    private static final int RC_CAMERA = 1000;
    private static final int RC_GALLERY = 1001;


    ArrayList<Uri> uriImages = new ArrayList<>();
    ArrayList<File> fileImages = new ArrayList<>();
    private APIService mApiSevice;
    private PreviewAdapter mPreviewAdapter;
    private List<String> mListFacilitiesChecked = new ArrayList<>();
    @BindView(R.id.et_add_inventory_name)
    EditText mEtInventName;
    @BindView(R.id.et_add_inventory_deskripsi)
    EditText mEtInventDeskripsi;
    @BindView(R.id.et_add_inventory_space)
    EditText mEtInventSpace;
    @BindView(R.id.et_add_inventory_stock)
    EditText mEtInventStock;
    @BindView(R.id.btn_add_new_inventory)
    Button mBtnAddInvent;
    @BindView(R.id.rv_preview_img_new_invent)
    RecyclerView mRvPreviewImageInvent;
    @BindView(R.id.select_image_inventory) Button mBtnAddImage;

    @BindView(R.id.layoutFacilities)
    LinearLayout layoutFacilities;

    CheckBox checkFacilities;

    int totalFacilities;


    SessionManager session;
    ListInventoryService mInventoryAPIService;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_inventory);

        session = new SessionManager(this);
        mInventoryAPIService = ApiUtils.getListInventoryService(session.getToken());
        ButterKnife.bind(this);

        prepareFacilitiesData();

        mRvPreviewImageInvent.setLayoutManager(new GridLayoutManager(this,2));
        mPreviewAdapter = new PreviewAdapter(uriImages, this);
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
        progressDialog.setMessage("Menyiapkan Data");
        progressDialog.show();

        mBtnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });

    }

    private void takePhoto(){
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

    private void pickImagesFromGallery(){
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
        openGalleryIntent.setType("image/*");
        openGalleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        openGalleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        openGalleryIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(Intent.createChooser(openGalleryIntent,"Select Picture"), RC_GALLERY);
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


    private void takePhotoFromCamera(){
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
                uriImages.add(FileProvider.getUriForFile(CreateNewInventory.this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        fileImages.get(fileImages.size() -1)));
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriImages.get(uriImages.size() -1));
                startActivityForResult(takePictureIntent, RC_CAMERA);
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

    public String getPath(Uri uri)
    {
        String[] projection = { MediaStore.Images.Media.DATA };
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

    public void prepareFacilitiesData() {



        Call<JsonObject> response = mInventoryAPIService.getAllFacilities();

        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> rawResponse) {
                if (rawResponse.isSuccessful()) {
                    try {
                        final JsonArray jsonArray = rawResponse.body().get("data").getAsJsonArray();

                        for (int i = 0; i < jsonArray.size(); i++) {

                            final JsonObject Data = jsonArray.get(i).getAsJsonObject();
                            Inventory invent = new Inventory();





                            checkFacilities = new CheckBox(CreateNewInventory.this);
                            checkFacilities.setText(Data.get("name").getAsString());
                            checkFacilities.setTextSize(12);
                            checkFacilities.setId(i);
                            checkFacilities.setTextColor(Color.BLACK);
                            layoutFacilities.addView(checkFacilities);

                            new CountDownTimer(1000, 1000) {

                                public void onTick(long millisUntilFinished) {
                                    // You don't need anything here
                                }

                                public void onFinish() {

                                    progressDialog.dismiss();
                                }
                            }.start();

                            checkFacilities.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked == true){
                                        int getChecked = buttonView.getId();

                                        final JsonObject DataChecked = jsonArray.get(getChecked).getAsJsonObject();

                                        Log.i("checkbox", DataChecked.get("_id").getAsString());
                                        mListFacilitiesChecked.add( DataChecked.get("_id").getAsString());
                                    }else{
                                        int getChecked = buttonView.getId();

                                        final JsonObject DataChecked = jsonArray.get(getChecked).getAsJsonObject();

                                        Log.i("checkbox", DataChecked.get("_id").getAsString());
                                        mListFacilitiesChecked.remove(DataChecked.get("_id").getAsString());
                                    }
                                }
                            });

                            mBtnAddInvent.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    uploadImagesToServer();


                                }
                            });
                        }
                        Toast.makeText(CreateNewInventory.this, "Terdapat : " + Integer.toString(jsonArray.size()) + " data",
                                Toast.LENGTH_LONG).show();

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

    public void uploadImagesToServer(){
        final String inventoryName = mEtInventName.getText().toString().trim();
        final String inventoryDescription = mEtInventDeskripsi.getText().toString().trim();
        final String inventoryStock = mEtInventStock.getText().toString().trim();
        final String inventorySpace = mEtInventSpace.getText().toString().trim();
        Log.d("getAllChecked", mListFacilitiesChecked.toString());

        MultipartBody.Part[] parts = new MultipartBody.Part[fileImages.size()];
        for (int i=0; i < fileImages.size(); i++){
            File file = fileImages.get(i);
            RequestBody uploadBody = RequestBody.create(MediaType.parse(getContentResolver().getType(uriImages.get(i))), file);
            parts[i] = MultipartBody.Part.createFormData("photos",file.getName(),uploadBody);
        }
        Call<ResponseBody> uploadPhotoReq = mApiSevice.uploadPhotos(parts);
        uploadPhotoReq.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    try {
                        JSONArray images = new JSONArray(response.body().string());
                        String idAsset = getIntent().getStringExtra("idAsset");
                        JSONObject object0 = new JSONObject();
                        object0.put("asset", idAsset);
                        object0.put("name", inventoryName);
                        object0.put("description", inventoryDescription);
                        object0.put("space", inventorySpace);
                        object0.put("price", "6000");
                        object0.put("stock", inventoryStock);


                        JSONArray jsonArrayChecked = new JSONArray(mListFacilitiesChecked);

                        object0.put("facilities",jsonArrayChecked);

                        JSONObject object = new JSONObject();
                        object.put("data", object0 );
                        object.put("images",images);

                        saveInventoryToServer(object);

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

    public void saveInventoryToServer(JSONObject jsonParams){


        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                (jsonParams).toString());

        Log.d("testJson", body.toString());

        Call<ResponseBody> addInventoryRequest = mInventoryAPIService.createInventoryCategory(body);

        addInventoryRequest.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Log.d("Post", response.body().toString());
                    //success then send back the user to the list user and destroy this activity
                    startActivity(new Intent(CreateNewInventory.this, ListUsers.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==  RC_GALLERY && resultCode == Activity.RESULT_OK){
            if(data.getData()!=null){

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
        } else if (requestCode == RC_CAMERA && resultCode == Activity.RESULT_OK){
            mPreviewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 13){
            takePhotoFromCamera();
        }

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


}
