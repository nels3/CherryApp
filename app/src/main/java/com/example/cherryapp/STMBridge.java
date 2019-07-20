package com.example.cherryapp;

import java.io.Serializable;

public class STMBridge {
    public static byte START_BYTE = 127;
    public static byte END_BYTE = 126;

    public static final byte MESSAGE_TEST = 9;
    public static final byte MESSAGE_DEBUG_ANALOG = 1;
    public static final byte MESSAGE_DEBUG_DIGITAL = 2;
    public static final byte MESSAGE_TUNING_SENSORS_FETCH = 3;
    public static final byte MESSAGE_TUNING_SENSORS_SET = 4;
    public static final byte MESSAGE_TUNING_MOTORS_FETCH = 5;
    public static final byte MESSAGE_TUNINT_MOTORS_SET = 6;
    public static final byte MESSAGE_FIGHT_FETCH = 7;
    public static final byte MESSAGE_FIGHT_SET = 8;
    public static final byte MESSAGE_FETCH_THRESHOLD = 0;

    public static byte mCode;
    public static byte mLength;
    public static byte mRecCode;
    public static byte mRecLength;
    public static byte mRecData = 0;
    public byte[] writeSTMBuf;

    public static int[] sensors = new int[20];

    public static int msg_index = 0;
    public static int msg_st = 0;
    public static int msg_id = 0;
    public static int msg_len = 0;
    public static int[] msg_msg = new int[255];
    public static int msg_end = 0;
    public static int ind = 0;

    public static boolean msg_received= false;

    public STMBridge() {

    }

    public static void receive_byte(byte msg){
        if (msg_index ==0){
            msg_st = msg;
            if (msg == START_BYTE){
                msg_index = 0;
            }
		else{
                msg_index = -1;
            }
        }
        else if (msg_index ==1){
            msg_id = msg;
        }
        else if (msg_index == 2){
            msg_len = msg+3;
        }
        else if (msg_index > 2 && msg_index < msg_len ){
            msg_msg[ind] = (int) msg;
            ind++;
        }
        else if (msg_index == msg_len){
            msg_received = true;
            msg_end = msg;
            ind = 0;
            msg_index = -1;
        }
        msg_index++;

    }

    public static void receive_bytes(byte[] msg, int len){
        for (int i=0; i<len; ++i){
            receive_byte(msg[i]);
        }

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


    public static boolean unpack_message(){

        mRecCode = (byte) msg_id;
        mRecLength = (byte) msg_len;

        if (mRecCode ==MESSAGE_TEST){
            mRecData = (byte) msg_msg[3];
        }
        else if (mRecCode == MESSAGE_DEBUG_ANALOG || mRecCode == MESSAGE_DEBUG_DIGITAL || mRecCode == MESSAGE_FETCH_THRESHOLD){
            for (int i = 0; i<6; i++){
                sensors[i] = msg_msg[i];
            }
        }
        msg_received = false;
        return true;
    }

    public int getSensorValue(int ID){
        return (int) sensors[ID];
    }

    public boolean getSensorValueBool(int ID){
        if (sensors[ID] ==0){
            return false;
        }
        else{
            return true;
        }
    }

}
