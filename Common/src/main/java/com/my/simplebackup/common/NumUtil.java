package com.my.simplebackup.common;

/**
 * Number utility.
 */
public class NumUtil {

    /**
     * Convert a int number to 4-length byte array. Note: 0xFF means 00000000
     * 00000000 00000000 11111111, can be used to get last 8 bits of a number.
     * 
     * @param n int number
     * @return 4-length byte array
     */
    public static byte[] intToByte(int n) {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) ((n >> 8 * i) & 0xFF);
        }
        return b;
    }

    /**
     * Convert byte array to int.
     * 
     * @param b byte array.
     * @return int number.
     */
    public static int bytesToInt(byte[] b) {
        int n = 0;
        for (int i = b.length - 1; i >= 0; i--) {
            n = n << 8;
            n = n | (b[i] & 0xFF);
        }
        return n;
    }
}
