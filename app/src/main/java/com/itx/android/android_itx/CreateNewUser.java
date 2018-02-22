package com.itx.android.android_itx;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;

public class CreateNewUser extends AppCompatActivity {

    @BindView(R.id.et_add_user_firstname) EditText mEtFirstname;
    @BindView(R.id.et_add_user_lastname) EditText mEtLastname;
    @BindView(R.id.et_add_user_no_ktp) EditText mEtNoKTP;
    @BindView(R.id.et_add_user_email) EditText mEtEmail;
    @BindView(R.id.et_add_user_address) EditText mEtAddress;
    @BindView(R.id.et_add_user_city) EditText mEtCity;
    @BindView(R.id.et_add_user_postal) EditText mEtPostalCode;
    @BindView(R.id.et_add_user_province) EditText mEtProvince;
    @BindView(R.id.btn_add_new_user) Button mBtnAddUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_user);

        mBtnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: implement for adding the user to the server
            }
        });
    }
}
