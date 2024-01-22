package com.my.simplebackup.backup.config;

import java.util.List;

/**
 * BackupConfig.
 */
public class BackupConfig {
    private int thread;
    private String key;
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
}