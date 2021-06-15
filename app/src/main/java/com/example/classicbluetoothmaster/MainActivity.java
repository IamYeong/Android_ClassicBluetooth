package com.example.classicbluetoothmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FrameLayout frame_lung, frame_respiration, frame_data;
    private BluetoothDevice device;
    private Intent intent;

    private String deviceName;
    private String deviceAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intent = getIntent();
        device = intent.getParcelableExtra("DEVICE");

        //deviceName = intent.getStringExtra("NAME");
        //deviceAddress = intent.getStringExtra("ADDRESS");

        frame_lung = findViewById(R.id.frame_main_lungs);
        frame_respiration = findViewById(R.id.frame_main_respiration);
        frame_data = findViewById(R.id.frame_main_data);

        frame_lung.setOnClickListener(MainActivity.this);
        frame_respiration.setOnClickListener(MainActivity.this);
        frame_data.setOnClickListener(MainActivity.this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.frame_main_lungs :

                Intent classicIntent = new Intent(MainActivity.this, ConnectionActivity.class);
                classicIntent.putExtra("DEVICE", device);
                startActivity(classicIntent);

                /*
                Intent intent1 = new Intent(MainActivity.this, FVCBLEActivity.class);
                intent1.putExtra("NAME", deviceName);
                intent1.putExtra("ADDRESS", deviceAddress);
                startActivity(intent1);


                 */
                break;



        }

    }
}