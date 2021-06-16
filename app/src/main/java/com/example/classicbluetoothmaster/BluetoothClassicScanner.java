package com.example.classicbluetoothmaster;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

import java.util.Set;

public class BluetoothClassicScanner {

    private BluetoothAdapter bluetoothAdapter;
    private OnDeviceFindListener listener;
    private Set<BluetoothDevice> bondedDevices;

    private final String DEVICE_ADDRESS = "98:D3:21:F4:A2:1B";
    private final String NEW_MAC_ADDRESS = "98:D3:21:F4:93:5C";

    private boolean isDeviceFind = false;

    public BluetoothClassicScanner(OnDeviceFindListener listener) {

        this.listener = listener;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    }

    public void stopScan() {

        bluetoothAdapter.cancelDiscovery();

    }



    public void startScan() {

        //Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        //context.startActivityForResult(intent, REQUEST_ENABLE_BT);

        //1. Bonded 상태의 기기를 먼저 찾아보고 리스너로 날린다.
        bondedDevices = bluetoothAdapter.getBondedDevices();

        for (BluetoothDevice device : bondedDevices) {

            if (device.getAddress().equals(NEW_MAC_ADDRESS)) {

                listener.onDeviceFind(device);
                isDeviceFind = true;
                bluetoothAdapter.cancelDiscovery();

            }

        }

        //위의 단계에서 못 찾았다면 추가로 스캔을 진행한다.
        if (!isDeviceFind) {
            bluetoothAdapter.startDiscovery();
        }

    }


}
