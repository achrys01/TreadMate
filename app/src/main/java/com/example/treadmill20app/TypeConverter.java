package com.example.treadmill20app;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/*
This class is based on a similar class from the BLE-GATT-Movesense-2.0
application provided by anderslm on github:
https://gits-15.sys.kth.se/anderslm/Ble-Gatt-Movesense-2.0
 */
public class TypeConverter {
    //Converts bytes to unsigned int. Use getShort to bypass the unsigned part
    public static int BytesToUInt(byte[] bytes, int offset,int length) {
        if(length == 1)
            return bytes[offset] & 0xFF;
        else
            return ByteBuffer.wrap(bytes, offset, length).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }
    //Converts bytes to signed int. Use getShort to prevent underflow error
    public static int BytesToSInt(byte[] bytes, int offset,int length) {
        if(length == 1)
            return bytes[offset];
        else
            return ByteBuffer.wrap(bytes, offset, length).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }

    public static byte[] intToBytes(int number, int length) {
        byte[] b = new byte[length];
        for(int i=0;i<length;i++)
            b[i] = (byte)(number >> 8*i);

        return b;
    }
}
