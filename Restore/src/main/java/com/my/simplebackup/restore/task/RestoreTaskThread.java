package com.my.simplebackup.restore.task;

import java.io.File;
import java.util.Date;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.my.simplebackup.common.Constants;
import com.my.simplebackup.common.HashUtil;
import com.my.simplebackup.common.KeyUtil;
import com.my.simplebackup.common.metadata.FileMetadata;
import com.my.simplebackup.common.metadata.FileMetadataHelper;
import com.my.simplebackup.common.task.TaskResult;
import com.my.simplebackup.restore.decryptor.AES256Decryptor;

/**
 * Restore task thread.
 */
public class RestoreTaskThread implements Callable<TaskResult> {

    private static Logger logger = Logger.getLogger(RestoreTaskThread.class);

    private byte[] keyBytes;
    private String srcPath;
    private String destDir;
    private boolean isFake;

    public RestoreTaskThread(byte[] keyBytes, String srcPath, String destDir, boolean isFake) {
        this.keyBytes = keyBytes;
        this.destDir = destDir;
        this.srcPath = srcPath;
        this.isFake = isFake;
    }

    @Override
    public TaskResult call() throws Exception {
        TaskResult taskResult = new TaskResult();
        try {
            taskResult.setStartTime(new Date().getTime());

            // Get metadata
            byte[] metadataKeyBytes = KeyUtil.getMetadataKeyBytes(this.keyBytes);
            byte[] metadataIVBytes = KeyUtil.getMetadataIVBytes(this.keyBytes);
            AES256Decryptor decryptor = new AES256Decryptor(metadataKeyBytes, metadataIVBytes);
            MetadataDecryptResult metadataRet = decryptor.decryptMetadata(srcPath);
            FileMetadata metadata = metadataRet.getMetadata();
            String json = FileMetadataHelper.getFileMetadataJSON(metadata);

            // Calculate metadata hash
            byte[] hashBytes = HashUtil.getSHA256Hash(json.getBytes(Constants.UTF_8));
            String newMetaDataHash = HashUtil.convertBytesToHexStr(hashBytes);

            // Metadata hash in decrypt file
            String metadataHash = HashUtil.convertBytesToHexStr(metadataRet.getMetadataHash());
            if (!metadataHash.equals(newMetaDataHash)) {
                throw new Exception("Metadata hash is inconsist.");
            }

            // Decrypt file
            String destFullPath = decryptFile(metadataRet.getFilePath(), this.destDir, this.keyBytes,
                            metadataRet.getMetadataEncryptLen(), metadata);

            // Update task result
            taskResult.setDestFileSize(new File(destFullPath).length());
            taskResult.setSucceed(true);
            logger.info("Restore file succeed, src path: " + this.srcPath + ", dest path: " + destFullPath);
        } catch (Exception e) {
            logger.info("Restore file failed, file path: " + this.srcPath + ", error msg: " + e.getMessage());
            taskResult.setSucceed(false);
        } finally {
            taskResult.setSrcFileSize(new File(this.srcPath).length());
            taskResult.setFinishTime(new Date().getTime());
        }
        return taskResult;
    }

    private String decryptFile(String srcFilePath, String destDirPath, byte[] keyBytes, int metadataLen,
                    FileMetadata metadata) throws Exception {
        byte[] fileKeyBytes = KeyUtil.getFileKeyBytes(keyBytes, metadata.getKeySalt());
        byte[] fileIVBytes = KeyUtil.getFileIVBytes(metadata.getAesIV());
        String destFileRelPath = metadata.getFileFullPath().substring(metadata.getFileBasePath().length() + 1);
        String fileBasePathName = new File(metadata.getFileBasePath()).getName();
        StringBuilder sb = new StringBuilder();
        sb.append(destDirPath).append(File.separator).append(fileBasePathName).append(File.separator)
                        .append(destFileRelPath);
        String destFilePath = sb.toString();

        // Delete destination file if exist
        File destFile = new File(destFilePath);
        if (destFile.exists()) {
            destFile.delete();
        }

        // Create empty file for fake mode
        if (this.isFake) {
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            }
            destFile.createNewFile();
        } else {
            AES256Decryptor decryptor = new AES256Decryptor(fileKeyBytes, fileIVBytes);
            decryptor.decryptFile(srcFilePath, destFilePath, metadataLen);
        }
        return destFilePath;
    }
}
