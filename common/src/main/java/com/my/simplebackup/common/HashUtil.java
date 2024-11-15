package com.my.simplebackup.common;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.SecureRandom;

/**
 * Hash utility.
 */
public class HashUtil {

    private static final String SHA_512 = "SHA-512";
    private static final String SHA_256 = "SHA-256";

    /**
     * Get SHA512 of a file.
     * 
     * @param file File
     * @return SHA512 bytes
     * @throws Exception Exception
     */
    public static byte[] getSHA512Hash(File file) throws Exception {
        return getSHAHash(file, SHA_512);
    }

    /**
     * Get SHA512 of bytes.
     * 
     * @param bytes bytes
     * @return SHA512 bytes
     * @throws Exception Exception
     */
    public static byte[] getSHA512Hash(byte[] bytes) throws Exception {
        return getSHAHash(bytes, SHA_512);
    }

    /**
     * Get SHA256 of a file.
     * 
     * @param file File
     * @return SH256 bytes
     * @throws Exception Exception
     */
    public static byte[] getSHA256Hash(File file) throws Exception {
        return getSHAHash(file, SHA_256);
    }

    /**
     * Get SHA256 of bytes.
     * 
     * @param bytes bytes
     * @return SHA256 bytes
     * @throws Exception Exception
     */
    public static byte[] getSHA256Hash(byte[] bytes) throws Exception {
        return getSHAHash(bytes, SHA_256);
    }

    private static byte[] getSHAHash(byte[] bytes, String algorithm) throws Exception {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        md.update(bytes);
        return md.digest();
    }

    private static byte[] getSHAHash(File file, String algorithm) throws Exception {
        try (FileInputStream in = new FileInputStream(file)) {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] bytes = new byte[1024 * 1024];
            int len = -1;
            while ((len = in.read(bytes)) != -1) {
                md.update(bytes, 0, len);
            }
            return md.digest();
        } catch (Exception e) {
            throw e;
        }
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

    /**
     * Generate random string.
     * 
     * @param len length
     * @return Random string
     */
    public static String generateRandomString(int len) {
        return generateRandomString(len, len);
    }

    /**
     * Generate random string in range.
     * 
     * @param lenMin min
     * @param lenMax max
     * @return Random string
     */
    public static String generateRandomString(int lenMin, int lenMax) {
        if (lenMin <= 0 || lenMin > lenMax) {
            return "";
        }
        int len = lenMin + (int) (Math.random() * (lenMax - lenMin + 1));
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder sb = new StringBuilder();
        SecureRandom rnd = new SecureRandom();
        while (sb.length() < len) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            sb.append(SALTCHARS.charAt(index));
        }
        return sb.toString();
    }
}
