package com.my.simplebackup.common;

import java.util.Arrays;

/**
 * Key utility.
 */
public class KeyUtil {

    /**
     * Get metadata key bytes.
     * 
     * @param rootKey root key
     * @return Metadata key bytes.
     * @throws Exception Exception
     */
    public static byte[] getMetaDataKeyBytes(String rootKey) throws Exception {
        byte[] metaDataKeyRootBytes = getMetaDataRootKeyBytes(rootKey);
        return Arrays.copyOfRange(metaDataKeyRootBytes, 0, 32);
    }

    /**
     * Get metadata IV bytes.
     * 
     * @param rootKey root key
     * @return Metadata IV bytes
     * @throws Exception Exception
     */
    public static byte[] getMetaDataIVBytes(String rootKey) throws Exception {
        byte[] metaDataKeyRootBytes = getMetaDataRootKeyBytes(rootKey);
        return Arrays.copyOfRange(metaDataKeyRootBytes, 32, 48);
    }

    /**
     * Get file key bytes.
     * 
     * @param rootKey     root key
     * @param rootKeySalt root key salt
     * @return File key bytes
     * @throws Exception Exception
     */
    public static byte[] getFileKeyBytes(String rootKey, String rootKeySalt) throws Exception {
        byte[] rootKeyBytes = generateRootKeyBytes(rootKey);
        byte[] fileRootKeyBytes = Arrays.copyOfRange(rootKeyBytes, 32, 64);
        String fileRootKeyStr = HashUtil.convertBytesToHexStr(fileRootKeyBytes);
        String fileKey = fileRootKeyStr + rootKeySalt;
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

    private static byte[] getMetaDataRootKeyBytes(String rootKey) throws Exception {
        byte[] rootKeyBytes = generateRootKeyBytes(rootKey);
        byte[] metaDataRootKeyBytes = Arrays.copyOfRange(rootKeyBytes, 0, 32);
        metaDataRootKeyBytes = HashUtil.getSHA512Hash(metaDataRootKeyBytes);
        return metaDataRootKeyBytes;
    }

    private static byte[] generateRootKeyBytes(String rootKey) throws Exception {
        byte[] keyHashBytes = HashUtil.getSHA512Hash(rootKey.getBytes(Constants.UTF_8));
        String keyHashStr = HashUtil.convertBytesToHexStr(keyHashBytes);
        keyHashBytes = HashUtil.getSHA512Hash(keyHashStr.getBytes(Constants.UTF_8));
        return keyHashBytes;
    }
}
