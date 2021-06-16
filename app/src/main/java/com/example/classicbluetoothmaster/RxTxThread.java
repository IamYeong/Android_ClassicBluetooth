package com.example.classicbluetoothmaster;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RxTxThread {

    private final String TR_ACTION_START = "S";

    private Thread readThread, writeThread;

    private BluetoothSocket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private OnThreadListener listener;
    private OnLogAddedListener logAddedListener;

    private boolean isSignalGet = false;
    private boolean isEndedSignalGet = false;

    private StringBuilder thermometer;
    private StringBuilder humidity;
    private StringBuilder pressure;
    private StringBuilder rotate;
    private Handler handler;

    private int colonCount = 0;
    private final int DATA_NUMBER = 4;
    private final byte WRITE_DATA = 66;

    public RxTxThread(Handler handler, OnThreadListener listener, BluetoothSocket socket) {

        this.listener = listener;
        this.logAddedListener = (OnLogAddedListener) listener;
        this.handler = handler;

        logAddedListener.onLogAdded("RxTxThread construct");

        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch(IOException e) {
            logAddedListener.onLogAdded(inputStream + ", " + outputStream);
        }

        logAddedListener.onLogAdded(inputStream + ", " + outputStream);


    }



    public void stopReadThread() {

        if (readThread != null) {
            readThread.interrupt();
        }

    }

    public void stopWriteThread(){

        if (writeThread != null) {
            writeThread.interrupt();
        }

    }


    public void readStart() {

        logAddedListener.onLogAdded("readStart()");

        readThread = new Thread() {

            @Override
            public void run() {
                super.run();

                try {

                    //Thread 가 살아있다면 일단 loop실행
                    while (!isInterrupted()) {

                        //1Byte 를 Serial 통신으로 받더라도 아래 로직을 수행할 수 있어야 함.
                            byte[] bytes = new byte[1];
                            inputStream.read(bytes);


                            //Default value is 0!
                            if (bytes[0] != 0) {


                                for (int i = 0; i < bytes.length; i++) {

                                    //i번째 byte가 아래에 해당할 때 특정 동작을 수행한다.
                                    byte b = bytes[i];

                                    switch (b) {

                                        case 0x02 :
                                            if (!isSignalGet) {
                                                isSignalGet = true;
                                                listener.onStartReadData();
                                            }
                                            break;

                                        case 0x3C :

                                            thermometer = new StringBuilder("");
                                            humidity = new StringBuilder("");
                                            pressure = new StringBuilder("");
                                            rotate = new StringBuilder("");
                                            colonCount = 0;

                                            break;


                                        case 0x3E :

                                            if (colonCount == DATA_NUMBER) {
                                                handler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        listener.onThread(thermometer, humidity, pressure, rotate);
                                                    }
                                                });

                                            } else {
                                                listener.onThreadOtherValue(thermometer.append(humidity.append(pressure.append(rotate))));
                                            }

                                            break;

                                        case 0x3A :
                                            colonCount++;
                                            break;

                                    }

                                    if (b >= 0x30 && b <= 0x39) {

                                        switch (colonCount) {

                                            case 0:
                                                thermometer.append(asciiToString(b));
                                                break;

                                            case 1:
                                                humidity.append(asciiToString(b));
                                                break;

                                            case 2:
                                                pressure.append(asciiToString(b));
                                                break;

                                            case 3:
                                                rotate.append(asciiToString(b));
                                                break;

                                        }
                                    }

                                }
                            }
                            //InputStream에서 읽어온 byte 배열을 다시 한 번 loop하며

                        }


                } catch(Exception e) {
                    currentThread().interrupt();
                }

            }
        };

        readThread.start();

    }

    public void writeStart() {

        writeThread = new Thread() {

            @Override
            public void run() {
                super.run();

                try {

                    outputStream.write(WRITE_DATA);

                } catch(IOException e) {
                    listener.onEndReadData();
                }

            }
        };

        writeThread.start();


    }

    private String asciiToString(Byte b) {

        switch (b) {

            case 0x30 :
                return "0";


            case 0x31 :
                return "1";


            case 0x32 :
                return "2";

            case 0x33 :
                return "3";


            case 0x34 :
                return "4";


            case 0x35 :
                return "5";


            case 0x36 :
                return "6";


            case 0x37 :
                return "7";


            case 0x38 :
                return "8";

            case 0x39 :
                return "9";


        }

        return "NULL";

    }
}
