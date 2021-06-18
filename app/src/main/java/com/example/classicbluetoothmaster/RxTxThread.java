package com.example.classicbluetoothmaster;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class RxTxThread {

    //Message.what Constant
    public final static int MESSAGE_DATA = 1;
    public final static int MESSAGE_START = 0;
    public final static int MESSAGE_END = 2;

    private final String TR_ACTION_START = "S";

    private Thread readThread, writeThread;
    private BufferedInputStream bufferedInputStream;

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
            logAddedListener.onLogAdded(inputStream + ",Exception " + outputStream);
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

                while(!isInterrupted()) {

                    byte[] bytes = new byte[1024];

                    try {

                        //실패
                        //int result = bufferedInputStream.read(bytes);

                        Thread.sleep(100);

                        thermometer = new StringBuilder();
                        humidity = new StringBuilder();
                        pressure = new StringBuilder();
                        rotate = new StringBuilder();

                        thermometer.append("30");
                        humidity.append("40");
                        pressure.append("998");
                        rotate.append("5");

                        Message msg = new Message();
                        msg.what = MESSAGE_DATA;
                        msg.obj = new TRData(thermometer, humidity, pressure, rotate);
                        handler.sendMessage(msg);


                    } catch (Exception e) {

                    }

                }

                /*
                try {
                    //Thread 가 살아있다면 일단 loop실행
                    while (!isInterrupted()) {

                        //1Byte 를 Serial 통신으로 받더라도 아래 로직을 수행할 수 있어야 함.

                        //Byte stream 은 배열에 저장하고, 결과 int 값은 result 에 저장.
                        byte[] bytes = new byte[1024];
                        int result;

                        result = inputStream.read(bytes);
                        logAddedListener.onLogAdded("Bytes : " + bytes + ", result : " + result);

                        //Default value is 0!
                        if (result != -1) {

                            for (int i = 0; i < result; i++) {

                                //i번째 byte가 아래에 해당할 때 특정 동작을 수행한다.
                                byte b = bytes[i];
                                logAddedListener.onLogAdded(b + "<< bytes[" + i + "]");

                                switch (b) {

                                    case 2:
                                        if (!isSignalGet) {
                                            isSignalGet = true;
                                            listener.onStartReadData();
                                        }
                                        break;

                                    case 3:
                                        if (!isEndedSignalGet) {
                                            isEndedSignalGet = true;
                                            listener.onEndReadData();
                                        }

                                    case 60 :

                                        thermometer = new StringBuilder("");
                                        humidity = new StringBuilder("");
                                        pressure = new StringBuilder("");
                                        rotate = new StringBuilder("");
                                        colonCount = 0;

                                        break;


                                    case 62 :

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

                                    case 58 :
                                        colonCount++;
                                        break;

                                }

                                if (b >= 48 && b <= 57) {

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

                    }

                } catch(Exception e) {

                }

                 */

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

            case 48 :
                return "0";


            case 49 :
                return "1";


            case 50 :
                return "2";

            case 51 :
                return "3";


            case 52 :
                return "4";


            case 0x53 :
                return "5";


            case 0x54 :
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
