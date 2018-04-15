package com.itx.android.android_itx;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
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
import com.bumptech.glide.request.RequestOptions;
import com.itx.android.android_itx.Entity.Address;
import com.itx.android.android_itx.Entity.Image;
import com.itx.android.android_itx.Entity.Photo;
import com.itx.android.android_itx.Entity.Request;
import com.itx.android.android_itx.Entity.Response;
import com.itx.android.android_itx.Entity.User;
import com.itx.android.android_itx.Service.APIService;
import com.itx.android.android_itx.Service.UsersService;
import com.itx.android.android_itx.Utils.ApiUtils;
import com.itx.android.android_itx.Utils.AutoCompleteUtils;
import com.itx.android.android_itx.Utils.ImageUtils;
import com.itx.android.android_itx.Utils.SessionManager;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

public class CreateNewUser extends AppCompatActivity implements View.OnClickListener, Validator.ValidationListener, EasyPermissions.PermissionCallbacks {

    private static final String TAG = CreateNewUser.class.getSimpleName();
    final AutoCompleteUtils completeUtils = new AutoCompleteUtils(this);
    private static final int CAMERA_REQUEST = 1000;
    private static final int RC_PERMS_CAMERA = 13;
    private static final int RC_PERMS_GALLERY = 14;
    private static final int GALLERY_REQUEST = 1001;

    private SessionManager sessManager;
    public static File filePhoto;
    public static Uri photoURI;

    ProgressDialog progressDialog;


    ArrayAdapter spAdapterCity;
    ArrayAdapter spAdapterProvince;
    UsersService mUserService;
    APIService mApiService;
    String chosenCity, chosenProvince;

    public static String mCurrentPhotoPath;

    Validator validator;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_user);

        getSupportActionBar().setTitle("Tambah Pemilik");

        sessManager = new SessionManager(this);
        ButterKnife.bind(this);

        validator = new Validator(this);
        validator.setValidationListener(this);

        mBtnAddUser.setOnClickListener(this);
        mFabAddPhoto.setOnClickListener(this);

        setProvince();
    }


    public void setProvince() {

        ArrayAdapter<String> adapterProvince = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, completeUtils.getArrayProvicesJson());

        String country[] = {"Indonesia"};

        ArrayAdapter<String> adapterCountry = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, country);

        spAdapterProvince = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                completeUtils.getArrayProvicesJson());
        mSpProvince.setAdapter(spAdapterProvince);

        mSpProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                chosenProvince = adapterView.getItemAtPosition(i).toString();

                setCitybyProvince(chosenProvince);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                chosenCity = null;
            }
        });


        mAcCountry.setAdapter(adapterCountry);
        mAcCountry.setThreshold(1);


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


    private void addNewUser(String token) {
        progressDialog = new ProgressDialog(CreateNewUser.this);
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

        final User newUser = new User();
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setKtp(noKTP);
        newUser.setPassword("telkom2018");

        Address updatedAddress = new Address();
        updatedAddress.setAddress(address);
        updatedAddress.setPostalCode(postal);
        updatedAddress.setCountry("Indonesia");
        updatedAddress.setCity(mSpCity.getSelectedItem().toString());
        updatedAddress.setProvince(mSpProvince.getSelectedItem().toString());
        newUser.setAddress(updatedAddress);
        newUser.setPhone(phone);

        File compressedImageFile = null;

        try {
            compressedImageFile = new Compressor(this).compressToFile(filePhoto);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Upload Photo first then on callback save the new User
        RequestBody uploadBody = RequestBody.create(MediaType.parse("image/jpeg"), compressedImageFile);
        MultipartBody.Part multipart = MultipartBody.Part.createFormData("photos", filePhoto.getName(), uploadBody);
        Call<Response.UploadResponse> uploadPhotoReq = mApiService.uploadPhoto(multipart);
        uploadPhotoReq.enqueue(new Callback<Response.UploadResponse>() {
            @Override
            public void onResponse(Call<Response.UploadResponse> call, retrofit2.Response<Response.UploadResponse> response) {
                if(response.isSuccessful()) {
                    if (response.body().getStatus().getCode() == 200) {
                        Image image = response.body().getData().getImage().get(0);
                        Photo photo = new Photo();
                        photo.setAlt(image.getmAlt());
                        photo.setFullsize(image.getmFullsize());
                        photo.setLarge(image.getmLarge());
                        photo.setMedium(image.getmMedium());
                        photo.setThumbnail(image.getmThumbnail());

                        newUser.setPhoto(photo);
                        saveUserToServer(newUser);
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(CreateNewUser.this, response.body().getStatus().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(CreateNewUser.this, "Upload foto gagal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response.UploadResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(CreateNewUser.this, "Upload foto gagal karna: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    public void saveUserToServer(User user) {
        Request.CreateUser requestUser = new Request().new CreateUser();
        requestUser.setUser(user);

        Call<Response.CreateUser> addUserRequest = mUserService.createUser(requestUser);
        addUserRequest.enqueue(new Callback<Response.CreateUser>() {
            @Override
            public void onResponse(Call<Response.CreateUser> call, retrofit2.Response<Response.CreateUser> response) {
                progressDialog.dismiss();
                if(response.isSuccessful()){
                    if(response.body().getStatus().getCode() == 200){
                        finish();
                    }else{
                        Toast.makeText(CreateNewUser.this, response.body().getStatus().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(CreateNewUser.this, "Proses gagal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response.CreateUser> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(CreateNewUser.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (photoURI != null) {
            outState.putString("URIFOTO", photoURI.toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("URIFOTO")) {
            photoURI = Uri.parse(savedInstanceState.getString("URIFOTO"));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        RequestOptions options = new RequestOptions()
                .fitCenter()
                .override(200, 200);

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Uri imageUri = Uri.parse(mCurrentPhotoPath);
            File file = new File(imageUri.getPath());
            try {
                InputStream ims = new FileInputStream(file);
                Glide.with(CreateNewUser.this).asBitmap().apply(options)
                        .load(BitmapFactory.decodeStream(ims)).into(mIvPhoto);
            } catch (FileNotFoundException e) {
                return;
            }
        } else if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK) {
            photoURI = data.getData();
            try {
                filePhoto = new File(ImageUtils.getPath(photoURI, this));
            } catch (Exception e) {
                e.printStackTrace();
            }
            Glide.with(CreateNewUser.this).asBitmap().apply(options)
                    .load(photoURI).into(mIvPhoto);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_new_user:
                validator.validate();
                break;
            case R.id.fab_add_foto:
                ImageUtils.takeOnePhoto(this,
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

        if (photoURI == null) {
            Toast.makeText(this, "Pilih foto terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        } else if (mSpProvince.getSelectedItem() == null) {
            Toast.makeText(this, "Pilih Provinsi terlebih dahulu", Toast.LENGTH_SHORT).show();
        } else if (mSpCity.getSelectedItem() == null) {
            Toast.makeText(this, "Pilih Kota terlebih dahulu", Toast.LENGTH_SHORT).show();
        }
        addNewUser(sessManager.getToken());
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
            ImageUtils.takeOnePhotoFromCamera(this, CAMERA_REQUEST);
        } else if (requestCode == RC_PERMS_GALLERY) {
            ImageUtils.takeOnePhotoFromGallery(this, GALLERY_REQUEST);
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
