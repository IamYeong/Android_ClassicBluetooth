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

        this.socket = socket;
        this.listener = listener;
        this.handler = handler;
        connectInitialize();
    }

    private void connectInitialize() {

        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch(IOException e) {

        }
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

        readThread = new Thread() {

            @Override
            public void run() {
                super.run();

                try {

                    while (!isInterrupted()) {

                        if (inputStream.available() >= 0) {

                            //n회차 loop때 length 10 이하의 배열을 InputStream에서 읽어온다.
                            byte[] bytes = new byte[10];
                            inputStream.read(bytes);

                            //InputStream에서 읽어온 byte 배열을 다시 한 번 loop하며
                            for (int i = 0; i < bytes.length; i++) {

                                //i번째 byte가 아래에 해당할 때 특정 동작을 수행한다.
                                byte b = bytes[i];

                                if (b == 60) {
                                    thermometer = new StringBuilder("");
                                    humidity = new StringBuilder("");
                                    pressure = new StringBuilder("");
                                    rotate = new StringBuilder("");

                                    colonCount = 0;

                                    continue;
                                } else if (b == 62) {

                                    //가장중요. 그 중 보내온 마지막 문자가 감지되면
                                    //Thread를 열었던 handler에게 Runnable 객체를 보낸다.
                                    //그러면 알아서 MessageQueue에 보내줄 것이다.
                                    //Queue에 들어간 Message는 Looper가 다시 Handler에게 보내서
                                    //동작을 수행한다.

                                    //만약 ":"이 특정값 미만이라면 부정확한 데이터이므로 무시.
                                    if (colonCount == DATA_NUMBER) {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                listener.onThread(thermometer, humidity, pressure, rotate);
                                                //Rotate add
                                            }
                                        });
                                    }

                                } else if (b == 91) {
                                    // : 이면 다음 값으로 넘어가야 함. 58
                                    // [ 이면 다음 값. 91
                                    colonCount++;

                                } else if (b >= 48 && b <= 57) {

                                    switch (colonCount) {

                                        case 1 : thermometer.append(asciiToString(b));
                                            break;

                                        case 2 : humidity.append(asciiToString(b));
                                            break;

                                        case 3 : pressure.append(asciiToString(b));
                                            break;

                                        case 4 : rotate.append(asciiToString(b));
                                            break;

                                    }

                                } else if (b == 83) {

                                    listener.onStartReadData();

                                    /*

                                    if (isSignalGet) {

                                        listener.onStartReadData();
                                    }


                                     */

                                } else if (b == 2) {
                                    isSignalGet = true;
                                } else if (b == 3) {
                                    isEndedSignalGet = true;
                                } else if (b == 69) {
                                    if (isEndedSignalGet) {
                                        listener.onEndReadData();
                                    }
                                }


                            }


                        } else {
                            connectInitialize();
                        }

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

                    while (!isInterrupted()) {

                        if (outputStream != null) {

                            Thread.sleep(1000);
                            outputStream.write(WRITE_DATA);

                        } else {
                            connectInitialize();
                        }

                    }

                } catch(Exception e) {
                    currentThread().interrupt();
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


            case 53 :
                return "5";


            case 54 :
                return "6";


            case 55 :
                return "7";


            case 56 :
                return "8";

            case 57 :
                return "9";



        }

        return "NULL";

    }
}
