package com.itx.android.android_itx;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.CountDownTimer;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import jp.wasabeef.blurry.Blurry;
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
    @BindView(R.id.iv_blurry)
    ImageView mRootView;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        ButterKnife.bind(this);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_login_2);

        Blurry.with(this)
                .radius(10)
                .sampling(5)
                .from(bitmap)
                .into(mRootView);
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                String username = editEmaill.getText().toString().trim();
                String password = editPassword.getText().toString().trim();
                session = new SessionManager(getApplicationContext());
                progressDialog = new ProgressDialog(Login.this);
                progressDialog.setMessage("Mohon Tungggu");
                progressDialog.show();

                sendPost(username, password);
            }
        });
    }

    public void sendPost(String email, String password) {

        mAuthAPIService = ApiUtils.getAuthAPIService();

        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put("email", email);
        jsonParams.put("password", password);


        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject(jsonParams)).toString());


        Call<ResponseBody> response = mAuthAPIService.loginPost("login", body);

        response.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> rawResponse) {
                if (rawResponse.isSuccessful()) {
                    try {

                        JSONObject jsonObject = new JSONObject(rawResponse.body().string());
                        String data = jsonObject.getString("data");

                        JSONObject jsonToken = new JSONObject(data);
                        String dataToken = jsonToken.getString("token");


                        session.setSession(dataToken);

                        new CountDownTimer(1000, 1000) {

                            public void onTick(long millisUntilFinished) {
                                // You don't need anything here
                            }
                            public void onFinish() {
                                progressDialog.dismiss();
                                Intent dashboard = new Intent(Login.this, DashboardUtama.class);
                                startActivity(dashboard);
                                finish();

                            }
                        }.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(Login.this, "Password / Username Salah",
                            Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {

                Toast.makeText(Login.this, throwable.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });

    }

}




