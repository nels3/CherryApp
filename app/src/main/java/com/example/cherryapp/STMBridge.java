package com.example.cherryapp;

public class STMBridge {
    public int START_BYTE = 0xFF;
    public int END_BYTE = 0xFE;

    public int MESSAGE_TEST = 0x01;
    public int MESSAGE_DEBUG = 0x02;
    public int MESSAGE_TUNING_SENSORS_FETCH = 0x03;
    public int MESSAGE_TUNING_SENSORS_SET = 0x04;
    public int MESSAGE_TUNING_MOTORS_FETCH = 0x05;
    public int MESSAGE_TUNINT_MOTORS_SET = 0x06;
    public int MESSAGE_FIGHT_FETCH = 0x07;
    public int MESSAGE_FIGHT_SET = 0x08;
    private int mStartByte;
    private int mCode;
    public int mLength;
    private int mData;
    private int mEndByte;

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
