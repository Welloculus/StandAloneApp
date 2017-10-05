package com.transility.tracker.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.transility.tracker.R;
import com.transility.tracker.bluetooth.BluetoothHandler;
import com.transility.tracker.fora.PCLinkLibraryDemoActivity;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DashboardActivity extends BaseActivity implements View.OnClickListener {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dashboard);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar mToolbar;
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        initUI();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Find which menu item was selected
        int menuItem = item.getItemId();
        switch (menuItem)
        {
            case R.id.user_about_app:
                aboutApp();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void aboutApp(){
        Intent aboutAppActivity = new Intent(this, AboutUsActivity.class);
        startActivity(aboutAppActivity);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {

            case R.id.btn_fora_devices:
                intent = new Intent(mContext, PCLinkLibraryDemoActivity.class);
                //intent = new Intent(mContext, AndroidDatabaseManager.class);
                startActivity(intent);
                break;
            case R.id.btn_zephyr_devices:
                intent = new Intent(mContext, DeviceListActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_get_reports:
                intent = new Intent(mContext, ReportsActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_utilities:
                intent = new Intent(mContext, UtilitiesActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    protected void initUI() {
        LinearLayout mllZephyrDevices;
        LinearLayout mllUtilities;
        LinearLayout mllGetReports;
        LinearLayout mllForaDevices;
        mllForaDevices = (LinearLayout) findViewById(R.id.btn_fora_devices);
        if(mllForaDevices!=null){
            mllForaDevices.setOnClickListener(this);
        }
        mllZephyrDevices = (LinearLayout) findViewById(R.id.btn_zephyr_devices);
        if(mllZephyrDevices!=null){
            mllZephyrDevices.setOnClickListener(this);
        }
        mllGetReports = (LinearLayout) findViewById(R.id.btn_get_reports);
        if(mllGetReports!=null){
            mllGetReports.setOnClickListener(this);
        }
        mllUtilities = (LinearLayout) findViewById(R.id.btn_utilities);
        if(mllUtilities!=null){
            mllUtilities.setOnClickListener(this);
        }
        mContext = this;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (BluetoothHandler.getInstance().isConnected()) {
            BluetoothHandler.getInstance().disconnect(getApplicationContext());
        }
    }
}

