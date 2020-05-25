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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Activity that handles communication with Wisienka device during fight
 */
public class FightActivity extends BasicActivity {

    protected boolean mBound = false;
    private boolean mAttached = false;

    TimerTask mTimerTask;
    final Handler handler = new Handler();
    Timer t = new Timer();

    // stores what we have requested from bluetooth service
    private byte mDataRequest = 0;
    public static final byte MSG_FETCH = 1;
    public static final byte MSG_SEND = 2;
    public static final byte MSG_STOP = 3;
    public static final byte MSG_START = 4;
    public static final byte MSG_RESTART = 5;

    // objects from activity
    private byte mFightBytes[] = new byte[3];
    private TextView tvOut[] = new TextView[12];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fight);
        setupBottomNavigationView();
        findObjectsByID();

        final CheckBox cbDelay = findViewById(R.id.cbDelay);
        final CheckBox cbTranslation = findViewById(R.id.cbTranslation);
        final CheckBox cbLeds = findViewById(R.id.cbLeds);
        final Button bSendData = findViewById(R.id.bSendData);
        final ToggleButton bFetch = findViewById(R.id.bFetch);
        final ToggleButton bStart = findViewById(R.id.bStart);
        final LinearLayout llRestart = findViewById(R.id.llRestart);
        final Button bRestart = findViewById(R.id.bRestart);
        final Button bBack = findViewById(R.id.bBack);
        llRestart.setVisibility(View.INVISIBLE);

        bFetch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                attachService();
                if (isChecked) {
                    mDataRequest = MSG_FETCH;
                    mSTMBridge.pack_message_fight_fetch();
                    mService.write(mService.mChatService.FIGHT_ACTIVITY_ID, mSTMBridge.writeSTMBuf);
                    doTimerSendingRequest();

                } else{
                    stopTimerSendingRequest();
                }
            }
        });

        bStart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                attachService();
                if (isChecked) {
                    stopTimerSendingRequest();
                    mDataRequest = MSG_START;
                    mSTMBridge.pack_message_command_robot(mSTMBridge.MSG_START);
                    mService.write(mService.mChatService.FIGHT_ACTIVITY_ID, mSTMBridge.writeSTMBuf);
                } else{
                    bFetch.setVisibility(View.INVISIBLE);
                    bStart.setVisibility(View.INVISIBLE);
                    bSendData.setVisibility(View.INVISIBLE);
                    stopTimerSendingRequest();
                    mDataRequest = MSG_STOP;
                    mSTMBridge.pack_message_command_robot(mSTMBridge.MSG_STOP);
                    mService.write(mService.mChatService.FIGHT_ACTIVITY_ID, mSTMBridge.writeSTMBuf);
                }
            }
        });


        bSendData.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                attachService();
                if (cbDelay.isChecked())
                    mFightBytes[0] = 1;
                else
                    mFightBytes[0] = 0;
                if (cbTranslation.isChecked())
                    mFightBytes[1] = 1;
                else
                    mFightBytes[1] = 0;
                if (cbLeds.isChecked())
                    mFightBytes[2] = 1;
                else
                    mFightBytes[2] = 0;

                mDataRequest = MSG_SEND;
                mSTMBridge.pack_message_fight_send(mFightBytes);
                byte[] send = mSTMBridge.writeSTMBuf;
                mService.write(mService.mChatService.FIGHT_ACTIVITY_ID, send);

            }
        });
        for (int i =0; i< 12; ++i){
            tvOut[i].setText("-1");
        }

        bRestart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                attachService();

                mDataRequest = MSG_RESTART;
                mSTMBridge.pack_message_command_robot(mSTMBridge.MSG_RESTART);
                byte[] send = mSTMBridge.writeSTMBuf;
                mService.write(mService.mChatService.FIGHT_ACTIVITY_ID, send);
                openTacticActivity();
            }
        });

        bBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mDataRequest = MSG_RESTART;
                mSTMBridge.pack_message_command_robot(mSTMBridge.MSG_RESTART);
                byte[] send = mSTMBridge.writeSTMBuf;
                mService.write(mService.mChatService.FIGHT_ACTIVITY_ID, send);
                mService.stopChatService();
                openMainActivity();

            }
        });
    }

    public void doTimerSendingRequest(){
        mTimerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        mService.write(mService.mChatService.FIGHT_ACTIVITY_ID, mSTMBridge.writeSTMBuf);
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

    private void attachService(){
        if (!mAttached){
            mService.attachHandler(mService.mChatService.FIGHT_ACTIVITY_ID, mHandler);
            mAttached = true;
        }
    }




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
                        // Toast.makeText(getApplicationContext(), "Success. Got code: " + mSTMBridge.mRecCode, Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(this, FightActivity.class);
        startActivity(intent);
    }

    public void openSensorActivity() {
        Intent intent = new Intent(this, DebuggingActivity.class);
        startActivity(intent);
    }
    public void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public void openTacticActivity() {
        Intent intent = new Intent(this, TacticActivity.class);
        startActivity(intent);
    }

    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
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
            int var = mSTMBridge.getBridgeInt16Value(i);
            if (i==0 || i ==1){
                float fVar = (float)var / 1000.f;
                tvOut[i].setText(String.format("%f", fVar));
            }
            else if (i==8 || i==9 || i == 10 || i ==11){
                float fVar = (float)var / 10.f;
                tvOut[i].setText(String.format("%f", fVar));
            }
            else
                tvOut[i].setText(String.format("%d", var));
        }
    }

    void enableRestartingRobotView() {
        final LinearLayout llRestart = findViewById(R.id.llRestart);
        llRestart.setVisibility(View.VISIBLE);
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

    private void unpack_app_bridge_message(){
        int status;
        switch (mDataRequest){
            case MSG_FETCH:
                showDataFight();
                break;
            case MSG_SEND:
                status = mSTMBridge.getBridgeValue(0);
                if (status == 0){
                    Toast.makeText(getApplicationContext(), "Success - message sent to robot", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Error - sending message", Toast.LENGTH_SHORT).show();
                }
                break;
            case MSG_STOP:
                status = mSTMBridge.getBridgeValue(0);
                if (status == 0){
                    Toast.makeText(getApplicationContext(), "Robot has stopped!", Toast.LENGTH_SHORT).show();
                    enableRestartingRobotView();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Error - stopping robot", Toast.LENGTH_SHORT).show();
                }
                break;

            case MSG_START:
                status = mSTMBridge.getBridgeValue(0);
                if (status == 0){
                    Toast.makeText(getApplicationContext(), "Robot has started!", Toast.LENGTH_SHORT).show();
                    mDataRequest = MSG_FETCH;
                    mSTMBridge.pack_message_fight_fetch();
                    mService.write(mService.mChatService.FIGHT_ACTIVITY_ID, mSTMBridge.writeSTMBuf);
                    doTimerSendingRequest();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Error - starting robot", Toast.LENGTH_SHORT).show();
                }
                break;
            case MSG_RESTART:
                status = mSTMBridge.getBridgeValue(0);
                if (status != 0){
                    Toast.makeText(getApplicationContext(), "Error - cannot restart robot "+status, Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }
}
