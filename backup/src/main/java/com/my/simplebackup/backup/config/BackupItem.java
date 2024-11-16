package com.my.simplebackup.backup.config;

import java.util.List;

/**
 * BackupItem.
 */
public class BackupItem {
    private String src;
    private String dest;
    private List<String> excludeFiles;
    private List<String> excludeDirs;

    public BackupItem() {}

    public BackupItem(String src, String dest, List<String> excludeFiles,
                    List<String> excludeDirs) {
        this.src = src;
        this.dest = dest;
        this.excludeFiles = excludeFiles;
        this.excludeDirs = excludeDirs;
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

    public List<String> getExcludeFiles() {
        return excludeFiles;
    }

    public void setExcludeFiles(List<String> excludeFiles) {
        this.excludeFiles = excludeFiles;
    }

    public List<String> getExcludeDirs() {
        return excludeDirs;
    }

    public void setExcludeDirs(List<String> excludeDirs) {
        this.excludeDirs = excludeDirs;
    }
}
