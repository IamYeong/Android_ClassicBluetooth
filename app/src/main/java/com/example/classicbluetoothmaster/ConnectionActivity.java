package com.example.classicbluetoothmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
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

public class ConnectionActivity extends AppCompatActivity implements OnThreadListener, OnLogAddedListener {

    private Handler handler;
    private Thread chartThread;
    private ConnectThread connectThread;
    //private AcceptThread acceptThread;

    private TextView tv_thermo, tv_humidity, tv_pressure, tv_rotate, tv_log, tv_connecting;
    private FrameLayout frameLayout;
    private Button btn_connect;

    private ImageView img_signal;
    private Button btn_fvc;
    //private InputStream inputStream;
    private boolean isChartInit = false;
    private boolean isStart = false;

    private LineChart lineChart;
    private LineDataSet lineDataSet;
    private LineData lineData;
    private List<Entry> entries;

    private String thermometer, humidity, pressure, rotate;
    private int count = 0;

    private Intent intent;
    private BluetoothDevice device;

    private RxTxThread txRxThread;

    private Paint paint;
    private Path path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        //받는 데이터 텍스트
        tv_thermo = findViewById(R.id.tv_thermometer_fvc);
        tv_humidity = findViewById(R.id.tv_humidity_fvc);
        tv_pressure = findViewById(R.id.tv_pressure_fvc);
        tv_rotate = findViewById(R.id.tv_rotate_fvc);
        tv_connecting = findViewById(R.id.tv_connecting);

        //로그 텍스트
        tv_log = findViewById(R.id.tv_log_connection);
        tv_log.setText("log : ");

        frameLayout = findViewById(R.id.frame_connection);

        //연결 버튼
        btn_connect = findViewById(R.id.btn_connection);
        btn_connect.setVisibility(View.INVISIBLE);

        //차트, 점등 이미지
        img_signal = findViewById(R.id.img_sgnal_connection);
        lineChart = findViewById(R.id.line_chart_fvc);
        btn_fvc = findViewById(R.id.btn_fvc);

        //디바이스 받아오기
        intent = getIntent();
        device = intent.getParcelableExtra("DEVICE");

        //acceptSocket();
        connectSocket();
        createChart();
        tv_log.append("\n createChart(), connectSocket()");

        btn_fvc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txRxThread.writeStart();
            }
        });

        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //acceptSocket();
                connectSocket();
            }
        });

    }//onCreate

    private void connectSocket() {

        btn_connect.setVisibility(View.INVISIBLE);
        img_signal.setBackgroundColor(Color.WHITE);
        tv_log.setVisibility(View.VISIBLE);
        //메인스레드에서 생성한 디폴트 생성자 핸들러를 새로 생성하여 할당.
        handler = new Handler();

        connectThread = new ConnectThread(device, this, handler);
        connectThread.start();

    }

    private void acceptSocket() {
        btn_connect.setVisibility(View.INVISIBLE);
        img_signal.setBackgroundColor(Color.WHITE);
        tv_log.setVisibility(View.VISIBLE);

        handler = new Handler();

        connectThread = new ConnectThread(device, this, handler);
        connectThread.start();


        //acceptThread = new AcceptThread(device, this, handler);
        //acceptThread.start();


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void createChart() {

        //final Handler handler = new Handler();
        chartThread = new Thread() {
            @Override
            public void run() {

                initChart();

            }
        };

        chartThread.start();

    }

    private void addEntry(String y, int x) {

        if (y != null && !y.equals("")) {

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

    }

    private void initViewChart(float initX, float initY) {
        path = new Path();
        paint = new Paint();

        path.moveTo(initX, initY);


    }

    private void addPath(String value, int count) {

        float x = (float) Integer.parseInt(value);
        float y = (float) count;


        //1. 직전값 저장
        //2. 변화량 측정
        //3. 유효한 값 확인
        //4. 좌표변환

        path.lineTo(x, y);
        LineView line = new LineView(ConnectionActivity.this, paint, path);

        frameLayout.removeAllViews();
        frameLayout.addView(line);

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

        //tv_log.append("\n" + "onThread()");
        //tv_log.append("\n" + thermometer + ", " + humidity + ", " + pressure + ", " + rotate);

        tv_log.append("\n" + "onThread()" + ", boolean is" + isStart);

        if (isStart) {

            this.thermometer = thermometer.toString();
            this.humidity = humidity.toString();
            this.pressure = pressure.toString();
            this.rotate = rotate.toString();

            tv_thermo.setText(thermometer);
            tv_humidity.setText(humidity);
            tv_pressure.setText(pressure);
            tv_rotate.setText(rotate);

            if (isChartInit) {
                tv_log.append("\n" + "addEntry()");
                addEntry(this.rotate, count);

            }

        }



    }

    @Override
    public void onThreadOtherValue(StringBuilder value) {

        tv_log.append("\n" + value.toString());

    }

    @Override
    public void onStartReadData() {

        isStart = true;
        img_signal.setBackgroundColor(Color.GREEN);
        tv_log.append("\n" + "onStartRead_S");

    }

    @Override
    public void onEndReadData() {

        txRxThread.stopReadThread();
        chartThread.interrupt();
        connectThread.cancel();

        img_signal.setBackgroundColor(Color.RED);

        btn_connect.setVisibility(View.VISIBLE);


        //멈췄을 때의 반응 구현 필요 (*ex 결과창 보여주기)

    }

    @Override
    public void onLogAdded(String log) {

        if (log.equals("SUCCESS")) {

            tv_connecting.setVisibility(View.INVISIBLE);
            BluetoothSocket socket = connectThread.getSocket();
            tv_log.append("\n" + socket.toString());

            handler = new Handler();
            RxTxThread thread = new RxTxThread(handler, this, socket);
            thread.readStart();
            tv_log.append("\n readStart()");

            
            //준비완료되면 데이터 보낼 수 있도록
            //thread.writeStart();

        }

        if (log.equals(ConnectThread.SOCKET_CREATE_FAIL)) {
            //연결 실패했을 경우.\
            tv_log.append("\n" + log);
            btn_connect.setVisibility(View.VISIBLE);
            img_signal.setBackgroundColor(Color.RED);
            tv_connecting.setVisibility(View.INVISIBLE);

        } else if (log.equals(ConnectThread.SOCKET_CONNECT_FAIL)) {
            tv_log.append("\n" + log);
            btn_connect.setVisibility(View.VISIBLE);
            img_signal.setBackgroundColor(Color.RED);
            tv_connecting.setVisibility(View.INVISIBLE);

        } else if (log.equals(ConnectThread.SOCKET_RETURN_FAIL)) {
            tv_log.append("\n" + log);
            btn_connect.setVisibility(View.VISIBLE);
            img_signal.setBackgroundColor(Color.RED);
            tv_connecting.setVisibility(View.INVISIBLE);

        } else if (log.equals(ConnectThread.SOCKET_CLOSE_FAIL)) {
            tv_log.append("\n" + log);
            btn_connect.setVisibility(View.VISIBLE);
            img_signal.setBackgroundColor(Color.RED);
            tv_connecting.setVisibility(View.INVISIBLE);

        } else {
            tv_log.append("\n" + log);
        }

        //성공해서 바로 통신해야 하는 경우


    }
}