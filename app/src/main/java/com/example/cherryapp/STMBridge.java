package com.example.cherryapp;

import java.io.Serializable;

public class STMBridge {
    public static byte START_BYTE = 127;
    public static byte END_BYTE = 126;

    //public static final byte MESSAGE_TEST = 9;
    public static final byte MESSAGE_FETCH = 1;

    //public static final byte MESSAGE_TUNING_SENSORS_FETCH = 3;
    //public static final byte MESSAGE_TUNING_SENSORS_SET = 4;
    //public static final byte MESSAGE_TUNING_MOTORS_FETCH = 5;
    //public static final byte MESSAGE_TUNINT_MOTORS_SET = 6;
    //public static final byte MESSAGE_FIGHT_FETCH = 7;
    //public static final byte MESSAGE_FIGHT_SET = 8;

    public static final byte MSG_DIGITAL = 1;
    public static final byte MSG_ANALOG = 2;
    public static final byte MSG_THRESHOLD = 3;
    public static final int MSG_IMU = 4;

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
            if (msg_id != mCode){
                msg_index = -1;
            }
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


    public void pack_message_sensors_fetch(byte msg) {
        mCode = MESSAGE_FETCH;
        mLength = 1;
        byte[] writeBuf = new byte[mLength + 4];
        writeBuf[0] = START_BYTE;
        writeBuf[1] = mCode;
        writeBuf[2] = mLength;
        writeBuf[3] = msg;
        writeBuf[4] = END_BYTE;
        writeSTMBuf = writeBuf;
    }



    public static boolean unpack_message(){

        mRecCode = (byte) msg_id;
        mRecLength = (byte) msg_len;

        if (mRecCode == MESSAGE_FETCH){
            for (int i = 0; i<12; i++) {
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
