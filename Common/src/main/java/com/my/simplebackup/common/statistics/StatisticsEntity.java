package com.my.simplebackup.common.statistics;

import java.util.Date;

/**
 * Statistics entity.
 */
public class StatisticsEntity {
    private long totalFiles;
    private long succeedFiles;
    private long failedFiles;
    private long totalFileSize;
    private long succeedFileSize;
    private long failedFileSize;
    private long succeedTargetFileSize;
    private long startTime;
    private long endTime;
    private long rate;

    public long calculateRate() {
        long timeTaken = (endTime - startTime) / 1000; // Second
        if (timeTaken > 0) {
            rate = succeedFileSize / timeTaken;
        }
        return rate;
    }

    public void startStat() {
        this.startTime = new Date().getTime();
    }

    public void endStat() {
        this.endTime = new Date().getTime();
    }

    public long getTotalFiles() {
        return totalFiles;
    }

    public void setTotalFiles(long totalFiles) {
        this.totalFiles = totalFiles;
    }

    public long getSucceedFiles() {
        return succeedFiles;
    }

    public void setSucceedFiles(long succeedFiles) {
        this.succeedFiles = succeedFiles;
    }

    public long getFailedFiles() {
        return failedFiles;
    }

    public void setFailedFiles(long failedFiles) {
        this.failedFiles = failedFiles;
    }

    public long getTotalFileSize() {
        return totalFileSize;
    }

    public void setTotalFileSize(long totalFileSize) {
        this.totalFileSize = totalFileSize;
    }

    public long getSucceedFileSize() {
        return succeedFileSize;
    }

    public void setSucceedFileSize(long succeedFileSize) {
        this.succeedFileSize = succeedFileSize;
    }

    public long getFailedFileSize() {
        return failedFileSize;
    }

    public void setFailedFileSize(long failedFileSize) {
        this.failedFileSize = failedFileSize;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getRate() {
        return rate;
    }

    public void setRate(long rate) {
        this.rate = rate;
    }

    public long getSucceedTargetFileSize() {
        return succeedTargetFileSize;
    }

    public void setSucceedTargetFileSize(long succeedTargetFileSize) {
        this.succeedTargetFileSize = succeedTargetFileSize;
    }
}
