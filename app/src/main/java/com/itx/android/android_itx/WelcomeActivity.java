package com.itx.android.android_itx;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itx.android.android_itx.Adapter.WelcomeSliderAdapter;

import com.itx.android.android_itx.Utils.SessionManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class WelcomeActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {


    WelcomeSliderAdapter welcomeSliderAdapter;
    private int[] layouts;

    @BindView(R.id.btn_skip)
    Button btnSkip;

    @BindView(R.id.btn_next)
    Button btnNext;

    @BindView(R.id.view_pager_welcome)
    ViewPager viewPager;

    @BindView(R.id.layoutDotsWelcomeSlider)
    LinearLayout dotsLayout;

    TextView[] dots;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        btnNext.setOnClickListener(this);
        btnSkip.setOnClickListener(this);

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        layouts = new int[]{
                R.layout.welcome_slide1,
                R.layout.welcome_slide2,
                R.layout.welcome_slide3,
        };

        addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor();

        welcomeSliderAdapter = new WelcomeSliderAdapter(WelcomeActivity.this, layouts);
        viewPager.setAdapter(welcomeSliderAdapter);
        viewPager.addOnPageChangeListener(this);

    }

    private void launchHomeScreen() {
        SessionManager sessionManager = new SessionManager(WelcomeActivity.this);
        if (sessionManager.getToken().length() > 0) {
            startActivity(new Intent(WelcomeActivity.this, ListUsers.class));
            finish();
        } else {
            startActivity(new Intent(WelcomeActivity.this, Login.class));
            finish();
        }
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void isLastSlider() {
        int current = getItem(+1);
        if (current < layouts.length) {
            // move to next screen
            viewPager.setCurrentItem(current);
        } else {
            launchHomeScreen();
        }
    }


    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        addBottomDots(position);
        // changing the next button text 'NEXT' / 'GOT IT'
        if (position == layouts.length - 1) {
            // last page. make button text to GOT IT
            btnNext.setText("Done");
            btnSkip.setVisibility(View.GONE);
        } else {
            // still pages are left
            btnNext.setText("Next");
            btnSkip.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                isLastSlider();
                break;
            case R.id.btn_skip:
                launchHomeScreen();
                break;
            default:
                break;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
