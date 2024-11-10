package com.my.simplebackup.backup;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

import com.my.simplebackup.backup.config.BackupConfigManager;
import com.my.simplebackup.backup.config.BackupItem;
import com.my.simplebackup.backup.process.BackupExecutor;
import com.my.simplebackup.backup.process.BackupTaskController;
import com.my.simplebackup.backup.process.BackupTaskThread;
import com.my.simplebackup.backup.record.RecordItem;
import com.my.simplebackup.common.Constants;
import com.my.simplebackup.common.FileUtil;
import com.my.simplebackup.common.HashUtil;

/**
 * The main entry of backup.
 */
public class BackupMain {

    private static Logger logger;

    private BackupTaskController taskController;
    private BackupConfigManager configManager;
    private BackupExecutor executor;

    public static void main(String[] args) {
        new BackupMain(args).startBackup();;
    }

    public BackupMain(String[] args) {
        init(args);
    }

    private void startBackup() {
        try {
            logger.info("Begin to execute backup task.");
            executeBackupTask();
            this.taskController.await();
            logger.info("End to execute backup task.");
        } catch (Throwable e) {
            logger.error("Exception occurred while executing backup task.", e);
        } finally {
            this.executor.shutDownExecutorService();
        }
    }

    private void executeBackupTask() throws Exception {
        List<BackupItem> backupItems = this.configManager.getBackupConfig().getBackups();
        for (BackupItem item : backupItems) {
            String srcDir = item.getSrc();
            String destDir = item.getDest();
            if (null == srcDir || "".equals(srcDir)) {
                logger.warn("sourceDir is null or empty, sourceDir: " + srcDir + ", destDir: "
                                + destDir);
                continue;
            }
            if (null == destDir || "".equals(destDir)) {
                logger.warn("destDir is null or empty, sourceDir: " + srcDir + ", destDir: "
                                + destDir);
                continue;
            }

            File srcFile = new File(srcDir);
            File destFile = new File(destDir);
            if (!srcFile.isDirectory() || !srcFile.exists()) {
                logger.warn("Source dir isn't a directory or doesn't exist, sourceDir: " + srcDir);
                continue;
            }
            if (!destFile.isDirectory() || !destFile.exists()) {
                logger.warn("Dest dir isn't a directory or doesn't exist, destDir: " + destDir);
                continue;
            }
            if (FileUtil.isSubFile(srcFile, destFile)) {
                logger.warn("Source dir is the sub dir of dest dir, or dest dir is the sub dir of source dir, skip this entry, source dir: "
                                + srcDir + ", dest dir: " + destDir);
                continue;
            }

            // Append source directory name to destination directory name
            //destDir = destDir + File.separator + srcFile.getName();

            logger.info("Processing backup task, sourceDir: " + srcDir + ", destDir: " + destDir);
            processBackupTask(srcDir, srcDir, destDir);
        }
    }

    private void processBackupTask(String srcBaseDir, String srcFullDir, String destBaseDir)
                    throws Exception {
        File sourceFile = new File(srcFullDir);
        File[] files = sourceFile.listFiles();
        if (null == files) {
            logger.error("Files is null, cannot process backup task, source dir: " + srcFullDir);
            return;
        }
        for (File file : files) {
            if (file.isFile()) {
                String srcFullPath = file.getAbsolutePath();
                String srcFullPathHash = HashUtil.convertBytesToHexStr(
                                HashUtil.getSHA256Hash(srcFullPath.getBytes(Constants.UTF_8)));
                String destFullDir = genDestFullDir(destBaseDir, srcFullPathHash);
                String destFullPath = genDestFullPath(destFullDir, srcFullPathHash);
                if (new File(destFullPath).exists()) {
                    continue;
                }
                RecordItem recordItem =
                                new RecordItem(srcBaseDir, srcFullPath, destBaseDir, destFullPath);
                String key = this.configManager.getBackupConfig().getKey();
                BackupTaskThread task = new BackupTaskThread(key, recordItem, this.taskController);
                this.executor.submitTask(task, this.taskController);
            } else {
                processBackupTask(srcBaseDir, file.getAbsolutePath(), destBaseDir);
            }
        }
    }

    // Generate destination full directory based on source full path HASH
    // Use the previous 6 characters to be the path directory.
    private String genDestFullDir(String destBaseDir, String srcFullPathHash) {
        String destFullDir = destBaseDir;
        for (int i = 0; i < 6; i++) {
            destFullDir = destFullDir + File.separator + srcFullPathHash.substring(i, i + 1);
        }
        return destFullDir;
    }

    private String genDestFullPath(String destFullDir, String srcFullPathHash) {
        String destFullPath = destFullDir + File.separator + srcFullPathHash + ".data";
        return destFullPath;
    }

    private void init(String[] args) {
        String rootDir = System.getProperty("rootDir");
        if (null == rootDir || "".equals(rootDir) || !new File(rootDir).isDirectory()) {
            System.out.println("ERROR: rootDir is not set or not exist, system exits.");
            System.exit(-1);
        }
        try {
            logger = Logger.getLogger(BackupMain.class);
        } catch (Exception e) {
            System.out.println("Failed to initialize logger.");
            System.exit(-1);
        }
        try {
            this.taskController = new BackupTaskController();
            this.configManager = new BackupConfigManager(rootDir);
            this.executor = new BackupExecutor(this.configManager.getBackupConfig().getThread());
        } catch (Exception e) {
            logger.error("Failedto initialize system.", e);
            System.exit(-1);
        }
    }
}
