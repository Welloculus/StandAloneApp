package com.transility.tracker.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.transility.tracker.R;
import com.transility.tracker.utils.Constants;

/**
 * The type Splash activity.
 */
public class SplashActivity extends BaseActivity {
    public static int SPLASH_TIME_OUT = 3000;
    /**
     * The M context.
     */
    Context mContext;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        initUI();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                launchDashboard();
            }
        }, SPLASH_TIME_OUT);
    }

    private void launchDashboard() {
        Intent userActivity = new Intent(this, DashboardActivity.class);
        startActivityForResult(userActivity, Constants.launchUserReq);
        finish();
    }

    @Override
    protected void initUI() {
        mContext = this;

    }
}
