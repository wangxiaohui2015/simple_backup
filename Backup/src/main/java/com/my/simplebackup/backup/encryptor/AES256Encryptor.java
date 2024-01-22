package com.my.simplebackup.backup.encryptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

import com.my.simplebackup.common.FileUtil;
import com.my.simplebackup.common.HashUtil;
import com.my.simplebackup.common.NumUtil;

/**
 * Use AES256 algorithm to encrypt file.
 * 
 */
public class AES256Encryptor {

    private static Logger logger = Logger.getLogger(AES256Encryptor.class);

    private static final String ALGORITHM = "AES";
    private static final String ALGORITHM_PKCS5PADDING = "AES/CBC/PKCS5Padding";
    private static final int CACHE_SIZE = 1024 * 1024;
    private byte[] keyBytes;
    private byte[] ivBytes;

    public AES256Encryptor(byte[] keyBytes, byte[] ivBytes) {
        this.keyBytes = keyBytes;
        this.ivBytes = ivBytes;
    }

    /**
     * Encrypt meta data.
     * 
     * Data format: EncryptedMetaDataLen(4 Bytes) + OriMetaDataHash(32 Bytes) +
     * EncryptedMetaData
     * 
     * @param bytes        meta data bytes
     * @param destFilePath destination file path
     * @throws Exception Exception
     */
    public void encryptMetaData(byte[] bytes, String destFilePath) throws Exception {
        File destFile = new File(destFilePath);
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        try (OutputStream out = new FileOutputStream(destFile)) {

            // Metadata Hash SHA256
            byte[] metaDataHash = HashUtil.getSHA256Hash(bytes);

            // Encrypt metadata
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, 0, 32, ALGORITHM);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes, 0, 16);
            Cipher cipher = Cipher.getInstance(ALGORITHM_PKCS5PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] encryptedMetaDataBytes = cipher.doFinal(bytes);

            // Write metadata length, will use 4 bytes
            int metaDataLen = encryptedMetaDataBytes.length;
            byte[] metaDataLenBytes = NumUtil.intToByte(metaDataLen);
            out.write(metaDataLenBytes);

            // Write meta data HASH, will use 32 bytes
            out.write(metaDataHash);

            // Write encrypted metadata
            out.write(encryptedMetaDataBytes);
            out.flush();
        } catch (Exception e) {
            logger.error("Exception occurred when encryptMetaData.", e);
            throw e;
        }
    }

    /**
     * Encrypt file.
     * 
     * @param sourceFilePath source file path
     * @param destFilePath   destination file path
     * @throws Exception Exception
     */
    public void encryptFile(String sourceFilePath, String destFilePath) throws Exception {
        File sourceFile = new File(sourceFilePath);
        File destFile = new File(destFilePath);
        if (!sourceFile.exists() || !sourceFile.isFile()) {
            throw new Exception("sourceFile doesn't exist or sourceFile isn't a file, sourceFile:" + sourceFile);
        }
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
        if (!destFile.exists()) {
            destFile.createNewFile();
        }
        InputStream in = null;
        CipherInputStream cin = null;
        RandomAccessFile rFile = null;
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, 0, 32, ALGORITHM);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes, 0, 16);
            Cipher cipher = Cipher.getInstance(ALGORITHM_PKCS5PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

            in = new FileInputStream(sourceFile);
            rFile = new RandomAccessFile(destFile, "rw");
            rFile.seek(destFile.length()); // Seek to the end of dest file.
            cin = new CipherInputStream(in, cipher);

            byte[] bytes = new byte[CACHE_SIZE];
            int length = -1;
            while ((length = cin.read(bytes)) != -1) {
                rFile.write(bytes, 0, length);
            }
        } catch (Exception e) {
            logger.error("Exception occurred when encryptFile.", e);
            throw e;
        } finally {
            FileUtil.closeRandomAccessFile(rFile);
            FileUtil.closeInputStream(cin);
            FileUtil.closeInputStream(in);
        }
    }
}
