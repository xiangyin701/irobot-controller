package com.smarthome.utils;

public class ConversionUtils {
    public static byte[] intToBytes(int a) {
        byte[] result = new byte[4];
        for(int i = result.length - 1; i > 0; i --) {
            result[i] = (byte)a;
            a = a >> 8;
        }
        return result;
    }
}
