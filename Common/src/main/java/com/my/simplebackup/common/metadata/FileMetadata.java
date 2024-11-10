package com.my.simplebackup.common.metadata;

/**
 * File meta data.
 */
public class FileMetadata {

    private String fileName = "";
    private String fileBasePath = "";
    private String fileFullPath = "";
    private long fileLen = 0;
    private long backupTime = 0;
    private String backupSWVersion = "";
    private String aesVersion = "";
    private String aesIV = "";
    private String keySalt = "";
    private String checkSum = "";
    private String obscure = "";

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileBasePath() {
        return fileBasePath;
    }

    public void setFileBasePath(String fileBasePath) {
        this.fileBasePath = fileBasePath;
    }

    public String getFileFullPath() {
        return fileFullPath;
    }

    public void setFileFullPath(String fileFullPath) {
        this.fileFullPath = fileFullPath;
    }

    public long getFileLen() {
        return fileLen;
    }

    public void setFileLen(long fileLen) {
        this.fileLen = fileLen;
    }

    public long getBackupTime() {
        return backupTime;
    }

    public void setBackupTime(long backupTime) {
        this.backupTime = backupTime;
    }

    public String getBackupSWVersion() {
        return backupSWVersion;
    }

    public void setBackupSWVersion(String backupSWVersion) {
        this.backupSWVersion = backupSWVersion;
    }

    public String getAesVersion() {
        return aesVersion;
    }

    public void setAesVersion(String aesVersion) {
        this.aesVersion = aesVersion;
    }

    public String getAesIV() {
        return aesIV;
    }

    public void setAesIV(String aesIV) {
        this.aesIV = aesIV;
    }

    public String getKeySalt() {
        return keySalt;
    }

    public void setKeySalt(String keySalt) {
        this.keySalt = keySalt;
    }

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }

    public String getObscure() {
        return obscure;
    }

    public void setObscure(String obscure) {
        this.obscure = obscure;
    }
}
