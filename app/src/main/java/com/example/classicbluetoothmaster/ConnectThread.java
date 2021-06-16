package com.example.classicbluetoothmaster;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;

import java.io.IOException;
import java.util.UUID;

public class ConnectThread extends Thread {

    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OnLogAddedListener listener;
    private Handler handler;

    public static String SOCKET_CREATE_FAIL = "tr.iamyeong.socket_create_fail";
    public static String SOCKET_CONNECT_FAIL = "tr.iamyeong.socket_connect_fail";
    public static String SOCKET_CLOSE_FAIL = "tr.iamyeong.socket_close_fail";
    public static String SOCKET_RETURN_FAIL = "tr.iamyeong.socket_return_fail";

    //Serial communication UUID
    private final UUID TR_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    //Constuct & getSocket
    public ConnectThread(BluetoothDevice device, OnLogAddedListener listener, Handler handler) {
        this.device = device;
        BluetoothSocket mSocket = null;
        this.listener = listener;

        try {
            mSocket = device.createRfcommSocketToServiceRecord(TR_UUID);
        } catch(IOException e) {
            //연결 실패
            this.listener.onLogAdded(SOCKET_CREATE_FAIL);
        }

        this.socket = mSocket;

    }

    @Override
    public void run() {
        super.run();

        try {

            socket.connect();

        } catch(IOException e) {

            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onLogAdded(SOCKET_CONNECT_FAIL);
                }
            });


        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.onLogAdded("SUCCESS");
            }
        });

        //통신



    }

    public void cancel() {

        try {

            socket.close();

        } catch(IOException e) {
            listener.onLogAdded(SOCKET_CLOSE_FAIL);
        }


    }

    public BluetoothSocket getSocket() {

        if (socket != null) {
            return socket;
        }

        listener.onLogAdded(SOCKET_RETURN_FAIL);

        return null;
    }

}
