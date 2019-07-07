package com.example.cherryapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button buttonInfo, buttonConnect, buttonClose, buttonTactics;
    private MenuItem nav_sensors, nav_fight;
    private RecyclerView rvDevicesList;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    List<String> dataset = new ArrayList<>();

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

        for (int i = 0; i < 10; i++) {
            dataset.add("Test" + i);
        }
        mAdapter = new MyAdapter(dataset);
        rvDevicesList.setAdapter(mAdapter);

        buttonInfo = findViewById(R.id.buttonInfo);
        buttonInfo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                llInfo.setVisibility(View.VISIBLE);
                rvDevicesList.setVisibility(View.INVISIBLE);
            }
        });

        buttonClose = findViewById(R.id.buttonClose);
        buttonClose.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                llInfo.setVisibility(View.INVISIBLE);
                rvDevicesList.setVisibility(View.VISIBLE);
            }
        });

        buttonConnect = findViewById(R.id.buttonConnect);
        buttonConnect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                llInfo.setVisibility(View.INVISIBLE);
                rvDevicesList.setVisibility(View.VISIBLE);
            }
        });

        buttonTactics = findViewById(R.id.buttonStartTactics);
        buttonTactics.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openTacticActivity();
            }
        });
        buttonTactics.setVisibility(View.INVISIBLE);

        /*nav_sensors = findViewById(R.id.navigation_sensors);
        nav_fight = findViewById(R.id.navigation_fight);
        nav_sensors.setEnabled(false);
        nav_fight.setEnabled(false);*/

    }

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
