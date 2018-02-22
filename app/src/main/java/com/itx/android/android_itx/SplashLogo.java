package com.itx.android.android_itx;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.itx.android.android_itx.Utils.PrefManager;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by faisal on 2/21/18.
 */



public class SplashLogo extends AppCompatActivity {

    TranslateAnimation translation;

    @BindView(R.id.logoSplashITX) ImageView logoItx;
    @BindView(R.id.logoSplashTelkom) ImageView logoTelkom;
    @BindView(R.id.logoSplashWonderful) ImageView logoWonderful;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();

        ButterKnife.bind(this);

        logoItx.setAlpha(0f);
        ValueAnimator fadeAnim1 = ObjectAnimator.ofFloat(logoItx, "alpha", 3f, 0f);
        ValueAnimator fadeAnim2 = ObjectAnimator.ofFloat(logoTelkom, "alpha", 3f, 0f);
        ValueAnimator fadeAnim3 = ObjectAnimator.ofFloat(logoWonderful, "alpha", 3f, 0f);

        fadeAnim1.setDuration(1000);
        fadeAnim1.start();

        fadeAnim2.setDuration(1000);
        fadeAnim2.start();

        fadeAnim3.setDuration(1000);
        fadeAnim3.start();

        Thread timer = new Thread() {
            public void run() {
                try {

                    sleep(1000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

                    PrefManager prefManager = new PrefManager(getApplicationContext());

                    // make first time launch TRUE
                    prefManager.setFirstTimeLaunch(true);

                    startActivity(new Intent(SplashLogo.this, Login.class));
                    finish();


                }
            }
        };
        timer.start();

    }

    private int getDisplayHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }
}
