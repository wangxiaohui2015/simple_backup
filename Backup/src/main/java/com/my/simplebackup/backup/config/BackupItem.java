package com.my.simplebackup.backup.config;

/**
 * BackupItem.
 */
public class BackupItem {
    private String src;
    private String dest;

    public BackupItem() {
    }

    public BackupItem(String src, String dest) {
        this.src = src;
        this.dest = dest;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }
}
