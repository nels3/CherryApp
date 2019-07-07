package com.example.cherryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SensorActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_sensors);
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
        setContentView(R.layout.activity_sensor);
        setupBottomNavigationView();

        SeekBar sbPosition = findViewById(R.id.sbPosition);
        sbPosition.setHorizontalScrollBarEnabled(false);
        sbPosition.setMax(100);
        sbPosition.setEnabled(false);
        setBar(0);

        final ToggleButton tbSensor0 = findViewById(R.id.tbSensor0);
        final ToggleButton tbSensor1 = findViewById(R.id.tbSensor1);
        final ToggleButton tbSensor2 = findViewById(R.id.tbSensor2);
        final ToggleButton tbSensor3 = findViewById(R.id.tbSensor3);
        final ToggleButton tbSensor4 = findViewById(R.id.tbSensor4);
        final ToggleButton tbSensor5 = findViewById(R.id.tbSensor5);
        final ToggleButton tbSensorLeft = findViewById(R.id.tbSensorLeft);
        final ToggleButton tbSensorRight = findViewById(R.id.tbSensorRight);
        final TextView tvSensor0 = findViewById(R.id.tvSensor0);
        final TextView tvSensor1 = findViewById(R.id.tvSensor1);
        final TextView tvSensor2 = findViewById(R.id.tvSensor2);
        final TextView tvSensor3 = findViewById(R.id.tvSensor3);
        final TextView tvSensor4 = findViewById(R.id.tvSensor4);
        final TextView tvSensor5 = findViewById(R.id.tvSensor5);
        final TextView tvSensorLeft = findViewById(R.id.tvSensorLeft);
        final TextView tvSensorRight = findViewById(R.id.tvSensorRight);

        tbSensor0.setEnabled(false);
        tbSensor1.setEnabled(false);
        tbSensor2.setEnabled(false);
        tbSensor3.setEnabled(false);
        tbSensor4.setEnabled(false);
        tbSensor5.setEnabled(false);
        tbSensorLeft.setEnabled(false);
        tbSensorRight.setEnabled(false);
        tvSensor0.setText("0");
        tvSensor1.setText("0");
        tvSensor2.setText("0");
        tvSensor3.setText("0");
        tvSensor4.setText("0");
        tvSensor5.setText("0");
        tvSensorLeft.setText("0");
        tvSensorRight.setText("0");

        final ToggleButton tbTestSensors = findViewById(R.id.tbTestSensors);
        final ToggleButton tbShowMeasurements = findViewById(R.id.tbShowMeasurements);
        final ToggleButton tbMotion = findViewById(R.id.tbMotion);
        final ToggleButton tbSafety = findViewById(R.id.tbSafety);
        final ToggleButton tbOrientation = findViewById(R.id.tbOrientation);
        final TextView tvOrientatationInfo = findViewById(R.id.tvOrientationInfo);
        final TextView tvOrientation = findViewById(R.id.tvOrientation);
        tvOrientatationInfo.setVisibility(View.INVISIBLE);
        tvOrientation.setVisibility(View.INVISIBLE);
        tbMotion.setEnabled(false);

        tbSafety.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    tbMotion.setEnabled(true);
                }else{
                    tbMotion.setEnabled(false);
                }
            }
        });

        tbMotion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    tbOrientation.setEnabled(false);
                }else{
                    tbOrientation.setEnabled(true);
                }
            }
        });

        tbTestSensors.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    tbShowMeasurements.setEnabled(false);
                    tbOrientation.setEnabled(false);
                    tbSensor0.setEnabled(true);
                    tbSensor1.setEnabled(true);
                    tbSensor2.setEnabled(true);
                    tbSensor3.setEnabled(true);
                    tbSensor4.setEnabled(true);
                    tbSensor5.setEnabled(true);
                    tbSensorLeft.setEnabled(true);
                    tbSensorRight.setEnabled(true);
                }else{
                    tbShowMeasurements.setEnabled(true);
                    tbOrientation.setEnabled(true);
                    tbSensor0.setEnabled(false);
                    tbSensor1.setEnabled(false);
                    tbSensor2.setEnabled(false);
                    tbSensor3.setEnabled(false);
                    tbSensor4.setEnabled(false);
                    tbSensor5.setEnabled(false);
                    tbSensorLeft.setEnabled(false);
                    tbSensorRight.setEnabled(false);
                }
            }
        });

        tbSensor0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sendSensorsToRobot();
                if (isChecked){
                    tbSensorLeft.setEnabled(false);
                    tbSensorRight.setEnabled(false);
                }else{
                    tbSensorLeft.setEnabled(true);
                    tbSensorRight.setEnabled(true);
                }
            }
        });

        tbSensor1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sendSensorsToRobot();
                if (isChecked){
                    tbSensorLeft.setEnabled(false);
                    tbSensorRight.setEnabled(false);
                }else{
                    tbSensorLeft.setEnabled(true);
                    tbSensorRight.setEnabled(true);
                }
            }
        });

        tbSensor2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sendSensorsToRobot();
                if (isChecked){
                    tbSensorLeft.setEnabled(false);
                    tbSensorRight.setEnabled(false);
                }else{
                    tbSensorLeft.setEnabled(true);
                    tbSensorRight.setEnabled(true);
                }
            }
        });

        tbSensor3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sendSensorsToRobot();
                if (isChecked){
                    tbSensorLeft.setEnabled(false);
                    tbSensorRight.setEnabled(false);
                }else{
                    tbSensorLeft.setEnabled(true);
                    tbSensorRight.setEnabled(true);
                }
            }
        });

        tbSensor4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sendSensorsToRobot();
                if (isChecked){
                    tbSensorLeft.setEnabled(false);
                    tbSensorRight.setEnabled(false);
                }else{
                    tbSensorLeft.setEnabled(true);
                    tbSensorRight.setEnabled(true);
                }
            }
        });

        tbSensor5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sendSensorsToRobot();
                if (isChecked){
                    tbSensorLeft.setEnabled(false);
                    tbSensorRight.setEnabled(false);
                }else{
                    tbSensorLeft.setEnabled(true);
                    tbSensorRight.setEnabled(true);
                }
            }
        });

        tbSensorLeft.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sendSensorsToRobot();
                if (isChecked){
                    tbSensor0.setEnabled(false);
                    tbSensor1.setEnabled(false);
                    tbSensor2.setEnabled(false);
                    tbSensor3.setEnabled(false);
                    tbSensor4.setEnabled(false);
                    tbSensor5.setEnabled(false);
                }else{
                    tbSensor0.setEnabled(true);
                    tbSensor1.setEnabled(true);
                    tbSensor2.setEnabled(true);
                    tbSensor3.setEnabled(true);
                    tbSensor4.setEnabled(true);
                    tbSensor5.setEnabled(true);
                }
            }
        });

        tbSensorRight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sendSensorsToRobot();
                if (isChecked){
                    tbSensor0.setEnabled(false);
                    tbSensor1.setEnabled(false);
                    tbSensor2.setEnabled(false);
                    tbSensor3.setEnabled(false);
                    tbSensor4.setEnabled(false);
                    tbSensor5.setEnabled(false);
                }else{
                    tbSensor0.setEnabled(true);
                    tbSensor1.setEnabled(true);
                    tbSensor2.setEnabled(true);
                    tbSensor3.setEnabled(true);
                    tbSensor4.setEnabled(true);
                    tbSensor5.setEnabled(true);
                }
            }
        });

        tbShowMeasurements.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    tbTestSensors.setEnabled(false);
                    tbOrientation.setEnabled(false);
                    tvSensor0.setVisibility(View.VISIBLE);
                    tvSensor1.setVisibility(View.VISIBLE);
                    tvSensor2.setVisibility(View.VISIBLE);
                    tvSensor3.setVisibility(View.VISIBLE);
                    tvSensor4.setVisibility(View.VISIBLE);
                    tvSensor5.setVisibility(View.VISIBLE);
                    tvSensorLeft.setVisibility(View.VISIBLE);
                    tvSensorRight.setVisibility(View.VISIBLE);
                }else{
                    tbTestSensors.setEnabled(true);
                    tbOrientation.setEnabled(true);
                    tvSensor0.setVisibility(View.INVISIBLE);
                    tvSensor1.setVisibility(View.INVISIBLE);
                    tvSensor2.setVisibility(View.INVISIBLE);
                    tvSensor3.setVisibility(View.INVISIBLE);
                    tvSensor4.setVisibility(View.INVISIBLE);
                    tvSensor5.setVisibility(View.INVISIBLE);
                    tvSensorLeft.setVisibility(View.INVISIBLE);
                    tvSensorRight.setVisibility(View.INVISIBLE);
                }
            }
        });

        tbOrientation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    tbTestSensors.setEnabled(false);
                    tbShowMeasurements.setEnabled(false);
                    tvOrientation.setVisibility(View.VISIBLE);
                    tvOrientatationInfo.setVisibility(View.VISIBLE);
                }else{
                    tbTestSensors.setEnabled(true);
                    tbShowMeasurements.setEnabled(true);
                    tvOrientation.setVisibility(View.INVISIBLE);
                    tvOrientatationInfo.setVisibility(View.INVISIBLE);
                }
            }
        });


    }

    public void sendSensorsToRobot(){


    }

    public void setBar(int position){
        SeekBar sbPosition = findViewById(R.id.sbPosition);
        sbPosition.setProgress(position+50);
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
