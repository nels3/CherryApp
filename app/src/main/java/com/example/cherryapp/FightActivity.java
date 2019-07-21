package com.example.cherryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FightActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
