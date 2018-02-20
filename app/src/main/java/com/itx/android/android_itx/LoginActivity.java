package com.itx.android.android_itx;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText txtUsername, txtPassword;

    TextView errorLogin;

    // login button
    Button btnLogin;

    // Session Manager Class
//    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        btnLogin = (Button) findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent dashboard = new Intent(LoginActivity.this, DahsboardUtama.class);

                // Staring MainActivity
                startActivity(dashboard);

                // Get username, password from EditText

//                final String username = txtUsername.getText().toString();
//                final String password = txtPassword.getText().toString();
//
//                // Check if username, password is filled
//                if (username.trim().length() > 0 && password.trim().length() > 0) {
//                    // For testing puspose username, password is checked with sample data
//
//                    RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
//                    //this is the url where you want to send the request
//                    //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
//                    StorageModel store = new StorageModel();
//                    String urlAPI = store.getUrlApi()+"/mobile-login";
////                    String urlAPI = "http://192.168.1.100:3000/mobile-login";
//
//                    // Request a string response from the provided URL.
//                    StringRequest stringRequest = new StringRequest(Request.Method.POST, urlAPI,
//                            new Response.Listener<String>() {
//                                @Override
//                                public void onResponse(String response) {
//                                    // Display the response string.
//                                    getCallback(response);
//
//
//                                }
//                            }, new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            Toast.makeText(LoginActivity.this, error.toString(),
//                                    Toast.LENGTH_LONG).show();
//                            error.printStackTrace();
//                        }
//                    }) {
//                        //adding parameters to the request
//                        @Override
//                        protected Map<String, String> getParams() throws AuthFailureError {
//                            Map<String, String> params = new HashMap<>();
//                            params.clear();
//                            params.put("email", username);
//                            params.put("password", password);
//                            return params;
//                        }
//                    };
//
//                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(
//                            0,
//                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//                    queue.add(stringRequest);
//
//
//                } else {
//                    // user didn't entered username or password
//                    // Show alert asking him to enter the details
//                    errorLogin.setText("Masukan Username dan Password.");
//                }

            }
        });
    }


}




