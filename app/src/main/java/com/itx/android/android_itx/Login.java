package com.itx.android.android_itx;

import android.content.Intent;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.itx.android.android_itx.Entity.Users;
import com.itx.android.android_itx.Service.APIService;
import com.itx.android.android_itx.Service.AuthService;
import com.itx.android.android_itx.Utils.ApiUtils;
import com.itx.android.android_itx.Utils.SessionManager;

import org.json.JSONObject;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Login extends AppCompatActivity {

    private static final String TAG = Login.class.getSimpleName();
    AuthService mAuthAPIService;
    TextView errorLogin;

    SessionManager session;

    // login button
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.input_email)
    EditText editEmaill;
    @BindView(R.id.input_password)
    EditText editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        ButterKnife.bind(this);

        session = new SessionManager(getApplicationContext());


        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

<<<<<<< HEAD
                String username = editEmaill.getText().toString().trim();
                String password = editPassword.getText().toString().trim();

                sendPost(username, password);
            }
        });
    }

    public void sendPost(String email, String password) {

        mAuthAPIService = ApiUtils.getAuthAPIService();

        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put("email", email);
        jsonParams.put("password", password);
=======
                Intent dashboard = new Intent(Login.this, ListUsers.class);
                //String username = txtUsername.getText().toString().trim();
                //String password = txtPassword.getText().toString().trim();
>>>>>>> cdbbc6d7dd10bb03b69a4d8d0c9e507ae3ed8a22

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject(jsonParams)).toString());

<<<<<<< HEAD
        Call<ResponseBody> response = mAuthAPIService.loginPost("login", body);

        response.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> rawResponse) {
                if (rawResponse.isSuccessful()) {
                    try {
                        //get your response....
                        JSONObject jsonObject = new JSONObject(rawResponse.body().string());
                        String data = jsonObject.getString("data");

                        JSONObject jsonToken = new JSONObject(data);
                        String dataToken = jsonToken.getString("token");


                        session.setSession(dataToken);
=======
                // Staring MainActivity
                startActivity(dashboard);
>>>>>>> cdbbc6d7dd10bb03b69a4d8d0c9e507ae3ed8a22

                        Intent dashboard = new Intent(Login.this, DahsboardUtama.class);
                        startActivity(dashboard);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(Login.this, "Password / Username Salah",
                            Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                // other stuff...
            }
        });

    }

}




