package com.example.classicbluetoothmaster;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

public class ConnectThread extends Thread {

    private BluetoothDevice device;
    private BluetoothServerSocket serverSocket;
    private BluetoothSocket socket;
    private OnLogAddedListener listener;
    private Handler handler;

    private boolean isConnectingSuccess = true;

    public static String SOCKET_CONNECT_SUCCESS = "tr.iamyeong.socket_connect_success";
    public static String SOCKET_CREATE_FAIL = "tr.iamyeong.socket_create_fail";
    public static String SOCKET_CONNECT_FAIL = "tr.iamyeong.socket_connect_fail";
    public static String SOCKET_CLOSE_FAIL = "tr.iamyeong.socket_close_fail";
    public static String SOCKET_RETURN_FAIL = "tr.iamyeong.socket_return_fail";

    //Serial communication UUID
    private final UUID TR_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private final String MAC_ADDRESS = "98:D3:21:F4:93:5C";

    //Constuct & getSocket
    public ConnectThread(BluetoothDevice device, OnLogAddedListener listener, Handler handler) {

        this.handler = handler;
        this.device = device;
        socket = null;
        this.listener = listener;

    }

    @Override
    public void run() {
        //super.run();

        try {

            device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(MAC_ADDRESS);
            //socket = device.createInsecureRfcommSocketToServiceRecord(TR_UUID);
            //socket = createInsecureSocket(device);
            //socket = device.createRfcommSocketToServiceRecord(TR_UUID);

            socket = device.createInsecureRfcommSocketToServiceRecord(TR_UUID);

            socket.connect();
            socket.close();

            //socket은 내부적으로 connect 가 완료되면 필드에 인풋/아웃풋 스트림을 저장함.

        } catch(IOException e) {

            isConnectingSuccess = false;

            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onLogAdded(SOCKET_CONNECT_FAIL);
                }
            });

        }

        if (isConnectingSuccess) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onLogAdded(SOCKET_CONNECT_SUCCESS);
                }
            });
        }


    }

    private BluetoothSocket createInsecureSocket(BluetoothDevice mDevice) {

        try {
            BluetoothSocket socket = (BluetoothSocket) mDevice.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", int.class).invoke(mDevice, 1);
        } catch(Exception e) {

        }

        return socket;

    }

    public void cancel() {

        try {

            socket.close();

        } catch(IOException e) {
            listener.onLogAdded("Socket close()");
        }


    }

    public BluetoothSocket getSocket() {

        if (socket != null) {
            return socket;
        }

        listener.onLogAdded(SOCKET_RETURN_FAIL);

        return null;
    }

    /*
    private BluetoothSocket createSocket() {

        Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
        return (BluetoothSocket) m.invoke(device);

    }

     */

}
