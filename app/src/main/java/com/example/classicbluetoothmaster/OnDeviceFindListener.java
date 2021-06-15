package com.example.classicbluetoothmaster;

import android.bluetooth.BluetoothDevice;

public interface OnDeviceFindListener {
    void onDeviceFind(BluetoothDevice device);
    void onDeviceFineFailed();
}
