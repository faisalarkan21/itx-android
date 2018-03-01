package com.itx.android.android_itx;

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

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;

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
        String idUser = getIntent().getStringExtra("id");
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
                        mEtLastname.setText(jsonObject.get("firstName").getAsString());
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

    }

    @Override
    public void onValidationSucceeded() {

    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {

    }
}
