package com.example.classicbluetoothmaster;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;

import java.io.IOException;
import java.util.UUID;

public class ConnectThread extends Thread {

    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OnLogAddedListener listener;

    //Serial communication UUID
    private final UUID TR_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    //Constuct & getSocket
    public ConnectThread(BluetoothDevice device, OnLogAddedListener listener) {
        this.device = device;
        BluetoothSocket mSocket = null;
        this.listener = listener;

        try {
            mSocket = device.createRfcommSocketToServiceRecord(TR_UUID);
        } catch(IOException e) {
            //연결 실패
            this.listener.onLogAdded("소켓찾기실패");
        }

        this.socket = mSocket;

    }

    @Override
    public void run() {
        super.run();

        try {

            socket.connect();

        } catch(IOException e) {

            listener.onLogAdded("연결실패");

        }

        //통신
        listener.onLogAdded("SUCCESS");


    }

    public void cancel() {

        try {

            socket.close();

        } catch(IOException e) {
            listener.onLogAdded("CLOSE");
        }


    }

    public BluetoothSocket getSocket() {

        if (socket != null) {
            return socket;
        }

        listener.onLogAdded("실패");

        return null;
    }

}
