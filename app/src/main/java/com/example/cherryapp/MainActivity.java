package com.example.cherryapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private Button bScan, bDisconnect, bTuning, bDebugging, bFighting;
    private TextView tvDevices;

    private LinearLayout llDevices, llMenu;

    private RecyclerView rvDevicesList;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    //bluetooth
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_CONNECTED = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_SETUP_CONNECTION = 6;

    //getting from bluetoothchatservice
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothChatService mChatService = null;
    // Name of the connected device
    private String mConnectedDeviceName = null;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;

    List<String> datasetNames = new ArrayList<>();
    List<String> datasetAddresses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupBottomNavigationView();
        llDevices = findViewById(R.id.llBluetoothDevicesList);
        llMenu = findViewById(R.id.llMenu);
        rvDevicesList = findViewById(R.id.rvConnectedDevices);
        tvDevices = findViewById(R.id.tvPairedDevices);
        setupButtonsAction();

        rvDevicesList.setVisibility(View.INVISIBLE);
        tvDevices.setVisibility(View.INVISIBLE);
        rvDevicesList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rvDevicesList.setLayoutManager(layoutManager);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mChatService = new BluetoothChatService(this, mBluetoothAdapter, mHandler );
        mAdapter = new MyAdapter(datasetNames, datasetAddresses, mHandler);
        rvDevicesList.setAdapter(mAdapter);
        scanBluetoothDevices();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }


    public void scanBluetoothDevices(){
        //clearing list of pairedDevices
        datasetNames.clear();
        datasetAddresses.clear();

        // Get a set of currently paired devices
        Set pairedDevices = mBluetoothAdapter.getBondedDevices();
        List<BluetoothDevice> listPairedDevices = new ArrayList<BluetoothDevice>(pairedDevices);
        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            for (int i =0; i<pairedDevices.size(); ++i){
                datasetNames.add(listPairedDevices.get(i).getName());
                datasetAddresses.add(listPairedDevices.get(i).getAddress());
            }
        } else {
            datasetNames.add("No devices found");
            datasetAddresses.add("No address available");
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
    }

    public void setViewAsConnected(){
        llDevices.setVisibility(View.INVISIBLE);
        bTuning.setVisibility(View.VISIBLE);
        bFighting.setVisibility(View.VISIBLE);
        bDisconnect.setVisibility(View.VISIBLE);
        bDebugging.setVisibility(View.VISIBLE);
    }


    @Override
    public synchronized void onResume() {
        super.onResume();
        if (mChatService != null) {
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                mChatService.start();
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService.stop();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    //String address = data.getExtras().getString(EXTRA_DEVICE_ADDRESS);
                    String address = MyAdapter.getDeviceAddress();
                    // Get the BLuetoothDevice object
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    // Attempt to connect to the device
                    mChatService.connect(device);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    mChatService = new BluetoothChatService(this, mBluetoothAdapter, mHandler );
                } else {
                    // User did not enable Bluetooth or an error occured
                    Toast.makeText(this, "Not enabled bluetooth", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    private void sendMessage(String message) {

        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);
            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            //tvSend.setText(mOutStringBuffer);
        }
    }

    // The action listener for the EditText widget, to listen for the return key
    private TextView.OnEditorActionListener mWriteListener = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
            }
            return true;
        }
    };


    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    // mAdapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), "Wysylam "+ writeMessage, Toast.LENGTH_SHORT).show();
                    // messageList.add(new androidRecyclerView.Message(counter++, writeMessage, "Me"));
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Toast.makeText(getApplicationContext(), "Odczytalem "+ readMessage, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_DEVICE_CONNECTED:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "+ mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    //buttonSend.setVisibility(View.VISIBLE);
                    setViewAsConnected();

                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;

                case MESSAGE_SETUP_CONNECTION:
                    String deviceName = msg.getData().getString(DEVICE_NAME);
                    String address = MyAdapter.getDeviceAddress();
                    // Get the BLuetoothDevice object
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    //String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                    // Attempt to connect to the device
                    mChatService.connect(device);
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
        startActivity(intent);
    }

    public void openTuningActivity() {
        Intent intent = new Intent(this, TuningV2Activity.class);
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
                        openSensorActivity();
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
        bScan = findViewById(R.id.bScan);
        bScan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                scanBluetoothDevices();
                rvDevicesList.setVisibility(View.VISIBLE);
                tvDevices.setVisibility(View.VISIBLE);
            }
        });

        bDisconnect = findViewById(R.id.bDisconnect);
        bDisconnect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mChatService.stop();
                rvDevicesList.setVisibility(View.INVISIBLE);
                bTuning.setVisibility(View.INVISIBLE);
                bFighting.setVisibility(View.INVISIBLE);
                bDisconnect.setVisibility(View.INVISIBLE);
                bDebugging.setVisibility(View.INVISIBLE);
                bScan.setVisibility(View.VISIBLE);
                tvDevices.setVisibility(View.INVISIBLE);
                rvDevicesList.setVisibility(View.INVISIBLE);
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

        bTuning.setVisibility(View.INVISIBLE);
        bFighting.setVisibility(View.INVISIBLE);
        bDisconnect.setVisibility(View.INVISIBLE);
        bDebugging.setVisibility(View.INVISIBLE);

        /*tvSend = findViewById(R.id.tvSend);
        tvSend.setOnEditorActionListener(mWriteListener);
        tvReceice = findViewById(R.id.tvReceived);
        buttonSend = findViewById(R.id.buttonSend);
        buttonSend.setVisibility(View.INVISIBLE);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView view = (TextView) findViewById(R.id.tvSend);
                String message = view.getText().toString();
                sendMessage(message);
            }
        }); */


    }

}
