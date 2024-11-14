package com.my.simplebackup.backup.task;

/**
 * Backup task configuration.
 */
public class BackupTaskConfig {
    private String srcBasePath;
    private String srcFullPath;
    private String destBasePath;
    private String destFullPath;
    private boolean enableChecksum;

    public BackupTaskConfig() {}

    public BackupTaskConfig(String srcBasePath, String srcFullPath, String destBaseDir,
                    String destFullPath, boolean enableChecksum) {
        this.srcBasePath = srcBasePath;
        this.srcFullPath = srcFullPath;
        this.destBasePath = destBaseDir;
        this.destFullPath = destFullPath;
        this.enableChecksum = enableChecksum;
    }

    public String getSrcBasePath() {
        return srcBasePath;
    }

    public void setSrcBasePath(String srcBasePath) {
        this.srcBasePath = srcBasePath;
    }

    public String getSrcFullPath() {
        return srcFullPath;
    }

    public void setSrcFullPath(String srcFullPath) {
        this.srcFullPath = srcFullPath;
    }

    public String getDestBasePath() {
        return destBasePath;
    }

    public void setDestBasePath(String destBasePath) {
        this.destBasePath = destBasePath;
    }

    public String getDestFullPath() {
        return destFullPath;
    }

    public void setDestFullPath(String destFullPath) {
        this.destFullPath = destFullPath;
    }

    public boolean isEnableChecksum() {
        return enableChecksum;
    }

    public void setEnableChecksum(boolean enableChecksum) {
        this.enableChecksum = enableChecksum;
    }
}
