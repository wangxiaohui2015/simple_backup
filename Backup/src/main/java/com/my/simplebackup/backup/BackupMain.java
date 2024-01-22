package com.my.simplebackup.backup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.my.simplebackup.backup.config.BackupConfigManager;
import com.my.simplebackup.backup.index.IndexConfigManager;
import com.my.simplebackup.backup.index.IndexFile;
import com.my.simplebackup.backup.process.BackupExecutor;
import com.my.simplebackup.backup.process.BackupTaskController;
import com.my.simplebackup.backup.process.BackupTaskThread;
import com.my.simplebackup.backup.record.RecordConfig;
import com.my.simplebackup.backup.record.RecordConfigManager;
import com.my.simplebackup.backup.record.RecordItem;

/**
 * Entry of backup.
 */
public class BackupMain {

    private static Logger logger;

    private BackupConfigManager backupConfigManager;
    private RecordConfigManager recordConfigManager;
    private IndexConfigManager indexConfigManager;
    private BackupExecutor backupExecutor;

    public static void main(String[] args) {
        BackupMain main = new BackupMain();
        main.init(args);
        main.startBackup();
    }

    private void startBackup() {
        try {
            logger.info("Begin to execute backup task.");
            RecordConfig currentRecordConfig = this.recordConfigManager
                    .getCurrentRecordConfig(this.backupConfigManager.getBackupConfig());
            RecordConfig lastSucceedRecordConfig = this.recordConfigManager.getLastSucceedRecordConfig();
            RecordConfig recordConfigDiff = this.recordConfigManager.diffRecordConfig(currentRecordConfig,
                    lastSucceedRecordConfig);
            if (recordConfigDiff.getRecords().isEmpty()) {
                logger.info("No new files to backup.");
                return;
            }
            List<IndexFile> indexFileList = indexConfigManager
                    .generateIndexFileList(recordConfigDiff.getRecords().size());
            BackupTaskController controller = new BackupTaskController();
            List<Future<RecordItem>> futures = new ArrayList<Future<RecordItem>>();
            for (RecordItem item : recordConfigDiff.getRecords()) {
                IndexFile indexFile = indexFileList.remove(0);
                String key = this.backupConfigManager.getBackupConfig().getKey();
                BackupTaskThread thread = new BackupTaskThread(key, item, indexFile, controller);
                Future<RecordItem> future = backupExecutor.submitTask(thread, controller);
                futures.add(future);
            }
            controller.await();
            this.recordConfigManager.updateRecordConfig(lastSucceedRecordConfig, futures);
            this.recordConfigManager.saveRecordConfig(lastSucceedRecordConfig);
        } catch (Throwable e) {
            logger.error("Exception occurred while executing backup task.", e);
        } finally {
            logger.info("Shutting down executor service...");
            backupExecutor.shutDownExecutorService();
            logger.info("End to execute backup task.");
        }
    }

    private void init(String[] args) {
        String rootDir = System.getProperty("rootDir");
        if (null == rootDir || "".equals(rootDir) || !new File(rootDir).isDirectory()) {
            String errorInfo = "ERROR: rootDir is not set or not exist, system exits.";
            System.out.println(errorInfo);
            System.exit(-1);
        }
        try {
            logger = Logger.getLogger(BackupMain.class);
        } catch (Exception e) {
            System.out.println("Failed to initialize logger.");
            System.exit(-1);
        }
        try {
            this.backupConfigManager = new BackupConfigManager(rootDir);
            this.recordConfigManager = new RecordConfigManager(rootDir);
            this.indexConfigManager = new IndexConfigManager(rootDir);
            this.backupExecutor = new BackupExecutor(this.backupConfigManager.getBackupConfig().getThread());
        } catch (Exception e) {
            logger.error("Failed to initialize system.", e);
            System.exit(-1);
        }
    }
}
