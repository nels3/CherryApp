package com.example.cherryapp;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

public class MyBluetoothService extends Service {
    private final IBinder mBinder = new LocalBinder();

    public BluetoothChat mChatService;

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
        mChatService.stop();
    }

    public void connect(BluetoothDevice device){
        mChatService.connect(device);
    }

    public void write(int ID, byte[] send){
        mChatService.write(ID, send);
    }

}