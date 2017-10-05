package com.transility.tracker.app;

import android.app.Application;

import com.transility.tracker.R;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;


public class HealthCareApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/Roboto-RobotoRegular.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
    }
}
