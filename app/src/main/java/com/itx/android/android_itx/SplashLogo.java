package com.itx.android.android_itx;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;


import com.itx.android.android_itx.Utils.SessionManager;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by faisal on 2/21/18.
 */


public class SplashLogo extends AppCompatActivity {



    private Boolean firstTime = null;

    @BindView(R.id.logoSplashITX)
    ImageView logoItx;
    @BindView(R.id.logoSplashTelkom)
    ImageView logoTelkom;
    @BindView(R.id.logoSplashWonderful)
    ImageView logoWonderful;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        ButterKnife.bind(this);
        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

                    if (isFirstTime()) {
                        sliderFirstTimeInstall();
                    } else {
                        isHasToken();
                    }
                }
            }
        };
        timer.start();
    }

    private void sliderFirstTimeInstall() {
        startActivity(new Intent(SplashLogo.this, WelcomeActivity.class));
        finish();
    }

    public void isHasToken() {
        SessionManager sessionManager = new SessionManager(SplashLogo.this);
        if (sessionManager.getToken().length() > 0) {
            startActivity(new Intent(SplashLogo.this, ListUsers.class));
            finish();
        } else {
            startActivity(new Intent(SplashLogo.this, Login.class));
            finish();
        }

    }


    private boolean isFirstTime() {
        if (firstTime == null) {
            SharedPreferences mPreferences = this.getSharedPreferences("first_time", Context.MODE_PRIVATE);
            firstTime = mPreferences.getBoolean("firstTime", true);
            if (firstTime) {
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean("firstTime", false);
                editor.commit();
            }
        }
        return firstTime;
    }


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }
}
