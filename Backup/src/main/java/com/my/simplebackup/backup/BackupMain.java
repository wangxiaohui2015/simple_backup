package com.my.simplebackup.backup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.my.simplebackup.backup.config.BackupConfigManager;
import com.my.simplebackup.backup.config.BackupItem;
import com.my.simplebackup.backup.task.BackupTaskConfig;
import com.my.simplebackup.backup.task.BackupTaskThread;
import com.my.simplebackup.common.Constants;
import com.my.simplebackup.common.FileUtil;
import com.my.simplebackup.common.HashUtil;
import com.my.simplebackup.common.StringUtil;
import com.my.simplebackup.common.statistics.StatisticsEntity;
import com.my.simplebackup.common.statistics.StatisticsHelper;
import com.my.simplebackup.common.task.TaskResult;

/**
 * The main entry of backup.
 */
public class BackupMain {

    private static Logger logger;

    private BackupConfigManager configManager;
    private ExecutorService taskExecutor;

    public static void main(String[] args) {
        new BackupMain(args).startBackup();
    }

    public BackupMain(String[] args) {
        init(args);
    }

    private void startBackup() {
        try {
            logger.info("Begin to execute backup task.");
            StatisticsEntity statEntity = new StatisticsEntity();
            statEntity.startStat();
            List<TaskResult> taskResult = executeBackupTask();
            statEntity.endStat();
            StatisticsHelper.statAndShowBackupResult(statEntity, taskResult);
        } catch (Throwable e) {
            logger.error("Exception occurred while executing backup task.", e);
        } finally {
            this.taskExecutor.shutdown();
            logger.info("End to execute backup task.");
        }
    }

    private List<TaskResult> executeBackupTask() throws Exception {
        List<TaskResult> taskResults = new ArrayList<TaskResult>();
        List<Future<TaskResult>> taskFutures = new ArrayList<Future<TaskResult>>();

        List<BackupItem> backupItems = this.configManager.getBackupConfig().getBackups();
        for (BackupItem item : backupItems) {
            String srcDir = item.getSrc();
            String destDir = item.getDest();
            if (!verifyBackupTask(srcDir, destDir)) {
                continue;
            }
            logger.info("Processing backup task, sourceDir: " + srcDir + ", destDir: " + destDir);
            processBackupTask(srcDir, srcDir, destDir, taskFutures);
        }

        // Wait all task to be finished and get results
        for (Future<TaskResult> future : taskFutures) {
            taskResults.add(future.get());
        }
        return taskResults;
    }

    private boolean verifyBackupTask(String srcDir, String destDir) {
        if (StringUtil.isEmpty(srcDir)) {
            logger.warn("sourceDir is null or empty, sourceDir: " + srcDir + ", destDir: " + destDir);
            return false;
        }
        if (StringUtil.isEmpty(destDir)) {
            logger.warn("destDir is null or empty, sourceDir: " + srcDir + ", destDir: " + destDir);
            return false;
        }
        File srcFile = new File(srcDir);
        File destFile = new File(destDir);
        if (!srcFile.isDirectory() || !srcFile.exists()) {
            logger.warn("Source dir isn't a directory or doesn't exist, sourceDir: " + srcDir);
            return false;
        }
        if (!destFile.isDirectory() || !destFile.exists()) {
            logger.warn("Dest dir isn't a directory or doesn't exist, destDir: " + destDir);
            return false;
        }
        if (FileUtil.isSubFile(srcFile, destFile)) {
            logger.warn(
                    "Source dir is the sub dir of dest dir, or dest dir is the sub dir of source dir, skip this entry, source dir: "
                            + srcDir + ", dest dir: " + destDir);
            return false;
        }
        return true;
    }

    private void processBackupTask(String srcBaseDir, String srcFullDir, String destBaseDir,
            List<Future<TaskResult>> taskFutures) throws Exception {
        File sourceFile = new File(srcFullDir);
        File[] files = sourceFile.listFiles();
        if (null == files) {
            logger.error("files is null, cannot process backup task, source dir: " + srcFullDir);
            return;
        }
        for (File file : files) {
            if (file.isFile()) {
                String srcFullPath = file.getAbsolutePath();
                String srcFullPathHash = HashUtil
                        .convertBytesToHexStr(HashUtil.getSHA256Hash(srcFullPath.getBytes(Constants.UTF_8)));
                String destFullPath = genDestFullPath(destBaseDir, srcFullPathHash);
                if (new File(destFullPath).exists()) {
                    continue;
                }
                BackupTaskConfig config = new BackupTaskConfig(srcBaseDir, srcFullPath, destBaseDir, destFullPath,
                        this.configManager.getBackupConfig().isEnableChecksum());
                BackupTaskThread task = new BackupTaskThread(this.configManager.getBackupConfig().getKeyBytes(),
                        config);
                Future<TaskResult> future = this.taskExecutor.submit(task);
                taskFutures.add(future);
            } else {
                processBackupTask(srcBaseDir, file.getAbsolutePath(), destBaseDir, taskFutures);
            }
        }
    }

    private String genDestFullPath(String destBaseDir, String srcFullPathHash) {
        // By default, use the previous 6 characters to be the path directory
        StringBuilder sb = new StringBuilder(destBaseDir);
        for (int i = 0; i < Constants.BACKUP_TARGET_DIR_LEN; i++) {
            sb.append(File.separator).append(srcFullPathHash.substring(i, i + 1));
        }
        sb.append(File.separator).append(srcFullPathHash).append(Constants.BACKUP_TARGET_FILE_POST_FIX);
        return sb.toString();
    }

    private void init(String[] args) {
        String rootDir = System.getProperty(Constants.MAIN_ROOT_DIR_KEY);
        if (StringUtil.isEmpty(rootDir) || !new File(rootDir).isDirectory()) {
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
            this.configManager = new BackupConfigManager(rootDir);
            this.taskExecutor = Executors.newFixedThreadPool(this.configManager.getBackupConfig().getThread());
        } catch (Exception e) {
            logger.error("Failed to initialize system.", e);
            System.exit(-1);
        }
    }
}
