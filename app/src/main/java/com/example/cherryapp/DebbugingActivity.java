package com.example.cherryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DebbugingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        setupBottomNavigationView();


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
        final TextView tvTSensor0 = findViewById(R.id.tvTSensor0);
        final TextView tvTSensor1 = findViewById(R.id.tvTSensor1);
        final TextView tvTSensor2 = findViewById(R.id.tvTSensor2);
        final TextView tvTSensor3 = findViewById(R.id.tvTSensor3);
        final TextView tvTSensor4 = findViewById(R.id.tvTSensor4);
        final TextView tvTSensor5 = findViewById(R.id.tvTSensor5);
        final TextView tvRight = findViewById(R.id.tvRight);
        final TextView tvShowRight = findViewById(R.id.tvShowRight);
        final TextView tvTShowRight = findViewById(R.id.tvTShowRight);
        final TextView tvLeft = findViewById(R.id.tvLeft);
        final TextView tvShowLeft = findViewById(R.id.tvShowLeft);
        final TextView tvTShowLeft = findViewById(R.id.tvTShowLeft);
        final LinearLayout llImu = findViewById(R.id.llImu);
        final LinearLayout llSISensors = findViewById(R.id.llSensors);
        final LinearLayout llKTIR = findViewById(R.id.llKtir);
        llImu.setVisibility(View.INVISIBLE);
        llSISensors.setVisibility(View.INVISIBLE);
        llKTIR.setVisibility(View.INVISIBLE);

        tbSensor0.setEnabled(false);
        tbSensor1.setEnabled(false);
        tbSensor2.setEnabled(false);
        tbSensor3.setEnabled(false);
        tbSensor4.setEnabled(false);
        tbSensor5.setEnabled(false);
        tbSensorLeft.setEnabled(false);
        tbSensorRight.setEnabled(false);
        /*tvSensor0.setText("0");
        tvSensor1.setText("0");
        tvSensor2.setText("0");
        tvSensor3.setText("0");
        tvSensor4.setText("0");
        tvSensor5.setText("0");
        tvSensorLeft.setText("0");
        tvSensorRight.setText("0");*/

        final ToggleButton tbStart = findViewById(R.id.tbStart);
        final ToggleButton tbDisplay = findViewById(R.id.tbTSend);
        final Button bTuning = findViewById(R.id.tbTDebbuging);

        tbStart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    tbDisplay.setEnabled(false);
                    bTuning.setEnabled(false);
                }else{
                    tbDisplay.setEnabled(true);
                    bTuning.setEnabled(true);
                }
            }
        });


        tbDisplay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    tbSensor0.setVisibility(View.INVISIBLE);
                    tbSensor1.setVisibility(View.INVISIBLE);
                    tbSensor2.setVisibility(View.INVISIBLE);
                    tbSensor3.setVisibility(View.INVISIBLE);
                    tbSensor4.setVisibility(View.INVISIBLE);
                    tbSensor5.setVisibility(View.INVISIBLE);
                    tbSensorLeft.setVisibility(View.INVISIBLE);
                    tbSensorRight.setVisibility(View.INVISIBLE);
                    llSISensors.setVisibility(View.VISIBLE);
                    llKTIR.setVisibility(View.VISIBLE);
                    llImu.setVisibility(View.VISIBLE);

                }else{
                    tbSensor0.setVisibility(View.VISIBLE);
                    tbSensor1.setVisibility(View.VISIBLE);
                    tbSensor2.setVisibility(View.VISIBLE);
                    tbSensor3.setVisibility(View.VISIBLE);
                    tbSensor4.setVisibility(View.VISIBLE);
                    tbSensor5.setVisibility(View.VISIBLE);
                    tbSensorLeft.setVisibility(View.VISIBLE);
                    tbSensorRight.setVisibility(View.VISIBLE);
                    llSISensors.setVisibility(View.INVISIBLE);
                    llKTIR.setVisibility(View.INVISIBLE);
                    llImu.setVisibility(View.INVISIBLE);
                }
            }
        });

        bTuning.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openTuningActivity();
            }
        });

    }


    public void openTuningActivity() {
        Intent intent = new Intent(this, TuningActivity.class);
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

    public void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

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

}
