package com.example.classicbluetoothmaster;

public interface OnThreadListener {
    void onThread(StringBuilder thermometer, StringBuilder humidity, StringBuilder pressure, StringBuilder rotate);
    void onThreadOtherValue(StringBuilder value);
    void onStartReadData();
    void onEndReadData();
}
