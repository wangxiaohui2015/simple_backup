package com.my.simplebackup.backup.record;

/**
 * Record item.
 */
public class RecordItem {
    private String srcBasePath;
    private String srcFullPath;
    private String destBasePath;
    private String destFullPath;
    private long srcFileSize;
    private long destFileSize;
    private long lastModifyTime;
    private long backupStartTime;
    private long backupFinishTime;
    private boolean isBackupSucceed;

    public RecordItem() {
    }

    public RecordItem(String srcBasePath, String srcFullPath, String destBaseDir, String destFullPath,
            long srcFileSize) {
        this.srcBasePath = srcBasePath;
        this.srcFullPath = srcFullPath;
        this.destBasePath = destBaseDir;
        this.destFullPath = destFullPath;
        this.srcFileSize = srcFileSize;
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

    public long getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public long getBackupStartTime() {
        return backupStartTime;
    }

    public void setBackupStartTime(long backupStartTime) {
        this.backupStartTime = backupStartTime;
    }

    public long getBackupFinishTime() {
        return backupFinishTime;
    }

    public void setBackupFinishTime(long backupFinishTime) {
        this.backupFinishTime = backupFinishTime;
    }

    public boolean isBackupSucceed() {
        return isBackupSucceed;
    }

    public void setBackupSucceed(boolean isBackupSucceed) {
        this.isBackupSucceed = isBackupSucceed;
    }

    public long getSrcFileSize() {
        return srcFileSize;
    }

    public void setSrcFileSize(long srcFileSize) {
        this.srcFileSize = srcFileSize;
    }

    public long getDestFileSize() {
        return destFileSize;
    }

    public void setDestFileSize(long destFileSize) {
        this.destFileSize = destFileSize;
    }
}