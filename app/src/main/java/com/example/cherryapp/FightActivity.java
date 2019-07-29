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
    private STMBridge mSTMBridge;
    private boolean mAttached = false;
    private boolean mAnalog = false;
    private boolean mFetched = false;

    private int mRequest = 0;
    public static final int MSG_DIGITAL = 1;
    public static final int MSG_ANALOG = 2;
    public static final int MSG_THRESHOLD = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, MyBluetoothService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        mSTMBridge = new STMBridge();

        setContentView(R.layout.activity_fight);
        setupBottomNavigationView();

        final CheckBox cbDelay = findViewById(R.id.cbDelay);
        final CheckBox cbTranslation = findViewById(R.id.cbTranslation);
        final CheckBox cbLeds = findViewById(R.id.cbLeds);
        final Button bSendData = findViewById(R.id.bSendData);

        ToggleButton bStart = (ToggleButton) findViewById(R.id.bStart);
        bStart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    startRobot();
                    bSendData.setEnabled(false);
                    cbDelay.setEnabled(false);
                    cbLeds.setEnabled(false);
                    cbTranslation.setEnabled(false);
                } else{
                    stopRobot();
                }
            }
        });


        bSendData.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                boolean delay_status = cbDelay.isSelected();
                boolean translation_status = cbTranslation.isSelected();
                boolean leds_status = cbLeds.isSelected();
            }
        });
    }

        public void startRobot(){

        }

        public void stopRobot(){

        }


    private void attachService(){
        if (!mAttached){
            mService.attachHandler(mService.DEBUGGING_ACTIVITY_ID, mHandler);
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
                            //unpack_message();
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
        bottomNavigationView.setSelectedItemId(R.id.navigation_fight);
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
                        //openFightActivity();
                        break;
                }
                return true;
            }
        });
    }

}
