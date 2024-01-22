package com.my.simplebackup.backup.process;

import java.io.File;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.my.simplebackup.backup.encryptor.AES256Encryptor;
import com.my.simplebackup.backup.index.IndexFile;
import com.my.simplebackup.backup.record.RecordItem;
import com.my.simplebackup.common.Constants;
import com.my.simplebackup.common.KeyUtil;
import com.my.simplebackup.common.metadata.FileMetaData;
import com.my.simplebackup.common.metadata.FileMetaDataHelper;

/**
 * File encryption thread.
 */
public class BackupTaskThread implements Callable<RecordItem> {

    private static Logger logger = Logger.getLogger(BackupTaskThread.class);

    private String key;
    private RecordItem recordItem;
    private IndexFile indexFile;
    private BackupTaskController controller;

    public BackupTaskThread(String key, RecordItem recordItem, IndexFile indexFile, BackupTaskController controller) {
        this.key = key;
        this.recordItem = recordItem;
        this.indexFile = indexFile;
        this.controller = controller;
    }

    @Override
    public RecordItem call() throws Exception {
        try {
            // Prepare meta data
            FileMetaData metaData = FileMetaDataHelper.generateFileMetaData(this.recordItem.getSrcFullPath(),
                    this.recordItem.getSrcBaseDir());
            String metaDataJSON = FileMetaDataHelper.getFileMetaDataJSON(metaData);
            byte[] metaDataBytes = metaDataJSON.getBytes(Constants.UTF_8);

            // Encrypt meta data
            byte[] metaDataKeyBytes = KeyUtil.getMetaDataKeyBytes(this.key);
            byte[] metaDataIVBytes = KeyUtil.getMetaDataIVBytes(this.key);
            String destPath = new File(this.recordItem.getDestBaseDir() + File.separator + this.indexFile.getFilePath())
                    .getAbsolutePath();
            AES256Encryptor metaDataEncryptor = new AES256Encryptor(metaDataKeyBytes, metaDataIVBytes);
            metaDataEncryptor.encryptMetaData(metaDataBytes, destPath);

            // Encrypt file
            byte[] fileKeyBytes = KeyUtil.getFileKeyBytes(this.key, metaData.getKeySalt());
            byte[] fileIVBytes = KeyUtil.getFileIVBytes(metaData.getAesIV());
            AES256Encryptor fileEncryptor = new AES256Encryptor(fileKeyBytes, fileIVBytes);
            fileEncryptor.encryptFile(this.recordItem.getSrcFullPath(), destPath);

            // Update record item
            this.recordItem.setDestFullPath(destPath);
            this.recordItem.setBackupTime(metaData.getBackupTime());
            this.recordItem.setBackupSucceed(true);
            logger.info("Backup succeed, src path: " + this.recordItem.getSrcFullPath() + ", dest path: " + destPath);
        } catch (Exception e) {
            logger.error("Backup failed, source: " + this.recordItem.getSrcFullPath(), e);
            this.recordItem.setBackupSucceed(false);
        } finally {
            controller.finishTask();
        }
        return this.recordItem;
    }
}
