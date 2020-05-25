package com.example.cherryapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends BasicActivity {

    private Button bConnect, bTuning, bDebugging, bFighting;

    private static final int REQUEST_ENABLE_BT = 1;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_DEVICE_CONNECTED = 1;
    public static final int MESSAGE_TOAST = 2;
    public static final int MESSAGE_DEVICE_LOST = 3;
    public static final String TOAST = "toast";

    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;

    public static  String WISIENKA_DeviceName = "Wisienka";
    public static  String WISIENKA_DeviceAddress = null;

    public STMBridge mSTMBridge = null;
    protected MyBluetoothService mService;
    protected boolean bluetooth_bonded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSTMBridge = new STMBridge();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void scanBluetoothDevices(){
        // Get a set of currently paired devices
        Set pairedDevices = mBluetoothAdapter.getBondedDevices();
        List<BluetoothDevice> listPairedDevices = new ArrayList<BluetoothDevice>(pairedDevices);

        //get DeviceAdress based on DeviceName
        for (int i =0; i<pairedDevices.size(); ++i){
            if (listPairedDevices.get(i).getName().equals(WISIENKA_DeviceName)){
                WISIENKA_DeviceAddress = listPairedDevices.get(i).getAddress();
                break;
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MyBluetoothService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        setupButtonsOnClickListener();

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            Toast.makeText(this, "Waiting for bluetooth to be enabled", Toast.LENGTH_LONG).show();
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            MyBluetoothService.LocalBinder binder = (MyBluetoothService.LocalBinder) service;
            mService = binder.getService();
            bluetooth_bonded = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bluetooth_bonded = false;
        }
    };


    public void setViewAsConnected(){
        bTuning.setEnabled(true);
        bFighting.setEnabled(true);
        bDebugging.setEnabled(true);
    }
    public void setViewAsDisConnected(){
        bTuning.setEnabled(false);
        bFighting.setEnabled(false);
        bDebugging.setEnabled(false);
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService.getChatService() != null)
            mService.stopChatService();
    }

    @Override
    public void onStop(){
        super.onStop();
        if (bluetooth_bonded) {
            unbindService(mConnection);
            bluetooth_bonded = false;
        }
    }

    // The Handler that gets information back from the BluetoothChatService
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_DEVICE_CONNECTED:
                    Toast.makeText(getApplicationContext(), "Connected to Wisienka", Toast.LENGTH_SHORT).show();
                    bConnect.setText("Disconnect");
                    setViewAsConnected();

                    mSTMBridge.pack_message_command_robot(mSTMBridge.MSG_HI);
                    mService.write(mService.mChatService.MAIN_ACTIVITY_ID, mSTMBridge.writeSTMBuf);
                    break;
                case MESSAGE_DEVICE_LOST:
                    Toast.makeText(getApplicationContext(), "Connection lost", Toast.LENGTH_SHORT).show();
                    bConnect.setText("Connect");
                    openMainActivity();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    private void setupButtonsOnClickListener(){
        bConnect= findViewById(R.id.bConnect);
        bConnect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mService.createBluetoothChatService(mBluetoothAdapter,mHandler);
                if (mService.getChatService() != null) {
                    if (mService.getChatService().getState() == BluetoothChat.STATE_NONE) {
                       mService.startChatService();
                    }
                }
                scanBluetoothDevices();
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(WISIENKA_DeviceAddress);
                mService.connect(device);
            }
        });

        bFighting = findViewById(R.id.bFight);
        bFighting.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openTacticActivity();
            }
        });

        bDebugging = findViewById(R.id.bDebug);
        bDebugging.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openDebuggingActivity();
            }
        });

        bTuning = findViewById(R.id.bTuning);
        bTuning.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openTuningActivity();
            }
        });

        setViewAsDisConnected();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    mService.createBluetoothChatService(mBluetoothAdapter,mHandler);
                } else {
                    // User did not enable Bluetooth or an error ocurred
                    Toast.makeText(this, "Not enabled bluetooth", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }
}
