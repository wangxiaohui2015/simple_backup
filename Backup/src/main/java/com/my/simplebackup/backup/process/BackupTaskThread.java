package com.my.simplebackup.backup.process;

import java.util.Date;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.my.simplebackup.backup.encryptor.AES256Encryptor;
import com.my.simplebackup.backup.record.RecordItem;
import com.my.simplebackup.common.Constants;
import com.my.simplebackup.common.KeyUtil;
import com.my.simplebackup.common.metadata.FileMetadata;
import com.my.simplebackup.common.metadata.FileMetadataHelper;

/**
 * Backup task thread.
 */
public class BackupTaskThread implements Callable<RecordItem> {

    private static Logger logger = Logger.getLogger(BackupTaskThread.class);

    private String key;
    private RecordItem recordItem;
    private BackupTaskController taskController;

    public BackupTaskThread(String key, RecordItem recordItem,
                    BackupTaskController taskController) {
        this.key = key;
        this.recordItem = recordItem;
        this.taskController = taskController;
    }

    @Override
    public RecordItem call() throws Exception {
        try {
            // Prepare metadata
            FileMetadata metadata = FileMetadataHelper.generateFileMetadata(
                            this.recordItem.getSrcBasePath(), this.recordItem.getSrcFullPath());
            String metaDataJSON = FileMetadataHelper.getFileMetadataJSON(metadata);
            byte[] metaDataBytes = metaDataJSON.getBytes(Constants.UTF_8);

            // Encrypt metadata
            byte[] metaDataKeyBytes = KeyUtil.getMetadataKeyBytes(this.key);
            byte[] metaDataIVBytes = KeyUtil.getMetadataIVBytes(this.key);
            AES256Encryptor metaDataEncryptor =
                            new AES256Encryptor(metaDataKeyBytes, metaDataIVBytes);
            metaDataEncryptor.encryptMetadata(metaDataBytes, this.recordItem.getDestFullPath());

            // Encrypt file
            byte[] fileKeyBytes = KeyUtil.getFileKeyBytes(this.key, metadata.getKeySalt());
            byte[] fileIVBytes = KeyUtil.getFileIVBytes(metadata.getAesIV());
            AES256Encryptor fileEncryptor = new AES256Encryptor(fileKeyBytes, fileIVBytes);
            fileEncryptor.encryptFile(this.recordItem.getSrcFullPath(),
                            this.recordItem.getDestFullPath());

            // Update record item
            this.recordItem.setBackupStartTime(metadata.getBackupTime());
            this.recordItem.setBackupFinishTime(new Date().getTime());
            this.recordItem.setBackupSucceed(true);
            logger.info("Backup succeed, src path: " + this.recordItem.getSrcFullPath()
                            + ", dest path: " + this.recordItem.getDestFullPath());
        } catch (Exception e) {
            logger.info("Backup failed, src path: " + this.recordItem.getSrcFullPath()
                            + ", dest path: " + this.recordItem.getDestFullPath(), e);
            this.recordItem.setBackupSucceed(false);
        } finally {
            taskController.finishTask();
        }
        return this.recordItem;
    }
}
