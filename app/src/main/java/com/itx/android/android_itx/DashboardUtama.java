package com.itx.android.android_itx;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DashboardUtama extends AppCompatActivity {

    Button btnUsers, btnAssets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_utama);

        btnUsers = (Button) findViewById(R.id.btnUsers);

        btnUsers.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent listUser = new Intent(DashboardUtama.this, ListUsers.class);

                // Staring MainActivity
                startActivity(listUser);

            }
        });

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}