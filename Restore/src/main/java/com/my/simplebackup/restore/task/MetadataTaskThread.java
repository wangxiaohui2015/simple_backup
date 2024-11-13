package com.my.simplebackup.restore.task;

import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.my.simplebackup.common.KeyUtil;
import com.my.simplebackup.restore.decryptor.AES256Decryptor;

/**
 * Metadata task thread.
 */
public class MetadataTaskThread implements Callable<MetadataDecryptResult> {

    private static Logger logger = Logger.getLogger(MetadataTaskThread.class);

    private byte[] keyBytes;
    private String fileFullPath;

    public MetadataTaskThread(byte[] keyBytes, String fileFullPath) {
        this.keyBytes = keyBytes;
        this.fileFullPath = fileFullPath;
    }

    @Override
    public MetadataDecryptResult call() throws Exception {
        try {
            byte[] metadataKeyBytes = KeyUtil.getMetadataKeyBytes(this.keyBytes);
            byte[] metadataIVBytes = KeyUtil.getMetadataIVBytes(this.keyBytes);
            AES256Decryptor decryptor = new AES256Decryptor(metadataKeyBytes, metadataIVBytes);
            MetadataDecryptResult ret = decryptor.decryptMetadata(this.fileFullPath);
            logger.info("Restore metadata succeed, file path: " + this.fileFullPath);
            return ret;
        } catch (Exception e) {
            logger.info("Restore metadata failed, file path: " + this.fileFullPath + ", error msg: "
                            + e.getMessage());
        }
        return null;
    }
}
