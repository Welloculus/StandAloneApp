package com.transility.tracker.bluetooth;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.transility.tracker.R;
import com.transility.tracker.app.receiver.IHealthCareReceiver;
import com.transility.tracker.beans.DeviceInfoBean;
import com.transility.tracker.bluetooth.zephyr.ZephyrHandler;
import com.transility.tracker.utils.Constants;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for handling bluetooth connection
 *
 * @author arpit.garg
 */
public class BluetoothHandler {
    private BluetoothAdapter mBluetoothAdapter;
    private static BluetoothHandler INSTANCE;
    private Context mContext;
    private List<BluetoothDevice> mDeviceList = new ArrayList<>();
    private ProgressDialog mProgressDlg;
    private DeviceInfoBean mDeviceInfo;
    private IHealthCareReceiver iHealthCareReceiver = null;
    private ZephyrHandler mZephyrHandler;
    private boolean isDeviceFound = false;
    private boolean scanOnly = true;

    private BluetoothHandler() {

    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static BluetoothHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BluetoothHandler();
        }
        return INSTANCE;
    }

    private void showProgress(String title, String message) {
        mProgressDlg = new ProgressDialog(mContext);
        mProgressDlg.setTitle(title);
        mProgressDlg.setMessage(message);
        mProgressDlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if (mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.cancelDiscovery();
                }
            }
        });
        // mProgressDlg.setCancelable(false);
        mProgressDlg.show();
    }

    /**
     * dismiss the progress dialog
     */
    private void dismissProgress() {
        if (mProgressDlg != null && mProgressDlg.isShowing()) {
            mProgressDlg.dismiss();
            mProgressDlg = null;
        }
    }

    /**
     * disconnect the device connection.
     *
     * @param context the context
     */
    public void disconnect(Context context) {
        mContext = context;
        try {
            if (mBluetoothAdapter != null) {
                mBluetoothAdapter.disable();
            }
            if (iHealthCareReceiver != null)
                iHealthCareReceiver.isConnected(false);
            if (mReceiver != null)
                mContext.getApplicationContext().unregisterReceiver(mReceiver);
            mZephyrHandler.setReceiver(null);
            mDeviceInfo = null;
            mZephyrHandler.disconnect();

        } catch (Exception e) {
            Log.e(Constants.TAG, Log.getStackTraceString(e));
        }
    }

    /**
     * connect the device to bluetooth
     *
     * @param context    the context
     * @param deviceInfo the device info
     * @param receiver   the receiver
     * @throws DeviceConnectException the device connect exception
     */
    public void connectToBluetooth(Context context, DeviceInfoBean deviceInfo, IHealthCareReceiver receiver) throws DeviceConnectException {
        scanOnly = deviceInfo == null;

        isDeviceFound = false;
        iHealthCareReceiver = receiver;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            throw new DeviceConnectException(mContext.getString(R.string.bluetooth_settings_problem));
        }

        mContext = context;
        mDeviceInfo = deviceInfo;
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mContext.getApplicationContext().registerReceiver(mReceiver, filter);

        if (mBluetoothAdapter.getState() != BluetoothAdapter.STATE_ON)
            throw new DeviceConnectException(mContext.getString(R.string.bluetooth_state_not_ready));

        mBluetoothAdapter.startDiscovery();
    }


    /**
     * Receiver to handler the bluetooth actions
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    mDeviceList = new ArrayList<>();
                    showProgress("", mContext.getString(R.string.searching_bluetooth_devices));
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    performDiscoveryFinishedAction();
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    performActionFound(intent);
                    break;
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED: {
                    performBondStateChangeAction(intent);
                    break;
                }
                default:
                    break;

            }
        }

        private void performBondStateChangeAction(Intent intent) {
            final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
            final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);
            if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                showToast(mContext.getString(R.string.device_paired));
                connectToZephyrDevice();
            } else if (state == BluetoothDevice.BOND_NONE) {
                showToast(mContext.getString(R.string.device_pairing_failure));
            }
        }

        private void performActionFound(Intent intent) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (scanOnly) {
                String deviceName = device.getName();
                if (deviceName != null && deviceName.startsWith("HXM")) {
                    /*
	        		 * we found an HxM to try to talk to!, let's remember its name and
	        		 * stop looking for more
	        		 */
                    dismissProgress();
                    if (mBluetoothAdapter.isDiscovering()) {
                        mBluetoothAdapter.cancelDiscovery();
                    }
                    isDeviceFound = true;
                    String deviceAddress = device.getAddress();
                    Log.d("Welloculus", "found a device whose name starts with 'HXM', its name is " + deviceName + " and its address is " + deviceAddress);
                    DeviceInfoBean mDeviceInfo = new DeviceInfoBean("true", deviceAddress, deviceName, "Bluetooth", true);
                    mDeviceInfo.setCurrentValue("Waiting..");
                    iHealthCareReceiver.onZephyrDeviceFound(mDeviceInfo);
                }
            } else {
                if (device.getAddress().equalsIgnoreCase(mDeviceInfo.getDevice_udi())) {
                    performDeviceFoundAction(device);
                }
                mDeviceList.add(device);
            }
        }

        private void performDiscoveryFinishedAction() {
            for (int i = 0; i < mDeviceList.size(); i++) {
                BluetoothDevice bluetoothDevice = mDeviceList.get(i);
                Log.v(Constants.TAG, "name : " + bluetoothDevice.getName() + " address : " + bluetoothDevice.getAddress());
            }
            if (!isDeviceFound) {
                dismissProgress();
                showToast(mContext.getString(R.string.no_connected_device_found));
            }
        }
    };


    private void performDeviceFoundAction(BluetoothDevice device) {
        String display = device.getAddress();
        if (!TextUtils.isEmpty(device.getName())) {
            display = device.getName();
        }
        showToast(mContext.getString(R.string.found_the_device) + " " + display);
        Log.v(Constants.TAG, "Found the device with name : " + device.getName() + " address : " + device.getAddress());
        isDeviceFound = true;
        if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
            connectToZephyrDevice();
        } else {
            showToast(mContext.getString(R.string.device_not_paired));
            try {
                pairDevice(device);
            } catch (Exception e) {
                Log.e(Constants.TAG, Log.getStackTraceString(e));
            }
        }
        dismissProgress();
    }

    /**
     * check if device is connected or not
     *
     * @return true - if connected
     */
    public boolean isConnected() {
        return mZephyrHandler != null && mZephyrHandler.isConnected();
    }

    /**
     * connect to the Zephyr device
     */
    private void connectToZephyrDevice() {
        try {
            if (mZephyrHandler == null) {
                mZephyrHandler = new ZephyrHandler(mContext);
            }
            mZephyrHandler.setReceiver(iHealthCareReceiver);
            mZephyrHandler.connect(mDeviceInfo, mBluetoothAdapter);
        } catch (DeviceConnectException e) {
            showToast(e.getMessage());
            Log.e(Constants.TAG, Log.getStackTraceString(e));
        }
    }


    private boolean pairDevice(BluetoothDevice btDevice) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
        Method createBondMethod = class1.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }

    /**
     * Gets connected device.
     *
     * @return the connected device
     */
    public DeviceInfoBean getConnectedDevice() {
        return mDeviceInfo;
    }

    /**
     * Show toast.
     *
     * @param message the message
     */
    protected void showToast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

}
