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
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TacticActivity extends AppCompatActivity {

    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        openMainActivity();
                        break;
                    case R.id.navigation_dashboard:
                        openSensorActivity();
                        break;
                    case R.id.navigation_notifications:
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
        setContentView(R.layout.activity_tactic);
        setupBottomNavigationView();
        setupInitTactic();

        Button buttonTactic = findViewById(R.id.buttonTactic);
        buttonTactic.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
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

        rbTypeAngle.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if ( isChecked ){
                    rbTurnSlow.setChecked(false);
                    rbTurnFast.setChecked(false);
                    rbTimeShort.setChecked(false);
                    rbTimeLong.setChecked(false);
                    rbTimeVLong.setChecked(false);
                }
            }
        });

        rbTypeTurn.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if ( isChecked ){
                    rbDriveSlow.setChecked(false);
                    rbDriveFast.setChecked(false);
                    rbDriveVFast.setChecked(false);
                    rbTimeShort.setChecked(false);
                    rbTimeLong.setChecked(false);
                    rbTimeVLong.setChecked(false);
                }
            }
        });

        rbTypeStop.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if ( isChecked ){
                    rbTurnSlow.setChecked(false);
                    rbTurnFast.setChecked(false);
                }
            }
        });

        rbTurnSlow.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if ( rbTypeAngle.isChecked() || rbTypeStop.isChecked()){
                    rbTurnSlow.setChecked(false);
                }
            }
        });

        rbTurnFast.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if ( rbTypeAngle.isChecked() || rbTypeStop.isChecked() ){
                    rbTurnFast.setChecked(false);
                }
            }
        });

        rbDriveSlow.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if ( rbTypeTurn.isChecked() ){
                    rbDriveSlow.setChecked(false);
                }
            }
        });

        rbDriveFast.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if ( rbTypeTurn.isChecked() ){
                    rbDriveFast.setChecked(false);
                }
            }
        });

        rbDriveVFast.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if ( rbTypeTurn.isChecked()  ){
                    rbDriveVFast.setChecked(false);
                }
            }
        });

        rbTimeShort.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if ( rbTypeTurn.isChecked() || rbTypeAngle.isChecked() ){
                    rbTimeShort.setChecked(false);
                }
            }
        });

        rbTimeLong.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if ( rbTypeTurn.isChecked() || rbTypeAngle.isChecked() ){
                    rbTimeLong.setChecked(false);
                }
            }
        });

        rbTimeVLong.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if ( rbTypeTurn.isChecked() || rbTypeAngle.isChecked() ){
                    rbTimeVLong.setChecked(false);
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

        //ImageView img= findViewById(R.id.ivTactic);


        //send direction
        if (rbDirLeft.isChecked()) {

        }
        else {

        }

        //send type
        if (rbTypeAngle.isChecked()){
            //img.setImageResource(R.drawable.go_back_arrow);
            if (rbDriveSlow.isChecked()){

            }
            else if (rbDriveFast.isChecked()){

            }
            else if (rbDriveVFast.isChecked()){

            }
            else{

            }
        }
        else if (rbTypeTurn.isChecked()){
            //img.setImageResource(R.drawable.returnturn);
            if (rbTurnSlow.isChecked()){

            }
            else if (rbTurnFast.isChecked()){

            }
            else{

            }
        }
        else if (rbTypeStop.isChecked()){
            //img.setImageResource(R.drawable.back_arrows);
            if (rbDriveSlow.isChecked()){

            }
            else if (rbDriveFast.isChecked()){

            }
            else if (rbDriveVFast.isChecked()){

            }

            //more for stop tactic
        }

    }


    public void setupInitTactic(){
        RadioButton rbLeft = findViewById(R.id.rbLeft);
        RadioButton rbTypeAngle = findViewById(R.id.rbAngle);
        RadioButton rbDriveSlow = findViewById(R.id.rbDriveSlow);

        rbLeft.setChecked(true);
        rbTypeAngle.setChecked(true);
        rbDriveSlow.setChecked(true);

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
