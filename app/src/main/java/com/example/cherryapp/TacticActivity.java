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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TacticActivity extends AppCompatActivity {

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

            openFightActivity();

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
