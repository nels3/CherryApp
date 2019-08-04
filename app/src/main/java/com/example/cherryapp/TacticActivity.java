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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TacticActivity extends AppCompatActivity {
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_TOAST = 5;
    public static final String TOAST = "toast";

    protected MyBluetoothService mService;
    protected boolean mBound = false;
    private boolean mAttached = false;

    private STMBridge mSTMBridge;
    private byte mTacticBytes[] = new byte[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, MyBluetoothService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        mSTMBridge = new STMBridge();

        setContentView(R.layout.activity_tactic);
        setupBottomNavigationView();
        setupInitTactic();

        Button buttonTactic = findViewById(R.id.buttonStartTactics);
        buttonTactic.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                attachService();
                setTactic();
            }
        });

        final RadioButton rbDirLeft = findViewById(R.id.rbLeft);
        final RadioButton rbDirRight = findViewById(R.id.rbRight);
        final RadioButton rbTypeAngle = findViewById(R.id.rbAngle);
        final RadioButton rbTypeTurn = findViewById(R.id.rbTurn);
        final RadioButton rbTypeStop = findViewById(R.id.rbStop);
        final RadioButton rbDriveSlow = findViewById(R.id.rbDriveSlow);
        final RadioButton rbDriveFast = findViewById(R.id.rbDriveFast);
        final RadioButton rbDriveVFast = findViewById(R.id.rbDriveVFast);
        final RadioButton rbTurnSlow = findViewById(R.id.rbTurnSlow);
        final RadioButton rbTurnFast = findViewById(R.id.rbTurnFast);
        final RadioButton rbTimeShort = findViewById(R.id.rbTimeShort);
        final RadioButton rbTimeLong = findViewById(R.id.rbTimeLong);
        final RadioButton rbTimeVLong = findViewById(R.id.rbTimeVLong);
        final RadioGroup rgDrive = findViewById(R.id.rgDrive);
        final RadioGroup rgTime = findViewById(R.id.rgTime);
        final RadioGroup rgTurn = findViewById(R.id.rgTurn);

        final TextView tvTime = findViewById(R.id.tvTime);
        final TextView tvTurn = findViewById(R.id.tvTurn);
        final TextView tvDrive = findViewById(R.id.tvDrive);

        rbTypeAngle.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if ( isChecked ){
                    rgTurn.setVisibility(View.INVISIBLE);
                    rgTime.setVisibility(View.INVISIBLE);
                    rgDrive.setVisibility(View.VISIBLE);
                    tvTurn.setVisibility(View.INVISIBLE);
                    tvTime.setVisibility(View.INVISIBLE);
                    tvDrive.setVisibility(View.VISIBLE);
                    rbDriveSlow.setChecked(true);
                }
            }
        });

        rbTypeTurn.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if ( isChecked ){
                    rgTurn.setVisibility(View.VISIBLE);
                    rgTime.setVisibility(View.INVISIBLE);
                    rgDrive.setVisibility(View.INVISIBLE);
                    tvTurn.setVisibility(View.VISIBLE);
                    tvTime.setVisibility(View.INVISIBLE);
                    tvDrive.setVisibility(View.INVISIBLE);
                    rbTimeVLong.setChecked(false);
                    rbTurnFast.setChecked(true);
                }
            }
        });

        rbTypeStop.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if ( isChecked ){
                    rgTurn.setVisibility(View.INVISIBLE);
                    rgTime.setVisibility(View.VISIBLE);
                    rgDrive.setVisibility(View.VISIBLE);
                    tvTurn.setVisibility(View.INVISIBLE);
                    tvTime.setVisibility(View.VISIBLE);
                    tvDrive.setVisibility(View.VISIBLE);
                    rbDriveSlow.setChecked(true);
                    rbTimeShort.setChecked(true);
                }
            }
        });

    }

        public void setTactic(){
            final RadioButton rbDirLeft = findViewById(R.id.rbLeft);
            final RadioButton rbDirRight = findViewById(R.id.rbRight);
            final RadioButton rbTypeAngle = findViewById(R.id.rbAngle);
            final RadioButton rbTypeTurn = findViewById(R.id.rbTurn);
            final RadioButton rbTypeStop = findViewById(R.id.rbStop);
            final RadioButton rbDriveSlow = findViewById(R.id.rbDriveSlow);
            final RadioButton rbDriveFast = findViewById(R.id.rbDriveFast);
            final RadioButton rbDriveVFast = findViewById(R.id.rbDriveVFast);
            final RadioButton rbTurnSlow = findViewById(R.id.rbTurnSlow);
            final RadioButton rbTurnFast = findViewById(R.id.rbTurnFast);
            final RadioButton rbTimeShort= findViewById(R.id.rbTimeShort);
            final RadioButton rbTimeLong = findViewById(R.id.rbTimeLong);
            final RadioButton rbTimeVLong = findViewById(R.id.rbTimeVLong);

            for (int i=0; i<5; ++i){
                mTacticBytes[i] = 0;
            }

            //send direction
            if (rbDirLeft.isChecked()) {
                mTacticBytes[0] = 1;
            }
            else {
                mTacticBytes[0] = 2;
            }

            if (rbTypeAngle.isChecked()){

                mTacticBytes[1] = 1;
            }
            else if (rbTypeTurn.isChecked()){
                mTacticBytes[1] = 2;
            }
            else if (rbTypeStop.isChecked()){
                mTacticBytes[1] = 3;
            }

            if (rbTurnSlow.isChecked()){
                mTacticBytes[2] = 1;
            }
            else if (rbTurnFast.isChecked()){
                mTacticBytes[2] = 2;
            }

            if (rbDriveSlow.isChecked()){
                mTacticBytes[3] = 1;
            }
            else if (rbDriveFast.isChecked()){
                mTacticBytes[3] = 2;
            }
            else if (rbDriveVFast.isChecked()) {
                mTacticBytes[3] = 3;
            }

            if (rbTimeShort.isChecked()){
                mTacticBytes[4] = 1;
            }
            else if (rbTimeLong.isChecked()){
                mTacticBytes[4] = 2;
            }
            else if (rbTimeVLong.isChecked()){
                mTacticBytes[4] = 3;
            }

            mSTMBridge.pack_message_tactic(mTacticBytes);
            byte[] send = mSTMBridge.writeSTMBuf;
            mService.write(mService.TACTIC_ACTIVITY_ID, send);
        }

        public void setupInitTactic(){
            RadioButton rbLeft = findViewById(R.id.rbLeft);
            RadioButton rbTypeAngle = findViewById(R.id.rbAngle);
            RadioButton rbDriveSlow = findViewById(R.id.rbDriveSlow);
            RadioGroup rgTime = findViewById(R.id.rgTime);
            RadioGroup rgTurn = findViewById(R.id.rgTurn);
            TextView tvTime = findViewById(R.id.tvTime);
            TextView tvTurn = findViewById(R.id.tvTurn);

            rbLeft.setChecked(true);
            rbTypeAngle.setChecked(true);
            rbDriveSlow.setChecked(true);
            rgTime.setVisibility(View.INVISIBLE);
            rgTurn.setVisibility(View.INVISIBLE);
            tvTime.setVisibility(View.INVISIBLE);
            tvTurn.setVisibility(View.INVISIBLE);
        }

    private void attachService(){
        if (!mAttached){
            mService.attachHandler(mService.TACTIC_ACTIVITY_ID, mHandler);
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
                    byte[] readBuf = (byte[]) msg.obj;;
                    mSTMBridge.receive_bytes(readBuf, msg.arg1);

                    if (mSTMBridge.msg_received) {
                        mSTMBridge.message_processed();

                        Toast.makeText(getApplicationContext(), "Success. Tactic set.", Toast.LENGTH_SHORT).show();
                        openFightActivity();
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
        Intent intent = new Intent(this, DebbugingActivity.class);
        startActivity(intent);
    }

    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
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

}
