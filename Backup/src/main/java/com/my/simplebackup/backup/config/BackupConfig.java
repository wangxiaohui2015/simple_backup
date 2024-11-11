package com.my.simplebackup.backup.config;

import java.util.List;

/**
 * BackupConfig.
 */
public class BackupConfig {
    private int thread = 0;
    private String key = "";
    private byte[] keyBytes;
    private List<BackupItem> backups;

    public int getThread() {
        return thread;
    }

    public void setThread(int thread) {
        this.thread = thread;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<BackupItem> getBackups() {
        return backups;
    }

    public void setBackups(List<BackupItem> backups) {
        this.backups = backups;
    }

    public byte[] getKeyBytes() {
        return keyBytes;
    }

    public void setKeyBytes(byte[] keyBytes) {
        this.keyBytes = keyBytes;
    }
}