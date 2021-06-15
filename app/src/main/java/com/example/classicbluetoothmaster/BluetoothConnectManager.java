package com.example.classicbluetoothmaster;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

public class BluetoothConnectManager {

    private BluetoothSocket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private BluetoothDevice device;

    //Gatt Health thermometer
    private UUID BLE_UUID = UUID.fromString("00001809-0000-1000-8000-00805f9b34fb");

    //Serial port
    private UUID HC06_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    public BluetoothConnectManager() {}

    public boolean connecting(BluetoothDevice device) {

        try {

            this.device = device;
            socket = createSocket();
            socket.connect();
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            if (inputStream.available() >= 0) {
                return true;
            }

        } catch (Exception e) {

        }

        return false;
    }

    public InputStream getInputStream() {

        if (inputStream != null) {
            return inputStream;
        }

        return null;
    }

    public OutputStream getOutputStream() {

        if (outputStream != null) {
            return outputStream;
        }

        return null;
    }


    private BluetoothSocket createSocket() throws IOException {

        if (Build.VERSION.SDK_INT >= 10){
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] {UUID.class});
                return (BluetoothSocket) m.invoke(device, HC06_UUID);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return device.createInsecureRfcommSocketToServiceRecord(HC06_UUID);

    }

}
