package com.itx.android.android_itx;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by faisal on 3/1/18.
 */

public class UpdateUser extends AppCompatActivity implements View.OnClickListener, Validator.ValidationListener, TextWatcher {

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

    Validator validator;
    String idUser;


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

    @NotEmpty
    @BindView(R.id.et_add_user_city)
    AutoCompleteTextView mAcCity;

    @NotEmpty
    @BindView(R.id.et_add_user_postal)
    EditText mEtPostalCode;

    @NotEmpty
    @BindView(R.id.et_add_user_province)
    AutoCompleteTextView mAcProvince;

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

        setAutoComplete();
        prepareUserData();
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
                        mAcCity.setText(jsonObject.get("address").getAsJsonObject().get("city").getAsString());
                        mEtPostalCode.setText(jsonObject.get("address").getAsJsonObject().get("postalCode").getAsString());
                        mAcProvince.setText(jsonObject.get("address").getAsJsonObject().get("province").getAsString());
                        mAcCountry.setText(jsonObject.get("address").getAsJsonObject().get("country").getAsString());
                        mEtAssetPhone.setText(jsonObject.get("phone").getAsString());


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
        mUserService = ApiUtils.getListUsersService(token);
        mApiService = ApiUtils.getAPIService(token);

        final String firstName = mEtFirstname.getText().toString().trim();
        final String lastName = mEtLastname.getText().toString().trim();
        final String email = mEtEmail.getText().toString().trim();
        final String noKTP = mEtNoKTP.getText().toString().trim();
        final String address = mEtAddress.getText().toString().trim();
        final String city = mAcCity.getText().toString().trim();
        final String postal = mEtPostalCode.getText().toString().trim();
        final String province = mAcProvince.getText().toString().trim();
        final String country = mAcCountry.getText().toString().trim();
        final String phone = mEtAssetPhone.getText().toString().trim();

        Toast.makeText(this, "Sedang membuat User", Toast.LENGTH_SHORT).show();
//        //Upload Photo first then on callback save the new User
//        RequestBody uploadBody = RequestBody.create(MediaType.parse(getContentResolver().getType(photoURI)), filePhoto);
//        MultipartBody.Part multipart = MultipartBody.Part.createFormData("photos", filePhoto.getName(), uploadBody);
//        Call<ResponseBody> uploadPhotoReq = mApiService.uploadPhoto(multipart);
//
//        uploadPhotoReq.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Log.d(TAG, response.body().toString());
//                if (response.isSuccessful()) {
                    try {
//                        JSONArray responseJson = new JSONArray(response.body().string());
//                        JSONObject images = responseJson.getJSONObject(0);
//                        String urlFoto = images.getString("thumbnail");
//                        Toast.makeText(UpdateUser.this, "Upload foto berhasil", Toast.LENGTH_SHORT).show();


                        JSONObject object0 = new JSONObject();
                        object0.put("firstName", firstName);
                        object0.put("lastName", lastName);
                        object0.put("ktp", noKTP);
                        object0.put("email", email);
                        object0.put("phone", phone);

                        JSONObject location = new JSONObject();
                        location.put("address", address);
                        location.put("province", province);
                        location.put("city", city);
                        location.put("postalCode", postal);
                        location.put("country", country);

                        JSONObject object = new JSONObject();
                        object.put("data", object0);
                        object.put("location", location);
//                        object.put("images", images);

                        saveUserToServer(object);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Toast.makeText(UpdateUser.this, "Upload foto gagal karna: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//
//            }
//        });
    }

    public void setAutoComplete() {

        ArrayAdapter<String> adapterProvince = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, completeUtils.getArrayProvicesJson());

        String country[] = {"Indonesia"};

        ArrayAdapter<String> adapterCountry = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, country);

        mAcProvince.setAdapter(adapterProvince);
        mAcProvince.setThreshold(1);

        mAcCity.addTextChangedListener(this);

        mAcCountry.setAdapter(adapterCountry);
        mAcCountry.setThreshold(1);

    }

    public void saveUserToServer(JSONObject jsonParams) {

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                (jsonParams).toString());

        Call<ResponseBody> addUserRequest = mUserService.updateUser(idUser, body);

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


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        ArrayAdapter<String> adapterCity = new ArrayAdapter<String>
                (UpdateUser.this, android.R.layout.select_dialog_item, completeUtils.getArrayCityJson(mAcProvince.getText().toString()));
        mAcCity.setAdapter(adapterCity);
        mAcCity.setThreshold(1);

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_new_user:
                validator.validate();
                break;
            case R.id.iv_add_user:
//                takePhotoWithPermission();
                break;
            default:
                break;
        }
    }

    @Override
    public void onValidationSucceeded() {

//        if (photoURI == null) {
//            Toast.makeText(this, "Pilih foto terlebih dahulu", Toast.LENGTH_SHORT).show();
//            return;
//        }
        Toast.makeText(this, "Sedang Membuat User", Toast.LENGTH_SHORT).show();
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