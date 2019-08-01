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

    private int mRequest = 0;
    public static final int MSG_DIGITAL = 1;
    public static final int MSG_ANALOG = 2;
    public static final int MSG_THRESHOLD = 3;

    private ToggleButton tbSensor[] = new ToggleButton[8];
    private TextView tvSensor[] = new TextView[8];
    private TextView tvThresSensor[] = new TextView[8];
    private TextView tvImu[] = new TextView[4];
    private LinearLayout llSISensors, llImu, llKTIR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, MyBluetoothService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        mSTMBridge = new STMBridge();

        setContentView(R.layout.activity_sensor);
        setupBottomNavigationView();


        tbSensor[0] = findViewById(R.id.tbSensor0);
        tbSensor[1] = findViewById(R.id.tbSensor1);
        tbSensor[2] = findViewById(R.id.tbSensor2);
        tbSensor[3] = findViewById(R.id.tbSensor3);
        tbSensor[4] = findViewById(R.id.tbSensor4);
        tbSensor[5] = findViewById(R.id.tbSensor5);
        tbSensor[6] = findViewById(R.id.tbSensorRight);
        tbSensor[7] = findViewById(R.id.tbSensorLeft);
        tvSensor[0] = findViewById(R.id.tvSensor0);
        tvSensor[1] = findViewById(R.id.tvSensor1);
        tvSensor[2] = findViewById(R.id.tvSensor2);
        tvSensor[3] = findViewById(R.id.tvSensor3);
        tvSensor[4] = findViewById(R.id.tvSensor4);
        tvSensor[5] = findViewById(R.id.tvSensor5);
        tvThresSensor[0] = findViewById(R.id.tvTSensor0);
        tvThresSensor[1] = findViewById(R.id.tvTSensor1);
        tvThresSensor[2] = findViewById(R.id.tvTSensor2);
        tvThresSensor[3] = findViewById(R.id.tvTSensor3);
        tvThresSensor[4] = findViewById(R.id.tvTSensor4);
        tvThresSensor[5] = findViewById(R.id.tvTSensor5);
        tvImu[0] = findViewById(R.id.tvImu10);
        tvImu[1] = findViewById(R.id.tvImu20);
        tvImu[2] = findViewById(R.id.tvImu30);
        tvImu[3] = findViewById(R.id.tvImu40);

        tvSensor[6] = findViewById(R.id.tvShowRight);
        tvThresSensor[6] = findViewById(R.id.tvTShowRight);

        tvSensor[7] = findViewById(R.id.tvShowLeft);
        tvThresSensor[7] = findViewById(R.id.tvTShowLeft);

        llImu = findViewById(R.id.llImu);
        llSISensors = findViewById(R.id.llSensors);
        llKTIR = findViewById(R.id.llKtir);
        llImu.setVisibility(View.INVISIBLE);
        llSISensors.setVisibility(View.INVISIBLE);
        llKTIR.setVisibility(View.INVISIBLE);

        final ToggleButton tbStart = findViewById(R.id.tbStart);
        final ToggleButton tbDisplay = findViewById(R.id.tbTSend);
        final Button bTuning = findViewById(R.id.tbTDebbuging);

        tbStart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                attachService();

                if (isChecked) {
                    tbDisplay.setEnabled(false);
                    bTuning.setEnabled(false);
                    if (mAnalog){
                        mRequest = MSG_ANALOG;
                        fetchData(mSTMBridge.MSG_ANALOG);
                    }
                    else{
                        mRequest = MSG_DIGITAL;
                        fetchData(mSTMBridge.MSG_DIGITAL);
                    }
                }else{
                    tbDisplay.setEnabled(true);
                    bTuning.setEnabled(true);
                    mRequest = MSG_THRESHOLD;
                    fetchData(mSTMBridge.MSG_THRESHOLD);
                }


            }
        });


        tbDisplay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                attachService();
                if (isChecked){
                    mAnalog = true;
                    for (int i=0; i<8; ++i)
                        tbSensor[i].setVisibility(View.INVISIBLE);

                    llSISensors.setVisibility(View.VISIBLE);
                    llKTIR.setVisibility(View.VISIBLE);
                    llImu.setVisibility(View.VISIBLE);

                    mRequest = MSG_THRESHOLD;
                    fetchData(mSTMBridge.MSG_THRESHOLD);

                }else{
                    mAnalog = false;
                    for (int i=0; i<8; ++i)
                        tbSensor[i].setVisibility(View.VISIBLE);

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
        setAsNotFetched();
    }

    private void fetchData(byte msg){
        mSTMBridge.pack_message_sensors_fetch(msg);
        byte[] send = mSTMBridge.writeSTMBuf;
        mService.write(mService.DEBUGGING_ACTIVITY_ID, send);
    }

    private void showDataSensorsAnalog(){
        for (int i=0; i<8; ++i){
            tvSensor[i].setText(Integer.toString(mSTMBridge.getBridgeValue(i)));
        }
        for (int i=0; i<4; ++i){
            tvImu[i].setText(Integer.toString(mSTMBridge.getBridgeValue(8+i)));
        }

    }

    private void showDataSensorsDigital(){
        for (int i=0; i<8; ++i){
            tbSensor[i].setChecked(mSTMBridge.getSensorValueBool(i));
        }
    }

    private void showFetchData(){
        for (int i=0; i<8; ++i){
            tvThresSensor[i].setText("T: "+Integer.toString(mSTMBridge.getBridgeValue(i)));
        }
    }


    private void unpack_message(){
        switch (mRequest){
            case MSG_ANALOG:
                showDataSensorsAnalog();
                break;
            case MSG_DIGITAL:
                showDataSensorsDigital();
                break;
            case MSG_THRESHOLD:
                showFetchData();
                break;

        }
    }

    private void attachService(){
        if (!mAttached){
            mService.attachHandler(mService.DEBUGGING_ACTIVITY_ID, mHandler);
            mAttached = true;
        }
    }

    private void setAsNotFetched(){
        for (int i =0 ;i<8; i++){
            tvSensor[i].setText(Integer.toString(-1));
        }
        for (int i=0; i<4; i++){
            tvImu[i].setText(Integer.toString(-1));
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
        Intent intent = new Intent(this, TacticActivity.class);
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
                        openFightActivity();
                        break;
                    case R.id.navigation_sensors:
                        //openSensorActivity();
                        break;
                    case R.id.navigation_fight:
                        //openFightActivity();
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
                    //String readMessage = new String(readBuf, 0, msg.arg1);
                    mSTMBridge.receive_bytes(readBuf, msg.arg1);

                    if (mSTMBridge.msg_received) {
                        boolean success = mSTMBridge.unpack_message_sensors_fetch();

                        if (success) {
                            Toast.makeText(getApplicationContext(), "Success. Got code: " + mSTMBridge.mRecCode, Toast.LENGTH_SHORT).show();
                            unpack_message();
                        } else {
                            Toast.makeText(getApplicationContext(), "Not succees / len: " + msg.arg1 + " . Got code: " + mSTMBridge.mRecCode, Toast.LENGTH_SHORT).show();
                        }

                    }
                   // else{
                   //     Toast.makeText(getApplicationContext(), "len: " + msg.arg1 + " . Not succees", Toast.LENGTH_SHORT).show();

                    //}
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

}
