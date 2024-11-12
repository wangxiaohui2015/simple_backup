package com.my.simplebackup.restore.task;

import com.my.simplebackup.common.metadata.FileMetadata;

/**
 * MetadataDecryptResult.
 */
public class MetadataDecryptResult {
    private int metadataEncryptLen;
    private byte[] metadataBytes;
    private byte[] metadataHash;
    private String filePath;
    private FileMetadata metadata;

    public MetadataDecryptResult(int metadataEncryptLen, byte[] metadataBytes, byte[] metadataHash) throws Exception {
        this.metadataEncryptLen = metadataEncryptLen;
        this.metadataBytes = metadataBytes;
        this.metadataHash = metadataHash;
    }

    public int getMetadataEncryptLen() {
        return metadataEncryptLen;
    }

    public void setMetadataEncryptLen(int metadataEncryptLen) {
        this.metadataEncryptLen = metadataEncryptLen;
    }

    public byte[] getMetadataBytes() {
        return metadataBytes;
    }

    public void setMetadataBytes(byte[] metadataBytes) {
        this.metadataBytes = metadataBytes;
    }

    public byte[] getMetadataHash() {
        return metadataHash;
    }

    public void setMetadataHash(byte[] metadataHash) {
        this.metadataHash = metadataHash;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public FileMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(FileMetadata metadata) {
        this.metadata = metadata;
    }
}
