package com.my.simplebackup.common.task;

/**
 * Task result.
 */
public class TaskResult {
    private long srcFileSize;
    private long destFileSize;
    private long startTime;
    private long finishTime;
    private boolean isSucceed;

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

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
    }

    public boolean isSucceed() {
        return isSucceed;
    }

    public void setSucceed(boolean isSucceed) {
        this.isSucceed = isSucceed;
    }
}