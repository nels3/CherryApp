package com.example.cherryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.Serializable;

public class DebbugingActivity extends AppCompatActivity {
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_TOAST = 5;
    public static final String TOAST = "toast";

    protected MyBluetoothService mService;
    protected boolean mBound = false;
    private STMBridge mSTMBridge;
    private boolean mAttached = false;
    private boolean mAnalog = false;
    private boolean mFetched = false;

    private ToggleButton tbSensor0, tbSensor1, tbSensor2, tbSensor3, tbSensor4, tbSensor5, tbSensorLeft, tbSensorRight;
    private TextView tvSensor0, tvSensor1, tvSensor2, tvSensor3, tvSensor4, tvSensor5, tvTSensor0, tvTSensor1, tvTSensor2, tvTSensor3, tvTSensor4, tvTSensor5;
    private TextView tvSensorRight, tvSensorLeft, tvTSensorRight, tvTSensorLeft;
    private LinearLayout llSISensors, llImu, llKTIR;

    public static final byte MESSAGE_TEST = 0;
    public static final byte MESSAGE_DEBUG_ANALOG = 1;
    public static final byte MESSAGE_DEBUG_DIGITAL = 2;
    public static final byte MESSAGE_TUNING_SENSORS_FETCH = 3;
    public static final byte MESSAGE_TUNING_SENSORS_SET = 4;
    public static final byte MESSAGE_TUNING_MOTORS_FETCH = 5;
    public static final byte MESSAGE_TUNINT_MOTORS_SET = 6;
    public static final byte MESSAGE_FIGHT_FETCH = 7;
    public static final byte MESSAGE_FIGHT_SET = 8;
    public static final byte MESSAGE_FETCH_THRESHOLD = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //startService(new Intent(this, MyBluetoothService.class));
        Intent intent = new Intent(this, MyBluetoothService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        mSTMBridge = new STMBridge();

        setContentView(R.layout.activity_sensor);
        setupBottomNavigationView();


        tbSensor0 = findViewById(R.id.tbSensor0);
        tbSensor1 = findViewById(R.id.tbSensor1);
        tbSensor2 = findViewById(R.id.tbSensor2);
        tbSensor3 = findViewById(R.id.tbSensor3);
        tbSensor4 = findViewById(R.id.tbSensor4);
        tbSensor5 = findViewById(R.id.tbSensor5);
        tbSensorLeft = findViewById(R.id.tbSensorLeft);
        tbSensorRight = findViewById(R.id.tbSensorRight);
        tvSensor0 = findViewById(R.id.tvSensor0);
        tvSensor1 = findViewById(R.id.tvSensor1);
        tvSensor2 = findViewById(R.id.tvSensor2);
        tvSensor3 = findViewById(R.id.tvSensor3);
        tvSensor4 = findViewById(R.id.tvSensor4);
        tvSensor5 = findViewById(R.id.tvSensor5);
        tvTSensor0 = findViewById(R.id.tvTSensor0);
        tvTSensor1 = findViewById(R.id.tvTSensor1);
        tvTSensor2 = findViewById(R.id.tvTSensor2);
        tvTSensor3 = findViewById(R.id.tvTSensor3);
        tvTSensor4 = findViewById(R.id.tvTSensor4);
        tvTSensor5 = findViewById(R.id.tvTSensor5);
        //tvRight = findViewById(R.id.tvRight);
        tvSensorRight = findViewById(R.id.tvShowRight);
        tvTSensorRight = findViewById(R.id.tvTShowRight);
        //tvLeft = findViewById(R.id.tvLeft);
        tvSensorLeft = findViewById(R.id.tvShowLeft);
        tvTSensorLeft = findViewById(R.id.tvTShowLeft);
        llImu = findViewById(R.id.llImu);
        llSISensors = findViewById(R.id.llSensors);
        llKTIR = findViewById(R.id.llKtir);
        llImu.setVisibility(View.INVISIBLE);
        llSISensors.setVisibility(View.INVISIBLE);
        llKTIR.setVisibility(View.INVISIBLE);

        /*tbSensor0.setEnabled(false);
        tbSensor1.setEnabled(false);
        tbSensor2.setEnabled(false);
        tbSensor3.setEnabled(false);
        tbSensor4.setEnabled(false);
        tbSensor5.setEnabled(false);
        tbSensorLeft.setEnabled(false);
        tbSensorRight.setEnabled(false);*/
        /*tvSensor0.setText("0");
        tvSensor1.setText("0");
        tvSensor2.setText("0");
        tvSensor3.setText("0");
        tvSensor4.setText("0");
        tvSensor5.setText("0");
        tvSensorLeft.setText("0");
        tvSensorRight.setText("0");*/

        final ToggleButton tbStart = findViewById(R.id.tbStart);
        final ToggleButton tbDisplay = findViewById(R.id.tbTSend);
        final Button bTuning = findViewById(R.id.tbTDebbuging);

        tbStart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!mAttached){
                    mService.attachHandler(mService.DEBUGGING_ACTIVITY_ID, mHandler);
                    mAttached = true;
                }

                if (!mFetched){
                    fetchThreshold();
                    mFetched = true;
                }

