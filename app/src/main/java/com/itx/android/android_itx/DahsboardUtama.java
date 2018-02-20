package com.itx.android.android_itx;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DahsboardUtama extends AppCompatActivity {

    Button btnUsers, btnAssets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dahsboard_utama);

        btnUsers = (Button) findViewById(R.id.btnUsers);
        btnAssets = (Button) findViewById(R.id.btnAssets);

        btnUsers.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent listUser = new Intent(DahsboardUtama.this, ListUsers.class);


                // Staring MainActivity
                startActivity(listUser);

                // Get username, password from EditText

//                final String username = txtUsername.getText().toString();
//                final String password = txtPassword.getText().toString();
//
//                // Check if username, password is filled
//                if (username.trim().length() > 0 && password.trim().length() > 0) {
//                    // For testing puspose username, password is checked with sample data
//
//                    RequestQueue queue = Volley.newRequestQueue(Login.this);
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
//                            Toast.makeText(Login.this, error.toString(),
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
