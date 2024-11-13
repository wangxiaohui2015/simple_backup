package com.my.simplebackup.restore.decryptor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.my.simplebackup.common.FileUtil;
import com.my.simplebackup.common.NumUtil;
import com.my.simplebackup.common.metadata.FileMetadataHelper;
import com.my.simplebackup.restore.task.MetadataDecryptResult;

/**
 * Use AES256 algorithm to decrypt file.
 * 
 */
public class AES256Decryptor {

    private static final String ALGORITHM = "AES";
    private static final String ALGORITHM_PKCS5PADDING = "AES/CBC/PKCS5Padding";
    private static final int CACHE_SIZE = 1024 * 1024;
    private static final int META_DATA_BYTES_LEN = 4;
    private static final int META_DATA_HASH_BYTES_LEN = 32;

    private byte[] keyBytes;
    private byte[] ivBytes;

    public AES256Decryptor(byte[] keyBytes, byte[] ivBytes) {
        this.keyBytes = keyBytes;
        this.ivBytes = ivBytes;
    }

    /**
     * Restore metadata.
     * 
     * @param filePath file path
     * @return MetadataDecryptRet object
     * @throws Exception Exception
     */
    public MetadataDecryptResult decryptMetadata(String filePath) throws Exception {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new Exception("File doesn't exist: " + filePath);
        }

        try (RandomAccessFile rf = new RandomAccessFile(file, "r")) {

            // Metadata length, 4 bytes
            byte[] lenBytes = new byte[META_DATA_BYTES_LEN];
            rf.read(lenBytes);
            int metadataLen = NumUtil.bytesToInt(lenBytes);

            // Metadata checksum, 32 bytes
            byte[] metadataHashBytes = new byte[META_DATA_HASH_BYTES_LEN];
            rf.read(metadataHashBytes);

            byte[] encryptedMetadataBytes = new byte[metadataLen];
            rf.read(encryptedMetadataBytes);

            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, 0, 32, ALGORITHM);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes, 0, 16);
            Cipher cipher = Cipher.getInstance(ALGORITHM_PKCS5PADDING);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] metadataBytes = cipher.doFinal(encryptedMetadataBytes);

            // Calculate metadata encrypt length
            int metadataEncryptLen = META_DATA_BYTES_LEN + META_DATA_HASH_BYTES_LEN + metadataLen;

            MetadataDecryptResult ret = new MetadataDecryptResult(metadataEncryptLen, metadataBytes, metadataHashBytes);
            ret.setMetadata(FileMetadataHelper.getFileMetadataObj(metadataBytes));
            ret.setFilePath(filePath);
            return ret;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Decrypt file.
     * 
     * @param srcFilePath Source file path
     * @param destFilePath Destination file path
     * @param seekLen Seek length
     * @throws Exception Exception
     */
    public void decryptFile(String srcFilePath, String destFilePath, long seekLen) throws Exception {
        File srcFile = new File(srcFilePath);
        File destFile = new File(destFilePath);
        if (!srcFile.exists() || !srcFile.isFile()) {
            throw new Exception("sourceFile doesn't exist or sourceFile isn't a file, sourceFile:" + srcFile);
        }
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        OutputStream out = null;
        CipherOutputStream cout = null;
        RandomAccessFile rFile = null;
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, 0, 32, ALGORITHM);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes, 0, 16);
            Cipher cipher = Cipher.getInstance(ALGORITHM_PKCS5PADDING);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

            // Skip metadata part
            rFile = new RandomAccessFile(srcFilePath, "rw");
            rFile.seek(seekLen);
            out = new FileOutputStream(destFile);
            cout = new CipherOutputStream(out, cipher);

            byte[] bytes = new byte[CACHE_SIZE];
            int length = -1;
            while ((length = rFile.read(bytes)) != -1) {
                cout.write(bytes, 0, length);
            }
            cout.flush();
        } catch (Exception e) {
            throw e;
        } finally {
            FileUtil.closeOutputStream(cout);
            FileUtil.closeOutputStream(out);
            FileUtil.closeRandomAccessFile(rFile);
        }
    }
}
