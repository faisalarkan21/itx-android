package com.itx.android.android_itx;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class Login extends AppCompatActivity {

    EditText txtUsername, txtPassword;

    TextView errorLogin;

    // login button
    @BindView(R.id.btn_login) Button btnLogin;
    @BindView(R.id.input_email) EditText editEmaill;
    @BindView(R.id.input_password) EditText editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        ButterKnife.bind(this);


        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent dashboard = new Intent(Login.this, DahsboardUtama.class);

                // Staring MainActivity
                startActivity(dashboard);

                // Get username, password from EditText


            }
        });
    }


}




