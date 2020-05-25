package com.example.cherryapp;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;

public class TuningActivity extends BasicActivity{

    private boolean service_attached = false;

    private byte mDataRequest = 0;
    public final byte MSG_THRESHOLD = 3;
    public final byte MSG_ANALOGY = 4;
    public final byte MSG_SAVE = 5;

    private TextView tvSensor[] = new TextView[8];
    private TextView tvThreshold[] = new TextView[8];
    private EditText tvUserThreshold[] = new EditText[8];
    private int tvUserThresholdInput[] = new int[8];

    private LinearLayout llSensors, llMotors;
    private ToggleButton bStart;
    private Button bFetch, bSend, bSensors, bMotors, bDebbuging, bSave;

    TimerTask mTimerTask;
    final Handler handler = new Handler();
    Timer t = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tuning);
        setupBottomNavigationView();
        findObjectsByID();
    }

    private void attachService(){
        if (!service_attached){
            mService.attachHandler(mService.mChatService.TUNING_ACTIVITY_ID, mHandler);
            service_attached = true;
        }
    }

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    //String readMessage = new String(readBuf, 0, msg.arg1);
                    mSTMBridge.receive_bytes(readBuf, msg.arg1);

                    if (mSTMBridge.msg_received) {
                        mSTMBridge.message_processed();
                        Toast.makeText(getApplicationContext(), "Success. Got code: " + mSTMBridge.mRecCode, Toast.LENGTH_SHORT).show();
                        unpack_app_bridge_message();
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

    private void findObjectsByID(){
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
        bSave = findViewById(R.id.tbTSave);

        llSensors = findViewById(R.id.llSensors);
        llMotors = findViewById(R.id.llMotors);
        llSensors.setVisibility(View.INVISIBLE);
        llMotors.setVisibility(View.INVISIBLE);

        llSensors.setVisibility(View.VISIBLE);
        llMotors.setVisibility(View.INVISIBLE);
        bSensors.setEnabled(false);
        bMotors.setEnabled(true);

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

                    fetchData(mSTMBridge.MSG_ANALOGY);
                    doTimerSendingRequest();
                } else{
                    stopTimerSendingRequest();
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
                bSend.setEnabled(true);
                fetchData(mSTMBridge.MSG_THRESHOLD);

            }
        });

        bSend.setEnabled(false);
        bSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                attachService();
                for (int i=0; i<8; ++i) {
                    String value = tvUserThreshold[i].getText().toString();
                    tvUserThresholdInput[i] = (int) Integer.parseInt(value);
                }
                mSTMBridge.pack_message_threshold(tvUserThresholdInput);
                byte[] send = mSTMBridge.writeSTMBuf;
                mDataRequest = mSTMBridge.MSG_THRESHOLD;
                mService.write(mService.mChatService.TUNING_ACTIVITY_ID, send);
                bSave.setEnabled(true);
            }
        });

        bSave.setEnabled(false);
        bSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                attachService();

                mSTMBridge.pack_message_command_robot(mSTMBridge.MSG_SAVE_CONFIG);
                mDataRequest = mSTMBridge.MSG_SAVE;
                mService.write(mService.mChatService.TUNING_ACTIVITY_ID, mSTMBridge.writeSTMBuf);
            }
        });

    }

    public void doTimerSendingRequest(){
        mTimerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        mService.write(mService.mChatService.TUNING_ACTIVITY_ID,  mSTMBridge.writeSTMBuf);
                    }
                });
            }};
        t.schedule(mTimerTask, 10, TIMER_PERIOD);  //
    }

    public void stopTimerSendingRequest(){
        if(mTimerTask!=null){
            mTimerTask.cancel();
        }
    }

    private void fetchData(byte msg){
        mDataRequest = msg;
        mSTMBridge.pack_message_sensors_fetch(msg);
        mService.write(mService.mChatService.TUNING_ACTIVITY_ID,  mSTMBridge.writeSTMBuf);
    }

    private void showDataSensors(){
        for (int i=0; i<8; ++i)
            tvSensor[i].setText(Integer.toString(mSTMBridge.getBridgeInt16Value(i)));
    }

    private void showFetchThreshold(){
        for (int i=0; i<8; ++i){
            tvThreshold[i].setText("T: "+Integer.toString(mSTMBridge.getBridgeInt16Value(i)));
            tvUserThreshold[i].setText(Integer.toString(mSTMBridge.getBridgeInt16Value(i)));
        }
    }

    private void unpack_app_bridge_message(){
        switch (mDataRequest){
            case MSG_ANALOGY:
                showDataSensors();
                break;
            case MSG_THRESHOLD:
                showFetchThreshold();
                break;
            case MSG_SAVE:
                int status = mSTMBridge.getBridgeValue(0);
                if (status == 0){
                    Toast.makeText(getApplicationContext(), "Config saved successfully!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Error when saving config!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
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
