package com.my.simplebackup.restore.args;

public class RestoreParameter {

    private int threads = 4; // Default is 4
    private byte[] keyBytes;
    private String srcPath = "";
    private String destPath = "";
    private MODE_TYPE mode = MODE_TYPE.RESTORE; // Default is RESTORE

    public static enum MODE_TYPE {
        METADATA, FAKE, RESTORE
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public byte[] getKeyBytes() {
        return keyBytes;
    }

    public void setKeyBytes(byte[] keyBytes) {
        this.keyBytes = keyBytes;
    }

    public String getSrcPath() {
        return srcPath;
    }

    public void setSrcPath(String srcPath) {
        this.srcPath = srcPath;
    }

    public String getDestPath() {
        return destPath;
    }

    public void setDestPath(String destPath) {
        this.destPath = destPath;
    }

    public MODE_TYPE getMode() {
        return mode;
    }

    public void setMode(MODE_TYPE mode) {
        this.mode = mode;
    }
}
