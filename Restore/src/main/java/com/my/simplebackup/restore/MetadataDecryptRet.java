package com.my.simplebackup.restore;

/**
 * MetaDataDecryptRet.
 */
public class MetadataDecryptRet {
    private int metadataEncryptLen;
    private byte[] metadataBytes;
    private byte[] metadataHash;

    public MetadataDecryptRet(int metadataEncryptLen, byte[] metadataBytes, byte[] metadataHash) {
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
}
