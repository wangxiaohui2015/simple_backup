package com.my.simplebackup.backup.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.apache.log4j.Logger;

import com.my.simplebackup.backup.BackupMain;

/**
 * Backup configuration properties utility.
 * 
 * @author Administrator
 */
public class BackupConfigPropertiesUtil {

    private static Logger logger = Logger.getLogger(BackupConfigPropertiesUtil.class);
    private static final String CHARSET_UTF8 = "UTF-8";
    private static final String CHARSET_ISO88591 = "ISO8859-1";
    private static Properties properties = new Properties();;
    private static final BackupConfigPropertiesUtil instance = new BackupConfigPropertiesUtil();

    private String configFilePath = "";

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private ReadLock readLock = lock.readLock();
    private WriteLock writeLock = lock.writeLock();

    private BackupConfigPropertiesUtil() {
        reloadProperties();
    }

    /**
     * Reload properties object.
     */
    public void reloadProperties() {
        writeLock.lock();
        try {
            configFilePath = BackupMain.getRootDir() + File.separator + "conf" + File.separator
                            + "backup_config.properties";
            InputStream in = new FileInputStream(configFilePath);
            properties.clear();
            properties.load(in);
        } catch (IOException e) {
            logger.error("Failed to reload BackupConfigPropertiesUtil, service exits.", e);
            System.exit(-1);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Get singleton object.
     * 
     * @return singleton object.
     */
    public static BackupConfigPropertiesUtil getInstance() {
        return instance;
    }

    /**
     * Get value from properties file.
     * 
     * @param key key
     * @return value
     */
    public String getValue(String key) {
        readLock.lock();
        try {
            String value = properties.getProperty(key);
            if (null != value && !"".equals(value)) {
                try {
                    value = new String(value.getBytes(CHARSET_ISO88591), CHARSET_UTF8);
                } catch (UnsupportedEncodingException e) {
                    logger.error("UnsupportedEncodingException occurred when get value.", e);
                }
            }
            return value;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Get all source keys.
     * 
     * @return All source keys.
     */
    public List<String> getAllSourceKeys() {
        readLock.lock();
        try {
            List<String> keys = new ArrayList<String>();
            Set<Object> keySet = properties.keySet();
            Iterator<Object> it = keySet.iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                if (key.endsWith("source")) {
                    keys.add(key);
                }
            }
            return keys;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Get destination key by source key.
     * 
     * @param sourceKey source key.
     * @return destination key.
     */
    public String getDestKeyBySourceKey(String sourceKey) {
        String destKey = sourceKey.replaceAll("source", "target");
        return destKey;
    }
}
