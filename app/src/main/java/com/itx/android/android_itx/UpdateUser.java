package com.itx.android.android_itx;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.itx.android.android_itx.Entity.Users;
import com.itx.android.android_itx.Service.APIService;
import com.itx.android.android_itx.Service.UsersService;
import com.itx.android.android_itx.Utils.ApiUtils;
import com.itx.android.android_itx.Utils.AutoCompleteUtils;
import com.itx.android.android_itx.Utils.SessionManager;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
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

public class UpdateUser extends AppCompatActivity implements View.OnClickListener, Validator.ValidationListener {

    private static final String TAG = CreateNewUser.class.getSimpleName();
    final AutoCompleteUtils completeUtils = new AutoCompleteUtils(this);
    private static final int CAMERA_REQUEST = 1000;
    private static final int GALLERY_REQUEST = 1001;

    private SessionManager session;
    private File filePhoto;
    private Uri photoURI;

    UsersService mUserService;
    APIService mApiService;

    String mCurrentPhotoPath;

    ArrayAdapter spAdapterCity;
    ArrayAdapter spAdapterProvince;
    String chosenCity, chosenProvince;

    Validator validator;
    String idUser;

    ProgressDialog progressDialog;


    @BindView(R.id.fab_add_foto)
    FloatingActionButton mFabAddPhoto;

    @BindView(R.id.iv_add_user)
    ImageView mIvPhoto;

    @NotEmpty
    @BindView(R.id.et_add_user_firstname)
    EditText mEtFirstname;

    @NotEmpty
    @BindView(R.id.et_add_user_lastname)
    EditText mEtLastname;

    @NotEmpty
    @BindView(R.id.et_add_user_no_ktp)
    EditText mEtNoKTP;

    @NotEmpty
    @Email
    @BindView(R.id.et_add_user_email)
    EditText mEtEmail;

    @NotEmpty
    @BindView(R.id.et_add_user_address)
    EditText mEtAddress;

    @BindView(R.id.sp_add_user_city)
    Spinner mSpCity;

    @NotEmpty
    @BindView(R.id.et_add_user_postal)
    EditText mEtPostalCode;

    @BindView(R.id.sp_add_user_province)
    Spinner mSpProvince;

    @NotEmpty
    @BindView(R.id.btn_add_new_user)
    Button mBtnAddUser;

    @NotEmpty
    @BindView(R.id.et_add_user_country)
    AutoCompleteTextView mAcCountry;

