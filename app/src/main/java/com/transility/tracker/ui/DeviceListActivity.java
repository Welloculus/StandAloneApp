package com.transility.tracker.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.Thing;
import com.transility.tracker.R;
import com.transility.tracker.adapter.DeviceListAdapter;
import com.transility.tracker.app.receiver.IHealthCareReceiver;
import com.transility.tracker.app.receiver.OnDeviceConnectListener;
import com.transility.tracker.beans.DeviceInfoBean;
import com.transility.tracker.bluetooth.BluetoothHandler;
import com.transility.tracker.bluetooth.DeviceConnectException;
import com.transility.tracker.utils.Constants;

import java.util.ArrayList;

/**
 * Activity to show list of devices associated with the user.
 */
public class DeviceListActivity extends BaseActivity implements OnDeviceConnectListener, IHealthCareReceiver {
    protected RecyclerView mRecyclerView;
    private DeviceListAdapter mAdapter;
    private static final int REQUEST_ACCESS_COARSE_LOCATION = 1;
    private IHealthCareReceiver healthCareReceiver;
    private Context mContext;
    private static DeviceInfoBean mDeviceInfo;
    protected ArrayList<DeviceInfoBean> deviceInfoList = new ArrayList<>();
    private int mGreenColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_device_list_actvity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar!=null){
            toolbar.setNavigationIcon(R.drawable.back_arrow_white);
            setSupportActionBar(toolbar);
            if(null!=getSupportActionBar()){
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        mContext = this;
        healthCareReceiver = this;
        initUI();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        mGreenColor = ContextCompat.getColor(mContext, R.color.green);

        performDeviceAction(null);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_zephyr_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Find which menu item was selected
        int menuItem = item.getItemId();
        switch (menuItem)
        {
            case R.id.rescan:
                performDeviceAction(null);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectClick(DeviceInfoBean deviceInfo) {
        mDeviceInfo = deviceInfo;
        performDeviceAction(deviceInfo);
    }

    @Override
    protected void initUI() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new DeviceListAdapter(mContext, deviceInfoList, this);
    }

    private void connectToBluetooth(final DeviceInfoBean deviceInfo) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                switch (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    case PackageManager.PERMISSION_DENIED:
                        performPermissionDeniedAction(deviceInfo);
                        break;
                    case PackageManager.PERMISSION_GRANTED:
                        startDeviceConnection(deviceInfo, healthCareReceiver);
                        break;
                    default:
                        break;
                }
            } else {
                startDeviceConnection(deviceInfo, healthCareReceiver);
            }

        } catch (RuntimeException e) {
            showToastMessage(e.getMessage(), mContext);
            Log.e(Constants.TAG, Log.getStackTraceString(e));
        }

    }

    private void performPermissionDeniedAction(DeviceInfoBean deviceInfo) {
        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) mContext,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_ACCESS_COARSE_LOCATION);
        } else {
            startDeviceConnection(deviceInfo, healthCareReceiver);
        }
    }

    private void startDeviceConnection(final DeviceInfoBean deviceInfo, IHealthCareReceiver receiver) {
        try {
            if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                showBluetoothDisabledDialog();
            } else {
                BluetoothHandler.getInstance().connectToBluetooth(mContext, deviceInfo, receiver);
            }

        } catch (DeviceConnectException e) {
            Log.e(Constants.TAG, Log.getStackTraceString(e));
            showToastMessage(e.getMessage(), mContext);
        }
    }

    private void showBluetoothDisabledDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                .setTitle(mContext.getString(R.string.bluetooth_is_disabled))
                .setMessage(mContext.getString(R.string.bluetooth_should_be_enabled))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intentOpenBluetoothSettings = new Intent();
                        intentOpenBluetoothSettings.setAction(Settings.ACTION_BLUETOOTH_SETTINGS);
                        startActivity(intentOpenBluetoothSettings);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        showToastMessage(mContext.getString(R.string.bluetooth_should_be_enabled), mContext);
                    }
                })
                .setIcon(R.drawable.app_icon)
                .show();
        Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(mGreenColor);
        Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(mGreenColor);
    }

    /**
     * disconnect the device
     */
    private void disconnectDevice() {
        BluetoothHandler.getInstance().disconnect(mContext);
    }

    @Override
    public void onHeartRateReceived(int heartRate, DeviceInfoBean deviceInfo) {
        Log.v(Constants.TAG, "heartRate : " + heartRate);
        if(heartRate!=0){
            deviceInfoList.get(0).setCurrentValue(heartRate+"");
        }else{
            deviceInfoList.get(0).setCurrentValue("invalid");
        }
        prepareDeviceInfoData();
    }


    @Override
    public void isConnected(boolean connect) {
        if (connect) {
            showToastMessage(mContext.getString(R.string.device_connected), mContext);
        } else {
            showToastMessage(mContext.getString(R.string.device_disconnected), mContext);
        }
        mAdapter = new DeviceListAdapter(mContext, deviceInfoList, this);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onZephyrDeviceFound(DeviceInfoBean deviceInfo) {
        deviceInfoList.clear();
        deviceInfoList.add(deviceInfo);
        prepareDeviceInfoData();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ACCESS_COARSE_LOCATION:
                boolean isPermitted = false;
                for (int gr : grantResults) {
                    // Check if request is granted or not
                    if (gr == PackageManager.PERMISSION_GRANTED) {
                        isPermitted = true;
                    }
                }
                if (isPermitted) {
                    if (mDeviceInfo != null) {
                        connectToBluetooth(mDeviceInfo);
                    }
                } else {
                    showToastMessage(mContext.getString(R.string.bluetooth_requires_location_permission), mContext);
                }
                break;
            default:
                return;
        }
    }


    /**
     * perform the device disconnect action
     */
    private void performDeviceDisconnect(final DeviceInfoBean deviceInfo) {

        if (!BluetoothHandler.getInstance().getConnectedDevice().getDevice_udi().equalsIgnoreCase(mDeviceInfo.getDevice_udi())) {
            AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                    .setTitle(mContext.getString(R.string.another_device_already_connected))
                    .setMessage(mContext.getString(R.string.confirm_proceed_connect_new_device))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            disconnectDevice();
                            connectToBluetooth(deviceInfo);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(R.drawable.app_icon)
                    .show();
            Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setTextColor(mGreenColor);
            Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(mGreenColor);
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                    .setTitle(R.string.disconnect_device_tittle)
                    .setMessage(R.string.disconnect_device_message)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            disconnectDevice();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(R.drawable.app_icon)
                    .show();
            Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setTextColor(mGreenColor);
            Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(mGreenColor);
        }
    }

    /**
     * perform the device connect and disconnect action
     */
    private void performDeviceAction(DeviceInfoBean deviceInfo) {
        if (BluetoothHandler.getInstance().isConnected()) {
            performDeviceDisconnect(deviceInfo);
        } else {
            performDeviceConnect(deviceInfo);
        }
    }

    /**
     * perform device disconnect action
     */
    private void performDeviceConnect(DeviceInfoBean deviceInfo) {
        connectToBluetooth(deviceInfo);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("DeviceList Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void prepareDeviceInfoData() {
        mAdapter.notifyDataSetChanged();
    }

}
