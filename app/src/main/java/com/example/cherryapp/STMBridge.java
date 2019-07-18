package com.example.cherryapp;

import java.io.Serializable;

public class STMBridge {
    public static byte START_BYTE = 127;
    public static byte END_BYTE = 126;

    public static byte MESSAGE_TEST = 1;
    public static byte MESSAGE_DEBUG = 2;
    public static byte MESSAGE_TUNING_SENSORS_FETCH = 0x03;
    public static byte MESSAGE_TUNING_SENSORS_SET = 0x04;
    public static byte MESSAGE_TUNING_MOTORS_FETCH = 0x05;
    public static byte MESSAGE_TUNINT_MOTORS_SET = 0x06;
    public static byte MESSAGE_FIGHT_FETCH = 0x07;
    public static byte MESSAGE_FIGHT_SET = 0x08;
    public static byte mCode;
    public static byte mLength;
    public static byte mRecCode;
    public static byte mRecLength;
    public static byte mRecData = 0;
    public byte[] writeSTMBuf;

    public static byte sensor0;
    public static byte sensor1;
    public static byte sensor2;
    public static byte sensor3;
    public static byte sensor4;
    public static byte sensor5;

    public STMBridge() {

    }

    public void pack_message_test(int msg) {
        mCode = MESSAGE_TEST;
        mLength = 1;
        byte[] writeBuf = new byte[mLength + 4];
        writeBuf[0] = (byte) START_BYTE;
        writeBuf[1] = (byte) MESSAGE_TEST;
        writeBuf[2] = (byte) 1;
        writeBuf[3] = (byte) 1;
        writeBuf[4] = (byte) END_BYTE;
        writeSTMBuf = writeBuf;
    }

    public void pack_message_sensors_fetch() {
        mCode = MESSAGE_TUNING_SENSORS_FETCH;
        mLength = 0;
        byte[] writeBuf = new byte[mLength + 4];
        writeBuf[0] = START_BYTE;
        writeBuf[1] = MESSAGE_TUNING_SENSORS_FETCH;
        writeBuf[2] = mLength;
        writeBuf[3] = END_BYTE;
        writeSTMBuf = writeBuf;
    }

    public static boolean unpack_message(byte[] msg, int length){
        if (msg[0]!=START_BYTE){
            return false;
        }
        mRecCode = msg[1];
        mRecLength = msg[2];

        if (mRecCode ==MESSAGE_TEST){
            mRecData = msg[3];
        }
        else if (mRecCode == MESSAGE_TUNING_SENSORS_FETCH){
            if (mRecLength <6){
                return false;
            }
            sensor0 = msg[3];
            sensor1 = msg[4];
            sensor2 = msg[5];
            sensor3 = msg[6];
            sensor4 = msg[7];
            sensor5 = msg[8];
        }
        return true;
    }

}
