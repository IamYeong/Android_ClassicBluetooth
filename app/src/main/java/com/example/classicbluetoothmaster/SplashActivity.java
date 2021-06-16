package com.example.classicbluetoothmaster;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class SplashActivity extends AppCompatActivity implements OnDeviceFindListener {

    private TextView tv_log;
    private Button btn_signIn, btn_signUp, btn_restart;
    //private BluetoothClassicScanner classicScanner;

    //private BroadcastReceiver receiver;
    private BluetoothDevice device;
    private BluetoothClassicScanner classicScanner;

    private final long DELAY_TIME = 5000;
    private final String DEVICE_ADDRESS = "98:D3:21:F4:A2:1B";
    private final String NEW_MAC_ADDRESS = "98:D3:21:F4:93:5C";

    private final String DEVICE_NAME = "HC-06";
    private final int BT_REQUEST_ENABLE = 1;
    private final int LOCATION_REQUEST_ENABLE = 2;

    private Intent signInIntent;
    private Intent signUpIntent;
    private IntentFilter intentFilter;
    private boolean scanning = false;
    private boolean findDevice = false;

    //Classic scanner field
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            tv_log.append("\n onReceive()");
            tv_log.append("\n " + action);

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {

                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String name = device.getName();
                tv_log.append("\n" + name + "\n" + device.getAddress());

                if (device.getAddress().equals(NEW_MAC_ADDRESS)) {

                    Toast.makeText(context, name + "과 연결", Toast.LENGTH_SHORT).show();

                    signInIntent = new Intent(SplashActivity.this, MainActivity.class);
                    signInIntent.putExtra("DEVICE", device);

                    btn_signIn.setVisibility(View.VISIBLE);
                    btn_signUp.setVisibility(View.VISIBLE);

                    classicScanner.stopScan();

                }

            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        tv_log = findViewById(R.id.tv_log_splash);
        tv_log.setText("log : ");
        //tv_log.setVisibility(View.INVISIBLE);

        btn_signIn = findViewById(R.id.btn_signIn);
        btn_signIn.setVisibility(View.INVISIBLE);
        //btn_signIn.setClickable(false);

        btn_signUp = findViewById(R.id.btn_signUp);
        btn_signUp.setVisibility(View.INVISIBLE);
        //btn_signUp.setClickable(false);

        btn_restart = findViewById(R.id.btn_rescan);
        btn_restart.setVisibility(View.INVISIBLE);



        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, BT_REQUEST_ENABLE);

        btn_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(signInIntent);
            }
        });

        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(signInIntent);
            }
        });

        btn_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!scanning) {

                    scanning = true;
                    deviceScan();
                }
            }
        });




        intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

        registerReceiver(receiver, intentFilter);

        /*
        if (!scanning) {

            scanning = true;
            deviceScan();
        }

         */


    }



    private void deviceScan() {

        tv_log.append("\n deviceScan()");

        if (btn_restart.getVisibility() == View.VISIBLE) {
            btn_restart.setVisibility(View.INVISIBLE);
        }

        final Handler handler = new Handler();

        classicScanner = new BluetoothClassicScanner(SplashActivity.this);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (!findDevice) {
                    scanning = false;
                    classicScanner.stopScan();
                    btn_restart.setVisibility(View.VISIBLE);
                    tv_log.append("\n stopScan()");
                }

            }
        }, DELAY_TIME);

        classicScanner.startScan();



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == BT_REQUEST_ENABLE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "기기와 연동 중입니다", Toast.LENGTH_SHORT).show();

                tv_log.append("\n" + "request code : " + requestCode + "\n result code : " + resultCode);


                if (!scanning) {
                    scanning = true;
                    deviceScan();

                }

            } else {
                //동의하지 않았다면
                Toast.makeText(this, "블루투스가 연동되어야 앱 이용이 가능합니다", Toast.LENGTH_SHORT).show();
                finish();
            }

        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiver);
    }

    @Override
    public void onDeviceFind(BluetoothDevice device) {

        Toast.makeText(this, device.getName() + " 과 연결", Toast.LENGTH_SHORT).show();

        signInIntent = new Intent(SplashActivity.this, MainActivity.class);
        signInIntent.putExtra("DEVICE", device);

        btn_signIn.setVisibility(View.VISIBLE);
        btn_signUp.setVisibility(View.VISIBLE);

        classicScanner.stopScan();

    }

    @Override
    public void onDeviceFineFailed() {

        //Bonded device를 검색한 후에는 startDescovery() 를 통해 BroadcastReceiver 가 동작하므로,
        //해당 콜백은 이용되지 않음

    }
}