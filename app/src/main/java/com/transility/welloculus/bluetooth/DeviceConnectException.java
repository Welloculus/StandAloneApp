package com.transility.welloculus.bluetooth;


public class DeviceConnectException extends Exception {

    private String message = null;

    public DeviceConnectException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}