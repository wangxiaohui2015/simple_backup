package com.my.simplebackup.backup.task;

import java.io.File;
import java.util.Date;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.my.simplebackup.backup.encryptor.AES256Encryptor;
import com.my.simplebackup.common.Constants;
import com.my.simplebackup.common.KeyUtil;
import com.my.simplebackup.common.metadata.FileMetadata;
import com.my.simplebackup.common.metadata.FileMetadataHelper;
import com.my.simplebackup.common.task.TaskResult;

/**
 * Backup task thread.
 */
public class BackupTaskThread implements Callable<TaskResult> {

    private static Logger logger = Logger.getLogger(BackupTaskThread.class);

    private byte[] keyBytes;
    private BackupTaskConfig backupTaskConfig;

    public BackupTaskThread(byte[] keyBytes, BackupTaskConfig recordItem) {
        this.keyBytes = keyBytes;
        this.backupTaskConfig = recordItem;
    }

    @Override
    public TaskResult call() throws Exception {
        TaskResult taskResult = new TaskResult();
        try {
            taskResult.setStartTime(new Date().getTime());

            // Prepare metadata
            FileMetadata metadata = FileMetadataHelper.generateFileMetadata(
                            this.backupTaskConfig.getSrcBasePath(),
                            this.backupTaskConfig.getSrcFullPath(),
                            this.backupTaskConfig.getDestFullPath(), taskResult.getStartTime(),
                            this.backupTaskConfig.isEnableChecksum());
            String metadataJSON = FileMetadataHelper.getFileMetadataJSON(metadata);
            byte[] metadataBytes = metadataJSON.getBytes(Constants.UTF_8);

            // Encrypt metadata
            byte[] metadataKeyBytes = KeyUtil.getMetadataKeyBytes(this.keyBytes);
            byte[] metadataIVBytes = KeyUtil.getMetadataIVBytes(this.keyBytes);
            AES256Encryptor metadataEncryptor =
                            new AES256Encryptor(metadataKeyBytes, metadataIVBytes);
            metadataEncryptor.encryptMetadata(metadataBytes,
                            this.backupTaskConfig.getDestFullPath());

            // Encrypt file
            byte[] fileKeyBytes = KeyUtil.getFileKeyBytes(this.keyBytes, metadata.getKeySalt());
            byte[] fileIVBytes = KeyUtil.getFileIVBytes(metadata.getAesIV());
            AES256Encryptor fileEncryptor = new AES256Encryptor(fileKeyBytes, fileIVBytes);
            fileEncryptor.encryptFile(this.backupTaskConfig.getSrcFullPath(),
                            this.backupTaskConfig.getDestFullPath());

            // Update task result
            taskResult.setDestFileSize(new File(this.backupTaskConfig.getDestFullPath()).length());
            taskResult.setSucceed(true);
            logger.info("Backup succeed, src path: " + this.backupTaskConfig.getSrcFullPath()
                            + ", dest path: " + this.backupTaskConfig.getDestFullPath());
        } catch (Exception e) {
            logger.info("Backup failed, src path: " + this.backupTaskConfig.getSrcFullPath()
                            + ", dest path: " + this.backupTaskConfig.getDestFullPath()
                            + ", error msg: " + e.getMessage());
            taskResult.setSucceed(false);
        } finally {
            taskResult.setSrcFileSize(new File(this.backupTaskConfig.getSrcFullPath()).length());
            taskResult.setFinishTime(new Date().getTime());
        }
        return taskResult;
    }
}
