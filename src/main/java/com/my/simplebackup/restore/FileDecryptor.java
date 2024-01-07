package com.my.simplebackup.restore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.my.simplebackup.common.FileUtil;
import com.my.simplebackup.common.HashUtil;

/**
 * File decryptor.
 * 
 * @author Administrator
 *
 */
public class FileDecryptor {

    private static final String ALGORITHM = "AES";
    private static final String ALGORITHM_PKCS5PADDING = "AES/CBC/PKCS5Padding";
    private static final int CACHE_SIZE = 1024 * 1024;
    private static final String CHARSET_UTF8 = "UTF-8";
    private static byte[] keyBytes;
    private static byte[] ivBytes;

    /**
     * Initialize keyBytes and ivBytes.
     * 
     * @param key Security key
     * @throws Exception Exception
     */
    private static void initKeyBytes(String key) throws Exception {
        if (null == keyBytes || null == ivBytes) {
            String keyStr = key;
            byte[] keyStrBytes = HashUtil.getSHA512Hash(keyStr.getBytes(CHARSET_UTF8));
            keyStr = HashUtil.convertBytesToHexStr(keyStrBytes);
            keyStrBytes = HashUtil.getSHA512Hash(keyStr.getBytes(CHARSET_UTF8));
            keyBytes = Arrays.copyOfRange(keyStrBytes, 0, 32);
            ivBytes = Arrays.copyOfRange(keyStrBytes, 32, 48);
        }
    }

    /**
     * Decrypt file.
     * 
     * @param key security key
     * @param sourceFilePath source file path
     * @param destFilePath destination file path
     * @throws Exception Exception
     */
    public static void decryptFile(String key, String sourceFilePath, String destFilePath)
                    throws Exception {
        initKeyBytes(key);
        File sourceFile = new File(sourceFilePath);
        File destFile = new File(destFilePath);
        if (sourceFile.exists() && sourceFile.isFile()) {
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            }
            try {
                destFile.createNewFile();
            } catch (IOException e) {
                throw e;
            }

            InputStream in = null;
            OutputStream out = null;
            CipherOutputStream cout = null;
            try {
                in = new FileInputStream(sourceFile);
                out = new FileOutputStream(destFile);

                SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, ALGORITHM);
                IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);
                Cipher cipher = Cipher.getInstance(ALGORITHM_PKCS5PADDING);
                cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
                cout = new CipherOutputStream(out, cipher);

                byte[] bytes = new byte[CACHE_SIZE];
                int length = -1;
                while ((length = in.read(bytes)) != -1) {
                    cout.write(bytes, 0, length);
                }
                cout.flush();
            } catch (Exception e) {
                throw e;
            } finally {
                FileUtil.closeOutputStream(cout);
                FileUtil.closeOutputStream(cout);
                FileUtil.closeInputStream(in);
            }
        }
    }
}
