package com.my.simplebackup.backup.config;

import java.io.File;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.simplebackup.common.Constants;
import com.my.simplebackup.common.StringUtil;

/**
 * Backup configure manager, used to manage configure file backup.json
 */
public class BackupConfigManager {

    private String configFilePath;
    private BackupConfig config;

    private static final int CONFIG_THREAD_MIN = 1;
    private static final int CONFIG_THREAD_MAX = 256;

    public static final String DEFAULT_CONFIG_FILE_NAME = "backup.json";

    public BackupConfigManager(String configRootPath) throws Exception {
        this.configFilePath = configRootPath + File.separator + "conf" + File.separator
                        + DEFAULT_CONFIG_FILE_NAME;
        ObjectMapper mapper = new ObjectMapper();
        this.config = mapper.readValue(new File(this.configFilePath), BackupConfig.class);
        checkConfig();
        // Use byte[] to store key instead of String for security reason
        this.config.setKeyBytes(this.config.getKey().getBytes(Constants.UTF_8));
        this.config.setKey("");
    }

    private void checkConfig() throws IllegalArgumentException {
        String msg = "please check configuration file: " + this.configFilePath;

        // check thread number
        int thread = this.config.getThread();
        if (thread < CONFIG_THREAD_MIN || thread > CONFIG_THREAD_MAX) {
            throw new IllegalArgumentException("Thread number must be in [" + CONFIG_THREAD_MIN
                            + "," + CONFIG_THREAD_MAX + "], " + msg);
        }

        // check key
        if (StringUtil.isEmpty(this.config.getKey())) {
            throw new IllegalArgumentException("key cannot be empty, " + msg);
        }

        // check backup items
        List<BackupItem> backupItems = this.config.getBackups();
        if (null == backupItems || backupItems.isEmpty()) {
            throw new IllegalArgumentException("backups cannot be empty, " + msg);
        }
    }

    public BackupConfig getBackupConfig() {
        return config;
    }
}
