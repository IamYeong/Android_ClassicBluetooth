package com.example.classicbluetoothmaster;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class RxTxThread {

    //Message.what Constant
    public final static int MESSAGE_DATA = 1;
    public final static int MESSAGE_START = 0;
    public final static int MESSAGE_END = 2;
    public final static int MESSAGE_OTHER = 3;
    public final static int MESSAGE_WRITE = 4;

    private final String TR_ACTION_START = "S";

    private Thread readThread, writeThread;
    private BufferedInputStream bufferedInputStream;

    private BluetoothSocket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private OnLogAddedListener logAddedListener;

    private boolean isSignalGet = false;
    private boolean isEndedSignalGet = false;

    private StringBuilder thermometer = new StringBuilder();
    private StringBuilder humidity = new StringBuilder();
    private StringBuilder pressure = new StringBuilder();
    private StringBuilder rotate = new StringBuilder();
    private Handler handler;

    private int colonCount = 0;
    private final int DATA_NUMBER = 4;
    private final byte WRITE_DATA = 66;

    public RxTxThread(Handler handler, OnLogAddedListener listener, BluetoothSocket socket) {

        this.logAddedListener = listener;
        this.handler = handler;
        logAddedListener.onLogAdded("RxTxThread construct");

        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            logAddedListener.onLogAdded(inputStream + ",Exception " + outputStream);
        }

        logAddedListener.onLogAdded(inputStream + ", " + outputStream);


    }


    public void stopReadThread() {

        if (readThread != null) {
            readThread.interrupt();
        }

    }

    public void stopWriteThread() {

        if (writeThread != null) {
            writeThread.interrupt();
        }

    }


    public void readStart() {

        //ByteArrayInputStream

        logAddedListener.onLogAdded("readStart()");

        readThread = new Thread() {

            @Override
            public void run() {
                super.run();

                while (!isInterrupted()) {

                    try {

                        int available = inputStream.available();

                        if (available > 0) {

                            byte[] bytes = new byte[1024];
                            inputStream.read(bytes);

                            for (int i = 0; i < bytes.length; i++) {

                                byte b = bytes[i];

                                switch (b) {

                                    //Start
                                    //case 0x02 :
                                    case 2 :
                                        if (!isSignalGet) {
                                            isSignalGet = true;

                                            Message startMsg = new Message();
                                            startMsg.what = MESSAGE_START;

                                            handler.sendMessage(startMsg);

                                        }
                                        break;

                                    //End
                                    //case 0x03 :
                                    case 3 :
                                        if (!isEndedSignalGet) {
                                            isEndedSignalGet = true;

                                            Message endMsg = new Message();
                                            endMsg.what = MESSAGE_END;

                                            handler.sendMessage(endMsg);
                                        }

                                    //"<"
                                    //case 0x3C :
                                    case 60 :

                                        thermometer = new StringBuilder();
                                        humidity = new StringBuilder();
                                        pressure = new StringBuilder();
                                        rotate = new StringBuilder();
                                        colonCount = 0;

                                        break;

                                    //">"
                                    //case 0x02 :
                                    case 62 :

                                        if (colonCount == DATA_NUMBER) {

                                            Message msgOther = new Message();
                                            msgOther.what = MESSAGE_DATA;

                                            msgOther.obj = new TRData(thermometer, humidity, pressure, rotate);
                                            handler.sendMessage(msgOther);

                                        } else {

                                        }

                                        break;

                                    //":"
                                    //case 0x3A :
                                    case 58 :
                                        colonCount++;
                                        break;

                                }

                                //0~9
                                //if (b >= 0x30 && b <= 0x39) {
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

                    } catch(Exception e) {

                        currentThread().interrupt();

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                logAddedListener.onLogAdded(e + "");
                            }
                        });



                    }

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

                    Message message = new Message();
                    message.what = MESSAGE_WRITE;
                    message.arg1 = WRITE_DATA;
                    handler.sendMessage(message);

                } catch(Exception e) {

                    Message messageEND = new Message();
                    messageEND.what = MESSAGE_END;
                    //messageEND.arg1 = Integer.parseInt(e + "");
                    handler.sendMessage(messageEND);


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

        return null;

    }

}