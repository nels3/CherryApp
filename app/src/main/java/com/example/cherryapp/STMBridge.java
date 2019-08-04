package com.example.cherryapp;

import java.io.Serializable;

public class STMBridge {
    public static byte START_BYTE = 127;
    public static byte END_BYTE = 126;

    public static final byte MESSAGE_FETCH = 0;
    public static final byte MSG_DIGITAL = 1;
    public static final byte MSG_ANALOG = 2;
    public static final byte MSG_THRESHOLD = 3;
    public static final byte MSG_ANALOGY = 4;
    public static final byte MESSAGE_SEND_THRESHOLD = 1;
    public static final byte MESSAGE_SEND_TACTIC = 2;
    public static final byte MSG_TACTIC = 5;
    public static final byte MESSAGE_GET_FIGHT = 3;
    public static final byte MSG_FIGHT = 6;
    public static final byte MESSAGE_SEND_FIGHT = 4;

    //public static final byte MESSAGE_TUNING_SENSORS_FETCH = 3;
    //public static final byte MESSAGE_TUNING_SENSORS_SET = 4;
    //public static final byte MESSAGE_TUNING_MOTORS_FETCH = 5;
    //public static final byte MESSAGE_TUNINT_MOTORS_SET = 6;
    //public static final byte MESSAGE_FIGHT_FETCH = 7;
    //public static final byte MESSAGE_FIGHT_SET = 8;

    public static final int MSG_IMU = 4;

    public static byte mCode;
    public static byte mLength;
    public static int mRecCode;
    public static byte mType;
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
            mRecCode = msg_id;
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
        mType = msg;
        mLength = 1;
        byte[] writeBuf = new byte[mLength + 4];
        writeBuf[0] = START_BYTE;
        writeBuf[1] = mCode;
        writeBuf[2] = mLength;
        writeBuf[3] = msg;
        writeBuf[4] = END_BYTE;
        writeSTMBuf = writeBuf;
    }

    public void pack_message_threshold(int[] msg) {
        mCode = MESSAGE_SEND_THRESHOLD;
        mType = MSG_THRESHOLD;
        mLength = 16;
        byte[] writeBuf = new byte[mLength + 4];
        writeBuf[0] = START_BYTE;
        writeBuf[1] = mCode;
        writeBuf[2] = mLength;
        for (int i = 0; i < 8; ++i){
            writeBuf[3 + 2 * i] =  (byte)(msg[i] & 0xFF);
            writeBuf[3 + 2 * i + 1] = (byte)(msg[i] >> 8);
        }
        writeBuf[19] = END_BYTE;
        writeSTMBuf = writeBuf;
    }

    public void pack_message_tactic(byte[] msg){
        mCode = MESSAGE_SEND_TACTIC;
        mType = MSG_TACTIC;
        mLength = 5;
        byte[] writeBuf = new byte[mLength + 4];
        writeBuf[0] = START_BYTE;
        writeBuf[1] = mCode;
        writeBuf[2] = mLength;
        for (int i=0; i<5; ++i)
            writeBuf[3+i] = msg[i];
        writeBuf[8] = END_BYTE;
        writeSTMBuf = writeBuf;
    }

    public void pack_message_fight_fetch(){
        mCode = MESSAGE_GET_FIGHT;
        mType = MSG_FIGHT;
        mLength = 0;
        byte[] writeBuf = new byte[mLength + 4];
        writeBuf[0] = START_BYTE;
        writeBuf[1] = mCode;
        writeBuf[2] = mLength;
        writeBuf[3] = END_BYTE;
        writeSTMBuf = writeBuf;
    }

    public void pack_message_fight_send(byte[] msg){
        mCode = MESSAGE_SEND_FIGHT;
        mType = MSG_FIGHT;
        mLength = 3;
        byte[] writeBuf = new byte[mLength + 4];
        writeBuf[0] = START_BYTE;
        writeBuf[1] = mCode;
        writeBuf[2] = mLength;
        for (int i=0; i<3; ++i)
            writeBuf[3+i] = msg[i];
        writeBuf[6] = END_BYTE;
        writeSTMBuf = writeBuf;
    }



    public static boolean unpack_message_sensors_fetch(){
        msg_received = false;
        return true;
    }

    public int getBridgeValue(int ID){
        //int sensor = msg_msg[2*ID]*100+msg_msg[2*ID];
        return (int)msg_msg[ID];
    }

    public int getBridgeInt16Value(int ID){
        int sensor = (((msg_msg[2*ID+1] & 0xFF) << 8) | (msg_msg[2*ID]& 0xFF));
        return sensor;

    }

    public boolean getSensorValueBool(int ID){
        if (msg_msg[ID] ==0){
            return false;
        }
        else{
            return true;
        }
    }

}
