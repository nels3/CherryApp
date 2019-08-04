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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FightActivity extends AppCompatActivity {
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_TOAST = 5;
    public static final String TOAST = "toast";

    protected MyBluetoothService mService;
    protected boolean mBound = false;
    private boolean mAttached = false;

    private STMBridge mSTMBridge;

    private byte mDataRequest = 0;
    public static final byte MSG_FETCH = 1;
    public static final byte MSG_SEND = 2;

    private byte mFightBytes[] = new byte[3];
    private TextView tvOut[] = new TextView[12];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, MyBluetoothService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        mSTMBridge = new STMBridge();

        setContentView(R.layout.activity_fight);
        setupBottomNavigationView();
        findObjectsByID();

        final CheckBox cbDelay = findViewById(R.id.cbDelay);
        final CheckBox cbTranslation = findViewById(R.id.cbTranslation);
        final CheckBox cbLeds = findViewById(R.id.cbLeds);
        final Button bSendData = findViewById(R.id.bSendData);

        ToggleButton bStart = (ToggleButton) findViewById(R.id.bStart);
        bStart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                attachService();
                if (isChecked) {
                    startRobot();
                    bSendData.setEnabled(false);
                    cbDelay.setEnabled(false);
                    cbLeds.setEnabled(false);
                    cbTranslation.setEnabled(false);

                    mDataRequest = MSG_FETCH;
                    mSTMBridge.pack_message_fight_fetch();
                    byte[] send = mSTMBridge.writeSTMBuf;
                    mService.write(mService.FIGHT_ACTIVITY_ID, send);

                } else{
                    stopRobot();
                }
            }
        });


        bSendData.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                attachService();
                if (cbDelay.isSelected())
                    mFightBytes[0] = 1;
                else
                    mFightBytes[0] = 0;
                if (cbTranslation.isSelected())
                    mFightBytes[1] = 1;
                else
                    mFightBytes[1] = 0;
                if (cbLeds.isSelected())
                    mFightBytes[2] = 1;
                else
                    mFightBytes[2] = 0;

                mDataRequest = MSG_SEND;
                mSTMBridge.pack_message_fight_send(mFightBytes);
                byte[] send = mSTMBridge.writeSTMBuf;
                mService.write(mService.FIGHT_ACTIVITY_ID, send);

            }
        });
    }

        public void startRobot(){

        }

        public void stopRobot(){

        }


    private void attachService(){
        if (!mAttached){
            mService.attachHandler(mService.FIGHT_ACTIVITY_ID, mHandler);
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
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    mSTMBridge.receive_bytes(readBuf, msg.arg1);

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

    public void openFightActivity() {
        Intent intent = new Intent(this, TacticActivity.class);
        startActivity(intent);
    }

    public void openSensorActivity() {
        Intent intent = new Intent(this, DebbugingActivity.class);
        startActivity(intent);
    }

    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_fight);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        openFightActivity();
                        //openMainActivity();
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

    private void showDataFight(){
        for (int i=0; i<12; ++i){
            tvOut[i].setText(Integer.toString(mSTMBridge.getBridgeValue(i)));
        }
    }

    private void unpack_app_bridge_message(){
        switch (mDataRequest){
            case MSG_FETCH:
                showDataFight();
                break;
        }
    }

    private void findObjectsByID() {
        tvOut[0] = findViewById(R.id.tvOutVar1);
        tvOut[1] = findViewById(R.id.tvOutVar2);
        tvOut[2] = findViewById(R.id.tvOutVar3);
        tvOut[3] = findViewById(R.id.tvOutVar4);
        tvOut[4] = findViewById(R.id.tvOutVar5);
        tvOut[5] = findViewById(R.id.tvOutVar6);
        tvOut[6] = findViewById(R.id.tvOutVar7);
        tvOut[7] = findViewById(R.id.tvOutVar8);
        tvOut[8] = findViewById(R.id.tvOutVar9);
        tvOut[9] = findViewById(R.id.tvOutVar10);
        tvOut[10] = findViewById(R.id.tvOutVar11);
        tvOut[11] = findViewById(R.id.tvOutVar12);
    }
}
