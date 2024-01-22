package com.my.simplebackup.backup.record;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.my.simplebackup.backup.config.BackupConfig;
import com.my.simplebackup.backup.config.BackupItem;
import com.my.simplebackup.common.FileUtil;

/**
 * Record configure manager, used to manage configuration file record.json
 */
public class RecordConfigManager {

    private static Logger logger = Logger.getLogger(RecordConfigManager.class);

    private String configFilePath;

    public static final String DEFAULT_CONFIG_FILE_NAME = "record.json";

    public RecordConfigManager(String configRootPath) {
        this.configFilePath = configRootPath + File.separator + "conf" + File.separator + DEFAULT_CONFIG_FILE_NAME;
    }

    /**
     * Get last succeed records from record.json.
     * 
     * @return RecordConfig object
     * @throws Exception Exception
     */
    public RecordConfig getLastSucceedRecordConfig() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        if (!new File(this.configFilePath).exists()) {
            return new RecordConfig(new ArrayList<RecordItem>());
        }
        RecordConfig config = mapper.readValue(new File(this.configFilePath), RecordConfig.class);
        return config;
    }

    /**
     * Save record config to record.json.
     * 
     * @param config RecordConfig object
     * @throws Exception Exception
     */
    public void saveRecordConfig(RecordConfig config) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.writeValue(new File(this.configFilePath), config);
    }

    /**
     * Get current record config according to backup config.
     * 
     * @param backupConfig BackupConfig object
     * @return RecordConfig object
     */
    public RecordConfig getCurrentRecordConfig(BackupConfig backupConfig) {
        RecordConfig recordConfig = new RecordConfig();
        for (BackupItem item : backupConfig.getBackups()) {
            String srcDir = item.getSrc();
            String destDir = item.getDest();
            if (null == srcDir || "".equals(srcDir)) {
                logger.warn("sourceDir is null or empty, sourceDir: " + srcDir + ", destDir: " + destDir);
                continue;
            }
            if (null == destDir || "".equals(destDir)) {
                logger.warn("destDir is null or empty, sourceDir: " + srcDir + ", destDir: " + destDir);
                continue;
            }
            if (!srcDir.endsWith(File.separator)) {
                srcDir = srcDir + File.separator;
            }
            if (!destDir.endsWith(File.separator)) {
                destDir = destDir + File.separator;
            }

            File sourceFile = new File(srcDir);
            File destFile = new File(destDir);
            if (FileUtil.isSubFile(sourceFile, destFile)) {
                logger.warn(
                        "Source dir is the sub dir of dest dir, or dest dir is the sub dir of source dir, skip this entry, source dir: "
                                + srcDir + ", dest dir: " + destDir);
                continue;
            }
            if (!sourceFile.isDirectory() || !sourceFile.exists()) {
                logger.warn("Source dir isn't a directory or doesn't exist, sourceDir: " + sourceFile);
                continue;
            }
            if (destFile.exists()) {
                if (destFile.isFile()) {
                    logger.warn("Dest dir exists, but it's a file, should be a directory.");
                    continue;
                }
            }
            if (!destFile.exists()) {
                logger.info("Dest dir doesn't exist, create it now.");
                destFile.mkdirs();
            }
            doGenerateRecordConfig(srcDir, destDir, srcDir, recordConfig);
        }
        return recordConfig;
    }

    private void doGenerateRecordConfig(String srcBaseDir, String detsBaseDir, String srcDir,
            RecordConfig recordConfig) {
        File srcFile = new File(srcDir);
        File[] files = srcFile.listFiles();
        if (null == files) {
            logger.error("files is null in doGenerateRecordConfig, " + srcDir);
            return;
        }
        for (File file : files) {
            if (file.isFile()) {
                String srcFullPath = file.getAbsolutePath();
                long lastModifiedTime = file.lastModified();
                RecordItem item = new RecordItem(srcFullPath, srcBaseDir, "", detsBaseDir, lastModifiedTime, 0, false);
                recordConfig.getRecords().add(item);
            } else {
                doGenerateRecordConfig(srcBaseDir, detsBaseDir, file.getAbsolutePath(), recordConfig);
            }
        }
    }

    /**
     * Compare two RecordConfig object.
     * 
     * @param config1 RecordConfig
     * @param config2 RecordConfig
     * @return The diff of RecordConfig object.
     */
    public RecordConfig diffRecordConfig(RecordConfig config1, RecordConfig config2) {
        RecordConfig config = new RecordConfig();
        for (RecordItem item : config1.getRecords()) {
            int index = config2.getRecords().indexOf(item);
            if (index == -1) { // New file
                config.getRecords().add(item);
                continue;
            }
            RecordItem lastSucceedItem = config2.getRecords().get(index);
            File destFile = new File(lastSucceedItem.getDestFullPath());
            if (!destFile.exists()) { // Old file, but dest backup file doesn't exist
                config.getRecords().add(item);
                continue;
            }
            if (item.getLastModifyTime() != lastSucceedItem.getLastModifyTime()) { // Old file, but file is changed
                config.getRecords().add(item);
                continue;
            }
        }
        return config;
    }

    /**
     * Update RecordConfig object after all backup tasks are finished.
     * 
     * @param lastRecordConfig RecordConfig
     * @param futures          List<Future<RecordItem>>
     * @throws Exception Exception
     */
    public void updateRecordConfig(RecordConfig lastRecordConfig, List<Future<RecordItem>> futures) throws Exception {
        for (Future<RecordItem> future : futures) {
            if (null == future) {
                continue;
            }
            RecordItem item = future.get();
            if (!item.isBackupSucceed()) {
                continue;
            }

            // lastRecordConfig may have item because, so need to remove it firstly,
            // 1, Dest backup file doesn't exist, need new backup
            // 2, Src file is changed, need new backup
            if (lastRecordConfig.getRecords().contains(item)) {
                lastRecordConfig.getRecords().remove(item);
            }
            lastRecordConfig.getRecords().add(item);
        }
    }
}
