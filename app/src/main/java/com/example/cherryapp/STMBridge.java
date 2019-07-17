package com.example.cherryapp;

public class STMBridge {
    public byte START_BYTE = 127;
    public byte END_BYTE = 126;

    public byte MESSAGE_TEST = 1;
    public byte MESSAGE_DEBUG = 2;
    public byte MESSAGE_TUNING_SENSORS_FETCH = 0x03;
    public byte MESSAGE_TUNING_SENSORS_SET = 0x04;
    public byte MESSAGE_TUNING_MOTORS_FETCH = 0x05;
    public byte MESSAGE_TUNINT_MOTORS_SET = 0x06;
    public byte MESSAGE_FIGHT_FETCH = 0x07;
    public byte MESSAGE_FIGHT_SET = 0x08;
    private byte mCode;
    public byte mLength;
    private int mData;

    public byte[] writeSTMBuf;

    public STMBridge() {

    }

    public void pack_message_test(int msg) {
        mCode = MESSAGE_TEST;
        mLength = 1;
        mData = msg;
        byte[] writeBuf = new byte[mLength + 4];
        writeBuf[0] = (byte) START_BYTE;
        writeBuf[1] = (byte) mCode;
        writeBuf[2] = (byte) mLength;
        writeBuf[3] = (byte) msg;
        writeBuf[4] = (byte) END_BYTE;
        writeSTMBuf = writeBuf;
    }

}
