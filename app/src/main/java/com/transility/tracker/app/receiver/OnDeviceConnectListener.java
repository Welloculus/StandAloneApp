package com.transility.tracker.app.receiver;

import com.transility.tracker.beans.DeviceInfoBean;

/**
 * The interface On device connect listener.
 */
public interface OnDeviceConnectListener {

    void onConnectClick(DeviceInfoBean deviceInfo);

}