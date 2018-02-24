package com.itx.android.android_itx;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.itx.android.android_itx.Entity.Address;
import com.itx.android.android_itx.Service.APIService;
import com.itx.android.android_itx.Service.ListUsersService;
import com.itx.android.android_itx.Utils.ApiUtils;
import com.itx.android.android_itx.Utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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

public class CreateNewUser extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = CreateNewUser.class.getSimpleName();
    private static final String IMAGE_DIRECTORY_NAME = "ITX";
    private static final int CAMERA_REQUEST = 1000;
    private static final int GALLERY_REQUEST = 1001;

    private SessionManager sessManager;
    private File filePhoto;
    private Uri photoURI;

    ListUsersService mUserService;
    APIService mApiService;

    String mCurrentPhotoPath;

    @BindView(R.id.iv_add_user) ImageView mIvPhoto;
    @BindView(R.id.et_add_user_firstname) EditText mEtFirstname;
    @BindView(R.id.et_add_user_lastname) EditText mEtLastname;
    @BindView(R.id.et_add_user_no_ktp) EditText mEtNoKTP;
    @BindView(R.id.et_add_user_email) EditText mEtEmail;
    @BindView(R.id.et_add_user_address) EditText mEtAddress;
    @BindView(R.id.et_add_user_city) EditText mEtCity;
    @BindView(R.id.et_add_user_postal) EditText mEtPostalCode;
    @BindView(R.id.et_add_user_province) EditText mEtProvince;
    @BindView(R.id.btn_add_new_user) Button mBtnAddUser;
    @BindView(R.id.et_add_user_country) EditText mEtCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_user);

        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        sessManager = new SessionManager(this);
        ButterKnife.bind(this);

        mBtnAddUser.setOnClickListener(this);
        mIvPhoto.setOnClickListener(this);
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

        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void addNewUser(String token){
        mUserService = ApiUtils.getListUsersService(token);
        mApiService = ApiUtils.getAPIService(token);

        if(photoURI == null) {
            Toast.makeText(this, "Pilih foto terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        //TODO: Cleaning this messy things

        final String firstName = mEtFirstname.getText().toString().trim();
        final String lastName = mEtLastname.getText().toString().trim();
        final String email = mEtEmail.getText().toString().trim();
        final String noKTP = mEtNoKTP.getText().toString().trim();
        String address = mEtAddress.getText().toString().trim();
        String city = mEtCity.getText().toString().trim();
        String postal = mEtPostalCode.getText().toString().trim();
        String province = mEtProvince.getText().toString().trim();
        String country = mEtCountry.getText().toString().trim();

        final Address fullAddress = new Address(address,city,province ,postal,country);

        //Upload Photo first then on callback save the new User
        RequestBody uploadBody = RequestBody.create(MediaType.parse(getContentResolver().getType(photoURI)), filePhoto);
        MultipartBody.Part multipart = MultipartBody.Part.createFormData("photos", filePhoto.getName(), uploadBody);
        Call<ResponseBody> uploadPhotoReq = mApiService.uploadPhoto(multipart);

        uploadPhotoReq.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, response.body().toString());
                if(response.isSuccessful()){
                    try {
                        JSONArray responseJson = new JSONArray(response.body().string());
                        JSONObject firstUrl = responseJson.getJSONObject(0);
                        String urlFoto = firstUrl.getString("thumbnail");
                        Toast.makeText(CreateNewUser.this, "Upload foto berhasil, url: " + urlFoto, Toast.LENGTH_SHORT).show();
                        Map<String, Object> jsonParams = new ArrayMap<>();
                        jsonParams.put("email", email);
                        jsonParams.put("firstName", firstName);
                        jsonParams.put("lastName", lastName);
                        jsonParams.put("ktp", noKTP);
                        jsonParams.put("address",fullAddress);

                        saveUserToServer(jsonParams);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(CreateNewUser.this, "Upload foto gagal", Toast.LENGTH_SHORT).show();
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
                takePhotoFromGallery();
            }
        });
        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    private void takePhotoFromCamera(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            try {
                filePhoto = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                return;
            }
            // Continue only if the File was successfully created
            if (filePhoto != null) {
                photoURI = FileProvider.getUriForFile(CreateNewUser.this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        filePhoto);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        }
    }

    private void takePhotoFromGallery(){
        /*
            ambil foto dari gallery lalu hasilnya akan ada di onActivityResult
        */

        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , GALLERY_REQUEST);
    }

    public void saveUserToServer(Map<String, Object> jsonParams){
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                (new JSONObject(jsonParams).toString()));

        Call<ResponseBody> addUserRequest = mUserService.createUser(body);

        addUserRequest.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Log.d(TAG, response.body().toString());
                    //success then send back the user to the list user and destroy this activity
                    startActivity(new Intent(CreateNewUser.this, ListUsers.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 13){
            takePhotoFromCamera();
        }

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(photoURI != null){
            outState.putString("URIFOTO",photoURI.toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState.containsKey("URIFOTO")){
            photoURI = Uri.parse(savedInstanceState.getString("URIFOTO"));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK){
            Uri imageUri = Uri.parse(mCurrentPhotoPath);
            File file = new File(imageUri.getPath());
            try {
                InputStream ims = new FileInputStream(file);
                mIvPhoto.setImageBitmap(BitmapFactory.decodeStream(ims));
            } catch (FileNotFoundException e) {
                return;
            }
        } else if(requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK){
            photoURI = data.getData();
            mIvPhoto.setImageURI(photoURI);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_add_new_user:
                addNewUser(sessManager.getToken());
                break;
            case R.id.iv_add_user:
                takePhoto();
                break;
            default:
                break;
        }
    }
}
