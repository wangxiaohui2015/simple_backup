package com.my.simplebackup.restore;

import java.io.File;

import com.my.simplebackup.common.Constants;
import com.my.simplebackup.common.HashUtil;
import com.my.simplebackup.common.KeyUtil;
import com.my.simplebackup.common.metadata.FileMetaData;
import com.my.simplebackup.common.metadata.FileMetaDataHelper;
import com.my.simplebackup.restore.decryptor.AES256Decryptor;

/**
 * Restore service.
 */
public class RestoreService {

    /**
     * Restore a file.
     * 
     * @param srcFile Source file
     * @param destDir Destination folder
     * @param key     Key
     * @throws Exception Exception
     */
    public static void restoreFile(File srcFile, File destDir, String key) throws Exception {
        MetaDataDecryptRet metaDataDecryptRet = decryptMetadata(srcFile, key);
        FileMetaData metaData = FileMetaDataHelper.getFileMetaDataObj(metaDataDecryptRet.getMetaDataBytes());
        String json = FileMetaDataHelper.getFileMetaDataJSON(metaData);

        // Metadata hash in decrypt file
        String metaDataHash = HashUtil.convertBytesToHexStr(metaDataDecryptRet.getMetaDataHash());

        // Calculate metadata hash
        byte[] hashBytes = HashUtil.getSHA256Hash(json.getBytes(Constants.UTF_8));
        String newMetaDataHash = HashUtil.convertBytesToHexStr(hashBytes);

        // Compare metadata hash
        if (!metaDataHash.equals(newMetaDataHash)) {
            throw new Exception("Metadata hash is inconsist.");
        }

        int len = metaDataDecryptRet.getMetaDataEncryptLen();
        decryptFile(srcFile, destDir, key, len, metaData);
    }

    private static MetaDataDecryptRet decryptMetadata(File file, String key) throws Exception {
        byte[] keyBytes = KeyUtil.getMetaDataKeyBytes(key);
        byte[] ivBytes = KeyUtil.getMetaDataIVBytes(key);
        AES256Decryptor decryptor = new AES256Decryptor(keyBytes, ivBytes);
        MetaDataDecryptRet ret = decryptor.decryptMetaData(file.getAbsolutePath());
        return ret;
    }

    private static void decryptFile(File srcFile, File destDir, String key, int metaDataLen, FileMetaData metaData)
            throws Exception {
        byte[] keyBytes = KeyUtil.getFileKeyBytes(key, metaData.getKeySalt());
        byte[] ivBytes = KeyUtil.getFileIVBytes(metaData.getAesIV());
        String srcFilePath = srcFile.getAbsolutePath();
        String destFileRelPath = metaData.getFileFullPath().replaceAll(":", "");
        String destFilePath = destDir.getAbsolutePath() + File.separator + destFileRelPath;

        AES256Decryptor decryptor = new AES256Decryptor(keyBytes, ivBytes);
        decryptor.decryptFile(srcFilePath, destFilePath, metaDataLen);
    }
}