    @NotEmpty
    @BindView(R.id.et_add_asset_phone)
    EditText mEtAssetPhone;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_user);

        session = new SessionManager(this);
        ButterKnife.bind(this);

        validator = new Validator(this);
        validator.setValidationListener(this);

        mBtnAddUser.setOnClickListener(this);
        mIvPhoto.setOnClickListener(this);
        mFabAddPhoto.setOnClickListener(this);

        progressDialog = new ProgressDialog(UpdateUser.this);
        progressDialog.setMessage("Menyiapkan Data");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        prepareUserData();

    }


    public void setProvince(final String initProvince) {

        String country[] = {"Indonesia"};

        ArrayAdapter<String> adapterCountry = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, country);

        mAcCountry.setAdapter(adapterCountry);
        mAcCountry.setThreshold(1);

        spAdapterProvince = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                completeUtils.getArrayProvicesJson());
        mSpProvince.setAdapter(spAdapterProvince);

        mSpProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                chosenProvince = adapterView.getItemAtPosition(i).toString();

                if (!adapterView.getItemAtPosition(i).toString().equals(initProvince)){
                    setCitybyProvince(chosenProvince);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                chosenCity = null;
            }
        });
    }


    public void setCitybyProvince(String provinceName){

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

    /**
     * Prepares sample data to provide data set to adapter
     */
    private void prepareUserData() {
        idUser = getIntent().getStringExtra("id");
        mUserService = ApiUtils.getListUsersService(session.getToken());
        Call<JsonObject> response = mUserService.getUser(idUser);

        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> rawResponse) {
                if (rawResponse.isSuccessful()) {
                    try {
                        JsonObject jsonObject = rawResponse.body().get("data").getAsJsonObject();

                        Log.d("Data", jsonObject.toString());
                        mEtFirstname.setText(jsonObject.get("firstName").getAsString());
                        mEtLastname.setText(jsonObject.get("lastName").getAsString());
                        mEtNoKTP.setText(jsonObject.get("ktp").getAsString());
                        mEtEmail.setText(jsonObject.get("email").getAsString());
                        mEtAddress.setText(jsonObject.get("address").getAsJsonObject().get("address").getAsString());
                        mEtPostalCode.setText(jsonObject.get("address").getAsJsonObject().get("postalCode").getAsString());
                        mAcCountry.setText(jsonObject.get("address").getAsJsonObject().get("country").getAsString());
                        mEtAssetPhone.setText(jsonObject.get("phone").getAsString());
                        String images  = jsonObject.get("photo").getAsJsonObject().get("thumbnail").getAsString();

                        if (images != null){
                            Glide.with(UpdateUser.this)
                                    .load(ApiUtils.BASE_URL_USERS_IMAGE + images)
                                    .into(mIvPhoto);
                        }

                        String selectedValueProv = jsonObject.get("address").getAsJsonObject().get("province").getAsString();
                        setProvince(selectedValueProv);

                        int selectionPositionProv = spAdapterProvince.getPosition(selectedValueProv);
                        mSpProvince.setSelection(selectionPositionProv);
                        setCitybyProvince(selectedValueProv);

                        int selectionPositionCity = spAdapterCity.getPosition(jsonObject.get("address").getAsJsonObject().get("city").getAsString());
                        mSpCity.setSelection(selectionPositionCity);

                        new CountDownTimer(1000, 1000) {
                            public void onTick(long millisUntilFinished) {
                            }

                            public void onFinish() {
                                progressDialog.dismiss();
                            }
                        }.start();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(UpdateUser.this, "Gagal",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {

                Toast.makeText(UpdateUser.this, throwable.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateUser(String token) {

        progressDialog = new ProgressDialog(UpdateUser.this);
        progressDialog.setMessage("Menyimpan Data");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        mUserService = ApiUtils.getListUsersService(token);
        mApiService = ApiUtils.getAPIService(token);

        final String firstName = mEtFirstname.getText().toString().trim();
        final String lastName = mEtLastname.getText().toString().trim();
        final String email = mEtEmail.getText().toString().trim();
        final String noKTP = mEtNoKTP.getText().toString().trim();
        final String address = mEtAddress.getText().toString().trim();
        final String postal = mEtPostalCode.getText().toString().trim();
        final String country = mAcCountry.getText().toString().trim();
        final String phone = mEtAssetPhone.getText().toString().trim();

        Toast.makeText(this, "Sedang membuat User", Toast.LENGTH_SHORT).show();
//        Upload Photo first then on callback save the new User
        RequestBody uploadBody = RequestBody.create(MediaType.parse(getContentResolver().getType(photoURI)), filePhoto);
        MultipartBody.Part multipart = MultipartBody.Part.createFormData("photos", filePhoto.getName(), uploadBody);
        Call<ResponseBody> uploadPhotoReq = mApiService.uploadPhoto(multipart);

        uploadPhotoReq.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, response.body().toString());
                if (response.isSuccessful()) {
                    try {
                        JSONArray responseJson = new JSONArray(response.body().string());
                        JSONObject images = responseJson.getJSONObject(0);

                        Toast.makeText(UpdateUser.this, "Upload foto berhasil", Toast.LENGTH_SHORT).show();

                        JSONObject object0 = new JSONObject();
                        object0.put("firstName", firstName);
                        object0.put("lastName", lastName);
                        object0.put("ktp", noKTP);
                        object0.put("email", email);
                        object0.put("phone", phone);

                        JSONObject location = new JSONObject();
                        location.put("address", address);
                        location.put("province", chosenProvince);
                        location.put("city", chosenCity);
                        location.put("postalCode", postal);
                        location.put("country", country);

                        JSONObject object = new JSONObject();
                        object.put("data", object0);
                        object.put("location", location);
                        object.put("images", images);

                        saveUserToServer(object);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(UpdateUser.this, "Upload foto gagal karna: " + t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void saveUserToServer(JSONObject jsonParams) {

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                (jsonParams).toString());

        Call<ResponseBody> addUserRequest = mUserService.updateUser(idUser, body);

        new CountDownTimer(1000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                progressDialog.dismiss();
            }
        }.start();

        addUserRequest.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, response.body().toString());
                    //success then send back the user to the list user and destroy this activity
                    startActivity(new Intent(UpdateUser.this, ListUsers.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

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

        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void takePhotoFromCamera() {
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
                photoURI = FileProvider.getUriForFile(UpdateUser.this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        filePhoto);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
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
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Uri imageUri = Uri.parse(mCurrentPhotoPath);
            File file = new File(imageUri.getPath());
            try {
                InputStream ims = new FileInputStream(file);
                mIvPhoto.setImageBitmap(BitmapFactory.decodeStream(ims));
            } catch (FileNotFoundException e) {
                return;
            }
        } else if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK) {
            photoURI = data.getData();
            //filePhoto = new File(getPath(photoURI));
            mIvPhoto.setImageURI(photoURI);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_new_user:
                validator.validate();
                break;
            case R.id.fab_add_foto:
                takePhotoWithPermission();
                break;
            default:
                break;
        }
    }

    @Override
    public void onValidationSucceeded() {

        if (photoURI == null) {
            Toast.makeText(this, "Pilih foto terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }
        updateUser(session.getToken());

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
