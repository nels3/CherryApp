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
        setContentView(R.layout.activity_fight);
        setupBottomNavigationView();



        ToggleButton toggle = (ToggleButton) findViewById(R.id.buttonStart);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startRobot();
                } else{
                    stopRobot();
                }
            }
        });

    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.cbTranslation:
                if (checked){
                    sendNoTranslationStatus(true);
                }
                else{
                    sendNoTranslationStatus(false);
                }
                break;
            case R.id.cbDelay:
                if (checked) {
                    sendStartDelayStatus(true);
                }
                else {
                    sendStartDelayStatus(false);
                }
                break;
        }
    }

    public void sendNoTranslationStatus(boolean status){

    }

    public void sendStartDelayStatus(boolean status){

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
        Intent intent = new Intent(this, SensorActivity.class);
        startActivity(intent);
    }

    public void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
