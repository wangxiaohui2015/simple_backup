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
     * @param key     Key
     * @throws Exception Exception
     */
    public static void restoreFile(File srcFile, File destDir, byte[] keyBytes) throws Exception {
        MetadataDecryptRet metadataDecryptRet = decryptMetadata(srcFile, keyBytes);
        FileMetadata metaData = FileMetadataHelper.getFileMetadataObj(metadataDecryptRet.getMetadataBytes());
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
        decryptFile(srcFile, destDir, keyBytes, len, metaData);
    }

    private static MetadataDecryptRet decryptMetadata(File file, byte[] keyBytes) throws Exception {
        byte[] metadataKeyBytes = KeyUtil.getMetadataKeyBytes(keyBytes);
        byte[] metadataIVBytes = KeyUtil.getMetadataIVBytes(keyBytes);
        AES256Decryptor decryptor = new AES256Decryptor(metadataKeyBytes, metadataIVBytes);
        MetadataDecryptRet ret = decryptor.decryptMetadata(file.getAbsolutePath());
        return ret;
    }

    private static void decryptFile(File srcFile, File destDir, byte[] keyBytes, int metaDataLen, FileMetadata metaData)
            throws Exception {
        byte[] fileKeyBytes = KeyUtil.getFileKeyBytes(keyBytes, metaData.getKeySalt());
        byte[] fileIVBytes = KeyUtil.getFileIVBytes(metaData.getAesIV());
        String destFileRelPath = metaData.getFileFullPath().substring(metaData.getFileBasePath().length() + 1);
        String fileBasePathName = new File(metaData.getFileBasePath()).getName();
        StringBuilder sb = new StringBuilder();
        sb.append(destDir.getAbsolutePath()).append(File.separator).append(fileBasePathName).append(File.separator)
                .append(destFileRelPath);
        String destFilePath = sb.toString();
        String srcFilePath = srcFile.getAbsolutePath();

        AES256Decryptor decryptor = new AES256Decryptor(fileKeyBytes, fileIVBytes);
        decryptor.decryptFile(srcFilePath, destFilePath, metaDataLen);
    }
}
