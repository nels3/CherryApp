package com.example.cherryapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements Serializable {

    private Button bConnect, bDisconnect, bTuning, bDebugging, bFighting;

    public static final byte START_BYTE = 127;
    public static final byte END_BYTE = 126;

    private static final int REQUEST_ENABLE_BT = 1;
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_CONNECTED = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_SETUP_CONNECTION = 6;

    //getting from bluetoothchatservice
    public static final String TOAST = "toast";
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;

    public static  String WISIENKA_DeviceName = "HC05_CHARLIE";
    public static  String WISIENKA_DeviceAddress = null;

    public STMBridge mSTMBridge = null;
    protected MyBluetoothService mService = new MyBluetoothService();;
    protected boolean mBound = false;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupBottomNavigationView();

        mSTMBridge = new STMBridge();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    public void scanBluetoothDevices(){
        // Get a set of currently paired devices
        Set pairedDevices = mBluetoothAdapter.getBondedDevices();
        List<BluetoothDevice> listPairedDevices = new ArrayList<BluetoothDevice>(pairedDevices);

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
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            Toast.makeText(this, "Waiting for bluetooth to be enabled", Toast.LENGTH_LONG).show();
        }
        Intent intent = new Intent(this, MyBluetoothService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        startService(new Intent(getBaseContext(), MyBluetoothService.class));

        mService.createBluetoothChatService(mBluetoothAdapter,mHandler);

        scanBluetoothDevices();
        setupButtonsAction();
        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }


    public void setViewAsConnected(){
        bTuning.setEnabled(true);
        bFighting.setEnabled(true);
        bDebugging.setEnabled(true);
    }
    public void setViewAsUnConnected(){
        bTuning.setEnabled(false);
        bFighting.setEnabled(false);
        bDebugging.setEnabled(false);
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if (mService.getChatService() != null) {
            if (mService.getChatService().getState() == BluetoothChat.STATE_NONE) {
                mService.startChatService();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService.getChatService() != null) mService.stopChatService();
    }

    @Override
    public void onStop(){
        super.onStop();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
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

                    Toast.makeText(getApplicationContext(), "Got code: "+ mSTMBridge.mRecCode, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_DEVICE_CONNECTED:
                    Toast.makeText(getApplicationContext(), "Connected to Wisienka", Toast.LENGTH_SHORT).show();
                    setViewAsConnected();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    public void openTacticActivity() {
        Intent intent = new Intent(this, TacticActivity.class);
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


    public void openDebuggingActivity() {

        Intent intent = new Intent(this, DebbugingActivity.class);
      //  intent.putExtra("Handler", mHandler);
        startActivity(intent);
    }

    public void openTuningActivity() {
        Intent intent = new Intent(this, TuningActivity.class);
        startActivity(intent);
    }

    public void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        openMainActivity();
                        break;
                    case R.id.navigation_sensors:
                        if (mBound) {
                            mService.generateToast();
                        }
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

    private void setupButtonsAction(){
        bConnect= findViewById(R.id.bConnect);
        bConnect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(WISIENKA_DeviceAddress);
                mService.connect(device);
            }
        });

        bDisconnect = findViewById(R.id.bDisc);
        bDisconnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mService.stopChatService();
                setViewAsUnConnected();
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

        setViewAsUnConnected();

        Button bTest = findViewById(R.id.bTest);
        bTest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mSTMBridge.pack_message_test(1);
                byte[] send = mSTMBridge.writeSTMBuf;
                mService.write(send);
            }
        });
        Button bTest2 = findViewById(R.id.bTest2);
        bTest2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mSTMBridge.pack_message_sensors_fetch();
                byte[] send = mSTMBridge.writeSTMBuf;
                mService.write(send);
            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    mService.createBluetoothChatService(mBluetoothAdapter,mHandler);
                } else {
                    // User did not enable Bluetooth or an error occured
                    Toast.makeText(this, "Not enabled bluetooth", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

}
