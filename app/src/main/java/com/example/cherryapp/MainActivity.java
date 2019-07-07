package com.example.cherryapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.bluetooth.BluetoothAdapter;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private Button buttonInfo, buttonConnect, buttonClose, buttonTactics;
    private MenuItem nav_sensors, nav_fight;
    private RecyclerView rvDevicesList;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    //bluetooth
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private EditText mOutEditText;
    private Button mSendButton;
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
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
    // devices list
    private ArrayAdapter mPairedDevicesArrayAdapter = null;
    private ArrayAdapter mNewDevicesArrayAdapter = null;
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    List<String> datasetNames = new ArrayList<>();
    List<String> datasetAddresses = new ArrayList<>();

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupBottomNavigationView();

        final LinearLayout llInfo= findViewById(R.id.llInfo);
        llInfo.setVisibility(View.INVISIBLE);
        rvDevicesList = findViewById(R.id.rvDevicesList);
        rvDevicesList.setVisibility(View.VISIBLE);
        rvDevicesList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rvDevicesList.setLayoutManager(layoutManager);
        mAdapter = new MyAdapter(datasetNames, datasetAddresses);
        rvDevicesList.setAdapter(mAdapter);
        rvDevicesList.setVisibility(View.VISIBLE);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        buttonInfo = findViewById(R.id.buttonInfo);
        buttonInfo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                llInfo.setVisibility(View.VISIBLE);
            }
        });

        buttonClose = findViewById(R.id.buttonClose);
        buttonClose.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                llInfo.setVisibility(View.INVISIBLE);
            }
        });

        buttonConnect = findViewById(R.id.buttonConnect);

        buttonTactics = findViewById(R.id.buttonStartTactics);
        buttonTactics.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openTacticActivity();
            }
        });
        buttonTactics.setVisibility(View.INVISIBLE);

        // one for newly discovered devices
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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

        /*TextView tvResult = findViewById(R.id.result);
        tvResult.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                TextView myOutputBox = (TextView) findViewById(R.id.myOutputBox);
                myOutputBox.setText(s);
            }
        });*/
    }






    // The on-click listener for all devices in the ListViews
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            mBluetoothAdapter.cancelDiscovery();
            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            EXTRA_DEVICE_ADDRESS = address;
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
            mChatService.connect(device);

        }
    };

    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                /*// Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
                // When discovery is finished, change the Activity title*/
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                /*setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_device);
                if (mNewDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.none_found).toString();
                    mNewDevicesArrayAdapter.add(noDevices);
                }*/
            }
        }
    };

    private void doDiscovery() {
        // Indicate scanning in the title
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);
        // Turn on sub-title for new devices
        //findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);
        // If we're already discovering, stop it
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        // Request discover from BluetoothAdapter
        mBluetoothAdapter.startDiscovery();
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

    private void setupChat() {
        //mOutEditText = (EditText) findViewById(R.id.edit_text_out);
       // mOutEditText.setOnEditorActionListener(mWriteListener);
        //mSendButton = (Button) findViewById(R.id.button_send);
       /* mSendButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                TextView view = (TextView) findViewById(R.id.edit_text_out);
                String message = view.getText().toString();
                sendMessage(message);
            }
        });*/

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);

        //// Initialize the buffer for outgoing messages
        //mOutStringBuffer = new StringBuffer("");
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
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occured
                    Toast.makeText(this, "Not enabled bluetooth", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                /*case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    mAdapter.notifyDataSetChanged();
                    messageList.add(new androidRecyclerView.Message(counter++, writeMessage, "Me"));
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    mAdapter.notifyDataSetChanged();
                    messageList.add(new androidRecyclerView.Message(counter++, readMessage, mConnectedDeviceName));
                    break;*/
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "+ mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(this, SensorActivity.class);
        startActivity(intent);
    }

    public void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


}