                if (isChecked){
                    tbDisplay.setEnabled(false);
                    bTuning.setEnabled(false);
                }else{
                    tbDisplay.setEnabled(true);
                    bTuning.setEnabled(true);
                }
                fetchData();

            }
        });


        tbDisplay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    tbSensor1.setChecked(true);
                    mAnalog = true;
                    tbSensor0.setVisibility(View.INVISIBLE);
                    tbSensor1.setVisibility(View.INVISIBLE);
                    tbSensor2.setVisibility(View.INVISIBLE);
                    tbSensor3.setVisibility(View.INVISIBLE);
                    tbSensor4.setVisibility(View.INVISIBLE);
                    tbSensor5.setVisibility(View.INVISIBLE);
                    tbSensorLeft.setVisibility(View.INVISIBLE);
                    tbSensorRight.setVisibility(View.INVISIBLE);
                    llSISensors.setVisibility(View.VISIBLE);
                    llKTIR.setVisibility(View.VISIBLE);
                    llImu.setVisibility(View.VISIBLE);

                }else{
                    tbSensor1.setChecked(false);
                    mAnalog = false;
                    tbSensor0.setVisibility(View.VISIBLE);
                    tbSensor1.setVisibility(View.VISIBLE);
                    tbSensor2.setVisibility(View.VISIBLE);
                    tbSensor3.setVisibility(View.VISIBLE);
                    tbSensor4.setVisibility(View.VISIBLE);
                    tbSensor5.setVisibility(View.VISIBLE);
                    tbSensorLeft.setVisibility(View.VISIBLE);
                    tbSensorRight.setVisibility(View.VISIBLE);
                    llSISensors.setVisibility(View.INVISIBLE);
                    llKTIR.setVisibility(View.INVISIBLE);
                    llImu.setVisibility(View.INVISIBLE);
                }
            }
        });

        bTuning.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openTuningActivity();
            }
        });

    }

    private void fetchThreshold(){
        mSTMBridge.pack_message_fetch_threshold();
        byte[] send = mSTMBridge.writeSTMBuf;
        mService.write(mService.DEBUGGING_ACTIVITY_ID, send);
    }

    private void fetchData(){
        mSTMBridge.pack_message_sensors_fetch(mAnalog);
        byte[] send = mSTMBridge.writeSTMBuf;
        mService.write(mService.DEBUGGING_ACTIVITY_ID, send);
    }

    private void showDataSensorsAnalog(){
        tvSensor0.setText("1234");
        tvSensor0.setText(mSTMBridge.getSensorValue(0));
        tvSensor1.setText(mSTMBridge.getSensorValue(1));
        tvSensor2.setText(mSTMBridge.getSensorValue(2));
        tvSensor3.setText(mSTMBridge.getSensorValue(3));
        tvSensor4.setText(mSTMBridge.getSensorValue(4));
        tvSensor5.setText(mSTMBridge.getSensorValue(5));
        tvSensorLeft.setText(mSTMBridge.getSensorValue(6));
        tvSensorRight.setText(mSTMBridge.getSensorValue(7));
    }

    private void showDataSensorsDigital(){
        tbSensor0.setChecked(true);

        tbSensor0.setChecked(mSTMBridge.getSensorValueBool(0));
        tbSensor1.setChecked(mSTMBridge.getSensorValueBool(1));
        tbSensor2.setChecked(mSTMBridge.getSensorValueBool(2));
        tbSensor3.setChecked(mSTMBridge.getSensorValueBool(3));
        tbSensor4.setChecked(mSTMBridge.getSensorValueBool(4));
        tbSensor5.setChecked(mSTMBridge.getSensorValueBool(5));
        tbSensorLeft.setChecked(mSTMBridge.getSensorValueBool(6));
        tbSensorRight.setChecked(mSTMBridge.getSensorValueBool(7));
    }

    private void showFetchData(){
        tvTSensor0.setText("1234");
        tvTSensor0.setText(mSTMBridge.getSensorValue(0));
        tvTSensor1.setText(mSTMBridge.getSensorValue(1));
        tvTSensor2.setText(mSTMBridge.getSensorValue(2));
        tvTSensor3.setText(mSTMBridge.getSensorValue(3));
        tvTSensor4.setText(mSTMBridge.getSensorValue(4));
        tvTSensor5.setText(mSTMBridge.getSensorValue(5));
        tvTSensorLeft.setText(mSTMBridge.getSensorValue(6));
        tvTSensorRight.setText(mSTMBridge.getSensorValue(7));
    }


    private void unpack_message(){
        switch (mSTMBridge.mRecCode){
            case MESSAGE_DEBUG_ANALOG:
                showDataSensorsAnalog();
                break;
            case MESSAGE_DEBUG_DIGITAL:
                showDataSensorsDigital();
                break;
            case MESSAGE_FETCH_THRESHOLD:
                showFetchData();
                break;

        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {

            MyBluetoothService.LocalBinder binder = (MyBluetoothService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    public void openTuningActivity() {
        Intent intent = new Intent(this, TuningActivity.class);

        startActivity(intent);
    }

    public void openFightActivity() {
        Intent intent = new Intent(this, FightActivity.class);
        startActivity(intent);
    }

    public void openSensorActivity() {
        Intent intent = new Intent(this, DebbugingActivity.class);

        startActivity(intent);
    }

    public void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_sensors);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        openMainActivity();
                        break;
                    case R.id.navigation_sensors:
                        //openSensorActivity();
                        break;
                    case R.id.navigation_fight:
                        openFightActivity();
                        break;
                }
                return true;
            }
        });
    }

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    String writeMessage = new String(writeBuf);
                    //Toast.makeText(getApplicationContext(), "Sending "+ writeMessage, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    mSTMBridge.unpack_message(readBuf, (int) msg.arg1);
                    unpack_message();
                    Toast.makeText(getApplicationContext(), "Got code: "+ mSTMBridge.mRecCode, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

}
