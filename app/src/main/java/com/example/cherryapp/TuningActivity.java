package com.example.cherryapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class TuningActivity extends AppCompatActivity {

    private TextView tvSi01, tvSi02, tvSi03, tvSi04,tvSi11, tvSi12, tvSi13, tvSi14, tvSi21, tvSi22, tvSi23, tvSi24, tvSi31, tvSi32, tvSi33, tvSi34, tvSi41, tvSi42, tvSi43, tvSi44, tvSi51, tvSi52, tvSi53, tvSi54;
    private TextView tvKL1, tvKL2, tvKL3, tvKL4,tvKR1, tvKR2, tvKR3, tvKR4;
    private LinearLayout llSensors, llMotors;
    private ToggleButton bStart;
    private Button bFetch, bSend, bSensors, bMotors, bDebbuging;

    private final Handler mHandler = null;

    public TuningActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuning);
        setupBottomNavigationView();
        findObjectsOnView();
    }

    private void findObjectsOnView(){
        tvSi02 = findViewById(R.id.tvSi02);
        tvSi12 = findViewById(R.id.tvSi12);
        tvSi22 = findViewById(R.id.tvSi22);
        tvSi32 = findViewById(R.id.tvSi32);
        tvSi42 = findViewById(R.id.tvSi42);
        tvSi52 = findViewById(R.id.tvSi52);

        tvKL2 = findViewById(R.id.tvKTL2);
        tvKR2 = findViewById(R.id.tvKTR2);

        tvSi02.setVisibility(View.INVISIBLE);
        tvSi12.setVisibility(View.INVISIBLE);
        tvSi22.setVisibility(View.INVISIBLE);
        tvSi32.setVisibility(View.INVISIBLE);
        tvSi42.setVisibility(View.INVISIBLE);
        tvSi52.setVisibility(View.INVISIBLE);

        tvKL2.setVisibility(View.INVISIBLE);
        tvKR2.setVisibility(View.INVISIBLE);

        tvSi03 = findViewById(R.id.tvSi03);
        tvSi13 = findViewById(R.id.tvSi13);
        tvSi23 = findViewById(R.id.tvSi23);
        tvSi33 = findViewById(R.id.tvSi33);
        tvSi43 = findViewById(R.id.tvSi43);
        tvSi53 = findViewById(R.id.tvSi53);

        tvKL3 = findViewById(R.id.tvKTL3);
        tvKR3 = findViewById(R.id.tvKTR3);

        tvSi03.setText("-1");
        tvSi13.setText("-1");
        tvSi23.setText("-1");
        tvSi33.setText("-1");
        tvSi43.setText("-1");
        tvSi53.setText("-1");

        tvKL3.setText("-1");
        tvKR3.setText("-1");

        bFetch = findViewById(R.id.tbFetch);
        bSend = findViewById(R.id.tbTSend);
        bSensors = findViewById(R.id.tbTSensors);
        bMotors = findViewById(R.id.tbTMotors);
        bDebbuging = findViewById(R.id.tbTDebbuging);
        bStart = findViewById(R.id.tbTStart);

        llSensors = findViewById(R.id.llSensors);
        llMotors = findViewById(R.id.llMotors);
        llSensors.setVisibility(View.INVISIBLE);
        llMotors.setVisibility(View.INVISIBLE);

        bStart.setEnabled(false);

        bSensors.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                llSensors.setVisibility(View.VISIBLE);
                llMotors.setVisibility(View.INVISIBLE);
                bSensors.setEnabled(false);
                bMotors.setEnabled(true);
                bStart.setEnabled(true);
            }
        });

        bMotors.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                llSensors.setVisibility(View.INVISIBLE);
                llMotors.setVisibility(View.VISIBLE);
                bMotors.setEnabled(false);
                bSensors.setEnabled(true);
                bStart.setEnabled(true);
            }
        });

        bDebbuging.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openSensorActivity();
            }
        });

        bStart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    bFetch.setEnabled(false);
                    bSend.setEnabled(false);
                    tvSi02.setVisibility(View.VISIBLE);
                    tvSi12.setVisibility(View.VISIBLE);
                    tvSi22.setVisibility(View.VISIBLE);
                    tvSi32.setVisibility(View.VISIBLE);
                    tvSi42.setVisibility(View.VISIBLE);
                    tvSi52.setVisibility(View.VISIBLE);
                    tvKL2.setVisibility(View.VISIBLE);
                    tvKR2.setVisibility(View.VISIBLE);
                    bDebbuging.setEnabled(false);
                    bSensors.setVisibility(View.INVISIBLE);
                    bMotors.setVisibility(View.INVISIBLE);
                }else{
                    bFetch.setEnabled(true);
                    bSend.setEnabled(true);
                    tvSi02.setVisibility(View.INVISIBLE);
                    tvSi12.setVisibility(View.INVISIBLE);
                    tvSi22.setVisibility(View.INVISIBLE);
                    tvSi32.setVisibility(View.INVISIBLE);
                    tvSi42.setVisibility(View.INVISIBLE);
                    tvSi52.setVisibility(View.INVISIBLE);
                    tvKL2.setVisibility(View.INVISIBLE);
                    tvKR2.setVisibility(View.INVISIBLE);
                    bDebbuging.setEnabled(true);
                    bSensors.setVisibility(View.VISIBLE);
                    bMotors.setVisibility(View.VISIBLE);
                }
            }
        });

        bFetch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                tvSi03.setText("1");
                tvSi13.setText("1");
                tvSi23.setText("1");
                tvSi33.setText("1");
                tvSi43.setText("1");
                tvSi53.setText("1");

                tvKL3.setText("1");
                tvKR3.setText("1");

            }
        });

        bSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
            }
        });

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
