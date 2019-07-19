package com.example.cherryapp;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.Context;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

public class MyBluetoothService extends Service {
    final public int MAIN_ACTIVITY_ID = 0;
    final public int DEBUGGING_ACTIVITY_ID = 1;


    private Handler handler = new Handler();
    private Handler debuggingHandler = new Handler();
    private final IBinder mBinder = new LocalBinder();

    private BluetoothAdapter mBluetoothAdapter = null;
    private final Handler mHandler = null;
    private BluetoothChat mChatService;

    public class LocalBinder extends Binder {
        MyBluetoothService getService() {
            return MyBluetoothService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void createBluetoothChatService(BluetoothAdapter myBluetoothAdapter, Handler myHandler){
        mChatService = new BluetoothChat(this, myBluetoothAdapter, myHandler );
    }

    public void attachHandler(int ID, Handler myHandler){
        mChatService.attachHandler(ID, myHandler);
    }

    public BluetoothChat getChatService(){
        return mChatService;
    }

    public void startChatService(){
        mChatService.start();
    }

    public void stopChatService(){
        mChatService.start();
    }

    public void connect(BluetoothDevice device){
        mChatService.connect(device);
    }

    public void write(int ID, byte[] send){
        mChatService.write(ID, send);
    }

    public void write(){

    }

    public void generateToast() {
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), "WITAJ W SERWISIE(znowu)", Toast.LENGTH_SHORT).show();
            }
        });
    }
}