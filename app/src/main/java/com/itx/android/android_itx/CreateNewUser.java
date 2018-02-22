package com.itx.android.android_itx;

import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.itx.android.android_itx.Entity.Address;
import com.itx.android.android_itx.Service.ListUsersService;
import com.itx.android.android_itx.Utils.ApiUtils;
import com.itx.android.android_itx.Utils.SessionManager;

import org.json.JSONObject;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateNewUser extends AppCompatActivity {

    private static final String TAG = CreateNewUser.class.getSimpleName();

    ListUsersService mUserService;

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

        ButterKnife.bind(this);

        mBtnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: implement for adding the user to the server
                addNewUser();
            }
        });
    }

    private void addNewUser(){
        mUserService = ApiUtils.getListUsersService(new SessionManager(this).getToken());

        //TODO: Cleaning this messy things

        String firstName = mEtFirstname.getText().toString().trim();
        String lastName = mEtLastname.getText().toString().trim();
        String email = mEtEmail.getText().toString().trim();
        String noKTP = mEtNoKTP.getText().toString().trim();
        String address = mEtAddress.getText().toString().trim();
        String city = mEtCity.getText().toString().trim();
        String postal = mEtPostalCode.getText().toString().trim();
        String province = mEtProvince.getText().toString().trim();
        String country = mEtCountry.getText().toString().trim();

        Address fullAddress = new Address(address,city,province ,postal,country);


        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put("email", email);
        jsonParams.put("firstName", firstName);
        jsonParams.put("lastName", lastName);
        jsonParams.put("ktp", noKTP);
        jsonParams.put("address",fullAddress);

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                (new JSONObject(jsonParams).toString()));

        Call<ResponseBody> response = mUserService.createUser(body);

        response.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Log.d(TAG, response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}
