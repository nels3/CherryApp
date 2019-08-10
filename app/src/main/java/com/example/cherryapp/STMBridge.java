package com.example.cherryapp;

import java.io.Serializable;

public class STMBridge {
    public static byte START_BYTE = 127;
    public static byte END_BYTE = 126;

    public static final byte MESSAGE_FETCH = 0;
    public final byte MSG_DIGITAL = 1;
    public final byte MSG_ANALOG = 2;
    public final byte MSG_THRESHOLD = 3;
    public final byte MSG_ANALOGY = 4;

    public static final byte MESSAGE_SEND_THRESHOLD = 1;
    public static final byte MESSAGE_SEND_TACTIC = 2;
    public static final byte MSG_TACTIC = 5;
    public static final byte MESSAGE_GET_FIGHT = 3;
    public static final byte MSG_FIGHT = 6;
    public static final byte MESSAGE_SEND_FIGHT = 4;
    public static final byte MESSAGE_COMMAND_ROBOT = 5;

    public static byte mCode;
    public static byte mLength;
    public static int mRecCode;
    public static byte mType;
    public byte[] writeSTMBuf;


    public static int start_byte = 0;
    public static int id = 0;
    public static int length = 0;
    public static int[] msg = new int[255];
    public static int end_byte = 0;

    public static int byte_iterator = 0;
    public static int index = 0;

    public static boolean msg_received= false;

    public STMBridge() {

    }

    public static void receive_byte(byte msg_byte){
        if (byte_iterator ==0){
            start_byte = msg_byte;
            if (start_byte == START_BYTE){
                byte_iterator = 0;
            }
		    else{
                byte_iterator = -1;
            }
        }
        else if (byte_iterator ==1){
            id = msg_byte;
            if (id != mCode){
                byte_iterator = -1;
            }
        }
        else if (byte_iterator == 2){
            length = msg_byte+3;
        }
        else if (byte_iterator > 2 && byte_iterator < length ){
            msg[index] = (int) msg_byte;
            index++;
        }
        else if (byte_iterator == length){
            end_byte = msg_byte;
            if (end_byte == END_BYTE) {
                msg_received = true;
                mRecCode = id;
            }
            index = 0;
            byte_iterator = -1;
        }
        byte_iterator++;
    }

    public static void receive_bytes(byte[] msg, int len){
        for (int i=0; i<len; ++i){
            receive_byte(msg[i]);
        }
    }

    public static void message_processed(){
        msg_received = false;
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

    public void pack_message_command_robot(byte msg){
        mCode = MESSAGE_COMMAND_ROBOT;
        mType = MSG_FIGHT;
        mLength = 1;
        byte[] writeBuf = new byte[mLength + 4];
        writeBuf[0] = START_BYTE;
        writeBuf[1] = mCode;
        writeBuf[2] = mLength;
        writeBuf[3] = msg;
        writeBuf[4] = END_BYTE;
        writeSTMBuf = writeBuf;
    }

    public int getBridgeValue(int ID){
        //int sensor = msg_msg[2*ID]*100+msg_msg[2*ID];
        return (int)msg[ID];
    }

    public int getBridgeInt16Value(int ID){
        int sensor = (((msg[2*ID+1] & 0xFF) << 8) | (msg[2*ID]& 0xFF));
        return sensor;

    }

    public boolean getSensorValueBool(int ID){
        if (msg[ID] == 0){
            return false;
        }
        else{
            return true;
        }
    }

}
