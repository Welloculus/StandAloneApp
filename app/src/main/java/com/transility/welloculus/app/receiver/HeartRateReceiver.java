package com.transility.welloculus.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.transility.welloculus.db.DBHelper;
import com.transility.welloculus.utils.Constants;

import java.util.Calendar;


public class HeartRateReceiver extends BroadcastReceiver {
    Context ctx;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.ctx = context;
        DBHelper dbHelper = DBHelper.getInstance(context);
        if (intent != null) {
            String action = intent.getAction();
            if (action == Constants.BROADCAST_NEW_DATA_ACTION && intent.hasExtra(Constants.EXTRAS_DATA_TYPE)) {
                String dataType = intent.getStringExtra(Constants.EXTRAS_DATA_TYPE);
                String data = intent.getStringExtra(Constants.EXTRAS_HEALTH_DATA);
                long time = intent.getLongExtra(Constants.EXTRAS_HEART_RATE_LOG_TIME, Calendar.getInstance().getTimeInMillis());
                String deviceId = intent.getStringExtra(Constants.EXTRAS_DEVICE_ID);
                String deviceName = intent.getStringExtra(Constants.EXTRAS_DEVICE_NAME);
                if (data != null) {
                    dbHelper.insertHealthData(deviceId, deviceName, time, dataType, data);
                }
            }
        }
    }
}

