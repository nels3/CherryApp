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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class DebbugingActivity extends AppCompatActivity {
    public static final int MESSAGE_READ = 1;
    public static final int MESSAGE_TOAST = 2;
    public static final String TOAST = "toast";

    private STMBridge mSTMBridge;
    protected MyBluetoothService mService;
    protected boolean mBound = false;
    private boolean mAttached = false;
    private boolean mAnalog = false;

    private int mDataRequest = 0;
    public static final byte MSG_DIGITAL = 1;
    public static final byte MSG_ANALOG = 2;
    public static final byte MSG_THRESHOLD = 3;

    private ToggleButton tbSensor[] = new ToggleButton[8];
    private TextView tvSensor[] = new TextView[8];
    private TextView tvThresholdSensor[] = new TextView[8];
    private TextView tvImu[] = new TextView[4];
    private LinearLayout llSISensors, llImu, llKTIR;
    private ProgressBar pbSensor[] = new ProgressBar[8];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, MyBluetoothService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        mSTMBridge = new STMBridge();

        setContentView(R.layout.activity_sensor);
        setupBottomNavigationView();
        findObjectsByID();

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
                    if (mAnalog)
                        fetchData(MSG_ANALOG);
                    else
                        fetchData(MSG_DIGITAL);
                }else{
                    tbDisplay.setEnabled(true);
                    bTuning.setEnabled(true);
                    fetchData(MSG_THRESHOLD);
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
        handleProgressBars();
    }

    private void handleProgressBars(){
        pbSensor[0] = findViewById(R.id.pbSensor0);
        pbSensor[1] = findViewById(R.id.pbSensor1);
        pbSensor[2] = findViewById(R.id.pbSensor2);
        pbSensor[3] = findViewById(R.id.pbSensor3);
        pbSensor[4] = findViewById(R.id.pbSensor4);
        pbSensor[5] = findViewById(R.id.pbSensor5);
        pbSensor[6] = findViewById(R.id.pbRight);
        pbSensor[7] = findViewById(R.id.pbLeft);

        for (int i=0; i<6; ++i) {
            pbSensor[i].setMax(1000);
        }
        for (int i=6; i<8; ++i) {
            pbSensor[i].setMax(4000);
        }
    }

    private void fetchData(byte msg_id){
        mDataRequest = msg_id;
        mSTMBridge.pack_message_sensors_fetch(msg_id);
        byte[] send = mSTMBridge.writeSTMBuf;
        mService.write(mService.DEBUGGING_ACTIVITY_ID, send);
    }

    private void showDataSensorsAnalog(){
        for (int i=0; i<8; ++i)
            tvSensor[i].setText(Integer.toString(mSTMBridge.getBridgeInt16Value(i)));
        for (int i=0; i<4; ++i)
            tvImu[i].setText(Integer.toString(mSTMBridge.getBridgeInt16Value(8+i)));
        for (int i=0; i<8; ++i)
            pbSensor[i].setProgress(mSTMBridge.getBridgeInt16Value(i));
    }

    private void showDataSensorsDigital(){
        for (int i=0; i<8; ++i)
            tbSensor[i].setChecked(mSTMBridge.getSensorValueBool(i));
    }

    private void showFetchData(){
        for (int i=0; i<8; ++i)
            tvThresholdSensor[i].setText("T: "+Integer.toString(mSTMBridge.getBridgeInt16Value(i)));
    }

    private void unpack_app_bridge_message(){
        switch (mDataRequest){
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
            tvSensor[i].setText("-1");
        }
        for (int i=0; i<4; i++){
            tvImu[i].setText("-1");
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
                        break;
                    case R.id.navigation_fight:
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
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    mSTMBridge.receive_bytes(readBuf, msg.arg1);
                    //wait till whole message received
                    if (mSTMBridge.msg_received) {
                        mSTMBridge.message_processed();
                        Toast.makeText(getApplicationContext(), "Success. Got code: " + mSTMBridge.mRecCode, Toast.LENGTH_SHORT).show();
                        unpack_app_bridge_message();
                    }
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void findObjectsByID(){
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
        tvThresholdSensor[0] = findViewById(R.id.tvTSensor0);
        tvThresholdSensor[1] = findViewById(R.id.tvTSensor1);
        tvThresholdSensor[2] = findViewById(R.id.tvTSensor2);
        tvThresholdSensor[3] = findViewById(R.id.tvTSensor3);
        tvThresholdSensor[4] = findViewById(R.id.tvTSensor4);
        tvThresholdSensor[5] = findViewById(R.id.tvTSensor5);
        tvImu[0] = findViewById(R.id.tvImu10);
        tvImu[1] = findViewById(R.id.tvImu20);
        tvImu[2] = findViewById(R.id.tvImu30);
        tvImu[3] = findViewById(R.id.tvImu40);

        tvSensor[6] = findViewById(R.id.tvShowRight);
        tvThresholdSensor[6] = findViewById(R.id.tvTShowRight);

        tvSensor[7] = findViewById(R.id.tvShowLeft);
        tvThresholdSensor[7] = findViewById(R.id.tvTShowLeft);

        llImu = findViewById(R.id.llImu);
        llSISensors = findViewById(R.id.llSensors);
        llKTIR = findViewById(R.id.llKtir);
        llImu.setVisibility(View.INVISIBLE);
        llSISensors.setVisibility(View.INVISIBLE);
        llKTIR.setVisibility(View.INVISIBLE);
    }
}
