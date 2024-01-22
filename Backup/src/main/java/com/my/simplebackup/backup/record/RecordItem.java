package com.my.simplebackup.backup.record;

import java.util.Objects;

/**
 * Record item.
 */
public class RecordItem {
    private String srcFullPath;
    private String srcBaseDir;
    private String destFullPath;
    private String destBaseDir;
    private long lastModifyTime;
    private long backupTime;
    private boolean isBackupSucceed;

    public RecordItem() {
    }

    public RecordItem(String srcFullPath, String srcBaseDir, String destFullPath, String destBaseDir,
            long lastModifyTime, long backupTime, boolean isBackupSucceed) {
        this.srcFullPath = srcFullPath;
        this.srcBaseDir = srcBaseDir;
        this.destFullPath = destFullPath;
        this.destBaseDir = destBaseDir;
        this.backupTime = backupTime;
        this.lastModifyTime = lastModifyTime;
        this.isBackupSucceed = isBackupSucceed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(srcFullPath);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        RecordItem other = (RecordItem) obj;
        return Objects.equals(srcFullPath, other.srcFullPath);
    }

    public String getSrcFullPath() {
        return srcFullPath;
    }

    public void setSrcFullPath(String srcFullPath) {
        this.srcFullPath = srcFullPath;
    }

    public String getSrcBaseDir() {
        return srcBaseDir;
    }

    public void setSrcBaseDir(String srcBaseDir) {
        this.srcBaseDir = srcBaseDir;
    }

    public String getDestFullPath() {
        return destFullPath;
    }

    public void setDestFullPath(String destFullPath) {
        this.destFullPath = destFullPath;
    }

    public String getDestBaseDir() {
        return destBaseDir;
    }

    public void setDestBaseDir(String destBaseDir) {
        this.destBaseDir = destBaseDir;
    }

    public long getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public long getBackupTime() {
        return backupTime;
    }

    public void setBackupTime(long backupTime) {
        this.backupTime = backupTime;
    }

    public boolean isBackupSucceed() {
        return isBackupSucceed;
    }

    public void setBackupSucceed(boolean isBackupSucceed) {
        this.isBackupSucceed = isBackupSucceed;
    }
}
