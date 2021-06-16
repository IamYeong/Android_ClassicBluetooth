package com.example.classicbluetoothmaster;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.IOException;
import java.util.UUID;

public class AcceptThread extends Thread {

    private BluetoothDevice device;
    private BluetoothServerSocket serverSocket;
    private BluetoothSocket socket;
    private OnLogAddedListener listener;
    private Handler handler;
    private BluetoothAdapter bluetoothAdapter;

    private boolean isConnectingSuccess = true;

    public static String SOCKET_CREATE_FAIL = "tr.iamyeong.socket_create_fail";
    public static String SOCKET_CONNECT_FAIL = "tr.iamyeong.socket_connect_fail";
    public static String SOCKET_CLOSE_FAIL = "tr.iamyeong.socket_close_fail";
    public static String SOCKET_RETURN_FAIL = "tr.iamyeong.socket_return_fail";

    //Serial communication UUID
    private final UUID TR_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    public AcceptThread(BluetoothDevice device, OnLogAddedListener listener, Handler handler) {

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.handler = handler;
        this.device = device;
        BluetoothSocket mSocket = null;
        this.listener = listener;

    }

    @Override
    public void run() {
        super.run();

        try {
            serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord("HC-06", TR_UUID);
            socket = serverSocket.accept();

            listener.onLogAdded("serverSocket : " + serverSocket + ", " + "socket : " + socket);

            serverSocket.close();

        } catch(IOException e) {
            //연결 실패
            this.listener.onLogAdded(SOCKET_CREATE_FAIL);
        }

        listener.onLogAdded("SUCCESS");

    }

    public BluetoothSocket getAcceptSocket() {

        if (socket != null) {
            return socket;
        }

        return null;
    }


}