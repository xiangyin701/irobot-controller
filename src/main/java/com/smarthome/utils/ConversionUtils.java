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
    public static int bytesToInt(byte[] a, int start, int end) {
        int result = 0;
        for (int i = start; i <= end; i++) {
            result = (result << 8) + a[i];
        }
        return result;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    private final static char[] hexArray = "0123456789abcdef".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
