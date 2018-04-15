package com.itx.android.android_itx;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.CountDownTimer;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.itx.android.android_itx.Entity.Request;
import com.itx.android.android_itx.Entity.Response;
import com.itx.android.android_itx.Entity.User;
import com.itx.android.android_itx.Service.AuthService;
import com.itx.android.android_itx.Utils.ApiUtils;
import com.itx.android.android_itx.Utils.SessionManager;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.blurry.Blurry;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class Login extends AppCompatActivity implements Validator.ValidationListener {

    private static final String TAG = Login.class.getSimpleName();
    AuthService mAuthAPIService;
    TextView errorLogin;

    SessionManager session;

    // login button
    @BindView(R.id.btn_login)
    Button btnLogin;
    @NotEmpty
    @Email
    @BindView(R.id.input_email)
    EditText editEmaill;
    @BindView(R.id.input_password)
    @NotEmpty
    EditText editPassword;
    @BindView(R.id.iv_blurry)
    ImageView mRootView;

    Validator validator;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        ButterKnife.bind(this);

        validator = new Validator(this);
        validator.setValidationListener(this);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_login_2);

        Blurry.with(this)
                .radius(10)
                .sampling(5)
                .from(bitmap)
                .into(mRootView);

        editPassword.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    validator.validate();
                    return true;
                }
                return false;
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                validator.validate();
            }
        });
    }

    public void sendPost(String email, String password) {

        mAuthAPIService = ApiUtils.getAuthAPIService();
        Request.Login login = new Request().new Login();
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        login.setUser(user);

        Call<Response.Login> response = mAuthAPIService.loginPost(login);
        response.enqueue(new Callback<Response.Login>() {
            @Override
            public void onResponse(Call<Response.Login> call, final retrofit2.Response<Response.Login> response) {
                if(response.isSuccessful()){
                    if(response.body().getStatus().getCode() == 200){
                        Gson gson = new Gson();
                        String token = response.body().getData().getToken();
                        String userData = gson.toJson(response.body().getData().getUser(), User.class);

                        session.setSession(token);
                        session.setUserData(userData);

                        new CountDownTimer(1000, 1000) {

                            public void onTick(long millisUntilFinished) {
                                // You don't need anything here
                            }
                            public void onFinish() {
                                progressDialog.dismiss();
                                Intent listUser = new Intent(Login.this, ListUsers.class);
                                Intent listAsset = new Intent(Login.this, ListAssets.class);

                                if(response.body().getData().getUser().getRole().getName().equals("Admin")){
                                    startActivity(listUser);
                                }else{
                                    startActivity(listAsset);
                                }

                                finish();

                            }
                        }.start();
                    }else{
                        Toast.makeText(Login.this, response.body().getStatus().getMessage(),
                                Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }else{
                    Toast.makeText(Login.this, "Password / Username Salah",
                            Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Response.Login> call, Throwable t) {
                Toast.makeText(Login.this, t.getMessage(),
                        Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
    }


    @Override
    public void onValidationSucceeded() {

        String username = editEmaill.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        session = new SessionManager(getApplicationContext());
        progressDialog = new ProgressDialog(Login.this);
        progressDialog.setMessage("Mohon Tungggu");
        progressDialog.show();
        sendPost(username, password);
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
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}




