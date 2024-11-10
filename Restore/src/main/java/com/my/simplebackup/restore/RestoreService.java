package com.my.simplebackup.restore;

import java.io.File;

import com.my.simplebackup.common.Constants;
import com.my.simplebackup.common.HashUtil;
import com.my.simplebackup.common.KeyUtil;
import com.my.simplebackup.common.metadata.FileMetadata;
import com.my.simplebackup.common.metadata.FileMetadataHelper;
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
     * @param key Key
     * @throws Exception Exception
     */
    public static void restoreFile(File srcFile, File destDir, String key) throws Exception {
        MetadataDecryptRet metadataDecryptRet = decryptMetadata(srcFile, key);
        FileMetadata metaData = FileMetadataHelper
                        .getFileMetadataObj(metadataDecryptRet.getMetadataBytes());
        String json = FileMetadataHelper.getFileMetadataJSON(metaData);

        // Metadata hash in decrypt file
        String metaDataHash = HashUtil.convertBytesToHexStr(metadataDecryptRet.getMetadataHash());

        // Calculate metadata hash
        byte[] hashBytes = HashUtil.getSHA256Hash(json.getBytes(Constants.UTF_8));
        String newMetaDataHash = HashUtil.convertBytesToHexStr(hashBytes);

        // Compare metadata hash
        if (!metaDataHash.equals(newMetaDataHash)) {
            throw new Exception("Metadata hash is inconsist.");
        }

        int len = metadataDecryptRet.getMetadataEncryptLen();
        decryptFile(srcFile, destDir, key, len, metaData);
    }

    private static MetadataDecryptRet decryptMetadata(File file, String key) throws Exception {
        byte[] keyBytes = KeyUtil.getMetadataKeyBytes(key);
        byte[] ivBytes = KeyUtil.getMetadataIVBytes(key);
        AES256Decryptor decryptor = new AES256Decryptor(keyBytes, ivBytes);
        MetadataDecryptRet ret = decryptor.decryptMetadata(file.getAbsolutePath());
        return ret;
    }

    private static void decryptFile(File srcFile, File destDir, String key, int metaDataLen,
                    FileMetadata metaData) throws Exception {
        byte[] keyBytes = KeyUtil.getFileKeyBytes(key, metaData.getKeySalt());
        byte[] ivBytes = KeyUtil.getFileIVBytes(metaData.getAesIV());
        String srcFilePath = srcFile.getAbsolutePath();
        String destFileRelPath = metaData.getFileFullPath()
                        .substring(metaData.getFileBasePath().length() + 1);
        String fileBasePathName = new File(metaData.getFileBasePath()).getName();
        String destFilePath = destDir.getAbsolutePath() + File.separator + fileBasePathName
                        + File.separator + destFileRelPath;

        AES256Decryptor decryptor = new AES256Decryptor(keyBytes, ivBytes);
        decryptor.decryptFile(srcFilePath, destFilePath, metaDataLen);
    }
}
