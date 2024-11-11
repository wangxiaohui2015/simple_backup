package com.my.simplebackup.common;

import java.util.Arrays;

/**
 * Key utility.
 */
public class KeyUtil {

    /**
     * Get metadata key bytes.
     * 
     * @param keyBytes key bytes
     * @return Metadata key bytes.
     * @throws Exception Exception
     */
    public static byte[] getMetadataKeyBytes(byte[] keyBytes) throws Exception {
        byte[] metaDataKeyRootBytes = getMetaDataRootKeyBytes(keyBytes);
        return Arrays.copyOfRange(metaDataKeyRootBytes, 0, 32);
    }

    /**
     * Get metadata IV bytes.
     * 
     * @param keyBytes key bytes
     * @return Metadata IV bytes
     * @throws Exception Exception
     */
    public static byte[] getMetadataIVBytes(byte[] keyBytes) throws Exception {
        byte[] metaDataKeyRootBytes = getMetaDataRootKeyBytes(keyBytes);
        return Arrays.copyOfRange(metaDataKeyRootBytes, 32, 48);
    }

    /**
     * Get file key bytes.
     * 
     * @param keyBytes key bytes
     * @param keySalt  key salt
     * @return File key bytes
     * @throws Exception Exception
     */
    public static byte[] getFileKeyBytes(byte[] keyBytes, String keySalt) throws Exception {
        byte[] rootKeyBytes = generateRootKeyBytes(keyBytes);
        byte[] fileRootKeyBytes = Arrays.copyOfRange(rootKeyBytes, 32, 64);
        String fileRootKeyStr = HashUtil.convertBytesToHexStr(fileRootKeyBytes);
        String fileKey = fileRootKeyStr + keySalt;
        byte[] fileKeyBytes = HashUtil.getSHA512Hash(fileKey.getBytes(Constants.UTF_8));
        return Arrays.copyOfRange(fileKeyBytes, 0, 32);
    }

    /**
     * Get file IV bytes.
     * 
     * @param iv IV string
     * @return file IV bytes
     * @throws Exception Exception
     */
    public static byte[] getFileIVBytes(String iv) throws Exception {
        byte[] ivBytes = HashUtil.getSHA512Hash(iv.getBytes(Constants.UTF_8));
        return Arrays.copyOfRange(ivBytes, 0, 16);
    }

    private static byte[] getMetaDataRootKeyBytes(byte[] keyBytes) throws Exception {
        byte[] rootKeyBytes = generateRootKeyBytes(keyBytes);
        byte[] metaDataRootKeyBytes = Arrays.copyOfRange(rootKeyBytes, 0, 32);
        metaDataRootKeyBytes = HashUtil.getSHA512Hash(metaDataRootKeyBytes);
        return metaDataRootKeyBytes;
    }

    private static byte[] generateRootKeyBytes(byte[] keyBytes) throws Exception {
        byte[] keyHashBytes = HashUtil.getSHA512Hash(keyBytes);
        String keyHashStr = HashUtil.convertBytesToHexStr(keyHashBytes);
        keyHashBytes = HashUtil.getSHA512Hash(keyHashStr.getBytes(Constants.UTF_8));
        return keyHashBytes;
    }
}
