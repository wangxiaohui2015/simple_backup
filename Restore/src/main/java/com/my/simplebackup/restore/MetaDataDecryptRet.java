package com.my.simplebackup.restore;

/**
 * MetaDataDecryptRet.
 */
public class MetaDataDecryptRet {
    private int metaDataEncryptLen;
    private byte[] metaDataBytes;
    private byte[] metaDataHash;

    public MetaDataDecryptRet(int metaDataEncryptLen, byte[] metaDataBytes, byte[] metaDataHash) {
        this.metaDataEncryptLen = metaDataEncryptLen;
        this.metaDataBytes = metaDataBytes;
        this.metaDataHash = metaDataHash;
    }

    public int getMetaDataEncryptLen() {
        return metaDataEncryptLen;
    }

    public void setMetaDataEncryptLen(int metaDataEncryptLen) {
        this.metaDataEncryptLen = metaDataEncryptLen;
    }

    public byte[] getMetaDataBytes() {
        return metaDataBytes;
    }

    public void setMetaDataBytes(byte[] metaDataBytes) {
        this.metaDataBytes = metaDataBytes;
    }

    public byte[] getMetaDataHash() {
        return metaDataHash;
    }

    public void setMetaDataHash(byte[] metaDataHash) {
        this.metaDataHash = metaDataHash;
    }
}
