package com.example.cherryapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import androidx.appcompat.app.AppCompatActivity;

public class BasicActivity extends AppCompatActivity {
    // time period for timer requests
    public static int TIMER_PERIOD = 300;

    // handler of messages from bluetooth chat
    public static final int MESSAGE_READ = 1;
    public static final int MESSAGE_TOAST = 2;
    public static final String TOAST = "toast";


    private void unpack_app_bridge_message(){
    }

    public void openTacticActivity() {
        Intent intent = new Intent(this, TacticActivity.class);
        startActivity(intent);
    }

    public void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void openDebuggingActivity() {
        Intent intent = new Intent(this, DebuggingActivity.class);
        startActivity(intent);
    }

    public void openTuningActivity() {
        Intent intent = new Intent(this, TuningActivity.class);
        startActivity(intent);
    }

}
