package com.example.classicbluetoothmaster;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;

public class BluetoothClassicScanner {

    private final int REQUEST_ENABLE_BT = 1;

    private BluetoothAdapter bluetoothAdapter;
    private Activity context;

    public BluetoothClassicScanner(Context activityContext) {

        this.context = (Activity) activityContext;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter != null) {

            startScan();

        }
    }

    public void stopScan() {

        bluetoothAdapter.cancelDiscovery();

    }



    public void startScan() {

        //Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        //context.startActivityForResult(intent, REQUEST_ENABLE_BT);
        bluetoothAdapter.startDiscovery();

    }


}
