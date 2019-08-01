package com.example.cherryapp;

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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class TuningActivity extends AppCompatActivity {
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

    public static final int MSG_THRESHOLD = 3;
    public static final int MSG_ANALOGY = 4;
    public static final int MSG_THRESHOLD_SEND = 1;

    private TextView tvSensor[] = new TextView[8];
    private TextView tvThreshold[] = new TextView[8];
    private EditText tvUserThreshold[] = new EditText[8];
    private byte tvUserThresholdInput[] = new byte[8];


    private LinearLayout llSensors, llMotors;
    private ToggleButton bStart;
    private Button bFetch, bSend, bSensors, bMotors, bDebbuging;

    //private final Handler mHandler = null;

    public TuningActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, MyBluetoothService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        mSTMBridge = new STMBridge();

        setContentView(R.layout.activity_tuning);
        setupBottomNavigationView();
        findObjectsOnView();
    }

    private void attachService(){
        if (!mAttached){
            mService.attachHandler(mService.TUNING_ACTIVITY_ID, mHandler);
            mAttached = true;
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


    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    String writeMessage = new String(writeBuf);
                    Toast.makeText(getApplicationContext(), "Sending "+ writeMessage, Toast.LENGTH_SHORT).show();
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
                    else{
                         Toast.makeText(getApplicationContext(), "len: " + msg.arg1 + " . Not succees", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void findObjectsOnView(){
        tvSensor[0] = findViewById(R.id.tvSi02);
        tvSensor[1]  = findViewById(R.id.tvSi12);
        tvSensor[2]  = findViewById(R.id.tvSi22);
        tvSensor[3]  = findViewById(R.id.tvSi32);
        tvSensor[4]  = findViewById(R.id.tvSi42);
        tvSensor[5]  = findViewById(R.id.tvSi52);
        tvSensor[6]  = findViewById(R.id.tvKTR2);
        tvSensor[7]  = findViewById(R.id.tvKTL2);

        for (int i=0; i<8; i++)
            tvSensor[i].setVisibility(View.INVISIBLE);

        tvThreshold[0] = findViewById(R.id.tvSi03);
        tvThreshold[1] = findViewById(R.id.tvSi13);
        tvThreshold[2] = findViewById(R.id.tvSi23);
        tvThreshold[3] = findViewById(R.id.tvSi33);
        tvThreshold[4] = findViewById(R.id.tvSi43);
        tvThreshold[5] = findViewById(R.id.tvSi53);
        tvThreshold[6] = findViewById(R.id.tvKTR3);
        tvThreshold[7] = findViewById(R.id.tvKTL3);

        for (int i=0; i<8; i++)
            tvThreshold[i].setVisibility(View.INVISIBLE);

        tvUserThreshold[0] = findViewById(R.id.tvSi04);
        tvUserThreshold[1] = findViewById(R.id.tvSi14);
        tvUserThreshold[2] = findViewById(R.id.tvSi24);
        tvUserThreshold[3] = findViewById(R.id.tvSi34);
        tvUserThreshold[4] = findViewById(R.id.tvSi44);
        tvUserThreshold[5] = findViewById(R.id.tvSi54);
        tvUserThreshold[6] = findViewById(R.id.tvKTR4);
        tvUserThreshold[7] = findViewById(R.id.tvKTL4);

        for (int i=0; i<8; i++)
            tvUserThresholdInput[i] = 0;

        bFetch = findViewById(R.id.tbFetch);
        bSend = findViewById(R.id.tbTSend);
        bSensors = findViewById(R.id.tbTSensors);
        bMotors = findViewById(R.id.tbTMotors);
        bDebbuging = findViewById(R.id.tbTDebbuging);
        bStart = findViewById(R.id.tbTStart);

        llSensors = findViewById(R.id.llSensors);
        llMotors = findViewById(R.id.llMotors);
        llSensors.setVisibility(View.INVISIBLE);
        llMotors.setVisibility(View.INVISIBLE);

        llSensors.setVisibility(View.VISIBLE);
        llMotors.setVisibility(View.INVISIBLE);
        bSensors.setEnabled(false);
        bMotors.setEnabled(true);
        bStart.setEnabled(true);

        bSensors.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                attachService();
                llSensors.setVisibility(View.VISIBLE);
                llMotors.setVisibility(View.INVISIBLE);
                bSensors.setEnabled(false);
                bMotors.setEnabled(true);
                bStart.setEnabled(true);
            }
        });

        bMotors.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                attachService();
                llSensors.setVisibility(View.INVISIBLE);
                llMotors.setVisibility(View.VISIBLE);
                bMotors.setEnabled(false);
                bSensors.setEnabled(true);
                bStart.setEnabled(true);
            }
        });

        bDebbuging.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openSensorActivity();
            }
        });

        bStart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                attachService();
                if (isChecked) {
                    bFetch.setEnabled(false);
                    bSend.setEnabled(false);

                    for (int i = 0; i < 8; i++){
                        tvSensor[i].setVisibility(View.VISIBLE);
                        tvUserThreshold[i].setEnabled(false);
                    }

                    bDebbuging.setEnabled(false);
                    bSensors.setVisibility(View.INVISIBLE);
                    bMotors.setVisibility(View.INVISIBLE);

                    mRequest = MSG_ANALOGY;
                    fetchData(mSTMBridge.MSG_ANALOGY);

                }else{
                    bFetch.setEnabled(true);
                    bSend.setEnabled(true);
                    for (int i = 0; i < 8; i++){
                        tvUserThreshold[i].setEnabled(true);
                    }
                    bDebbuging.setEnabled(true);
                    bSensors.setVisibility(View.VISIBLE);
                    bMotors.setVisibility(View.VISIBLE);
                }
            }
        });

        bFetch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                for (int i=0; i<8; i++)
                    tvThreshold[i].setVisibility(View.VISIBLE);
                attachService();
                mRequest = MSG_THRESHOLD;
                fetchData(mSTMBridge.MSG_THRESHOLD);

            }
        });

        bSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                attachService();
                for (int i=0; i<8; ++i) {
                    String value = tvUserThreshold[i].getText().toString();
                    tvUserThresholdInput[i] = (byte) Integer.parseInt(value);
                }
                mSTMBridge.pack_message_threshold(tvUserThresholdInput);
                byte[] send = mSTMBridge.writeSTMBuf;
                mRequest = MSG_THRESHOLD;
                mService.write(mService.TUNING_ACTIVITY_ID, send);
            }
        });

    }

    private void fetchData(byte msg){
        mSTMBridge.pack_message_sensors_fetch(msg);
        byte[] send = mSTMBridge.writeSTMBuf;
        mService.write(mService.TUNING_ACTIVITY_ID, send);
    }

    private void showDataSensors(){
        for (int i=0; i<8; ++i){
            tvSensor[i].setText(Integer.toString(mSTMBridge.getBridgeValue(i)));
        }
    }

    private void showFetchThreshold(){
        for (int i=0; i<8; ++i){
            tvThreshold[i].setText("T: "+Integer.toString(mSTMBridge.getBridgeValue(i)));
        }
    }

    private void unpack_message(){
        switch (mRequest){
            case MSG_ANALOGY:
                showDataSensors();
                break;
            case MSG_THRESHOLD:
                showFetchThreshold();
                break;

        }
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
                        openSensorActivity();
                        break;
                    case R.id.navigation_fight:
                        //openFightActivity();
                        break;
                }
                return true;
            }
        });
    }

}
