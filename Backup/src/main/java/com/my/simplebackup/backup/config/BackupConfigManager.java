package com.my.simplebackup.backup.config;

import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Backup configure manager, used to manage configure file backup.json
 */
public class BackupConfigManager {

    private String configFilePath;
    private BackupConfig config;

    public static final String DEFAULT_CONFIG_FILE_NAME = "backup.json";

    public BackupConfigManager(String configRootPath) {
        this.configFilePath = configRootPath + File.separator + "conf" + File.separator + DEFAULT_CONFIG_FILE_NAME;
    }

    /**
     * Get BackupConfig object from backup.json
     * 
     * @return BackupConfig object
     * @throws Exception Exception
     */
    public BackupConfig getBackupConfig() throws Exception {
        if (null != config) {
            return config;
        }
        ObjectMapper mapper = new ObjectMapper();
        this.config = mapper.readValue(new File(this.configFilePath), BackupConfig.class);
        return config;
    }
}
