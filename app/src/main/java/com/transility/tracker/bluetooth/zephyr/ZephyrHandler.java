package com.transility.tracker.bluetooth.zephyr;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.transility.tracker.R;
import com.transility.tracker.app.receiver.IHealthCareReceiver;
import com.transility.tracker.beans.DeviceInfoBean;
import com.transility.tracker.bluetooth.DeviceConnectException;
import com.transility.tracker.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Set;

import zephyr.android.HxMBT.BTClient;
import zephyr.android.HxMBT.ConnectedEvent;
import zephyr.android.HxMBT.ConnectedListener;
import zephyr.android.HxMBT.ZephyrPacketEvent;
import zephyr.android.HxMBT.ZephyrPacketListener;
import zephyr.android.HxMBT.ZephyrProtocol;

/**
 * Class for handling the zephyr device connectivity and obtaining data.
 *
 * @author arpit.garg
 */
public class ZephyrHandler implements ZephyrPacketListener, ConnectedListener<BTClient> {
    private static final int HEART_RATE = 0x100;
    private BTClient mBTClient;
    private IHealthCareReceiver mReceiver = null;
    private ZephyrConnectListener mZephyrConnectionListener;
    private Context mContext;
    private DeviceInfoBean mDeviceInfo;

    /**
     * Instantiates a new Zephyr handler.
     *
     * @param context the context
     */
    public ZephyrHandler(Context context) {
        mContext = context;
    }

    /**
     * connect the zephyr device and set Connection event listener
     *
     * @param deviceInfo        the device info
     * @param mBluetoothAdapter the m bluetooth adapter
     * @throws DeviceConnectException the device connect exception
     */
    public synchronized void connect(DeviceInfoBean deviceInfo, BluetoothAdapter mBluetoothAdapter) throws DeviceConnectException {
        this.mDeviceInfo = deviceInfo;
        if(mBTClient!=null&& mBTClient.IsConnected()){
            Log.i("welloculus", "-----------------device already connected");
            return;
        }
        /**
         * checking for the paired devices
         */
        Set<BluetoothDevice> bondedDevices = mBluetoothAdapter.getBondedDevices();
        for (BluetoothDevice pairedDevice : bondedDevices) {
            Log.i("welloculus", "-----------------paired device=="+pairedDevice.getAddress());
            if (pairedDevice.getAddress().startsWith(deviceInfo.getDevice_udi())) {
                mBTClient = new BTClient(mBluetoothAdapter, deviceInfo.getDevice_udi());
                mZephyrConnectionListener = new ZephyrConnectListener(mHandler, mHandler);
                mBTClient.addConnectedEventListener(mZephyrConnectionListener);
                if (mBTClient.IsConnected()) {
                    mReceiver.isConnected(true);
                    /*HealthCareApp healthCareApp = (HealthCareApp) ((Activity) mContext).getApplication();
                    healthCareApp.startPostDataService();*/
                    break;
                }
                mBTClient = null;
            }
        }


        if (mBTClient == null)
            throw new DeviceConnectException(mContext.getString(R.string.no_device_found));

        mBTClient.start();
    }

    /**
     * disconnect the zephyr device
     */
    public void disconnect() {
        if (mBTClient != null) {
            mBTClient.removeConnectedEventListener(this);
            mBTClient.Close();
            mBTClient = null;
        }
    }

    /**
     * checks if connected or not
     *
     * @return boolean
     */
    public boolean isConnected() {
        if (mBTClient != null && mBTClient.IsConnected()) {
            return true;
        }
        return false;
    }

    @Override
    public void Connected(ConnectedEvent<BTClient> eventArgs) {
        Log.v(Constants.TAG, String.format("Connected to Device %s.", eventArgs.getSource().getDevice().getName()));
        ZephyrProtocol protocol = new ZephyrProtocol(eventArgs.getSource().getComms());
        protocol.addZephyrPacketEventListener(this);
    }

    @Override
    public void ReceivedPacket(ZephyrPacketEvent eventArgs) {
    }

    /**
     * set the health care receiver
     *
     * @param mReceiver the m receiver
     */
    public void setReceiver(IHealthCareReceiver mReceiver) {
        this.mReceiver = mReceiver;
    }

    /**
     * The M handler.
     */
    final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == HEART_RATE) {
                int heartRate = msg.getData().getInt(Constants.EXTRAS_HEART_RATE);
                Log.v(Constants.TAG, "Heart Rate is " + heartRate);
                if (mReceiver != null) {
                    mReceiver.onHeartRateReceived(heartRate, mDeviceInfo);
                }
                if (heartRate > 0) {
                    //Constants.sendNotification(mContext, String.format("%s - %s : %d", mDeviceInfo.getDevice_name(), mContext.getString(R.string.heart_rate_is), heartRate), "", Constants.isHearRateCritical(heartRate));
                    createBroadcast(heartRate);
                } /*else {
                    Context applicationContext = mContext.getApplicationContext();
                    Toast.makeText(applicationContext, applicationContext.getString(R.string.zephyr_invalid_data) + "" + heartRate, Toast.LENGTH_SHORT).show();
                }*/
            }
        }

        /**
         * send the broad cast for the heart rate.
         */
        private void createBroadcast(int heartRate) {
            try {
                Intent intent = new Intent();
                JSONObject heartRateJson = new JSONObject();
                heartRateJson.put(Constants.DATA_TYPE_HEART_RATE, heartRate);
                heartRateJson.put("time",Calendar.getInstance().getTimeInMillis()+"");
                intent.putExtra(Constants.EXTRAS_HEALTH_DATA, heartRateJson.toString());
                intent.putExtra(Constants.EXTRAS_DATA_TYPE, Constants.DATA_TYPE_HEART_RATE);
                intent.putExtra(Constants.EXTRAS_DEVICE_ID, mDeviceInfo.getDevice_udi());
                intent.putExtra(Constants.EXTRAS_DEVICE_NAME, mDeviceInfo.getDevice_name());
                intent.putExtra(Constants.EXTRAS_HEART_RATE_LOG_TIME, Calendar.getInstance().getTimeInMillis());
                intent.setAction(Constants.BROADCAST_NEW_DATA_ACTION);
                mContext.sendBroadcast(intent);
            }catch (JSONException je){
                Log.e("welloculus","error occured", je);
            }
        }

    };


}
