package com.my.simplebackup.common;

import java.security.MessageDigest;

/**
 * Hash utility.
 * 
 * @author Administrator
 */
public class HashUtil {

    /**
     * Get SHA512 hash value.
     * 
     * @param bytes given bytes
     * @return SHA512 hash value
     * @throws Exception Exception
     */
    public static byte[] getSHA512Hash(byte[] bytes) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(bytes);
        return md.digest();
    }

    /**
     * Convert bytes to hex string.
     * 
     * @param bytes given bytes
     * @return Hex string of bytes
     */
    public static String convertBytesToHexStr(byte[] bytes) {
        if (null == bytes) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (byte b : bytes) {
            String str = Integer.toHexString(b & 0xFF);
            if (str.length() == 1) {
                sb.append(0);
            }
            sb.append(str);
        }
        return sb.toString().toUpperCase();
    }
}
