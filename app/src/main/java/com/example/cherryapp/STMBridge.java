package com.example.cherryapp;

import java.io.Serializable;

public class STMBridge {
    public static byte START_BYTE = 127;
    public static byte END_BYTE = 126;

    public static final byte MESSAGE_TEST = 0;
    public static final byte MESSAGE_DEBUG_ANALOG = 1;
    public static final byte MESSAGE_DEBUG_DIGITAL = 2;
    public static final byte MESSAGE_TUNING_SENSORS_FETCH = 3;
    public static final byte MESSAGE_TUNING_SENSORS_SET = 4;
    public static final byte MESSAGE_TUNING_MOTORS_FETCH = 5;
    public static final byte MESSAGE_TUNINT_MOTORS_SET = 6;
    public static final byte MESSAGE_FIGHT_FETCH = 7;
    public static final byte MESSAGE_FIGHT_SET = 8;
    public static final byte MESSAGE_FETCH_THRESHOLD = 9;

    public static byte mCode;
    public static byte mLength;
    public static byte mRecCode;
    public static byte mRecLength;
    public static byte mRecData = 0;
    public byte[] writeSTMBuf;

    public static byte sensor0=0;
    public static byte sensor1=0;
    public static byte sensor2=0;
    public static byte sensor3=0;
    public static byte sensor4=0;
    public static byte sensor5=0;
    public static byte sensorLeft=0;
    public static byte sensorRight=0;

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

    public void pack_message_sensors_fetch(boolean analog) {
        if (analog)
            mCode = MESSAGE_DEBUG_ANALOG;
        else
            mCode = MESSAGE_DEBUG_DIGITAL;
        mLength = 0;
        byte[] writeBuf = new byte[mLength + 4];
        writeBuf[0] = START_BYTE;
        writeBuf[1] = mCode;
        writeBuf[2] = mLength;
        writeBuf[3] = END_BYTE;
        writeSTMBuf = writeBuf;
    }

    public void pack_message_fetch_threshold() {
        mCode = MESSAGE_FETCH_THRESHOLD;
        mLength = 0;
        byte[] writeBuf = new byte[mLength + 4];
        writeBuf[0] = START_BYTE;
        writeBuf[1] = mCode;
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

    public int getSensorValue(int ID){
        switch (ID){
            case 0:
                return (int)sensor0;
            case 1:
                return (int)sensor1;
            case 2:
                return (int)sensor2;
            case 3:
                return (int)sensor3;
            case 4:
                return (int)sensor4;
            case 5:
                return (int)sensor5;
            case 6:
                return (int)sensorRight;
            case 7:
                return (int)sensorLeft;
        }
        return -1;
    }

    public boolean getSensorValueBool(int ID){
        switch (ID){
            case 0:
                if (sensor0 ==0)
                    return false;
                else
                    return true;
            case 1:
                if (sensor1 ==0)
                    return false;
                else
                    return true;
            case 2:
                if (sensor2 ==0)
                    return false;
                else
                    return true;
            case 3:
                if (sensor3 ==0)
                    return false;
                else
                    return true;
            case 4:
                if (sensor4 ==0)
                    return false;
                else
                    return true;
            case 5:
                if (sensor5 ==0)
                    return false;
                else
                    return true;
            case 6:
                if (sensorRight ==0)
                    return false;
                else
                    return true;
            case 7:
                if (sensorLeft ==0)
                    return false;
                else
                    return true;
        }
        return false;
    }

}
