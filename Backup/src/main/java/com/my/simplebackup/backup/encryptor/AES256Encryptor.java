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
     * Encrypt metadata.
     * 
     * Data format: EncryptedMetadataBytesLen(4 Bytes) + OriMetadataHash(32 Bytes) +
     * EncryptedMetadataBytes
     * 
     * @param bytes metadata bytes
     * @param destFilePath destination file path
     * @throws Exception Exception
     */
    public void encryptMetadata(byte[] bytes, String destFilePath) throws Exception {

        // Check parent directories
        File destFile = new File(destFilePath);
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        try (OutputStream out = new FileOutputStream(destFile)) {

            // Metadata Hash SHA256
            byte[] metadataHash = HashUtil.getSHA256Hash(bytes);

            // Encrypt metadata
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, 0, 32, ALGORITHM);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes, 0, 16);
            Cipher cipher = Cipher.getInstance(ALGORITHM_PKCS5PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] encryptedMetadataBytes = cipher.doFinal(bytes);

            // Write metadata length, will use 4 bytes
            int metadataLen = encryptedMetadataBytes.length;
            byte[] metadataLenBytes = NumUtil.intToByte(metadataLen);
            out.write(metadataLenBytes);

            // Write metadata HASH, will use 32 bytes
            out.write(metadataHash);

            // Write encrypted metadata
            out.write(encryptedMetadataBytes);
            out.flush();
        } catch (Exception e) {
            logger.error("Exception occurred whiling encrypting metadata.", e);
            throw e;
        }
    }

    /**
     * Encrypt file.
     * 
     * @param sourceFilePath source file path
     * @param destFilePath destination file path
     * @throws Exception Exception
     */
    public void encryptFile(String sourceFilePath, String destFilePath) throws Exception {

        // Check parent directories
        File destFile = new File(destFilePath);
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

            // Seek to the end of destination file.
            rFile = new RandomAccessFile(destFile, "rw");
            rFile.seek(destFile.length());

            in = new FileInputStream(sourceFilePath);
            cin = new CipherInputStream(in, cipher);

            byte[] bytes = new byte[CACHE_SIZE];
            int length = -1;
            while ((length = cin.read(bytes)) != -1) {
                rFile.write(bytes, 0, length);
            }
        } catch (Exception e) {
            logger.error("Exception occurred while encrypting file.", e);
            throw e;
        } finally {
            FileUtil.closeRandomAccessFile(rFile);
            FileUtil.closeInputStream(cin);
            FileUtil.closeInputStream(in);
        }
    }
}
