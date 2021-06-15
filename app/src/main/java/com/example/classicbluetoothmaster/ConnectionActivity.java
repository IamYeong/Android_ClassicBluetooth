package com.example.classicbluetoothmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ConnectionActivity extends AppCompatActivity implements OnThreadListener {

    private Handler handler;
    private Thread chartThread;
    private TextView tv_thermo, tv_humidity, tv_pressure, tv_rotate;

    private ImageView img_signal;

    private BluetoothConnectManager manager;
    private Button btn_fvc;
    private InputStream inputStream;
    private boolean isChartInit = false;

    private LineChart lineChart;
    private LineDataSet lineDataSet;
    private LineData lineData;
    private List<Entry> entries;

    private String thermometer, humidity, pressure, rotate;
    private int count = 0;

    private Intent intent;
    private BluetoothDevice device;

    private RxTxThread txRxThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        tv_thermo = findViewById(R.id.tv_thermometer_fvc);
        tv_humidity = findViewById(R.id.tv_humidity_fvc);
        tv_pressure = findViewById(R.id.tv_pressure_fvc);
        tv_rotate = findViewById(R.id.tv_rotate_fvc);

        img_signal = findViewById(R.id.img_sgnal_connection);

        lineChart = findViewById(R.id.line_chart_fvc);

        btn_fvc = findViewById(R.id.btn_fvc);

        intent = getIntent();
        device = intent.getParcelableExtra("DEVICE");

        handler = new Handler();
        txRxThread = new RxTxThread(handler, device, this);

        txRxThread.readStart();

        btn_fvc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txRxThread.writeStart();
            }
        });

    }

    private void createChart() {

        //final Handler handler = new Handler();
        chartThread = new Thread() {
            @Override
            public void run() {

                initChart();

                /*
                while(true) {

                    try {
                        Thread.sleep(100);
                    } catch (Exception e) { }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            if (rotate != null && !rotate.equals("")) {
                                addEntry(rotate, count);
                                count++;
                            }

                        }
                    });

                }

                 */

            }
        };

        chartThread.start();

    }

    private void addEntry(String y, int x) {


        float yValue = (float) Integer.parseInt(y);
        float xValue = (float) x;

        entries.add(new Entry(xValue, yValue));

        /*
        if (entries.size() > 500) {
            entries.remove(0);
        }

         */

        lineDataSet.notifyDataSetChanged();
        lineData.notifyDataChanged();
        lineChart.notifyDataSetChanged();

        lineChart.invalidate();

        count++;

    }

    private void initChart() {

        //여기서 미리 속성 반영할 것
        XAxis xAxis = lineChart.getXAxis();

        entries = new ArrayList<>();
        lineDataSet = new LineDataSet(entries, "예시");
        lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);

        YAxis yRight = lineChart.getAxisRight();
        YAxis yLeft = lineChart.getAxisLeft();

        xAxis.setEnabled(false);
        yLeft.setEnabled(false);
        yRight.setEnabled(false);


        /*
        xAxis.setAxisMaximum(1000f);
        yRight.setAxisMaximum(1000f);
        yLeft.setAxisMaximum(1000f);

         */

        //xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        lineChart.setDragEnabled(true);
        lineChart.setAutoScaleMinMaxEnabled(true);

        lineChart.getDescription().setEnabled(false);
        //lineChart.setVisibleXRangeMaximum(5);
        //lineChart.setDrawMarkers(false);
        //lineChart.setDrawGridBackground(false);
        lineChart.getLegend().setEnabled(false);
        //lineChart.setDrawMarkers(false);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawValues(false);
        lineDataSet.setColor(Color.RED);
        lineDataSet.setLineWidth(1f);

        isChartInit = true;

    }

    @Override
    public void onThread(StringBuilder thermometer, StringBuilder humidity, StringBuilder pressure, StringBuilder rotate) {

        this.thermometer = thermometer.toString();
        this.humidity = humidity.toString();
        this.pressure = pressure.toString();
        this.rotate = rotate.toString();

        tv_thermo.setText(thermometer);
        tv_humidity.setText(humidity);
        tv_pressure.setText(pressure);
        tv_rotate.setText(rotate);

        if (isChartInit) {
            addEntry(this.rotate, count);
        }

    }

    @Override
    public void onStartReadData() {

        createChart();
        img_signal.setBackgroundColor(Color.GREEN);

    }

    @Override
    public void onEndReadData() {

        txRxThread.stopReadThread();
        img_signal.setBackgroundColor(Color.RED);

    }
}