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
    private String destDir;
    private MetadataDecryptResult metadataRet;
    private boolean isFake;

    public RestoreTaskThread(byte[] keyBytes, String destDir, MetadataDecryptResult metadataRet, boolean isFake) {
        this.keyBytes = keyBytes;
        this.destDir = destDir;
        this.metadataRet = metadataRet;
        this.isFake = isFake;
    }

    @Override
    public TaskResult call() throws Exception {
        TaskResult taskResult = new TaskResult();
        try {
            taskResult.setStartTime(new Date().getTime());

            // Get metadata
            FileMetadata metadata = FileMetadataHelper.getFileMetadataObj(metadataRet.getMetadataBytes());
            String json = FileMetadataHelper.getFileMetadataJSON(metadata);

            // Calculate metadata hash
            byte[] hashBytes = HashUtil.getSHA256Hash(json.getBytes(Constants.UTF_8));
            String newMetaDataHash = HashUtil.convertBytesToHexStr(hashBytes);

            // Metadata hash in decrypt file
            String metaDataHash = HashUtil.convertBytesToHexStr(metadataRet.getMetadataHash());

            // Compare metadata hash
            if (!metaDataHash.equals(newMetaDataHash)) {
                throw new Exception("Metadata hash is inconsist.");
            }

            // Decrypt file
            String destFullPath = decryptFile(this.metadataRet.getFilePath(), this.destDir, this.keyBytes,
                    metadataRet.getMetadataEncryptLen(), metadata);

            // Update task result
            taskResult.setDestFileSize(new File(destFullPath).length());
            taskResult.setSucceed(true);
        } catch (Exception e) {
            logger.info("Restore metadata failed, file path: " + this.metadataRet.getFilePath(), e);
            taskResult.setSucceed(false);
        } finally {
            taskResult.setSrcFileSize(new File(this.metadataRet.getFilePath()).length());
            taskResult.setFinishTime(new Date().getTime());
        }
        return taskResult;
    }

    private String decryptFile(String srcFilePath, String destDirPath, byte[] keyBytes, int metaDataLen,
            FileMetadata metaData) throws Exception {
        byte[] fileKeyBytes = KeyUtil.getFileKeyBytes(keyBytes, metaData.getKeySalt());
        byte[] fileIVBytes = KeyUtil.getFileIVBytes(metaData.getAesIV());
        String destFileRelPath = metaData.getFileFullPath().substring(metaData.getFileBasePath().length() + 1);
        String fileBasePathName = new File(metaData.getFileBasePath()).getName();
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
            decryptor.decryptFile(srcFilePath, destFilePath, metaDataLen);
        }
        return destFilePath;
    }
}
