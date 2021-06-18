package com.example.classicbluetoothmaster;

public class TRData {

    /**2021.06.18 by GwangyeongJeong
     *
     * Data flow is RxTxThread -> InputStream -> byte[] -> asciiToString() -> ConnectionActivity -> setText() and addEntry(String, int)
     * So, more efficient data type String
     */
    private StringBuilder thermometer;
    private StringBuilder humidity;
    private StringBuilder  pressure;
    private StringBuilder rotate;

    public TRData(StringBuilder thermometer, StringBuilder humidity, StringBuilder pressure, StringBuilder rotate) {
        this.thermometer = thermometer;
        this.humidity = humidity;
        this.pressure = pressure;
        this.rotate = rotate;
    }

    public StringBuilder getThermometer() {
        return thermometer;
    }

    public void setThermometer(StringBuilder thermometer) {
        this.thermometer = thermometer;
    }

    public StringBuilder getHumidity() {
        return humidity;
    }

    public void setHumidity(StringBuilder humidity) {
        this.humidity = humidity;
    }

    public StringBuilder getPressure() {
        return pressure;
    }

    public void setPressure(StringBuilder pressure) {
        this.pressure = pressure;
    }

    public StringBuilder getRotate() {
        return rotate;
    }

    public void setRotate(StringBuilder rotate) {
        this.rotate = rotate;
    }


}
