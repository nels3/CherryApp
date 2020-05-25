package com.example.cherryapp;

import androidx.annotation.NonNull;
import android.os.Bundle;
import android.os.Handler;
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

public class TacticActivity extends BasicActivity {

    private boolean service_attached = false;
    private byte mTacticBytes[] = new byte[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        final RadioButton rbIncrement = findViewById(R.id.rbIncrement);
        final RadioButton rbTypeAngle = findViewById(R.id.rbAngle);
        final RadioButton rbTypeTurn = findViewById(R.id.rbTurn);
        final RadioButton rbTypeStop = findViewById(R.id.rbStop);
        final RadioButton rbTypeAfter = findViewById(R.id.rbAfter);
        final RadioButton rbDriveVSlow = findViewById(R.id.rbDriveVSlow);
        final RadioButton rbDriveSlow = findViewById(R.id.rbDriveSlow);
        final RadioButton rbDriveFast = findViewById(R.id.rbDriveFast);
        final RadioButton rbDriveVFast = findViewById(R.id.rbDriveVFast);
        final RadioButton rbTurnSlow = findViewById(R.id.rbTurnSlow);
        final RadioButton rbTurnFast = findViewById(R.id.rbTurnFast);
        final RadioButton rbTimeLongVSlow = findViewById(R.id.rbTimeLongVSlow);
        final RadioButton rbTimeShortSlow = findViewById(R.id.rbTimeShortSlow);
        final RadioButton rbTimeShortVSlow = findViewById(R.id.rbTimeShortVSlow);
        final RadioButton rbTimeLongSlow = findViewById(R.id.rbTimeLongSlow);
        final RadioButton rbTimeShortFast = findViewById(R.id.rbTimeShortFast);

        final RadioGroup rgType = findViewById(R.id.rgType);
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
                    rgDrive.setVisibility(View.INVISIBLE);
                    tvTurn.setVisibility(View.INVISIBLE);
                    tvTime.setVisibility(View.VISIBLE);
                    tvDrive.setVisibility(View.INVISIBLE);
                    rbTimeLongSlow.setVisibility(View.INVISIBLE);
                    rbTimeShortFast.setVisibility(View.INVISIBLE);
                    rbTimeShortSlow.setChecked(true);
                }
            }
        });
        rbTypeStop.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if ( isChecked ){
                    rgTurn.setVisibility(View.VISIBLE);
                    rgTime.setVisibility(View.VISIBLE);
                    rgDrive.setVisibility(View.INVISIBLE);
                    tvTurn.setVisibility(View.VISIBLE);
                    tvTime.setVisibility(View.VISIBLE);
                    tvDrive.setVisibility(View.INVISIBLE);
                    rbTimeLongSlow.setVisibility(View.VISIBLE);
                    rbTimeShortFast.setVisibility(View.VISIBLE);
                    rbTimeShortSlow.setChecked(true);
                    rbTimeShortFast.setChecked(true);
                }
            }
        });

        rbIncrement.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if ( isChecked ){
                    rgDrive.setVisibility(View.INVISIBLE);
                    rgTime.setVisibility(View.INVISIBLE);
                    rgTurn.setVisibility(View.INVISIBLE);
                    rgType.setVisibility(View.INVISIBLE);
                }
            }
        });

        rbDirLeft.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if ( isChecked ){
                    rgDrive.setVisibility(View.VISIBLE);
                    rgTime.setVisibility(View.VISIBLE);
                    rgTurn.setVisibility(View.VISIBLE);
                    rgType.setVisibility(View.VISIBLE);
                }
            }
        });

        rbDirRight.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if ( isChecked ){
                    rgDrive.setVisibility(View.VISIBLE);
                    rgTime.setVisibility(View.VISIBLE);
                    rgTurn.setVisibility(View.VISIBLE);
                    rgType.setVisibility(View.VISIBLE);
                }
            }
        });

    }

        public void setTactic(){
            final RadioButton rbDirLeft = findViewById(R.id.rbLeft);
            final RadioButton rbDirRight = findViewById(R.id.rbRight);
            final RadioButton rbIncrement = findViewById(R.id.rbIncrement);
            final RadioButton rbTypeAngle = findViewById(R.id.rbAngle);
            final RadioButton rbTypeTurn = findViewById(R.id.rbTurn);
            final RadioButton rbTypeStop = findViewById(R.id.rbStop);
            final RadioButton rbTypeAfter = findViewById(R.id.rbAfter);
            final RadioButton rbDriveVSlow = findViewById(R.id.rbDriveVSlow);
            final RadioButton rbDriveSlow = findViewById(R.id.rbDriveSlow);
            final RadioButton rbDriveFast = findViewById(R.id.rbDriveFast);
            final RadioButton rbDriveVFast = findViewById(R.id.rbDriveVFast);
            final RadioButton rbTurnSlow = findViewById(R.id.rbTurnSlow);
            final RadioButton rbTurnFast = findViewById(R.id.rbTurnFast);
            final RadioButton rbTimeLongVSlow = findViewById(R.id.rbTimeLongVSlow);
            final RadioButton rbTimeShortSlow = findViewById(R.id.rbTimeShortSlow);
            final RadioButton rbTimeShortVSlow = findViewById(R.id.rbTimeShortVSlow);
            final RadioButton rbTimeLongSlow = findViewById(R.id.rbTimeLongSlow);
            final RadioButton rbTimeShortFast = findViewById(R.id.rbTimeShortFast);

            for (int i=0; i<5; ++i){
                mTacticBytes[i] = 5;
            }

            //send direction
            if (rbDirLeft.isChecked()) {
                mTacticBytes[0] = 1;
            }
            else if (rbDirRight.isChecked()){
                mTacticBytes[0] = 2;
            }
            else if (rbIncrement.isChecked()){
                mTacticBytes[0] = 3;
            }

            if (rbTypeAngle.isChecked()){

                mTacticBytes[1] = 0;
            }
            else if (rbTypeTurn.isChecked()){
                mTacticBytes[1] = 1;
            }
            else if (rbTypeStop.isChecked()){
                mTacticBytes[1] = 2;
            }
            else if (rbTypeAfter.isChecked()){
                mTacticBytes[1] = 3;
            }

            if (rbTurnSlow.isChecked()){
                mTacticBytes[2] = 0;
            }
            else if (rbTurnFast.isChecked()){
                mTacticBytes[2] = 1;
            }

            if (rbDriveVSlow.isChecked()){
                mTacticBytes[3] = 0;
            }
            else if (rbDriveSlow.isChecked()){
                mTacticBytes[3] = 1;
            }
            else if (rbDriveFast.isChecked()){
                mTacticBytes[3] = 2;
            }
            else if (rbDriveVFast.isChecked()) {
                mTacticBytes[3] = 3;
            }

            if (rbTimeLongVSlow.isChecked()){
                mTacticBytes[4] = 0;
            }
            else if (rbTimeShortSlow.isChecked()){
                mTacticBytes[4] = 1;
            }
            else if (rbTimeShortVSlow.isChecked()){
                mTacticBytes[4] = 2;
            }
            else if (rbTimeLongSlow.isChecked()){
                mTacticBytes[4] = 3;
            }
            else if (rbTimeShortFast.isChecked()){
                mTacticBytes[4] = 4;
            }

            mSTMBridge.pack_message_tactic(mTacticBytes);
            byte[] send = mSTMBridge.writeSTMBuf;
            mService.write(mService.mChatService.TACTIC_ACTIVITY_ID, send);
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

    protected void attachService(){
        if (!service_attached){
            mService.attachHandler(mService.mChatService.FIGHT_ACTIVITY_ID, mHandler);
            service_attached = true;
        }
    }

    private void unpack_app_bridge_message(){
        int tactic_status = mSTMBridge.getBridgeValue(0);
        if (tactic_status==0){
            Toast.makeText(getApplicationContext(), "Success. Tactic set.", Toast.LENGTH_SHORT).show();
            openFightActivity();
        }
        else{
            Toast.makeText(getApplicationContext(), "Wrong tactic.", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(), "Sending "+ writeMessage, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;;
                    mSTMBridge.receive_bytes(readBuf, msg.arg1);

                    if (mSTMBridge.msg_received) {
                        mSTMBridge.message_processed();

                        unpack_app_bridge_message();
                    }
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };



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
